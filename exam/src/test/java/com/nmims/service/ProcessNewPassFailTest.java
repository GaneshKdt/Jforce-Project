package com.nmims.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.PassFailDAO;
import com.nmims.services.PassFailExecutorService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProcessNewPassFailTest {

	@Autowired
	PassFailDAO passFailDAO;

	@Autowired
	PassFailExecutorService passFailExecutorService;

	@Test
	public void processNewLogicComparisonTest() {
		
		StopWatch stopWatch = new StopWatch();
		StudentExamBean searchBean = new StudentExamBean();
		searchBean.setYear("2022");
		searchBean.setMonth("Dec");

		stopWatch.start("gettingRecordsToProcess");
		HashMap<String, ArrayList> keysMap = passFailDAO.getPendingRecordsForPassFailProcessingBYSAPID(searchBean);
		stopWatch.stop();
		System.out.println("gettingRecordsToProcess : " + keysMap.size());

		stopWatch.start("processNew");
		ArrayList<PassFailExamBean> processNew = passFailDAO.processNew(keysMap);
		stopWatch.stop();
		System.out.println("processNew : " + processNew.size());

		Set<PassFailTestBean> processNewSet = processNew.stream().map(k -> {
			PassFailTestBean pfTestBean = new PassFailTestBean();
			BeanUtils.copyProperties(k, pfTestBean);
			return pfTestBean;
		}).collect(Collectors.toSet());

		processNew = null;

		stopWatch.start("processNewForPassFail");

		ArrayList<PassFailExamBean> processNewForPassFail = passFailExecutorService.processNewForPassFail(keysMap);

		stopWatch.stop();
		System.out.println("processNewForPassFail : " + processNewForPassFail.size());

		keysMap = null;

		Set<PassFailTestBean> processNewForPassFailSet = processNewForPassFail.stream().map(k -> {
			PassFailTestBean pfTestBean = new PassFailTestBean();
			BeanUtils.copyProperties(k, pfTestBean);
			return pfTestBean;
		}).collect(Collectors.toSet());

		processNewForPassFail = null;

		Set<PassFailTestBean> notMatchingBeans = processNewForPassFailSet.stream()
				.filter(k -> !processNewSet.contains(k)).collect(Collectors.toSet());

		System.out.println("Number of not matching beans size : " + notMatchingBeans.size());

		System.out.println(notMatchingBeans);
		
		System.out.println(stopWatch.prettyPrint());
		
		assertEquals(processNewSet.size(), processNewForPassFailSet.size());
		
		assertEquals(0, notMatchingBeans.size());
	}

}
