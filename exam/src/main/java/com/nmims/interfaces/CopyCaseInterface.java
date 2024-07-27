package com.nmims.interfaces;

import org.slf4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ReadCopyCasesListBean;
import com.nmims.beans.ResultDomain;

public interface CopyCaseInterface {
	
	public abstract ReadCopyCasesListBean readCopyCasesList(final ResultDomain searchBean, final ReadCopyCasesListBean bean, final Logger CCLogger) throws Exception;
	
	public abstract void createCopyCasesList(final ResultDomain searchBean, final ReadCopyCasesListBean listBean, final ModelAndView excelView, final Logger CCLogger)throws Exception;
	
	public abstract ReadCopyCasesListBean readDetailedThresholdCCList(final ResultDomain searchBean, final ReadCopyCasesListBean bean, final Logger CCLogger) throws Exception;

	public abstract void createDetailedThresholdCCList(final ResultDomain searchBean, final ReadCopyCasesListBean listBean, final ModelAndView view, final Logger CCLogger)throws Exception;

}
