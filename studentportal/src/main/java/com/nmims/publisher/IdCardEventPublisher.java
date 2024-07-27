package com.nmims.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.nmims.beans.StudentStudentPortalBean;

@Component
public class IdCardEventPublisher {
	
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;
	
	public void updateIdCardEvent(StudentStudentPortalBean student) {
		applicationEventPublisher.publishEvent(student);
	}
}
