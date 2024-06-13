package io.bhimsur.posservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bhimsur.posservice.entity.ProductType;
import io.bhimsur.posservice.repository.ProductTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ProductTypeControllerTest {


	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ProductTypeRepository productTypeRepository;

	@BeforeEach
	void setUp() {
		ProductType productType = new ProductType();
		productType.setDeleted(false);
		productType.setCreatedAt(LocalDateTime.now());
		productType.setCreatedBy("system");
		productType.setName("Book");
		productType.setDescription("Book");
		productTypeRepository.saveAndFlush(productType);
	}

	@Test
	@Order(value = 0)
	void findAll() throws Exception {
		this.mockMvc.perform(get("/api/product-types")
						.param("deleted", "false")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content").isNotEmpty())
				.andDo(MockMvcResultHandlers.print());
	}
}