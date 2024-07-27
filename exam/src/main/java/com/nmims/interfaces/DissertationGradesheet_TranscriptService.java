package com.nmims.interfaces;

import java.util.List;

import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.MBAPassFailBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.dto.DissertationResultProcessingDTO;

public interface DissertationGradesheet_TranscriptService {

	public EmbaPassFailBean getPassFailForQ7(String sapid);

	public int checkSapidExistForQ7(String sapid);

	public EmbaPassFailBean getPassFailForQ8(String sapid);

	public int checkSapidExistForQ8(String sapid);

	public StudentSubjectConfigExamBean getPassFailForQ8Timebound(String sapid);

	public MBAPassFailBean getPassFailForQ7Transcript(String sapid);

	public MBAPassFailBean getPassFailForQ8Transcript(String sapid);

	public List<Integer> getTimeboundUser(String sapid);

	public DissertationResultBean getPassFail(String sapid);

	public List<DissertationResultProcessingDTO> getTimeBounds(String commaSepratedTimeBoundIds);

	public DissertationResultProcessingDTO getSubjectDetails(int iaMasterDissertationSubjectId);

	public EmbaPassFailBean filterSubjectToClear(DissertationResultBean passFail,
			List<DissertationResultProcessingDTO> timeBound, DissertationResultProcessingDTO subjects);

	public boolean hasAppearedForQ8(String sapid);

	public DissertationResultBean getPassFailQ8(String sapid);

}
