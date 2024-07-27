package com.nmims.strategies.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.SyllabusBean;
import com.nmims.daos.SyllabusDAO;
import com.nmims.strategies.DeleteSyllabusStrategyInterface;

@Service("deleteSyllabusStrategy")
public class DeleteSyllabusStrategy implements DeleteSyllabusStrategyInterface{

	@Autowired
	SyllabusDAO syllabusDao;
	
	@Override
	public int deleteSyllabus(SyllabusBean bean) throws Exception {
		// TODO Auto-generated method stub
		return syllabusDao.deleteSyllabus(bean);
	}

	@Override
	public int deleteSyllabusDetails(SyllabusBean bean) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
