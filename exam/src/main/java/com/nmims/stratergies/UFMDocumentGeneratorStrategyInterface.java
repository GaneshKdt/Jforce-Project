package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.UFMIncidentBean;
import com.nmims.beans.UFMNoticeBean;

public interface UFMDocumentGeneratorStrategyInterface {

	public abstract UFMNoticeBean createUpdatedPDFDocument(UFMNoticeBean ufmNoticeBean,List<UFMIncidentBean> incidentDetails) throws Exception;
	/**
	 * shivam.pandey.EXT COC - START
	 * @param incidentDetails 
	 */
	//To Create PDF For Student
	public abstract UFMNoticeBean createUpdatedCOCPDFDocument(UFMNoticeBean ufmNoticeBean, List<UFMIncidentBean> incidentDetails) throws Exception;
	//To Get Updated UFM Bean After DB Insert Or Update
	public UFMNoticeBean getUpdatedCOCUFMBean(UFMNoticeBean ufmNoticeBean) throws Exception;
	/**
	 * shivam.pandey.EXT COC - END
	 */
	
	
	/**
	 * shivam.pandey.EXT Disconnect Above - START
	 */
	//To Create PDF For Student
	public abstract UFMNoticeBean createUpdatedDisconnectAbovePDFDocument(UFMNoticeBean ufmNoticeBean) throws Exception;
	//To Get Updated UFM Bean After DB Insert Or Update
	public UFMNoticeBean getUpdatedDisconnectAboveUFMBean(UFMNoticeBean ufmNoticeBean) throws Exception;
	/**
	 * shivam.pandey.EXT Disconnect Above - END
	 */
	
	
	/**
	 * shivam.pandey.EXT Disconnect Below - START
	 */
	//To Create PDF For Student
	public abstract UFMNoticeBean createUpdatedDisconnectBelowPDFDocument(UFMNoticeBean ufmNoticeBean) throws Exception;
	//To Get Updated UFM Bean After DB Insert Or Update
	public UFMNoticeBean getUpdatedDisconnectBelowUFMBean(UFMNoticeBean ufmNoticeBean) throws Exception;
	/**
	 * shivam.pandey.EXT Disconnect Below - END
	 */
}
