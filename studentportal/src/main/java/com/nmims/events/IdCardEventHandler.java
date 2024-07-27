package com.nmims.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.interfaces.IdCardServiceInterface;

@Component
public class IdCardEventHandler {
	
	@Autowired
	IdCardServiceInterface idCardService;
	
	@EventListener
	public void handleEvent(StudentStudentPortalBean student) {
		idCardService.updateIdCard(student);
	}

}
