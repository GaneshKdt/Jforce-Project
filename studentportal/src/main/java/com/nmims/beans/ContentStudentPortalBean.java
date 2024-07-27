package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * old name - ContentBean
 * @author
 *
 */
public class ContentStudentPortalBean  implements Serializable{
	private String id;
	private String name;
	private String subject;
	private String description;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String filePath;
	private CommonsMultipartFile fileData;
	private String previewPath;
	private String webFileurl;
	private String urlType;
	private String contentType;
	private String fileName;
	private String year;
	private String month;
	private String programStructure;
	private String documentPath;
	
	private String toYear;
	private String toMonth;
	
	private ArrayList<String> contentToTransfer = new ArrayList<>();
	
	private String bookmarked;
	private String url;
	private String sapId;
	
	
	public String getDocumentPath() {
		return documentPath;
	}
	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public ArrayList<String> getContentToTransfer() {
		return contentToTransfer;
	}
	public void setContentToTransfer(ArrayList<String> contentToTransfer) {
		this.contentToTransfer = contentToTransfer;
	}
	public String getToYear() {
		return toYear;
	}
	public void setToYear(String toYear) {
		this.toYear = toYear;
	}
	public String getToMonth() {
		return toMonth;
	}
	public void setToMonth(String toMonth) {
		this.toMonth = toMonth;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getUrlType() {
		return urlType;
	}
	public void setUrlType(String urlType) {
		this.urlType = urlType;
	}
	public String getWebFileurl() {
		return webFileurl;
	}
	public void setWebFileurl(String webFileurl) {
		this.webFileurl = webFileurl;
	}
	public String getPreviewPath() {
		return previewPath;
	}
	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	public String getBookmarked() {
		return bookmarked;
	}
	public void setBookmarked(String bookmarked) {
		this.bookmarked = bookmarked;
	}	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	@Override
	public String toString() {
		return "ContentStudentPortalBean [id=" + id + ", name=" + name + ", subject=" + subject + ", description=" + description
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", filePath=" + filePath + ", fileData=" + fileData
				+ ", previewPath=" + previewPath + ", webFileurl=" + webFileurl + ", urlType=" + urlType
				+ ", contentType=" + contentType + ", fileName=" + fileName + ", year=" + year + ", month=" + month
				+ ", programStructure=" + programStructure + ", documentPath=" + documentPath + ", toYear=" + toYear
				+ ", toMonth=" + toMonth + ", contentToTransfer=" + contentToTransfer + ", bookmarked=" + bookmarked
				+ ", url=" + url + ", sapId=" + sapId + "]";
	}
}
