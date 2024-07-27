package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class SessionPlanGetVideosAPIResponseBean  implements Serializable  {

	private List<VideoContentAcadsBean> sessionVideos;

	public List<VideoContentAcadsBean> getSessionVideos() {
		return sessionVideos;
	}

	public void setSessionVideos(List<VideoContentAcadsBean> sessionVideos) {
		this.sessionVideos = sessionVideos;
	} 
	
	
}
