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
@Table(name="post_reactions", schema="lti") 
public class PostReactions implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int reactionId;
	
	String userId;
	String reactionType;
	
	@Transient
	int reactionCount;
	
	@Transient
	String fullName;
	

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "postId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="postId")
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("postId")
    private Post post;
    
    
	
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public int getReactionCount() {
		return reactionCount;
	}
	public void setReactionCount(int reactionCount) {
		this.reactionCount = reactionCount;
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
	
	
	public void setReactionId(int reactionId) {
		this.reactionId = reactionId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public void setReactionType(String reactionType) {
		this.reactionType = reactionType;
	}
	@Override
	public String toString() {
		return "PostReactions [reactionId=" + reactionId + ", userId=" + userId + ", reactionType=" + reactionType
				+ ", reactionCount=" + reactionCount + ", fullName=" + fullName + ", post=" + post + "]";
	}
	
	
	
}
