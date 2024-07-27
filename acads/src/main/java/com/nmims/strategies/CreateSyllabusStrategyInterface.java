package com.nmims.strategies;

import com.nmims.beans.SyllabusBean;

public interface CreateSyllabusStrategyInterface {

	public abstract Long createSyllabus(SyllabusBean bean) throws Exception;
	
}
