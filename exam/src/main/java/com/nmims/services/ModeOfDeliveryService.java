package com.nmims.services;

import java.util.Map;

import com.nmims.beans.ProgramExamBean;

public interface ModeOfDeliveryService {

	public Map<String, ProgramExamBean> getModProgramMap()throws Exception;
}
