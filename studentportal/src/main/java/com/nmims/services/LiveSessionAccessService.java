/**
 * 
 */
package com.nmims.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.daos.LiveSessionAccessDAO;

/**
 * @author vil_m
 *
 */
@Service(value = "LiveSessionAccessService")
public class LiveSessionAccessService {
	
	@Autowired
	LiveSessionAccessDAO liveSessionAccessDAO;
	
	private static final Logger logger = LoggerFactory.getLogger(LiveSessionAccessService.class);

	public List<Integer> fetchPSSforLiveSessionAccess(final String sapId, final String year, final String month) {
		logger.info("LiveSessionAccessService : fetchPSSforLiveSessionAccess");
		List<Integer> list = null;
		list = liveSessionAccessDAO.fetchPSSforLiveSessionAccess(sapId, year, month);
		return list;
	}
	
	public ArrayList<String> getListOfFreeLiveSessionAccessMasterKeys(List<String> timeboundPortalList) {
		logger.info("LiveSessionAccessService : getListOfLiveSessionAccessMasterKeys");
		ArrayList<String> list = null;
		list = liveSessionAccessDAO.getNonPgProgramsList(timeboundPortalList);
		return list;
	}
	
}
