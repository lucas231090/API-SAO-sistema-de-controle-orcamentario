package br.com.senac.projeto.resourcesource.domain;

import br.com.senac.projeto.costcenter.domain.CostCenter;
import br.com.senac.projeto.type.Type;
import jakarta.validation.constraints.NotBlank;


public record ResourceSourceDTO(
        Long id,
        @NotBlank(message = "Name can't be blank")
        String name,
        @NotBlank(message = "number can't be blank")
        String number,
        CostCenter costCenter,
        Type type
) {}
