package io.bhimsur.posservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductDto implements Serializable {
	private ProductTypeDto productType;
	private Long id;
	private String name;
	private BigDecimal price;
	private Integer stock;
}