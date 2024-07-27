package com.nmims.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.SyllabusBean;
import com.nmims.strategies.SubjectForSyllabusStrategyInterface;
import com.nmims.strategies.impl.CreateSyllabusStrategy;
import com.nmims.strategies.impl.DeleteSyllabusDetailsStrategy;
import com.nmims.strategies.impl.DeleteSyllabusStrategy;
import com.nmims.strategies.impl.ReadSyllabusForPSSStrategy;
import com.nmims.strategies.impl.ReadSyllabusForSyllabusIdStrategy;
import com.nmims.strategies.impl.SubjectForSyllabusStrategy;
import com.nmims.strategies.impl.UpdateSyllabusStrategy;


@Service("syllabusService")
public class SyllabusService implements SyllabusServiceInterface{

	@Autowired
	CreateSyllabusStrategy createSyllabusStrategy;
	
	@Autowired
	ReadSyllabusForPSSStrategy readSyllabusForPSSStrategy;
	
	@Autowired
	ReadSyllabusForSyllabusIdStrategy readSyllabusForSyllabusIdStrategy;
	
	@Autowired
	SubjectForSyllabusStrategy subjectForSyllabusStrategy;
	
	@Autowired
	DeleteSyllabusStrategy deleteSyllabusStrategy;
	
	@Autowired
	UpdateSyllabusStrategy updateSyllabusStrategy;

	@Autowired
	DeleteSyllabusDetailsStrategy deleteSyllabusDetailsStrategy;
	
	@Override
	public Long createSyllabus(SyllabusBean bean) throws Exception{
		
		return createSyllabusStrategy.createSyllabus(bean);
	}

	@Override
	public ArrayList<SyllabusBean> readSyllabusForPSS(SyllabusBean bean) throws Exception{
		// TODO Auto-generated method stub
		return readSyllabusForPSSStrategy.readSyllabusForPSS(bean);
	}

	@Override
	public SyllabusBean readSyllabusForSyllabusId(SyllabusBean bean) throws Exception{
		// TODO Auto-generated method stub
		return readSyllabusForSyllabusIdStrategy.readSyllabusForSyllabusId(bean);
	}
	
	@Override
	public int updateSyllabus(SyllabusBean bean) throws Exception{
		// TODO Auto-generated method stub
		return updateSyllabusStrategy.updateSyllabus(bean);
	}

	@Override
	public int deleteSyllabus(SyllabusBean bean) throws Exception{
		// TODO Auto-generated method stub
		return deleteSyllabusStrategy.deleteSyllabus(bean);
	}

	@Override
	public SyllabusBean createSyllabusBulk(SyllabusBean bean) throws Exception{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SyllabusBean> getSubject() throws Exception {
		// TODO Auto-generated method stub
		return subjectForSyllabusStrategy.getSubject();
	}

	@Override
	public int deleteSyllabusDetails(SyllabusBean bean) throws Exception {
		// TODO Auto-generated method stub
		return deleteSyllabusDetailsStrategy.deleteSyllabusDetails(bean);
	}

}
