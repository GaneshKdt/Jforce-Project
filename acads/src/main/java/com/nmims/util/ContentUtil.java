package com.nmims.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.YearMonth;

import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.nmims.beans.SearchBean;
import com.nmims.beans.VideoContentAcadsBean;

/**
 * @author Siddheshwar_Khanse
 * */

public interface ContentUtil {

	public final static String defaultHistoryDate = "2020Jul";
	
	
	/**
	 * @param	values in the form of List of Sting
	 * @return	String	This method get values in list and return single string
	 */
	public static String frameINClauseString(List<String> values){
		StringBuilder sb=null; 
		sb = new StringBuilder();
		
		//If list of string is empty or null then return empty string
		if(values==null || values.isEmpty())
			return "''";
		
		//Preparing a comma separated single string builder object of multiple list of string values
		for(String val:values){
			sb.append("\"");
			sb.append(val);
			sb.append("\",");
		}
		//Get the index of last comma
		int commaIndex=sb.lastIndexOf(",");
		//Remove the last comma
		if(commaIndex!=-1)
			sb.delete(commaIndex,commaIndex+1);
		
		//Return a string
		return sb.toString();
	}//frameINClauseString()
	
	/***
	 * This method is used to prepare single string of academic cycle in the YYYY-MM-DD format
	 * @param acadCycles list contains the unique pair of year and month 
	 * @return single string of formated dates
	 */
	public static String pepareAcadDateFormat(List<SearchBean> acadCycles){
		StringBuilder sb=null; 
		sb = new StringBuilder();
		
		//If list is empty or null then return empty string
		if(acadCycles == null || acadCycles.isEmpty())
			return "''";
		
		//Preparing a comma separated single string builder object of multiple list of values
		for(SearchBean search:acadCycles){
			String date = prepareAcadDateFormat(search.getMonth(), search.getYear());
			sb.append("'");
			sb.append(date);
			sb.append("',");
		}
	
		//Return a string
		String str = sb.toString();
		return str.substring(0, str.length()-1);
	}//frameINClauseString()
	
	/***
	 * This method is used to prepare sql date based on month and year
	 * @param cycle
	 * @return String return the prepared date in the form of YYYY-MM-DD
	 */
	public static String prepareAcadDateFormat(String cycle) {
		String date = "";
		String month=null,year=null;
		
		if(cycle == null)
			return "'2021-07-01'";
		
		else if(cycle.length() == 7) {
			month = cycle.substring(0, 3);
			year = cycle.substring(3, 7);
		}
		else if(cycle.length() == 8){
			month = cycle.substring(0, 3);
			year = cycle.substring(4, 8);
		}
		else if(cycle.length() == 9){
			month = cycle.substring(0, 3);
			year = cycle.substring(5, 9);
		}
		
		date = "'"+prepareAcadDateFormat(month,year)+"'";
		
		return date;
	}
	
	/***
	 * Prepare academic date format based on the year and month
	 * @param month contain academic cycle month
	 * @param year	contain academic cycle year
	 * @return the date in YYYY-MM-DD format
	 */
	public static String prepareAcadDateFormat(String month, String year) {
		String frameDate = year+"-"+month+"-01";
		String acadDateFormat= "";
		
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MMM-dd");
		Date date;
		
		try {
		    date = parser.parse(frameDate);
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		    acadDateFormat = formatter.format(date);
		} catch (ParseException e1) {
		   
		}
		
		return acadDateFormat;
	}
	
	/***
	 * This method is used to sort the video content bean based on the video content id in the descending order.
	 * @param videoContentList contains list of video content beans.
	 * @return video content list sorted in descending order.
	 */
	public static List<VideoContentAcadsBean> sortInDesc(List<VideoContentAcadsBean> videoContentList){
		
		//Sort the video content beans in descending order by video content id
		videoContentList= videoContentList.stream().sorted(
				(video1,video2)->(video1.getId() > video2.getId())? -1 :(video1.getId() < video2.getId()) ? 1 : 0 
				).collect(Collectors.toList());
		
		//return the sorted list
		return videoContentList;
	}
	
	// Find The acadDate For Current Cycle Content
	public static String prepareCurrentAcadDate(String year, String month) {
		String date = "";

		if ("Jan".equalsIgnoreCase(month))
			date = year + "-01-01";
		else if ("Apr".equalsIgnoreCase(month))
			date = year + "-04-01";
		else if ("Jul".equalsIgnoreCase(month))
			date = year + "-07-01";
		else if ("Oct".equalsIgnoreCase(month))
			date = year + "-10-01";
		return date;
	}

	// Find The acadDate For Last Cycle Content
	public static String prepareLastAcadDate(String year, String month) {
		String date = "";
		if ("Jan".equalsIgnoreCase(month))
			date = String.valueOf(Integer.parseInt(year) - 1) + "-07-01";
//		else if ("Apr".equalsIgnoreCase(month))
//			date = year + "-01-01";
		else if ("Jul".equalsIgnoreCase(month))
			date = year + "-01-01";
//		else if ("Oct".equalsIgnoreCase(month))
//			date = year + "-04-01";

		return date;
	}
	

	  public static int findContentHistoryValidDate(String c_month,String c_year){
 			
	   		if(c_month.equals("Jan"))
	   			c_month = "JANUARY";
	   		else
	   			c_month = "JULY";
	   		
	   		Month c_month_current = Month.valueOf(c_month);	
	   		Month c_month_history = Month.valueOf("JANUARY");
	   		
	   		YearMonth content_date_current = YearMonth.of(Integer.parseInt(c_year) ,c_month_current);
	   		YearMonth content_date_history = YearMonth.of(Integer.parseInt("2020") ,c_month_history);
	   		
	   		return content_date_current.compareTo(content_date_history);
	   	}

	/***
	 * This method will compare month and year to find out whether data belongs to current table or history table.
	 * @param currentDate :- Year+Month e.g 2021Jul
	 * @return :- -1 belongs to history table
	 *            +1 belongs to current table
	 *            0 belongs to current table
	 */
	
	
	 public static int findValidHistoryDate(String currentDate){
		 try {
			DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyyMMM");
			YearMonth yearMonth = YearMonth.parse(currentDate, inputFormat);

			DateTimeFormatter defaultDate = DateTimeFormatter.ofPattern("yyyyMMM");
			YearMonth yearMonth2 = YearMonth.parse(defaultHistoryDate, defaultDate);
	   		
	   	 	return yearMonth.compareTo(yearMonth2);
	   	 
		 }catch(Exception e){
			   
			 return 0;
		 }
	   	}
		

}
