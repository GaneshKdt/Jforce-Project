package com.nmims.stratergies.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.MBAMarksheetBean;
import com.nmims.beans.MBATranscriptBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.stratergies.MBATranscriptStrategyInterface;

@Service("mbaxTranscriptStrategy")
public class MBAXTranscriptStrategy implements MBATranscriptStrategyInterface {

	@Autowired
	MBAXMarksheetStrategy marksheetStrategy;

	@Override
	public MBATranscriptBean getTranscriptBeanForStudent(String sapid, String logoRequired, StudentExamBean student) throws Exception {
		MBATranscriptBean transcriptBean = new MBATranscriptBean();
		transcriptBean.setSapid(sapid);
		transcriptBean.setLogoRequired(logoRequired);
		transcriptBean.setStudent(student);
		
		int numberOfTerms = 0; 
		if(student.getConsumerProgramStructureId().equals("119")) {
			numberOfTerms = 5;
		} else if(student.getConsumerProgramStructureId().equals("126")) {
			numberOfTerms = 6;
		} else if(student.getConsumerProgramStructureId().equals("162")) {
			numberOfTerms = 8;
		} else {
			throw new Exception("Error getting information for student!");
		}

		Map<Integer, MBAMarksheetBean> marksheetList = new HashMap<Integer, MBAMarksheetBean>();
		for (int term = 1; term <= numberOfTerms; term++) {
			MBAMarksheetBean marksheetBean = marksheetStrategy.getMarksheetBeanForStudentForTerm(sapid, student, term);
			marksheetList.put(term, marksheetBean);
		}
		transcriptBean.setSemSubjectList(marksheetList);
		if(StringUtils.isBlank(marksheetList.get(numberOfTerms).getClearExamMonth()) || StringUtils.isBlank(marksheetList.get(numberOfTerms).getClearExamYear())) {
			transcriptBean.setPassYearMonth("Pursuing");
		} else {
			transcriptBean.setPassYearMonth(marksheetList.get(numberOfTerms).getClearExamMonth() + "-" + marksheetList.get(numberOfTerms).getClearExamYear() );
		}
		
		return transcriptBean;
	}
}
