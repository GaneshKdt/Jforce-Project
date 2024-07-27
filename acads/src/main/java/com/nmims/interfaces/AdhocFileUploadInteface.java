package com.nmims.interfaces;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

public interface AdhocFileUploadInteface {
	
	public ModelAndView uploadFileForm(HttpServletRequest request, HttpServletResponse response);
	public ModelAndView uploadFile(HttpServletRequest request, HttpServletResponse response, @RequestParam("file") MultipartFile file);
	public ModelAndView deleteDocument(HttpServletRequest request, HttpServletResponse response);
	
}
