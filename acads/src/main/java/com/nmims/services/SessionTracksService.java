package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.SessionTrackBean;
import com.nmims.daos.SessionTracksDAO;

@Service("sessionTracksService")
public class SessionTracksService {

	@Autowired 
	SessionTracksDAO sessionTracksDao;
	
	//Purpose to get All tracks Name 
	public ArrayList<String> getTrackNames(){		
		return sessionTracksDao.getTrackNames();
	}
	
	//Purpose to get all Tracks Details like (Track name,color code ,etc.) 
	public ArrayList<SessionTrackBean> getAllTracksDetails(){		
		return sessionTracksDao.getAllTracksDetails();
	}

	/* purpose to update the track details
	 * @param trackBean - SessionTrackBean
	 *  Return - Updated Count. */
	public int updateSessionTrackColor(SessionTrackBean trackBean) {	
		return sessionTracksDao.updateSessionTrackColor(trackBean);		
	}
	
	/* purpose to insert new track color details
	 * @param trackBean - SessionTrackBean
	 *  Return - boolean true on success. */
	public void insertSessionTrackColor(SessionTrackBean trackBean) {	
		String colorclass=trackBean.getTrack().replaceAll("[-_ ]+", "");		
		trackBean.setColorClass(colorclass);
		sessionTracksDao.insertTrackDetails(trackBean);
    }
}
