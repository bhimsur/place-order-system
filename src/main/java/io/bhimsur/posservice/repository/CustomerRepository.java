package io.bhimsur.posservice.repository;

import io.bhimsur.posservice.entity.Customer;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends BaseRepository<Customer, Long> {
}