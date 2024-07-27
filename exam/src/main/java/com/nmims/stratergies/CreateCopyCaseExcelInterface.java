package com.nmims.stratergies;

import org.slf4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ReadCopyCasesListBean;
import com.nmims.beans.ResultDomain;

public interface CreateCopyCaseExcelInterface {
	
	public abstract void createCopyCasesList(final ResultDomain bean, final ReadCopyCasesListBean listBean, final ModelAndView excelView, final Logger CCLogger)throws Exception;

}
