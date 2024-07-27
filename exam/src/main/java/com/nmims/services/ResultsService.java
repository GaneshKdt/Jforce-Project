package com.nmims.services;

import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentsDataInRedisBean;

public interface ResultsService {

	String setAllResultsDataInRedisCache();

	StudentsDataInRedisBean getResultsDataFromRedisBySapid(String sapid);

	StudentMarksBean setAllResultsDataInRedisCacheByYearMonth(StudentMarksBean studentMarksBean);

	StudentMarksBean setResultsDataInRedisCacheBySapid(StudentMarksBean studentMarksBean);

}
