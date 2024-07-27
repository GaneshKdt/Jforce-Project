package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.UFMNoticeBean;

public interface UFMShowCauseStrategyInterface {

	public List<UFMNoticeBean> readShowCauseList(UFMNoticeBean inputBean) throws Exception;
	
	/**
	 * shivam.pandey.EXT COC - START
	 */
	//To Insert Show Cause Entry In DB
	public void upsertCOCShowCause(UFMNoticeBean bean);
	/**
	 * shivam.pandey.EXT COC - END
	 */
	
	
	/**
	 * shivam.pandey.EXT Disconnect Above - START
	 */
	//To Insert Show Cause Entry In DB
	public void upsertDisconnectAboveShowCause(UFMNoticeBean bean);
	//To Check Uploaded Sapid Have Any Active Booking Or Not
	public void checkUploadedDisconnectAboveShowCauseFile(UFMNoticeBean bean);
	/**
	 * shivam.pandey.EXT Disconnect Above - END
	 */
	
	
	
	/**
	 * shivam.pandey.EXT Disconnect Below - START
	 */
	//To Insert Show Cause Entry In DB
	public void upsertDisconnectBelowShowCause(UFMNoticeBean bean);
	//To Check Uploaded Sapid Have Any Active Booking Or Not
	public void checkUploadedDisconnectBelowShowCauseFile(UFMNoticeBean bean);
	/**
	 * shivam.pandey.EXT Disconnect Below - END
	 */
}
