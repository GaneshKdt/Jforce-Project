package com.nmims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nmims.interfaces.ContentFactoryInterface;
import com.nmims.interfaces.ContentInterface;
import com.nmims.services.ContentLeadService;
import com.nmims.services.ContentMBAWXService;
import com.nmims.services.ContentService;

@Component
public class ContentFactory implements ContentFactoryInterface{
	public enum StudentType {
		MBAWX, PG, LEAD
	}
	
	@Autowired
	ContentService contentService;
	
	@Autowired
	ContentLeadService contentLeadService;
	
	@Autowired
	ContentMBAWXService contentMBAWXService;
	
	public ContentInterface getStudentType(StudentType type) {
		ContentInterface content = null;
		switch (type) {
		case MBAWX:
			content = contentMBAWXService;
			break;
		case PG:
			content = contentService;
			break;
		case LEAD:
			content = contentLeadService;
			break;
		}
		return content;
	};

}
