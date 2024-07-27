package com.nmims.stratergies;

import com.nmims.beans.MBAMarksheetBean;
import com.nmims.beans.StudentExamBean;

public interface MBAMarksheetStrategyInterface {
	
	MBAMarksheetBean getMarksheetBeanForStudentForTerm(String sapid, StudentExamBean student, int term) throws Exception;
	
	void setExamMonthYearForMarksheet(MBAMarksheetBean studentMarks) throws Exception;
	
	void setRemarkForTerm(MBAMarksheetBean marksheet) throws Exception;
}
