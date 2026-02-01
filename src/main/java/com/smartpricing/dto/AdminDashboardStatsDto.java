package com.smartpricing.dto;

import lombok.Data;

@Data
public class AdminDashboardStatsDto {

    private long totalOrders;
    private long totalUsers;
    private double totalRevenue;
    private long cancelledOrders;
    private long pendingOrders;
}
