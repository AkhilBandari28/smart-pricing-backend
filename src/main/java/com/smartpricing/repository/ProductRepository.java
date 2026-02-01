//package com.smartpricing.repository;
//
//import com.smartpricing.entity.Product;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface ProductRepository extends JpaRepository<Product, Long> {
//}


//==================================================

//package com.smartpricing.repository;
//
//import com.smartpricing.entity.Product;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface ProductRepository extends JpaRepository<Product, Long> {
//}


//===================================================


package com.smartpricing.repository;

import com.smartpricing.entity.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findTop5ByOrderByStockDesc();

}


