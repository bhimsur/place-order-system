package io.bhimsur.posservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bhimsur.posservice.dto.SaveProductRequest;
import io.bhimsur.posservice.entity.Product;
import io.bhimsur.posservice.entity.ProductType;
import io.bhimsur.posservice.repository.ProductRepository;
import io.bhimsur.posservice.repository.ProductTypeRepository;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

	@Autowired
	private ProductTypeRepository productTypeRepository;
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	private ProductType productType;
	private Product product;

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

	@BeforeEach
	void setUp() {
		this.productType = createProductType("Book");
		this.product = createProduct("Spring Boot", BigDecimal.valueOf(14000), 2, this.productType);
		this.productType = createProductType("Electronic");
		this.product = createProduct("AC", BigDecimal.valueOf(3400000), 5, this.productType);
	}

	@AfterEach
	void tearDown() {
		productRepository.deleteAll();
		productTypeRepository.deleteAll();
	}

	@Test
	@Order(value = 1)
	void findAll_expectSuccess() throws Exception {
		this.mockMvc.perform(get("/api/products")
						.param("deleted", "false")
						.param("sort", "id,desc"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content").isNotEmpty())
				.andExpect(jsonPath("$.data.sort.sorted").value(true))
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@Order(value = 2)
	void findAll_expectEmptyResult() throws Exception {
		this.mockMvc.perform(get("/api/products")
						.param("deleted", "true")
						.param("type", "Book"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content").isEmpty())
				.andExpect(jsonPath("$.data.empty").value(true))
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@Order(value = 3)
	void save_expectPriceIsInvalid() throws Exception {
		SaveProductRequest request = new SaveProductRequest();
		request.setName("The Harvest");
		request.setTypeId(this.productType.getId());
		request.setStock(2);
		request.setPrice(BigDecimal.valueOf(-10));
		this.mockMvc.perform(post("/api/products")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(request))
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").isNotEmpty())
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@Order(value = 4)
	void save_expectSuccess() throws Exception {
		SaveProductRequest request = new SaveProductRequest();
		request.setName("The Harvest");
		request.setTypeId(this.productType.getId());
		request.setStock(2);
		request.setPrice(BigDecimal.valueOf(100000));
		this.mockMvc.perform(post("/api/products")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(request))
				)
				.andExpect(status().isCreated())
				.andDo(MockMvcResultHandlers.print());

		SpecificationBuilder<Product> spec1 = new SpecificationBuilder<>("name", "=", request.getName());
		Optional<Product> one = productRepository.findOne(spec1);
		assertTrue(one.isPresent());
		assertEquals(one.get().getPrice(), request.getPrice());
	}

	@Test
	@Order(value = 5)
	void update_expectIdIsNotPositive() throws Exception {
		SaveProductRequest request = new SaveProductRequest();
		request.setName("The Harvest");
		request.setTypeId(this.productType.getId());
		request.setStock(2);
		request.setPrice(BigDecimal.valueOf(100000));
		this.mockMvc.perform(put("/api/products/-1")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").isNotEmpty())
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@Order(value = 6)
	void update_expectSuccess() throws Exception {
		SaveProductRequest request = new SaveProductRequest();
		request.setName("The Harvest");
		request.setTypeId(this.productType.getId());
		request.setStock(2);
		request.setPrice(BigDecimal.valueOf(100000));
		this.mockMvc.perform(put("/api/products/" + this.product.getId())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print());

		Product product = productRepository.findById(this.product.getId()).orElse(new Product());
		assertEquals(request.getPrice(), product.getPrice());
		assertNotEquals(request.getPrice(), this.product.getPrice());
	}

	@Test
	@Order(value = 7)
	void delete_expectSuccess() throws Exception {
		this.mockMvc.perform(delete("/api/products/" + this.product.getId()))
				.andExpect(status().isNoContent())
				.andDo(MockMvcResultHandlers.print());

		Product product = productRepository.findById(this.product.getId()).orElse(new Product());
		assertTrue(product.getDeleted());
	}

	@Test
	@Order(value = 20)
	void saveProduct_expectInvalidProductType() throws Exception {
		SaveProductRequest request = new SaveProductRequest();
		request.setName("The Harvest");
		request.setTypeId(100L);
		request.setStock(2);
		request.setPrice(BigDecimal.valueOf(100000));
		this.mockMvc.perform(post("/api/products")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(request))
				)
				.andExpect(status().isBadRequest())
				.andDo(MockMvcResultHandlers.print());
	}
}