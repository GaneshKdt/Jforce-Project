package com.nmims.strategies.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.SyllabusBean;
import com.nmims.daos.SyllabusDAO;
import com.nmims.strategies.ReadSyllabusStrategyInterface;

@Service("readSyllabusForPSSStrategy")
public class ReadSyllabusForPSSStrategy implements ReadSyllabusStrategyInterface{

	@Autowired
	SyllabusDAO syllabusDao;

	@Override
	public ArrayList<SyllabusBean> readSyllabusForPSS(SyllabusBean bean) throws Exception {
		// TODO Auto-generated method stub
		return syllabusDao.getSyllabusForPSS(bean);
	}

	@Override
	public SyllabusBean readSyllabusForSyllabusId(SyllabusBean bean) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
