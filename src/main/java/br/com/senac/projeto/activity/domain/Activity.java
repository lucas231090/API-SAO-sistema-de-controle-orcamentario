package br.com.senac.projeto.activity.domain;

import br.com.senac.projeto.costcenter.domain.CostCenter;
import br.com.senac.projeto.product.domain.Product;
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
@SQLDelete(sql = "UPDATE activity SET deleted = true WHERE id=?")
@FilterDef(name = "deletedActivityFilter", parameters = @ParamDef(name = "isDeleted", type = org.hibernate.type.descriptor.java.BooleanJavaType.class))
@Filter(name = "deletedActivityFilter", condition = "deleted = :isDeleted")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@NotBlank(message = "name cannot be blank")
    private String name;
    //@NotBlank(message = "number cannot be blank")
    private String number;
    private BigDecimal balanceActivityValue;
    private BigDecimal totalActivityValue;
    private BigDecimal committedValue;
    private BigDecimal realizedValue;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private CostCenter costCenter;
    @OneToMany(mappedBy = "activity")
    private List<Product> productList;
    private boolean deleted = Boolean.FALSE;

    @JsonBackReference
    public CostCenter getCostCenter(){
        return costCenter;
    }

    @JsonManagedReference
    public List<Product> getProductList(){
        return productList;
    }
}
