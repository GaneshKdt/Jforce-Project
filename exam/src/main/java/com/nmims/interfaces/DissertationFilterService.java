package com.nmims.interfaces;

import java.util.List;
import java.util.Map;

import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.dto.DissertationResultProcessingDTO;

public interface DissertationFilterService {

	public List<TEEResultBean> filterIAEligibleStudent(List<TimeBoundUserMapping> timboundUser, String subjectId, 
			DissertationResultProcessingDTO program, List<DissertationResultBean> errorList);
	
	public List<DissertationResultBean> filterIAScores(TEEResultBean sapId, 
			List<DissertationResultProcessingDTO> iaTestScore, List<TestExamBean> examTest, String loggedInUser) ;

}
