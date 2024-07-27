package com.nmims.helpers;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nmims.beans.PassFailExamBean;

public class PassFailSubjectHelper {
	
	public static ArrayList<PassFailExamBean> removeLateralDuplicateSubjectsPG_MBAPolicy_updated(final ArrayList<PassFailExamBean> passList) {
		ArrayList<PassFailExamBean> filteredList = passList.stream()
		        .collect(Collectors.groupingBy(PassFailExamBean::getSubject,
		                Collectors.maxBy((a, b) -> compareMarks(a.getTotal(), b.getTotal()))))
		        .values().stream()
		        .map(Optional::get)
		        .collect(Collectors.toCollection(ArrayList::new));
		return filteredList;
	}
	
	 private static int compareMarks(String marks1, String marks2) {
	        if (marks1.isEmpty() && marks2.isEmpty()) {
	            return 0;
	        } else if (marks1.isEmpty()) {
	            return -1;
	        } else if (marks2.isEmpty()) {
	            return 1;
	        } else {
	            return Integer.compare(Integer.parseInt(marks1), Integer.parseInt(marks2));
	        }
	    }
}
