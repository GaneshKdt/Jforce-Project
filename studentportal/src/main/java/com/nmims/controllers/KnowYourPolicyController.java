package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.KnowYourPolicyBean;
import com.nmims.interfaces.KnowYourPolicyInterface;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class KnowYourPolicyController extends BaseController {
	@Autowired
	KnowYourPolicyInterface policyInterface;

	private static final Logger logger = LoggerFactory.getLogger(KnowYourPolicyController.class);

	@GetMapping("/knowYourPolicy")
	public ModelAndView KnowYourPolicyForm(Model m, HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}
		try {
			Map<Integer, String> groupmap = new TreeMap<Integer, String>(policyInterface.getGroupMapFromDatabase());
			Map<Integer, String> categorymap = new TreeMap<Integer, String>(
					policyInterface.getCategoryMapFromDatabase());
			Map<Integer, String> subcategorymap = new TreeMap<Integer, String>(
					policyInterface.getSubCategoryMapFromDatabase());
			List<KnowYourPolicyBean> policylist = policyInterface.getAllPolicy(groupmap, categorymap, subcategorymap);
			m.addAttribute("policylist", policylist);
		} catch (Exception e) {
			logger.error("Error While Loading Know Your Policy Page:" + e);
			// e.printStackTrace();
		}
		return new ModelAndView("jsp/knowYourPolicy/knowYourPolicyForm");
	}

	@GetMapping("/knowYourPolicyEntry")
	public ModelAndView KnowYourPolicyEntryForm(Model m, HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}
		try {
			Map<Integer, String> groupmap = new TreeMap<Integer, String>(policyInterface.getGroupMapFromDatabase());
			Map<Integer, String> categorymap = new TreeMap<Integer, String>(
					policyInterface.getCategoryMapFromDatabase());
			Map<Integer, String> subcategorymap = new TreeMap<Integer, String>(
					policyInterface.getSubCategoryMapFromDatabase());
			List<KnowYourPolicyBean> policylist = policyInterface.getAllPolicy(groupmap, categorymap, subcategorymap);
			KnowYourPolicyBean bean = new KnowYourPolicyBean();
			m.addAttribute("policybean", bean);
			m.addAttribute("groupmap", groupmap);
			m.addAttribute("categorymap", categorymap);
			m.addAttribute("policylist", policylist);
			String isSaved = request.getParameter("isSaved");
			String isDeleted = request.getParameter("isDeleted");
			String isUpdated = request.getParameter("isUpdated");
			String isMissing = request.getParameter("isMissing");
			if (!StringUtils.isBlank(isMissing)) {
				if (Boolean.valueOf(isMissing)) {
					setError(request, "Group Name , Title , Description are Mandatory Field to Create Policy !");
				}
			}
			if (!StringUtils.isBlank(isSaved)) {
				if (Boolean.valueOf(isSaved)) {
					setSuccess(request, "Policy Added Successfully !");
				} else {
					setError(request, "Error While Adding Policy !");
				}
			}
			if (!StringUtils.isBlank(isDeleted)) {
				if (Boolean.valueOf(isDeleted)) {
					setSuccess(request, "Policy Deleted Successfully !");
				} else {
					setError(request, "Error While Deleting Policy !");
				}
			}

			if (!StringUtils.isBlank(isUpdated)) {
				if (Boolean.valueOf(isUpdated)) {
					setSuccess(request, "Policy Updated Successfully !");
				} else {
					setError(request, "Error While Updating Policy !");
				}
			}

		} catch (Exception e) {
			logger.error("Error While Loading  Know Your Policy Entry Form:" + e);
			// e.printStackTrace();
		}

		return new ModelAndView("jsp/knowYourPolicy/knowYourPolicyEntryForm");
	}

	@GetMapping(value = "/knowYourPolicyCategory")
	public ModelAndView KnowYourPolicyCategoryForm(Model m, HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}
		try {
			KnowYourPolicyBean bean = new KnowYourPolicyBean();
			m.addAttribute("bean", bean);
			Map<Integer, String> categorymap = new TreeMap<Integer, String>(
					policyInterface.getCategoryMapFromDatabase());
			m.addAttribute("categorymap", categorymap);
			String isSaved = request.getParameter("isSaved");
			String isDuplicate = request.getParameter("isDuplicate");
			if (!StringUtils.isBlank(isDuplicate)) {
				if (Boolean.valueOf(isDuplicate)) {
					setError(request, "Category Already Exists !");
				}
			}
			if (!StringUtils.isBlank(isSaved)) {
				if (Boolean.valueOf(isSaved)) {
					setSuccess(request, "Category Added Successfully !");
				} else
					setError(request, "Error While Adding Category !");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error While Loading Category Form Page:" + e);
		}

		return new ModelAndView("jsp/knowYourPolicy/knowYourPolicyCategoryForm");
	}

	@GetMapping("/knowYourPolicySubCategory")
	public ModelAndView KnowYourPolicySubCateogyForm(Model m, HttpServletRequest request,
			HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}
		KnowYourPolicyBean bean = new KnowYourPolicyBean();
		try {
			Map<Integer, String> categorymap = new TreeMap<Integer, String>(
					policyInterface.getCategoryMapFromDatabase());
			List<KnowYourPolicyBean> subcategorylist = policyInterface.getSubcategoryListFromDataBase();
			m.addAttribute("subcateogorymap", subcategorylist);
			m.addAttribute("categorymap", categorymap);
			m.addAttribute("bean", bean);
			String isSaved = request.getParameter("isSaved");
			String isDuplicate = request.getParameter("isDuplicate");
			String isMissing = request.getParameter("isMissing");
			if (!StringUtils.isBlank(isMissing)) {
				if (Boolean.valueOf(isMissing)) {
					setError(request, "Category Name And Sub Category Name are Mandatory Field to Add Sub Category !");
				}
			}
			if (!StringUtils.isBlank(isDuplicate)) {
				if (Boolean.valueOf(isDuplicate)) {
					setError(request, "Sub Category Already Exists !");
				}
			}
			if (!StringUtils.isBlank(isSaved)) {
				if (Boolean.valueOf(isSaved)) {
					setSuccess(request, "Sub Category Added Successfully !");
				} else {
					setError(request, "Error While Adding Sub Category !");
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error While Loading Sub Category Form Page:" + e);
		}
		return new ModelAndView("jsp/knowYourPolicy/knowYourPolicySubCategoryForm");
	}

	@PostMapping(value = "/fetchsubcateogyusingcateogy", consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<KnowYourPolicyBean>> fetchsubcategoryusingcategory(
			@RequestBody KnowYourPolicyBean bean) {
		int categoryId = 0;
		List<KnowYourPolicyBean> subcategorylist = new ArrayList<KnowYourPolicyBean>();
		try {
			categoryId = bean.getCategoryId();
			subcategorylist = policyInterface.fetchSubcategoryusingcategory(categoryId);
			return new ResponseEntity<>(subcategorylist, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Error While Fetching SubCategory List Using Category Id " + categoryId + " :" + e);
			return new ResponseEntity<>(subcategorylist, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("savepolicy")
	public String savepolicy(@ModelAttribute KnowYourPolicyBean policybean, HttpServletRequest request, Model m) {
		String createdBy = "";
		String title = "";
		String description = "";
		int groupId = 0;
		int categoryId = 0;
		int subcategoryId = 0;
		try {
			createdBy = String.valueOf(request.getSession().getAttribute("userId"));
			title = policybean.getTitle();
			description = policybean.getDescription();
			groupId = policybean.getGroupId();
			categoryId = policybean.getCategoryId();
			subcategoryId = policybean.getSubcategoryId();
			if (groupId == 0 || StringUtils.isBlank(title) || StringUtils.isBlank(description)) {
				return "redirect:/knowYourPolicyEntry?isMissing=true";
			}
			policyInterface.savePolicy(createdBy, title, description, groupId, categoryId, subcategoryId);
			return "redirect:/knowYourPolicyEntry?isSaved=true";
		} catch (Exception e) {
			logger.error("Error While Saving Policy:" + e);
			// e.printStackTrace();
			return "redirect:/knowYourPolicyEntry?isSaved=false";
		}
	}

	@GetMapping("updatepolicyform")
	public ModelAndView updatepolicyform(HttpServletRequest request, Model m, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}
		int policyId = 0;
		try {
			policyId = Integer.valueOf(request.getParameter("policyId"));
			List<KnowYourPolicyBean> bean = new ArrayList<KnowYourPolicyBean>();
			Map<Integer, String> groupmap = new TreeMap<Integer, String>(policyInterface.getGroupMapFromDatabase());
			Map<Integer, String> categorymap = new TreeMap<Integer, String>(
					policyInterface.getCategoryMapFromDatabase());
			Map<Integer, String> subcategorymap = new TreeMap<Integer, String>(
					policyInterface.getSubCategoryMapFromDatabase());
			List<KnowYourPolicyBean> policylist = policyInterface.getAllPolicy(groupmap, categorymap, subcategorymap);
			KnowYourPolicyBean bean1 = new KnowYourPolicyBean();
			List<Integer> grouplist = new ArrayList<Integer>(groupmap.keySet());
			List<Integer> categorylist = new ArrayList<Integer>(categorymap.keySet());
			for (KnowYourPolicyBean policy : policylist) {
				if (policy.getPolicyId() == policyId) {
					for (Integer group : grouplist) {
						if (group == policy.getGroupId()) {
							groupmap.remove(group);
						}
					}
					for (Integer category : categorylist) {
						if (category == policy.getCategoryId()) {
							categorymap.remove(category);
						}
					}
					bean.add(policy);
				}
			}
			request.getSession().setAttribute("policyId", policyId);
			m.addAttribute("policybean", bean1);
			m.addAttribute("groupmap", groupmap);
			m.addAttribute("categorymap", categorymap);
			m.addAttribute("policy", bean);
		} catch (Exception e) {
			logger.error("Error While Loading Uploading Update Policy Form Having Policy Id " + policyId + ":" + e);
			e.printStackTrace();
		}
		return new ModelAndView("jsp/knowYourPolicy/updateKnowYourPolicy");
	}

	@PostMapping("updatepolicy")
	public String updatepolicy(@ModelAttribute KnowYourPolicyBean bean, HttpServletRequest request, Model m) {
		int policyId = 0;
		String title = "";
		String description = "";
		int groupId = 0;
		int categoryId = 0;
		int subcategoryId = 0;
		String lastModifiedBy = "";
		try {
			policyId = (Integer) request.getSession().getAttribute("policyId");
			title = bean.getTitle();
			description = bean.getDescription();
			groupId = bean.getGroupId();
			categoryId = bean.getCategoryId();
			subcategoryId = bean.getSubcategoryId();
			lastModifiedBy = String.valueOf(request.getSession().getAttribute("userId"));
			policyInterface.updatepolicy(policyId, title, description, groupId, categoryId, subcategoryId,
					lastModifiedBy);
			return "redirect:/knowYourPolicyEntry?isUpdated=true";
		} catch (Exception e) {
			logger.error("Error While Updating Policy Having Policy Id " + policyId + " :" + e);
			// e.printStackTrace();
			setError(request, "Error While Updating Policy");
			return "redirect:/knowYourPolicyEntry?isUpdated=false";
		}
	}

	@GetMapping("deletepolicy")
	public String deletepolicy(HttpServletRequest request, Model m) {
		int policyId = 0;
		try {
			policyId = Integer.valueOf(request.getParameter("policyId"));
			policyInterface.deletepolicy(policyId);
			return "redirect:/knowYourPolicyEntry?isDeleted=true";
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error While Deleting Policy Having Policy Id " + policyId + " :" + e);
			return "redirect:/knowYourPolicyEntry?isDeleted=false";
		}
	}

	@PostMapping("addcategory")
	public String addcategory(@ModelAttribute KnowYourPolicyBean bean, HttpServletRequest request, Model m) {
		String categoryName = "";
		String createdBy = "";
		try {
			createdBy = String.valueOf(request.getSession().getAttribute("userId"));
			categoryName = bean.getCategoryName();
			List<String> categorylist = new ArrayList<>(policyInterface.getCategoryMapFromDatabase().values());
			for (String list : categorylist) {
				if (list.equalsIgnoreCase(categoryName)) {
					return "redirect:/knowYourPolicyCategory?isDuplicate=true";
				}
			}
			policyInterface.addcategory(categoryName, createdBy);
			return "redirect:/knowYourPolicyCategory?isSaved=true";
		} catch (Exception e) {
			logger.error("Error While Adding Category:" + e);
			// e.printStackTrace();
			return "redirect:/knowYourPolicyCategory?isSaved=false";
		}
	}

	@PostMapping("updatepolicycategory")
	public ResponseEntity<Map<String, String>> updatepolicycategory(@RequestBody KnowYourPolicyBean body,
			HttpServletRequest request, Model m, HttpServletResponse response) {
		Map<String, String> res = new HashMap<String, String>();
		String categoryName = "";
		String lastModifiedBy = "";
		int categoryId = 0;
		if (!checkSession(request, response)) {
			res.put("status", "fail");
			return new ResponseEntity<>(res, HttpStatus.OK);
		}
		try {
			categoryName = body.getCategoryName();
			lastModifiedBy = String.valueOf(request.getSession().getAttribute("userId"));
			categoryId = body.getCategoryId();
			List<String> categorylist = new ArrayList<>(policyInterface.getCategoryMapFromDatabase().values());
			for (String list : categorylist) {
				if (list.equalsIgnoreCase(categoryName)) {
					res.put("status", "duplicate");
					return new ResponseEntity<>(res, HttpStatus.OK);
				}
			}
			policyInterface.updatecategory(categoryName, lastModifiedBy, categoryId);
			res.put("status", "success");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("status", "fail");
			logger.error("Error While Updating Category Having Category Id " + categoryId + " :" + e);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping("deletepolicycategory")
	public ResponseEntity<Map<String, String>> deletepolicycategory(HttpServletRequest request, Model m,
			HttpServletResponse response) {
		Map<String, String> res = new HashMap<String, String>();
		int categoryId = 0;
		if (!checkSession(request, response)) {
			res.put("status", "fail");
			res.put("message", "Log in to continue!!");
			return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
		}
		try {
			categoryId = Integer.parseInt(request.getParameter("categoryId"));
			policyInterface.deletecategory(categoryId);
			policyInterface.deletepolicybasedoncategory(categoryId);
			policyInterface.deletesubcategorybasedcategory(categoryId);
			res.put("status", "success");
		} catch (Exception e) {
			logger.error("Error While Deleting Category Having Category Id " + categoryId + " :" + e);
			// e.printStackTrace();
			res.put("status", "fail");
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@PostMapping("updatepolicysubcategory")
	public ResponseEntity<HashMap<String, String>> updatepolicysubcategory(@RequestBody KnowYourPolicyBean map,
			HttpServletResponse response, Model m, HttpServletRequest request) {
		HashMap<String, String> res = new HashMap<String, String>();
		if (!checkSession(request, response)) {
			res.put("status", "fail");
			return new ResponseEntity<HashMap<String, String>>(res, HttpStatus.OK);
		}
		String subcategoryName = "";
		String lastModifiedBy = "";
		int subcategoryId = 0;
		int categoryId = 0;
		try {
			lastModifiedBy = String.valueOf(request.getSession().getAttribute("userId"));
			subcategoryName = map.getSubcategoryName();
			subcategoryId = map.getSubcategoryId();
			categoryId = map.getCategoryId();
			List<KnowYourPolicyBean> subcategorylist = policyInterface.getSubcategoryListFromDataBase();
			for (KnowYourPolicyBean list : subcategorylist) {
				if (list.getSubcategoryName().equalsIgnoreCase(subcategoryName) && list.getCategoryId() == categoryId) {
					res.put("status", "duplicate");
					return new ResponseEntity<>(res, HttpStatus.OK);
				}
			}
			policyInterface.updatepolicysubcategory(subcategoryName, lastModifiedBy, subcategoryId);
			res.put("status", "success");
		} catch (Exception e) {
			logger.error("Error While Updating Subcategory Having subcategory Id " + subcategoryId + " :" + e);
			res.put("status", "fail");
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping("deletepolicysubcategory")
	public ResponseEntity<Map<String, String>> deletepolicysubcategory(HttpServletRequest request, Model m,
			HttpServletResponse response) {
		Map<String, String> res = new HashMap<String, String>();
		int subcategoryId = 0;
		if (!checkSession(request, response)) {
			res.put("status", "fail");
			res.put("message", "Log in to continue!!");
			return new ResponseEntity<>(res, HttpStatus.OK);
		}
		try {
			subcategoryId = Integer.parseInt(request.getParameter("subcategoryId"));
			policyInterface.deletepolicysubcategory(subcategoryId);
			policyInterface.deletepolicybasedonsubcateogry(subcategoryId);
			res.put("status", "success");
		} catch (Exception e) {
			logger.error("Error While Deleting Subcategory Having subcategory Id " + subcategoryId + " :" + e);
			// e.printStackTrace();
			res.put("status", "fail");
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@PostMapping("addsubcategory")
	public String addsubcategory(@ModelAttribute KnowYourPolicyBean bean, HttpServletRequest request, Model m) {
		String subcategoryName = "";
		int categoryId = 0;
		String createdBy = "";
		try {
			createdBy = String.valueOf(request.getSession().getAttribute("userId"));
			subcategoryName = bean.getSubcategoryName();
			categoryId = bean.getCategoryId();
			List<KnowYourPolicyBean> subcategorylist = policyInterface.getSubcategoryListFromDataBase();
			for (KnowYourPolicyBean list : subcategorylist) {
				if (list.getSubcategoryName().equalsIgnoreCase(subcategoryName) && list.getCategoryId() == categoryId) {
					return "redirect:/knowYourPolicySubCategory?isDuplicate=true";
				}
			}

			if (categoryId == 0 || StringUtils.isBlank(subcategoryName)) {
				return "redirect:/knowYourPolicySubCategory?isMissing=true";
			}
			policyInterface.addsubcategory(subcategoryName, categoryId, createdBy);
			return "redirect:/knowYourPolicySubCategory?isSaved=true";
		} catch (Exception e) {
			logger.error("Error While Adding Sub Category:" + e);
			// e.printStackTrace();
			return "redirect:/knowYourPolicySubCategory?isSaved=false";
		}
	}
}
