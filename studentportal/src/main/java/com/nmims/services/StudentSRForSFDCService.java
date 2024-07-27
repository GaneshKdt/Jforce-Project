package com.nmims.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.interfaces.StudentSRForSFDCInterface;
@Service
public class StudentSRForSFDCService implements StudentSRForSFDCInterface {

	@Override
	public List<ServiceRequestStudentPortal> getStudentSR(List<ServiceRequestStudentPortal> list,
			Map<String, String> mapOfServiceRequestTypeAndTAT) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar c = Calendar.getInstance();
			String exceptedDate = "";
			for (ServiceRequestStudentPortal student : list) {
				if ((!StringUtils.isBlank(student.getRequestClosedDate()))
						&& (!StringUtils.isBlank(mapOfServiceRequestTypeAndTAT.get(student.getServiceRequestType())))) {
					c.setTime(dateFormat.parse(student.getRequestClosedDate())); // use SR Created date.
					c.add(Calendar.DATE, Integer.parseInt(mapOfServiceRequestTypeAndTAT.get(student.getServiceRequestType()))); // Adding TAT
					exceptedDate = sdf.format(c.getTime());
				}
				student.setRequestExpectedClosedDate(exceptedDate);
			}

		} catch (Exception e) {
		}
		return list;
	}

	@Override
	public Map<String, String> mapOfActiveSRTypesAndTAT(ServiceRequestDao sDao) {
		Map<String, String> mapOfActiveSRTypesAndTAT = sDao.mapOfActiveSRTypesAndTAT();
		return mapOfActiveSRTypesAndTAT;
	}

	@Override
	public List<ServiceRequestStudentPortal> getAllServiceRequestFromSapIDForStudentDetailDashBoard(String sapid, ServiceRequestDao sDao) {
		ArrayList<ServiceRequestStudentPortal> getAllServiceRequestFromSapIDForStudentDetailDashBoard = sDao.getStudentsSR(sapid);
		return getAllServiceRequestFromSapIDForStudentDetailDashBoard;
	}

}
