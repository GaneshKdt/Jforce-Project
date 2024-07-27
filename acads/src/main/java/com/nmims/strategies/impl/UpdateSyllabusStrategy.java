package com.nmims.strategies.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.SyllabusBean;
import com.nmims.daos.SyllabusDAO;
import com.nmims.strategies.UpdateSyllabusStrategyInterface;

@Service("updateSyllabusStrategy")
public class UpdateSyllabusStrategy implements UpdateSyllabusStrategyInterface {

	@Autowired
	SyllabusDAO syllabusDao;
	
	@Override
	public int updateSyllabus(SyllabusBean bean) throws Exception {
		// TODO Auto-generated method stub
		return syllabusDao.updateSyllabus(bean);
	}

}
