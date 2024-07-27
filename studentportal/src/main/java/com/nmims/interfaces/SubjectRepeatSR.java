package com.nmims.interfaces;

import com.nmims.beans.ServiceRequestStudentPortal;

public interface SubjectRepeatSR {
	ServiceRequestStudentPortal getSubjectRepeatStatusForStudent(String sapid);
	ServiceRequestStudentPortal saveSubjectRegistrationSRPayment(ServiceRequestStudentPortal sr);
}
