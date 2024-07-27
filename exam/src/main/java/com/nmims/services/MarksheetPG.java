package com.nmims.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.factory.MarksheetFactory;

public class MarksheetPG extends MarksheetFactory{
	@Autowired
	ApplicationContext act;

	@Override
	public MarksheetBean studentSelfMarksheet(PassFailExamBean studentMarks) {
		// TODO Auto-generated method stub
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		MarksheetBean marksheetBean = new MarksheetBean();
		String sapId = studentMarks.getSapid();
		
		boolean hasAppearedForExamForGivenSemMonthYear = dao
				.hasAppearedForExamForGivenSemMonthYear(studentMarks,studentMarks.getExamMode());
		if (hasAppearedForExamForGivenSemMonthYear == false) {
			marksheetBean.setError(true);
			marksheetBean.setMessage(
					"You have not appeared for Semester "
							+ studentMarks.getSem() + " in Year "
							+ studentMarks.getWrittenYear() + " and month "
							+ studentMarks.getWrittenMonth());
			
			return marksheetBean;
		}
		
		List<StudentMarksBean> studentMarksBean = getStudentMarksHistory(studentMarks, marksheetBean);

		// Check if results are live

		marksheetBean = dao.getSingleStudentsData(studentMarks);
		String resultDeclarationDate = "";
		ExamOrderExamBean exam = dao.getExamDetails(studentMarks.getWrittenMonth(),
				studentMarks.getWrittenYear());
		
		if (exam == null) {
			marksheetBean.setError(true);
			marksheetBean.setMessage(
					"No Exam Bookings found for "
							+ studentMarks.getWrittenMonth() + "-"
							+ studentMarks.getWrittenYear()
							+ " Exam, Semester " + studentMarks.getSem());
			return marksheetBean;
		}
		
		String resultLive = "N";

		if ("Online".equals(studentMarks.getExamMode())) {
			resultDeclarationDate = dao.getOnlineExamDeclarationDate(
					studentMarks.getWrittenMonth(),
					studentMarks.getWrittenYear());
			resultLive = exam.getLive();
		} else {
			resultDeclarationDate = dao.getOfflineExamDeclarationDate(
					studentMarks.getWrittenMonth(),
					studentMarks.getWrittenYear());
			resultLive = exam.getOflineResultslive();
		}
		marksheetBean.setResultDeclarationDate(resultDeclarationDate);
		if (!"Y".equalsIgnoreCase(resultLive)) {
			marksheetBean.setError(true);
			marksheetBean.setMessage(
					"Results are not yet announced for "
							+ studentMarks.getWrittenMonth() + "-"
							+ studentMarks.getWrittenYear() + " Exam cycle.");
			return marksheetBean;
		}
		
		
		

		return marksheetBean;
	}
	
	private List<StudentMarksBean> getStudentMarksHistory(PassFailExamBean psBean, MarksheetBean msBean) {
		StudentMarksBean bean = new StudentMarksBean();
		StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		bean.setSapid(psBean.getSapid());
		List<StudentMarksBean> studentMarksListForMarksHistory = null;
		if ("Online".equals(psBean.getExamMode())) {
			studentMarksListForMarksHistory = dao
					.getAStudentsMarksForOnline(bean);
		} else {
			studentMarksListForMarksHistory = dao
					.getAStudentsMarksForOffline(bean);
		}
	
		
		return studentMarksListForMarksHistory;
	}

	@Override
	public ArrayList<EmbaPassFailBean> getClearedSemForStudent(String sapid) {
		// TODO Auto-generated method stub
		return null;
	}


}
