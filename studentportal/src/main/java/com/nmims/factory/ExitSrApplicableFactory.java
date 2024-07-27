package com.nmims.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.ExitSrApplicableInterface;
import com.nmims.services.ExitApplicableMscStudents;
import com.nmims.services.ExitApplicablePDDMstudents;
import com.nmims.services.ExitApplicableForRetail;
@Service("exitSrApplicableFactory")
public class ExitSrApplicableFactory {

	@Autowired
	ExitApplicableForRetail exitApplicableForRetail;
	
	@Autowired
	ExitApplicablePDDMstudents exitApplicablePDDMstudents;
	
	@Autowired 
	ExitApplicableMscStudents exitApplicableMscStudents;
	
	private ArrayList<String> pddmTypeMasterKey = new ArrayList<String>(Arrays.asList("142","143","144","145","146","147","148","149")); 
	private List<String> mScTypeMasterKey = new ArrayList<String>(Arrays.asList("111","131","151","154","155","156","157","158","160"));
	
	public ExitSrApplicableInterface getProductType(String masterKeytype) {
		
		String ProductType="";
		if(pddmTypeMasterKey.contains(masterKeytype)) {
			ProductType="PDDM";
		}else if(mScTypeMasterKey.contains(masterKeytype)) {
			ProductType="MSC";
		}else {
			ProductType="PGnMBAretail";
		}
		
		ExitSrApplicableInterface certificate = null;
		switch (ProductType) {
		case "PDDM":
			certificate = exitApplicablePDDMstudents;
			break;
		case "MSC":
			certificate = exitApplicableMscStudents;
			break;
		case "PGnMBAretail":
			certificate = exitApplicableForRetail;
			break;
		}
		return certificate;
	};
	
}
