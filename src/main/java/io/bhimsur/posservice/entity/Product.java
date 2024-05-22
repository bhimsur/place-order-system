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
public class Product extends Audit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private BigDecimal price;

	@ManyToOne
	@JoinColumn
	private ProductType type;

	@Column(nullable = false)
	private Integer stock;

}