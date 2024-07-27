package com.nmims.stratergies.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nmims.beans.Page;
import com.nmims.beans.UFMNoticeBean;
import com.nmims.daos.UFMNoticeDAO;
import com.nmims.stratergies.UFMMarkedStudentsListStrategyInterface;

@Service("ufmMarkedStudentsListStrategy")
public class UFMMarkedStudentsListStrategy implements UFMMarkedStudentsListStrategyInterface {

	@Autowired
	UFMNoticeDAO dao;
	
	@Override
	public Page<UFMNoticeBean> getListOfStudentsMarked(UFMNoticeBean inputBean, int pageNo, int pageSize) throws Exception {
		return dao.getStudentsMarkedForShowCause(inputBean,pageNo,pageSize);
	}

}
