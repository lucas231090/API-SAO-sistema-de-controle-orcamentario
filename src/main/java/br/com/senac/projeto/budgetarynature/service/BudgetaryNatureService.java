package br.com.senac.projeto.budgetarynature.service;

import br.com.senac.projeto.activity.service.ActivityService;
import br.com.senac.projeto.budgetarynature.domain.BudgetaryNatureDTO;
import br.com.senac.projeto.costcenter.service.CostCenterService;
import br.com.senac.projeto.budgetarynature.domain.BudgetaryNature;
import br.com.senac.projeto.budgetarynature.exception.BudgetaryNatureNotFoundException;
import br.com.senac.projeto.budgetarynature.repository.BudgetaryNatureRepository;
import br.com.senac.projeto.product.domain.Product;
import br.com.senac.projeto.product.repository.ProductRepository;
import br.com.senac.projeto.product.service.ProductService;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class BudgetaryNatureService {

    private final BudgetaryNatureRepository budgetaryNatureRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ActivityService activityService;
    private final CostCenterService costCenterService;
    @PersistenceContext

    private EntityManager entityManager;

    public List<BudgetaryNatureDTO> getAll(boolean isDeleted){
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedBudgetaryNatureFilter");
        filter.setParameter("isDeleted", isDeleted);
        Iterable<BudgetaryNature> budgetaryNatures = budgetaryNatureRepository.findAll();
        session.disableFilter("deletedBudgetaryNatureFilter");
        List<BudgetaryNatureDTO> budgetaryNatureDTOs = new ArrayList<>();
        for(BudgetaryNature budgetaryNature : budgetaryNatures){
            BudgetaryNatureDTO budgetaryNatureDTO = new BudgetaryNatureDTO(
                    budgetaryNature.getName(), budgetaryNature.getNumber(), budgetaryNature.getBalanceBudgetaryNatureValue(),
                    budgetaryNature.getTotalBudgetaryNatureValue(), budgetaryNature.getCommittedValue(),
                    budgetaryNature.getRealizedValue(), budgetaryNature.getProduct(), budgetaryNature.isDeleted());

            budgetaryNatureDTOs.add(budgetaryNatureDTO);
        }
        return budgetaryNatureDTOs;
    }

    public BudgetaryNatureDTO getById(Long id){
        BudgetaryNature budgetaryNature = budgetaryNatureRepository.findById(id).orElseThrow(()->
                new BudgetaryNatureNotFoundException("budgetary nature not found with id: " + id));
        return new BudgetaryNatureDTO(budgetaryNature.getName(), budgetaryNature.getNumber(),
                budgetaryNature.getBalanceBudgetaryNatureValue(), budgetaryNature.getTotalBudgetaryNatureValue(),
                budgetaryNature.getCommittedValue(), budgetaryNature.getRealizedValue(), budgetaryNature.getProduct(),
                budgetaryNature.isDeleted());
    }

    private BudgetaryNature getByIdBudgetaryNature(Long id){
        return budgetaryNatureRepository.findById(id).orElseThrow(() ->
                new BudgetaryNatureNotFoundException("budgetary nature not found with: " + id));
    }


    @Transactional
    public BudgetaryNatureDTO create (Long productId, BudgetaryNatureDTO budgetaryNatureDTO){

        BudgetaryNature budgetaryNature = convertToBudgetaryNature(budgetaryNatureDTO);

        Product productFound = productRepository.getReferenceById(productId);
        BudgetaryNature budgetaryNatureSaved = budgetaryNatureRepository.save(budgetaryNature);

        productFound.getBudgetaryNatureList().add(budgetaryNatureSaved);
        BigDecimal oldCommitted = productFound.getCommittedValue();
        BigDecimal oldRealized = productFound.getRealizedValue();

        productFound.setCommittedValue(oldCommitted.add(budgetaryNatureSaved.getCommittedValue()));
        productFound.setRealizedValue(oldRealized.add(budgetaryNatureSaved.getRealizedValue()));

        recalculateBudgetaryNature(budgetaryNature);

        productService.recalculateProduct(productFound);
        activityService.recalculateActivityWhenUpdatingProduct(productFound.getActivity());
        costCenterService.recalculateCostCenterWhenUpdatingActivity(productFound.getActivity().getCostCenter());

        return convertToBudgetaryNatureDTO(budgetaryNatureSaved);
    }

    @Transactional
    public BudgetaryNature update(Long id, BudgetaryNature updateBudgetaryNature){
        BudgetaryNature budgetaryNatureFound = getByIdBudgetaryNature(id);

        budgetaryNatureFound.setName(updateBudgetaryNature.getName());
        budgetaryNatureFound.setNumber(updateBudgetaryNature.getNumber());
        budgetaryNatureFound.setBalanceBudgetaryNatureValue(updateBudgetaryNature.getBalanceBudgetaryNatureValue());
        budgetaryNatureFound.setTotalBudgetaryNatureValue(updateBudgetaryNature.getTotalBudgetaryNatureValue());
        budgetaryNatureFound.setCommittedValue(updateBudgetaryNature.getCommittedValue());
        budgetaryNatureFound.setRealizedValue(updateBudgetaryNature.getRealizedValue());

        recalculateBudgetaryNature(budgetaryNatureFound);
        productService.recalculateProductWhenUpdatingBudgetaryNature(budgetaryNatureFound.getProduct());
        activityService.recalculateActivityWhenUpdatingProduct(budgetaryNatureFound.getProduct().getActivity());
        costCenterService.recalculateCostCenterWhenUpdatingActivity(budgetaryNatureFound.getProduct().getActivity().getCostCenter());

        return budgetaryNatureRepository.save(budgetaryNatureFound);
    }

    public void delete(Long id){
        BudgetaryNatureDTO budgetaryNatureFound = getById(id);
        BudgetaryNature budgetaryNature = convertToBudgetaryNature(budgetaryNatureFound);
        budgetaryNatureRepository.deleteById(id);

        productService.recalculateProductWhenUpdatingBudgetaryNature(budgetaryNature.getProduct());
        activityService.recalculateActivityWhenUpdatingProduct(budgetaryNature.getProduct().getActivity());
        costCenterService.recalculateCostCenterWhenUpdatingActivity(budgetaryNature.getProduct().getActivity().getCostCenter());
    }

    public void recalculateBudgetaryNature(BudgetaryNature budgetaryNature){
        budgetaryNature.setBalanceBudgetaryNatureValue(budgetaryNature.getTotalBudgetaryNatureValue()
                .subtract(budgetaryNature.getRealizedValue().add(budgetaryNature.getCommittedValue())));
    }

    private BudgetaryNature convertToBudgetaryNature(BudgetaryNatureDTO dto){
        return new BudgetaryNature(
                null,
                dto.name(),
                dto.number(),
                dto.balanceBudgetaryNatureValue(),
                dto.totalBudgetaryNatureValue(),
                dto.committedValue(),
                dto.realizedValue(),
                dto.product(),
                dto.deleted());
    }

    private BudgetaryNatureDTO convertToBudgetaryNatureDTO(BudgetaryNature budgetaryNature){
        return new BudgetaryNatureDTO(
                budgetaryNature.getName(),
                budgetaryNature.getNumber(),
                budgetaryNature.getBalanceBudgetaryNatureValue(),
                budgetaryNature.getTotalBudgetaryNatureValue(),
                budgetaryNature.getCommittedValue(),
                budgetaryNature.getRealizedValue(),
                budgetaryNature.getProduct(),
                budgetaryNature.isDeleted());
    }

    private BudgetaryNature updateBudgetaryFromDTO(BudgetaryNature budgetaryNature, BudgetaryNatureDTO dto){
        budgetaryNature.setName(dto.name());
        budgetaryNature.setNumber(dto.number());
        budgetaryNature.setBalanceBudgetaryNatureValue(dto.balanceBudgetaryNatureValue());
        budgetaryNature.setTotalBudgetaryNatureValue(dto.totalBudgetaryNatureValue());
        budgetaryNature.setCommittedValue(dto.committedValue());
        budgetaryNature.setRealizedValue(dto.realizedValue());
        budgetaryNature.setProduct(dto.product());
        budgetaryNature.setDeleted(dto.deleted());

        return budgetaryNature;
    }
}