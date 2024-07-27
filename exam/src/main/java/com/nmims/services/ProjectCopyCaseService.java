package com.nmims.services;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ReadCopyCasesListBean;
import com.nmims.beans.ResultDomain;
import com.nmims.interfaces.CopyCaseInterface;
import com.nmims.services.impl.ReadDetailedThresholdCCList;
import com.nmims.stratergies.impl.CreateCopyCasesExcel;
import com.nmims.stratergies.impl.CreateDetailedThresholdCCList;
import com.nmims.stratergies.impl.ReadCopyCasesList;

@Service("projectCCService")
public class ProjectCopyCaseService implements CopyCaseInterface{

	@Autowired
	ReadCopyCasesList readCopyCasesList;
	
	@Autowired
	CreateCopyCasesExcel createCopyCasesExcel;
	
	@Autowired
	ReadDetailedThresholdCCList readDetailedThresholdCCList;
	
	@Autowired
	CreateDetailedThresholdCCList createDetailedThresholdCCList;
	
	@Override
	public ReadCopyCasesListBean readCopyCasesList(final ResultDomain searchBean, final ReadCopyCasesListBean listBean, final Logger CCLogger) throws Exception {
		return readCopyCasesList.readCopyCasesList(searchBean, listBean, CCLogger);
	}

	@Override
	public void createCopyCasesList(final ResultDomain searchBean, final ReadCopyCasesListBean listBean, final ModelAndView excelView,final Logger CCLogger) throws Exception {
		createCopyCasesExcel.createCopyCasesList(searchBean, listBean, excelView, CCLogger);
	}

	@Override
	public ReadCopyCasesListBean readDetailedThresholdCCList(final ResultDomain searchBean, final ReadCopyCasesListBean bean,
			final Logger CCLogger) throws Exception {
		return readDetailedThresholdCCList.readDetailedThresholdCCList(searchBean, bean, CCLogger);
	}

	@Override
	public void createDetailedThresholdCCList(final ResultDomain searchBean, final ReadCopyCasesListBean listBean,
			final ModelAndView view, final Logger CCLogger) throws Exception {
		createDetailedThresholdCCList.createDetailedThresholdCCList(searchBean, listBean, view, CCLogger);
	}

	
}
