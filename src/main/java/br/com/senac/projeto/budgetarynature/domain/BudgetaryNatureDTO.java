package br.com.senac.projeto.budgetarynature.domain;

import br.com.senac.projeto.product.domain.Product;

import java.math.BigDecimal;

public record BudgetaryNatureDTO(
        String name,
        String number,
        BigDecimal balanceBudgetaryNatureValue,
        BigDecimal totalBudgetaryNatureValue,
        BigDecimal committedValue,
        BigDecimal realizedValue,
        Product product,
        boolean deleted){

}
