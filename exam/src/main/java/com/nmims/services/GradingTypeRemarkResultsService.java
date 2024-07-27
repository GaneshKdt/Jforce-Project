/**
 * 
 */
package com.nmims.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nmims.beans.RemarksGradeResultsBean;
import com.nmims.beans.RemarksGradeResultsDTO;
import com.nmims.interfaces.GradingTypeResultsServiceInterface;
import com.nmims.stratergies.RemarkResultsDisplayStrategyInterface;

/**
 * @author vil_m
 *
 */

@Service("gradingTypeRemarkResultsService")
public class GradingTypeRemarkResultsService implements GradingTypeResultsServiceInterface {
	
	public static final Logger logger = LoggerFactory.getLogger(GradingTypeRemarkResultsService.class);
	
	@Autowired
	@Qualifier("remarkResultsDisplayStrategy")
	private RemarkResultsDisplayStrategyInterface remarkResultsDisplayStrategy;

	@Override
	public List<RemarksGradeResultsDTO> fetchStudentsResult(String sapid,
			Integer resultLive, String status, Integer statusPassFail, String activeFlag) {
		// TODO Auto-generated method stub
		logger.info("Entering GradingTypeRemarkResultsService : fetchStudentsResult");
		List<RemarksGradeResultsBean> list1 = null;
		List<RemarksGradeResultsDTO> list2 = null;
		RemarksGradeResultsDTO remarksGradeResultsDTO = null;
		
		list1 = remarkResultsDisplayStrategy.fetchStudentsResult(sapid, resultLive, status, statusPassFail, activeFlag);
		
		list2 = new ArrayList<RemarksGradeResultsDTO>();
		for (int z = 0; z < list1.size(); z++) {
			remarksGradeResultsDTO = new RemarksGradeResultsDTO(list1.get(z).getYear(), list1.get(z).getMonth(),
					list1.get(z).getSapid(), list1.get(z).getSem(), list1.get(z).getSubject());
			remarksGradeResultsDTO.setScoreTotal(list1.get(z).getScoreTotal());
			remarksGradeResultsDTO.setGrade(list1.get(z).getGrade());
			remarksGradeResultsDTO.setRemarks(list1.get(z).getRemarks());
			remarksGradeResultsDTO.setFailReason(list1.get(z).getFailReason());
            //if(Integer.parseInt(remarksGradeResultsDTO.getScoreTotal())>= 30) {
				list2.add(remarksGradeResultsDTO);
			//}
		}
		return list2;
	}

	@Override
	public boolean checkResultsAvailable(String sapid, Integer resultLive,
			String status, Integer statusPassFail, Integer processedFlag, String activeFlag) {
		// TODO Auto-generated method stub
		logger.info("Entering GradingTypeRemarkResultsService : checkResultsAvailable");
		Boolean areResultsAvailable = Boolean.FALSE;
		areResultsAvailable = remarkResultsDisplayStrategy.checkResultsAvailable(sapid, resultLive,
				status, statusPassFail, processedFlag, activeFlag);
		return areResultsAvailable;
	}

}
