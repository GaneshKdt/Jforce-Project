package com.nmims.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.nmims.beans.IdCardExamBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.interfaces.IdCardServiceInterface;

@Service
public class StudentIdCardService implements IdCardServiceInterface {
	
	@Autowired
	ApplicationContext act;
	
	/**
	 * @Param:- sapid - sapid of the student
	 * @return:- IdCardExamBean - IdCardExamBean bean object contains sapid, fileName and status
	 * */
	@Override
	public IdCardExamBean getIdCardForStudent(String sapid) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		IdCardExamBean idCardBean=new IdCardExamBean();
		try {
			idCardBean=dao.getIdCardForStudent(sapid);
			idCardBean.setStatus("success");
			return idCardBean;
		}catch(EmptyResultDataAccessException e){
			idCardBean.setStatus("error");
			idCardBean.setMessage("Id card not available");
			return idCardBean;
		}catch (Exception e) {
			idCardBean.setStatus("error");
			idCardBean.setMessage(e.getMessage());
			return idCardBean;
		}
	}

}
