package com.nmims.helpers;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

@Component
public class OtpHelper {
	
	
	public String generatingOtp() {
		return Integer.toString(ThreadLocalRandom.current().nextInt(1000, 9999 + 1));
	
	}
	
	
}
