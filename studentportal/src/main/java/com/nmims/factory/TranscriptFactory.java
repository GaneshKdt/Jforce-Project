package com.nmims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.TranscriptFactoryInterface;
import com.nmims.interfaces.TranscriptServiceInterface;
import com.nmims.services.TranscriptService;
import com.nmims.services.TranscriptServiceMBAX;
import com.nmims.services.TranscriptServicePG;
import com.nmims.services.TrasncriptServicePDDM;

@Service("transcriptFactory")
public class TranscriptFactory implements TranscriptFactoryInterface {

	public enum ProductType {
		MBAWX , MBAX , PG,PDDM
	}

	@Autowired
	TranscriptService transcriptServiceWX;
	
	@Autowired
	TranscriptServiceMBAX transcriptServiceX;
	
	@Autowired
	TranscriptServicePG transcriptServicePG;
	
	@Autowired
	TrasncriptServicePDDM transcriptServicePPDM;

	@Override
	public TranscriptServiceInterface getProductType(ProductType type) {
		// TODO Auto-generated method stub

		TranscriptServiceInterface transcriptService = null;

		switch (type) {
		case MBAWX:
			transcriptService = transcriptServiceWX;
			break;
		case MBAX:
			transcriptService = transcriptServiceX;
			break;
		case PG:
			transcriptService = transcriptServicePG;
			break;
		case PDDM:
			transcriptService =  transcriptServicePPDM;
			break;
		
		}
		
		
		return transcriptService;

	}
}
