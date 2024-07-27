package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import com.google.gson.Gson;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.PackageFamily;
import com.nmims.beans.AboutCS;
import com.nmims.beans.CSResponse;
import com.nmims.beans.TermsAndConditions;
import com.nmims.beans.PacakageAvailabilityBean;
import com.nmims.beans.UpgradePathDetails;
import com.nmims.beans.AvailablePackagesModelBean;
import com.nmims.daos.PackageApplicabilityDAO;
import com.nmims.helpers.DataValidationHelpers;

@Controller
public class PackageController extends BaseController {

	
	@Autowired 
	private PackageApplicabilityDAO applicabilityDAO;

	private static final Logger logger = LoggerFactory.getLogger(PackageController.class);
 
	DataValidationHelpers validationHelpers = new DataValidationHelpers();
	private Gson gson = new Gson();
	
//---------APIS---------

	//Returns the packages that the user can apply for.
		@RequestMapping(value = "/getStudentPackageApplicability", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
		public ResponseEntity<String> getStudentPackageApplicability(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
			CSResponse csResponse = new CSResponse();
			List<UpgradePathDetails> upgradePathDetails = new ArrayList<UpgradePathDetails>();
			if(!requestParams.containsKey("sapid")) {
				csResponse.setNoSapid();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			String sapid = requestParams.get("sapid");
			if(!applicabilityDAO.checkIfUserExists(sapid)) {
				csResponse.setInvalidSapid();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			try {

				upgradePathDetails = applicabilityDAO.studentApplicablePackages(sapid);
				AvailablePackagesModelBean dataModel = new AvailablePackagesModelBean();
				dataModel.setUpgradePathsAndPackageDetails(upgradePathDetails);
				dataModel.setTermsAndConditions(getTermsAndConditions());
				dataModel.setAboutCS(getAboutCS());
				
				csResponse.setResponse(dataModel);
				csResponse.setStatusSuccess();
				if(upgradePathDetails.size() == 0) {
					csResponse.setMessage("No applicable packages found");
				}else {
					csResponse.setMessage("Packages found");
				}
			}catch (Exception e) {
				logger.info("in LearningPortal class got exception : "+e.getMessage());
				csResponse.setStatusFailure();
				csResponse.setMessage("Error getting packages.");
			}
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
//
//	//Get a single package
//		@RequestMapping(value = "/getPackage", method = RequestMethod.GET, produces = "application/json")
//		public ResponseEntity<PackageBean> getPackage(Locale locale, Model model, @RequestParam("packageId") String packageId) {
//			PackageBean packageToReturn = packagesDAO.getPackageFromId(packageId);
//			packageToReturn.setPackageRequirements(null);
//			return ResponseEntity.ok(packagesDAO.getPackageFromId(packageId));
//		}

	//Checks if student has any active packages
		@RequestMapping(value = "/studentHasActivePackages", method = RequestMethod.POST, produces = "application/json", consumes="application/json")
		public ResponseEntity<String> studentHasActivePackages(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
		
			Map<String, String> result = new HashMap<String, String>();
			if(!applicabilityDAO.checkIfUserExists(requestParams.get("sapid"))){
				result.put("status", "0");
				result.put("message", "Invalid sapid!");
			}else if(requestParams.containsKey("sapid")) {
				if(applicabilityDAO.checkIfStudentHasActivePackages(requestParams.get("sapid"))) { 
					result.put("status", "1");
					result.put("message", "Student has active packages");
				}else {
					result.put("status", "0");
					result.put("message", "Student has no active packages");
				}
			}else if(!applicabilityDAO.checkIfUserExists(requestParams.get("sapid"))){
				result.put("status", "0");
				result.put("message", "Invalid sapid!");
			}else{
				result.put("status", "0");
				result.put("message", "sapid missing");
			}
			 
			
			return ResponseEntity.ok(gson.toJson(result));
		}
		
		@RequestMapping(value = "/packageDetailsWebView", method = RequestMethod.GET)
		public String packageDetailsWebView(HttpServletRequest request, HttpServletResponse response, Model model, String sapid, String productId) {
			 response.setHeader("Access-Control-Allow-Origin", "*");

			PackageFamily availabilityBean = applicabilityDAO.getFamily(productId);
			model.addAttribute("PageData", availabilityBean);
			return "portal/showSinglePackageMobile";
		}
		
		

//----------------------
	
		
//--------PORTAL--------
	
	//View all applicable packages to the student
		@CrossOrigin
		@RequestMapping(value = "/showAllProducts", method = RequestMethod.GET)
		public String viewPackages(HttpServletRequest request, HttpServletResponse response, Model model) {
			
			response.setHeader("Access-Control-Allow-Origin", "*");
	
			 if(!checkSession(request, response)) {
				 return "redirect:../studentportal/home";
			 }
			 
			//Check to not let a student buy multiple packages at once
				String sapid = (String)request.getSession().getAttribute("userId");
				resetStudentInSession(request, response);
				if(request.getSession().getAttribute("student_careerservices") != null) {
					StudentCareerservicesBean student = (StudentCareerservicesBean)request.getSession().getAttribute("student_careerservices");
					if(student.isPurchasedOtherPackages()) {
						return "redirect:../studentportal/home";
					}
				}
			//END
			
//			model.addAttribute("packageList", packagesDAO.setPricesToPackages(packagesDAO.studentApplicablePackages(sapid)));
			
			AvailablePackagesModelBean dataModel = new AvailablePackagesModelBean();
			
			dataModel.setTermsAndConditions(getTermsAndConditions());
			dataModel.setAboutCS(getAboutCS());
			
			dataModel.setUpgradePathsAndPackageDetails(applicabilityDAO.studentApplicablePackages(sapid));
			
			model.addAttribute("PageData", dataModel);
			return "portal/showPackageList";
		}
		private TermsAndConditions getTermsAndConditions() {
			TermsAndConditions termsAndConditions = new TermsAndConditions();
			return termsAndConditions;
		}
		private AboutCS getAboutCS() {
			AboutCS about = new AboutCS();
			
			about.setText("Career Services is a pioneer service by NGA-SCE. It is built with an aim to tap into the potential students & alumni carry and then delve into their strengths and weaknesses to unearth their innate skills which would then be matched with unique career opportunities.");
			return about;
		}
		
	//View single package details page
		@RequestMapping(value = "/viewProductDetails", method = RequestMethod.GET)
		public String viewPackage(HttpServletRequest request, Model model, @RequestParam("productId") String familyId) {
			String sapid = (String)request.getSession().getAttribute("userId");
			PacakageAvailabilityBean availabilityBean = applicabilityDAO.getPackageAvailabilityBeanFor(sapid, familyId);
			model.addAttribute("PageData", availabilityBean);
			return "portal/showSinglePackage";
		}

		
	//View single package details page
		@RequestMapping(value = "/getProductCriteria", method = RequestMethod.GET)
		public String viewPackageMobile(Locale locale, Model model, @RequestParam("productId") String familyId) {
			model.addAttribute("Family", applicabilityDAO.getFamily(familyId));
			return "portal/showSinglePackageMobile";
		}

	//Default landing page. set it to show purchased package list / a dashboard with upcoming events later
		@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
		public String homePage(Locale locale, Model model) {
	//		packagesDAO.getStudentSemesterViseResults("");
		
			return "redirect:showAllProducts";
		}

//----------------------
	
		
//---Payment---
	

//----------------------

		
		@RequestMapping(value = "/m/getStudentPacakgeForSemester", method = RequestMethod.POST, produces = "application/json", consumes="application/json")
		public ResponseEntity<List<PacakageAvailabilityBean>> getStudentPacakgeForSemester( @RequestBody StudentCareerservicesBean details) {
		
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			List<UpgradePathDetails> upgradePackages = new ArrayList<UpgradePathDetails>();
			ArrayList<PacakageAvailabilityBean> response = new ArrayList<PacakageAvailabilityBean>();
			
			if( !StringUtils.isBlank(details.getSem()) && StringUtils.isNumeric(details.getSem()) && !StringUtils.isBlank(details.getSapid()) ) {
				int sem = Integer.parseInt(details.getSem());
				upgradePackages = applicabilityDAO.studentApplicablePackagesForSemester(details.getSapid(), sem);
				for(UpgradePathDetails upgrades : upgradePackages) {
					
					upgrades.setFeaturesAvailableForThisFamily(null);
					for(PacakageAvailabilityBean packages : upgrades.getPackages()) {
						
						PacakageAvailabilityBean bean = new PacakageAvailabilityBean();
						
						if(packages.isPurchased() == false && packages.isAvailable() == true) {
							bean.setDurationType(packages.getDurationType());
							bean.setPrice(packages.getPrice());
							bean.setFamilyName(packages.getFamilyName());
							bean.setAvailable(packages.isAvailable());
							bean.setUpcoming(packages.isUpcoming());
							bean.setSalesForceUID(packages.getSalesForceUID());
							response.add(bean);
						}
					}
					
				}
			}
			
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
}
