package com.nmims.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public interface DateTimeUtil {
	
	public static final String FORMAT_ddMMMyyyyHHmmss = "yyyy-MM-dd HH:mm:ss";
	public static final String GivenFORMAT_ddMMMyyyyHHmmss = "yyyy-MM-dd'T'HH:mm";
	
	public static String getDateInDefaultFormat(String defaultFormat, String default_dob)throws ParseException {
		Date d1 = new SimpleDateFormat(defaultFormat).parse(default_dob);
		return new SimpleDateFormat(FORMAT_ddMMMyyyyHHmmss).format(d1);
	}
	
	//Get current Date in default format
	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_ddMMMyyyyHHmmss);
		return sdf.format(cal.getTime());
	}
	
	/**
	 * 
	 * @param Given Date By user (date1)
	 * @return compare the given date by current Date , return true if given date1 is less than or equal to current date
	 * @throws ParseException
	 */
	public static boolean compareActiveDateWithCurrentDate(String date1) 
			throws ParseException {
	
		SimpleDateFormat sdf1 = new SimpleDateFormat(FORMAT_ddMMMyyyyHHmmss);
		Date d1 = sdf1.parse(date1);
		Date d2 = sdf1.parse(sdf1.format(new Date()));
		return (d1.getTime() <= d2.getTime());
	}
	
	public static String getTheactiveDateForContent(String activeDate) throws ParseException{
		if(StringUtils.isBlank(activeDate)) 
			activeDate = now();
		else
			activeDate = getDateInDefaultFormat(GivenFORMAT_ddMMMyyyyHHmmss,activeDate);
		return activeDate;
		
	}

}
