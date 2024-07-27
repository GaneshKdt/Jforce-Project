package com.nmims.stratergies.impl;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ReadCopyCasesListBean;
import com.nmims.beans.ResultDomain;
import com.nmims.stratergies.CreateDetailedThresholdCCListInterface;

@Service("createDetailedThresholdCCList")
public class CreateDetailedThresholdCCList implements CreateDetailedThresholdCCListInterface{

	@Override
	public void createDetailedThresholdCCList(final ResultDomain searchBean, final ReadCopyCasesListBean listBean,
			final ModelAndView view, final Logger CCLogger) throws Exception {
		try {
			if(listBean.getDetailedThreshold1CClist() != null && listBean.getDetailedThreshold1CClist().size() >0) {
				view.addObject("DT1ListCount",listBean.getDetailedThreshold1CClist().size());
				view.addObject("detailedThreshold1List",listBean.getDetailedThreshold1CClist());
			}
			if(listBean.getDetailedThreshold2CClist() != null && listBean.getDetailedThreshold2CClist().size() >0) {
				view.addObject("DT2ListCount",listBean.getDetailedThreshold2CClist().size());
				view.addObject("detailedThreshold2List",listBean.getDetailedThreshold2CClist());
			}
			
			String erroMessage = "";
			String sapidError ="";
			if(listBean.getDetailedThreshold1CClist().size() == 0) {
				erroMessage = " Detailed Threshold 1 and ";
			}
			if(listBean.getDetailedThreshold2CClist().size() == 0) {
				erroMessage = erroMessage + " Detailed Threshold 2 ";
			}
			if(searchBean.getSapId1() != null && !(searchBean.getSapId1().equals(""))) {
				sapidError = "   and Sapid:" + searchBean.getSapId1();
			}
			if(erroMessage != null && !erroMessage.equals("")) {
				listBean.setErrorMessage("Error: No records found for "+erroMessage+"   Month-Year:"+searchBean.getMonth()+searchBean.getYear()+"   Subject:"+searchBean.getSubject()+sapidError);
				CCLogger.error("Error: No records found for "+erroMessage+" Month-Year:{}{} Subject:{} Sapid:{}",
						searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapId1());
			}
		} catch (Exception e) {
			listBean.setErrorMessage(e.getMessage());
			CCLogger.error("Exception Error createDetailedThresholdCCList() Month-Year:{}{} Subject:{} Error:{}",
					searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), e);
//			e.printStackTrace();
		}
		
	}

}
