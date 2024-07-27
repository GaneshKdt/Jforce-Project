package com.nmims.timeline.model;

import java.io.Serializable;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


@Entity
@Table(name="post_comments") 
@DynamicUpdate
public class Comments implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	Integer commentId;

	/*
	 * @Column(name="post_id") String postId;
	 */
	
	String sapid;
	String comment;
	int visibility;
	int master_comment_id;
	

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "postId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="postId")
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("postId")
    private Post post;
	

@OneToMany(cascade = CascadeType.ALL,
        fetch = FetchType.EAGER,
        mappedBy = "comment",
        orphanRemoval = true)
private Set<CommentReactions> commentReactions = new HashSet<>();

	@javax.persistence.Transient
	LinkedList<Comments> replies = new LinkedList<>();
	 
	//added to merge profile details of commenter
	@javax.persistence.Transient
	String firstName;
	@javax.persistence.Transient
	String lastName;
	@javax.persistence.Transient
	String imageUrl;
	@javax.persistence.Transient
	int subcomments_count;
	
	String createdDate;
	
	String lastModifiedDate;
	

@javax.persistence.Transient
SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  

public String getUnixTimeFromStringDate(String dateInString) {
	if(!StringUtils.isBlank(dateInString)) {

		try {
			Date date = sdf.parse(dateInString);
			return date.getTime()+"";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	}else {
		return null;
	}
}
	
	
	
	public String getLastModifiedDate() {
		return getUnixTimeFromStringDate(lastModifiedDate);
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getCreatedDate() {
		return getUnixTimeFromStringDate(createdDate);
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public Set<CommentReactions> getCommentReactions() {
		return commentReactions;
	}
	public void setCommentReactions(Set<CommentReactions> commentReactions) {
		this.commentReactions = commentReactions;
	}
	public int getSubcomments_count() {
		return subcomments_count;
	}
	public void setSubcomments_count(int subcomments_count) {
		this.subcomments_count = subcomments_count;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public LinkedList<Comments> getReplies() {
		return replies;
	}
	public void setReplies(LinkedList<Comments> replies) {
		this.replies = replies;
	}
	
	
	
	/*
	 * public String getPostId() { return postId; } public void setPostId(String
	 * postId) { this.postId = postId; }
	 */
	public String getSapid() {
		return sapid;
	}
	public String getComment() {
		return comment;
	}
	public int getVisibility() {
		return visibility;
	}
	public int getMaster_comment_id() {
		return master_comment_id;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}
	public void setMaster_comment_id(int master_comment_id) {
		this.master_comment_id = master_comment_id;
	}


    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
	public Integer getCommentId() {
		return commentId;
	}
	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}
	
    
	
	


	
	
}
