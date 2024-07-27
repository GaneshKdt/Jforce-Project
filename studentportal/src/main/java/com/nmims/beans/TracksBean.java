package com.nmims.beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TracksBean implements Serializable {
	
	private int id;
	private String track;
	private String border;
	private String fontColor;
	private String active;
	private String hexCode;
	private String colorClass;
	
	public TracksBean() {
		super();
	}

	public int getId() {
		return id;
	}

	public String getTrack() {
		return track;
	}

	public String getBorder() {
		return border;
	}

	public String getFontColor() {
		return fontColor;
	}

	public String getActive() {
		return active;
	}

	public String getHexCode() {
		return hexCode;
	}

	public String getColorClass() {
		return colorClass;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public void setHexCode(String hexCode) {
		this.hexCode = hexCode;
	}

	public void setColorClass(String colorClass) {
		this.colorClass = colorClass;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TracksBean [id=");
		builder.append(id);
		builder.append(", track=");
		builder.append(track);
		builder.append(", border=");
		builder.append(border);
		builder.append(", fontColor=");
		builder.append(fontColor);
		builder.append(", active=");
		builder.append(active);
		builder.append(", hexCode=");
		builder.append(hexCode);
		builder.append(", colorClass=");
		builder.append(colorClass);
		builder.append("]");
		return builder.toString();
	}
	
}