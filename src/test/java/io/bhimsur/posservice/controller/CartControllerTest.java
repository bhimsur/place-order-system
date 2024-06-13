package io.bhimsur.posservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bhimsur.posservice.dto.AddToCartRequest;
import io.bhimsur.posservice.entity.Cart;
import io.bhimsur.posservice.entity.Customer;
import io.bhimsur.posservice.entity.Product;
import io.bhimsur.posservice.entity.ProductType;
import io.bhimsur.posservice.repository.CartRepository;
import io.bhimsur.posservice.repository.CustomerRepository;
import io.bhimsur.posservice.repository.ProductRepository;
import io.bhimsur.posservice.repository.ProductTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductTypeRepository productTypeRepository;
	private Product product;
	private Customer customer;
	private Cart cart;

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
		return productRepository.saveAndFlush(product);
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
		this.product = createProduct("Spring Boot", BigDecimal.valueOf(14000), 2, productType);
		productType = createProductType("Electronic");
		this.product = createProduct("AC", BigDecimal.valueOf(3400000), 5, productType);
		this.customer = createCustomer("John Doe", "john1doe");
		createCustomer("Jane Doe", "jane1doe");
	}

	@AfterEach
	void tearDown() {
		cartRepository.deleteAll();
		productRepository.deleteAll();
		productTypeRepository.deleteAll();
		customerRepository.deleteAll();
	}

	@Test
	@Order(value = 11)
	void findByUsername_expectEmpty() throws Exception {
		this.mockMvc.perform(get("/api/carts")
						.param("username", this.customer.getUsername())
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.items").isEmpty())
				.andExpect(jsonPath("$.data.total").value(BigDecimal.ZERO))
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@Order(value = 12)
	void findByUsername_expectInvalidRequest() throws Exception {
		this.mockMvc.perform(get("/api/carts")
						.param("username", ""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").isNotEmpty())
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@Order(value = 13)
	void findByUsername_expectUsernameIsInvalid() throws Exception {
		this.mockMvc.perform(get("/api/carts")
						.param("username", "jjj11444"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").isNotEmpty())
				.andDo(MockMvcResultHandlers.print());
	}


	@Test
	@Order(value = 8)
	void addToCart_expectInvalidProductId() throws Exception {
		AddToCartRequest request = new AddToCartRequest();
		request.setProductId(0L);
		request.setUsername(this.customer.getUsername());
		request.setQuantity(1);
		this.mockMvc.perform(post("/api/carts")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(request))
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").isArray())
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@Order(value = 9)
	void addToCart_expectSuccess() throws Exception {
		AddToCartRequest request = new AddToCartRequest();
		request.setProductId(this.product.getId());
		request.setUsername(this.customer.getUsername());
		request.setQuantity(1);
		this.mockMvc.perform(post("/api/carts")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(request))
				)
				.andExpect(status().isCreated())
				.andDo(MockMvcResultHandlers.print());
		List<Cart> carts = cartRepository.findAll();
		assertFalse(carts.isEmpty());

		//updating quantity
		request.setQuantity(2);
		this.mockMvc.perform(post("/api/carts")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(request))
				)
				.andExpect(status().isCreated())
				.andDo(MockMvcResultHandlers.print());
		List<Cart> carts1 = cartRepository.findAll();
		Cart cart1 = carts1.get(0);
		assertEquals(request.getQuantity(), cart1.getQuantity());
	}

	@Test
	@Order(value = 10)
	void addToCart_expectInvalidStock() throws Exception {
		AddToCartRequest request = new AddToCartRequest();
		request.setProductId(this.product.getId());
		request.setUsername(this.customer.getUsername());
		request.setQuantity(10);
		this.mockMvc.perform(post("/api/carts")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(request))
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").value("Invalid Stock Quantity"))
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@Order(value = 19)
	void findByUsername_expectSuccess() throws Exception {
		addToCart_expectSuccess();
		this.mockMvc.perform(get("/api/carts")
						.param("username", this.customer.getUsername())
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.items").isNotEmpty())
				.andDo(MockMvcResultHandlers.print());

	}
}