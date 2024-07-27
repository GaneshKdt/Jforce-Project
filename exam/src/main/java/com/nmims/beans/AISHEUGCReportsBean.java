package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AISHEUGCReportsBean extends StudentExamBean implements Serializable {
	
	
	private String enrollmentMonth;
	private String enrollmentYear;
	private String gender;
	private String program;
	private String Sem;
	private String total;
	private String girlsTotal;
	private String totalPass;
	private String girlsPass;
	private String totalForPg;    
	private String girlsForPg;
	private String totalForDiploma;
	private String girlsForDiploma;
	private String totalForCertificate;
	private String girlsForCertificate;
	private String totaForMba;
	private String girlsForMba;
	private String totalPassPg;
	private String girlspassPg;
	private String totalPassDiploma;
	private String girlsPassDiploma;
	private String totalPassCertificate;
	private String girlsPassCertificate;
	private String totaPassMba;
	private String girlsPassMba;
	private String totalPg;
	private String girlsPg;
	private Integer totalDiploma;
	private String girlsDiploma;
	private String totalCertificate;
	private String girlsCertificate;
	private String totalMba;
	private String girlsMba;
	private String sem;
	private String sapid;
    private String totalMarks;
    private Integer totalAbove60PercentageDiploma;
    private Integer girlsAbove60PercentageDiploma;
    private Integer totalAbove60PercentagePg;
    private Integer girlsAbove60PercentagePg;
	private Integer noOfApplicablesubject;
	private Integer totalAbove60PercentageCertificate;
    private Integer girlsAbove60PercentageCertificate;
    private Integer totalAbove60PercentageMba;
    private Integer girlsAbove60PercentageMba;
	private String semProgramStructureId;
	private String consumerProgramStructureId;
	private String resultProcessedyear;
	private String resultProcessedMonth;
	private String ProgramId;
	private String FirstLetterOfProgram;
	
	
	
	

	private List<AISHEUGCReportsBean> findStudentList = new ArrayList< AISHEUGCReportsBean>();
	
	
	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}
	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}
	public String getEnrollmentYear() {
		return enrollmentYear;
	}
	public void setEnrollmentYear(String enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getSem() {
		return Sem;
	}
	public void setSem(String Sem) {
		this.Sem = Sem;
	}
	
	public String getTotalForPg() {
		return totalForPg;
	}
	public void setTotalForPg(String totalForPg) {
		this.totalForPg = totalForPg;
	}
	public String getGirlsForPg() {
		return girlsForPg;
	}
	public void setGirlsForPg(String girlsForPg) {
		this.girlsForPg = girlsForPg;
	}
	public String getTotalForDiploma() {
		return totalForDiploma;
	}
	public void setTotalForDiploma(String totalForDiploma) {
		this.totalForDiploma = totalForDiploma;
	}
	public String getGirlsForDiploma() {
		return girlsForDiploma;
	}
	public void setGirlsForDiploma(String girlsForDiploma) {
		this.girlsForDiploma = girlsForDiploma;
	}
	
	public String getTotalForCertificate() {
		return totalForCertificate;
	}
	public void setTotalForCertificate(String totalForCertificate) {
		this.totalForCertificate = totalForCertificate;
	}
	public String getGirlsForCertificate() {
		return girlsForCertificate;
	}
	public void setGirlsForCertificate(String girlsForCertificate) {
		this.girlsForCertificate = girlsForCertificate;
	}
	
	
	public String getTotaForMba() {
		return totaForMba;
	}
	public void setTotaForMba(String totaForMba) {
		this.totaForMba = totaForMba;
	}
	public String getGirlsForMba() {
		return girlsForMba;
	}
	public void setGirlsForMba(String girlsForMba) {
		this.girlsForMba = girlsForMba;
		}
	
	
	public List<AISHEUGCReportsBean> getFindStudentList() {
		return findStudentList;
	}
	public void setFindStudentList(List<AISHEUGCReportsBean> findStudentList) {
		this.findStudentList = findStudentList;
	}
	
	
	public String getResultProcessedyear() {
		return resultProcessedyear;
	}
	public void setResultProcessedyear(String resultProcessedyear) {
		this.resultProcessedyear = resultProcessedyear;
	}
	public String getResultProcessedMonth() {
		return resultProcessedMonth;
	}
	public void setResultProcessedMonth(String resultProcessedMonth) {
		this.resultProcessedMonth = resultProcessedMonth;
	}
	public String getTotalPassPg() {
		return totalPassPg;
	}
	public void setTotalPassPg(String totalPassPg) {
		this.totalPassPg = totalPassPg;
	}
	public String getGirlspassPg() {
		return girlspassPg;
	}
	public void setGirlspassPg(String girlspassPg) {
		this.girlspassPg = girlspassPg;
	}
	public String getTotalPassDiploma() {
		return totalPassDiploma;
	}
	public void setTotalPassDiploma(String totalPassDiploma) {
		this.totalPassDiploma = totalPassDiploma;
	}
	
	
	
	public String getGirlsPassDiploma() {
		return girlsPassDiploma;
	}
	public void setGirlsPassDiploma(String girlsPassDiploma) {
		this.girlsPassDiploma = girlsPassDiploma;
	}
	public String getTotalPassCertificate() {
		return totalPassCertificate;
	}
	public void setTotalPassCertificate(String totalPassCertificate) {
		this.totalPassCertificate = totalPassCertificate;
	}
	public String getGirlsPassCertificate() {
		return girlsPassCertificate;
	}
	public void setGirlsPassCertificate(String girlsPassCertificate) {
		this.girlsPassCertificate = girlsPassCertificate;
	}
	public String getTotaPassMba() {
		return totaPassMba;
	}
	public void setTotaPassMba(String totaPassMba) {
		this.totaPassMba = totaPassMba;
	}
	public String getGirlsPassMba() {
		return girlsPassMba;
	}
	public void setGirlsPassMba(String girlsPassMba) {
		this.girlsPassMba = girlsPassMba;
	}
	
	
	public String getTotalPg() {
		return totalPg;
	}
	public void setTotalPg(String totalPg) {
		this.totalPg = totalPg;
	}
	public String getGirlsPg() {
		return girlsPg;
	}
	public void setGirlsPg(String girlsPg) {
		this.girlsPg = girlsPg;
	}
	public Integer getTotalDiploma() {
		return totalDiploma;
	}
	public void setTotalDiploma(Integer integer) {
		this.totalDiploma = integer;
	}
	public String getGirlsDiploma() {
		return girlsDiploma;
	}
	public void setGirlsDiploma(String girlsDiploma) {
		this.girlsDiploma = girlsDiploma;
	}
	public String getTotalCertificate() {
		return totalCertificate;
	}
	public void setTotalCertificate(String totalCertificate) {
		this.totalCertificate = totalCertificate;
	}
	public String getGirlsCertificate() {
		return girlsCertificate;
	}
	public void setGirlsCertificate(String girlsCertificate) {
		this.girlsCertificate = girlsCertificate;
	}
	public String getTotalMba() {
		return totalMba;
	}
	public void setTotalMba(String totalMba) {
		this.totalMba = totalMba;
	}
	public String getGirlsMba() {
		return girlsMba;
	}
	public void setGirlsMba(String girlsMba) {
		this.girlsMba = girlsMba;
	}
	
	public String getSemProgramStructureId() {
		return semProgramStructureId;
	}
	public void setSemProgramStructureId(String semProgramStructureId) {
		this.semProgramStructureId = semProgramStructureId;
	}
	
	public Integer getNoOfApplicablesubject() {
		return noOfApplicablesubject;
	}
	public void setNoOfApplicablesubject(Integer noOfApplicablesubject) {
		this.noOfApplicablesubject = noOfApplicablesubject;
	}
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	
	public String getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(String totalMarks) {
		this.totalMarks = totalMarks;
	}
	
	
	public Integer getTotalAbove60PercentageDiploma() {
		return totalAbove60PercentageDiploma;
	}
	public void setTotalAbove60PercentageDiploma(Integer totalAbove60PercentageDiploma) {
		this.totalAbove60PercentageDiploma = totalAbove60PercentageDiploma;
	}
	public Integer getGirlsAbove60PercentageDiploma() {
		return girlsAbove60PercentageDiploma;
	}
	public void setGirlsAbove60PercentageDiploma(Integer girlsAbove60PercentageDiploma) {
		this.girlsAbove60PercentageDiploma = girlsAbove60PercentageDiploma;
	}
	
	public Integer getTotalAbove60PercentagePg() {
		return totalAbove60PercentagePg;
	}
	public void setTotalAbove60PercentagePg(Integer totalAbove60PercentagePg) {
		this.totalAbove60PercentagePg = totalAbove60PercentagePg;
	}
	public Integer getGirlsAbove60PercentagePg() {
		return girlsAbove60PercentagePg;
	}
	public void setGirlsAbove60PercentagePg(Integer girlsAbove60PercentagePg) {
		this.girlsAbove60PercentagePg = girlsAbove60PercentagePg;
	}
	
	
	public Integer getTotalAbove60PercentageCertificate() {
		return totalAbove60PercentageCertificate;
	}
	public void setTotalAbove60PercentageCertificate(Integer totalAbove60PercentageCertificate) {
		this.totalAbove60PercentageCertificate = totalAbove60PercentageCertificate;
	}
	public Integer getGirlsAbove60PercentageCertificate() {
		return girlsAbove60PercentageCertificate;
	}
	public void setGirlsAbove60PercentageCertificate(Integer girlsAbove60PercentageCertificate) {
		this.girlsAbove60PercentageCertificate = girlsAbove60PercentageCertificate;
	}
	public Integer getTotalAbove60PercentageMba() {
		return totalAbove60PercentageMba;
	}
	public void setTotalAbove60PercentageMba(Integer totalAbove60PercentageMba) {
		this.totalAbove60PercentageMba = totalAbove60PercentageMba;
	}
	
	public Integer getGirlsAbove60PercentageMba() {
		return girlsAbove60PercentageMba;
	}
	public void setGirlsAbove60PercentageMba(Integer girlsAbove60PercentageMba) {
		this.girlsAbove60PercentageMba = girlsAbove60PercentageMba;
	}
	
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getGirlsTotal() {
		return girlsTotal;
	}
	public void setGirlsTotal(String girlsTotal) {
		this.girlsTotal = girlsTotal;
	}
	
	public String getTotalPass() {
		return totalPass;
	}
	public void setTotalPass(String totalPass) {
		this.totalPass = totalPass;
	}
	public String getGirlsPass() {
		return girlsPass;
	}
	public void setGirlsPass(String girlsPass) {
		this.girlsPass = girlsPass;
	}
	
	public String getProgramId() {
		return ProgramId;
	}
	public void setProgramId(String programId) {
		ProgramId = programId;
	}
	
	
	public String getFirstLetterOfProgram() {
		return FirstLetterOfProgram;
	}
	public void setFirstLetterOfProgram(String firstLetterOfProgram) {
		FirstLetterOfProgram = firstLetterOfProgram;
	}
	@Override
	public String toString() {
		return "AISHEUGCReportsBean [enrollmentMonth=" + enrollmentMonth + ", enrollmentYear=" + enrollmentYear
				+ ", gender=" + gender + ", program=" + program + ", Sem=" + Sem + ", total=" + total + ", girlsTotal="
				+ girlsTotal + ", totalPass=" + totalPass + ", girlsPass=" + girlsPass + ", totalForPg=" + totalForPg
				+ ", girlsForPg=" + girlsForPg + ", totalForDiploma=" + totalForDiploma + ", girlsForDiploma="
				+ girlsForDiploma + ", totalForCertificate=" + totalForCertificate + ", girlsForCertificate="
				+ girlsForCertificate + ", totaForMba=" + totaForMba + ", girlsForMba=" + girlsForMba + ", totalPassPg="
				+ totalPassPg + ", girlspassPg=" + girlspassPg + ", totalPassDiploma=" + totalPassDiploma
				+ ", girlsPassDiploma=" + girlsPassDiploma + ", totalPassCertificate=" + totalPassCertificate
				+ ", girlsPassCertificate=" + girlsPassCertificate + ", totaPassMba=" + totaPassMba + ", girlsPassMba="
				+ girlsPassMba + ", totalPg=" + totalPg + ", girlsPg=" + girlsPg + ", totalDiploma=" + totalDiploma
				+ ", girlsDiploma=" + girlsDiploma + ", totalCertificate=" + totalCertificate + ", girlsCertificate="
				+ girlsCertificate + ", totalMba=" + totalMba + ", girlsMba=" + girlsMba + ", sem=" + sem + ", sapid="
				+ sapid + ", totalMarks=" + totalMarks + ", totalAbove60PercentageDiploma="
				+ totalAbove60PercentageDiploma + ", girlsAbove60PercentageDiploma=" + girlsAbove60PercentageDiploma
				+ ", totalAbove60PercentagePg=" + totalAbove60PercentagePg + ", girlsAbove60PercentagePg="
				+ girlsAbove60PercentagePg + ", noOfApplicablesubject=" + noOfApplicablesubject
				+ ", totalAbove60PercentageCertificate=" + totalAbove60PercentageCertificate
				+ ", girlsAbove60PercentageCertificate=" + girlsAbove60PercentageCertificate
				+ ", totalAbove60PercentageMba=" + totalAbove60PercentageMba + ", girlsAbove60PercentageMba="
				+ girlsAbove60PercentageMba + ", semProgramStructureId=" + semProgramStructureId
				+ ", consumerProgramStructureId=" + consumerProgramStructureId + ", resultProcessedyear="
				+ resultProcessedyear + ", resultProcessedMonth=" + resultProcessedMonth + ", ProgramId=" + ProgramId
				+ ", FirstLetterOfProgram=" + FirstLetterOfProgram + ", findStudentList=" + findStudentList + "]";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
	

