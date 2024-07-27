package com.nmims.stratergies.impl;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ReadCopyCasesListBean;
import com.nmims.beans.ResultDomain;
import com.nmims.services.AssignmentService;
import com.nmims.stratergies.ReadCopyCasesListInterface;

@Service("readCopyCasesList")
public class ReadCopyCasesList implements ReadCopyCasesListInterface{
	
	@Autowired
	AssignmentService asgService;

	@Override
	public ReadCopyCasesListBean readCopyCasesList(final ResultDomain searchBean, final ReadCopyCasesListBean listBean, final Logger CCLogger)
			throws Exception {
		
		List<ResultDomain> unique1CCList = null;
		try {
			unique1CCList= asgService.getUnique1CCList(searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapid());
			listBean.setUnique1CCList(unique1CCList);
		} catch (Exception e) {
			CCLogger.error("Exception Error getUnique1CC() Month-Year:{}{} Subject:{} Error:{}",
					searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), e);
			listBean.setErrorMessage(e.getMessage());
//			e.printStackTrace();
		}
		
		List<ResultDomain> above90CCList = null;
		try {
			above90CCList = asgService.getStudentAbove90CCList(searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapid());
			listBean.setAbove90CCList(above90CCList);
		} catch (Exception e) {
			CCLogger.error("Exception Error getStudentAbove90CCList() Month-Year:{}{} Subject:{} Error:{}",
					searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), e);
			listBean.setErrorMessage(e.getMessage());
//			e.printStackTrace();
		}
		
		List<ResultDomain> unique2CCList = null;
		try {
			unique2CCList = asgService.getUnique2CCList(searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapid());
			listBean.setUnique2CCList(unique2CCList);
		} catch (Exception e) {
			CCLogger.error("Exception Error getUnique2CC() Month-Year:{}{} Subject:{} Error:{}",
					searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), e);
			listBean.setErrorMessage(e.getMessage());
//			e.printStackTrace();
		}
		return listBean;
	}



	

}
