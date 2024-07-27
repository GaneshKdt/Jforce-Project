package com.nmims.interfaces;

import java.util.ArrayList;

import com.nmims.beans.LeadStudentPortalBean;
import com.sforce.ws.ConnectionException;

public interface LeadInterface {

	LeadStudentPortalBean getLeadDetailsLocally( LeadStudentPortalBean bean ) throws Exception;

	boolean checkIfLeadExists( LeadStudentPortalBean bean ) throws Exception ;

	void insertLeadDetailsLocally( LeadStudentPortalBean bean ) throws Exception ;

	ArrayList<LeadStudentPortalBean> getLeadFromSalesForce( LeadStudentPortalBean bean ) throws ConnectionException, Exception;
}
