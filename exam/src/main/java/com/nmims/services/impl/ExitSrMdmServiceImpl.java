package com.nmims.services.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.nmims.beans.ConsumerType;
import com.nmims.beans.ProgramsBean;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.ExitSrMdmDao;
import com.nmims.services.ExitSrMdmService;

@Component
public class ExitSrMdmServiceImpl implements ExitSrMdmService {
	@Autowired
	ExitSrMdmDao exitSrMdmDao;
	
	@Autowired
	DashboardDAO dashboardDao;

	private ArrayList<ProgramsBean>listOfNewProgramMapped=null;
	
	public ArrayList<ProgramsBean> getListOfNewProgramMapped() {
		if (this.listOfNewProgramMapped == null) {
			this.listOfNewProgramMapped = getMappedNewMasterKey();
		}
		return listOfNewProgramMapped;
	}
	
	@Override
	public ArrayList<ProgramsBean> getMappedNewMasterKey() {
		ArrayList<ProgramsBean> listNewMasterKey=exitSrMdmDao.getMappedNewMasterKey();
		return listNewMasterKey;
	}

	@Override
	public ArrayList<ConsumerType> getConsumerTypeList() {
		// TODO Auto-generated method stub
		ArrayList<ConsumerType> consumerTypeListData = dashboardDao.getConsumerTypeList();
		return consumerTypeListData;
	}

	@Override
	public String getMasterkey(String program, String programStructure, String consumerType) {
		// TODO Auto-generated method stub
		String consumerprogramStructureId=exitSrMdmDao.getMasterkey(program,programStructure,consumerType);
		return consumerprogramStructureId;
	}

	@Override
	public int getAlreadyEntryCheck(String consumerprogramStructureId, String sem) {
		// TODO Auto-generated method stub
		int alreadyEntrycheck=exitSrMdmDao.getAlreadyEntryCheck(consumerprogramStructureId,sem);
		return alreadyEntrycheck;
	}

	@Override
	public boolean insertCertificateByMasterkey(String consumerprogramStructureId, String sem,
			String newConsumerprogramStructureId, String userId) {
		// TODO Auto-generated method stub
		boolean saveCount = false;
		try {
			saveCount = exitSrMdmDao.insertCertificateByMasterkey(consumerprogramStructureId,sem,newConsumerprogramStructureId,userId);
		} catch (SQLException e) {
		
		}
		return saveCount;
	}

	@Override
	public int getTotalSemByMasterKey(String consumerprogramStructureId) {
		int sem=exitSrMdmDao.getTotalSemByMasterKey(consumerprogramStructureId);
		return sem;
	}

	@Override
	public void updateSemCertificateExitprogram(String sem, String newConsumerprogramStructureId, String id,String userId) {
		exitSrMdmDao.updateSemCertificateExitprogram(sem,newConsumerprogramStructureId,id,userId);
		
	}

	@Override
	public void deleteSemCertificateExitprogram(String id) {
		// TODO Auto-generated method stub
		exitSrMdmDao.deleteSemCertificateExitprogram(id);
		
	}

	@Override
	public ArrayList<String> getlistofConsumerProgramStructureIdbySem(String sem) {
		// TODO Auto-generated method stub
		ArrayList<String> listofConsumerProgramStructureId=exitSrMdmDao.getlistofConsumerProgramStructureIdbySem(sem);
		return listofConsumerProgramStructureId;
	}

	@Override
	public ArrayList<ProgramsBean> getListOfSrExitData() {
		// TODO Auto-generated method stub
		ArrayList<ProgramsBean>  listOfSrExitData =exitSrMdmDao.getListOfSrExitData();
		return listOfSrExitData;
	}

	@Override
	public ArrayList<ProgramsBean> getListOfNewMappedPrograms() {
		HashMap<String,ProgramsBean> mappedIdAndProgrambean=getmappedIdAndProgramBean();
		  ArrayList<ProgramsBean>listOfSrExitData=getListOfSrExitData();				
		  for(ProgramsBean bean:listOfSrExitData) {			  
				  ProgramsBean bean1= mappedIdAndProgrambean.get(bean.getConsumerProgramStructureId());		
				  ProgramsBean bean2=mappedIdAndProgrambean.get(bean.getNewConsumerProgramStructureId());	
				  bean.setProgramname(bean1.getProgram());
				  bean.setConsumerType(bean1.getConsumerType());
				  bean.setProgramStructure(bean1.getProgramStructure());
				  bean.setNewPrgm_structure_map(bean2.getConsumerType()+" - "+bean2.getProgramStructure()+" - "+bean2.getProgram());
				  
		  }
		  	return listOfSrExitData;
		
	}
	
	 public HashMap<String,ProgramsBean> getmappedIdAndProgramBean() {
		  HashMap<String,ProgramsBean> mappedIdAndProgram=new HashMap<String,ProgramsBean>();
		  ArrayList<ProgramsBean> getListNewMasterKey=getListOfNewProgramMapped();		
		  for(ProgramsBean bean:getListNewMasterKey) {
			  mappedIdAndProgram.put(bean.getId(),bean);
		  }
		  return mappedIdAndProgram;
	}

	
	
}
