package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;

import com.nmims.beans.ConsumerType;
import com.nmims.beans.ProgramsBean;
import com.nmims.daos.ExitSrMdmDao;

public interface ExitSrMdmService {
	public ArrayList<ProgramsBean> getMappedNewMasterKey();
	public ArrayList<ConsumerType> getConsumerTypeList();
	public String getMasterkey(String program,String programStructure,String consumerType);
	public int getAlreadyEntryCheck(String consumerprogramStructureId,String sem);
	public boolean insertCertificateByMasterkey(String consumerprogramStructureId,String sem,String newConsumerprogramStructureId,String userId);
	public int getTotalSemByMasterKey(String consumerprogramStructureId);
	public void updateSemCertificateExitprogram(String sem,String newConsumerprogramStructureId,String id,String userId);
	public void deleteSemCertificateExitprogram(String id);
	public ArrayList<String> getlistofConsumerProgramStructureIdbySem(String sem);
	public ArrayList<ProgramsBean> getListOfSrExitData();
	public ArrayList<ProgramsBean> getListOfNewMappedPrograms();
	public HashMap<String,ProgramsBean> getmappedIdAndProgramBean();
}
