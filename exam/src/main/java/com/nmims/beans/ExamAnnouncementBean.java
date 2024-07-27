package com.nmims.beans;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

//spring security related changes rename AnnouncementBean to ExamAnnouncementBean 
public class ExamAnnouncementBean implements Serializable{
	
	public ExamAnnouncementBean() {
		super();
	}
	private String id;
	private String subject;
	private String description;
	private String shortDescription;
	private String startDate;
	private String endDate;
	private String active;
	private String category;
	private String descriptionForDisplay;
	
	private String attachment1;
	private String attachment2;
	private String attachment3;
	
	private String attachmentFile1Path;
	private String attachmentFile2Path;
	private String attachmentFile3Path;
	
	private String attachmentFile1Name;
	private String attachmentFile2Name;
	private String attachmentFile3Name;
	
	private String programType;
	private String program;
	private String programStructure;
	
	public void setProgramType(String p){
		this.programType = p;
	}
	public String getProgramType(){
		return programType;
	}
	public String getAttachment1() {
		return attachment1;
	}
	public void setAttachment1(String attachment1) {
		this.attachment1 = attachment1;
	}
	public String getAttachment2() {
		return attachment2;
	}
	public void setAttachment2(String attachment2) {
		this.attachment2 = attachment2;
	}
	public String getAttachment3() {
		return attachment3;
	}
	public void setAttachment3(String attachment3) {
		this.attachment3 = attachment3;
	}
	public String getAttachmentFile1Path() {
		return attachmentFile1Path;
	}
	public void setAttachmentFile1Path(String attachmentFile1Path) {
		this.attachmentFile1Path = attachmentFile1Path;
	}
	public String getAttachmentFile2Path() {
		return attachmentFile2Path;
	}
	public void setAttachmentFile2Path(String attachmentFile2Path) {
		this.attachmentFile2Path = attachmentFile2Path;
	}
	public String getAttachmentFile3Path() {
		return attachmentFile3Path;
	}
	public void setAttachmentFile3Path(String attachmentFile3Path) {
		this.attachmentFile3Path = attachmentFile3Path;
	}
	public String getAttachmentFile1Name() {
		if(attachment1 != null){
			return attachment1.substring(attachment1.lastIndexOf("/")+1, attachment1.length());
		}else{
			return null;
		}
	}
	public void setAttachmentFile1Name(String attachmentFile1Name) {
		this.attachmentFile1Name = attachmentFile1Name;
	}
	public String getAttachmentFile2Name() {
		if(attachment2 != null){
			return attachment2.substring(attachment2.lastIndexOf("/")+1, attachment2.length());
		}else{
			return null;
		}
	}
	public void setAttachmentFile2Name(String attachmentFile2Name) {
		this.attachmentFile2Name = attachmentFile2Name;
	}
	public String getAttachmentFile3Name() {
		if(attachment3 != null){
			return attachment3.substring(attachment3.lastIndexOf("/")+1, attachment3.length());
		}else{
			return null;
		}
	}
	public void setAttachmentFile3Name(String attachmentFile3Name) {
		this.attachmentFile3Name = attachmentFile3Name;
	}
	public String getDescriptionForDisplay() {
		if(description != null){
			String descriptionForDisplay = new String(description);
			return descriptionForDisplay.replaceAll("\n", "<br/>");
		}
		return description;
	}
	public void setDescriptionForDisplay(String descriptionForDisplay) {
		this.descriptionForDisplay = descriptionForDisplay;
	}
	public String getShortDescription() {
		if(this.description != null){
			int endIndex = description.length() > 250 ? 250 : description.length();
			int subStringLastIndex = 0;
			int counter = 0;
			for(int i = 0; (i < endIndex && counter < 250); i++){
				subStringLastIndex = i;
				counter++;
				if(description.charAt(i) == '\n'){
					counter = counter + 100;
				}
			}
			
			boolean moreLinesNeeded = true;
			if(counter >= 250 || endIndex >= 250){
				moreLinesNeeded = false;
			}
			
			
			shortDescription = description.substring(0, subStringLastIndex)+"...";
			if(moreLinesNeeded){
				int noOfNewLines = 0;
				if(subStringLastIndex < 250){
					noOfNewLines = (250 - subStringLastIndex) / 50;
				}
				for(int i = 0; i < noOfNewLines-1; i++){
					shortDescription = shortDescription + "<br/>";
				}
			}
			return shortDescription.replaceAll("\n", "<br/>");
		}else{
			return "";
		}

	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	
	

}
