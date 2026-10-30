package br.com.senac.projeto.costcenter.domain;

import br.com.senac.projeto.activity.domain.Activity;
import br.com.senac.projeto.resourcesource.domain.ResourceSource;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record CostCenterDTO(Long Id,
                            //@NotBlank(message = "{name cannot be blank}")
                            String name,
                            //@NotBlank(message = "{number cannot be blank}")
                            String number,
                            BigDecimal totalContractValue,
                            BigDecimal balanceContractValue,
                            BigDecimal committedValue,
                            BigDecimal realizedValue,
                            ResourceSource resourceSource,
                            List<Activity> activityList) {
}
