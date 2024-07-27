package com.nmims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.SemDeregisterFactoryInterface;
import com.nmims.interfaces.SemDeregisterInterface;
import com.nmims.services.SemDeregisterServiceMBAWX;
import com.nmims.services.SemDeregisterServiceMBAX;



@Service("semDeregisterFactory")
public class SemDeregisterFactory implements SemDeregisterFactoryInterface {
	
	@Autowired
	SemDeregisterServiceMBAWX semDeregisterServiceMBAWX;
	
	@Autowired
	SemDeregisterServiceMBAX semDeregisterServiceMBAX;

	
	
	public enum ProductType{
		MBAX, MBAWX
	}
	


		@Override
		public SemDeregisterInterface getProductType(ProductType type) {
			// TODO Auto-generated method stub
			//System.out.println("--->");
			//System.out.println(type);
			SemDeregisterInterface semDeregisterInterface = null;
			
			switch (type) {
			case MBAWX:
				semDeregisterInterface = semDeregisterServiceMBAWX;
				break;
			case MBAX:
				semDeregisterInterface =  semDeregisterServiceMBAX;
				break;
			}
			return semDeregisterInterface;
		}



		
	
}
