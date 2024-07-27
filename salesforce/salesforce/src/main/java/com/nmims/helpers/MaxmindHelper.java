package com.nmims.helpers;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.maxmind.geoip2.DatabaseReader;


@Component
public class MaxmindHelper {
	private File database;
	private DatabaseReader reader;
	private static final Logger logger = LoggerFactory.getLogger(MaxmindHelper.class);
	public MaxmindHelper()
	{
		try
		{
			database = new File("C:\\ProgramData\\MaxMind\\GeoIPUpdate\\GeoIP\\GeoLite2-City.mmdb");
			reader = new DatabaseReader.Builder(database).build();
		}
		catch(Exception e)
		{
			logger.info("Exception in maxmindhelper constructor"+e.getMessage());
		}
	}
	
	public DatabaseReader getReader()
	{
		return reader;
	}
	
	public void getUpdatedReader()
	{
		try
		{
			database = new File("C:\\ProgramData\\MaxMind\\GeoIPUpdate\\GeoIP\\GeoLite2-City.mmdb");
			reader = new DatabaseReader.Builder(database).build();
		}
		catch(Exception e)
		{
			logger.info("Exception in getUpdatedReader:"+e.getMessage());
		}
	}
}
