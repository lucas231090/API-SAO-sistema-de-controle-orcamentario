package br.com.senac.projeto.budgetarynature.domain;

import br.com.senac.projeto.product.domain.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.*;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "budgetary_nature")
@SQLDelete(sql = "UPDATE budgetary_nature SET deleted = true WHERE id=?")
//@Where(clause = "delete=false")
@FilterDef(name = "deletedBudgetaryNatureFilter", parameters = @ParamDef( name = "isDeleted", type = org.hibernate.type.descriptor.java.BooleanJavaType.class ))
@Filter(name = "deletedBudgetaryNatureFilter", condition = "deleted = :isDeleted")
public class BudgetaryNature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@NotBlank(message = "name cannot be blank")
    private String name;
    //@NotBlank(message = "number cannot be blank")
    private String number;
    private BigDecimal balanceBudgetaryNatureValue;
    private BigDecimal totalBudgetaryNatureValue  = BigDecimal.ZERO;
    //@NotNull(message = "Committed value cannot be null")
    private BigDecimal committedValue;
    //@NotNull(message = "Realized value cannot be null")
    private BigDecimal realizedValue;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Product product;
    private boolean deleted = Boolean.FALSE;

    @JsonBackReference
    public Product getProduct(){
        return product;
    }
}