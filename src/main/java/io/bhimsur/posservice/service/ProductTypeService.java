package io.bhimsur.posservice.service;

import io.bhimsur.posservice.dto.ProductTypeDto;
import io.bhimsur.posservice.entity.ProductType;
import io.bhimsur.posservice.repository.ProductTypeRepository;
import io.bhimsur.posservice.util.SpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ProductTypeService {
	private final ProductTypeRepository productTypeRepository;

	@Transactional(readOnly = true)
	public Page<ProductTypeDto> findAll(Boolean deleted, Pageable pageable) {
		try {
			SpecificationBuilder<ProductType> spec1 = new SpecificationBuilder<>("deleted", "=", deleted);
			return productTypeRepository.findAll(spec1, pageable).map(this::mapper);
		} catch (Exception e) {
			log.error("error findAll productType: {}", e.getMessage(), e);
			throw e;
		}
	}

	@Transactional(readOnly = true)
	public ProductType findById(Long id, Boolean deleted) throws Exception {
		try {
			SpecificationBuilder<ProductType> spec1 = new SpecificationBuilder<>("id", "=", id);
			SpecificationBuilder<ProductType> spec2 = new SpecificationBuilder<>("deleted", "=", deleted);
			Specification<ProductType> spec = Specification.where(spec1).and(spec2);
			return productTypeRepository.findOne(spec).orElseThrow(() -> new Exception("Data not found"));
		} catch (Exception e) {
			log.error("error findById: {}", e.getMessage(), e);
			throw e;
		}
	}

	public ProductTypeDto mapper(ProductType entity) {
		ProductTypeDto dto = new ProductTypeDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		return dto;
	}
}