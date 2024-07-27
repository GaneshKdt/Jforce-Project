/**
 * 
 */
package com.nmims.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author vil_m
 *
 */
public class DateTimeHelper {

	public static final String FORMAT_ddMMMyyyy = "dd/MMM/yyyy";
	
	
	private static final Logger logger = LoggerFactory.getLogger(DateTimeHelper.class);

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
			System.out.println("Exception : checkDate : " + pe);
			logger.error("DateTimeHelper : checkDate : Error : " + pe);
			throw pe;
		}
		return checked;
	}
	
	
	public static String getDateInFormat(String defaultFormat,String default_dob)
			throws ParseException {
		String formatedDob = null;
		try {
			Date dob = new SimpleDateFormat(defaultFormat).parse(default_dob);
            formatedDob = new SimpleDateFormat("dd/MM/yyyy").format(dob);
			
		} catch (ParseException pe) {
			logger.error("DateTimeHelper : getDateInFormat : Error : " + pe);
			return pe.toString();
		}
		return formatedDob;
	}
	
	public static final String FORMAT_YEAR_DASH_MONTH = "yyyy-MMM";

	/**
	 * yearMonth combination highest(greatest) among similar combination.
	 * 
	 * @param yearMonthSet data 2020-Dec, 2021-Apr, 2021-Sep in Set.
	 * @param pattern      properly parse input yearMonth so as create valid
	 *                     YearMonth object
	 * @return greatest i.e 2021-Sep
	 */
	public static String findHighestYearMonth(Set<String> yearMonthSet, String pattern) {
		String yearMonth = null;
		CharSequence charSeq = null;
		DateTimeFormatter formatter = null;
		YearMonth yearMonObj = null;
		TreeSet<YearMonth> ymSet = null;
		String reconstructedMonth = null;

		formatter = DateTimeFormatter.ofPattern(pattern);
		ymSet = new TreeSet<YearMonth>();
		if (null != yearMonthSet && !yearMonthSet.isEmpty()) {
			for (String str : yearMonthSet) {
				charSeq = str.subSequence(0, 8);// from 2020-Dec, create CharSequence
				yearMonObj = YearMonth.parse(charSeq, formatter);//can only read 2020-12
				ymSet.add(yearMonObj);
			}
			logger.info("DateTimeHelper : findHighestYearMonth : (size, lowest, highest) (" + ymSet.size() + ", " + ymSet.first() + ", "
					+ ymSet.last() + ")");

			yearMonObj = ymSet.last();
			reconstructedMonth = yearMonObj.getMonth().toString();//DECEMBER
			reconstructedMonth = reconstructedMonth.substring(0, 1) + reconstructedMonth.substring(1, 3).toLowerCase();
			yearMonth = yearMonObj.getYear() + "-" + reconstructedMonth;//2020-Dec
			ymSet.clear();
		}
		return yearMonth;
	}
}
