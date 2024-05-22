package io.bhimsur.posservice.repository;

import io.bhimsur.posservice.entity.ProductOrderTransaction;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOrderTransactionRepository extends BaseRepository<ProductOrderTransaction, Long> {
}