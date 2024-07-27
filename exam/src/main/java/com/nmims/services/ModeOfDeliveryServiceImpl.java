package com.nmims.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ProgramExamBean;
import com.nmims.daos.StudentMarksDAO;

@Service("modService")
public class ModeOfDeliveryServiceImpl implements ModeOfDeliveryService{

	@Autowired
	private StudentMarksDAO studentMarksDAO;
	
	
	@Override
	public Map<String, ProgramExamBean> getModProgramMap()throws Exception {
		Map<String, ProgramExamBean> modProgramMap = new HashMap<>();
		try
		{
			List<ProgramExamBean> modProgramList = studentMarksDAO.getModProgramList();
			modProgramMap = modProgramList.stream().collect(Collectors.toMap(bean -> bean.getCode(), bean -> bean));
		}
		catch(Exception ex)
		{
			throw new Exception("Error in getting Mode Of Delivery: "+ex);
		}
		return modProgramMap;
	}
}
