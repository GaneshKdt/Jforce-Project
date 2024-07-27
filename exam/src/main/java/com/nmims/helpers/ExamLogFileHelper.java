package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

import com.nmims.beans.LogFileAnalysisBean;

public class ExamLogFileHelper {
	
	public ArrayList<String> getExamLogsFromLogFile(LogFileAnalysisBean bean, ArrayList<LogFileAnalysisBean> questionIdList) throws FileNotFoundException {

		FileReader fr = null;
		BufferedReader br = null;
		ArrayList<String> logList = new ArrayList<>();
		ArrayList<String> listOfFiles = new ArrayList<>();
		boolean found = false;
		String log="";
		
		final File folder = new File("E:\\Studentzone_logs\\exam_logs");
		listOfFiles = listFilesForFolder(folder);
		
		for(String fileName : listOfFiles ) {
			
			
			fr = new FileReader("E:\\Studentzone_logs\\exam_logs\\"+fileName);
			br = new BufferedReader(fr);
			String currentLine;
			
			try {
				while ((currentLine = br.readLine()) != null) {
					
					if( currentLine.contains(bean.getSapid()) && currentLine.contains(bean.getTestId()) &&
							 !currentLine.contains("Exception") && !found ){
						
						found = true;
						log = currentLine;
						
					}else if ( found ) {

						if( !StringUtils.isBlank(currentLine) ) {
							if( currentLine.contains("Closing WebApplicationContext") ) {
								log+="\n"+currentLine+"\n";
								found = false;
							}else
								log+="\n"+currentLine+"\n";
						}else {
							logList.add(log);
							found = false;
						}
						
					}
				}
				
				
			} catch (IOException e) {
				
			}
			
			
			if( "exams.log".equals(fileName) && logList.size() > 0) {
				break;
			}else 
				continue;
		}


		return logList;
	}

	public ArrayList<String> listFilesForFolder(final File folder) {
		
		ArrayList<String> listOfFiles = new ArrayList<>();
		
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	listOfFiles.add(fileEntry.getName());
	        }
	    }
	    
	    return listOfFiles;
	}
}
