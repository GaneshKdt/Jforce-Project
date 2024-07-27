package com.nmims.interfaces;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nmims.beans.FinalCertificateABCreportBean;

@Component
public interface FinalCertificateABCreportServiceInterface {
	
	public List<FinalCertificateABCreportBean> getStudentsDataForABCreport(FinalCertificateABCreportBean bean ) throws Exception;

}
