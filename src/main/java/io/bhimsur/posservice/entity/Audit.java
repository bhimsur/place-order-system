package io.bhimsur.posservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public abstract class Audit implements Serializable {
	@Column(nullable = false)
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	@Column(nullable = false)
	private String createdBy;
	private String updatedBy;
	@Column(nullable = false)
	private Boolean deleted;
}