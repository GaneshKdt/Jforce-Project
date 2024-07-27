package com.nmims.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.interfaces.AdhocFileUploadInteface;
import com.nmims.services.AdhocFileUploadService;

@Controller
@RequestMapping("admin")
public class AdhocAdminFileUploadController extends BaseController implements AdhocFileUploadInteface{

	@Value("${AWS_ADHOC_FILE_ACCESS_URL}")
	private String AWS_ADHOC_FILE_ACCESS_URL;
	
	@Value("${ADHOC_FILE_UPLOAD_EXTENSIONS}")
	private String ADHOC_FILE_UPLOAD_EXTENSIONS;
	
	@Autowired
	AdhocFileUploadService uploadService;
	
	private static final Logger logger = LoggerFactory.getLogger("adhocAdminFileUploadService");

	@RequestMapping(value = "/adhoc-upload-file-form", method = { RequestMethod.GET })
	@Override
	public ModelAndView uploadFileForm(HttpServletRequest request, HttpServletResponse response) {

		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}

		ModelAndView modelAndView = new ModelAndView("fileUpload");

		String userId = (String) request.getSession().getAttribute("userId_acads");
		try {
			ArrayList<ContentAcadsBean> urlList = uploadService.getAllUploadedDocumentsURLByAdmin(userId);
			modelAndView.addObject("urlList", urlList);

		} catch (Exception e) {
			// TODO: handle exception
			  
		}

		return modelAndView;
	}

	@RequestMapping(value = "/adhoc-upload-file", method = { RequestMethod.POST })
	@Override
	public ModelAndView uploadFile(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("file") MultipartFile file) {
		String title=request.getParameter("title");

		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}

		ModelAndView modelAndView = new ModelAndView("fileUpload");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		
			//check for valid the extension to upload on server
			String extension = "."+FilenameUtils.getExtension(file.getOriginalFilename());
			
			if(!ADHOC_FILE_UPLOAD_EXTENSIONS.contains(extension) ) {
				modelAndView.addObject("error","true");
				modelAndView.addObject("errorMessage", "File type not supported!");
				ArrayList<ContentAcadsBean> urlList = uploadService.getAllUploadedDocumentsURLByAdmin(userId);
				modelAndView.addObject("urlList", urlList);
				return modelAndView;
			}
			
			try {
				String updatedfileName=uploadService.fileUpload(file, userId, title, logger);
				updatedfileName=AWS_ADHOC_FILE_ACCESS_URL+updatedfileName;
				ArrayList<ContentAcadsBean> urlList = uploadService.getAllUploadedDocumentsURLByAdmin(userId);
				modelAndView.addObject("file_path", updatedfileName);
				modelAndView.addObject("urlList", urlList);
				modelAndView.addObject("success","true");
				modelAndView.addObject("successMessage", "File Uploaded Successfully!");
				return modelAndView;
			} catch (DuplicateKeyException e) {
				// TODO Auto-generated catch block
				  
				ArrayList<ContentAcadsBean> urlList = uploadService.getAllUploadedDocumentsURLByAdmin(userId);
				modelAndView.addObject("urlList", urlList);
				modelAndView.addObject("error","true");
				modelAndView.addObject("errorMessage", "This title already present. Please try again with different file title!");

				return modelAndView;
			}catch (Exception e) {
				// TODO: handle exception
				  
				ArrayList<ContentAcadsBean> urlList = uploadService.getAllUploadedDocumentsURLByAdmin(userId);
				modelAndView.addObject("urlList", urlList);
				modelAndView.addObject("error","true");
				modelAndView.addObject("errorMessage", "Couldn't upload file. Please may try again!");
				StringWriter errors = new StringWriter();
	            e.printStackTrace(new PrintWriter(errors));
	            logger.info("Error in Getting data from api (uploadFile) "+errors.toString());
				return modelAndView;
			}
	}
	
	@RequestMapping(value = "/adhocdeleteFile", method = { RequestMethod.GET})
	@Override
	public ModelAndView deleteDocument(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView=new ModelAndView("fileUpload");
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		
		String userId = (String) request.getSession().getAttribute("userId_acads");
		
		String id=request.getParameter("id");
		String filePath=uploadService.getFilePath(id);
		
		boolean flag=uploadService.deleteFileURLByAdmin(id, filePath, logger);
		if(flag) {
		ArrayList<ContentAcadsBean> urlList = uploadService.getAllUploadedDocumentsURLByAdmin(userId);
		modelAndView.addObject("urlList", urlList);
		modelAndView.addObject("success","true");
		modelAndView.addObject("successMessage", "File Deleted Successfully!");
		}else{
			ArrayList<ContentAcadsBean> urlList = uploadService.getAllUploadedDocumentsURLByAdmin(userId);
			modelAndView.addObject("urlList", urlList);
			modelAndView.addObject("error","true");
			modelAndView.addObject("errorMessage", "Couldn't delete file. Please try again!");
		}
		
	
		return modelAndView;
	}
	
}
