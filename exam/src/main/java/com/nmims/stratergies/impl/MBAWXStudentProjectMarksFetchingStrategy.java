package com.nmims.stratergies.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.TEEResultBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.stratergies.ITimeboundStudentProjectMarksFetchingStrategy;
/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
@Service("mbawxStudentProjectMarksFetchingStrategy")
public class MBAWXStudentProjectMarksFetchingStrategy implements ITimeboundStudentProjectMarksFetchingStrategy {

	@Autowired
	private ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Override
	public List<TEEResultBean> getTimeboundStudentProjectMarks(String subjectName, Integer timeboundId) {
		return examsAssessmentsDAO.getStudentProjectMarks(subjectName,timeboundId);
	}

}
