package com.nmims.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="videos")
public class VimeoResponseBean implements Serializable  {
	private Long id;
	private String title;
	private String description;
	private String url;
	private String upload_date;
	private String thumbnail_small;
	private String thumbnail_medium;
	private String thumbnail_large;
	private Long user_id;
	private String user_name;
	private String user_url;
	private String user_portrait_small;
	private String user_portrait_medium;
	private String user_portrait_large;
	private String user_portrait_huge;
	private Integer stats_number_of_likes;
	private Integer stats_number_of_plays;
	private Integer stats_number_of_comments;
	private Integer duration;
	private Integer width;
	private Integer height;
	private String tags;
	private String embed_privacy;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUpload_date() {
		return upload_date;
	}
	public void setUpload_date(String upload_date) {
		this.upload_date = upload_date;
	}
	public String getThumbnail_small() {
		return thumbnail_small;
	}
	public void setThumbnail_small(String thumbnail_small) {
		this.thumbnail_small = thumbnail_small;
	}
	public String getThumbnail_medium() {
		return thumbnail_medium;
	}
	public void setThumbnail_medium(String thumbnail_medium) {
		this.thumbnail_medium = thumbnail_medium;
	}
	public String getThumbnail_large() {
		return thumbnail_large;
	}
	public void setThumbnail_large(String thumbnail_large) {
		this.thumbnail_large = thumbnail_large;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_url() {
		return user_url;
	}
	public void setUser_url(String user_url) {
		this.user_url = user_url;
	}
	public String getUser_portrait_small() {
		return user_portrait_small;
	}
	public void setUser_portrait_small(String user_portrait_small) {
		this.user_portrait_small = user_portrait_small;
	}
	public String getUser_portrait_medium() {
		return user_portrait_medium;
	}
	public void setUser_portrait_medium(String user_portrait_medium) {
		this.user_portrait_medium = user_portrait_medium;
	}
	public String getUser_portrait_large() {
		return user_portrait_large;
	}
	public void setUser_portrait_large(String user_portrait_large) {
		this.user_portrait_large = user_portrait_large;
	}
	public String getUser_portrait_huge() {
		return user_portrait_huge;
	}
	public void setUser_portrait_huge(String user_portrait_huge) {
		this.user_portrait_huge = user_portrait_huge;
	}
	public Integer getStats_number_of_likes() {
		return stats_number_of_likes;
	}
	public void setStats_number_of_likes(Integer stats_number_of_likes) {
		this.stats_number_of_likes = stats_number_of_likes;
	}
	public Integer getStats_number_of_plays() {
		return stats_number_of_plays;
	}
	public void setStats_number_of_plays(Integer stats_number_of_plays) {
		this.stats_number_of_plays = stats_number_of_plays;
	}
	public Integer getStats_number_of_comments() {
		return stats_number_of_comments;
	}
	public void setStats_number_of_comments(Integer stats_number_of_comments) {
		this.stats_number_of_comments = stats_number_of_comments;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getEmbed_privacy() {
		return embed_privacy;
	}
	public void setEmbed_privacy(String embed_privacy) {
		this.embed_privacy = embed_privacy;
	}
	
	
}
