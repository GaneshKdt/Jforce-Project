package com.nmims.timeline.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@Table(name="post")
@DynamicUpdate 
public class Post implements Serializable{

private static final long serialVersionUID = 1L;
@Id  
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name="post_id")
private String postId;

@Column(name="userId")
private String userId;

@Column(name="subject_config_id")
private Integer subjectConfigId;
private String role; 
private String type; 

@NotBlank(message = "please fill content") 
private String content;
private String fileName;
private String FilePath;
private String FileType;
private String referenceId;
private String visibility;

private Integer acadYear;
private String acadMonth;
private Integer examYear;
private String examMonth;

private String url;


 
@Column(name="scheduledDate")
private String scheduledDate;

private String scheduledTime;

@Column(name="scheduleFlag")
private String scheduleFlag="N";

private String session_plan_module_id;
private String group_id;

private String embedImage;
private String embedUrl;
private String embedDescription;
private String embedTitle;
private String hashtags;

private String createdBy;
private String lastModifiedBy;

//For Session
private String sessionDate;
private String subject;
private String videolink;
private String thumbnailUrl;
private String mobileUrlHd;

//For Announcements
 
private String startDate;
 
private String endDate;
private String active;
private String category;
private String attachment1;
private String attachment2;
private String attachment3;



//For faculty details
@javax.persistence.Transient
private String firstName;
@javax.persistence.Transient
private String lastName;
@javax.persistence.Transient
private String profilePicFilePath;

@javax.persistence.Transient
private String consumerType;

@javax.persistence.Transient 
private String programStructure;

@javax.persistence.Transient
private String program;

@javax.persistence.Transient
private List<Comments> comments;

@javax.persistence.Transient
private Integer timeboundId;

@javax.persistence.Transient
private String studentType;

@javax.persistence.Transient
SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  

@OneToMany(cascade = CascadeType.ALL,
        fetch = FetchType.EAGER,
        mappedBy = "post",
        orphanRemoval = true)
@OrderBy("commentId")
private List<Comments> postComments = new LinkedList<>();


@OneToMany(cascade = CascadeType.ALL,
        fetch = FetchType.EAGER,
        mappedBy = "post",
        orphanRemoval = true)
@OrderBy("reactionId")
private Set<PostReactions> postReactions = new HashSet<>();


 
private String createdDate;

 
private String lastModifiedDate;

private Integer duration;




public Integer getDuration() {
	return duration;
}

public void setDuration(Integer duration) {
	this.duration = duration;
}

public String getCreatedDate() {
	return getUnixTimeFromStringDate(createdDate);
}

public String getUnixTimeFromStringDate(String dateInString) {
	if(!StringUtils.isBlank(dateInString)) {

		try {
			Date date = sdf.parse(dateInString);
			return date.getTime()+"";
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return null;
		}	
	}else {
		return null;
	}
}

public void setCreatedDate(String createdDate) {
	this.createdDate = createdDate;
}

public String getLastModifiedDate() {
	return getUnixTimeFromStringDate(lastModifiedDate);
}

public void setLastModifiedDate(String lastModifiedDate) {
	this.lastModifiedDate = lastModifiedDate;
}

public Integer getTimeboundId() {
	return timeboundId;
}

public void setTimeboundId(Integer timeboundId) {
	this.timeboundId = timeboundId;
}

public Set<PostReactions> getPostReactions() {
	return postReactions;
}

public void setPostReactions(Set<PostReactions> postReactions) {
	this.postReactions = postReactions;
}

public List<Comments> getPostComments() {
	return postComments;
}

public void setPostComments(LinkedList<Comments> postComments) {
	this.postComments = postComments;
}

@Transient
public String getFirstName() {
	return firstName;
}

@Transient
public void setFirstName(String firstName) {
	this.firstName = firstName;
}

@Transient
public String getLastName() {
	return lastName;
}

@Transient
public void setLastName(String lastName) {
	this.lastName = lastName;
}

@Transient
public String getProfilePicFilePath() {
	return profilePicFilePath;
}

@Transient
public void setProfilePicFilePath(String profilePicFilePath) {
	this.profilePicFilePath = profilePicFilePath;
}

public List<Comments> getComments() {
	return comments;
}

public void setComments(List<Comments> comments) {
	this.comments = comments;
}

public String getPostId() {
	return postId;
}

public void setPostId(String postId) {
	this.postId = postId;
}

public String getUserId() {
	return userId;
}

public void setUserId(String userId) {
	this.userId = userId;
}

public Integer getSubjectConfigId() {
	return subjectConfigId;
}

public void setSubjectConfigId(Integer subjectConfigId) {
	this.subjectConfigId = subjectConfigId;
}

public String getRole() {
	return role;
}

public void setRole(String role) {
	this.role = role;
}

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}

public String getContent() {
	return content;
}

public void setContent(String content) {
	this.content = content;
}

public String getFileName() {
	return fileName;
}

public void setFileName(String fileName) {
	this.fileName = fileName;
}

public String getFilePath() {
	return FilePath;
}

public void setFilePath(String filePath) {
	FilePath = filePath;
}

public String getFileType() {
	return FileType;
}

public void setFileType(String fileType) {
	FileType = fileType;
}

public String getReferenceId() {
	return referenceId;
}

public void setReferenceId(String referenceId) {
	this.referenceId = referenceId;
}

public String getVisibility() {
	return visibility;
}

public void setVisibility(String visibility) {
	this.visibility = visibility;
}

public Integer getAcadYear() {
	return acadYear;
}

public void setAcadYear(Integer acadYear) {
	this.acadYear = acadYear;
}

public String getAcadMonth() {
	return acadMonth;
}

public void setAcadMonth(String acadMonth) {
	this.acadMonth = acadMonth;
}

public Integer getExamYear() {
	return examYear;
}

public void setExamYear(Integer examYear) {
	this.examYear = examYear;
}

public String getExamMonth() {
	return examMonth;
}

public void setExamMonth(String examMonth) {
	this.examMonth = examMonth;
}

public String getUrl() {
	return url;
}

public void setUrl(String url) {
	this.url = url;
}

public String getScheduledDate() {
	return getUnixTimeFromStringDate(scheduledDate);
}

public void setScheduledDate(String scheduledDate) {
	this.scheduledDate = scheduledDate;
}

public String getScheduledTime() {
	return scheduledTime;
}

public void setScheduledTime(String scheduledTime) {
	this.scheduledTime = scheduledTime;
}

public String getScheduleFlag() {
	return scheduleFlag;
}

public void setScheduleFlag(String scheduleFlag) {
	this.scheduleFlag = scheduleFlag;
}

public String getSession_plan_module_id() {
	return session_plan_module_id;
}

public void setSession_plan_module_id(String session_plan_module_id) {
	this.session_plan_module_id = session_plan_module_id;
}

public String getGroup_id() {
	return group_id;
}

public void setGroup_id(String group_id) {
	this.group_id = group_id;
}

public String getEmbedImage() {
	return embedImage;
}

public void setEmbedImage(String embedImage) {
	this.embedImage = embedImage;
}

public String getEmbedUrl() {
	return embedUrl;
}

public void setEmbedUrl(String embedUrl) {
	this.embedUrl = embedUrl;
}

public String getEmbedDescription() {
	return embedDescription;
}

public void setEmbedDescription(String embedDescription) {
	this.embedDescription = embedDescription;
}

public String getEmbedTitle() {
	return embedTitle;
}

public void setEmbedTitle(String embedTitle) {
	this.embedTitle = embedTitle;
}

public String getHashtags() {
	return hashtags;
}

public void setHashtags(String hashtags) {
	this.hashtags = hashtags;
}

public String getCreatedBy() {
	return createdBy;
}

public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
}

public String getLastModifiedBy() {
	return lastModifiedBy;
}

public void setLastModifiedBy(String lastModifiedBy) {
	this.lastModifiedBy = lastModifiedBy;
}

public String getSessionDate() {
	return getUnixTimeFromStringDate(sessionDate);
}

public void setSessionDate(String sessionDate) {
	this.sessionDate = sessionDate;
}

public String getSubject() {
	return subject;
}

public void setSubject(String subject) {
	this.subject = subject;
}

public String getVideolink() {
	return videolink;
}

public void setVideolink(String videolink) {
	this.videolink = videolink;
}

public String getThumbnailUrl() {
	return thumbnailUrl;
}

public void setThumbnailUrl(String thumbnailUrl) {
	this.thumbnailUrl = thumbnailUrl;
}

public String getMobileUrlHd() {
	return mobileUrlHd;
}

public void setMobileUrlHd(String mobileUrlHd) {
	this.mobileUrlHd = mobileUrlHd;
}

public String getStartDate() {
	return getUnixTimeFromStringDate(startDate);
}

public void setStartDate(String startDate) {
	this.startDate = startDate;
}

public String getEndDate() {
	return getUnixTimeFromStringDate(endDate);
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

public String getConsumerType() {
	return consumerType;
}

public void setConsumerType(String consumerType) {
	this.consumerType = consumerType;
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

public String getStudentType() {
	return studentType;
}

public void setStudentType(String studentType) {
	this.studentType = studentType;
}

@Override
public String toString() {
	return "\nPost [postId=" + postId + ", userId=" + userId + ", subjectConfigId=" + subjectConfigId + ", role="
			+ role + ", type=" + type + ", content=" + content + ", fileName=" + fileName + ", FilePath=" + FilePath
			+ ", FileType=" + FileType + ", referenceId=" + referenceId + ", visibility=" + visibility + ", acadYear="
			+ acadYear + ", acadMonth=" + acadMonth + ", examYear=" + examYear + ", examMonth=" + examMonth + ", url="
			+ url + ", scheduledDate=" + scheduledDate + ", scheduledTime=" + scheduledTime + ", scheduleFlag="
			+ scheduleFlag + ", session_plan_module_id=" + session_plan_module_id + ", group_id=" + group_id
			+ ", embedImage=" + embedImage + ", embedUrl=" + embedUrl + ", embedDescription=" + embedDescription
			+ ", embedTitle=" + embedTitle + ", hashtags=" + hashtags + ", createdBy=" + createdBy + ", lastModifiedBy="
			+ lastModifiedBy + ", sessionDate=" + sessionDate + ", subject=" + subject + ", videolink=" + videolink
			+ ", thumbnailUrl=" + thumbnailUrl + ", mobileUrlHd=" + mobileUrlHd + ", startDate=" + startDate
			+ ", endDate=" + endDate + ", active=" + active + ", category=" + category + ", attachment1=" + attachment1
			+ ", attachment2=" + attachment2 + ", attachment3=" + attachment3 + ", firstName=" + firstName
			+ ", lastName=" + lastName + ", profilePicFilePath=" + profilePicFilePath + ", consumerType=" + consumerType
			+ ", programStructure=" + programStructure + ", program=" + program + ", comments=" + comments
			+ ", postComments Size=" + getPostCommentsSize((LinkedList<Comments>)postComments) + "]\n";
}

private String getPostCommentsSize(LinkedList<Comments> postComments2) {
	if(postComments2 != null) {
		return ((LinkedList<Comments>)postComments2).size()+".";
	}else {
		return 0+" postComments is null. ";
	}
}




}
