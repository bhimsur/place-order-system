package io.bhimsur.posservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
public class SaveProductRequest implements Serializable {
	@NotBlank
	private String name;
	@PositiveOrZero
	private BigDecimal price;
	@PositiveOrZero
	private Integer stock;
	@Positive
	private Long typeId;
}