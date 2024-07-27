package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service("responseBean")
public class ResponseBean  implements Serializable  {

	private ArrayList<ConsumerProgramStructureExam> programData;
	private ArrayList<ConsumerProgramStructureExam> programStructureData;
	private ArrayList<ConsumerProgramStructureExam> subjectsData;
	private ArrayList<StudentExamBean> studentsData;
	private ArrayList<BatchExamBean> batchData;
	private ArrayList<StudentExamBean> studentList;
	
	private List<TestExamBean> dataForReferenceId;
	private List<String> listOfStringData;
	
	private ArrayList<MBAXPassFailBean> mbaXPassFailData ;
	
	private int code;
	private String status;
	private String message;
	private List<TestQuestionConfigBean> questionConfigBean;
	private List<ExamBookingTransactionBean> examBookingTransactionBeanList;

	private Page<TcsOnlineExamBean> page;
	private List<TcsOnlineExamBean> tcsOnlineExamBeanList;	
	private ArrayList<TCSMarksBean> tcsResultsDetails;
	private TcsOnlineExamBean tcsOnlineExamBean;
	
	private UpgradAssessmentExamBean upgradAssessmentBean;
	private List<UpgradTestQuestionExamBean> upgradTestQuestionBean;
	private List<DemoExamAttendanceBean> demoExamAttendanceList;

	private List<MettlResultsSyncBean> resultsReponse;
	private List<MettlResponseBean> resultsSuccessReponse;
	private List<MettlResponseBean> resultsFailureReponse;
	

	private List<ExamBookingTransactionBean> examBookingTransactionBean;
	
	ArrayList<ExamBookingTransactionBean> listOfExamBookingFeeReceiptsBasedOnSapid = new ArrayList<ExamBookingTransactionBean>();
	ArrayList<ExamBookingTransactionBean> listOfSRFeeReceiptsBasedOnSapid  = new ArrayList<ExamBookingTransactionBean>();
	
	private ArrayList<MettlRegisterCandidateBeanMBAWX> mettlRegisterCandidateBeanMBAWX = new ArrayList<MettlRegisterCandidateBeanMBAWX>();
	
	public ArrayList<MettlRegisterCandidateBeanMBAWX> getMettlRegisterCandidateBeanMBAWX() {
		return mettlRegisterCandidateBeanMBAWX;
	}
	public void setMettlRegisterCandidateBeanMBAWX(
			ArrayList<MettlRegisterCandidateBeanMBAWX> mettlRegisterCandidateBeanMBAWX) {
		this.mettlRegisterCandidateBeanMBAWX = mettlRegisterCandidateBeanMBAWX;
	}
	public ArrayList<ExamBookingTransactionBean> getListOfExamBookingFeeReceiptsBasedOnSapid() {
		return listOfExamBookingFeeReceiptsBasedOnSapid;
	}
	public void setListOfExamBookingFeeReceiptsBasedOnSapid(
		ArrayList<ExamBookingTransactionBean> listOfExamBookingFeeReceiptsBasedOnSapid) {
		this.listOfExamBookingFeeReceiptsBasedOnSapid = listOfExamBookingFeeReceiptsBasedOnSapid;
	}
	public ArrayList<ExamBookingTransactionBean> getListOfSRFeeReceiptsBasedOnSapid() {
		return listOfSRFeeReceiptsBasedOnSapid;
	}
	public void setListOfSRFeeReceiptsBasedOnSapid(ArrayList<ExamBookingTransactionBean> listOfSRFeeReceiptsBasedOnSapid) {
		this.listOfSRFeeReceiptsBasedOnSapid = listOfSRFeeReceiptsBasedOnSapid;
	}
	public List<ExamBookingTransactionBean> getExamBookingTransactionBean() {
		return examBookingTransactionBean;
	}
	public void setExamBookingTransactionBean(List<ExamBookingTransactionBean> examBookingTransactionBean) {
		this.examBookingTransactionBean = examBookingTransactionBean;
	}
	
	
	public TcsOnlineExamBean getTcsOnlineExamBean() {
		return tcsOnlineExamBean;
	}
	public void setTcsOnlineExamBean(TcsOnlineExamBean tcsOnlineExamBean) {
		this.tcsOnlineExamBean = tcsOnlineExamBean;
	}	

	public List<MettlResultsSyncBean> getResultsReponse() {
		return resultsReponse;
	}
	public void setResultsReponse(List<MettlResultsSyncBean> resultsReponse) {
		this.resultsReponse = resultsReponse;
	}
	public List<MettlResponseBean> getResultsSuccessReponse() {
		return resultsSuccessReponse;
	}
	public void setResultsSuccessReponse(List<MettlResponseBean> resultsSuccessReponse) {
		this.resultsSuccessReponse = resultsSuccessReponse;
	}
	public List<MettlResponseBean> getResultsFailureReponse() {
		return resultsFailureReponse;
	}
	public void setResultsFailureReponse(List<MettlResponseBean> resultsFailureReponse) {
		this.resultsFailureReponse = resultsFailureReponse;
	}
	public List<DemoExamAttendanceBean> getDemoExamAttendanceList() {
		return demoExamAttendanceList;
	}
	public void setDemoExamAttendanceList(List<DemoExamAttendanceBean> demoExamAttendanceList) {
		this.demoExamAttendanceList = demoExamAttendanceList;
	}
	public List<TestQuestionConfigBean> getQuestionConfigBean() {
		return questionConfigBean;
	}
	public void setQuestionConfigBean(List<TestQuestionConfigBean> questionConfigBean) {
		this.questionConfigBean = questionConfigBean;
	}
	
	public List<UpgradTestQuestionExamBean> getUpgradTestQuestionBean() {
		return upgradTestQuestionBean;
	}
	public void setUpgradTestQuestionBean(List<UpgradTestQuestionExamBean> upgradTestQuestionBean) {
		this.upgradTestQuestionBean = upgradTestQuestionBean;
	}
	public UpgradAssessmentExamBean getUpgradAssessmentBean() {
		return upgradAssessmentBean;
	}
	public void setUpgradAssessmentBean(UpgradAssessmentExamBean upgradAssessmentBean) {
		this.upgradAssessmentBean = upgradAssessmentBean;
	}
	public ArrayList<ConsumerProgramStructureExam> getProgramsData() {
		return programData;
	}
	public void setProgramsData(ArrayList<ConsumerProgramStructureExam> programData) {
		this.programData = programData;
	}
	public ArrayList<ConsumerProgramStructureExam> getProgramData() {
		return programData;
	}
	public void setProgramData(ArrayList<ConsumerProgramStructureExam> programData) {
		this.programData = programData;
	}
	public ArrayList<ConsumerProgramStructureExam> getProgramStructureData() {
		return programStructureData;
	}
	public void setProgramStructureData(ArrayList<ConsumerProgramStructureExam> programStructureData) {
		this.programStructureData = programStructureData;
	}
	public ArrayList<ConsumerProgramStructureExam> getSubjectsData() {
		return subjectsData;
	}
	public void setSubjectsData(ArrayList<ConsumerProgramStructureExam> subjectsData) {
		this.subjectsData = subjectsData;
	}
	public ArrayList<StudentExamBean> getStudentsData() {
		return studentsData;
	}
	public void setStudentsData(ArrayList<StudentExamBean> studentsData) {
		this.studentsData = studentsData;
	}
	public ArrayList<BatchExamBean> getBatchData() {
		return batchData;
	}
	public void setBatchData(ArrayList<BatchExamBean> batchData) {
		this.batchData = batchData;
	}
	public ArrayList<StudentExamBean> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentExamBean> studentList) {
		this.studentList = studentList;
	}
	public List<TestExamBean> getDataForReferenceId() {
		return dataForReferenceId;
	}
	public void setDataForReferenceId(List<TestExamBean> dataForReferenceId) {
		this.dataForReferenceId = dataForReferenceId;
	}
	public List<String> getListOfStringData() {
		return listOfStringData;
	}
	public void setListOfStringData(List<String> listOfStringData) {
		this.listOfStringData = listOfStringData;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<TCSMarksBean> getTcsResultsDetails() {
		return tcsResultsDetails;
	}
	public void setTcsResultsDetails(ArrayList<TCSMarksBean> tcsResultsDetails) {
		this.tcsResultsDetails = tcsResultsDetails;
	}

	public ArrayList<MBAXPassFailBean> getMbaXPassFailData() {
		return mbaXPassFailData;
	}
	public void setMbaXPassFailData(ArrayList<MBAXPassFailBean> mbaXPassFailData) {
		this.mbaXPassFailData = mbaXPassFailData;
	}
	public Page<TcsOnlineExamBean> getPage() {
		return page;
	}
	public void setPage(Page<TcsOnlineExamBean> page) {
		this.page = page;
	}
	public List<TcsOnlineExamBean> getTcsOnlineExamBeanList() {
		return tcsOnlineExamBeanList;
	}
	public void setTcsOnlineExamBeanList(List<TcsOnlineExamBean> tcsOnlineExamBeanList) {
		this.tcsOnlineExamBeanList = tcsOnlineExamBeanList;
	}
	public List<ExamBookingTransactionBean> getExamBookingTransactionBeanList() {
		return examBookingTransactionBeanList;
	}
	public void setExamBookingTransactionBeanList(List<ExamBookingTransactionBean> examBookingTransactionBeanList) {
		this.examBookingTransactionBeanList = examBookingTransactionBeanList;
	}
	


	

}
