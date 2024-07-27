package com.nmims.strategies;

import com.nmims.beans.SyllabusBean;

public interface DeleteSyllabusStrategyInterface {

	public abstract int deleteSyllabus(SyllabusBean bean) throws Exception;

	public abstract int deleteSyllabusDetails(SyllabusBean bean) throws Exception;
	
}
