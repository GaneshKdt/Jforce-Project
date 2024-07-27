package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;
import com.nmims.beans.StudentBean;
import com.nmims.daos.StudentZoneDao;
import com.nmims.listeners.SalesforceSyncScheduler;

@Service
public class SpecializationService {
	@Autowired
	private StudentZoneDao studentdao;

	private static final Logger logger = LoggerFactory.getLogger(SalesforceSyncScheduler.class);

	
	public ArrayList<StudentBean> insertEntriesInMBASpecialization(ArrayList<StudentBean> students) {
		HashMap<String, String> portalSpecialisationMapping = new HashMap<String, String>();
		ArrayList<StudentBean> errorList = new ArrayList<StudentBean>();
		portalSpecialisationMapping.put("MBA(WX) - M", "9");
		portalSpecialisationMapping.put("MBA(WX) - LS", "10");
		portalSpecialisationMapping.put("MBA(WX) - OSC", "11");
		portalSpecialisationMapping.put("MBA(WX) - AF", "12");
		portalSpecialisationMapping.put("M.Sc. (AI) - DL", "14");
		portalSpecialisationMapping.put("M.Sc. (AI) - DO", "15");
			for (StudentBean student : students) {
				try {
					logger.info("Student for Sync Specialization  : "+student.getSapid());

				if (portalSpecialisationMapping.containsKey(student.getSpecialisation1())) {
					studentdao.insertSpecialisationDetails(student, portalSpecialisationMapping);
				}
			}
		catch (Exception e) {
			errorList.add(student);
			student.setErrorMessage("Error while Syncing specialization");
			

		}}
			return errorList;
	}
}
