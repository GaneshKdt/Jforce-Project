/**
 * 
 */
package com.nmims.services;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nmims.beans.RedisPassFailBean;
import com.nmims.beans.RedisStudentMarksBean;

import com.nmims.helpers.DateHelper;

/**
 * @author vil_m 
 *
 */
@Service("redisResultsOrderService")
public class RedisResultsOrderService {
	
	private static final Logger logger = LoggerFactory.getLogger("resultsStore");
	
	public List<RedisStudentMarksBean> orderMarksHistoryList(List<RedisStudentMarksBean> list) {
		List<RedisStudentMarksBean> orderedList = null;
		orderedList = this.orderSemForMarksHistory(list);
		return orderedList;
	}
	
	protected List<RedisStudentMarksBean> orderSemForMarksHistory(List<RedisStudentMarksBean> listBean) {
		Set<String> setSemester;
		List<RedisStudentMarksBean> tempList = null;
		List<RedisStudentMarksBean> listOrdered = null;
		List<RedisStudentMarksBean> listOrderedSemWise = null;
		Map<String, List<RedisStudentMarksBean>> mapSemesterMarksHistory;
		
		if(null != listBean && !listBean.isEmpty()) {
			//Collect distinct semester in TreeSet ordered in ascending order
			setSemester = listBean.stream().map(a -> a.getSem()).collect(Collectors.toCollection(TreeSet::new));
			logger.info("orderSemForMarksHistory : Distinct Semester(s): "+ setSemester.size());
			
			if(null != setSemester && !setSemester.isEmpty()) {
				//Create Map of each Semester and its list of beans
				mapSemesterMarksHistory = listBean.stream().collect(Collectors.groupingBy(b -> b.getSem()));
				
				listOrderedSemWise = new LinkedList<RedisStudentMarksBean>();
				
				for(String sem : setSemester) {
					//lowest semester picked up and added in LinkedList
					tempList = mapSemesterMarksHistory.get(sem);
					listOrdered = this.orderExamYearInSemForMarksHistory(tempList);
					listOrderedSemWise.addAll(listOrdered);
				}
				if (null != mapSemesterMarksHistory) {
					mapSemesterMarksHistory.clear();
				}
				setSemester.clear();
			}
		} else {
			listOrderedSemWise = listBean;
		}
		return listOrderedSemWise;
	}
	
	protected List<RedisStudentMarksBean> orderExamYearInSemForMarksHistory(List<RedisStudentMarksBean> listBean1) {
		Set<String> setExamYear;
		List<RedisStudentMarksBean> tempList1 = null;
		List<RedisStudentMarksBean> listOrdered1 = null;
		List<RedisStudentMarksBean> listOrderedExamYearWise = null;
		Map<String, List<RedisStudentMarksBean>> mapExamYearMarksHistory = null;
		
		//Distinct ExamYear
		setExamYear = listBean1.stream().map(c -> c.getYear()).collect(Collectors.toCollection(TreeSet::new));
		//logger.info("orderExamYearInSemForMarksHistory : Distinct ExamYear(s): "+ setExamYear.size());
		if(null != setExamYear && !setExamYear.isEmpty()) {
			//Create Map of each ExamYear and its list of beans
			mapExamYearMarksHistory = listBean1.stream().collect(Collectors.groupingBy(d -> d.getYear()));
			
			listOrderedExamYearWise = new LinkedList<RedisStudentMarksBean>();
			for(String examYear : setExamYear) {
				listOrdered1 = mapExamYearMarksHistory.get(examYear);
				tempList1 = this.orderExamMonthInSemForMarksHistory(listOrdered1);
				listOrderedExamYearWise.addAll(tempList1);
			}
			
			if(null != mapExamYearMarksHistory) {
				mapExamYearMarksHistory.clear();
			}
			setExamYear.clear();
		}
		return listOrderedExamYearWise;
	}
	
	protected List<RedisStudentMarksBean> orderExamMonthInSemForMarksHistory(List<RedisStudentMarksBean> listBean1) {
		Set<Integer> setExamMonth;
		List<RedisStudentMarksBean> tempList1 = null;
		List<RedisStudentMarksBean> listOrdered1 = null;
		List<RedisStudentMarksBean> listOrderedExamMonthWise = null;
		Map<Integer, List<RedisStudentMarksBean>> mapExamMonthMarksHistory = null;
		
		//Distinct ExamMonth
		setExamMonth = listBean1.stream().map(e -> (DateHelper.monthNameOnly3CharacterMap.get(e.getMonth().toLowerCase()))).collect(Collectors.toCollection(TreeSet::new));
		//logger.info("orderExamMonthInSemForMarksHistory : Distinct ExamMonth(s): "+ setExamMonth.size());
		if(null != setExamMonth && !setExamMonth.isEmpty()) {
			//Create Map of each ExamMonth and its list of beans
			mapExamMonthMarksHistory = listBean1.stream().collect(Collectors.groupingBy(f -> (DateHelper.monthNameOnly3CharacterMap.get(f.getMonth().toLowerCase())) ));
			
			listOrderedExamMonthWise = new LinkedList<RedisStudentMarksBean>();
			for(Integer examMonth : setExamMonth) {
				listOrdered1 = mapExamMonthMarksHistory.get(examMonth);
				tempList1 = this.orderSubjectInSemForMarksHistory(listOrdered1);
				listOrderedExamMonthWise.addAll(tempList1);
			}
			
			if(null != mapExamMonthMarksHistory) {
				mapExamMonthMarksHistory.clear();
			}
			setExamMonth.clear();
		}
		return listOrderedExamMonthWise;
	}
	
	protected List<RedisStudentMarksBean> orderSubjectInSemForMarksHistory(List<RedisStudentMarksBean> listBean2) {
		Set<String> setSubject;
		List<RedisStudentMarksBean> listOrdered2 = null;
		List<RedisStudentMarksBean> listOrderedSubjectWise = null;
		Map<String, List<RedisStudentMarksBean>> mapSubjectMarksHistory = null;
		
		//Distinct Subjects
		setSubject = listBean2.stream().map(g -> g.getSubject()).collect(Collectors.toCollection(TreeSet::new));
		//logger.info("orderSubjectInSemForMarksHistory : Distinct Subject(s): "+ setSubject.size());
		if(null != setSubject && !setSubject.isEmpty()) {
			//Create Map of each Subject and its list of beans
			mapSubjectMarksHistory = listBean2.stream().collect(Collectors.groupingBy(h -> h.getSubject()));
			
			listOrderedSubjectWise = new LinkedList<RedisStudentMarksBean>();
			for(String subject : setSubject) {
				listOrdered2 = mapSubjectMarksHistory.get(subject);
				listOrderedSubjectWise.addAll(listOrdered2);
			}
			
			if(null != mapSubjectMarksHistory) {
				mapSubjectMarksHistory.clear();
			}
			setSubject.clear();
		}
		return listOrderedSubjectWise;
	}
	
	public List<RedisStudentMarksBean> orderMarksList(List<RedisStudentMarksBean> list) {
		List<RedisStudentMarksBean> orderedList = null;
		orderedList = this.orderSemForMarks(list);
		return orderedList;
	}
	
	protected List<RedisStudentMarksBean> orderSemForMarks(List<RedisStudentMarksBean> list) {
		Set<String> setSemester;
		List<RedisStudentMarksBean> tempList = null;
		List<RedisStudentMarksBean> listOrdered = null;
		List<RedisStudentMarksBean> listOrderedSemWise = null;
		Map<String, List<RedisStudentMarksBean>> mapSemesterMarks;
		
		if(null != list && !list.isEmpty()) {
			//Collect distinct semester in TreeSet ordered in ascending order
			setSemester = list.stream().map(n -> n.getSem()).collect(Collectors.toCollection(TreeSet::new));
			logger.info("orderSemForMarks : Distinct Semester(s): "+ setSemester.size());
			
			if(null != setSemester && !setSemester.isEmpty()) {
				//Create Map of each Semester and its list of beans
				mapSemesterMarks = list.stream().collect(Collectors.groupingBy(o -> o.getSem()));
				
				listOrderedSemWise = new LinkedList<RedisStudentMarksBean>();
				
				for(String sem : setSemester) {
					//lowest semester picked up and added in LinkedList
					tempList = mapSemesterMarks.get(sem);
					listOrdered = this.orderSubjectInSemForMarks(tempList);
					listOrderedSemWise.addAll(listOrdered);
				}
				if (null != mapSemesterMarks) {
					mapSemesterMarks.clear();
				}
				setSemester.clear();
			}
		} else {
			listOrderedSemWise = list;
		}
		return listOrderedSemWise;
	}
	
	protected List<RedisStudentMarksBean> orderSubjectInSemForMarks(List<RedisStudentMarksBean> list2) {
		Set<String> setSubject;
		List<RedisStudentMarksBean> listOrdered2 = null;
		List<RedisStudentMarksBean> listOrderedSubjectWay = null;
		Map<String, List<RedisStudentMarksBean>> mapSubjectMarks = null;
		
		//Distinct Subjects
		setSubject = list2.stream().map(p -> p.getSubject()).collect(Collectors.toCollection(TreeSet::new));
		//logger.info("orderSubjectInSemForMarks : Distinct Subject(s): "+ setSubject.size());
		if(null != setSubject && !setSubject.isEmpty()) {
			//Create Map of each Subject and its list of beans
			mapSubjectMarks = list2.stream().collect(Collectors.groupingBy(q -> q.getSubject()));
			
			listOrderedSubjectWay = new LinkedList<RedisStudentMarksBean>();
			for(String subject : setSubject) {
				listOrdered2 = mapSubjectMarks.get(subject);
				listOrderedSubjectWay.addAll(listOrdered2);
			}
			
			if(null != mapSubjectMarks) {
				mapSubjectMarks.clear();
			}
			setSubject.clear();
		}
		return listOrderedSubjectWay;
	}
	
	public List<RedisPassFailBean> orderPassFailList(List<RedisPassFailBean> list) {
		List<RedisPassFailBean> orderedList = null;
		orderedList = this.orderSemForPassfail(list);
		return orderedList;
	}
	
	protected List<RedisPassFailBean> orderSemForPassfail(List<RedisPassFailBean> listOfBean) {
		Set<String> setSemester;
		List<RedisPassFailBean> tempList;
		List<RedisPassFailBean> listOrdered = null;
		List<RedisPassFailBean> listOrderedSemWise = null;
		Map<String, List<RedisPassFailBean>> mapSemesterPassFail;
		
		if(null != listOfBean && !listOfBean.isEmpty()) {
			//Collect distinct semester in TreeSet ordered in ascending order
			setSemester = listOfBean.stream().map(u -> u.getSem()).collect(Collectors.toCollection(TreeSet::new));
			logger.info("orderSemForPassfail : Distinct Semester(s): "+ setSemester.size());
			
			if(null != setSemester && !setSemester.isEmpty()) {
				//Create Map of each Semester and its list of beans
				mapSemesterPassFail = listOfBean.stream().collect(Collectors.groupingBy(v -> v.getSem()));
				
				listOrderedSemWise = new LinkedList<RedisPassFailBean>();
				
				for(String sem : setSemester) {
					//lowest semester picked up and added in LinkedList
					tempList = mapSemesterPassFail.get(sem);
					listOrdered = this.orderSubjectInSemForPassfail(tempList);
					//listOrderedSemWise.addAll(tempList);
					listOrderedSemWise.addAll(listOrdered);
				}
				if(null != mapSemesterPassFail) {
					mapSemesterPassFail.clear();
				}
				setSemester.clear();
			}
		} else {
			listOrderedSemWise = listOfBean;
		}
		return listOrderedSemWise;
	}
	
	protected List<RedisPassFailBean> orderSubjectInSemForPassfail(List<RedisPassFailBean> listOfBean2) {
		Set<String> setSubjects;
		List<RedisPassFailBean> listOrdered2 = null;
		List<RedisPassFailBean> listOrderedSubjectWise = null;
		Map<String, List<RedisPassFailBean>> mapSubjectPassFail = null;
		
		//Distinct Subjects
		setSubjects = listOfBean2.stream().map(w -> w.getSubject()).collect(Collectors.toCollection(TreeSet::new));
		//logger.info("orderSubjectInSemForPassfail : Distinct Subjects(s): "+ setSubjects.size());
		if(null != setSubjects && !setSubjects.isEmpty()) {
			//Create Map of each Subject and its list of beans
			mapSubjectPassFail = listOfBean2.stream().collect(Collectors.groupingBy(x -> x.getSubject()));
			
			listOrderedSubjectWise = new LinkedList<RedisPassFailBean>();
			for(String subject : setSubjects) {
				listOrdered2 = mapSubjectPassFail.get(subject);
				listOrderedSubjectWise.addAll(listOrdered2);
			}
			
			if (null != mapSubjectPassFail) {
				mapSubjectPassFail.clear();
			}
			setSubjects.clear();
		}
		return listOrderedSubjectWise;
	}

}
