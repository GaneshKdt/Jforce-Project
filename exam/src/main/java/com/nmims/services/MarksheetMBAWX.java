package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.nmims.beans.EmbaMarksheetBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.factory.MarksheetFactory;

public class MarksheetMBAWX  extends MarksheetFactory{
	@Autowired
	ApplicationContext act;
	@Override
	public MarksheetBean studentSelfMarksheet(PassFailExamBean studentMarks) {
		// TODO Auto-generated method stub
		EmbaMarksheetBean marksheetBean = new EmbaMarksheetBean();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		StudentExamBean student = sapIdStudentsMap.get(studentMarks.getSapid());		
		//marksheetBean.setSem(studentData.getSem());
		marksheetBean.setFirstName(student.getFirstName());
		marksheetBean.setLastName(student.getLastName());
		marksheetBean.setSapid(student.getSapid());
		marksheetBean.setFatherName(student.getFatherName());
		marksheetBean.setMotherName(student.getMotherName());
		marksheetBean.setProgram(student.getProgram());
		
		return null;
	}
	@Override
	public ArrayList<EmbaPassFailBean> getClearedSemForStudent(String sapid) {
		// TODO Auto-generated method stub
		
		
		return null;
	}

}
