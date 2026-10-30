package br.com.senac.projeto.costcenter.domain;

import br.com.senac.projeto.activity.domain.Activity;
import br.com.senac.projeto.resourcesource.domain.ResourceSource;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CostCenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@NotBlank(message = "name cannot be blank")
    private String name;
    //@NotBlank(message = "number cannot be blank")
    private String number;
    private BigDecimal totalContractValue;
    private BigDecimal balanceContractValue;
    private BigDecimal committedValue;
    private BigDecimal realizedValue;
    @JsonManagedReference
    @OneToOne(fetch =  FetchType.EAGER, cascade =  CascadeType.MERGE)
    private ResourceSource resourceSource;
    @OneToMany(mappedBy = "costCenter")
    private List<Activity> activityList;

    @JsonManagedReference
    public List<Activity> getActivityList(){
        return activityList;
    }

}
