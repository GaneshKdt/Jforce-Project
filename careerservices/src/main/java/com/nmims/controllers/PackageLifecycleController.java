package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

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
import com.nmims.beans.EntitlementDependency;
import com.nmims.beans.Feature;
import com.nmims.beans.PackageBean;
import com.nmims.beans.PackageEntitlementInfo;
import com.nmims.beans.PackageFamily;
import com.nmims.beans.PackageFeature;
import com.nmims.beans.PackageRequirementsMasterMapping;
import com.nmims.beans.ReturnStatus;
import com.nmims.beans.StudentEntitlement;
import com.nmims.beans.UpgradePath;
import com.nmims.beans.UpgradePathFamily;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.daos.ConsumerProgramStructureDAO;
import com.nmims.daos.PackageAdminDAO;

@Controller
public class PackageLifecycleController extends CSAdminBaseController {

	@Autowired
	PackageAdminDAO packageAdminDAO;

	@Autowired
	private ConsumerProgramStructureDAO consumerProgramStructureDAO;
	
	private Gson gson = new Gson();
	
	
	/*
	 * 	Portal Pages
	 */
	
		/*
		 * 	packages
		 */
			@RequestMapping(value = "/addPackage", method = RequestMethod.GET, produces = "application/json")
			public String addPackagePage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="packageId", required=false) String packageId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				model.addAttribute("Families", packageAdminDAO.getAllPackageFamilies());
				model.addAttribute("Package", new PackageBean());
				model.addAttribute("title", "Add Package");
				model.addAttribute("tableTitle", "All Packages");
				model.addAttribute("url", "/m/addPackage");
				model.addAttribute("AllPackagesData", packageAdminDAO.getAllPackages());

				return "admin/products/package";
			}	
			
			@RequestMapping(value = "/m/addPackage", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> addPackagePOST(Locale locale, HttpServletRequest request, Model model, @RequestBody PackageBean requestParams ) {
				ReturnStatus returnStatus = packageAdminDAO.addPackage(requestParams);
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	
			
			@RequestMapping(value = "/updatePackage", method = RequestMethod.GET, produces = "application/json")
			public String updatePackagePage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="packageId", required=false) String packageId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				if(packageId == null) {
					return "redirect: addPackage";
				}
				if(!packageAdminDAO.checkIfPackageWithIdExists(packageId)) {
					return "redirect: addPackage";
				}
				PackageBean packageToReturn = packageAdminDAO.getPackageFromId(packageId);
				model.addAttribute("Package", packageToReturn);
				model.addAttribute("Families", packageAdminDAO.getAllPackageFamilies());

				model.addAttribute("title", "Update Package : " + packageToReturn.getPackageName());
				model.addAttribute("tableTitle", "All Packages");
				
				model.addAttribute("url", "/m/updatePackage");
				model.addAttribute("AllPackagesData", packageAdminDAO.getAllPackages());

				return "admin/products/package";
			}
			
			@RequestMapping(value = "/m/updatePackage", method = RequestMethod.POST, produces = "application/json", consumes="application/json")
			public ResponseEntity<String> updatePackagePOST(Locale locale, HttpServletRequest request, Model model, @RequestBody PackageBean requestParams ) {
				
				
				ReturnStatus returnStatus = packageAdminDAO.updatePackage(requestParams);
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	
			

			@RequestMapping(value = "/deletePackage", method = RequestMethod.GET, produces = "application/json")
			public String deletePackage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="packageId") String packageId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				ReturnStatus returnStatus = packageAdminDAO.deletePackage(packageId);

				if(returnStatus.getStatus().equals("success")) {
					model.addAttribute("successMessage","Deleted Package with id : " + packageId);
				}else {
					model.addAttribute("errorMessage",""
							+ "Error deleting Package with id : " + packageId 
							+ "<br> "
							+ "Error Message: " + returnStatus.getMessage());
				}
				return "redirect:addPackage";
			}	
		/*
		 * 	end
		 */
		
		/*
		 * 	package family
		 */
			@RequestMapping(value = "/addPackageFamily", method = RequestMethod.GET, produces = "application/json")
			public String addPackageFamily(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="packageId", required=false) String packageId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				model.addAttribute("Family", new PackageFamily());

				model.addAttribute("title", "Add Package Family");
				model.addAttribute("tableTitle", "All Package Families");
				
				model.addAttribute("url", "/m/addPackageFamily");
				model.addAttribute("AllFamilyData", packageAdminDAO.getAllPackageFamilies());
				return "admin/products/package_family";
			}	
			
			@RequestMapping(value = "/m/addPackageFamily", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> addPackageFamilyPOST(Locale locale, HttpServletRequest request, Model model, @RequestBody PackageFamily requestParams ) {
				ReturnStatus returnStatus = packageAdminDAO.addPackageFamily(requestParams);
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}
		
			@RequestMapping(value = "/updatePackageFamily", method = RequestMethod.GET, produces = "application/json")
			public String updatePackageFamily(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="familyId", required=false) String familyId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				PackageFamily family = packageAdminDAO.getPackageFamily(familyId);
				model.addAttribute("Family", family);

				model.addAttribute("title", "Update Package Family : " + family.getFamilyName());
				model.addAttribute("tableTitle", "All Package Families");
				
				
				model.addAttribute("url", "/m/updatePackageFamily");				
				model.addAttribute("AllFamilyData", packageAdminDAO.getAllPackageFamilies());

				return "admin/products/package_family";
			}	
			
			@RequestMapping(value = "/m/updatePackageFamily", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> updatePackageFamilyPOST(Locale locale, HttpServletRequest request, Model model, @RequestBody PackageFamily requestParams ) {
				ReturnStatus returnStatus = packageAdminDAO.updatePackageFamily(requestParams);
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}

			@RequestMapping(value = "/deletePackageFamily", method = RequestMethod.GET, produces = "application/json")
			public String deletePackageFamily(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="familyId") String familyId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				ReturnStatus returnStatus = packageAdminDAO.deletePackageFamily(familyId);

				if(returnStatus.getStatus().equals("success")) {
					model.addAttribute("successMessage","Deleted Family with id : " + familyId);
				}else {
					model.addAttribute("errorMessage",""
							+ "Error deleting Family with id : " + familyId 
							+ "<br> "
							+ "Error Message: " + returnStatus.getMessage());
				}
				return "redirect:addPackageFamily";
			}	
			
		/*
		 * 	end
		 */
			
			
		/*
		 *	package requirements
		 */
			@RequestMapping(value = "/addPackageRequirements", method = RequestMethod.GET, produces = "application/json")
			public String addPackageRequirementsPage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="packageId", required=false) String packageId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				if(packageId == null) {
					return "redirect: addPackage";
				}
				if(!packageAdminDAO.checkIfPackageWithIdExists(packageId)) {
					return "redirect: addPackage";
				}
				PackageRequirementsMasterMapping reqs = new PackageRequirementsMasterMapping();
				reqs.setPackageId(packageId);

				ArrayList<String> currentYearList = new ArrayList<String>(Arrays.asList("2014", "2015","2016", "2017" , "2018" , "2019" )); 
				model.addAttribute("yearList", currentYearList);

				model.addAttribute("Update", true);
				model.addAttribute("Requirements", reqs);
				model.addAttribute("AllPackageRequirements", packageAdminDAO.getAllPackageRequirements(packageId));
				
				model.addAttribute("ConsumerTypes", gson.toJson(consumerProgramStructureDAO.getConsumerTypeList()));
				model.addAttribute("ProgramStructures", gson.toJson(consumerProgramStructureDAO.getAllProgramStructuresQuery()));
				model.addAttribute("ConsumerProgramStructureDetails", gson.toJson(consumerProgramStructureDAO.getAllConsumerProgramStructures()));
				
				model.addAttribute("title", "Add Package Requirements");
				model.addAttribute("tableTitle", "All Package Requirements");
				
				model.addAttribute("url", "/m/addPackageRequirements");
				return "admin/products/package_requirements";
			}
			
			@RequestMapping(value = "/m/addPackageRequirements", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> addPackageRequirementsPOST(Locale locale, HttpServletRequest request, Model model, @RequestBody PackageRequirementsMasterMapping requestParams ) {
				ReturnStatus returnStatus = packageAdminDAO.addPackageRequirements(requestParams);
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	
			
			@RequestMapping(value = "/updatePackageRequirements", method = RequestMethod.GET, produces = "application/json")
			public String updatePackageRequirementsPage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="requirementsId", required=false) String requirementsId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				if(requirementsId == null) {
					return "redirect: addPackage";
				}
				PackageRequirementsMasterMapping requirements = packageAdminDAO.getPackageRequirements(requirementsId);

				ArrayList<String> currentYearList = new ArrayList<String>(Arrays.asList("2014", "2015","2016", "2017" , "2018" , "2019" )); 
				model.addAttribute("yearList", currentYearList);

				model.addAttribute("Requirements", requirements);

				String packageId = requirements.getPackageId();

				model.addAttribute("ConsumerTypes", gson.toJson(consumerProgramStructureDAO.getConsumerTypeList()));
				model.addAttribute("ProgramStructures", gson.toJson(consumerProgramStructureDAO.getAllProgramStructuresQuery()));
				model.addAttribute("ConsumerProgramStructureDetails", gson.toJson(consumerProgramStructureDAO.getAllConsumerProgramStructures()));
				
				model.addAttribute("title", "Update Package Requirements : " + requirements.getRequirementsId());
				model.addAttribute("tableTitle", "All Package Requirements");
				
				model.addAttribute("url", "/m/updatePackageRequirements");
				model.addAttribute("AllPackageRequirements", packageAdminDAO.getAllPackageRequirements(packageId));
				return "admin/products/package_requirements";
			}
			
			@RequestMapping(value = "/m/updatePackageRequirements", method = RequestMethod.POST, produces = "application/json", consumes="application/json")
			public ResponseEntity<String> updatePackageRequirementsPOST(Locale locale, HttpServletRequest request, Model model, @RequestBody PackageRequirementsMasterMapping requestParams ) {
				ReturnStatus returnStatus = packageAdminDAO.updatePackageRequirements(requestParams);
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	
		/*
		 * 	end
		 */

		/*
		 *	upgrade path 
		 */
			@RequestMapping(value = "/addUpgradePath", method = RequestMethod.GET, produces = "application/json")
			public String addUpgradePathPage(Locale locale, HttpServletRequest request, Model model) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				model.addAttribute("AllUpgradePaths", packageAdminDAO.getAllUpgradePaths());
				model.addAttribute("UpgradePath", new UpgradePath());

				model.addAttribute("title", "Add Upgrade Path");
				model.addAttribute("tableTitle", "All Upgrade Path");
				
				model.addAttribute("url", "/m/addUpgradePath");
				return "admin/products/upgrade_path";
			}	
			
			@RequestMapping(value = "/m/addUpgradePath", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> addUpgradePathPOST(Locale locale, HttpServletRequest request, Model model, @RequestBody UpgradePath requestParams ) {
				
				ReturnStatus returnStatus = packageAdminDAO.addUpgradePath(requestParams);
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	
			
			@RequestMapping(value = "/updateUpgradePath", method = RequestMethod.GET, produces = "application/json")
			public String updateUpgradePath(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="pathId") String pathId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				UpgradePath upgradePath =  packageAdminDAO.getUpgradePath(pathId);
				model.addAttribute("AllUpgradePaths", packageAdminDAO.getAllUpgradePaths());
				model.addAttribute("UpgradePath", upgradePath);

				model.addAttribute("title", "Update Upgrade Path : " + upgradePath.getPathName());
				model.addAttribute("tableTitle", "All Upgrade Path");
				
				model.addAttribute("url", "/m/updateUpgradePath");
				return "admin/products/upgrade_path";
			}	
			
			@RequestMapping(value = "/m/updateUpgradePath", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> updateUpgradePathPOST(Locale locale, HttpServletRequest request, Model model, @RequestBody UpgradePath requestParams ) {
				
				ReturnStatus returnStatus = packageAdminDAO.updateUpgradePath(requestParams);
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	
			

			/*
			 *	upgrade path 
			 */
				@RequestMapping(value = "/deleteUpgradePath", method = RequestMethod.GET, produces = "application/json")
				public String deleteUpgradePath(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="pathId") String pathId) {
					if(!checkLogin(request)) {
						return "redirect:../studentportal/home";
					}
					UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
					String userAuthorizationRoles = userAuthorization.getRoles();
					if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
						return "redirect:../studentportal/home";
					}
					
					packageAdminDAO.deleteUpgradePath(pathId);
					
					return "redirect:addUpgradePath";
				}	
			
		/*
		 * 	package family upgrade path
		 */
			@RequestMapping(value = "/addToUpgradePath", method = RequestMethod.GET, produces = "application/json")
			public String addToUpgradePathPage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="pathId", required=false) String pathId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				if(pathId == null) {
					return "redirect: addUpgradePath";
				}
				if(!packageAdminDAO.checkIfUpgradePathWithIdExists(pathId)) {
					return "redirect: addPackage";
				}
				
				UpgradePathFamily pathFamily =  new UpgradePathFamily();
				pathFamily.setPathId(pathId);
				model.addAttribute("Update", false);
				model.addAttribute("UpgradePathFamily", pathFamily);
				model.addAttribute("FamiliesNotInPath", packageAdminDAO.getListOfPackagesNotInUpgradePath(pathId));
				model.addAttribute("AllFamiliesInUpgradePaths", packageAdminDAO.getAllUpgradePathFamily(pathId));
				model.addAttribute("pathId", pathId);


				
				model.addAttribute("title", "Add Package Family To Upgrade Path");
				model.addAttribute("tableTitle", "All Package Families in Upgrade Path");
				
				model.addAttribute("url", "/m/addToUpgradePath");
				return "admin/products/upgrade_path_family";
			}	
			
			@RequestMapping(value = "/m/addToUpgradePath", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> addToUpgradePath(Locale locale, HttpServletRequest request, Model model, @RequestBody UpgradePathFamily requestParams ) {
				if(packageAdminDAO.checkIfPackageFamilyExistsInPath( requestParams.getPathId(), requestParams.getPackageFamilyId() )) {
					ReturnStatus returnStatus = new ReturnStatus();
					returnStatus.setStatus("0");
					returnStatus.setMessage("Family Already exists in package!");
				}
				ReturnStatus returnStatus = packageAdminDAO.addFamilyToUpgradePath(requestParams);
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	

			
			@RequestMapping(value = "/updateFamilyInUpgradePath", method = RequestMethod.GET, produces = "application/json")
			public String updateFamilyInUpgradePathPage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="pathId") String pathId, @RequestParam(value="packageFamilyId") String packageFamilyId) {

				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				if(pathId == null) {
					return "redirect: addUpgradePath";
				}
				if(!packageAdminDAO.checkIfPackageFamilyExistsInPath( pathId, packageFamilyId )) {
					return "redirect: addToUpgradePath";
				}

				model.addAttribute("Update", true);
				model.addAttribute("UpgradePathFamily", packageAdminDAO.getUpgradePathFamily(pathId, packageFamilyId));
				model.addAttribute("AllFamiliesInUpgradePath", packageAdminDAO.getAllUpgradePathFamily());
				model.addAttribute("pathId", pathId);

				model.addAttribute("title", "Update Package Family in Upgrade Path");
				model.addAttribute("tableTitle", "All Package Families in Upgrade Path");
				
				
				model.addAttribute("url", "/m/updateFamilyInUpgradePath");
				return "admin/products/upgrade_path_family";
			}	
			
			@RequestMapping(value = "/m/updateFamilyInUpgradePath", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> updateFamilyInUpgradePath(Locale locale, HttpServletRequest request, Model model, @RequestBody UpgradePathFamily requestParams ) {
				if(!packageAdminDAO.checkIfPackageFamilyExistsInPath( requestParams.getPathId(), requestParams.getPackageFamilyId() )) {
					ReturnStatus returnStatus = new ReturnStatus();
					returnStatus.setStatus("0");
					returnStatus.setMessage("This family does not exist in path!");
					return ResponseEntity.ok(gson.toJson(returnStatus));
				}
				ReturnStatus returnStatus = packageAdminDAO.updateFamilyInUpgradePath(requestParams);
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	
			
		

		/*
		 * 	Features
		 */
			@RequestMapping(value = "/allFeatures", method = RequestMethod.GET, produces = "application/json")
			public String allFeaturesPage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="featureId", required=false) String featureId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				model.addAttribute("showFeatureTableOnly", true);
				model.addAttribute("AllFeatures", packageAdminDAO.getAllFeatures());


				model.addAttribute("title", "All Features");
				model.addAttribute("tableTitle", "");
				
				model.addAttribute("tableHeader", "");
				return "admin/products/feature";
			}	
		
			@RequestMapping(value = "/updateFeature", method = RequestMethod.GET, produces = "application/json")
			public String updateFeaturePage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="featureId", required=false) String featureId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				if(featureId == null) {
					return "redirect: addPackage";
				}
				if(!packageAdminDAO.checkIfFeatureExists(featureId)) {
					return "redirect: addPackage";
				}
				
				Feature feat = packageAdminDAO.getFeature(featureId);
				model.addAttribute("Feature", feat);
				model.addAttribute("AllFeatures", packageAdminDAO.getAllFeatures());


				model.addAttribute("title", "Update Feature : " + feat.getFeatureName());
				model.addAttribute("tableTitle", "All Features");
				
				model.addAttribute("url", "/m/updateFeature");
				return "admin/products/feature";
			}	
			
			@RequestMapping(value = "/m/updateFeature", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> updateFeature(Locale locale, HttpServletRequest request, Model model, @RequestBody Feature requestParams ) {
				ReturnStatus returnStatus = new ReturnStatus();
				if(!packageAdminDAO.checkIfFeatureExists(requestParams.getFeatureId())) {
					returnStatus.setStatus("0");
					returnStatus.setMessage("Feature Already exists in package!"); 
				}else {
					returnStatus = packageAdminDAO.updateFeature(requestParams);
				}
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	

		/*
		 * 	Package Features
		 */
			@RequestMapping(value = "/addFeatureToPackage", method = RequestMethod.GET, produces = "application/json")
			public String addFeatureToPackagePage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="packageId", required=false) String packageId) {

				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				if(packageId == null) {
					return "redirect: addPackage";
				}
				if(!packageAdminDAO.checkIfPackageWithIdExists(packageId)) {
					return "redirect: addPackage";
				}

				model.addAttribute("FeaturesNotInPackage", packageAdminDAO.getListOfFeaturesNotInPackage(packageId));
				model.addAttribute("AllPackageFeatures", packageAdminDAO.getAllPackageFeatures(packageId));
				model.addAttribute("packageId", packageId);
				
				
				model.addAttribute("title", "Add Features to : " + packageAdminDAO.getPackageFromId(packageId).getPackageName());
				model.addAttribute("tableTitle", "All Package Features");
				
				model.addAttribute("url", "/m/addFeatureToPackage");
				return "admin/products/package_feature";
			}	
			
			@RequestMapping(value = "/m/addFeatureToPackage", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> addFeatureToPackage(Locale locale, HttpServletRequest request, Model model, @RequestBody PackageFeature requestParams ) {
				ReturnStatus returnStatus = new ReturnStatus();
				if(packageAdminDAO.checkIfPackageFeatureExists(requestParams.getPackageId(), requestParams.getFeatureId())) {
					returnStatus.setStatus("0");
					returnStatus.setMessage("Feature Already exists in package!"); 
				}else {
					returnStatus = packageAdminDAO.addPackageFeature(requestParams);
				}
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	
			
			@RequestMapping(value = "/deletePackageFeature", method = RequestMethod.GET)
			public String deletePackageFeature(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="packageId", required=false) String packageId, @RequestParam(value="featureId", required=false) String featureId) {
				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				if(packageId == null) {
					return "redirect: addPackage";
				}
				if(featureId == null) {
					return "redirect: addFeatureToPackage?packageId="+ packageId;
				}
				packageAdminDAO.deletePackageFeature(packageId, featureId);
				
				return "redirect: addFeatureToPackage?packageId="+ packageId;
			}	
			

		/*
		 * 	Entitlement Info
		 */
			@RequestMapping(value = "/updateEntitlement", method = RequestMethod.GET, produces = "application/json")
			public String updateEntitlement(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="packageFeatureId", required=false) String packageFeatureId) {

				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				
				if(packageFeatureId == null) {
					return "redirect: addPackage";
				}
				if(!packageAdminDAO.checkIfPackageFeatureWithIdExists(packageFeatureId)) {
					return "redirect: addPackage";
				}
				
				if(!packageAdminDAO.checkIfEntitlementInfoExistsForPackageFeature(packageFeatureId)) {
					//initialize and add a new entitlement with basic info
					if(!packageAdminDAO.addNewEntitlement(packageFeatureId)) {
						
					}
				}
				
				PackageEntitlementInfo entitlementInfo = packageAdminDAO.getEntitlementWithPackageFeature(packageFeatureId);
				model.addAttribute("Entitlement", entitlementInfo);
				model.addAttribute("AllEntitlements", packageAdminDAO.getAllEntitlementInfo(packageFeatureId));

				model.addAttribute("title", "Update Entitlement :  Package - " + entitlementInfo.getPackageName() + " Feature - " + entitlementInfo.getFeatureName() );
				model.addAttribute("tableTitle", "All Package Features");
				
				model.addAttribute("url", "/m/updateEntitlement");
				return "admin/products/entitlements";
			}	
			
			@RequestMapping(value = "/m/updateEntitlement", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> updateEntitlement(Locale locale, HttpServletRequest request, Model model, @RequestBody PackageEntitlementInfo requestParams ) {
				ReturnStatus returnStatus = new ReturnStatus();
				
				if(!packageAdminDAO.checkIfPackageFeatureWithIdExists(requestParams.getPackageFeaturesId())) {
					returnStatus.setStatus("0");
					returnStatus.setMessage("Invalid Package Feature id!"); 
					return ResponseEntity.ok(gson.toJson(returnStatus));
				}else if(!packageAdminDAO.checkIfEntitlementInfoExistsForPackageFeature(requestParams.getPackageFeaturesId())) {
					returnStatus.setStatus("0");
					returnStatus.setMessage("Entitlement Doesnt Exist!"); 
					return ResponseEntity.ok(gson.toJson(returnStatus));
				}else {
					returnStatus = packageAdminDAO.updateEntitlement(requestParams);
				}
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	
			
			@RequestMapping(value = "/deleteEntitlement", method = RequestMethod.GET, consumes="application/json" , produces = "application/json")
			public String deleteEntitlement(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="packageFeaturesId", required=false) String packageFeaturesId, @RequestParam(value="entitlementId", required=false) String entitlementId) {

				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				PackageEntitlementInfo packageEntitlementInfo = new PackageEntitlementInfo();
				packageEntitlementInfo.setPackageFeaturesId(packageFeaturesId);
				packageEntitlementInfo.setEntitlementId(entitlementId);
				
				packageAdminDAO.deleteEntitlement(packageEntitlementInfo);
				
				return "redirect: addPackages";
			}	
			
		
		/*
		 * 	end
		 */

		

		/*
		 * 	Entitlement Dependency
		 */
			@RequestMapping(value = "/updateEntitlementDependency", method = RequestMethod.GET, produces = "application/json")
			public String updateEntitlementDependencyPage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="entitlementId", required=false) String entitlementId, @RequestParam(value="dependencyId", required=false) String dependencyId) {

				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				if(entitlementId == null) {
					return "redirect: addPackage";
				}
				if(!packageAdminDAO.checkIfEntitlementExists(entitlementId)) {
					return "redirect: addPackage";
				}
				model.addAttribute("Dependency", packageAdminDAO.getEntitlementDependency(entitlementId, dependencyId));
				model.addAttribute("FeaturesNotDependedUpon", packageAdminDAO.getFeaturesNotInDependency(entitlementId));
				model.addAttribute("AllEntitlementDependencies", packageAdminDAO.getAllEntitlementDependencies());

				model.addAttribute("Update", true);

				PackageEntitlementInfo entitlementInfo = packageAdminDAO.getEntitlementWithId(entitlementId);
				model.addAttribute("title", "Update Entitlement Dependency :  Package - " + entitlementInfo.getPackageName() + " Feature - " + entitlementInfo.getFeatureName() );
				model.addAttribute("tableTitle", "All Entitlement Dependencies");
				
				model.addAttribute("url", "/m/updateEntitlementDependency");
				return "admin/products/entitlement_dependency";
			}	
			
			@RequestMapping(value = "/m/updateEntitlementDependency", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> updateEntitlementDependency(Locale locale, Model model, @RequestBody EntitlementDependency requestParams ) {
				ReturnStatus returnStatus = new ReturnStatus();
				
				if(!packageAdminDAO.checkIfEntitlementDependencyExists(requestParams.getId())) {
					returnStatus.setStatus("0");
					returnStatus.setMessage("Invalid Dependency id!"); 
					return ResponseEntity.ok(gson.toJson(returnStatus));
				}else {
					returnStatus = packageAdminDAO.updateEntitlementDependency(requestParams);
				}
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	
			

			@RequestMapping(value = "/addEntitlementDependency", method = RequestMethod.GET, produces = "application/json")
			public String addEntitlementDependencyPage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="entitlementId", required=false) String entitlementId) {


				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				if(entitlementId == null) {
					return "redirect: addPackage";
				}
				if(!packageAdminDAO.checkIfEntitlementExists(entitlementId)) {
					return "redirect: addPackage";
				}

				EntitlementDependency dependency = new EntitlementDependency();
				dependency.setEntitlementId(entitlementId);
				
				model.addAttribute("Dependency", dependency);
				model.addAttribute("FeaturesNotDependedUpon", packageAdminDAO.getFeaturesNotInDependency(entitlementId));
				model.addAttribute("AllEntitlementDependencies", packageAdminDAO.getAllEntitlementDependencies());

				PackageEntitlementInfo entitlementInfo = packageAdminDAO.getEntitlementWithId(entitlementId);
				model.addAttribute("title", "Add Entitlement Dependency :  Package - " + entitlementInfo.getPackageName() + " Feature - " + entitlementInfo.getFeatureName() );
				model.addAttribute("tableTitle", "All Entitlement Dependencies");
				
				model.addAttribute("url", "/m/addEntitlementDependency");
				return "admin/products/entitlement_dependency";
			}	
			
			@RequestMapping(value = "/m/addEntitlementDependency", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> addEntitlementDependency(Locale locale, Model model, @RequestBody EntitlementDependency requestParams ) {
				ReturnStatus returnStatus = new ReturnStatus();
				
				if(packageAdminDAO.checkIfEntitlementDependencyExists(requestParams.getId())) {
					returnStatus.setStatus("0");
					returnStatus.setMessage("Dependency Already Exists!"); 
					return ResponseEntity.ok(gson.toJson(returnStatus));
				}else {
					returnStatus = packageAdminDAO.addEntitlementDependency(requestParams);
				}
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	
			
			@RequestMapping(value = "/deleteEntitlementDependency", method = RequestMethod.GET)
			public String deleteEntitlementDependency(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="dependencyId", required=false) String dependencyId, @RequestParam(value="entitlementId", required=false) String entitlementId) {


				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				
				if(entitlementId == null) {
					return "redirect: addPackages";
				}
				if(dependencyId == null) {
					return "redirect: addEntitlementDependency?entitlementId=" + entitlementId;
				}
				EntitlementDependency dependency = new EntitlementDependency();
				dependency.setEntitlementId(entitlementId);
				dependency.setId(dependencyId);
				
				packageAdminDAO.deleteEntitlementDependency(dependency);

				return "redirect: addEntitlementDependency?entitlementId=" + entitlementId;
			}	
			
		/*
		 * 	end
		 */
			

		/*
		 * 	Entitlement Initial Student Info
		 */
			@RequestMapping(value = "/updateEntitlementInitialInfo", method = RequestMethod.GET, produces = "application/json")
			public String updateEntitlementInitialInfoPage(Locale locale, HttpServletRequest request, Model model, @RequestParam(value="entitlementId", required=false) String entitlementId) {


				if(!checkLogin(request)) {
					return "redirect:../studentportal/home";
				}
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				String userAuthorizationRoles = userAuthorization.getRoles();
				if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSPackagingAdmin)) {
					return "redirect:../studentportal/home";
				}
				
				
				if(entitlementId == null) {
					return "redirect: entitlementIdNull";
				}
				if(!packageAdminDAO.checkIfEntitlementExists(entitlementId)) {
					return "redirect: checkIfEntitlementExistsFALSE";
				}
				if(!packageAdminDAO.checkIfInitialStudentInfoExists(entitlementId)) {
					return "redirect: checkIfInitialStudentInfoExistsFALSE";
				}
				
				model.addAttribute("InitialStudentData", packageAdminDAO.getInitialStudentInfo(entitlementId));
				model.addAttribute("AllInitialStudentData", packageAdminDAO.getAllInitialStudentInfo());


				PackageEntitlementInfo entitlementInfo = packageAdminDAO.getEntitlementWithId(entitlementId);
				model.addAttribute("title", "Update Entitlement Initial Student Info:  Package - " + entitlementInfo.getPackageName() + " Feature - " + entitlementInfo.getFeatureName() );
				model.addAttribute("tableTitle", "All Entitlement Initial Student Info");
				
				model.addAttribute("url", "/m/updateEntitlementInitialInfo");
				return "admin/products/entitlement_initial_student_data";
			}	
			
			@RequestMapping(value = "/m/updateEntitlementInitialInfo", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
			public ResponseEntity<String> updateEntitlementInitialInfo(Locale locale, Model model, @RequestBody StudentEntitlement requestParams ) {
				ReturnStatus returnStatus = new ReturnStatus();
				
				if(!packageAdminDAO.checkIfInitialStudentInfoExists(requestParams.getEntitlementId())) {
					returnStatus.setStatus("0");
					returnStatus.setMessage("Invalid Entitlement id!"); 
					return ResponseEntity.ok(gson.toJson(returnStatus));
				}else {
					returnStatus = packageAdminDAO.updateEntitlementInitialStudentInfo(requestParams);
				}
				return ResponseEntity.ok(gson.toJson(returnStatus));
			}	
			
		
		/*
		 * 	end
		 */
			

}
