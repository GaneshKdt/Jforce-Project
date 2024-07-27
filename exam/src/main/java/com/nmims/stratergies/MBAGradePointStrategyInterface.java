package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.MBAPassFailBean;

public interface MBAGradePointStrategyInterface {

	public String getGPA(int term, List<MBAPassFailBean> subjectResults) throws Exception;
	
	public String getCGPA(int term, List<MBAPassFailBean> passFailDataListAllSem) throws Exception;
}
