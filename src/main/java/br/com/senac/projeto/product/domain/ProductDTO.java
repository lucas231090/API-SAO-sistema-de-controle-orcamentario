package br.com.senac.projeto.product.domain;

import br.com.senac.projeto.activity.domain.Activity;
import br.com.senac.projeto.budgetarynature.domain.BudgetaryNature;

import java.math.BigDecimal;
import java.util.List;

public record ProductDTO(
        Long id,
        String name,
        String number,
        BigDecimal balanceProductValue,
        BigDecimal totalProductValue,
        BigDecimal committedValue,
        BigDecimal realizedValue,
        Activity activity,
        List<BudgetaryNature> budgetaryNatureList,
        boolean deleted) {
}
