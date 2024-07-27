package com.nmims.stratergies.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.UFMIncidentBean;
import com.nmims.beans.UFMNoticeBean;
import com.nmims.daos.UFMNoticeDAO;
import com.nmims.stratergies.UFMDocumentGeneratorStrategyInterface;
import com.nmims.stratergies.UFMGenerateNoticeNoticeStrategyInterface;

@Service("ufmDocumentGeneratorStrategy")
public class UFMDocumentGeneratorStrategy implements UFMDocumentGeneratorStrategyInterface {

	@Autowired
	UFMNoticeDAO dao;
	
	@Autowired
	UFMGenerateNoticeNoticeStrategy ufmGenerateNoticeNoticeStrategy;
	
	@Autowired
	UFMGenerateNoticeNoticeStrategyInterface ufmGenerateNoticeNoticeStrategyInterface;

	public UFMNoticeBean createUpdatedPDFDocument(UFMNoticeBean ufmNoticeBean,List<UFMIncidentBean> incidentDetails) throws Exception {		
		ufmGenerateNoticeNoticeStrategy.generatePDF(ufmNoticeBean,incidentDetails);
		dao.saveDocumentNamesForBean(ufmNoticeBean);
		return ufmNoticeBean;
	}
	
	public UFMNoticeBean getUpdatedUFMBean(UFMNoticeBean ufmNoticeBean) throws Exception {
		UFMNoticeBean bean = dao.getUFMBean(ufmNoticeBean.getSapid(), ufmNoticeBean.getSubject(), ufmNoticeBean.getYear(), ufmNoticeBean.getMonth(), ufmNoticeBean.getCategory());
		bean.setLastModifiedBy(ufmNoticeBean.getLastModifiedBy());
		
		return bean;
	}
	
	/**
	 * shivam.pandey.EXT COC - START
	 */
	@Override
	public UFMNoticeBean createUpdatedCOCPDFDocument(UFMNoticeBean ufmNoticeBean,List<UFMIncidentBean> incidentDetails) throws Exception {
		ufmGenerateNoticeNoticeStrategyInterface.generateCOCPDF(ufmNoticeBean,incidentDetails);
		dao.saveDocumentNamesForBean(ufmNoticeBean);
		return ufmNoticeBean;
	}
	
	@Override
	public UFMNoticeBean getUpdatedCOCUFMBean(UFMNoticeBean ufmNoticeBean) throws Exception {
		UFMNoticeBean bean = dao.getUFMBean(ufmNoticeBean.getSapid(), ufmNoticeBean.getSubject(), ufmNoticeBean.getYear(), ufmNoticeBean.getMonth(), ufmNoticeBean.getCategory());
		bean.setLastModifiedBy(ufmNoticeBean.getLastModifiedBy());
		return bean;
	}
	/**
	 * shivam.pandey.EXT COC - END
	 */
	
	
	
	/**
	 * shivam.pandey.EXT Disconnect Above - START
	 */
	@Override
	public UFMNoticeBean createUpdatedDisconnectAbovePDFDocument(UFMNoticeBean ufmNoticeBean) throws Exception {
		ufmGenerateNoticeNoticeStrategyInterface.generateDisconnectAbovePDF(ufmNoticeBean);
		dao.saveDocumentNamesForBean(ufmNoticeBean);
		return ufmNoticeBean;
	}
	
	@Override
	public UFMNoticeBean getUpdatedDisconnectAboveUFMBean(UFMNoticeBean ufmNoticeBean) throws Exception {
		UFMNoticeBean bean = dao.getUFMBean(ufmNoticeBean.getSapid(), ufmNoticeBean.getSubject(), ufmNoticeBean.getYear(), ufmNoticeBean.getMonth(), ufmNoticeBean.getCategory());
		bean.setLastModifiedBy(ufmNoticeBean.getLastModifiedBy());
		return bean;
	}
	/**
	 * shivam.pandey.EXT Disconnect Above - END
	 */
	
	
	
	/**
	 * shivam.pandey.EXT Disconnect Below - START
	 */
	@Override
	public UFMNoticeBean createUpdatedDisconnectBelowPDFDocument(UFMNoticeBean ufmNoticeBean) throws Exception {
		ufmGenerateNoticeNoticeStrategyInterface.generateDisconnectBelowPDF(ufmNoticeBean);
		dao.saveDocumentNamesForBean(ufmNoticeBean);
		return ufmNoticeBean;
	}
	
	@Override
	public UFMNoticeBean getUpdatedDisconnectBelowUFMBean(UFMNoticeBean ufmNoticeBean) throws Exception {
		UFMNoticeBean bean = dao.getUFMBean(ufmNoticeBean.getSapid(), ufmNoticeBean.getSubject(), ufmNoticeBean.getYear(), ufmNoticeBean.getMonth(), ufmNoticeBean.getCategory());
		bean.setLastModifiedBy(ufmNoticeBean.getLastModifiedBy());
		return bean;
	}
	/**
	 * shivam.pandey.EXT Disconnect Below - END
	 */	
}
