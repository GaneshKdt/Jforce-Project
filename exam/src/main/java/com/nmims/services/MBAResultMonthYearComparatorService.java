package com.nmims.services;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.nmims.beans.MBAPassFailBean;

public class MBAResultMonthYearComparatorService implements Comparator<MBAPassFailBean> {

	@Override
	public int compare(MBAPassFailBean o1, MBAPassFailBean o2) {
		String month1 = o1.getExamMonth();
		String year1 = o1.getExamYear();
		String monthYear1 = getYearValue(year1) + " " + getMonthValue(month1);
		
		String month2 = o2.getExamMonth();
		String year2 = o2.getExamYear();
		String monthYear2 = getYearValue(year2) + " " + getMonthValue(month2);

		return monthYear1.compareTo(monthYear2);
	}
	
	private int getYearValue(String year) {
		if(StringUtils.isNumeric(year)) {
			return Integer.parseInt(year);
		} else {
			return 0;
		}
	}

	private int getMonthValue(String month) {
		switch(month.toUpperCase()) {
			case "JAN" : return 1;
			case "FEB" : return 2;
			case "MAR" : return 3;
			case "APR" : return 4;
			case "MAY" : return 5;
			case "JUN" : return 6;
			case "JUL" : return 7;
			case "AUG" : return 8;
			case "SEP" : return 9;
			case "OCT" : return 10;
			case "NOV" : return 11;
			case "DEC" : return 12;
			default : return 0;
		}
	}
	
}
