//package com.smartpricing.dao;
//
//import com.smartpricing.entity.Product;
//import com.smartpricing.repository.ProductRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public class ProductDao {
//
//    private final ProductRepository productRepository;
//
//    public ProductDao(ProductRepository productRepository) {
//        this.productRepository = productRepository;
//    }
//
//    public Product save(Product product) {
//        return productRepository.save(product);
//    }
//
//    public List<Product> findAll() {
//        return productRepository.findAll();
//    }
//
//    public Optional<Product> findById(Long productId) {
//        return productRepository.findById(productId);
//    }
//}


//=====================================================


//package com.smartpricing.dao;
//
//import com.smartpricing.entity.Product;
//import com.smartpricing.repository.ProductRepository;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Optional;
//
//@Component
//public class ProductDao {
//
//    private final ProductRepository productRepository;
//
//    public ProductDao(ProductRepository productRepository) {
//        this.productRepository = productRepository;
//    }
//
//    public List<Product> findAll() {
//        return productRepository.findAll();
//    }
//
//    public Optional<Product> findById(Long id) {
//        return productRepository.findById(id);
//    }
//
//    public Product save(Product product) {
//        return productRepository.save(product);
//    }
//}


//======================================================



//package com.smartpricing.dao;
//
//import com.smartpricing.entity.Product;
//import com.smartpricing.repository.ProductRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public class ProductDao {
//
//    private final ProductRepository productRepository;
//
//    public ProductDao(ProductRepository productRepository) {
//        this.productRepository = productRepository;
//    }
//
//    public List<Product> findAll() {
//        return productRepository.findAll();
//    }
//
//    public Optional<Product> findById(Long id) {
//        return productRepository.findById(id);
//    }
//}



//================================================


package com.smartpricing.dao;

import com.smartpricing.entity.Product;
import com.smartpricing.repository.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductDao {

    private final ProductRepository productRepository;

    public ProductDao(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // ✅ NEW
    public Product save(Product product) {
        return productRepository.save(product);
    }
    
    public void delete(Product product) {
        productRepository.delete(product);
    }

}
