package com.nmims.repository;

import org.springframework.data.repository.CrudRepository;

import com.nmims.entity.LoginSSO;

public interface SSOLoaderRepositoryForRedis extends CrudRepository<LoginSSO,String>{
	
	LoginSSO findOne(String userId);
}
