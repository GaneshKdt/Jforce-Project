package com.nmims.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.nmims.beans.ContentAcadsBean;

@Component
public class FileUploadHelper 
{
	
		@Autowired
		AWSHelper awshelper;
	
		@Value( "${CONTENT_PATH}" )
		private String CONTENT_PATH;
		
		
		@Value("${CONTENT_BUCKET_NAME}")
		private String CONTENT_BUCKET_NAME;
		
		@Value("${CONTENT_S3_FOLDER_PATH}")
		private String CONTENT_S3_FOLDER_PATH;
		
		public  String uploadContentFileOnS3(ContentAcadsBean bean, String subject) 
		{
		
		
		try {  
			
			CommonsMultipartFile file = bean.getFileData(); 
			
			String extension = "."+FilenameUtils.getExtension(file.getOriginalFilename());
			
			String[] allowedContentExtension = {".pdf",".xls",".xlsx",".ppt",".pptx",".flv",".mov",".doc",".mpeg",".docx",".mov",".avi",".zip",".rar"};
			
			if(!(Arrays.asList(allowedContentExtension).contains(extension.toLowerCase())))
				return "File type not supported.";
				
			subject = subject.replaceAll(":", "_");
			subject = subject.replaceAll("'", "_");
			subject = subject.replaceAll(",", "_");
			subject = subject.replaceAll("&", "and");
			subject = subject.replaceAll(" ", "_");

			String folderPath = CONTENT_S3_FOLDER_PATH  + subject + "/";
			long currentUnixTime = System.currentTimeMillis() / 1000L;
				
			String fileName =  currentUnixTime+RandomStringUtils.randomAlphanumeric(5)+extension;
				
			HashMap<String,String> result = awshelper.uploadFile(file,folderPath,CONTENT_BUCKET_NAME,folderPath+fileName);
				
			if(result.get("status").equals("success")) {
				bean.setFilePath(result.get("url"));
				bean.setPreviewPath(subject + "/"+fileName);
					
				return "success";
			}else {
					
				return "Error in uploading file for "+subject + ": " +result.toString();
				
					
			}
		} catch (Exception e) {   
			return "Error in uploading file for "+subject + " : "+ e.getMessage();
			   
			
		}   

		
	}

	//Upload File On Local
		
		public String uploadContentFile(ContentAcadsBean bean, String subject) {

			String errorMessage = "success";
			InputStream inputStream = null;   
			OutputStream outputStream = null;   

			CommonsMultipartFile file = bean.getFileData(); 
			String fileName = file.getOriginalFilename();   


			//Replace special characters in file
			//fileName = fileName.replaceAll("'", "_");
			//fileName = fileName.replaceAll(",", "_");
			//fileName = fileName.replaceAll("&", "and");
			//fileName = fileName.replaceAll(" ", "_");
			//fileName = fileName.replaceAll(":", "_");
			//fileName = fileName.replaceAll("'", "_");
			
			subject = subject.replaceAll(":", "_");
			subject = subject.replaceAll("'", "_");
			subject = subject.replaceAll(",", "_");
			subject = subject.replaceAll("&", "and");
			subject = subject.replaceAll(" ", "_");	
			try {   
				String tempFileNameLowerCase = "."+FilenameUtils.getExtension(file.getOriginalFilename());
				
			if(!(tempFileNameLowerCase.endsWith(".pdf")  || tempFileNameLowerCase.endsWith(".zip")  || tempFileNameLowerCase.endsWith(".rar")  || tempFileNameLowerCase.endsWith(".xls") || tempFileNameLowerCase.endsWith(".xlsx") || 
					tempFileNameLowerCase.endsWith(".ppt") || tempFileNameLowerCase.endsWith(".pptx") || tempFileNameLowerCase.endsWith(".doc") || tempFileNameLowerCase.endsWith(".docx") 
					|| tempFileNameLowerCase.endsWith(".flv") || tempFileNameLowerCase.endsWith(".mov") || tempFileNameLowerCase.endsWith(".mpeg") || tempFileNameLowerCase.endsWith(".mov") || tempFileNameLowerCase.endsWith(".avi") ) ){
				errorMessage = "File type not supported.";
				return errorMessage;
			}

				inputStream = file.getInputStream();   
				
				//added to avoid special characters in fileName
				
				long currentUnixTime = System.currentTimeMillis() / 1000L;
				fileName = currentUnixTime+RandomStringUtils.randomAlphanumeric(5)+tempFileNameLowerCase;
				
				String filePath = CONTENT_PATH + subject + "/" +fileName;
				String previewPath = subject + "/" + fileName;
				
				//Check if Folder exists which is one folder per Subject 
				File folderPath = new File(CONTENT_PATH  + subject );
				if (!folderPath.exists()) {   
					folderPath.mkdirs();   
				}   
				File newFile = new File(filePath);   
				outputStream = new FileOutputStream(newFile);   
				int read = 0;   
				byte[] bytes = new byte[1024];   

				while ((read = inputStream.read(bytes)) != -1) {   
					outputStream.write(bytes, 0, read);   
				}
				bean.setFilePath(filePath);
				bean.setPreviewPath(previewPath);
				outputStream.close();
				inputStream.close();
			} catch (IOException e) {   
				errorMessage = "Error in uploading file for "+bean.getSubject() + " : "+ e.getMessage();
				     
			}   
			return errorMessage;
		}
}
