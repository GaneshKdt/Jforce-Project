package com.nmims.beans;

import java.util.ArrayList;
import java.util.Arrays;

public class FaqQuestionAnswerTableBean {
	
	int id;
	String question;
	String answer;
	String faqGroupId;
	String categoryId;
	String subCategoryId;
	String[] subCatArray;
	ArrayList<FaqQuestionAnswerTableBean> faqSubCategoryList;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getFaqGroupId() {
		return faqGroupId;
	}
	public void setFaqGroupId(String faqGroupId) {
		this.faqGroupId = faqGroupId;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getSubCategoryId() {
		return subCategoryId;
	}
	public void setSubCategoryId(String subCategoryId) {
		this.subCategoryId = subCategoryId;
	}
	
	
	public String[] getSubCatArray() {
		return subCatArray;
	}
	public void setSubCatArray(String[] subCatArray) {
		this.subCatArray = subCatArray;
	}
	
	public ArrayList<FaqQuestionAnswerTableBean> getFaqSubCategoryList() {
		return faqSubCategoryList;
	}
	public void setFaqSubCategoryList(ArrayList<FaqQuestionAnswerTableBean> faqSubCategoryList) {
		this.faqSubCategoryList = faqSubCategoryList;
	}
	@Override
	public String toString() {
		return "FaqQuestionAnswerTableBean [id=" + id + ", question=" + question + ", answer=" + answer
				+ ", faqGroupId=" + faqGroupId + ", categoryId=" + categoryId + ", subCategoryId=" + subCategoryId
				+ ", subCatArray=" + Arrays.toString(subCatArray) + ", faqSubCategoryList=" + faqSubCategoryList + "]";
	}

	
	

}
