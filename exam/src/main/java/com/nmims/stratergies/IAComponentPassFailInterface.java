package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.TEEResultBean;

/**
 * 
 * @author Abhay_Sakpal
 *
 */
public interface IAComponentPassFailInterface {
	
	public void searchPassFail(TEEResultBean resultBean, List<EmbaPassFailBean> finalListforPassFail,
			List<TEEResultBean> studentsListEligibleForPassFail,String loggedInUser, List<EmbaPassFailBean> unsuccessfulPassFail);
}
