package com.nmims.services;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.KnowlarityData;
import com.nmims.daos.StudentZoneDao;

@Service
public class KnowlarityService {

	@Autowired
	StudentZoneDao studentZoneDao;
	
	private static final Logger logger = LoggerFactory.getLogger(KnowlarityService.class);
	
	public int insertData(KnowlarityData kd) throws Exception
	{
		try
		{
			String strDate=kd.getCall_date__c();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date = sdf.parse(strDate);
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			return studentZoneDao.insertKnowlarityData(kd,sqlDate);
		}
		catch(Exception e)
		{
			logger.info("Exception is:"+e.getMessage());
			throw new Exception(e.getMessage());
		}
	}
	
}
