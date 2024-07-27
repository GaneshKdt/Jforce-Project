package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.UFMNoticeBean;

public interface UFMActionStrategyInterface {

	public List<UFMNoticeBean> readShowCauseList(UFMNoticeBean inputBean) throws Exception;
	
	/**
	 * shivam.pandey.EXT COC - START
	 */
	//To Check Any Records Have For Update Or Not
	public void checkUploadedCOCShowCauseFile(UFMNoticeBean bean);
	//To Update Stage or Action Of Student
	public void updateCOCActionRecord(UFMNoticeBean bean);
	/**
	 * shivam.pandey.EXT COC - END
	 */
	
	
	/**
	 * shivam.pandey.EXT Disconnect - START
	 */
	//To Check Any Records Have For Update Or Not
	public void checkUploadedDisconnectShowCauseFile(UFMNoticeBean bean);
	//To Update Stage or Action Of Student
	public void updateDisconnectActionRecord(UFMNoticeBean bean);
	/**
	 * shivam.pandey.EXT Disconnect - END
	 */
}
