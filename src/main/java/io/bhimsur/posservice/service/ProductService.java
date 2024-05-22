package io.bhimsur.posservice.service;

import io.bhimsur.posservice.dto.ProductDto;
import io.bhimsur.posservice.dto.ProductTypeDto;
import io.bhimsur.posservice.dto.SaveProductRequest;
import io.bhimsur.posservice.entity.Product;
import io.bhimsur.posservice.entity.ProductType;
import io.bhimsur.posservice.repository.ProductRepository;
import io.bhimsur.posservice.util.SpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ProductService {
	private final ProductRepository productRepository;
	private final ProductTypeService productTypeService;

	public ProductDto mapper(Product entity) {
		ProductTypeDto type = null;
		if (entity.getType() != null) {
			type = productTypeService.mapper(entity.getType());
		}

		ProductDto dto = new ProductDto();
		dto.setProductType(type);
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setPrice(entity.getPrice());
		dto.setStock(entity.getStock());

		return dto;
	}

	@Transactional(readOnly = true)
	public Product findById(Long id) throws Exception {
		log.info("start findById: {}", id);
		try {
			SpecificationBuilder<Product> spec1 = new SpecificationBuilder<>("id", "=", id);
			SpecificationBuilder<Product> spec2 = new SpecificationBuilder<>("deleted", "=", false);
			Specification<Product> specification = Specification.where(spec1).and(spec2);
			return productRepository.findOne(specification).orElseThrow(() -> new Exception("Product Not Found"));
		} catch (Exception e) {
			log.error("error findById: {}", e.getMessage(), e);
			throw e;
		}
	}

	@Transactional(readOnly = true)
	public Page<ProductDto> findAll(String type, String name, Boolean deleted, Pageable pageable) {
		log.info("start findAll: type {} name {} deleted {} page {}", type, name, deleted, pageable);
		try {
			SpecificationBuilder<Product> spec1 = new SpecificationBuilder<>("type.name", "%%", type);
			SpecificationBuilder<Product> spec2 = new SpecificationBuilder<>("name", "%%", name);
			SpecificationBuilder<Product> spec3 = new SpecificationBuilder<>("deleted", "=", deleted);

			Specification<Product> spec = Specification.where(spec3);
			if (StringUtils.hasLength(type)) {
				spec = spec.and(spec1);
			}
			if (StringUtils.hasLength(name)) {
				spec = spec.and(spec2);
			}

			return productRepository.findAll(spec, pageable).map(this::mapper);
		} catch (Exception e) {
			log.error("error findAll: {}", e.getMessage(), e);
			throw e;
		}
	}

	public ProductDto saveProduct(Long id, SaveProductRequest request) throws Exception {
		log.info("start saveProduct, id: {}, request: {}", id, request);
		try {
			ProductType type = null;
			if (null != request.getTypeId()) {
				type = productTypeService.findById(request.getTypeId(), false);
			}

			Product product = new Product();
			if (null != id) {
				SpecificationBuilder<Product> spec1 = new SpecificationBuilder<>("deleted", "=", false);
				SpecificationBuilder<Product> spec2 = new SpecificationBuilder<>("id", "=", id);

				Specification<Product> spec = Specification.where(spec2).and(spec1);
				product = productRepository.findOne(spec).orElseThrow(() -> new Exception("Invalid Id"));
			}

			product.setStock(request.getStock());
			product.setName(request.getName());
			product.setPrice(request.getPrice());

			if (null != type) {
				product.setType(type);
			}
			if (null != id) {
				product.setUpdatedAt(LocalDateTime.now());
				product.setUpdatedBy("system");
			} else {
				if (null == type) {
					throw new Exception("Invalid product type");
				}
				product.setDeleted(false);
				product.setCreatedAt(LocalDateTime.now());
				product.setCreatedBy("system");
			}

			product = productRepository.saveAndFlush(product);
			return this.mapper(product);
		} catch (Exception e) {
			log.error("error saveProduct: {}", e.getMessage(), e);
			throw e;
		}
	}

	public void deleteProduct(Long id) {
		log.info("start deleteProduct: {}", id);
		try {
			productRepository.findById(id).ifPresent(product -> {
				product.setDeleted(true);
				product.setUpdatedBy("system");
				product.setUpdatedAt(LocalDateTime.now());
				productRepository.saveAndFlush(product);
			});
		} catch (Exception e) {
			log.error("error deleteProduct: {}", e.getMessage(), e);
			throw e;
		}
	}

	public Product checkAndSetStock(Product product, Integer request) throws Exception {
		if (request > product.getStock()) {
			throw new Exception("Invalid Quantity Stock");
		} else {
			product.setStock(product.getStock() - request);
		}
		return product;
	}
}