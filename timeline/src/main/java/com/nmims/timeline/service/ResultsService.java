package com.nmims.timeline.service;

import com.nmims.timeline.model.StudentMarksBean;
import com.nmims.beans.StudentsDataInRedisBean;

public interface ResultsService {

	String setAllResultsDataInRedisCache();

	StudentsDataInRedisBean getResultsDataFromRedisBySapid(String sapid);

	StudentMarksBean setAllResultsDataInRedisCacheByYearMonth(StudentMarksBean studentMarksBean);

	StudentMarksBean setResultsDataInRedisCacheBySapid(StudentMarksBean studentMarksBean);

}
