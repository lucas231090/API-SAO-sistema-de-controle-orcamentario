package br.com.senac.projeto.costcenter.service;

import br.com.senac.projeto.activity.domain.Activity;
import br.com.senac.projeto.costcenter.domain.CostCenter;
import br.com.senac.projeto.costcenter.domain.CostCenterDTO;
import br.com.senac.projeto.costcenter.exceptions.CostCenterNotFoundException;
import br.com.senac.projeto.costcenter.repository.CostCenterRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class CostCenterService {
    private final CostCenterRepository costCenterRepository;

    public Page<CostCenterDTO> getAll(Pageable pageable){
        Page<CostCenter>costCenterFound = costCenterRepository.findAll(pageable);
        return costCenterFound.map(costCenter -> new CostCenterDTO(
                costCenter.getId(),
                costCenter.getName(),
                costCenter.getNumber(),
                costCenter.getTotalContractValue(),
                costCenter.getBalanceContractValue(),
                costCenter.getCommittedValue(),
                costCenter.getRealizedValue(),
                costCenter.getResourceSource(),
                costCenter.getActivityList()));
    }

    public CostCenterDTO getById(Long id){
        Optional<CostCenter> costCenterFound = costCenterRepository.findById(id);
        if(costCenterFound.isEmpty()){
            throw new CostCenterNotFoundException("Cost center not found with id: " + id);
        } else {
            CostCenter costCenter = costCenterFound.get();
            return new CostCenterDTO(
                    costCenter.getId(),
                    costCenter.getName(),
                    costCenter.getNumber(),
                    costCenter.getTotalContractValue(),
                    costCenter.getBalanceContractValue(),
                    costCenter.getCommittedValue(),
                    costCenter.getRealizedValue(),
                    costCenter.getResourceSource(),
                    costCenter.getActivityList());
        }
    }

    @Transactional
    public CostCenterDTO create(CostCenterDTO costCenterDTO) {
        CostCenter costCenter = convertToCostCenter(costCenterDTO);
        recalculateContract(costCenter);
        CostCenter costCenterSaved = costCenterRepository.save(costCenter);
        return convertToCostCenterDTO(costCenterSaved);
    }

    @Transactional
    public CostCenterDTO update(Long id, CostCenterDTO newCostCenterDTO){
        CostCenter costCenterFound = costCenterRepository.findById(id)
                .orElseThrow(() -> new CostCenterNotFoundException("Cost Center not found with id: " + id));
        CostCenter costCenterUpdated = updateEntityFromCostCenterDTO(costCenterFound, newCostCenterDTO);
        CostCenter costCenterSaved = costCenterRepository.save(costCenterUpdated);
        return convertToCostCenterDTO(costCenterSaved);
    }

    public void delete(Long id){
        if(!costCenterRepository.existsById(id)){
            throw new CostCenterNotFoundException("Cost center with id" + id + "does not exists");
        }
        costCenterRepository.deleteById(id);
    }

    private CostCenter convertToCostCenter(CostCenterDTO dto){
        return new CostCenter(dto.Id(), dto.name(), dto.number(), dto.totalContractValue(), dto.balanceContractValue(),
                dto.committedValue(),dto.realizedValue(), dto.resourceSource(), dto.activityList());
    }

    private CostCenterDTO convertToCostCenterDTO(CostCenter costCenter){
        return new CostCenterDTO(costCenter.getId(), costCenter.getName(), costCenter.getNumber(), costCenter.getTotalContractValue(), costCenter.getBalanceContractValue(),
                costCenter.getCommittedValue(), costCenter.getRealizedValue(), costCenter.getResourceSource(), costCenter.getActivityList());
    }

    private CostCenter updateEntityFromCostCenterDTO(CostCenter existingEntity, CostCenterDTO dto){
        existingEntity.setName(dto.name());
        existingEntity.setNumber(dto.number());
        existingEntity.setTotalContractValue(dto.totalContractValue());
        existingEntity.setBalanceContractValue(dto.balanceContractValue());
        existingEntity.setCommittedValue(dto.committedValue());
        existingEntity.setRealizedValue(dto.realizedValue());
        existingEntity.setResourceSource(dto.resourceSource());
        existingEntity.setActivityList(dto.activityList());

        return existingEntity;
    }

    public void recalculateContract(CostCenter costCenter){
        BigDecimal result = BigDecimal.ZERO;
        result = result.add(costCenter.getCommittedValue());
        result = result.add(costCenter.getRealizedValue());
        result = costCenter.getTotalContractValue().subtract(result);

        costCenter.setBalanceContractValue(result);
    }

    public void recalculateCostCenterWhenUpdatingActivity(CostCenter costCenter){
        if(!existsByCostCenter(costCenter)){
            throw new EntityNotFoundException("Cost center do not exists!");
        }
        BigDecimal committedValueSum = BigDecimal.ZERO;
        BigDecimal realizedValueSum = BigDecimal.ZERO;

        if(costCenter.getActivityList() != null)
            for(Activity activity : costCenter.getActivityList()){
                if(!activity.isDeleted()){
                    committedValueSum = committedValueSum.add(activity.getCommittedValue());
                    realizedValueSum = realizedValueSum.add(activity.getRealizedValue());
                }
            }

        costCenter.setCommittedValue(committedValueSum);
        costCenter.setRealizedValue(realizedValueSum);
        recalculateContract(costCenter);

        costCenterRepository.save(costCenter);
    }

    public boolean existsByCostCenter(CostCenter costCenter){
        return costCenterRepository.existsById(costCenter.getId());
    }
}
