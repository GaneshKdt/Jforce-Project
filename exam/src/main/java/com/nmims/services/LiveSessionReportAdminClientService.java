/**
 * 
 */
package com.nmims.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.LiveSessionReportAdminBean;
import com.nmims.beans.LiveSessionReportAdminDTO;
import com.nmims.interfaces.LiveSessionReportAdminClientServiceInterface;
import com.nmims.interfaces.LiveSessionReportAdminFactoryInterface;
import com.nmims.interfaces.LiveSessionReportAdminServiceInterface;

/**
 * @author vil_m
 *
 */

@Service("liveSessionReportAdminClientService")
public class LiveSessionReportAdminClientService implements LiveSessionReportAdminClientServiceInterface {
	@Autowired
	LiveSessionReportAdminFactoryInterface liveSessionReportAdminFactory;
	
	public static final Logger logger = LoggerFactory.getLogger(LiveSessionReportAdminClientService.class);

	@Override
	public List<LiveSessionReportAdminDTO> fetchLiveSessionReport(LiveSessionReportAdminDTO liveSessionReportAdminDTO,
			List<String> centerCodesList, String consumerTypeName) {
		// TODO Auto-generated method stub
		logger.info("Entering LiveSessionReportAdminClientService : fetchLiveSessionReport");
		LiveSessionReportAdminServiceInterface liveSessionReportAdminServiceInterface = null;
		LiveSessionReportAdminBean liveSessionReportAdminBean = null;
		List<LiveSessionReportAdminBean> listB = null;
		List<LiveSessionReportAdminDTO> listDTO = null;
		LiveSessionReportAdminDTO dtoObj = null;

		liveSessionReportAdminServiceInterface = liveSessionReportAdminFactory
				.getReportService(LiveSessionReportAdminFactoryInterface.PRODUCT_PG);

		liveSessionReportAdminBean = new LiveSessionReportAdminBean(liveSessionReportAdminDTO.getAcadYear(),
				liveSessionReportAdminDTO.getAcadMonth());

		listB = liveSessionReportAdminServiceInterface.fetchLiveSessionReport(liveSessionReportAdminBean,
				centerCodesList, consumerTypeName);

		if (null != listB) {
			listDTO = new ArrayList<LiveSessionReportAdminDTO>();
			for (int i = 0; i < listB.size(); i++) {
				dtoObj = new LiveSessionReportAdminDTO(listB.get(i).getAcadYear(), listB.get(i).getAcadMonth(),
						listB.get(i).getSapId(), listB.get(i).getStudentName(), listB.get(i).getEmailId(),
						listB.get(i).getPhone(), listB.get(i).getSubjectName(), listB.get(i).getSem(),
						listB.get(i).getCenterName(), listB.get(i).getConsumerType(),
						listB.get(i).getProgramStructure(), listB.get(i).getProgram(), listB.get(i).getSessionType());
				
				listDTO.add(dtoObj);
			}
		}
		return listDTO;
	}

}
