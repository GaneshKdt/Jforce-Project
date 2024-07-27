package com.nmims.repository;

import org.springframework.data.repository.CrudRepository;
import com.nmims.entity.LoginSSO;

public interface SSOLoaderRepositoryForRedis extends CrudRepository<LoginSSO,String>{
	
	LoginSSO findOne(String id);
	LoginSSO save(LoginSSO student);
	void delete(LoginSSO student);
}
