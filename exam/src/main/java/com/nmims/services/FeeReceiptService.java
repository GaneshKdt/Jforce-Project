package com.nmims.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.StudentExamBean;
import com.nmims.dto.FeeReceiptDTO;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.interfaces.FeeReceiptInterface;

@Service
public class FeeReceiptService implements FeeReceiptInterface{

	@Autowired
	SalesforceHelper salesforceHelper;
	
	@Override
	public ArrayList<FeeReceiptDTO> getAdmissionFeeReceiptFromSapId(String sapid) {
		// TODO Auto-generated method stub
		ArrayList<StudentExamBean> documentlist = salesforceHelper.listOfPaymentsMade(sapid);
		ArrayList<FeeReceiptDTO> feeReceipts = new ArrayList<FeeReceiptDTO>(); 
		for(StudentExamBean document : documentlist ) {
			FeeReceiptDTO feeReceipt = new FeeReceiptDTO();
			feeReceipt.setAcadMonth(document.getAcadMonth());
			feeReceipt.setAcadYear(document.getAcadYear());
			feeReceipt.setSem(document.getSem());
			feeReceipt.setRegistered(document.getRegistered());
			feeReceipts.add(feeReceipt);
		}
		return feeReceipts;
	
	}
}
	