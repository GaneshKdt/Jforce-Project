package com.nmims.interfaces;

import com.nmims.beans.SessionPlanPgBean;

public interface SessionPlanPGInterface {
	
	 SessionPlanPgBean fetchModuleDetails(String programSemSubjectId, String sapId) throws Exception;
}
