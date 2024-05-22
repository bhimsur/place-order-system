package io.bhimsur.posservice.repository;

import io.bhimsur.posservice.entity.Cart;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends BaseRepository<Cart, Long> {

	@EntityGraph(attributePaths = {"product"})
	List<Cart> findAll(Specification<Cart> specification, Sort sort);

	@EntityGraph(attributePaths = {"customer", "product", "product.type"})
	List<Cart> findAll(Specification<Cart> specification);

	@EntityGraph(attributePaths = {"product"})
	Optional<Cart> findOne(Specification<Cart> specification);
}