package com.nmims.interfaces;

import java.util.ArrayList;

import com.nmims.beans.StudentExamBean;
import com.nmims.dto.FeeReceiptDTO;

public interface FeeReceiptInterface {
	
	ArrayList<FeeReceiptDTO> getAdmissionFeeReceiptFromSapId(String sapid);
	
}
