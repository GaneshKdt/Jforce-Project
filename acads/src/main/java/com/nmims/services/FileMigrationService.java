package com.nmims.services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.nmims.beans.FileMigrationBean;
import com.nmims.daos.ContentDAO;
import com.nmims.helpers.AWSHelper;
import com.nmims.util.ContentUtil;

@Service("fileMigrationService")
public class FileMigrationService 
{
	
	private static final Logger aws_logger = LoggerFactory.getLogger("fileMigrationService");
	
	HashMap<String,String> s3_response = null;
	
	@Autowired
	AWSHelper amazons3;
	
	
	@Autowired
	ContentDAO contentdao;

	public int batchJobForExistingFileData(List<FileMigrationBean> filesDetails,String bucketName,String contentType)
	{
		s3_response = new HashMap<String,String>();
		
		
		
		List<String> error_list = new ArrayList<String>();
		List<String> success_list  = new ArrayList<String>();
		List<String> success_delete_count = new ArrayList<String>();
		List<String> error_delete_count = new ArrayList<String>();
		int counter = 0;
		
		System.out.println("Total size: "+filesDetails.size());
		System.out.println("shiftingDataInS3 Start");
		aws_logger.info("Batch Job File Shifting Started --- ");
		aws_logger.info("Total size to be transfered : "+filesDetails.size());
		
		
		for(FileMigrationBean filesDetail:filesDetails)
		{
		try 
		{
			System.out.println("SUCCESS/FAILURE COUNT "+success_list.size()+"/"+ error_list.size());
			
			
			System.out.println("Iteration:- "+(++counter)+"/"+filesDetails.size());
		
		
			final File folder = new File(filesDetail.getFilePath());
			
			String baseUrl = FilenameUtils.getPath(filesDetail.getFilePath());
			String fileName = FilenameUtils.getBaseName(filesDetail.getFilePath())
		                + "." + FilenameUtils.getExtension(filesDetail.getFilePath());
		
		
			fileName = baseUrl + fileName;
		
			if(!folder.exists()) {
				//check whether file is present on s3 or not
				
				s3_response = amazons3.checkWhetherFilePresentOnS3(bucketName, fileName);
				
				if(s3_response.get("status").equals("success")) {
					filesDetail.setFilePath(s3_response.get("url"));
					updateFileUrlLink(filesDetail,contentType);
					success_list.add("File Details " +filesDetail.toString());
					continue;
				}
				
				aws_logger.info(" checkWhetherFilePresent response :-  "+s3_response.get("url"));
				error_list.add(s3_response.get("url") +filesDetail.toString());
				continue;
				
			}
		
		
		
			s3_response = amazons3.uploadLocalFileX(filesDetail.getFilePath(),fileName,bucketName,baseUrl);
		
	
			String OriginalPath = filesDetail.getFilePath();
	
			if(s3_response.get("status").equals("success")) {
				filesDetail.setFilePath(s3_response.get("url"));
				
				//Update FilePath In DB
				if((updateFileUrlLink(filesDetail,contentType)) > 0) {
					
					//Delete File From Local
					if(deleteFileFromLocal(OriginalPath) == 1)
					{
						aws_logger.info("SuccessFully Deleted File From Local "+OriginalPath);
						success_delete_count.add(OriginalPath);
					}
					else {
						aws_logger.info("Error in deleting File From Local "+OriginalPath);
						error_delete_count.add(OriginalPath);
						
					}
					aws_logger.info("SuccessFully added file in amazon s3 and DB.  "+filesDetail.toString());
					success_list.add("File Details " +filesDetail.toString());
				}else 
					aws_logger.info("Error In uploading in database.(S3 Uploaded SuccessFully) "+filesDetail.toString());
	
			}else {
				
	
				aws_logger.error("Error:- "+s3_response.get("url"));
				aws_logger.info("Error In getting data from s3 Service "+filesDetail.toString());
				error_list.add("File Details " +filesDetail.toString());
			
				
			}
		
		}catch(Exception e)
		{
			e.printStackTrace();
			aws_logger.error("Upload File Error ",e);
			aws_logger.info("Error in uploading File."+filesDetail.toString());
			error_list.add("File Details " +filesDetail.toString());
			
		}//END OF TRY AND CATCH
		
		
		
		}//END OF LOOP
		
		
		System.out.println("SUCCESS/FAILURE COUNT "+success_list.size()+"/"+ error_list.size());
		System.out.println("SUCCESS/FAILURE DELETE FROM LOCAL COUNT "+success_delete_count.size()+"/"+ error_delete_count.size());
		
		
		System.out.println("Total Files "+filesDetails.size());
		System.out.println("Success_count "+success_list.size());
		System.out.println("Error Count "+error_list.size());
		System.out.println("Error_list "+error_list.toString());
		
		aws_logger.info("Total Files "+filesDetails.size());
		aws_logger.info("Success_count "+success_list.size());
		aws_logger.info("success_list "+success_list.toString());
		aws_logger.info("Error Count "+error_list.size());
		aws_logger.info("Error_list "+error_list.toString());
		aws_logger.info("SUCCESS/FAILURE DELETE FROM LOCAL COUNT "+success_delete_count.size()+"/"+ error_delete_count.size());
		aws_logger.info("Error in Local Delete File  "+error_delete_count.toString());
		
		return 1;
	
		}//END OF BATCH JOB METHOD
	
	
		public int deleteFileFromLocal(String filePath)
		{
			boolean result = false;
			try {
	       	File file = new File(filePath);
	       	result = file.delete();
	       	
			}
			catch(Exception e)
			{
				aws_logger.error("Error While Deleting file From local ",e);
			}
	        if(result)
	        	return 1;
	        else
	        	return 0;
	       
	       
	          
		}
		
		

		public int contentFileData(String contentType)
		{
			List<FileMigrationBean> contents = new ArrayList<FileMigrationBean>();
			if(contentType.equalsIgnoreCase("current")) {
			 contents = contentdao.getContentFromLocal();
			}else {
				contents = contentdao.getContentHistoryFromLocal();
			}
			return batchJobForExistingFileData(contents,"contentfilesngasce",contentType);
			
		}
		
		
		
		public int updateFileUrlLink(FileMigrationBean fileData,String contentType)
		{
			//final int index = fileData.getFilePath().indexOf("/", fileData.getFilePath().indexOf("/") + 1);
			//fileData.setFilePath(fileData.getFilePath().substring(index + 1));
	
			int update = 0;
			
			if(contentType.equalsIgnoreCase("current")) {
				
				update = contentdao.updateContentUrlLink(fileData);
				if(update > 0)
					contentdao.updateContentUrlLinkInDenormalized(fileData);
				
			}else {
					update = contentdao.updateContentHistoryUrlLink(fileData);
			}
			return update;
			
		}
		
}
