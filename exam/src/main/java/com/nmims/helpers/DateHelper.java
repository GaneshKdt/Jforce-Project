/**
 * 
 */
package com.nmims.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * All operations on Date, Time, DateTime.
 * @author vil_m
 *
 */
public class DateHelper {
	
	public static final DateTimeFormatter DATE_FORMATTER_1 = DateTimeFormatter.ISO_LOCAL_DATE; //"yyyy-MM-dd";
	
	public static final Map<String, Integer> monthNameOnly3CharacterMap = new HashMap<String, Integer>();
	
	static {
		for(Month monthName : Month.values()) {
			//jan - 1, feb - 2, mar - 3,
			monthNameOnly3CharacterMap.put(monthName.toString().toLowerCase().substring(0, 3), monthName.getValue());
		}
	}
	
	public static final Logger logger = LoggerFactory.getLogger(DateHelper.class);
	
	/**
	 * 
	 * @return Eg. 2021-12-18T13:28:18.098
	 */
	public static LocalDateTime currentDateTime() {
		return LocalDateTime.now();
	}
	
	/**
	 * Date as per the Zone, with default datestyle and timestyle.
	 * @param zoneId default is current server Zone
	 * @return Eg. 18/12/21 1:28:18 PM
	 */
	public static String currentDateTime(ZoneId zoneId) {
		if(null == zoneId) {
			return LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM));
		}
		return LocalDateTime.now(zoneId).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM));
	}
	
	/**
	 * Date as per the Zone, datestyle and timestyle specified.
	 * @param zoneId
	 * @param dateStyle
	 * @param timeStyle
	 * @return
	 */
	public static String currentDateTime(ZoneId zoneId, FormatStyle dateStyle, FormatStyle timeStyle) {
		return LocalDateTime.now(zoneId).format(DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle));
	}
	
	/**
	 * Creates LocalDate from date using the formatter.
	 * @param formatter
	 * @param date
	 * @return
	 */
	protected static LocalDate parseStringToLocalDate(DateTimeFormatter formatter, String date) {
		LocalDate locDate = null;
		CharSequence charSeq = null;
		
		charSeq = date.subSequence(0, date.length());
		locDate = LocalDate.parse(charSeq, formatter);
		
		return locDate;
	}
	
	/**
	 * Adds days to date, parsed as per formatter.
	 * @param formatter
	 * @param date
	 * @param numberofDaysToAdd Number of Days to add
	 * @return New Date (date + numberofDaysToAdd)
	 */
	public static String addDays(DateTimeFormatter formatter, String date, long numberofDaysToAdd) {
		String locDateStr = null;
		LocalDate locDate = null;
		LocalDate locDateAdd = null;
		
		locDate = DateHelper.parseStringToLocalDate(formatter, date);
		
		locDateAdd = locDate.plusDays(numberofDaysToAdd);
		locDateStr = locDateAdd.toString();
		
		return locDateStr;
	}
	
	/**
	 * Subtracts days from date, parsed as per formatter.
	 * @param formatter
	 * @param date
	 * @param numberofDaysToMinus Number of Days to subtract
	 * @return New Date (date - numberofDaysToMinus)
	 */
	public static String subtractDays(DateTimeFormatter formatter, String date, long numberofDaysToMinus) {
		String locDateStr = null;
		LocalDate locDate = null;
		LocalDate locDateSub = null;
		
		locDate = DateHelper.parseStringToLocalDate(formatter, date);
		
		locDateSub = locDate.minusDays(numberofDaysToMinus);
		locDateStr = locDateSub.toString();
		
		return locDateStr;
	}
	
	/**
	 * Adds days to current date, returned as per formatter.
	 * @param formatter
	 * @param numberofDaysToAdd
	 * @return current Date + numberofDaysToAdd
	 */
	public static String addDays(DateTimeFormatter formatter, long numberofDaysToAdd) {
		String locDateStr = null;
		LocalDateTime locDateTime = null;
		
		locDateTime = LocalDateTime.now(); 
		locDateStr = locDateTime.plusDays(numberofDaysToAdd).format(formatter);
		
		return locDateStr;
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
			logger.info("DateHelper : findHighestYearMonth : (size, lowest, highest) (" + ymSet.size() + ", " + ymSet.first() + ", "
					+ ymSet.last() + ")");

			yearMonObj = ymSet.last();
			reconstructedMonth = yearMonObj.getMonth().toString();//DECEMBER
			reconstructedMonth = reconstructedMonth.substring(0, 1) + reconstructedMonth.substring(1, 3).toLowerCase();
			yearMonth = yearMonObj.getYear() + "-" + reconstructedMonth;//2020-Dec
			ymSet.clear();
		}
		return yearMonth;
	}

	// keep this method getMonthNumber, Vilpesh on 20220630
	/**
	 * For first 3 characters from month name say jan from January, must return 1.
	 * 
	 * @param monthName3Char
	 * @return 1 for jan, 2 for feb so on till 12 for dec
	 */
	public static int getMonthNumber(String monthName3Char) {
		return monthNameOnly3CharacterMap.get(monthName3Char.toLowerCase());
	}

	// keep this method makeYearMonth, Vilpesh on 20220630
	protected YearMonth makeYearMonth(int year, String monthName3Char) {
		int month = getMonthNumber(monthName3Char);
		YearMonth yearMonthObj = YearMonth.of(year, month);
		return yearMonthObj;
	}

	// keep this method compareYearMonth, Vilpesh on 20220630
	protected Boolean compareYearMonth(YearMonth obj1, YearMonth obj2) {
		Boolean compare = Boolean.FALSE;
		if (obj1.equals(obj2)) {
			compare = Boolean.TRUE;
			logger.info("equals (obj2, obj1) : (" + obj2.toString() + ", " + obj1.toString() + ")");
			return compare;
		}
		if (obj2.isBefore(obj1)) {
			compare = Boolean.TRUE;
			logger.info("before (obj2, obj1) : (" + obj2.toString() + ", " + obj1.toString() + ")");
			return compare;
		}
		return compare;
	}

	// keep this method compareYearMonth, Vilpesh on 20220630
	public Boolean compareYearMonth(String year1, String month1, String year2, String month2) {
		Boolean compared;
		YearMonth yearMonthObj1 = null;
		YearMonth yearMonthObj2 = null;
		Integer y1;
		Integer y2;
		try {
			y1 = toInteger(year1);
			y2 = toInteger(year2);
			yearMonthObj1 = this.makeYearMonth(y1.intValue(), month1);
			yearMonthObj2 = this.makeYearMonth(y2.intValue(), month2);
			compared = this.compareYearMonth(yearMonthObj1, yearMonthObj2);
		} catch (Exception ex) {
			logger.error("compareYearMonth : Error : " + ex.getMessage());
			compared = Boolean.FALSE;
		}
		return compared;
	}

	// keep this method toInteger, Vilpesh on 20220630
	public static Integer toInteger(String arg) {
		return Integer.valueOf(arg);
	}
}
