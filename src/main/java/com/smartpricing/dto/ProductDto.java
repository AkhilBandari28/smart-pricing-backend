//package com.smartpricing.dto;
//
//import lombok.Data;
//
//@Data
//public class ProductDto {
//
//    private Long productId;
//    private String name;
//    private String category;
//    private Double basePrice;
//    private Integer stock;
//}

//=============================================

//package com.smartpricing.dto;
//
//public class ProductDto {
//
//    private Long productId;
//    private String name;
//    private String category;
//    private double basePrice;
//    private int stock;
//
//    // ===== Getters & Setters =====
//
//    public Long getProductId() {
//        return productId;
//    }
//
//    public void setProductId(Long productId) {
//        this.productId = productId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getCategory() {
//        return category;
//    }
//
//    public void setCategory(String category) {
//        this.category = category;
//    }
//
//    public double getBasePrice() {
//        return basePrice;
//    }
//
//    public void setBasePrice(double basePrice) {
//        this.basePrice = basePrice;
//    }
//
//    public int getStock() {
//        return stock;
//    }
//
//    public void setStock(int stock) {
//        this.stock = stock;
//    }
//}


//==============================================



package com.smartpricing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

    private Long productId;
    private String name;
    private String category;
    private double basePrice;
    private int stock;
}
