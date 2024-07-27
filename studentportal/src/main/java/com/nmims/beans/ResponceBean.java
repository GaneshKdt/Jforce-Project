package com.nmims.beans;

public class ResponceBean {

	private String id;
	private String title;
	private String contentType;
	
	//common
	private String programSemSubjectId;
	private Integer subjectcodeId;
	private String consumerProgramStructureId;
	private String filePath;
	private String facultyFirstName; 
	private String facultyLastName; 
	
	//videotranscript
	private String videoContentId;
	private Integer transcriptNumber;
	private String startTime;
	private String endTime;
	private String transcriptContent;
	private String thumbnailUrl;
	private String videoLink;
	private String mobileUrlHd;
	private String mobileUrlSd1;
	private String mobileUrlSd2;
	private String audioFile;
	
	//pdf
	private String previewPath;
	private Integer pageNumber;
	private String pdfContent;
	private String name;
	private String description;
	
	//qna
	private String query;
	private String queryType;
	private String sapId;
	private String answer;
	private String isPublic;
	private Integer timeBoundId;
	private String createdDate;
	private String lastModifiedDate;
	

	
	public String getFacultyFirstName() {
		return facultyFirstName;
	}
	public void setFacultyFirstName(String facultyFirstName) {
		this.facultyFirstName = facultyFirstName;
	}
	public String getFacultyLastName() {
		return facultyLastName;
	}
	public void setFacultyLastName(String facultyLastName) {
		this.facultyLastName = facultyLastName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getVideoLink() {
		return videoLink;
	}
	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}
	public String getMobileUrlHd() {
		return mobileUrlHd;
	}
	public void setMobileUrlHd(String mobileUrlHd) {
		this.mobileUrlHd = mobileUrlHd;
	}
	public String getMobileUrlSd1() {
		return mobileUrlSd1;
	}
	public void setMobileUrlSd1(String mobileUrlSd1) {
		this.mobileUrlSd1 = mobileUrlSd1;
	}
	public String getMobileUrlSd2() {
		return mobileUrlSd2;
	}
	public void setMobileUrlSd2(String mobileUrlSd2) {
		this.mobileUrlSd2 = mobileUrlSd2;
	}
	public String getAudioFile() {
		return audioFile;
	}
	public void setAudioFile(String audioFile) {
		this.audioFile = audioFile;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}
	public Integer getTimeBoundId() {
		return timeBoundId;
	}
	public void setTimeBoundId(Integer timeBoundId) {
		this.timeBoundId = timeBoundId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProgramSemSubjectId() {
		return programSemSubjectId;
	}
	public void setProgramSemSubjectId(String programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
	}
	public Integer getSubjectcodeId() {
		return subjectcodeId;
	}
	public void setSubjectcodeId(Integer subjectcodeId) {
		this.subjectcodeId = subjectcodeId;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getVideoContentId() {
		return videoContentId;
	}
	public void setVideoContentId(String videoContentId) {
		this.videoContentId = videoContentId;
	}
	public Integer getTranscriptNumber() {
		return transcriptNumber;
	}
	public void setTranscriptNumber(Integer transcriptNumber) {
		this.transcriptNumber = transcriptNumber;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getTranscriptContent() {
		return transcriptContent;
	}
	public void setTranscriptContent(String transcriptContent) {
		this.transcriptContent = transcriptContent;
	}
	public String getPreviewPath() {
		return previewPath;
	}
	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
	}
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getPdfContent() {
		return pdfContent;
	}
	public void setPdfContent(String pdfContent) {
		this.pdfContent = pdfContent;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	@Override
	public String toString() {
		return "ResponceBean [id=" + id + ", title=" + title + ", contentType=" + contentType + ", programSemSubjectId="
				+ programSemSubjectId + ", subjectcodeId=" + subjectcodeId + ", consumerProgramStructureId="
				+ consumerProgramStructureId + ", filePath=" + filePath + ", videoContentId=" + videoContentId
				+ ", transcriptNumber=" + transcriptNumber + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", transcriptContent=" + transcriptContent + ", thumbnailUrl=" + thumbnailUrl + ", videoLink="
				+ videoLink + ", mobileUrlHd=" + mobileUrlHd + ", mobileUrlSd1=" + mobileUrlSd1 + ", mobileUrlSd2="
				+ mobileUrlSd2 + ", audioFile=" + audioFile + ", previewPath=" + previewPath + ", pageNumber="
				+ pageNumber + ", pdfContent=" + pdfContent + ", name=" + name + ", query=" + query + ", queryType="
				+ queryType + ", sapId=" + sapId + ", answer=" + answer + ", isPublic=" + isPublic + ", timeBoundId="
				+ timeBoundId + "]";
	}

	
}
