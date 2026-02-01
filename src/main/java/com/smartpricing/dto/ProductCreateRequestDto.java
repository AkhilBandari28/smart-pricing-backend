package com.smartpricing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ProductCreateRequestDto {

	@NotBlank
	private String name;

	@NotBlank
	private String category;

	@NotNull
	private Double basePrice;

	@NotNull
	private Integer stock;

}
