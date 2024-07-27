package com.nmims.beans;
import java.io.Serializable;
@SuppressWarnings("serial")
public class SessionTrackBean implements Serializable{
	
	private String id;
	private String track;
	private String hexCode;
	private String border;
	private String colorClass;
	private String fontColor;
	private String active;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	public String getHexCode() {
		return hexCode;
	}
	public void setHexCode(String hexCode) {
		this.hexCode = hexCode;
	}
	public String getBorder() {
		return border;
	}
	public void setBorder(String border) {
		this.border = border;
	}
	public String getColorClass() {
		return colorClass;
	}
	public void setColorClass(String colorClass) {
		this.colorClass = colorClass;
	}
	public String getFontColor() {
		return fontColor;
	}
	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	
	@Override
	public String toString() {
		return "SessionTrackBean [id=" + id + ", track=" + track + ", hexCode=" + hexCode + ", border=" + border
				+ ", colorClass=" + colorClass + ", active=" + active + "]";
	}
}