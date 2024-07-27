package com.nmims.interfaces;

import com.nmims.factory.ContentFactory.StudentType;

public interface ContentFactoryInterface {
	
	 public abstract ContentInterface getStudentType(StudentType type);
	
}
