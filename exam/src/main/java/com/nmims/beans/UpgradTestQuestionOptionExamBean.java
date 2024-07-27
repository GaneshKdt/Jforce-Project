package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

//spring security related changes rename UpgradTestQuestionOptionBean to UpgradTestQuestionOptionExamBean
public class UpgradTestQuestionOptionExamBean implements Serializable {
	private Long optionId;
	private Long questionNo;
	private String optionData;
	private String isCorrect;
	private String selected;
	private String createdBy;
	private Date createdDate;
	private String lastModifiedBy;
	private Date lastModifiedDate;
	
	
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Long getOptionId() {
		return optionId;
	}
	public void setOptionId(Long optionId) {
		this.optionId = optionId;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	
	
	
	public Long getQuestionNo() {
		return questionNo;
	}
	public void setQuestionNo(Long questionNo) {
		this.questionNo = questionNo;
	}
	public String getOptionData() {
		return optionData;
	}
	public void setOptionData(String optionData) {
		this.optionData = optionData;
	}
	public String getIsCorrect() {
		return isCorrect;
	}
	public void setIsCorrect(String isCorrect) {
		this.isCorrect = isCorrect;
	}
	@Override
	public String toString() {
		return "[option:"+optionData+",isCorrect:"+isCorrect+"]";
	}
}
