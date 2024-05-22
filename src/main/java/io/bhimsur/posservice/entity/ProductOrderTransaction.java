package io.bhimsur.posservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table
public class ProductOrderTransaction extends Audit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private BigDecimal price;

	private String type;

	private Integer quantity;

	@ToString.Exclude
	@ManyToOne
	@JoinColumn
	private Product parentProduct;

	@ManyToOne
	@JoinColumn
	private OrderTransaction orderTransaction;

}