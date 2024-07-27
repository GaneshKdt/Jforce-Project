package com.nmims.strategies.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nmims.beans.SyllabusBean;
import com.nmims.daos.SyllabusDAO;
import com.nmims.strategies.CreateSyllabusStrategyInterface;

@Service("createSyllabusStrategy")
public class CreateSyllabusStrategy implements CreateSyllabusStrategyInterface{

	@Autowired
	SyllabusDAO syllabusDao;
	
	@Override
	public Long createSyllabus(SyllabusBean bean) throws Exception{
		
		return syllabusDao.insertSyllabus(bean);
	}

}
