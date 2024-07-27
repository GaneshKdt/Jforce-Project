package com.nmims.daos;

import java.util.List;

import com.nmims.beans.MDMSubjectCodeMappingBean;

public interface SubjectCreditDAO {

	public List<MDMSubjectCodeMappingBean> getSubjectCreditList() throws Exception;
	public List<MDMSubjectCodeMappingBean> getPssDetailList() throws Exception;
}
