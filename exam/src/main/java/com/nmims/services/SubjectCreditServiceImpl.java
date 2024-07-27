package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nmims.beans.MDMSubjectCodeMappingBean;
import com.nmims.daos.SubjectCreditDAO;

@Service("creditService")
public class SubjectCreditServiceImpl implements SubjectCreditService {

	@Autowired
	private SubjectCreditDAO creditDAO;
	
	public static final Logger subjectCredit = LoggerFactory.getLogger("subjectCredit");
	
	
	@Override
	public Map<Integer, MDMSubjectCodeMappingBean> getMappedSubjectCredit() throws Exception {
		List<MDMSubjectCodeMappingBean> subjectCreditList = new ArrayList<>();
		Map<Integer, MDMSubjectCodeMappingBean> mapSubjectCredit = new HashMap<>();
		try
		{
			subjectCreditList = creditDAO.getSubjectCreditList();
			mapSubjectCredit = subjectCreditList.stream().collect(Collectors.toMap(MDMSubjectCodeMappingBean :: getId, bean -> bean,(oldEntry, newEntry) -> newEntry));
			if(CollectionUtils.isEmpty(mapSubjectCredit))
				throw new Exception("No record found for subject credits!");
		}
		catch(Exception e)
		{
			subjectCredit.error("Error in getting Subject Credits: "+e);
			throw new Exception("Error in getting Subject Credits: "+e);
		}
		return mapSubjectCredit;
	}
	
	@Override
	public Map<String, MDMSubjectCodeMappingBean> getMappedPssDetail() throws Exception {
		List<MDMSubjectCodeMappingBean> pssDetailList = new ArrayList<>();
		Map<String, MDMSubjectCodeMappingBean> mapPssDetail = new HashMap<>();
		try
		{
			pssDetailList = creditDAO.getPssDetailList();
			mapPssDetail = pssDetailList.stream().collect(Collectors.toMap(bean -> bean.getConsumerProgramStructureId()+"-"+bean.getSubjectName()+"-"+bean.getSem(), bean -> bean,(oldEntry, newEntry) -> newEntry));
			if(CollectionUtils.isEmpty(mapPssDetail))
				throw new Exception("No record found for PSS-Detail!");
		}
		catch(Exception e)
		{
			subjectCredit.error("Error in getting PSS-Detail: "+e);
			throw new Exception("Error in getting PSS-Detail: "+e);
		}
		return mapPssDetail;
	}
}
