package com.nmims.interfaces;

import java.util.List;
import java.util.Map;

import com.nmims.beans.KnowYourPolicyBean;

public interface KnowYourPolicyInterface {
	Map<Integer, String> getGroupMapFromCache();

	Map<Integer, String> getGroupMapFromDatabase();

	Map<Integer, String> getCategoryMapFromDatabase();

	Map<Integer, String> getSubCategoryMapFromCache();

	Map<Integer, String> getSubCategoryMapFromDatabase();

	Map<Integer, String> getCategoryMapFromCache();

	List<KnowYourPolicyBean> getAllPolicy(Map<Integer, String> grouplist, Map<Integer, String> categorylist,
			Map<Integer, String> subcategorylist);

	void updatepolicy(int policyId, String title, String description, int groupid, int categoryId, int subcategoryId,
			String lastModifiedBy);

	void savePolicy(String createdBy, String title, String description, int groupId, int categoryId, int subcategoryId);

	void deletepolicy(int id);

	void updatecategory(String categoryName, String lastModifiedBy, int categoryId);

	void deletecategory(int id);

	void addcategory(String categoryName, String createdBy);

	void deletesubcategorybasedcategory(int categoryId);

	void deletepolicybasedonsubcateogry(int subcategoryId);

	void deletepolicybasedoncategory(int categoryId);

	List<KnowYourPolicyBean> fetchSubcategoryusingcategory(int categoryId);

	List<KnowYourPolicyBean> getSubcategoryListFromDataBase();

	void updatepolicysubcategory(String subcategoryName, String lastModifiedBy, int subcategoryId);

	void deletepolicysubcategory(int subcategoryId);

	void addsubcategory(String subcategoryName, int categoryId, String createdBy);

	int getcategoryIdBySubcategory(int subcategoryId);
}
