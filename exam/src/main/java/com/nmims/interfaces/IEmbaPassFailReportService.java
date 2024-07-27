package com.nmims.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.DissertationResultDTO;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.Q7Q8DissertationResultBean;
import com.nmims.dto.TEEResultDTO;

public interface IEmbaPassFailReportService {

	ArrayList<EmbaPassFailBean> searchEmbaMarks(TEEResultDTO resultBean, String string) throws Exception;

	Map<String, ArrayList<ConsumerProgramStructureExam>> getConsumerTypeByExamYearMonth(List<String> masterKeyList) throws Exception;

	List<String> getMsterKeysByBatch(ConsumerProgramStructureExam consumerProgramStructure) throws Exception;

	ArrayList<ConsumerProgramStructureExam> getBatchesByMsterKey(ConsumerProgramStructureExam consumerProgramStructure) throws Exception;
	
	Q7Q8DissertationResultBean getDissertionReport(TEEResultDTO resultBean, String authCode) throws Exception;
	
	
	
	

}
