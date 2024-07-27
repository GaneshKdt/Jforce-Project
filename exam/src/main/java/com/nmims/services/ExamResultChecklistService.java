package com.nmims.services;

import java.util.List;
import java.util.Map;

import com.nmims.beans.ExamResultChecklistBean;

/**
 * Results checklist interface to process TEE results dashboard data as of Mar
 * 2023
 * 
 * @author swarup.rajpurohit.EX
 */
public interface ExamResultChecklistService {

	/**
	 * A separated method that internally uses count project and subject methods and
	 * returns it to the controller in the form of hashmap the map is implemented as
	 * linkedhashmap to maintain insertion order
	 * 
	 * @param liveExamMonth
	 * @param liveExamYear
	 * @return Map<String, Integer> of count as value and key as count
	 * 
	 */
	Map<String, Integer> getDashboardCountForExamResults(String liveExamMonth, String liveExamYear);

	/**
	 * Populates exam_result_checklist table, fetches all non overlapping base data
	 * and applies logic for the same
	 * 
	 * @param examYear
	 * @param examMonth
	 * @return Number of records found as base data
	 */
	Integer populateResultChecklist(String examYear, String examMonth,String userId);

	/**
	 * Inserts base data collected into the table exam_result_checklist
	 * 
	 * @param checklistRecords
	 * @return
	 */
	Integer insertExamChecklistRecords(List<ExamResultChecklistBean> checklistRecords, String examYear,String examMonth,String userId);
}
