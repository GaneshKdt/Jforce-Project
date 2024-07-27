package com.nmims.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.LeadStudentPortalBean;
import com.nmims.daos.LeadDAO;
import com.nmims.interfaces.LeadInterface;
import com.sforce.ws.ConnectionException;

@Service("leadLoginWithMobile")
public class LeadLoginWithMobile implements LeadInterface{
	
	@Autowired
	LeadDAO leadDao;

	@Override
	public LeadStudentPortalBean getLeadDetailsLocally( LeadStudentPortalBean bean ) throws Exception {

		bean = leadDao.getLeadDetailsLocallyForMobile( bean );
		return bean;
		
	}

	@Override
	public boolean checkIfLeadExists(LeadStudentPortalBean bean) throws Exception {

		boolean present = false;
		present = leadDao.checkIfLeadPresentForMobile( bean );
		return present;
		
	}

	@Override
	public void insertLeadDetailsLocally(LeadStudentPortalBean bean) throws Exception {

		leadDao.insertLeadDetailsLocally(bean);
		
	}

	@Override
	public ArrayList<LeadStudentPortalBean> getLeadFromSalesForce( LeadStudentPortalBean bean ) throws ConnectionException, Exception {

		ArrayList<LeadStudentPortalBean> leadDetail = new ArrayList<>();
		leadDetail = leadDao.getLeadForMobileFromSalesForce( bean );
		return leadDetail;
		
	}

}
