package com.smartpricing.service;

import com.smartpricing.dao.ProductDao;
import com.smartpricing.dto.ProductCreateRequestDto;
import com.smartpricing.dto.ProductDto;
import com.smartpricing.entity.Product;
import com.smartpricing.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    // =========================
    // GET ALL PRODUCTS
    // =========================
    public List<ProductDto> getAllProducts() {
        return productDao.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // =========================
    // GET PRODUCT BY ID
    // =========================
    public ProductDto getProductById(Long productId) {

        Product product = productDao.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id " + productId
                        )
                );

        return mapToDto(product);
    }

    // =========================
    // CREATE PRODUCT (ADMIN)
    // =========================
    @Transactional
    public ProductDto createProduct(ProductCreateRequestDto request) {

        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setBasePrice(request.getBasePrice());
        product.setStock(request.getStock());

        Product savedProduct = productDao.save(product);

        return mapToDto(savedProduct);
    }

    // =========================
    // UPDATE PRODUCT (ADMIN)
    // =========================
    @Transactional
    public ProductDto updateProduct(Long productId, ProductDto dto) {

        Product product = productDao.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id " + productId
                        )
                );

        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setBasePrice(dto.getBasePrice());
        product.setStock(dto.getStock());

        Product updatedProduct = productDao.save(product);

        return mapToDto(updatedProduct);
    }

    // =========================
    // DELETE PRODUCT (ADMIN)
    // =========================
    @Transactional
    public void deleteProduct(Long productId) {

        Product product = productDao.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id " + productId
                        )
                );

        productDao.delete(product);
    }

    // =========================
    // ENTITY → DTO MAPPER
    // =========================
    private ProductDto mapToDto(Product product) {

        ProductDto dto = new ProductDto();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setCategory(product.getCategory());
        dto.setBasePrice(product.getBasePrice());
        dto.setStock(product.getStock());

        return dto;
    }
}
