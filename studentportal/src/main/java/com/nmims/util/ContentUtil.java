package com.nmims.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.nmims.beans.VideoContentStudentPortalBean;

/**
 * @author Siddheshwar_Khanse
 * */

public interface ContentUtil {

	public static final String acadMonth = "Jan";

	/**
	 * @param	values in the form of List of Sting
	 * @return	String	This method get values in list and return single string
	 */
	public static String frameINClauseString(List<String> values){
		StringBuilder sb=null; 
		sb = new StringBuilder();
		
		//If list of string is empty or null then return empty string
		if(values == null || values.isEmpty())
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
	 * This method is used to to prepare date as YYYY-MM-DD from given year and month value
	 * @param month - academic cycle month
	 * @param year - academic cycle year
	 * @return String - return a string in YYYY-MM-DD format e.g. 2021-07-01 
	 */
	public static String pepareAcadDateFormat(String month,String year) {
		String frameDate = year+"-"+month+"-01";
		String acadDateFormat= "";
		
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MMM-dd");
		Date date;
		
		try {
		    date = parser.parse(frameDate);
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		    acadDateFormat = formatter.format(date);
		} catch (ParseException e1) {
		    //e1.printStackTrace();
		}
		
		//return acadDateFormat
		return acadDateFormat;
	}
	
	/**
	 * This method is used to sort the given video content list in the descending order based on video content id.
	 * @param videoContentList - contains video content bean details.  
	 * @return List - return sorted video content list in descending order. 
	 */
	public static List<VideoContentStudentPortalBean> sortInDesc(List<VideoContentStudentPortalBean> videoContentList){
		//Sort the video content beans in descending order by video content id
		videoContentList= videoContentList.stream().sorted(
				(video1,video2)->(video1.getId() > video2.getId())? -1 :(video1.getId() < video2.getId()) ? 1 : 0 
				).collect(Collectors.toList());
		
		//return sorted list
		return videoContentList;
	}//sortInDesc()
	
	/* Added By Riya For Last Cycle Content*/
	
	public static String findLastAcadDate(String year,String month) 
	{
		String date = "";

		try {
			
		    if(month.equals(acadMonth))
		    	 date =	String.valueOf(Integer.parseInt(year)-1)+"-07-01"; //If it's acadMonth is Jan, then it should show of Jul
		    else
		    	 date = year+"-01-01" ; //If it's acadMonth is Jul, then it should show of Jan
		   
		    return date;
		    
			
		}catch(Exception e)
		{
			
			
			return "";
		}
		
	} 
	
	/* 
	Logic 2 Academic  Cycle live
	
	acadContentLiveOrder : Most recent content live order
	
	reg_order : Student's acad month and year order
	If true:- Content will be shown According to Student's acad cycle
	If false:- Content will be shown According to Current cycle live
	
	*/
    
    public static String getCorrectOrderAccordTo2AcadContentLive(double acadContentLiveOrder, double reg_order,String reg_month,String reg_year,String curr_month,String curr_year,boolean isCurrent)
    {
    	
		StringBuffer acadDateFormat = new StringBuffer();
			
		if(isCurrent) {
				/*========================  FOR CURRENT CONTENT MONTH AND YEAR  ========================  */
		if(reg_order == acadContentLiveOrder)
			acadDateFormat.append(ContentUtil.pepareAcadDateFormat(reg_month,reg_year));
		else
			acadDateFormat.append(ContentUtil.pepareAcadDateFormat(curr_month,curr_year));
		
				/*========================  END ========================  */
		}else {
			
			/*========================  FOR LAST CONTENT MONTH AND YEAR  ========================  */
			
			if(reg_order == acadContentLiveOrder)
				acadDateFormat.append(ContentUtil.findLastAcadDate(reg_year,reg_month));
			else
				acadDateFormat.append(ContentUtil.findLastAcadDate(curr_year,curr_month)); 
			
			
			
			/*========================  END ========================  */
		}
		return acadDateFormat.toString();
    }
    
    
}

