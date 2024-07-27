package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.FaqCategoryBean;
import com.nmims.beans.FaqQuestionAnswerTableBean;
import com.nmims.beans.FaqSubCategoryBean;
import com.nmims.daos.FaqDao;

@Service
public class FaqServices {

	@Autowired
	FaqDao faqdao;

	public ArrayList<FaqCategoryBean> getFaqCategories() {
		return faqdao.getFAQCategories();
	}

	public List<FaqSubCategoryBean> getFaqSubCategories() {
		return faqdao.getListOfSubCategerys();
	}

	public Map<Integer, List<FaqQuestionAnswerTableBean>> getMapOfFaqQuestionAnswer(String faqGroupTypeId, List<Integer> categoryIds) {
		Map<Integer, List<FaqQuestionAnswerTableBean>> questionmap = new HashMap<>();
		categoryIds.stream().forEach(x -> {
			questionmap.put(x, faqdao.getListOfFaqQnA(faqGroupTypeId, String.valueOf(x)));
		});
		return questionmap;
	}

	public Map<Integer, List<FaqSubCategoryBean>> getMapOfSubCatageory(List<Integer> categoryIds) {
		Map<Integer, List<FaqSubCategoryBean>> questionmap = new HashMap<>();
		categoryIds.stream().forEach(x -> {
			questionmap.put(x, faqdao.getFAQSubCategories(String.valueOf(x)));
		});
		return questionmap;
	}

	public Map<String, ArrayList<FaqQuestionAnswerTableBean>> getMapOfQnASubCategory(String faqGroupTypeId,
			List<Integer> categoryIds, List<Integer> subCategoryIds) {
		Map<String, ArrayList<FaqQuestionAnswerTableBean>> mapOfQnASubCategory = new HashMap<String, ArrayList<FaqQuestionAnswerTableBean>>();
		categoryIds.stream().forEach(e -> {
			subCategoryIds.stream().forEach(x -> {
				mapOfQnASubCategory.put(e + "-" + x,
						faqdao.getListOfFaqQnASubCategory(faqGroupTypeId, String.valueOf(e), String.valueOf(x)));
			});
		});
		return mapOfQnASubCategory;
	}

}
