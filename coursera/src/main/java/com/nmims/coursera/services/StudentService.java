package com.nmims.coursera.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.coursera.beans.StudentCourseraBean;
import com.nmims.coursera.dao.CourseraUsersDao;

@Service("studentService")
public class StudentService {
	
	@Autowired
	CourseraUsersDao courseraUsersDao; 

	@Transactional
	public StudentCourseraBean getSingleStudentsData(String sapid) {
		return courseraUsersDao.getSingleStudentsData(sapid);
	}
}
