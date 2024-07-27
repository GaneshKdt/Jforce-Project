package com.nmims.interfaces;

import java.util.List;

import com.nmims.beans.BatchBean;
import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.DummyUserBean;

public interface IDummyUserMgmtService {
	//Get ConsumerTypes
	public List<ConsumerProgramStructureAcads> getAllConsumerType();
	
	//Get ProgramTypes
	public List<ConsumerProgramStructureAcads> getProgramTypeByConsumerType(String consumerTypeId);
	
	
	//Get Programs
	public List<ConsumerProgramStructureAcads> getProgramByConsumerType(String consumerTypeId);
	public List<ConsumerProgramStructureAcads> getProgramByProgramTypeAndConsumerType(ConsumerProgramStructureAcads consumerProgramStructure);
	public List<ConsumerProgramStructureAcads> getProgramByMasterKey(ConsumerProgramStructureAcads consumerProgramStructure);
	
	
	//Get ProgramStructure
	public List<ConsumerProgramStructureAcads> getProgramStructureByConsumerType(String consumerTypeId);
	public List<ConsumerProgramStructureAcads> getProgramStructureByProgramTypeAndConsumerType(ConsumerProgramStructureAcads consumerProgramStructure);
	

	//Get Batches
	public List<ConsumerProgramStructureAcads> getBatchByConsumerType(String consumerTypeId);
	public List<ConsumerProgramStructureAcads> getBatchByProgramTypeAndConsumerType(ConsumerProgramStructureAcads consumerProgramStructure);
	public List<ConsumerProgramStructureAcads> getBatchByMasterKey(ConsumerProgramStructureAcads consumerProgramStructure);
	public List<ConsumerProgramStructureAcads> getBatchByMasterKeyAndProgram(ConsumerProgramStructureAcads consumerProgramStructure);
	public List<ConsumerProgramStructureAcads> getBatchByMasterKeyAndProgramAndSem(ConsumerProgramStructureAcads consumerProgramStructure);
	
	
	//Get Applicable Dummy Users
	public List<DummyUserBean> getApplicableDummyUsers(BatchBean batchBean);
	
	//Get User role
	public String getUserRoleById(String userId);
	
	//Set ConsumerProgramStructureId
	public String setConsumerPrograrmStructureId(String userId);
}

