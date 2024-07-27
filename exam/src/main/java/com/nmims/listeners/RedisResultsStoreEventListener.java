package com.nmims.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;
import com.nmims.controllers.MakeLiveController;
import com.nmims.events.MakeExamLive;
import com.nmims.services.RedisResultsStoreService;

/**
 * Stores Result data in Redis, previously existed in MakeLiveController,
 * Moved in March 2023
 */
@Component
public class RedisResultsStoreEventListener {

	private static final Logger logger = LoggerFactory.getLogger(MakeLiveController.class);

	@Autowired
	RedisResultsStoreService resultsStoreService;

	/**
	 * Synchronously saves Result data in Redis.
	 * 
	 * @param ExamLive Object that contains exam year and month for event listener
	 *                 to process and store data in redis accordinly
	 */
	@EventListener
	public void storeResultsInRedisUponMakingResultsLive(final MakeExamLive examLiveObject) {
		try {
			logger.info("----------------- Storing Results in Redis for exam Year : {} , Month : {} START -----------------",
					examLiveObject.getExamYear(), examLiveObject.getExamMonth());
			
			resultsStoreService.fetchAndStoreResultsInRedis(examLiveObject.getExamYear(),
					examLiveObject.getExamMonth());
			
		} catch (Exception e) {
			logger.info("Error while storing results in redis : {}", Throwables.getStackTraceAsString(e));
		}
		logger.info("----------------- Storing Results in Redis for exam Year : {} , Month : {} END -----------------",
				examLiveObject.getExamYear(), examLiveObject.getExamMonth());
	}
}
