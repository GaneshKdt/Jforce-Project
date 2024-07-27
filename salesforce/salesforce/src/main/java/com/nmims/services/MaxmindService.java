package com.nmims.services;

import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;

import com.nmims.dto.IpAddressDetails;
import com.nmims.helpers.MaxmindHelper;

@Service
public class MaxmindService {
	
	@Autowired
	MaxmindHelper maxmindHelper;
	
	private DatabaseReader reader;
	
	private static final Logger logger = LoggerFactory.getLogger(MaxmindService.class);
	
	public IpAddressDetails getIpDetails(String ip) throws Exception
	{
		
		try
		{
			IpAddressDetails ipDetails = new IpAddressDetails();
			InetAddress ipAddress = InetAddress.getByName(ip);
			
			reader= maxmindHelper.getReader();
			CityResponse response = reader.city(ipAddress);
			
			Country country = response.getCountry();            
			City city = response.getCity();
			Subdivision subdivision = response.getMostSpecificSubdivision();
			ipDetails.setCountry(country.getName()); 
			ipDetails.setState(subdivision.getName());
			ipDetails.setCity(city.getName());
			ipDetails.setError("false");
			return ipDetails;
		}
		catch(AddressNotFoundException ae)
		{
			logger.info("Exception : Address for the ip "+ip+" not found "+ae.getMessage());
			throw new AddressNotFoundException("Address for the ip "+ip+" not found "+ae.getMessage());
		}
		catch(Exception e)
		{
			logger.info("Exception in getting details for IP "+ip+":"+e.getMessage());
			throw new Exception("Exception in getting details for IP "+ip+":"+e.getMessage());
		}
	}
}
