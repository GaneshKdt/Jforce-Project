package com.nmims.strategies;

import java.util.ArrayList;
import com.nmims.beans.SyllabusBean;

public interface SubjectForSyllabusStrategyInterface {

	public abstract ArrayList<SyllabusBean> getSubject() throws Exception;
	
}
