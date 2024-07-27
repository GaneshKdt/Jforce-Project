package com.nmims.strategies.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.SyllabusBean;
import com.nmims.daos.SyllabusDAO;
import com.nmims.strategies.SubjectForSyllabusStrategyInterface;

@Service("subjectForSyllabusStrategy")
public class SubjectForSyllabusStrategy implements SubjectForSyllabusStrategyInterface{

	@Autowired
	SyllabusDAO syllabusDao;
	
	@Override
	public ArrayList<SyllabusBean> getSubject() throws Exception {
		// TODO Auto-generated method stub
		return syllabusDao.getSubjectForSyllabus();
	}

}
