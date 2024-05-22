package io.bhimsur.posservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bhimsur.posservice.constant.OrderStatus;
import io.bhimsur.posservice.dto.PlaceOrderRequest;
import io.bhimsur.posservice.entity.*;
import io.bhimsur.posservice.repository.*;
import io.bhimsur.posservice.util.SpecificationBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private OrderTransactionRepository orderTransactionRepository;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductTypeRepository productTypeRepository;
	@Autowired
	private ProductOrderTransactionRepository productOrderTransactionRepository;
	private final List<Long> cartIds = new ArrayList<>();

	private ProductType createProductType(String name) {
		ProductType productType = new ProductType();
		productType.setCreatedAt(LocalDateTime.now());
		productType.setDeleted(false);
		productType.setCreatedBy("system");
		productType.setName(name);
		productType.setDescription(name);
		return productTypeRepository.saveAndFlush(productType);
	}

	private Product createProduct(String name, BigDecimal price, Integer stock, ProductType productType) {
		Product product = new Product();
		product.setType(productType);
		product.setDeleted(false);
		product.setCreatedAt(LocalDateTime.now());
		product.setName(name);
		product.setPrice(price);
		product.setStock(stock);
		product.setCreatedBy("system");
		return product;
	}

	private Customer createCustomer(String name, String username) {
		Customer customer = new Customer();
		customer.setDeleted(false);
		customer.setCreatedBy("system");
		customer.setCreatedAt(LocalDateTime.now());
		customer.setName(name);
		customer.setPassword("Password1!");
		customer.setUsername(username);
		customer.setAddress("Some Address");
		return customerRepository.saveAndFlush(customer);
	}

	@BeforeEach
	void setUp() {
		ProductType productType = createProductType("Book");
		Product product = createProduct("Spring Boot", BigDecimal.valueOf(14000), 2, productType);
		Customer customer = createCustomer("John Doe", "john1doe");
		Cart cart = createCart(customer, product, 1);
		this.cartIds.add(cart.getId());

		productType = createProductType("Electronic");
		product = createProduct("AC", BigDecimal.valueOf(3400000), 5, productType);
		cart = createCart(customer, product, 2);
		this.cartIds.add(cart.getId());

		createCustomer("Jane Doe", "jane1doe");
	}

	private Cart createCart(Customer customer, Product product, int quantity) {
		Cart cart = new Cart();
		cart.setProduct(product);
		cart.setCustomer(customer);
		cart.setDeleted(false);
		cart.setCreatedAt(LocalDateTime.now());
		cart.setCreatedBy("system");
		cart.setQuantity(quantity);
		return cartRepository.saveAndFlush(cart);
	}

	@AfterEach
	void tearDown() {
		productOrderTransactionRepository.deleteAll();
		orderTransactionRepository.deleteAll();
		cartRepository.deleteAll();
		productRepository.deleteAll();
		productTypeRepository.deleteAll();
		customerRepository.deleteAll();
	}

	@Test
	@Order(value = 16)
	void placeOrder_expectInvalidId() throws Exception {
		PlaceOrderRequest request = new PlaceOrderRequest(Arrays.asList(1L, 2L, 3L), "john1doe");
		this.mockMvc.perform(post("/api/orders")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(request))
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Found Invalid Id"))
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@Order(value = 17)
	void placeOrder_expectSuccess() throws Exception {
		PlaceOrderRequest request = new PlaceOrderRequest(this.cartIds, "john1doe");
		MvcResult mvcResult = this.mockMvc.perform(post("/api/orders")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(request))
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.refNum").isNotEmpty())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		byte[] responseContent = mvcResult.getResponse().getContentAsByteArray();

		JsonNode jsonNode = objectMapper.readTree(responseContent);
		String refNum = jsonNode.get("refNum").asText();

		SpecificationBuilder<OrderTransaction> spec1 = new SpecificationBuilder<>("referenceNumber", "=", refNum);
		Optional<OrderTransaction> result = orderTransactionRepository.findOne(spec1);
		assertTrue(result.isPresent());
		assertEquals(OrderStatus.PLACED, result.get().getStatus());
	}

	@Test
	@Order(value = 14)
	void myOrder_expectUsernameIsEmpty() throws Exception {
		this.mockMvc.perform(get("/api/orders")
						.param("username", "")
				)
				.andExpect(status().isBadRequest())
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@Order(value = 15)
	void myOrder_expectSuccessAndEmpty() throws Exception {
		this.mockMvc.perform(get("/api/orders")
						.param("username", "jane1doe")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isEmpty())
				.andExpect(jsonPath("$.empty").value(true))
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@Order(value = 18)
	void myOrder_expectSuccess() throws Exception {
		placeOrder_expectSuccess();
		this.mockMvc.perform(get("/api/orders")
						.param("username", "john1doe")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.totalElements").value(1))
				.andDo(MockMvcResultHandlers.print());
	}

}