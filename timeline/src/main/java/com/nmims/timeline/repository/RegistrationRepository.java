package com.nmims.timeline.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.Registration;
import com.nmims.timeline.model.Student;

@Repository
public interface RegistrationRepository extends CrudRepository<Registration, String>   {
	
	Registration findFirstBySapidOrderBySemDesc(String sapid);
}
