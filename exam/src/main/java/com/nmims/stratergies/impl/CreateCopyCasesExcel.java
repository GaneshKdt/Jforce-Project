package com.nmims.stratergies.impl;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ReadCopyCasesListBean;
import com.nmims.beans.ResultDomain;
import com.nmims.stratergies.CreateCopyCaseExcelInterface;

@Service("createCopyCasesExcel")
public class CreateCopyCasesExcel implements CreateCopyCaseExcelInterface{

	@Override
	public void createCopyCasesList(final ResultDomain searchBean, final ReadCopyCasesListBean listBean, final ModelAndView excelView,
			final Logger CCLogger) throws Exception {
		try {
			if(listBean.getUnique1CCList() != null && listBean.getUnique1CCList().size() >0) {
				excelView.addObject("unique1CCList",listBean.getUnique1CCList());
			}
			if(listBean.getAbove90CCList() != null && listBean.getAbove90CCList().size() >0) {
				excelView.addObject("above90CCList",listBean.getAbove90CCList());
			}
			if(listBean.getUnique2CCList() != null && listBean.getUnique2CCList().size() >0) {
				excelView.addObject("unique2CCList",listBean.getUnique2CCList());
			}
		} catch (Exception e) {
			listBean.setErrorMessage(e.getMessage());
			CCLogger.error("Exception Error createCopyCasesList() Month-Year:{}{} Subject:{} Error:{}",
					searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), e);
//			e.printStackTrace();
		}
	}

	

	

	

}
