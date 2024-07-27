package com.nmims.stratergies;

import com.nmims.beans.MBATranscriptBean;
import com.nmims.beans.StudentExamBean;

public interface MBATranscriptStrategyInterface {

	public MBATranscriptBean getTranscriptBeanForStudent(String sapid, String logoRequired, StudentExamBean student) throws Exception;
}
