package com.nmims.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.nmims.beans.CSAdminAuthorizationTypes;
import com.nmims.beans.FeatureTypes;
import com.nmims.beans.ReturnStatus;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.beans.VideoContentCareerservicesBean;
import com.nmims.beans.VideoContentTypes;
import com.nmims.daos.VideoRecordingDao;

@Controller
public class VideoContentController extends CSAdminBaseController {

	@Autowired
	VideoRecordingDao videoRecordingDao;
	
	Gson gson = new Gson();

	FeatureTypes featureTypes = new FeatureTypes();
	
	@RequestMapping(value = "/addVideoContent", method = RequestMethod.GET)
	public String addVideoContent(HttpServletRequest request, HttpServletResponse respnse, Model model, @RequestParam(value="id", required=false) String id) {
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		String userAuthorizationRoles = userAuthorization.getRoles();
		if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSSessionsAdmin)) {
			return "redirect:../studentportal/home";
		}
		
		if(id == null || id.equals("")) {
			id = "0";
		}
		
		model.addAttribute("status", "");
		model.addAttribute("message", "");
		
		model.addAttribute("id", id);
		model.addAttribute("userId", getUserId(request, respnse));
		
		model.addAttribute("AllVideoContent", videoRecordingDao.getAllVideoContent());

		model.addAttribute("title", "Add Video Content");
		model.addAttribute("tableTitle", "Add Video Content");
		model.addAttribute("url", "addVideoContent");
		return "admin/video_content/videoContent";
	}

	@RequestMapping(value = "/addVideoContent", method = RequestMethod.POST , consumes="application/json" , produces = "application/json")
	public ResponseEntity<String> addVideoContent(HttpServletRequest request, HttpServletResponse respnse, Model model, @RequestBody VideoContentCareerservicesBean requestParams ) {

		ReturnStatus returnStatus = new ReturnStatus();
		if(videoRecordingDao.uploadVideoContent(requestParams)) {
			returnStatus.setStatus("1");
			returnStatus.setMessage("Success");
		}else {
			returnStatus.setStatus("0");
			returnStatus.setMessage("error. check all the fields and try again");
		}
		return ResponseEntity.ok(gson.toJson(returnStatus));
	}
	

	@RequestMapping(value = "/updateVideoContent", method = RequestMethod.GET)
	public String updateVideoContent(HttpServletRequest request, HttpServletResponse respnse, Model model, @RequestParam(value="id", required=true) String id) {
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		String userAuthorizationRoles = userAuthorization.getRoles();
		if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSSessionsAdmin)) {
			return "redirect:../studentportal/home";
		}
		
		
		model.addAttribute("status", "");
		model.addAttribute("message", "");
		
		model.addAttribute("id", id);
		model.addAttribute("userId", getUserId(request, respnse));
		
		model.addAttribute("AllVideoContent", videoRecordingDao.getAllVideoContent());
		
		model.addAttribute("title", "Update Video Content");
		model.addAttribute("tableTitle", "Update Video Content");
		model.addAttribute("url", "updateVideoContent");
		
		return "admin/video_content/videoContent";
	}

	@RequestMapping(value = "/updateVideoContent", method = RequestMethod.POST , consumes="application/json" , produces = "application/json")
	public ResponseEntity<String> updateVideoContent(HttpServletRequest request, HttpServletResponse respnse, Model model, @RequestBody VideoContentCareerservicesBean requestParams ) {

		ReturnStatus returnStatus = new ReturnStatus();
		if(videoRecordingDao.updateVideoContent(requestParams)) {
			returnStatus.setStatus("1");
			returnStatus.setMessage("Success");
		}else {
			returnStatus.setStatus("0");
			returnStatus.setMessage("error. check all the fields and try again");
		}
		return ResponseEntity.ok(gson.toJson(returnStatus));
	}


	@RequestMapping(value = "/deleteVideoContent", method = RequestMethod.GET)
	public String deleteVideoContent(HttpServletRequest request, HttpServletResponse respnse, Model model, @RequestParam(value="id", required=true) String id) {
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		String userAuthorizationRoles = userAuthorization.getRoles();
		if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSSessionsAdmin)) {
			return "redirect:../studentportal/home";
		}
		
		if(videoRecordingDao.deleteVideoContent(id)) {
			model.addAttribute("status", "1");
			model.addAttribute("message", "Deleted");
		}
		model.addAttribute("id", "0");
		model.addAttribute("userId", getUserId(request, respnse));
		
		model.addAttribute("AllVideoContent", videoRecordingDao.getAllVideoContent());
		
		return "redirect:addVideoContent";
	}

	private String getUserId(HttpServletRequest request, HttpServletResponse respnse) {
		String userId = (String)request.getSession().getAttribute("userId");
		return userId;
	}

	@RequestMapping(value = "/m/getVideoContent", method = RequestMethod.GET , produces = "application/json")
	public ResponseEntity<String> getVideoContent(HttpServletRequest request, HttpServletResponse respnse, @RequestParam(value="id", required=true) String id) {
		
		VideoContentCareerservicesBean videoContent = new VideoContentCareerservicesBean();
		videoContent.setId("");
		if(id != "0") {
			videoContent = videoRecordingDao.getVideoContentById(id);
		}
		
		return ResponseEntity.ok(gson.toJson(videoContent));
	}
	

	@RequestMapping(value = "/m/getAllSessionsWithoutVideoContent", method = RequestMethod.GET , produces = "application/json")
	public ResponseEntity<String> getAllSessionsWithoutVideoContent(HttpServletRequest request, HttpServletResponse respnse) {

		List<SessionDayTimeBean> videoContent = videoRecordingDao.getSessionsWithoutVideoContent();
		
		return ResponseEntity.ok(gson.toJson(videoContent));
	}


	@RequestMapping(value = "/getOrientationVideoContent", method = RequestMethod.GET , produces = "application/json")
	public ResponseEntity<String> getOrientationVideoContent(HttpServletRequest request, HttpServletResponse respnse) {

		List<VideoContentCareerservicesBean> videoContent = videoRecordingDao.getAllVideoContentByTypeId(VideoContentTypes.ORIENTATION_VIDEO);
		
		return ResponseEntity.ok(gson.toJson(videoContent));
	}
}
