package io.bhimsur.posservice.repository;

import io.bhimsur.posservice.entity.OrderTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTransactionRepository extends BaseRepository<OrderTransaction, Long> {
	@EntityGraph(attributePaths = {"customer", "productOrderTransactions"})
	Page<OrderTransaction> findAll(Specification<OrderTransaction> specification, Pageable pageable);
}