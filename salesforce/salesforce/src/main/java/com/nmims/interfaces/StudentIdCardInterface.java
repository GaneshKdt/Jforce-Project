package com.nmims.interfaces;

import java.util.ArrayList;
import java.util.List;

import com.nmims.beans.StudentBean;
import com.sforce.soap.partner.sobject.SObject;

public interface StudentIdCardInterface {
	
	public ArrayList<StudentBean> generateIdCardForStudent(StudentBean studentToCreateInLDAP);

}
