package com.nmims.timeline.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="comment_reactions") 
public class CommentReactions implements Serializable {
	
	/**
	 * reactionId, postId, userId, reactionType, createdDate, lastModifiedDate, reactionId, id
	 */
	private static final long serialVersionUID = 1L;

	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int reactionId;
	
	String userId;
	String reactionType;
	Integer postId;
	String createdDate;
	String lastModifiedDate;
	Integer id;
	
	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}

	@Transient
	int reactionCount;
	

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "commentId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="commentId")
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("commentId")
    private Comments comment;
	
    
    
	public Integer getPostId() {
		return postId;
	}



	public void setPostId(Integer postId) {
		this.postId = postId;
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



	public Comments getComment() {
		return comment;
	}



	public void setComment(Comments comment) {
		this.comment = comment;
	}

	public int getReactionId() {
		return reactionId;
	}

	

	public String getUserId() {
		return userId;
	}

	public String getReactionType() {
		return reactionType;
	}

	public int getReactionCount() {
		return reactionCount;
	}

	public void setReactionId(int reactionId) {
		this.reactionId = reactionId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setReactionType(String reactionType) {
		this.reactionType = reactionType;
	}

	public void setReactionCount(int reactionCount) {
		this.reactionCount = reactionCount;
	}

	


	

	
}
