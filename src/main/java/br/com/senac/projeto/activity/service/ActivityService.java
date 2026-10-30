package br.com.senac.projeto.activity.service;

import br.com.senac.projeto.activity.domain.Activity;
import br.com.senac.projeto.activity.domain.ActivityDTO;
import br.com.senac.projeto.activity.exceptions.ActivityNotFoundException;
import br.com.senac.projeto.activity.repository.ActivityRepository;
import br.com.senac.projeto.costcenter.domain.CostCenter;
import br.com.senac.projeto.costcenter.repository.CostCenterRepository;
import br.com.senac.projeto.costcenter.service.CostCenterService;
import br.com.senac.projeto.product.domain.Product;
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

@RequiredArgsConstructor
@Service
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CostCenterRepository costCenterRepository;
    private final CostCenterService costCenterService;
    @PersistenceContext
    private EntityManager entityManager;

    public List<ActivityDTO> getAll (boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedActivityFilter");
        filter.setParameter("isDeleted", isDeleted);
        Iterable<Activity> activities = activityRepository.findAll();
        session.disableFilter("deletedActivityFilter");
        List<ActivityDTO>activityDTOs = new ArrayList<>();
        for(Activity activity: activities){
            ActivityDTO activityDTO = new ActivityDTO(activity.getId(), activity.getName(), activity.getNumber(),
                    activity.getBalanceActivityValue(),activity.getTotalActivityValue(),activity.getCommittedValue(),
                    activity.getRealizedValue(),activity.getCostCenter(),activity.getProductList(),activity.isDeleted());

            activityDTOs.add(activityDTO);
        }
        return activityDTOs;
    }

    public ActivityDTO getById(Long id){
        Activity activity = activityRepository.findById(id)
                .orElseThrow(()-> new ActivityNotFoundException("Activity not found with id: " +id));
            return new ActivityDTO(activity.getId(), activity.getName(), activity.getNumber(),
                activity.getBalanceActivityValue(), activity.getTotalActivityValue(),activity.getCommittedValue(),
                activity.getRealizedValue(), activity.getCostCenter(),activity.getProductList(), activity.isDeleted());
    }

    @Transactional
    public ActivityDTO create(Long costCenterId, ActivityDTO activityDTO){

        Activity activity = convertToActivity(activityDTO);

        CostCenter costCenterFound = costCenterRepository.getReferenceById(costCenterId);
        Activity activitySaved = activityRepository.save(activity);

        costCenterFound.getActivityList().add(activitySaved);
        BigDecimal oldCommitted = costCenterFound.getCommittedValue();
        BigDecimal oldRealized = costCenterFound.getRealizedValue();

        costCenterFound.setCommittedValue(oldCommitted.add(activitySaved.getCommittedValue()));
        costCenterFound.setRealizedValue(oldRealized.add(activitySaved.getRealizedValue()));

        recalculateActivity(activity);
        costCenterService.recalculateContract(costCenterFound);
        return convertToActivityDTO(activitySaved);
    }

    @Transactional
    public ActivityDTO update(Long id, ActivityDTO newActivityDTO){
        Activity activityFound = activityRepository.findById(id)
                .orElseThrow(()-> new ActivityNotFoundException(" Activity not found with id: " + id));

        recalculateActivity(activityFound);
        costCenterService.recalculateCostCenterWhenUpdatingActivity(activityFound.getCostCenter());

        Activity activityUpdated = updateActivityFromDTO(activityFound, newActivityDTO);
        Activity activitySaved = activityRepository.save(activityUpdated);

        return convertToActivityDTO(activitySaved);
    }

    public void delete(Long id){
        ActivityDTO activityFound = getById(id);
        Activity activity = convertToActivity(activityFound);

        activityRepository.deleteById(id);
        costCenterService.recalculateCostCenterWhenUpdatingActivity(activity.getCostCenter());
        convertToActivityDTO(activity);
    }

    public void recalculateActivity(Activity activity){

        activity.setBalanceActivityValue(activity.getTotalActivityValue()
                .subtract(activity.getRealizedValue().add(activity.getCommittedValue())));
    }

    public void recalculateActivityWhenUpdatingProduct(Activity activity){
        if(!existsByActivity(activity)){
            throw new EntityNotFoundException("activity do not exists!");
        }
        BigDecimal committedValueSum = BigDecimal.ZERO;
        BigDecimal realizedValueSum = BigDecimal.ZERO;

        if(activity.getProductList() != null)
            for(Product product : activity.getProductList()) {
                committedValueSum = committedValueSum.add(product.getCommittedValue());
                realizedValueSum = realizedValueSum.add(product.getRealizedValue());
            }
        if(activity.getProductList() != null)
            for(Product product : activity.getProductList()){
                if(product.isDeleted()){
                    committedValueSum = committedValueSum.add(product.getCommittedValue());
                    realizedValueSum = realizedValueSum.add(product.getRealizedValue());
                }
            }

        activity.setCommittedValue((committedValueSum));
        activity.setRealizedValue(realizedValueSum);
        recalculateActivity(activity);
        activityRepository.save(activity);
    }

    private boolean existsByActivity(Activity activity) {
        return activityRepository.existsById(activity.getId());
    }

    private Activity convertToActivity(ActivityDTO dto){
        return new Activity(dto.id(), dto.name(), dto.number(), dto.balanceActivityValue(),
                dto.totalActivityValue(), dto.committedValue(), dto.realizedValue(), dto.costCenter(),
                dto.productList(), dto.deleted());
    }

    private ActivityDTO convertToActivityDTO(Activity activity){
        return new ActivityDTO(activity.getId(), activity.getName(), activity.getNumber(), activity.getBalanceActivityValue(),
                activity.getTotalActivityValue(), activity.getCommittedValue(), activity.getRealizedValue(),
                activity.getCostCenter(),activity.getProductList(), activity.isDeleted());
    }

    private Activity updateActivityFromDTO(Activity existingEntity, ActivityDTO dto) {
        existingEntity.setName(dto.name());
        existingEntity.setNumber(dto.number());
        existingEntity.setBalanceActivityValue(dto.balanceActivityValue());
        existingEntity.setTotalActivityValue(dto.totalActivityValue());
        existingEntity.setCommittedValue(dto.committedValue());
        existingEntity.setRealizedValue(dto.realizedValue());
        existingEntity.setCostCenter(dto.costCenter());
        existingEntity.setProductList(dto.productList());
        existingEntity.setDeleted(dto.deleted());

        return existingEntity;
    }
}