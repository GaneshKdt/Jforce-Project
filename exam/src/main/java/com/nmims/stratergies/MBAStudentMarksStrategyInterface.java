package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.MBAPassFailBean;

public interface MBAStudentMarksStrategyInterface {

	List<MBAPassFailBean> getMarksForStudentForSemester(String sapid, int term) throws Exception;

	List<MBAPassFailBean> getPassFailForCGPACalculationMBAX(String sapid, int term) throws Exception;
	
}
