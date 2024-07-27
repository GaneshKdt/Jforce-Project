package com.nmims.services;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.nmims.beans.ExamOrderExamBean;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.events.MakeExamLive;

@Service
public class MakeLiveService extends Observable {

	@Autowired 
	StudentMarksDAO studentMarksDAO;
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	private static final Logger logger = LoggerFactory.getLogger(MakeLiveService.class);
	
	public void makeResultsLive(ExamOrderExamBean exam){
		String event = "DeclareResultsLive";
		studentMarksDAO.updateExamStats(exam);
		notifyObservers();
		logger.info("Event notify {} -{} Month-{} Year-{}",event,exam.getLive(),exam.getMonth(),exam.getYear());
		applicationEventPublisher.publishEvent(new MakeExamLive(event,exam));
	}
	
	
	// Make regular assignment submission live
	public void makeAssignmentSubmissionLiveStatus(ExamOrderExamBean exam){
//		String event = "RegularAssignmentSubmissionLive";
		studentMarksDAO.updateAssignmentSubmissionLiveStatus(exam);
//		notifyObservers();
//		logger.info("Event notify {} -{} Month-{} Year-{}",event,exam.getAssignmentLive(),exam.getMonth(),exam.getYear());
//		applicationEventPublisher.publishEvent(new MakeExamLive(event,exam));
	}
	
	// Make resit assignment submission live
	public void makeResitAssignmentSubmissionLiveStatus(ExamOrderExamBean exam){
//		String event = "ResitAssignmentSubmissionLive";
		studentMarksDAO.updateResitAssignmentSubmissionLiveStatus(exam);
//		notifyObservers();
//		logger.info("Event notify {} -{} Month-{} Year-{}",event,exam.getResitAssignmentLive(),exam.getMonth(),exam.getYear());
//		applicationEventPublisher.publishEvent(new MakeExamLive(event,exam));
	}
	
	// Make project submission live
	public void makeProjectSubmissionLiveStatus(ExamOrderExamBean exam){
//		String event = "ProjectSubmissionLive";
		studentMarksDAO.updateProjectSubmissionLiveStatus(exam);
//		notifyObservers();
//		logger.info("Event notify {} -{} Month-{} Year-{}",event,exam.getProjectSubmissionLive(),exam.getMonth(),exam.getYear());
//		applicationEventPublisher.publishEvent(new MakeExamLive(event,exam));
	}
}
