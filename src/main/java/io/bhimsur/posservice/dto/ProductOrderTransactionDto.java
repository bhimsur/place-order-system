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
public class ProductOrderTransactionDto implements Serializable {
	private String name;
	private BigDecimal price;
	private Integer quantity;
	private String type;
}