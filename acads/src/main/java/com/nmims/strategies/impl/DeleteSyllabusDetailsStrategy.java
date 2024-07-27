package com.nmims.strategies.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.SyllabusBean;
import com.nmims.daos.SyllabusDAO;
import com.nmims.strategies.DeleteSyllabusStrategyInterface;

@Service("deleteSyllabusDetailsStrategy")
public class DeleteSyllabusDetailsStrategy implements DeleteSyllabusStrategyInterface {

	@Autowired
	SyllabusDAO syllabusDao;
	
	@Override
	public int deleteSyllabus(SyllabusBean bean) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteSyllabusDetails(SyllabusBean bean) throws Exception {
		// TODO Auto-generated method stub
		return syllabusDao.deleteSyllabusDetails(bean);
	}

}
