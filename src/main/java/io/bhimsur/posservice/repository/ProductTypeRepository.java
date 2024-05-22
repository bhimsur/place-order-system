package io.bhimsur.posservice.repository;

import io.bhimsur.posservice.entity.ProductType;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTypeRepository extends BaseRepository<ProductType, Long> {
}