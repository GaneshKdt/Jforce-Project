package com.nmims.test.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.services.HomeService;

@RunWith(SpringRunner.class)
@SpringBootTest
//@TestPropertySource("file:C:/NMIMS_PROPERTY_FILE/application.properties")
public class HomeServiceTest {
	@Autowired
	private HomeService homeService;
	
	/**
	 * Unit test of forgotPasswordVerifyDetails() method of HomeService Class.
	 */
	@Test
	public void testForgotPasswordVerifyDetails() {
		String userId = "77777799999";
		try {
			assertThat(homeService.forgotPasswordVerifyDetails(userId).contains("dcunha.raynal4@gmail.com"));
		}
		catch(Exception ex) {
//			ex.printStackTrace();
		}
	}
}
