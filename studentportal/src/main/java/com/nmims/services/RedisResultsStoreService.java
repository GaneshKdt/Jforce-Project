/**
 * 
 */
package com.nmims.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.FlagBean;
import com.nmims.beans.StudentsDataInRedisBean;
import com.nmims.repository.FlagsRepositoryForRedisImpl;
import com.nmims.repository.ResultsRepositoryForRedisImpl;

/**
 * @author vil_m
 *
 */
@Service("redisResultsStoreService")
public class RedisResultsStoreService {
	
	@Autowired
	private ResultsRepositoryForRedisImpl repository;

	@Autowired
	private FlagsRepositoryForRedisImpl flagRepo;
	
	private static final Logger logger = Logger.getLogger("resultsStore");
	
	public List<FlagBean> getAllFalgs() {
		//return flagRepo.getAll();
		
		List<FlagBean> bean = null;
		try {
			bean = flagRepo.getAll();
			return bean;
		} catch (Exception ex) {
			//ex.printStackTrace();
			logger.error("RedisResultsStoreService : getAllFalgs : error : " + ex.getMessage());
			throw ex;
		} finally {
			logger.info("RedisResultsStoreService : getAllFalgs : bean : " + bean);
		}
		
		//return bean;
	}
	
	public StudentsDataInRedisBean getRedisResultDataBySapid(String sapid) {
		//return repository.findBySapid(sapid);
		
		StudentsDataInRedisBean bean = null;
		try {
			bean = repository.findBySapid(sapid);
		} catch (Exception ex) {
			//ex.printStackTrace();
			logger.error("RedisResultsStoreService : getRedisResultDataBySapid : error : " + ex.getMessage());
		} finally {
			logger.info("RedisResultsStoreService : getRedisResultDataBySapid : bean : " + bean);
		}
		return bean;
	}
}
