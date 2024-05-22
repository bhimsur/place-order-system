package io.bhimsur.posservice.service;

import io.bhimsur.posservice.entity.OrderTransaction;
import io.bhimsur.posservice.entity.Product;
import io.bhimsur.posservice.entity.ProductOrderTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ProductOrderTransactionService {

	public ProductOrderTransaction mapper(Product product, Integer quantity, OrderTransaction orderTransaction) {
		ProductOrderTransaction entity = new ProductOrderTransaction();
		entity.setDeleted(false);
		entity.setCreatedAt(LocalDateTime.now());
		entity.setCreatedBy("system");
		entity.setQuantity(quantity);
		entity.setParentProduct(product);
		entity.setPrice(product.getPrice());
		entity.setName(product.getName());
		entity.setType(product.getType().getName());
		entity.setOrderTransaction(orderTransaction);
		return entity;
	}
}