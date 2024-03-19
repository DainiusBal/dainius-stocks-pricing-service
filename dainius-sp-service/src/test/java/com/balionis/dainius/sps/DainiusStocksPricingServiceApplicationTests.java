package com.balionis.dainius.sps;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DainiusStocksPricingServiceApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		// Verify that the ApplicationContext is not null
		assertNotNull(applicationContext, "ApplicationContext should not be null");

		// You can add more assertions to verify specific beans or conditions in the context
		// For example:
		// assertTrue(applicationContext.containsBean("myBean"), "myBean should be present in the context");
	}

}
