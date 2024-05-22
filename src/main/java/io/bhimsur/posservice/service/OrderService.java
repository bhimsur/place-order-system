package io.bhimsur.posservice.service;

import io.bhimsur.posservice.constant.OrderStatus;
import io.bhimsur.posservice.dto.OrderDto;
import io.bhimsur.posservice.dto.PlaceOrderRequest;
import io.bhimsur.posservice.dto.PlaceOrderResponse;
import io.bhimsur.posservice.dto.ProductOrderTransactionDto;
import io.bhimsur.posservice.entity.*;
import io.bhimsur.posservice.repository.OrderTransactionRepository;
import io.bhimsur.posservice.util.SpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class OrderService {

	private final OrderTransactionRepository orderRepository;
	private final CartService cartService;
	private final ProductService productService;
	private final ProductOrderTransactionService productOrderTransactionService;

	public PlaceOrderResponse placeOrder(PlaceOrderRequest request) throws Exception {
		log.info("start placeOrder, request: {}", request);
		try {
			List<Cart> myCart = cartService.findCartByIdIn(request.getCartIds());
			if (myCart.size() != request.getCartIds().size()) {
				throw new Exception("Found Invalid Id");
			}

			Optional<String> invalidUser = myCart.stream()
					.map(Cart::getCustomer)
					.map(Customer::getUsername)
					.filter(username -> !username.equalsIgnoreCase(request.getUsername()))
					.findFirst();

			if (invalidUser.isPresent()) throw new Exception("Found Invalid User");

			OrderTransaction orderTransaction = new OrderTransaction();
			orderTransaction.setDeleted(false);
			orderTransaction.setCreatedAt(LocalDateTime.now());
			orderTransaction.setCreatedBy(request.getUsername());
			orderTransaction.setStatus(OrderStatus.PLACED);

			Set<ProductOrderTransaction> products = new LinkedHashSet<>();

			myCart.stream().forEach(cart -> {
				if (null == orderTransaction.getCustomer()) {
					orderTransaction.setCustomer(cart.getCustomer());
				}
				Product product;
				try {
					product = productService.checkAndSetStock(cart.getProduct(), cart.getQuantity());
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
				cart.setProduct(product);
				cart.setDeleted(true);
				cart.setUpdatedBy("system");
				cart.setUpdatedAt(LocalDateTime.now());
				cartService.save(cart);

				ProductOrderTransaction pot = productOrderTransactionService.mapper(product, cart.getQuantity(), orderTransaction);
				products.add(pot);
			});

			orderTransaction.setReferenceNumber(UUID.randomUUID().toString());
			orderTransaction.setProductOrderTransactions(products);

			orderRepository.saveAndFlush(orderTransaction);
			return new PlaceOrderResponse(orderTransaction.getReferenceNumber());
		} catch (Exception e) {
			log.error("error placeOrder: {}", e.getMessage(), e);
			throw e;
		}
	}

	@Transactional(readOnly = true)
	public Page<OrderDto> myOrder(String username, Pageable pageable) {
		log.info("start myOrder: {}, {}", username, pageable);
		try {
			SpecificationBuilder<OrderTransaction> spec1 = new SpecificationBuilder<>("customer.username", "=", username);

			return orderRepository.findAll(spec1, pageable).map(this::mapper);
		} catch (Exception e) {
			log.error("error myOrder: {}", e.getMessage(), e);
			throw e;
		}
	}

	private OrderDto mapper(OrderTransaction order) {
		Set<ProductOrderTransaction> products = order.getProductOrderTransactions();
		Customer customer = order.getCustomer();

		OrderDto dto = new OrderDto();
		dto.setRefNum(order.getReferenceNumber());
		dto.setCustomerName(customer.getName());
		dto.setCustomerAddress(customer.getAddress());

		BigDecimal totalPrice = products.stream()
				.map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		dto.setTotalPrice(totalPrice);
		List<ProductOrderTransactionDto> items = products.stream()
				.map(p -> new ProductOrderTransactionDto(p.getName(), p.getPrice(), p.getQuantity(), p.getType()))
				.collect(Collectors.toList());
		dto.setItems(items);
		return dto;
	}
}