package com.nmims.awsfileupload;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.dto.AwsDto;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AWSFileUploadAPI {
	
	@Autowired
	private AWSFileUploadHelper awsFileUploadHelper;
	
	@Value("${ZOOM_AUDIO_TARGET_FOLDER}")
	private String ZOOM_AUDIO_TARGET_FOLDER;
	
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public @ResponseBody HashMap<String, String> upload(HttpServletRequest request) {
		String filePath = request.getParameter("filePath");
		String keyName = request.getParameter("keyName");
		String bucketName = request.getParameter("bucketName");
		String publicFlag = request.getParameter("public");
		System.out.println(" aws project filePath: " + filePath);
		HashMap<String, String> response = new HashMap<String, String>();
		if(filePath == null || keyName == null || bucketName == null) {
			response.put("status", "error");
			response.put("message", "missing request param filePath or keyName");
			return response;
		}
		if(publicFlag == null) {
			publicFlag = "true";
		}
		try {
			filePath = java.net.URLDecoder.decode(filePath,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
		}
		System.out.println("--->>>>> decoded filePath: " + filePath);
		if("true".equalsIgnoreCase(awsFileUploadHelper.uploadIntoSessionAudioRecordingS3Bucket(filePath, keyName, bucketName,publicFlag))) {
			response.put("status", "success");
			response.put("fileUrl", "https://" + bucketName + ".s3-ap-south-1.amazonaws.com/" + keyName);
		}else {
			response.put("status", "error");
			response.put("message", "failed to upload file");
		}
		return response;
	}
	
	
	@RequestMapping(value = "/uploadAudioFile", method = RequestMethod.GET)
	public @ResponseBody HashMap<String, String> uploadAudioFile(HttpServletRequest request) {
		String filePath = request.getParameter("filePath");
		String keyName = request.getParameter("keyName");
		String bucketName = request.getParameter("bucketName");
		String publicFlag = request.getParameter("public");
		System.out.println("filePath: " + filePath);
		HashMap<String, String> response = new HashMap<String, String>();
		if(filePath == null || keyName == null || bucketName == null) {
			response.put("status", "error");
			response.put("message", "missing request param filePath or keyName");
			return response;
		}
		if(publicFlag == null) {
			publicFlag = "true";
		}
		try {
			filePath = java.net.URLDecoder.decode(filePath,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
		}
		String fileName = RandomStringUtils.randomAlphanumeric(20) + "_" + RandomStringUtils.randomAlphanumeric(20) + ".m4a";
		URL url;
		File file = new File(ZOOM_AUDIO_TARGET_FOLDER + File.separator + fileName);
		try {
			url = new URL(filePath);
			Path targetPath = file.toPath();
			Files.copy(url.openStream(),targetPath,StandardCopyOption.REPLACE_EXISTING);
			System.out.println("---->>>>> file trying to upload: " + ZOOM_AUDIO_TARGET_FOLDER + fileName);
			if("true".equalsIgnoreCase(awsFileUploadHelper.uploadIntoSessionAudioRecordingS3Bucket(ZOOM_AUDIO_TARGET_FOLDER + fileName, keyName, bucketName,publicFlag))) {
				response.put("status", "success");
				response.put("fileUrl", "https://" + bucketName + ".s3-ap-south-1.amazonaws.com/" + keyName);
			}else {
				response.put("status", "error");
				response.put("message", "failed to upload file");
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.put("status", "error");
			response.put("message", e.getMessage());
		}
		file.delete();
		return response;
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public @ResponseBody String test(HttpServletRequest request) {
		return "Hello World";
	}
	
	
	
	/*To upload the MultipartFile files to s3 server
	 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
	 * @param:- bucketName- Name of the bucket present in the s3
	 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
	 */
	
	
	@RequestMapping(value = "/uploadFiles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody HashMap<String, String> uploadFiles(HttpServletRequest request,@RequestParam(value="filex") MultipartFile file) {
		String folderPath = request.getParameter("folderPath");
		String keyName = request.getParameter("keyName");
		String bucketName = request.getParameter("bucketName");
		
		HashMap<String, String> response = new HashMap<String, String>();
		
		System.out.println("Upload in amazon Started---  ");
		
		if(folderPath == null || keyName == null || bucketName == null) {
			response.put("status", "error");
			response.put("fileUrl", "missing request param filePath or keyName");
			return response;
		}

		
		
		
		if("error".equalsIgnoreCase(awsFileUploadHelper.createFolderOns3(bucketName,folderPath))) {
			response.put("status", "error");
			response.put("fileUrl", "failed to create folder. ");
			return response;
		}
		
		
		
		if("true".equalsIgnoreCase(awsFileUploadHelper.uploadMultipartFile(keyName, bucketName,file))) {
			response.put("status", "success");
			response.put("fileUrl", "https://" + bucketName + ".s3-ap-south-1.amazonaws.com/" + keyName);
		}else {
			response.put("status", "error");
			response.put("fileUrl", "failed to upload file on amazon s3.");
		}
		
		System.out.println("Upload in amazon Completed---  ");
		
		return response;
	}
	
	/*To Transfer the local files to s3 server
	 * @Param:- filePath - Whole Path File on local eg. E:\Content\Management Theory and Practice\TRY.pdf
	 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
	 * @param:- bucketName- Name of the bucket present in the s3
	 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
	 */
	
	
	@RequestMapping(value = "/uploadLocalFile", method = RequestMethod.GET)
	public @ResponseBody HashMap<String, String> uploadLocal(HttpServletRequest request) {
		
		String filePath = request.getParameter("filePath"); 
		String keyName = request.getParameter("keyName"); 
		String bucketName = request.getParameter("bucketName");
		String folderPath = request.getParameter("folderPath");
		
		
		System.out.println("Upload in amazon Started---  ");
		
		HashMap<String, String> response = new HashMap<String, String>();
		if(filePath == null || keyName == null || bucketName == null || folderPath == null) {
			response.put("status", "error");
			response.put("fileUrl", "missing request param filePath or keyName");
			return response;
		}
		
		if("error".equalsIgnoreCase(awsFileUploadHelper.createFolderOns3(bucketName,folderPath))) {
			response.put("status", "error");
			response.put("fileUrl", "failed to create folder. ");
			return response;
		}
		
		
		String result = awsFileUploadHelper.uploadFilesWithoutPublicAccess(filePath, keyName, bucketName);
		
		if("true".equalsIgnoreCase(result)) {
			response.put("status", "success");
			response.put("fileUrl", "https://" + bucketName + ".s3-ap-south-1.amazonaws.com/" + keyName);
		}else {
			response.put("status", "error");
			response.put("fileUrl", "failed to upload file on amazon s3."+result);
		}
		System.out.println("Upload in amazon Completed---  ");
		return response;
	}
	
	/*To delete file from s3 server
	 * @Param:- keyName - It is the name of file used in S3. eg. adhocfilesngasce\academics\TRY.pdf
	 * @param:- bucketName- Name of the bucket present in the s3
	 */
	
	
	@RequestMapping(value = "/deleteAdhocFiles", method = RequestMethod.POST)
	public @ResponseBody HashMap<String, String> deleteAdhocFiles(HttpServletRequest request) {
		String keyName = request.getParameter("keyName");
		String bucketName = request.getParameter("bucketName");
		System.out.println("IN deleteAdhocFiles ");
		System.out.println("keyName : "+keyName);
		System.out.println("bucketName : "+bucketName);
		HashMap<String, String> response = new HashMap<String, String>();
		
		if(keyName == null || bucketName == null) {
			response.put("status", "error");
			response.put("fileUrl", "missing request param filePath or keyName");
			return response;
		}
		
		if("true".equalsIgnoreCase(awsFileUploadHelper.deleteObjectFromS3(bucketName, keyName))) {
			response.put("status", "success");
		}else {
			response.put("status", "error");
			response.put("fileUrl", "failed to delete file");
		}
		
		return response;
	}
	
	
	//With DTO
	
	/*To upload the MultipartFile files to s3 server
	 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
	 * @param:- bucketName- Name of the bucket present in the s3
	 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
	 */
	
	
	@RequestMapping(value = "/uploadFilesX", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody HashMap<String, String> uploadFiles(@RequestParam(value="filex") MultipartFile file,
			@RequestParam(value="folderPath") String folderPath,
			@RequestParam(value="fileName") String keyName,
			@RequestParam(value="bucketName") String bucketName) {
		
		
		
		HashMap<String, String> response = new HashMap<String, String>();
		
		System.out.println("Upload in amazon Started---  ");
		
		if(folderPath == null || keyName == null || bucketName == null) {
			response.put("status", "error");
			response.put("fileUrl", "missing request param filePath or keyName");
			return response;
		}

		
		String folder_result = awsFileUploadHelper.createFolderOns3(bucketName,folderPath);
		
			if(!("true".equalsIgnoreCase(folder_result))) {
				response.put("status", "error");
				response.put("fileUrl", "failed to create folder. "+folder_result);
				return response;
			}
		
		
		String result = awsFileUploadHelper.uploadMultipartFile(keyName, bucketName,file);
		
		
		if("true".equalsIgnoreCase(result)) {
			response.put("status", "success");
			response.put("fileUrl", "https://" + bucketName + ".s3-ap-south-1.amazonaws.com/" + keyName);
		}else {
			response.put("status", "error");
			response.put("fileUrl", "failed to upload file on amazon s3."+result);
		}
		
		System.out.println("Upload in amazon Completed---  ");
		
		return response;
	}
	
	/*To Transfer the local files to s3 server
	 * @Param:- filePath - Whole Path File on local eg. E:\Content\Management Theory and Practice\TRY.pdf
	 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
	 * @param:- bucketName- Name of the bucket present in the s3
	 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
	 */
	
	
	@RequestMapping(value = "/uploadLocalFileX", method = RequestMethod.POST)
	public @ResponseBody HashMap<String, String> uploadLocal(@RequestBody AwsDto details) {
		
		String filePath = details.getFilePath(); 
		String keyName = details.getKeyName(); 
		String bucketName = details.getBucketName();
		String folderPath = details.getFolderPath();
		
	
		
		System.out.println("Upload in amazon Started---  ");
		
		HashMap<String, String> response = new HashMap<String, String>();
		if(filePath == null || keyName == null || bucketName == null || folderPath == null) {
			response.put("status", "error");
			response.put("fileUrl", "missing request param filePath or keyName");
			return response;
		}
		
		String folder_result = awsFileUploadHelper.createFolderOns3(bucketName,folderPath);
		
		if(!("true".equalsIgnoreCase(folder_result))) {
			response.put("status", "error");
			response.put("fileUrl", "failed to create folder. "+folder_result);
			return response;
		}
		
		
		String result = awsFileUploadHelper.uploadFilesWithoutPublicAccess(filePath, keyName, bucketName);
		
		if("true".equalsIgnoreCase(result)) {
			response.put("status", "success");
			response.put("fileUrl", "https://" + bucketName + ".s3-ap-south-1.amazonaws.com/" + keyName);
		}else {
			response.put("status", "error");
			response.put("fileUrl", "failed to upload file on amazon s3."+result);
		}
		System.out.println("Upload in amazon Completed---  ");
		return response;
	}
	
	@RequestMapping(value = "/checkFilePresentOnS3", method = RequestMethod.POST)
	public @ResponseBody HashMap<String, String> checkFilePresentOnS3(@RequestBody AwsDto details) {
		
		HashMap<String, String> response = new HashMap<String, String>();
		String keyName = details.getKeyName(); 
		String bucketName = details.getBucketName();
		
		String file_present = awsFileUploadHelper.filePresentOnS3(bucketName,keyName);
		
		if(file_present.equalsIgnoreCase("true")) {
			response.put("status", "success");
			response.put("fileUrl", "https://" + bucketName + ".s3-ap-south-1.amazonaws.com/" + keyName);
		}else {
			response.put("status", "error");
			response.put("fileUrl", "File Not Present On S3 [BucketName = "+bucketName+" , KeyName= "+keyName +"]. Error :- "+file_present);
		}
		
		return response;
	}
	
	@RequestMapping(value="/downloadFromS3",method= {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody HashMap<String, String> downloadFromS3(@RequestBody AwsDto details)
	{
		HashMap<String, String> response = new HashMap<String, String>();
		String keyName = details.getKeyName(); 
		String bucketName = details.getBucketName();
		String filePath=details.getFilePath();
		String result = awsFileUploadHelper.downloadFilesWithPublicAccess(filePath, keyName, bucketName);
		if(result.equalsIgnoreCase("true"))
		{
			response.put("status", "success");
		}
		else
		{
			response.put("status", "error");
		}
		return response;
	}
	
}
