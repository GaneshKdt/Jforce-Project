/**
 * 
 */
package com.nmims.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author vil_m
 *
 */
public class DateTimeHelper {
	
	public static final String FORMAT_ddMMMyyyy = "dd/MMM/yyyy";


	private static final Logger logger = LoggerFactory.getLogger(DateTimeHelper.class);
	
	private static final String acadMonth = "Jan";



	public static boolean checkDate(String dateFormat1, String date1, String dateFormat2, String date2)
			throws ParseException {
		boolean checked = Boolean.FALSE;
		SimpleDateFormat sdf1 = null;
		SimpleDateFormat sdf2 = null;
		Date d1 = null;
		Date d2 = null;
		try {
			sdf1 = new SimpleDateFormat(dateFormat1);
			sdf2 = new SimpleDateFormat(dateFormat2);

			d1 = sdf1.parse(date1);
			d2 = sdf2.parse(date2);

			checked = (d1.getTime() < d2.getTime());
			logger.info("Date (date1, date2) : (" + date1 + "," + date2 + ") : " + checked);
		} catch (ParseException pe) {
			logger.error("DateTimeHelper : checkDate : Error : " + pe);
			throw pe;
		}
		return checked;
	}
	

	//Find The acadDate For Partitioning (according to Month and year)
	public static java.sql.Date findAcadDate(String year,String month) 
	{
		try {
			
			String input = year+"-"+month+"-01";
			SimpleDateFormat parser = new SimpleDateFormat("yyyy-MMM-dd");
			String formattedDate= "";
			 
			
			 Date date = parser.parse(input);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			 formattedDate = formatter.format(date); 
			
		   
	
		    return java.sql.Date.valueOf(formattedDate);
			
		}catch(Exception e)
		{
			return null;
		}
		
	}
	
	public static String getDateInFormat(String defaultFormat, String default_dob) {
		String formatedDob = null;
		Date dob;
		try {
			dob = new SimpleDateFormat(defaultFormat).parse(default_dob);
		formatedDob = new SimpleDateFormat("dd/MM/yyyy").format(dob);
		} catch (ParseException e) {
		}
		return formatedDob;
	}
}
