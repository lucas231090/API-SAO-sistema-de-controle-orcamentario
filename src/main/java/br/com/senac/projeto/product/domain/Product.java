package br.com.senac.projeto.product.domain;

import br.com.senac.projeto.activity.domain.Activity;
import br.com.senac.projeto.budgetarynature.domain.BudgetaryNature;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE product SET deleted = true WHERE id=?")
@FilterDef(name = "deletedProductFilter", parameters = @ParamDef(name = "isDeleted", type = org.hibernate.type.descriptor.java.BooleanJavaType.class ))
@Filter(name = "deletedProductFilter", condition = "deleted = :isDeleted")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@NotBlank(message = "name not blank")
    private String name;
    //@NotBlank(message = "number not blank")
    private String number;
    private BigDecimal balanceProductValue;
    private BigDecimal totalProductValue;
    private BigDecimal committedValue;
    private BigDecimal realizedValue;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Activity activity;
    @OneToMany(mappedBy = "product")
    private List<BudgetaryNature> budgetaryNatureList;
    private boolean deleted = Boolean.FALSE;

    @JsonBackReference
    public Activity getActivity(){
        return activity;
    }

    @JsonManagedReference
    public List<BudgetaryNature> getBudgetaryNatureList(){
        return budgetaryNatureList;
    }
}
