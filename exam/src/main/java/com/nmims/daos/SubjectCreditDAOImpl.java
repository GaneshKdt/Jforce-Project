package com.nmims.daos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nmims.beans.MDMSubjectCodeMappingBean;

@Repository("creditDAO")
public class SubjectCreditDAOImpl implements SubjectCreditDAO{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	@Override
	public List<MDMSubjectCodeMappingBean> getPssDetailList() throws Exception {
		String query = "Select id,consumerProgramStructureId,subject As subjectName,sem From exam.program_sem_subject";
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<MDMSubjectCodeMappingBean>(MDMSubjectCodeMappingBean.class));
	}
	
	@Override
	public List<MDMSubjectCodeMappingBean> getSubjectCreditList() throws Exception {
		String query = "Select id,subjectCredits From exam.mdm_subjectCode_mapping";
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<MDMSubjectCodeMappingBean>(MDMSubjectCodeMappingBean.class));
	}
}
