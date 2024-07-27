package com.nmims.interfaces;

import java.util.List;

import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestExamBean;
import com.nmims.dto.DissertationResultProcessingDTO;

public interface DissertationQ8FilterService {

	public DissertationResultBean filterDissertationQ8IAScores(TEEResultBean id, List<DissertationResultProcessingDTO> iaTestScore,
			List<TestExamBean> examTest, String loggerInUser);

}
