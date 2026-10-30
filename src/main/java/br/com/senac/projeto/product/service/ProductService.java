package br.com.senac.projeto.product.service;

import br.com.senac.projeto.activity.domain.Activity;
import br.com.senac.projeto.activity.repository.ActivityRepository;
import br.com.senac.projeto.activity.service.ActivityService;
import br.com.senac.projeto.costcenter.service.CostCenterService;
import br.com.senac.projeto.budgetarynature.domain.BudgetaryNature;
import br.com.senac.projeto.product.domain.Product;
import br.com.senac.projeto.product.domain.ProductDTO;
import br.com.senac.projeto.product.exceptions.ProductNotFoundException;
import br.com.senac.projeto.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ActivityRepository activityRepository;
    private final ActivityService activityService;
    private final CostCenterService costCenterService;
    @PersistenceContext
    private EntityManager entityManager;

    public List<ProductDTO> getAll(boolean isDeleted){
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", isDeleted);
        Iterable<Product> products = productRepository.findAll();
        session.disableFilter("deletedProductFilter");
        List<ProductDTO> productDTOs = new ArrayList<>();
        for(Product product : products){
            ProductDTO productDTO = new ProductDTO(product.getId(), product.getName(), product.getNumber(),
                    product.getBalanceProductValue(), product.getTotalProductValue(), product.getCommittedValue(),
                    product.getRealizedValue(), product.getActivity(), product.getBudgetaryNatureList(), product.isDeleted());

            productDTOs.add(productDTO);
        }
        return productDTOs;
    }

    public ProductDTO getById(Long id){
        Product product =  productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product not found with id: " +id));
        return new ProductDTO(product.getId(), product.getName(), product.getNumber(), product.getBalanceProductValue(),
                product.getTotalProductValue(), product.getCommittedValue(), product.getRealizedValue(),
                product.getActivity(), product.getBudgetaryNatureList(), product.isDeleted());
    }

    @Transactional
    public ProductDTO create(Long activityId, ProductDTO productDTO){

        Product product = convertToProduct(productDTO);

        Activity activityFound = activityRepository.getReferenceById(activityId);
        Product productSaved = productRepository.save(product);

        activityFound.getProductList().add(productSaved);
        BigDecimal oldCommitted = activityFound.getCommittedValue();
        BigDecimal oldRealized = activityFound.getRealizedValue();

        activityFound.setCommittedValue(oldCommitted.add(productSaved.getCommittedValue()));
        activityFound.setRealizedValue(oldRealized.add(productSaved.getRealizedValue()));



        recalculateProduct(product);
        activityService.recalculateActivity(activityFound);
        costCenterService.recalculateCostCenterWhenUpdatingActivity(activityFound.getCostCenter());
        return convertToProductDTO(productSaved);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO newProductDTO){
        Product productFound = productRepository.findById(id).orElseThrow(()->
                new ProductNotFoundException(" Product not found with id: " + id));

        recalculateProduct(productFound);
        activityService.recalculateActivityWhenUpdatingProduct(productFound.getActivity());
        costCenterService.recalculateCostCenterWhenUpdatingActivity(productFound.getActivity().getCostCenter());

        Product productUpdated = updateProductFromDTO(productFound, newProductDTO);
        Product productSaved = productRepository.save(productUpdated);

        return convertToProductDTO(productSaved);
    }

    public void delete(Long id){
        ProductDTO productFound = getById(id);
        Product product = convertToProduct(productFound);

        productRepository.deleteById(id);
        activityService.recalculateActivityWhenUpdatingProduct(product.getActivity());
        costCenterService.recalculateCostCenterWhenUpdatingActivity(product.getActivity().getCostCenter());
        convertToProductDTO(product);
    }

    public void recalculateProduct(Product product){
        product.setBalanceProductValue(product.getTotalProductValue()
                .subtract(product.getRealizedValue().add(product.getCommittedValue())));
    }

    public void recalculateProductWhenUpdatingBudgetaryNature(Product product){
        if(!existsByProduct(product)){
            throw new EntityNotFoundException("product do not exists!");
        }
        BigDecimal committedValueSum = BigDecimal.ZERO;
        BigDecimal realizedValueSUm = BigDecimal.ZERO;

        if(product.getBudgetaryNatureList() != null)
            for(BudgetaryNature budgetaryNature : product.getBudgetaryNatureList()){
                    committedValueSum = committedValueSum.add(budgetaryNature.getCommittedValue());
                    realizedValueSUm = realizedValueSUm.add(budgetaryNature.getRealizedValue());
            }

//        if(product.getBudgetaryNatureList() != null)
//            for(BudgetaryNature budgetaryNature : product.getBudgetaryNatureList()){
//                if(!budgetaryNature.isDeleted()) {
//                    committedValueSum = committedValueSum.add(budgetaryNature.getCommittedValue());
//                    realizedValueSUm = realizedValueSUm.add(budgetaryNature.getRealizedValue());
//                }
//            }

        product.setCommittedValue(committedValueSum);
        product.setRealizedValue(realizedValueSUm);
        recalculateProduct(product);
        productRepository.save(product);
    }

    private boolean existsByProduct(Product product) {
        return productRepository.existsById(product.getId());
    }

    private Product convertToProduct(ProductDTO dto){
        return new Product(dto.id(), dto.name(), dto.number(), dto.balanceProductValue(),
                dto.totalProductValue(), dto.committedValue(), dto.realizedValue(),
                dto.activity(), dto.budgetaryNatureList(),dto.deleted());
    }

    private ProductDTO convertToProductDTO(Product product){
        return new ProductDTO(product.getId(), product.getName(), product.getNumber(), product.getBalanceProductValue(),
                product.getTotalProductValue(), product.getCommittedValue(), product.getRealizedValue(),
                product.getActivity(), product.getBudgetaryNatureList(), product.isDeleted());
    }

    private Product updateProductFromDTO(Product existingEntity, ProductDTO dto){
        existingEntity.setName(dto.name());
        existingEntity.setNumber(dto.number());
        existingEntity.setBalanceProductValue(dto.balanceProductValue());
        existingEntity.setTotalProductValue(dto.totalProductValue());
        existingEntity.setCommittedValue(dto.committedValue());
        existingEntity.setRealizedValue(dto.realizedValue());
        existingEntity.setActivity(dto.activity());
        existingEntity.setBudgetaryNatureList(dto.budgetaryNatureList());
        existingEntity.setDeleted(dto.deleted());

        return existingEntity;
    }
}