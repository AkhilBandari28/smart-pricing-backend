package com.smartpricing.dto;

public class AdminStatsResponse {

    private long totalProducts;
    private long totalOrders;

    public AdminStatsResponse(long totalProducts, long totalOrders) {
        this.totalProducts = totalProducts;
        this.totalOrders = totalOrders;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public long getTotalOrders() {
        return totalOrders;
    }
}
