package com.nmims.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nmims.beans.ExamOrderAcadsBean;

/**
 * 
 * @author Siddheshwar_K
 *
 */
public interface ExamOrderUtil {
	
	public static Map<String, BigDecimal> generateExamOrderMap(List<ExamOrderAcadsBean> liveFlagList) {
		HashMap<String, BigDecimal> orderMap = new HashMap<String, BigDecimal>();
		for (ExamOrderAcadsBean row : liveFlagList) {
			orderMap.put(row.getMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
			orderMap.put(row.getAcadMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
		}
		return orderMap;
	}
	
	public static double getMaxOrderOfAcadSessionLive(List<ExamOrderAcadsBean> liveFlagList){
		double sessionLiveOrder = 0.0;
		for (ExamOrderAcadsBean bean : liveFlagList) {
			double currentOrder = Double.parseDouble(bean.getOrder());
			if("Y".equalsIgnoreCase(bean.getAcadSessionLive()) && currentOrder > sessionLiveOrder){
				sessionLiveOrder = currentOrder;
			}
		}
		return sessionLiveOrder;
	}
	
	public static double getMaxOrderOfAcadContentLive(List<ExamOrderAcadsBean> liveFlagList) {
		double contentLiveOrder = 0.0;
		for (ExamOrderAcadsBean bean : liveFlagList) {
			double currentOrder = Double.parseDouble(bean.getOrder());
			if("Y".equalsIgnoreCase(bean.getAcadContentLive()) && currentOrder > contentLiveOrder){
				contentLiveOrder = currentOrder;
			}
		}
		return contentLiveOrder;
	}
}
