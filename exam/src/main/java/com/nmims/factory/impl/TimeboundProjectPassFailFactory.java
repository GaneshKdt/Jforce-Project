package com.nmims.factory.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nmims.factory.ITimeboundProjectPassFailFactory;
import com.nmims.services.ITimeboundProjectPassFailService;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
@Service("timeboundProjectPassFailFactory")
public class TimeboundProjectPassFailFactory implements ITimeboundProjectPassFailFactory {

	@Autowired
	@Qualifier("mbawxProjectPassFailService")
	private ITimeboundProjectPassFailService mbawxProjectPassFailService;
	
	@Override
	public ITimeboundProjectPassFailService getTimeboundProjectPassFailProcessingInstance(String productType) {
		
		switch (productType) {

			case ITimeboundProjectPassFailFactory.MBAWX:
				return mbawxProjectPassFailService;
				
			case ITimeboundProjectPassFailFactory.MBAX:
				break;
				
			case ITimeboundProjectPassFailFactory.MSCAIMLOPS:
				break;
				
			case ITimeboundProjectPassFailFactory.MSCAI:
			case ITimeboundProjectPassFailFactory.PCDS:
			case ITimeboundProjectPassFailFactory.PDDS:
				break;

			default:
				break;
		}
		return null;
	}

}
