package com.nmims.stratergies;

import java.util.List;
import java.util.Optional;

import com.nmims.beans.TEEResultBean;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface IUpsertTimeboundStudentProjectMarksStrategy {
	
	/**
	 * This method is used to insert or update the given students project marks. 
	 * @param studentMarksList - Contains students project marks
	 * @return It returns the list of errors.
	 */
	List<String> upsertTimeboundStudentProjectMarks(List<TEEResultBean> studentMarksList);
	
	/**
	 * This method is used to student project marks based on sapId and program sem subject id.
	 * @param sapId - student number e.g 77777777778
	 * @param pssId - program sem subject id.
	 * @return the stundet project marks bean.
	 */
	Optional<TEEResultBean> getTimeboundStudentProjectMarks(String sapId, int pssId);
	
}