package br.com.senac.projeto.product.controller;

import br.com.senac.projeto.product.domain.ProductDTO;
import br.com.senac.projeto.product.exceptions.ProductNotFoundException;
import br.com.senac.projeto.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> list(@RequestParam(value = "isDeleted", required = false, defaultValue = "false") boolean isDeleted){
        return ResponseEntity.ok(productService.getAll(isDeleted));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id){
        try {
            ProductDTO productDTO = productService.getById(id);
            return ResponseEntity.ok(productDTO);
        }catch (ProductNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<ProductDTO> create(@PathVariable Long id, @RequestBody /*@Valid*/ ProductDTO productDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(id, productDTO));
    }

    @PutMapping("/{id}")
    public ProductDTO update(@PathVariable Long id, @RequestBody ProductDTO updateProduct){
        return productService.update(id, updateProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDTO> delete(@PathVariable Long id){
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
