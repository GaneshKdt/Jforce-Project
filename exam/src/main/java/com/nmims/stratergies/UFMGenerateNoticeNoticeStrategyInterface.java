package com.nmims.stratergies;

import java.util.List;

import com.itextpdf.text.Document;
import com.nmims.beans.UFMIncidentBean;
import com.nmims.beans.UFMNoticeBean;

public interface UFMGenerateNoticeNoticeStrategyInterface {
	public abstract void generateShowCauseNotice(UFMNoticeBean bean, Document document,List<UFMIncidentBean> incidentDetails) throws Exception;
	public abstract void generateWarningNotice(UFMNoticeBean bean, Document document) throws Exception;
	public abstract void generatePenaltyNoticeSingleSubject(UFMNoticeBean bean, Document document) throws Exception;
	

	public abstract void generatePDF(UFMNoticeBean bean,List<UFMIncidentBean> incidentDetails) throws Exception;
	
	/**
	 * shivam.pandey.EXT COC - START
	 * @param incidentDetails 
	 */
	//To Generate PDF For COC Category Of UFM Students
	public abstract void generateCOCPDF(UFMNoticeBean bean, List<UFMIncidentBean> incidentDetails) throws Exception;
	/**
	 * shivam.pandey.EXT COC - END
	 */
	
	
	
	/**
	 * shivam.pandey.EXT Disconnect - START
	 */
	//To Generate PDF For COC Category Of UFM Students
	public abstract void generateDisconnectAbovePDF(UFMNoticeBean bean) throws Exception;
	/**
	 * shivam.pandey.EXT Disconnect - END
	 */
	
	
	
	/**
	 * shivam.pandey.EXT Disconnect Below - START
	 */
	//To Generate PDF For COC Category Of UFM Students
	public abstract void generateDisconnectBelowPDF(UFMNoticeBean bean) throws Exception;
	/**
	 * shivam.pandey.EXT Disconnect Below - END
	 */
}
