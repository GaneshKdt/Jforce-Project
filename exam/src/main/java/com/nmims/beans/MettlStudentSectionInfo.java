package com.nmims.beans;
import java.io.Serializable;
import java.util.List;
public class MettlStudentSectionInfo implements Serializable  {
	private String sapid;
	private String student_name;
	private String firstName;
	private String lastName;
	private String subject;
	private String scheduleAccessKey;
	private String sectionName;
	private int sectionNumber;
	private double totalMarks;
	private double maxMarks;
	private int timeTaken;
	private int totalQuestion;
	private int totalCorrectAnswers;
	private int totalUnAnswered;
	private boolean benfitOfDoubtSection;
	private double bonusMarks;
	
	private List<MettlSectionQuestionResponse> questionsForSection;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getScheduleAccessKey() {
		return scheduleAccessKey;
	}
	public void setScheduleAccessKey(String scheduleAccessKey) {
		this.scheduleAccessKey = scheduleAccessKey;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public int getSectionNumber() {
		return sectionNumber;
	}
	public void setSectionNumber(int sectionNumber) {
		this.sectionNumber = sectionNumber;
	}
	public double getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(double totalMarks) {
		this.totalMarks = totalMarks;
	}
	public double getMaxMarks() {
		return maxMarks;
	}
	public void setMaxMarks(double maxMarks) {
		this.maxMarks = maxMarks;
	}
	public int getTimeTaken() {
		return timeTaken;
	}
	public void setTimeTaken(int timeTaken) {
		this.timeTaken = timeTaken;
	}
	public int getTotalQuestion() {
		return totalQuestion;
	}
	public void setTotalQuestion(int totalQuestion) {
		this.totalQuestion = totalQuestion;
	}
	public int getTotalCorrectAnswers() {
		return totalCorrectAnswers;
	}
	public void setTotalCorrectAnswers(int totalCorrectAnswers) {
		this.totalCorrectAnswers = totalCorrectAnswers;
	}
	public int getTotalUnAnswered() {
		return totalUnAnswered;
	}
	public void setTotalUnAnswered(int totalUnAnswered) {
		this.totalUnAnswered = totalUnAnswered;
	}
	public boolean isBenfitOfDoubtSection() {
		return benfitOfDoubtSection;
	}
	public void setBenfitOfDoubtSection(boolean benfitOfDoubtSection) {
		this.benfitOfDoubtSection = benfitOfDoubtSection;
	}
	public double getBonusMarks() {
		return bonusMarks;
	}
	public void setBonusMarks(double bonusMarks) {
		this.bonusMarks = bonusMarks;
	}
	public List<MettlSectionQuestionResponse> getQuestionsForSection() {
		return questionsForSection;
	}
	public void setQuestionsForSection(List<MettlSectionQuestionResponse> questionsForSection) {
		this.questionsForSection = questionsForSection;
	}
	public String getStudent_name() {
		return student_name;
	}
	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}
}
