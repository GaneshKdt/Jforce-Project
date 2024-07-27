package com.nmims.paymentgateways.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.paymentgateways.bean.PaymentOptionsBean;
import com.nmims.paymentgateways.dao.PaymentOptionsDAO;

@Controller
public class PaymentOptionsController{
	
	@Autowired
	private PaymentOptionsDAO paymentOptionsDAO;
	
	@RequestMapping(value="/student/getPaymentOptions",method= {RequestMethod.GET})
	public @ResponseBody ArrayList<PaymentOptionsBean> getActivePaymentOption() {
		// TODO Auto-generated method stub
		ArrayList<PaymentOptionsBean> paymentOptionsList = paymentOptionsDAO.getActivePaymentGateway();
		return paymentOptionsList;
	}
	
	@RequestMapping(value="/testing",method= {RequestMethod.GET})
	public ModelAndView testing() {
		ModelAndView mv = new ModelAndView("testing");
		return mv;
	}
	

}
