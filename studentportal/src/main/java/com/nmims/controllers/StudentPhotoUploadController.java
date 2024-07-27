package com.nmims.controllers;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.StudentPhotoUploadBean;
import com.nmims.helpers.AWSHelper;
import com.nmims.helpers.SalesforceHelper;


@RestController
public class StudentPhotoUploadController {
	
	@Autowired
	SalesforceHelper salesforceHelper;
	
	@Value( "${STUDENT_UPLOADED_PHOTO_PATH}" )
	private String STUDENT_UPLOADED_PHOTO_PATH;
	
	@Autowired
	private AWSHelper awsHelper;
	
	@Value("${AWS_UPLOAD_URL}")
	private String AWS_UPLOAD_URL;
	
	
	
	@RequestMapping(value = "/saveUserProfileImage", method = RequestMethod.GET)
    public ModelAndView saveUserProfileImage(HttpServletRequest request, HttpServletResponse response) {
    	ModelAndView mv = new ModelAndView("jsp/webCam");
    	return mv;
    }
	
	
	@RequestMapping(value = "/checkCanUploadStudentPhoto", method = {RequestMethod.GET})
	public HashMap<String, String> checkCanUploadStudentPhoto(HttpServletRequest request){
		String registrationNo = request.getParameter("registrationNo");
		HashMap<String, String> response = new HashMap<String, String>();
		if(registrationNo == null) {
			response.put("status", "error");
			response.put("message","Invalid or not found registration number");
			return response;
		}
		String result = salesforceHelper.checkCanUpdateOrUploadStudentPhoto(registrationNo,3);
		if(result.indexOf("Error:") != -1) {
			response.put("status", "error");
			response.put("message",result);
		}else {
			response.put("status", "success");
			response.put("message","Successfully uploaded");
		}
		return response;
	}
	
	@RequestMapping(value = "/profileImageVerify", method = RequestMethod.GET)
	public ModelAndView  profileImageVerify(HttpServletRequest request, HttpServletResponse response) {
		String rId = request.getParameter("rid");
		if(rId == null) {
			return new ModelAndView("jsp/error404");
		}
		return new ModelAndView("redirect:https://ngasce.page.link/?link=https://studentzone-ngasce.nmims.edu/studentportal/saveUserProfileImage?registrationNo="+ rId +"&apn=com.ngasce.jforce&amv=405");
	}
	
	@RequestMapping(value = "/uploadStudentPhoto", method = {RequestMethod.POST})
	public HashMap<String, String>  uploadStudentPhoto(StudentPhotoUploadBean studentPhotoUploadBean) {
		HashMap<String, String> response = new HashMap<String, String>();
		if(studentPhotoUploadBean.getRegistrationNo() == null) {
			response.put("status", "error");
			response.put("message","Registration not found");
			return response;
		}
		try {
			String message = this.uploadImageFile(studentPhotoUploadBean);
			if(message == null) {
				String result = salesforceHelper.updateStudentPhotoFieldOnSFDC(studentPhotoUploadBean.getRegistrationNo(),studentPhotoUploadBean.getAwsFilePath(),3);
				if(result != null && result.indexOf("Error") != -1) {
					response.put("status", "error");
					response.put("message",result);
				}else {
					response.put("status", "success");
					response.put("message","Successfully uploaded student profile");
				}
			}else {
				response.put("status", "error");
				response.put("message",message);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			response.put("status", "error");
			response.put("message","Something went wrong we are working on it");
			response.put("error", e.getMessage());
		}
		return response;
	}

	
	private String uploadImageOnAWS(String filePath,String keyName) {
		try {
			filePath = java.net.URLEncoder.encode(filePath,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return "Error: While encoding filepath";
		}
		String url = AWS_UPLOAD_URL + "/upload?public=true&filePath=" + filePath + "&keyName=" + keyName + "&bucketName=salesforcestudentphoto";
		String result = awsHelper.uploadOnAWS(url);
		if(result != null) {
			return result;
		}
		return "Error: failed to upload on aws server"; 
	}
	
	private String uploadImageFile(StudentPhotoUploadBean studentPhotoUploadBean) {
		String errorMessage = null;
		InputStream inputStream = null;   
		OutputStream outputStream = null;
		
		CommonsMultipartFile file = studentPhotoUploadBean.getFileData(); 
		String fileName = file.getOriginalFilename();
		fileName = fileName.replaceAll("'", "_");
		fileName = fileName.replaceAll(",", "_");
		fileName = fileName.replaceAll("&", "and");
		fileName = fileName.replaceAll(" ", "_");
		String extension = "png";
		if(!"blob".equalsIgnoreCase(fileName)) {
			extension =  fileName.substring(fileName.lastIndexOf("."), fileName.length());
		}
		fileName = "profile_" + studentPhotoUploadBean.getRegistrationNo() + "." + extension;
		
		if(!(fileName.toUpperCase().endsWith(".JPEG") ) && !(fileName.toUpperCase().endsWith(".PNG")) && !(fileName.toUpperCase().endsWith(".JPG")) ){
			errorMessage = "File type not supported. Please upload .jpeg,.jpg,.png file only.";
			return errorMessage;
		}
		
		try {    
			inputStream = file.getInputStream();   
			String filePath = STUDENT_UPLOADED_PHOTO_PATH +fileName;

			String previewPath =  "/" + fileName;
			//Check if Folder exists which is one folder per Exam (Jun2015, Dec2015 etc.) 
			File folderPath = new File(STUDENT_UPLOADED_PHOTO_PATH);
			if (!folderPath.exists()) {   
				folderPath.mkdirs();   
			}   

			// System.out.println("File Path = "+filePath);
			File newFile = new File(filePath);   


			outputStream = new FileOutputStream(newFile);   
			int read = 0;   
			byte[] bytes = new byte[1024];   

			while ((read = inputStream.read(bytes)) != -1) {   
				outputStream.write(bytes, 0, read);   
			}
			studentPhotoUploadBean.setFilePath(filePath);
			studentPhotoUploadBean.setPreviewPath(previewPath);
			outputStream.close();
			inputStream.close();
			String result = this.uploadImageOnAWS(filePath,fileName);
			if(result.indexOf("Error:") != -1) {
				newFile.delete();
				return "Error: Failed to upload file on server";
			}
			studentPhotoUploadBean.setAwsFilePath(result);
			newFile.delete();
		} catch(Exception e){
			errorMessage = e.getMessage();
			//e.printStackTrace();
		}
		studentPhotoUploadBean.setFileName(fileName);
		return errorMessage;
	}
}
