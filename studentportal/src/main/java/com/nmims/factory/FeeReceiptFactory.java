package com.nmims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.FeeReceiptFactoryInterface;
import com.nmims.interfaces.FeeReceiptInterface;
import com.nmims.services.BonafideSRFeeReceipt;
import com.nmims.services.CertificateSRFeeReceipt;
import com.nmims.services.ChangeInSpecializationSRFeeReceipt;
import com.nmims.services.DuplicateFeeReceiptSR;
import com.nmims.services.DuplicateICardSRFeeReceipt;
import com.nmims.services.DuplicateStudyKitFeeReceipt;
import com.nmims.services.GradesheetSRFeeReceipt;
import com.nmims.services.MarksheetSRFeeReceipt;
import com.nmims.services.OtherSRFeeReceipt;
import com.nmims.services.TranscriptSRFeeReceipt;  
 
@Service("feeReceiptFactory")
public class FeeReceiptFactory implements FeeReceiptFactoryInterface{
	
	public enum ProductType  {
			ISSUANCEOFMARKSHEET, ISSUANCEOFBONAFIDE,
			ISSUANCEOFFINALCERTIFICATE, ISSUANCEOFTRANSCRIPT,
			REVALUATIONOFTERMENDEXAMMARKS,
			ISSUANCEOFGRADESHEET, DUPLICATEFEERECEIPT, DUPLICATEICARD,SUBJECTREPEATMSCAIANDMLOPS,
			DUPLICATESTUDYKIT,SUBJECTREPEATMBAWX,EXITPROGRAM,ASSIGNMENTREVALUATION,CHANGEINSPECIALISATION,
			SINGLEBOOK,SUBJECTREPEAT,REDISPATCHOFSTUDYKIT
    }
	  
	@Autowired
	MarksheetSRFeeReceipt  marksheetReceipt; 
	
	@Autowired 
	BonafideSRFeeReceipt  bonafideReceipt; 
	
	@Autowired 
	CertificateSRFeeReceipt  certificateReceipt;
	  
	@Autowired 
	GradesheetSRFeeReceipt  gradesheetReceipt;
	
	@Autowired 
	TranscriptSRFeeReceipt  transcriptReceipt;
	
	@Autowired 
	DuplicateFeeReceiptSR  duplicateFeeReceipt;
	
	@Autowired
	DuplicateICardSRFeeReceipt duplicateICardReceipt;
	
	@Autowired 
	OtherSRFeeReceipt  otherSrReceipt; 
	
	@Autowired
	DuplicateStudyKitFeeReceipt duplicateStudyKitReceipt;  
	
	@Autowired
	ChangeInSpecializationSRFeeReceipt changeInSpecializationSRFeeReceipt;
	
	public FeeReceiptInterface getSRType(ProductType type) {
		System.out.println("--->");
		System.out.println("type:"+type);
		FeeReceiptInterface srType = null;
		
		switch (type) { 
		case ISSUANCEOFMARKSHEET:
			srType = marksheetReceipt;
			break;
		case ISSUANCEOFBONAFIDE:
			srType = bonafideReceipt;
			break;
		case ISSUANCEOFFINALCERTIFICATE:
			srType = certificateReceipt;
			break;
		case ISSUANCEOFTRANSCRIPT:
			srType = transcriptReceipt;
			break;
		case ISSUANCEOFGRADESHEET:
			srType = gradesheetReceipt;
			break;  
		case DUPLICATEFEERECEIPT:
			srType = duplicateFeeReceipt;
			break;
		case DUPLICATEICARD:
			srType = duplicateICardReceipt;
			break;
		case DUPLICATESTUDYKIT:
			srType = duplicateStudyKitReceipt;
			break;
		case EXITPROGRAM:
			srType = otherSrReceipt;
			break;
		case ASSIGNMENTREVALUATION:
			srType = otherSrReceipt;
		case CHANGEINSPECIALISATION:
			srType = changeInSpecializationSRFeeReceipt;
			break;
		default :srType = otherSrReceipt;
		} 
		return srType;
	};
}
