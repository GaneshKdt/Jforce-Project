package com.nmims.helpers;

import java.util.ArrayList;
import java.util.List;

import com.nmims.beans.BaseExamBean;

public class DataFilter<E extends BaseExamBean> {

	public List<E> getFilteredList(ArrayList<String> authorizedStudentNumbers, List<E> unFilteredDataList) {
		List<E> filteredList = new ArrayList<E>();
		for (E bean : unFilteredDataList) {
			String studentNumber = bean.getSapid();
			if(authorizedStudentNumbers.contains(studentNumber)){
				filteredList.add(bean);
			}
		}
		return filteredList;
	}
	
}
