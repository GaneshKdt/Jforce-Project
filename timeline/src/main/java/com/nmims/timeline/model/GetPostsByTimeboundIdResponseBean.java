package com.nmims.timeline.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.nmims.timeline.model.Student;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPostsByTimeboundIdResponseBean implements Serializable{

private static final long serialVersionUID = 1L;

	private Post post ;
	
	private List<Post> listOfPosts ;
	
	int postCount = 0;

	String status;
	/*
	 * List<Programs> programData; List<ProgramStructure> programStructureData;
	 * List<ProgramSemSubject> subjectsData; List<Faculty> faculties;
	 * List<ConsumerTypes> consumerTypes; List<ProgramSubjectMappingBean>
	 * currentSemSubjectsIdStatusMap; HashMap<String, String> subjects;
	 * 
	 * private List<String> subjectIds ; private List activeSubjects; private List
	 * upcomingSubjects; private List archiveSubjects; private List pendingSubjects;
	 * private List activeUpcomingSubject;
	 * 
	 * List groupsForStudentBySubjectId; List<Hashtag> hashtags; List comments;
	 */
    
    private String title;
    private String image;
    private String description;
    private String serverPath;
    private int id ;
    private int commentCount;
    private int genericTimeBoundId;
    
    private long reactionCount;
    private long alreadyLiked;
    
   // private List<PostReactions>  reactions;
    private List<String> keywordList;
	private Boolean isStudentWorkEx;
    private String myReaction;
	private int timeboundId;
	//List<ProgramSemSubjectBean> applicableSubjects;
	boolean  flag;
	private Student student;
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
	public List<Post> getListOfPosts() {
		return listOfPosts;
	}
	public void setListOfPosts(List<Post> listOfPosts) {
		this.listOfPosts = listOfPosts;
	}
	public int getPostCount() {
		return postCount;
	}
	public void setPostCount(int postCount) {
		this.postCount = postCount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getServerPath() {
		return serverPath;
	}
	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public int getGenericTimeBoundId() {
		return genericTimeBoundId;
	}
	public void setGenericTimeBoundId(int genericTimeBoundId) {
		this.genericTimeBoundId = genericTimeBoundId;
	}
	public long getReactionCount() {
		return reactionCount;
	}
	public void setReactionCount(long reactionCount) {
		this.reactionCount = reactionCount;
	}
	public long getAlreadyLiked() {
		return alreadyLiked;
	}
	public void setAlreadyLiked(long alreadyLiked) {
		this.alreadyLiked = alreadyLiked;
	}
	public List<String> getKeywordList() {
		return keywordList;
	}
	public void setKeywordList(List<String> keywordList) {
		this.keywordList = keywordList;
	}
	public Boolean getIsStudentWorkEx() {
		return isStudentWorkEx;
	}
	public void setIsStudentWorkEx(Boolean isStudentWorkEx) {
		this.isStudentWorkEx = isStudentWorkEx;
	}
	public String getMyReaction() {
		return myReaction;
	}
	public void setMyReaction(String myReaction) {
		this.myReaction = myReaction;
	}
	public int getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(int timeboundId) {
		this.timeboundId = timeboundId;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	
	
	
}
