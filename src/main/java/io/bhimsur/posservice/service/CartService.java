package io.bhimsur.posservice.service;

import io.bhimsur.posservice.dto.AddToCartRequest;
import io.bhimsur.posservice.dto.CartDto;
import io.bhimsur.posservice.dto.CartResponse;
import io.bhimsur.posservice.entity.Cart;
import io.bhimsur.posservice.entity.Customer;
import io.bhimsur.posservice.entity.Product;
import io.bhimsur.posservice.repository.CartRepository;
import io.bhimsur.posservice.util.SpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class CartService {
	private final CartRepository cartRepository;
	private final CustomerService customerService;
	private final ProductService productService;

	public Boolean addToCart(AddToCartRequest request) throws Exception {
		log.info("start addToCart, request: {}", request);
		try {
			Cart cart = this.findByProductIdAndUsername(request.getProductId(), request.getUsername());
			Product product;
			if (null == cart.getProduct()) {
				product = productService.findById(request.getProductId());
			} else {
				product = cart.getProduct();
			}

			if (request.getQuantity() > product.getStock()) {
				throw new Exception("Invalid Stock Quantity");
			}

			if (null == cart.getId()) {
				log.info("create new item in cart");
				Customer customer = customerService.findByUsername(request.getUsername());

				cart.setProduct(product);
				cart.setCustomer(customer);
				cart.setQuantity(request.getQuantity());
				cart.setDeleted(false);
				cart.setCreatedAt(LocalDateTime.now());
				cart.setCreatedBy(request.getUsername());
			} else {
				log.info("updating quantity in cart");
				cart.setDeleted(false);
				cart.setQuantity(request.getQuantity());
				cart.setUpdatedAt(LocalDateTime.now());
				cart.setUpdatedBy(request.getUsername());
			}

			return cartRepository.saveAndFlush(cart).getId() > 0;
		} catch (Exception e) {
			log.error("error addToCart: {}", e.getMessage(), e);
			throw e;
		}
	}

	private Cart findByProductIdAndUsername(Long productId, String username) {
		SpecificationBuilder<Cart> spec1 = new SpecificationBuilder<>("product.id", "=", productId);
		SpecificationBuilder<Cart> spec2 = new SpecificationBuilder<>("customer.username", "=", username);
		SpecificationBuilder<Cart> spec3 = new SpecificationBuilder<>("product.deleted", "=", false);

		Specification<Cart> specification = Specification.where(spec1).and(spec2).and(spec3);
		return cartRepository.findOne(specification).orElse(new Cart());
	}

	@Transactional(readOnly = true)
	public CartResponse findCartByUsername(String username, Sort sort) throws Exception {
		log.info("start findCartByUsername: {}, {}", username, sort);
		try {
			SpecificationBuilder<Cart> spec1 = new SpecificationBuilder<>("customer.username", "=", username);
			SpecificationBuilder<Cart> spec2 = new SpecificationBuilder<>("product.deleted", "=", false);
			SpecificationBuilder<Cart> spec3 = new SpecificationBuilder<>("deleted", "=", false);
			if (sort.isUnsorted()) {
				sort = Sort.by(Sort.Direction.DESC, "id");
			}

			Specification<Cart> specification = Specification.where(spec1).and(spec2).and(spec3);

			List<Cart> items = cartRepository.findAll(specification, sort);

			List<CartDto> content = items.stream().map(this::mapper).collect(Collectors.toList());

			Customer customer = customerService.findByUsername(username);
			BigDecimal totalPrice = content.stream().map(CartDto::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

			return new CartResponse(content, customer.getName(), customer.getAddress(), totalPrice);
		} catch (Exception e) {
			log.error("error findCartByUsername: {}", e.getMessage(), e);
			throw e;
		}
	}

	private CartDto mapper(Cart entity) {
		CartDto dto = new CartDto();
		dto.setId(entity.getId());

		dto.setQuantity(entity.getQuantity());

		Product product = entity.getProduct();
		if (null != product) {
			dto.setProduct(productService.mapper(product));
			dto.setTotal(product.getPrice().multiply(BigDecimal.valueOf(entity.getQuantity())));
		}

		return dto;
	}

	@Transactional(readOnly = true)
	public List<Cart> findCartByIdIn(List<Long> ids) {
		SpecificationBuilder<Cart> spec1 = new SpecificationBuilder<>("id", "in", ids);
		SpecificationBuilder<Cart> spec2 = new SpecificationBuilder<>("deleted", "=", false);
		return cartRepository.findAll(spec1.and(spec2));
	}

	public void save(Cart cart) {
		cartRepository.saveAndFlush(cart);
	}

}