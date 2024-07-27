package com.nmims.stratergies;

import java.util.List;
import java.util.Optional;

import com.nmims.beans.EmbaPassFailBean;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface IUpsertTimeboundStudentProjectPassFailStrategy {
	
	/**
	 * This method is used to insert or update the given students project pass-fail records. 
	 * @param studentPassFailList - list of processed pass-fail records.
	 * @return It returns the list of errors during the insertion or updating.
	 */
	public List<String> upsertTimeboundStudentProjectPassFail(List<EmbaPassFailBean> studentPassFailList);
	
	/**
	 * Update time-bound student project pass-fail processed flag based on the sapId and timeboundId.
	 * @param studentPassFailBean - pass-fail processed bean.
	 * @return updated count.
	 * @throws Exception 
	 */
	public int updateTimeboundStudentProjectMarks(EmbaPassFailBean studentPassFailBean) throws Exception;
	
	/**
	 * Update time-bound student project pass-fail data based on the sapId and pssId.
	 * @param studentPassFailBean - pass-fail processed bean.
	 * @return updated count.
	 * @throws Exception 
	 */
	public int updateTimeboundStudentProjectPassFail(EmbaPassFailBean studentPassFailBean) throws Exception;
	
	/**
	 * This method is used to insert time-bound student project pass-fail record.
	 * @param studentPassFailBean - pass-fail processed bean.
	 * @return updated count.
	 * @throws Exception 
	 */
	public int insertTimeboundStudentProjectPassFail(EmbaPassFailBean studentPassFailBean) throws Exception;
	
	/**
	 * This method is used to get the student pass-fail data based on the sapId and program sem subject Id.
	 * @param sapId - Contains student number e.g 77777777778
	 * @param pssId - Contains program sem subject id.
	 * @return The student pass-fail data.
	 */
	public Optional<EmbaPassFailBean> getTimeboundStudentProjectPassFail(String sapId, Integer pssId);
}
