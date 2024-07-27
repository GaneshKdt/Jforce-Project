package com.nmims.interfaces;

import com.nmims.factory.LeadFactory.LoginType;

public interface LeadFactoryInterface {

	LeadInterface getLoginType(LoginType type);
	
}
