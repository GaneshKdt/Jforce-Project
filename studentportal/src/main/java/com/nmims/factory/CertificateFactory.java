package com.nmims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.CertificateFactoryInterface;
import com.nmims.interfaces.CertificateInterface;
import com.nmims.services.CertificateLead;
import com.nmims.services.CertificateMBAWX;
import com.nmims.services.CertificateMBAX;
import com.nmims.services.CertificatePDDM;
import com.nmims.services.CertificateService;

@Service("certificateFactory")
public class CertificateFactory implements CertificateFactoryInterface{
	public enum ProductType {
		PG, MBAWX, MBAX, LEAD, PDDM
	}
	
	@Autowired
	CertificateMBAWX  certificateMBAWX;
	
	@Autowired
	CertificateMBAX certificateMBAX;
	
	@Autowired 
	CertificateService certificateService;
	
	@Autowired
	CertificateLead certificateLead;
	
	@Autowired
	CertificatePDDM certificatePDDM;
	
	
	public CertificateInterface getProductType(ProductType type) {
		//System.out.println("--->");
		//System.out.println(type);
		CertificateInterface certificate = null;
		
		switch (type) {
		case MBAWX:
			certificate = certificateMBAWX;
			break;
		case PG:
			//System.out.println("---> PG");
			certificate = certificateService;
			break;
		case LEAD:
			certificate = certificateLead;
			break;
		case MBAX:
			certificate =  certificateMBAX;
			break;
		case PDDM:
			certificate =  certificatePDDM;
			break;
		}
		return certificate;
	};
}
