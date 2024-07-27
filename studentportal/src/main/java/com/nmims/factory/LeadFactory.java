package com.nmims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nmims.interfaces.LeadFactoryInterface;
import com.nmims.interfaces.LeadInterface;
import com.nmims.services.LeadLoginWithEmail;
import com.nmims.services.LeadLoginWithMobile;
import com.nmims.services.LeadLoginWithRegistrationId;

@Service("leadFactory")
public class LeadFactory implements LeadFactoryInterface{

	@Autowired
	LeadLoginWithMobile leadLoginWithMobile;
	
	@Autowired
	LeadLoginWithEmail leadLoginWithEmail;
	
	@Autowired
	LeadLoginWithRegistrationId leadLoginWithRegistrationId;

	public enum LoginType {
		MOBILE, EMAIL, REGISTATIONID
	}

	@Override
	public LeadInterface getLoginType(LoginType type) {
		
		LeadInterface lead = null;
		
		switch (type) {
		case MOBILE:
			lead = leadLoginWithMobile;
			break;
		case EMAIL:
			lead = leadLoginWithEmail;
			break;
		case REGISTATIONID:
			lead = leadLoginWithRegistrationId;
			break;
		}
		
		return lead;
	}
}
