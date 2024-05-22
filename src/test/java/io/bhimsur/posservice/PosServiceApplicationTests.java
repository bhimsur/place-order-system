package io.bhimsur.posservice;

import io.bhimsur.posservice.controller.CartControllerTest;
import io.bhimsur.posservice.controller.OrderControllerTest;
import io.bhimsur.posservice.controller.ProductControllerTest;
import io.bhimsur.posservice.controller.ProductTypeControllerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ProductTypeControllerTest.class, ProductControllerTest.class, CartControllerTest.class, OrderControllerTest.class})
class PosServiceApplicationTests {
}