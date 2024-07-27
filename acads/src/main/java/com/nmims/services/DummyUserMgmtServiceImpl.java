package com.nmims.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.BatchBean;
import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.DummyUserBean;
import com.nmims.daos.DummyUsersDAO;
import com.nmims.interfaces.IDummyUserMgmtService;

@Service("DummyUserService")
public class DummyUserMgmtServiceImpl implements IDummyUserMgmtService {
	@Autowired
	DummyUsersDAO dummyUserDAO;
	
	/*
	 * This method get values in list and return single string
	 * @Return String
	 * @Param  list of values
	 * @Auther */
	private String frameINClauseString(List<String> values){
		StringBuilder sb=null; 
		sb = new StringBuilder();

		for(String val:values){
			sb.append("'");
			sb.append(val);
			sb.append("',");
		}
		int commaIndex=sb.lastIndexOf(",");
		if(commaIndex!=-1)
			sb.delete(commaIndex,commaIndex+1);

		return sb.toString();
	}//frameINClauseString()
	
	/*
	 * This method get ConsumerType Name and ID
	 * @return List<ConsumerProgramStructure>
	 * @param  no parameter*/
	@Override
	public List<ConsumerProgramStructureAcads> getAllConsumerType(){
		List<ConsumerProgramStructureAcads> conPrgStrList=null;
		
		//Use DummyUserDAO and get all consumerType details
		conPrgStrList=dummyUserDAO.getConsumerTypeList();

		//return all ConsumerType details in ConsumerProgramStructure list
		return conPrgStrList;
	}//getAllConsumerType()

	@Override
	public List<ConsumerProgramStructureAcads> getProgramTypeByConsumerType(String consumerTypeId) {
		List<ConsumerProgramStructureAcads> programTypeList=null;
		
		//Use dummyUserDAO and gets programType list based on consumerTypeId 
		programTypeList=dummyUserDAO.getProgramTypeByConsumerType(consumerTypeId);
		
		//return programTypeList
		return programTypeList;
	}

	@Override
	public List<ConsumerProgramStructureAcads> getProgramByConsumerType(String consumerTypeId) {
		List<ConsumerProgramStructureAcads> appProgramsList=null;
		
		//Use dummyUserDAO and gets programs list based on consumerTypeId
		appProgramsList=dummyUserDAO.getProgramByConsumerType(consumerTypeId);
		
		//return programs list
		return appProgramsList;
	}//getProgramByConsumerType()

	@Override
	public List<ConsumerProgramStructureAcads> getProgramByProgramTypeAndConsumerType(
			ConsumerProgramStructureAcads consumerProgramStructure) {
		List<ConsumerProgramStructureAcads> appProgramsList=null;
		
		//In this method call we will get multiple programTypes as single quoted and comma separated in single string
		String programTypes = frameINClauseString(Arrays.asList(consumerProgramStructure.getProgramType().split(",")));
		
		//We will get programs list based on ProgramType and ConsumerType
		appProgramsList=dummyUserDAO.getProgramByProgramTypeAndConsumerType(consumerProgramStructure,programTypes);
		
		//Return programs list
		return appProgramsList;
	}//getProgramByProgramTypeAndConsumerType()

	@Override
	public List<ConsumerProgramStructureAcads> getProgramByMasterKey(
			ConsumerProgramStructureAcads consumerProgramStructure) {
		List<ConsumerProgramStructureAcads> appProgramsList=null;
		
		//Reading ProgramType and ProgramStructureId values from form data
		String prgmTypeValues=consumerProgramStructure.getProgramType();
		String prgmStructValues=consumerProgramStructure.getProgramStructureId();
		
		//In this method call we will get multiple programTypes and ProgramStructureIds as single quoted and comma separated in single string
		String programTypes = frameINClauseString(Arrays.asList(prgmTypeValues.split(",")));
		String prgmStructIds = frameINClauseString(Arrays.asList(prgmStructValues.split(",")));
	
		//set framed prgmTypeValues and prgmStructIds to ConsumerProgramStructure
		consumerProgramStructure.setProgramType(programTypes);
		consumerProgramStructure.setProgramStructureId(prgmStructIds);
		
		//Get Programs list based on ConsumerType, ProgramStructure and ProgramType 
		appProgramsList=dummyUserDAO.getProgramByMasterKey(consumerProgramStructure);
	
		//Setting read form data back to ConsumerProgramStructure to avoid Bad SQL Exception to another call 
		consumerProgramStructure.setProgramType(prgmTypeValues);
		consumerProgramStructure.setProgramStructureId(prgmStructValues);
		
		return appProgramsList;
	}//getProgramByProgramTypeAndConsumerTypeAndProgramStructure()

	@Override
	public List<ConsumerProgramStructureAcads> getProgramStructureByConsumerType(String consumerTypeId) {
		List<ConsumerProgramStructureAcads> appPrgStrList=null;
		
		//Get applicable program structure list based on ConsumerType
		appPrgStrList=dummyUserDAO.getProgramStructureByConsumerType(consumerTypeId);
		
		return appPrgStrList;
	}//getProgramStructureByConsumerType()

	@Override
	public List<ConsumerProgramStructureAcads> getProgramStructureByProgramTypeAndConsumerType(
			ConsumerProgramStructureAcads consumerProgramStructure) {
		List<ConsumerProgramStructureAcads> appPrgStrList=null;
		
		//In this method call we will get multiple programTypes as single quoted and comma separated in a single string
		String programTypes = frameINClauseString(Arrays.asList(consumerProgramStructure.getProgramType().split(",")));
		
		//Get applicable program structure list based on ConsumerType and ProgramTypes
		appPrgStrList=dummyUserDAO.getProgramStructureByProgramTypeAndConsumerType(consumerProgramStructure,programTypes);
		
		return appPrgStrList;
	}//getProgramStructureByProgramTypeAndConsumerType()

	@Override
	public List<ConsumerProgramStructureAcads> getBatchByConsumerType(String consumerTypeId) {
		List<ConsumerProgramStructureAcads> appBatchsList=null;
		
		//Get all applicable batches based on ConsumerType
		appBatchsList=dummyUserDAO.getBatchByConsumerType(consumerTypeId);
		
		return appBatchsList;
	}//getBatchByConsumerType()

	@Override
	public List<ConsumerProgramStructureAcads> getBatchByProgramTypeAndConsumerType(
			ConsumerProgramStructureAcads consumerProgramStructure) {
		List<ConsumerProgramStructureAcads> appBatchsList=null;
		
		//In this method call we will get multiple programTypes as single quoted and comma separated in a single string
		String programTypes = frameINClauseString(Arrays.asList(consumerProgramStructure.getProgramType().split(",")));

		//Get all applicable batches based on ConsumerType and ProgramType
		appBatchsList=dummyUserDAO.getBatchByProgramTypeAndConsumerType(consumerProgramStructure,programTypes);

		return appBatchsList;
	}//getBatchByProgramTypeAndConsumerType()

	@Override
	public List<ConsumerProgramStructureAcads> getBatchByMasterKey(
			ConsumerProgramStructureAcads consumerProgramStructure) {
		List<ConsumerProgramStructureAcads> appBatchsList=null;
		
		//Reading ProgramType and ProgramStructureId values from form data
		String prgmTypeValues=consumerProgramStructure.getProgramType();
		String prgmStructValues=consumerProgramStructure.getProgramStructureId();
		
		//In this method call we will get multiple programTypes and ProgramStructureIds as single quoted and comma separated in single string
		String programTypes = frameINClauseString(Arrays.asList(prgmTypeValues.split(",")));
		String prgmStructIds = frameINClauseString(Arrays.asList(prgmStructValues.split(",")));
		
		//set framed prgmTypeValues and prgmStructIds to ConsumerProgramStructure
		consumerProgramStructure.setProgramType(programTypes);
		consumerProgramStructure.setProgramStructureId(prgmStructIds);
		
		//Get Batches list based on ConsumerType, ProgramStructure and ProgramType
		appBatchsList=dummyUserDAO.getBatchByMasterKey(consumerProgramStructure);
		
		//Setting read form data back to ConsumerProgramStructure to avoid Bad SQL Exception to another call
		consumerProgramStructure.setProgramType(prgmTypeValues);
		consumerProgramStructure.setProgramStructureId(prgmStructValues);
		
		return appBatchsList;
	}//getBatchByProgramTypeAndConsumerTypeAndProgramStructure()

	@Override
	public List<ConsumerProgramStructureAcads> getBatchByMasterKeyAndProgram(
			ConsumerProgramStructureAcads consumerProgramStructure) {
		List<ConsumerProgramStructureAcads> appBatchsList=null;
		
		//In this method calls we will get multiple programTypes,ProgramStructureIds and ProgramIds as single quoted and comma separated in single string in respective variable
		String prgmTypeValues = frameINClauseString(Arrays.asList(consumerProgramStructure.getProgramType().split(",")));
		String prgmStructIds = frameINClauseString(Arrays.asList(consumerProgramStructure.getProgramStructureId().split(",")));
		String programIds = frameINClauseString(Arrays.asList(consumerProgramStructure.getProgramId().split(",")));
		
		//set framed prgmTypeValues,prgmStructIds and programIds to ConsumerProgramStructure 
		consumerProgramStructure.setProgramType(prgmTypeValues);
		consumerProgramStructure.setProgramStructureId(prgmStructIds);
		consumerProgramStructure.setProgramId(programIds);
		
		//Get applicable batches based on ConsumerType,ProgramType,ProgramStructure and Program
		appBatchsList=dummyUserDAO.getBatchByMasterKeyAndProgram(consumerProgramStructure);
		
		return appBatchsList;
	}//getBatchByProgramTypeAndConsumerTypeAndProgramStructureAndProgram()

	@Override
	public List<ConsumerProgramStructureAcads> getBatchByMasterKeyAndProgramAndSem(
			ConsumerProgramStructureAcads consumerProgramStructure) {
		List<ConsumerProgramStructureAcads> appBatchsList=null;
		
		//In this method calls we will get multiple programTypes,ProgramStructureIds,ProgramIds and semester values 
		//as single quoted and comma separated in single string in respective variable
		String prgmTypeValues = frameINClauseString(Arrays.asList(consumerProgramStructure.getProgramType().split(",")));
		String prgmStructIds = frameINClauseString(Arrays.asList(consumerProgramStructure.getProgramStructureId().split(",")));
		String programIds = frameINClauseString(Arrays.asList(consumerProgramStructure.getProgramId().split(",")));
		String semValues = frameINClauseString(Arrays.asList(consumerProgramStructure.getSem().split(",")));

		//set framed prgmTypeValues,prgmStructIds,programIds and semValues to ConsumerProgramStructure
		consumerProgramStructure.setProgramType(prgmTypeValues);
		consumerProgramStructure.setProgramStructureId(prgmStructIds);
		consumerProgramStructure.setProgramId(programIds);
		consumerProgramStructure.setSem(semValues);
		
		//Get applicable Batches as list based on master key, Program and semester values
		appBatchsList=dummyUserDAO.getBatchByMasterKeyAndProgramAndSem(consumerProgramStructure);
		
		return appBatchsList;
	}//getBatchByProgramTypeAndConsumerTypeAndProgramStructureAndProgramAndSem()
	
	@Override
	public List<DummyUserBean> getApplicableDummyUsers(BatchBean batchBean){
		List<DummyUserBean> dummyUsersList=null;
		
		//Get all dummy users details
		dummyUsersList=dummyUserDAO.getDummyUsers(batchBean);
		
		return dummyUsersList;
	}//getApplicableDummyUsers()

	@Override
	public String getUserRoleById(String userId) {
		String userRole=null;
		
		//Get role of a user
		userRole=dummyUserDAO.getRoleByUserId(userId);
		
		return userRole;
	}//getUserRoleById()

	@Override
	public String setConsumerPrograrmStructureId(String userId) {
		String id=null;
		
		id=dummyUserDAO.setConsumerPrograrmStructureId(userId);
		
		return id;
	}
}//class