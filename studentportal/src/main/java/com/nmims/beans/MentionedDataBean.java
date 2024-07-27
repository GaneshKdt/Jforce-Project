package com.nmims.beans;

import java.io.Serializable;

public class MentionedDataBean  implements Serializable  {
	private int id;
	private String mention_to;
	private int comment_id;
	private int post_id;
	private String sapid;
	private String comment;
	private String visibility;	
	private String createdDate;
	private String lastModifiedDate;
	private String master_comment_id;
	private String mentionBy;
	
	public String getMentionBy() {
		return mentionBy;
	}
	public void setMentionBy(String mentionBy) {
		this.mentionBy = mentionBy;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMention_to() {
		return mention_to;
	}
	public void setMention_to(String mention_to) {
		this.mention_to = mention_to;
	}
	public int getComment_id() {
		return comment_id;
	}
	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}
	public int getPost_id() {
		return post_id;
	}
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
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
	public String getMaster_comment_id() {
		return master_comment_id;
	}
	public void setMaster_comment_id(String master_comment_id) {
		this.master_comment_id = master_comment_id;
	}
	@Override
	public String toString() {
		return "MentionedDataBean [id=" + id + ", mention_to=" + mention_to + ", comment_id=" + comment_id
				+ ", post_id=" + post_id + ", sapid=" + sapid + ", comment=" + comment + ", visibility=" + visibility
				+ ", createdDate=" + createdDate + ", lastModifiedDate=" + lastModifiedDate + ", master_comment_id="
				+ master_comment_id + ", mentionBy=" + mentionBy + "]";
	}

	

}

