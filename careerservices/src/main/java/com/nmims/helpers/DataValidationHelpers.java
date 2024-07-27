package com.nmims.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nmims.beans.SessionDayTimeBean;

public class DataValidationHelpers {

	private static final Logger logger = LoggerFactory.getLogger(DataValidationHelpers.class);
 
	public boolean checkIfStringEmptyOrNull(String s) {
		//return true if not empty or null
		if(s == null || s.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean checkIfDateBeforeCurrent(Date date) {
		Calendar dateToTest = Calendar.getInstance();
		dateToTest.setTime(date);
		Calendar currentDate = Calendar.getInstance();
		
		if(dateToTest.before(currentDate)) {
			return true;
		}
		return false;
	}

	public Date addMonthsToDate(Date date, int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}


	public Date addDaysToDate(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	public Date addMinutesToDate(Date date, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

	public Date subtractMinutesToDate(Date date, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, - minutes);
		return calendar.getTime();
	}

	public int getDaysToDate(Date date) {
		LocalDate to = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        Period diff = Period.between(now, to);
 
	    return diff.getDays();
	}
	
	public long getDaysBetweenDate1AndDate2(Date date1, Date date2) {
		LocalDate from = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate to = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
 
        Period diff = Period.between(from, to);
 
	    return diff.getDays();
	}

	public int getMonthsBetweenCurrentDateAndRequiredDate(Date date) {
		LocalDate requiredDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
 
        Period diff = Period.between(now, requiredDate);
 
	    return diff.getMonths();
	}

	public int getMonthsBetweenDates(Date date1, Date date2) {
		LocalDate localDate1 = getLocalDateFromDate(date1);
        LocalDate localDate2 = getLocalDateFromDate(date2);
 
        Period diff = Period.between(localDate1, localDate2);
 
	    return diff.getMonths();
	}

	private LocalDate getLocalDateFromDate(Date date) {
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
//		
//	    String dateStr = formatter.format(date);
//	    
//	    DateTimeFormatter formatter_1=DateTimeFormatter.ofPattern("yyyy-MM-dd-hh.mm.ss");
	    LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
	    
	    return localDate;
	}
	
	public String getHoursMinAndSecondsBetweenCurrentDateAndRequiredDate(Date date) {

		String toReturn = "";
		Date startDate = Calendar.getInstance().getTime();
	    //milliseconds
	    long different = date.getTime() - startDate.getTime();

	    long secondsInMilli = 1000;
	    long minutesInMilli = secondsInMilli * 60;
	    long hoursInMilli = minutesInMilli * 60;
	    long elapsedHours = different / hoursInMilli;
	    different = different % hoursInMilli;
	    
    	long elapsedMinutes = different / minutesInMilli;
	    different = different % minutesInMilli;

	    long elapsedSeconds = different / secondsInMilli;
	    
	    if(elapsedHours > 4) {

		    toReturn += elapsedHours + " Hours";
		    
	    }else if(elapsedHours > 1) {

		    toReturn += elapsedHours + " Hours";
	    }else {
	    	toReturn += elapsedMinutes +  " minutes ";
	    	if(elapsedMinutes < 10) {
	    		toReturn += elapsedSeconds +  " seconds";
	    	}
	    }
	    

	    return toReturn;

	}

	public String getMonthName(Date date) {
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("MMM");
		return simpleDateformat.format(date);
	}
	
	
	
/*
 * 	get day name for the date
 */
	public String getDateDayOfWeek(Date date) {
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("E");
		return simpleDateformat.format(date);
		
	}
/*
 * 	get the month (int) for the date
 */
	@SuppressWarnings("static-access")
	public int getDateMonth(Date date) {
		Calendar dateToTest = Calendar.getInstance();
		dateToTest.setTime(date);
		return dateToTest.get(dateToTest.MONTH);
	}

/*
 * 	get the month (int) for the date
 */
	@SuppressWarnings("static-access")
	public int getCurrentMonth() {
		Calendar dateToTest = Calendar.getInstance();
		
		return dateToTest.get(dateToTest.MONTH);
	}
	
	/*
 * 	return true if both dates are in the same month
 */
	public boolean checkIfDatesInSameMonth(Date date1, Date date2) {

		if(getDateMonth(date1) == getDateMonth(date2)) {
			return true;
		}
		return false;
	}

/*
 * 	returns true if date1 is after date2
 */
	public boolean checkIfDate1AfterThanDate2(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		
		if(calendar1.after(calendar2)) {
			return true;
		}
		return false;
	}

	public Date getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}
	
	public Date convertStringToDate(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
        try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			logger.info("exception : "+e.getMessage());
			return new Date();
		}
	}

	public Date convertStringToDateAndTime(String dateWithTime){
		Date date = new Date();
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateWithTime);
	        return date;
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public boolean checkIfeventActive(SessionDayTimeBean sessionDayTime) {
		
		Date date = convertStringToDate(sessionDayTime.getDate());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
        String sessionDate = dateFormat.format(date);  
		String sessionStartTime = sessionDayTime.getStartTime();
		String sessionEndTime = sessionDayTime.getEndTime();
		Date sessionStartDateTime = new Date();
		Date sessionEndDateTime = new Date();
		
		
		try {
			sessionStartDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " " + sessionStartTime);
			sessionStartDateTime = subtractMinutesToDate(sessionStartDateTime, 60);
			sessionEndDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " " + sessionEndTime);
			if(!checkIfDateBeforeCurrent(sessionStartDateTime)) {
				return false;
			}else if(checkIfDateBeforeCurrent(sessionEndDateTime)) {
				return false;
			}
			return true;
		} catch (ParseException e) {
			return false;
		} 
	}
	

	public SessionDayTimeBean addStartAndEndDate(SessionDayTimeBean sessionDayTime) {
		
		Date date = convertStringToDate(sessionDayTime.getDate());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
        String sessionDate = dateFormat.format(date);  
		String sessionStartTime = sessionDayTime.getStartTime();
		String sessionEndTime = sessionDayTime.getEndTime();
		sessionDayTime.setStartDate(sessionDate + " " + sessionStartTime);
		sessionDayTime.setEndDate(sessionDate + " " + sessionEndTime);
		
		return sessionDayTime;
	}
	
	public String getETAString(SessionDayTimeBean sessionDayTime) {
		String etaString = "";
		Date date = convertStringToDate(sessionDayTime.getDate());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
        String sessionDate = dateFormat.format(date);  
		String sessionTime = sessionDayTime.getStartTime();
		Date sessionDateTime = new Date();
		
		try {
			sessionDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " " + sessionTime);
			if(checkIfDateBeforeCurrent(sessionDateTime)) {
				return null;
			}
		} catch (ParseException e) {
			return "";
		} 

		int monthsLeft = getMonthsBetweenCurrentDateAndRequiredDate(sessionDateTime);
		if(monthsLeft > 0) {
			if(monthsLeft > 1) {
				etaString += monthsLeft + " Months ";
			}else {
				etaString += monthsLeft + " Month ";
			}
		}
		int daysLeft = getDaysToDate(sessionDateTime);
		
		if(daysLeft > 0) {
			if(daysLeft > 1) {
				etaString += daysLeft + " Days ";
			}else {
				etaString += daysLeft + " Day ";
			}
		}
		
		if(daysLeft < 2) {
			etaString += getHoursMinAndSecondsBetweenCurrentDateAndRequiredDate(sessionDateTime);
		}
		
		
		return etaString;
	}
	
	
	public boolean checkIfNumberInRange(int value, int min, int max) {
		if(min <= value && max >= value) {
			return true;
		}
		return false;
	}
}
