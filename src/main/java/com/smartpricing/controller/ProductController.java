package com.smartpricing.controller;

import com.smartpricing.dto.ProductCreateRequestDto;
import com.smartpricing.dto.ProductDto;
import com.smartpricing.service.ProductService;
import com.smartpricing.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // =========================
    // 🔓 PUBLIC APIs
    // =========================

    // GET all products
    @Operation(
    	    summary = "Get all products",
    	    description = "Fetch all available products (public)"
    	)
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProducts() {

        ApiResponse<List<ProductDto>> response =
                new ApiResponse<>(
                        200,
                        "Products fetched successfully",
                        productService.getAllProducts()
                );

        return ResponseEntity.ok(response);
    }

    // GET product by ID
    @Operation(
    	    summary = "Get product by ID",
    	    description = "Fetch product details using product ID"
    	)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(
            @PathVariable Long id
    ) {
        ApiResponse<ProductDto> response =
                new ApiResponse<>(
                        200,
                        "Product fetched successfully",
                        productService.getProductById(id)
                );

        return ResponseEntity.ok(response);
    }

    // =========================
    // 🔐 ADMIN-ONLY APIs
    // =========================

    // CREATE product
    @Operation(
    	    summary = "Create product",
    	    description = "ADMIN only – create a new product"
    	)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
    		@Valid @RequestBody ProductCreateRequestDto request
    ) {
        ApiResponse<ProductDto> response =
                new ApiResponse<>(
                        201,
                        "Product created successfully",
                        productService.createProduct(request)
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // UPDATE product
    @Operation(
    	    summary = "Update product",
    	    description = "ADMIN only – update product details"
    	)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto productDto
    ) {
        ApiResponse<ProductDto> response =
                new ApiResponse<>(
                        200,
                        "Product updated successfully",
                        productService.updateProduct(id, productDto)
                );

        return ResponseEntity.ok(response);
    }

    // DELETE product
    @Operation(
    	    summary = "Delete product",
    	    description = "ADMIN only – delete a product"
    	)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);

        ApiResponse<String> response =
                new ApiResponse<>(
                        200,
                        "Product deleted successfully",
                        "Product with ID " + id + " removed"
                );

        return ResponseEntity.ok(response);
    }
}
