package br.com.senac.projeto.resourcesource.domain;

import br.com.senac.projeto.costcenter.domain.CostCenter;
import br.com.senac.projeto.type.Type;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResourceSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name can't be blank")
    //@Pattern(regexp = "^[A-Z]+(.)*", message = "The first letter must be capitalized")
    private String name;
    @NotBlank(message = "number can't be blank")
    private String number;
    @JsonBackReference
    @OneToOne(mappedBy = "resourceSource")
    private CostCenter costCenter;
    @OneToOne
    private Type type;

    public ResourceSource updateWith(ResourceSource resourceSource){
        return new ResourceSource(
                this.id,
                resourceSource.name,
                resourceSource.number,
                resourceSource.costCenter,
                resourceSource.type
        );
    }
}
