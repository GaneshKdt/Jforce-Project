package com.nmims.stratergies;

import org.slf4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ReadCopyCasesListBean;
import com.nmims.beans.ResultDomain;

public interface CreateDetailedThresholdCCListInterface {
	
	public abstract void createDetailedThresholdCCList(final ResultDomain searchBean, final ReadCopyCasesListBean listBean, final ModelAndView view, final Logger CCLogger)throws Exception;


}
