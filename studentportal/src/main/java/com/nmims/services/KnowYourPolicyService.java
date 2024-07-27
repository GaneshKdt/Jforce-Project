package com.nmims.services;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.KnowYourPolicyBean;
import com.nmims.daos.KnowYourPolicyDAO;
import com.nmims.interfaces.KnowYourPolicyInterface;

@Service
public class KnowYourPolicyService implements KnowYourPolicyInterface {

	@Autowired
	KnowYourPolicyDAO policyDAO;

	Map<Integer, String> groupmap = null;
	Map<Integer, String> categorymap = null;
	Map<Integer, String> subcategorymap = null;

	public Map<Integer, String> getGroupMapFromCache() {
		if (this.groupmap == null) {
			List<KnowYourPolicyBean> list = policyDAO.getAllGroups();
			for (KnowYourPolicyBean group : list) {
				groupmap.put(group.getGroupId(), group.getGroupName());
			}
		}
		return groupmap;
	}

	public Map<Integer, String> getCategoryMapFromCache() {
		if (this.categorymap == null) {
			List<KnowYourPolicyBean> categoryobjectlist = policyDAO.getAllCategory();
			for (KnowYourPolicyBean category : categoryobjectlist) {
				categorymap.put(category.getCategoryId(), category.getCategoryName());
			}
		}
		return categorymap;
	}

	public Map<Integer, String> getGroupMapFromDatabase() {
		Map<Integer, String> groupmap = new TreeMap<>();
		List<KnowYourPolicyBean> list = policyDAO.getAllGroups();
		for (KnowYourPolicyBean group : list) {
			groupmap.put(group.getGroupId(), group.getGroupName());
		}
		return groupmap;
	}

	public Map<Integer, String> getCategoryMapFromDatabase() {
		Map<Integer, String> categorymap = new TreeMap<>();
		List<KnowYourPolicyBean> categoryobjectlist = policyDAO.getAllCategory();
		for (KnowYourPolicyBean category : categoryobjectlist) {
			categorymap.put(category.getCategoryId(), category.getCategoryName());
		}
		return categorymap;
	}

	public Map<Integer, String> getSubCategoryMapFromDatabase() {
		Map<Integer, String> subcategorymap = new TreeMap<>();
		List<KnowYourPolicyBean> subcategoryobjectlist = policyDAO.getAllSubcategory();
		for (KnowYourPolicyBean subcategory : subcategoryobjectlist) {
			subcategorymap.put(subcategory.getSubcategoryId(), subcategory.getSubcategoryName());
		}
		return subcategorymap;
	}

	public void savePolicy(String createdBy, String title, String description, int groupId, int categoryId,
			int subcategoryId) {
		policyDAO.savePolicy(createdBy, title, description, groupId, categoryId, subcategoryId);
	}

	public Map<Integer, String> getSubCategoryMapFromCache() {
		if (this.subcategorymap == null) {
			List<KnowYourPolicyBean> subcategoryobjectlist = policyDAO.getAllSubcategory();
			for (KnowYourPolicyBean subcategory : subcategoryobjectlist) {
				subcategorymap.put(subcategory.getSubcategoryId(), subcategory.getSubcategoryName());
			}
		}
		return subcategorymap;
	}

	public List<KnowYourPolicyBean> getAllPolicy(Map<Integer, String> grouplist, Map<Integer, String> categorylist,
			Map<Integer, String> subcategorylist) {
		List<KnowYourPolicyBean> policylist = policyDAO.getAllPolicy();
		if (policylist.size() > 0) {
			for (int i = 0; i < policylist.size(); i++) {
				policylist.get(i).setGroupName(grouplist.get(policylist.get(i).getGroupId()));
				if (policylist.get(i).getCategoryId() != 0) {
					policylist.get(i).setCategoryName(categorylist.get(policylist.get(i).getCategoryId()));
				} else {
					policylist.get(i).setCategoryName("");
				}
				if (policylist.get(i).getSubcategoryId() != 0) {
					policylist.get(i).setSubcategoryName(subcategorylist.get(policylist.get(i).getSubcategoryId()));
				} else {
					policylist.get(i).setSubcategoryName("");
				}
			}
		}
		return policylist;
	}

	public void updatepolicy(int policyId, String title, String description, int groupid, int categoryId,
			int subcategoryId, String lastModifiedBy) {
		policyDAO.updatepolicy(policyId, title, description, groupid, categoryId, subcategoryId, lastModifiedBy);
	}

	public void deletepolicy(int id) {
		policyDAO.deletepolicy(id);
	}

	public void updatecategory(String categoryName, String lastModifiedBy, int categoryId) {
		policyDAO.updatecategory(categoryName, lastModifiedBy, categoryId);
	}

	public void deletecategory(int id) {
		policyDAO.deletecategory(id);
	}

	public void addcategory(String categoryName, String createdBy) {
		policyDAO.addcategory(categoryName, createdBy);
	}

	public List<KnowYourPolicyBean> fetchSubcategoryusingcategory(int categoryId) {
		return policyDAO.fetchsubcategoryusingcategory(categoryId);
	}

	public List<KnowYourPolicyBean> getSubcategoryListFromDataBase() {
		return policyDAO.getAllSubcategory();
	}

	public void updatepolicysubcategory(String subcategoryName, String lastModifiedBy, int subcategoryId) {
		policyDAO.updatepolicysubcategory(subcategoryName, lastModifiedBy, subcategoryId);
	}

	public void deletepolicysubcategory(int subcategoryId) {
		policyDAO.deletepolicysubcategory(subcategoryId);
	}

	public void addsubcategory(String subcategoryName, int categoryId, String createdBy) {
		policyDAO.addsubcategory(subcategoryName, categoryId, createdBy);
	}

	public void deletesubcategorybasedcategory(int categoryId) {
		policyDAO.deletesubcategorybasedcategory(categoryId);
	}

	public void deletepolicybasedonsubcateogry(int subcategoryId) {
		policyDAO.deletepolicybasedonsubcategory(subcategoryId);
	}

	public void deletepolicybasedoncategory(int categoryId) {
		policyDAO.deletepolicybasedoncategory(categoryId);
	}

	
	public int getcategoryIdBySubcategory(int subcategoryId) {
		return policyDAO.getcategoryIdBySubcategoryId(subcategoryId);
	}
}
