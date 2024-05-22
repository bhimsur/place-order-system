package io.bhimsur.posservice.entity;

import io.bhimsur.posservice.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table
public class OrderTransaction extends Audit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	@Column(nullable = false)
	private String referenceNumber;

	@ToString.Exclude
	@ManyToOne
	@JoinColumn
	private Customer customer;

	@ToString.Exclude
	@OneToMany(mappedBy = "orderTransaction", cascade = CascadeType.PERSIST)
	private Set<ProductOrderTransaction> productOrderTransactions = new LinkedHashSet<>();

}