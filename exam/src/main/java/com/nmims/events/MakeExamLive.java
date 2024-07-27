package com.nmims.events;

import org.springframework.context.ApplicationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nmims.beans.ExamOrderExamBean;

public class MakeExamLive extends ApplicationEvent {

	private static final Logger logger = LoggerFactory.getLogger(MakeExamLive.class);

	private String examYear;
	private String examMonth;

	public MakeExamLive(String event, ExamOrderExamBean exam) {
		super(event);
		logger.info("Event published {} Month-{} Year-{}", event, exam.getMonth(), exam.getYear());
		this.examMonth = exam.getMonth();
		this.examYear = exam.getYear();
	}

	public String getExamYear() {
		return examYear;
	}

	public String getExamMonth() {
		return examMonth;
	}
}
