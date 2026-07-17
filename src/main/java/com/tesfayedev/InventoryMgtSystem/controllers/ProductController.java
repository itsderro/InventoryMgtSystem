package com.tesfayedev.InventoryMgtSystem.controllers;

import com.tesfayedev.InventoryMgtSystem.dtos.ProductDTO;
import com.tesfayedev.InventoryMgtSystem.dtos.Response;
import com.tesfayedev.InventoryMgtSystem.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> saveProduct(
            @RequestParam("imageFile")MultipartFile imageFile,
            @RequestParam("name")String name,
            @RequestParam("sku")String sku,
            @RequestParam("retailPrice")BigDecimal retailPrice,
            @RequestParam("wholeSalePrice")BigDecimal wholeSalePrice,
            @RequestParam("stockQuantity")Integer stockQuantity,
            @RequestParam("categoryId")Long categoryId,
            @RequestParam(value = "description",required = false)String description,
            @RequestParam("costPrice")BigDecimal costPrice
            ){
        ProductDTO productDTO = new ProductDTO();
            productDTO.setName(name);
            productDTO.setSku(sku);
            productDTO.setRetailPrice(retailPrice);
            productDTO.setWholeSalePrice(  wholeSalePrice);
            productDTO.setStockQuantity(stockQuantity);
            productDTO.setCategoryId(categoryId);
            productDTO.setDescription(description);
            productDTO.setCostPrice(costPrice);

        return ResponseEntity.ok(productService.saveProduct(productDTO, imageFile));
    }

    @PutMapping("/update/{id}") // Keep this URL path
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateProduct(
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sku", required = false) String sku,
            @RequestParam(value = "retailPrice", required = false) BigDecimal retailPrice,
            @RequestParam(value = "wholeSalePrice", required = false) BigDecimal wholeSalePrice,
            @RequestParam(value = "stockQuantity", required = false) Integer stockQuantity,
            @PathVariable("id") Long productId, // CHANGE THIS from @RequestParam to @PathVariable
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "description", required = false) String description
    ){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(name);
        productDTO.setSku(sku);
        productDTO.setRetailPrice(retailPrice);
        productDTO.setWholeSalePrice(wholeSalePrice);
        productDTO.setProductId(productId); // This will now correctly use the URL ID
        productDTO.setStockQuantity(stockQuantity);
        productDTO.setCategoryId(categoryId);
        productDTO.setDescription(description);

        return ResponseEntity.ok(productService.updateProduct(productDTO, imageFile));
    }


    @GetMapping("/all")
    public ResponseEntity<Response> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteProduct(@PathVariable Long id){
        return ResponseEntity.ok(productService.deleteProduct(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchProduct(@RequestParam String input){
        return ResponseEntity.ok(productService.searchProduct(input));
    }

}
