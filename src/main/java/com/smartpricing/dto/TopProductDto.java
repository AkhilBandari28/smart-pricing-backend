package com.smartpricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductDto {

    private Long productId;
    private String productName;
    private long soldCount;
}
