package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.Faculty;

@Repository
public interface FacultyRepository extends CrudRepository<Faculty, Integer>   {

	List<Faculty> findAll();
	
	Faculty findByFacultyId(String facultyId);
	
}
