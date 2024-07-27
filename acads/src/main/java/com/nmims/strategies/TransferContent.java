package com.nmims.strategies;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ContentAcadsBean;
import com.nmims.daos.ContentDAO;

public interface TransferContent {
	

	public abstract HashMap<String,String> transferContent(ContentAcadsBean searchBean) throws Exception;


}
