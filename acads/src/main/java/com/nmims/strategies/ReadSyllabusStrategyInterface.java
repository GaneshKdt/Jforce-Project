package com.nmims.strategies;

import java.util.ArrayList;

import com.nmims.beans.SyllabusBean;

public interface ReadSyllabusStrategyInterface {

	public abstract ArrayList<SyllabusBean> readSyllabusForPSS(SyllabusBean bean) throws Exception;
	
	public abstract SyllabusBean readSyllabusForSyllabusId(SyllabusBean bean) throws Exception;
	
}