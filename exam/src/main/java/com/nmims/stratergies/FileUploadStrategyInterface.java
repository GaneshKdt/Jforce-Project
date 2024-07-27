/**
 * 
 */
package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.RemarksGradeBean;

/**
 * @author vil_m
 *
 */
public interface FileUploadStrategyInterface {
	/**
	 * Primary method to be implemented to process excel file.
	 * @param bean
	 * @param userId
	 * @return
	 */
	public List<RemarksGradeBean> processMarksExcelFile(RemarksGradeBean bean, String userId);
	
	/**
	 * Implement to validate data read from excel file for each column and row.
	 * Database access made to retrieve certain data (existing Subjects).
	 * HibernateValidator used to validate certain fields.
	 * @param list
	 * @return
	 */
	public List<RemarksGradeBean> validateMarksExcelFile(List<RemarksGradeBean> list);
	
	/**
	 * Implement to save data read from excel file in database.
	 * Necessary checks made of ProgramSemSubject, prior to save in transaction.
	 * @param list
	 * @param examYear
	 * @param examMonth
	 * @param userId
	 * @return
	 */
	public boolean saveMarksExcelFile(final List<RemarksGradeBean> list, final String examYear, final String examMonth,
			final String userId);
}
