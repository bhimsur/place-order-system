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
public class CartDto implements Serializable {
	private Long id;
	private Integer quantity;
	private ProductDto product;
	private BigDecimal total;
}