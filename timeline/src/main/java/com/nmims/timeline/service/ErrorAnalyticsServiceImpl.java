package com.nmims.timeline.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nmims.timeline.model.ErrorAnalytics;
import com.nmims.timeline.repository.ErrorAnalyticsRepository;

@Service
public class ErrorAnalyticsServiceImpl implements ErrorAnalyticsService {
	
	private ErrorAnalyticsRepository errorAnalyticsRepository;
	
	public ErrorAnalyticsServiceImpl(ErrorAnalyticsRepository errorAnalyticsRepository) {
		this.errorAnalyticsRepository = errorAnalyticsRepository;
	}
	
	@Override
	public String save(String module, String sapid, Exception e, String apiCalled, String data) {
		// TODO Auto-generated method stub
		String stackTrace=  "apiCalled="+ apiCalled + ",data= "+data; 
		
		try {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			stackTrace = stackTrace+ ",errors=" + errors.toString();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		ErrorAnalytics bean = new ErrorAnalytics();
		bean.setSapid(sapid);
		bean.setModule(module);
		bean.setStackTrace(stackTrace);
		bean.setFixed(0);
		bean.setIpAddress("NA");
		bean.setUserAgent("NA");
		bean.setCreatedBy(sapid);
		bean.setUpdatedBy(sapid);
		
		try {
			errorAnalyticsRepository.save(bean);
			return "Success";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "Error in saving errorAnalytics, Error : "+e.toString();
		}
		
	}

	@Override
	public List<ErrorAnalytics> findAllByModule(String module) {
		List<ErrorAnalytics> errors = new ArrayList<>();
		try {
			errors = errorAnalyticsRepository.findAllByModule(module);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return errors;
	}

}
