package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.UFMNoticeBean;

public interface UFMStudentStrategyInterface {

	public List<UFMNoticeBean> getListOfShowCauseSubjects(String sapid) throws Exception;

	List<UFMNoticeBean> getListOfShowCauseSubjectsForStudentYearMonth(String sapid, String year, String month) throws Exception;

	boolean checkIfMarkedForCurrentCycle(String sapid) throws Exception;

	void setStudentResponse(UFMNoticeBean bean) throws Exception;

	void setUFMStatus(UFMNoticeBean bean) throws Exception;
}
