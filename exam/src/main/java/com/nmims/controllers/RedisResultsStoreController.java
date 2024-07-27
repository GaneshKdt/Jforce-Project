package com.nmims.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nmims.beans.FlagBean;
import com.nmims.beans.StudentsDataInRedisBean;
import com.nmims.services.RedisResultsStoreService;

@Controller
@RequestMapping("/admin")
public class RedisResultsStoreController extends BaseController {

	@Autowired
	RedisResultsStoreService service;
	
	@RequestMapping(value = "/updateRedisFlags", method = {RequestMethod.GET}, produces = "application/json; charset=UTF-8")
	public ResponseEntity<List<FlagBean>> updateRedisFlags(HttpServletRequest request) throws IOException {
		
		String showResultsFromCache = request.getParameter("showResultsFromCache");
		String movingResultsToCache = request.getParameter("movingResultsToCache");

    	service.setFlagInRedis("showResultsFromCache", showResultsFromCache);
		service.setFlagInRedis("movingResultsToCache", movingResultsToCache);
		return ResponseEntity.ok(service.getAllFalgs());
	}

	@RequestMapping(value = "/getRedisFlags", method = {RequestMethod.GET}, produces = "application/json; charset=UTF-8")
	public ResponseEntity<List<FlagBean>> getRedisFlags(HttpServletRequest request) throws IOException {
		return ResponseEntity.ok(service.getAllFalgs());
	}

	@RequestMapping(value = "/deleteAllPreviousCachedResultsData", method = {RequestMethod.GET}, produces = "application/json; charset=UTF-8")
	public ResponseEntity<List<FlagBean>> deleteAllPreviousCachedResultsData(HttpServletRequest request) throws Exception {
		service.deleteAllResultsDataFromRedis();
		return ResponseEntity.ok(service.getAllFalgs());
	}

	@RequestMapping(value = "/getRedisResultDataBySapids", method = {RequestMethod.GET}, produces = "application/json; charset=UTF-8")
	public ResponseEntity<StudentsDataInRedisBean> getRedisResultDataBySapids(HttpServletRequest request) throws Exception {
		return ResponseEntity.ok(service.getRedisResultDataBySapid(request.getParameter("sapid")));
	}
	
	@RequestMapping(value = "/upsertDataToRedisCache", method = {RequestMethod.GET}, produces = "application/json; charset=UTF-8")
	public ResponseEntity<List<String>> upsertDataToRedisCache(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(service.fetchAndStoreResultsInRedis("2022", "Jun"));
	}
	
	@RequestMapping(value = "/upsertDataToRedisCacheForSingleStudent", method = {RequestMethod.GET}, produces = "application/json; charset=UTF-8")
	public ResponseEntity<List<String>> upsertDataToRedisCacheForSingleStudent(HttpServletRequest request) throws Exception {

		String sapid = request.getParameter("sapid");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
        return ResponseEntity.ok(service.fetchAndStoreResultsInRedisForSingleStudent(year, month, sapid));
	}

}
