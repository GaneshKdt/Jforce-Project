package com.nmims.dto;

import java.util.ArrayList;
import java.util.List;

import com.nmims.beans.AnnouncementStudentPortalBean;

public class AnnouncementDto {
	private String masterkey;
	private ArrayList<String> pssIds;
	private List<AnnouncementStudentPortalBean> announcements;
	public String getMasterkey() {
		return masterkey;
	}
	public void setMasterkey(String masterkey) {
		this.masterkey = masterkey;
	}
	public ArrayList<String> getPssIds() {
		return pssIds;
	}
	public void setPssIds(ArrayList<String> pssIds) {
		this.pssIds = pssIds;
	}
	public List<AnnouncementStudentPortalBean> getAnnouncements() {
		return announcements;
	}
	public void setAnnouncements(List<AnnouncementStudentPortalBean> announcements) {
		this.announcements = announcements;
	}
	
}
