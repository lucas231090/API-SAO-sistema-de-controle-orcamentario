package br.com.senac.projeto.activity.domain;

import br.com.senac.projeto.costcenter.domain.CostCenter;
import br.com.senac.projeto.product.domain.Product;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record ActivityDTO(
        Long id,
        @NotBlank(message = "name cannot be blank")
        String name,
        @NotBlank(message = "number cannot be blank")
        String number,
        BigDecimal balanceActivityValue,
        BigDecimal totalActivityValue,
        BigDecimal committedValue,
        BigDecimal realizedValue,
        CostCenter costCenter,
        List<Product> productList,
        boolean deleted) {
}
