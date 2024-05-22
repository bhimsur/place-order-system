package io.bhimsur.posservice.service;


import io.bhimsur.posservice.entity.Customer;
import io.bhimsur.posservice.repository.CustomerRepository;
import io.bhimsur.posservice.util.SpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

	private final CustomerRepository customerRepository;

	@Transactional(readOnly = true)
	public Customer findByUsername(String username) throws Exception {
		try {
			SpecificationBuilder<Customer> spec1 = new SpecificationBuilder<>("username", "=", username);
			SpecificationBuilder<Customer> spec2 = new SpecificationBuilder<>("deleted", "=", false);

			Specification<Customer> spec = Specification.where(spec1).and(spec2);

			return customerRepository.findOne(spec)
					.orElseThrow(() -> new Exception("Invalid Username"));
		} catch (Exception e) {
			log.error("error findByUsername: {}", e.getMessage(), e);
			throw e;
		}
	}
}