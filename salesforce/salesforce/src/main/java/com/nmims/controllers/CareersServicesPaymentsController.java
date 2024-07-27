package com.nmims.controllers;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.nmims.helpers.PaymentStatusCheckHelper;

@Controller
public class CareersServicesPaymentsController {

	@Autowired
	PaymentStatusCheckHelper  paymentStatusCheckHelper;

	@RequestMapping(value = "/getPaymentStatus", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> getPaymentStatus(Locale locale, Model model, @RequestBody List<String> allInputs) {
		Gson gson = new Gson();
		
		return ResponseEntity.ok(gson.toJson(paymentStatusCheckHelper.getStatusForPaymentInitiaionIds(allInputs)));
	}
}
