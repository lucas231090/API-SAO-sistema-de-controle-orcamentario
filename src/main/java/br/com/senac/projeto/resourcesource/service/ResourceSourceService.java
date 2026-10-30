package br.com.senac.projeto.resourcesource.service;

import br.com.senac.projeto.resourcesource.domain.ResourceSource;
import br.com.senac.projeto.resourcesource.domain.ResourceSourceDTO;
import br.com.senac.projeto.resourcesource.exceptions.ResourceSourceNotFoundException;
import br.com.senac.projeto.resourcesource.repository.ResourceSourceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceSourceService {

    private final ResourceSourceRepository resourceSourceRepository;

    public Page<ResourceSourceDTO> getAll(Pageable pageable){
        Page<ResourceSource>resourceFound = resourceSourceRepository.findAll(pageable);
        return resourceFound.map(resourceSource -> new ResourceSourceDTO(resourceSource.getId(), resourceSource.getName(),
                resourceSource.getNumber(),resourceSource.getCostCenter(),resourceSource.getType()));
    }

    public ResourceSourceDTO getById(Long id){
        ResourceSource resourceSource = resourceSourceRepository.findById(id)
        .orElseThrow(() -> new ResourceSourceNotFoundException("Resource source not found with id: " +id));
               return new ResourceSourceDTO(resourceSource.getId(), resourceSource.getName(),
                       resourceSource.getNumber(),resourceSource.getCostCenter(),resourceSource.getType());
        }

    @Transactional
    public ResourceSourceDTO create(ResourceSourceDTO resourceSourceDTO){
        ResourceSource resourceSource = convertToResourceSource(resourceSourceDTO);
        ResourceSource resourceSourceSaved = resourceSourceRepository.save(resourceSource);
        return convertToResourceSourceDTO(resourceSourceSaved);
    }

//    @Transactional
//    public Optional<ResourceSource> update(Long id, ResourceSource newResourceSource){
//        return resourceSourceRepository.findById(id)
//                .map( oldResourceSource -> {
//                    ResourceSource updatedResourceSource = oldResourceSource.updateWith(newResourceSource);
//                    return resourceSourceRepository.save(updatedResourceSource);
//                });
//    }

    @Transactional
    public ResourceSourceDTO update(Long id, ResourceSourceDTO newResourceSourceDTO){
        ResourceSource resourceSourceFound = resourceSourceRepository.findById(id)
                .orElseThrow(() -> new ResourceSourceNotFoundException("Resource source not found with id: " + id));

        ResourceSource resourceSourceUpdated = updateResourceFromDTO(resourceSourceFound, newResourceSourceDTO);
        ResourceSource resourceSourceSaved = resourceSourceRepository.save(resourceSourceUpdated);
        return convertToResourceSourceDTO(resourceSourceSaved);
    }

    public void delete(Long id){
        if(!resourceSourceRepository.existsById(id)){
            throw new ResourceSourceNotFoundException("Resource source with id " + id + "does not exists");
        }
        resourceSourceRepository.deleteById(id);
    }

    private ResourceSource convertToResourceSource(ResourceSourceDTO dto){
        return new ResourceSource(dto.id(), dto.name(), dto.number(), dto.costCenter(), dto.type());
    }

    private ResourceSourceDTO convertToResourceSourceDTO(ResourceSource resourceSource){
        return new ResourceSourceDTO(resourceSource.getId(), resourceSource.getName(),
                resourceSource.getNumber(), resourceSource.getCostCenter(), resourceSource.getType());
    }

    private ResourceSource updateResourceFromDTO(ResourceSource existingEntity, ResourceSourceDTO dto){
        existingEntity.setName((dto.name()));
        existingEntity.setNumber(dto.number());
        existingEntity.setType(dto.type());
        existingEntity.setCostCenter(dto.costCenter());

        return existingEntity;
    }

}
