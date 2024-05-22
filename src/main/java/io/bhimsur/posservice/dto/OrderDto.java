package io.bhimsur.posservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto implements Serializable {
	private String refNum;
	private String customerName;
	private String customerAddress;
	private BigDecimal totalPrice;
	private List<ProductOrderTransactionDto> items;
}