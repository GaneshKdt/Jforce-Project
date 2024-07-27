package com.nmims.stratergies;

import org.slf4j.Logger;

import com.nmims.beans.ReadCopyCasesListBean;
import com.nmims.beans.ResultDomain;

public interface ReadCopyCasesListInterface {
	
	public abstract ReadCopyCasesListBean readCopyCasesList(final ResultDomain searchBean, final ReadCopyCasesListBean bean, final Logger CCLogger) throws Exception;

}
