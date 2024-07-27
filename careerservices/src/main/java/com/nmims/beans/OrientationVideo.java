package com.nmims.beans;

import java.io.Serializable;

public class OrientationVideo  implements Serializable{

	private String name;
	private String thumbnailUrl;
	private String playUrl;
	private String playUrl360;
	private String playUrl480;
	private String playUrl720;
	private String description;
	
	
	public String getPlayUrl360() {
		return playUrl360;
	}
	public void setPlayUrl360(String playUrl360) {
		this.playUrl360 = playUrl360;
	}
	public String getPlayUrl480() {
		return playUrl480;
	}
	public void setPlayUrl480(String playUrl480) {
		this.playUrl480 = playUrl480;
	}
	public String getPlayUrl720() {
		return playUrl720;
	}
	public void setPlayUrl720(String playUrl720) {
		this.playUrl720 = playUrl720;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getPlayUrl() {
		return playUrl;
	}
	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
