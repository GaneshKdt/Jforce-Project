package com.nmims.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ReadCopyCasesListBean;
import com.nmims.beans.ResultDomain;
import com.nmims.services.AssignmentService;
import com.nmims.stratergies.ReadDetailedThresholdCCListInterface;

@Service("readDetailedThresholdCCList")
public class ReadDetailedThresholdCCList implements ReadDetailedThresholdCCListInterface{

	@Autowired
	AssignmentService asgService;
	
	@Override
	public ReadCopyCasesListBean readDetailedThresholdCCList(final ResultDomain searchBean, final ReadCopyCasesListBean listBean,
			final Logger CCLogger) throws Exception {
			
		// Detailed Threshold 1
		List<ResultDomain> detailedThreshold1CCList = null;
		try {
			detailedThreshold1CCList= asgService.getDetailedThreshold1CC(searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapId1());
			listBean.setDetailedThreshold1CClist(detailedThreshold1CCList);
		} catch (Exception e) {
			CCLogger.error("Exception Error getDetailedThreshold1CC() Month-Year:{}{} Subject:{} Error:{}",
					searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), e);
			listBean.setErrorMessage(e.getMessage());
//				e.printStackTrace();
		}
			
		// Detailed Threshold 2
		List<ResultDomain> detailedThreshold2CCList = null;
		try {
			detailedThreshold2CCList= asgService.getDetailedThreshold2CC(searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapId1());
			listBean.setDetailedThreshold2CClist(detailedThreshold2CCList);
		} catch (Exception e) {
			CCLogger.error("Exception Error getDetailedThreshold2CC() Month-Year:{}{} Subject:{} Error:{}",
					searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), e);
			listBean.setErrorMessage(e.getMessage());
//				e.printStackTrace();
		}
			
		return listBean;
	}

}
