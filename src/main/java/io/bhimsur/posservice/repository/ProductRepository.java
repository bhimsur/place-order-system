package io.bhimsur.posservice.repository;

import io.bhimsur.posservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends BaseRepository<Product, Long> {

	@EntityGraph(attributePaths = {"type"})
	Page<Product> findAll(Specification<Product> specification, Pageable pageable);
}