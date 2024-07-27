package com.nmims.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.CurrencyMappingBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.CurrencyDAO;
import com.nmims.daos.DashboardDAO;
import com.nmims.services.CurrencyMDMServiceInterface;


@Service
public class CurrencyMDMService implements CurrencyMDMServiceInterface {

	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	DashboardDAO dashboardDao;
	
	@Autowired
	CurrencyDAO currencyDAO;
	
	@Autowired
	AssignmentsDAO asignmentsDAO;
	
	@Override
	public ArrayList<CurrencyMappingBean> getAllCurrencyValue(HashMap<Integer, CurrencyMappingBean> masterkeyProgramMapping,
			HashMap<Integer, String> feeTypeList, Map<Integer, String> currencyMap) {
		ArrayList<CurrencyMappingBean> currencyDetailsList= currencyDAO.getAllCurrencyValue();
		for(int i=0;i<currencyDetailsList.size();i++) {
			int currencyValue=currencyDetailsList.get(i).getCurrencyId();
			int feeValue=currencyDetailsList.get(i).getFeeId();
			int masterkey=currencyDetailsList.get(i).getConsumerProgramStructureId();
			
			CurrencyMappingBean bean=masterkeyProgramMapping.get(masterkey);
			
			currencyDetailsList.get(i).setConsumerType(bean.getConsumerType());
			currencyDetailsList.get(i).setProgram(bean.getProgram());
			currencyDetailsList.get(i).setProgramStructure(bean.getProgramStructure());
		
			if(currencyMap.containsKey(currencyValue)) {
				currencyDetailsList.get(i).setCurrencyName(currencyMap.get(currencyValue));
			}
			if(feeTypeList.containsKey(feeValue)) {
				currencyDetailsList.get(i).setFeeName(feeTypeList.get(feeValue));
			}
		}
		return currencyDetailsList;
	}

	@Override
	public Map<Integer, String> getCurrency() {
		return currencyDAO.getCurrency();
	}

	@Override
	public ArrayList<CurrencyMappingBean> getMasterKey(String consumerType, ArrayList<String> programId,
			ArrayList<String> programStructureID,CurrencyMappingBean currency) {
		ArrayList<CurrencyMappingBean> currencyList=new ArrayList<CurrencyMappingBean>();
		try {
			List<Integer> masterKey= currencyDAO.getMasterKey(consumerType,programId,programStructureID);
			
			for(Integer master:masterKey) {
				CurrencyMappingBean currencyMappingBean= new CurrencyMappingBean();
				currencyMappingBean.setConsumerType(currency.getConsumerType());
				currencyMappingBean.setPrice(currency.getPrice());
				currencyMappingBean.setCurrencyId(currency.getCurrencyId());
				currencyMappingBean.setFeeId(currency.getFeeId());
				currencyMappingBean.setConsumerProgramStructureId(master);
				currencyList.add(currencyMappingBean);
				}
		}catch(Exception e) {
			return currencyList;	
		}
		return currencyList;
	}

	@Override
	public HashMap<Integer, String> getFeeType() {
		return currencyDAO.getFeeType();
	}

	@Override
	public ArrayList<CurrencyMappingBean> saveCurrencyDetails(ArrayList<CurrencyMappingBean> currencyList) {
		ArrayList<CurrencyMappingBean>currencyDetailsList= currencyDAO.getAllCurrencyValue();
		ArrayList<CurrencyMappingBean> dublicateList=new ArrayList<CurrencyMappingBean>();
		try {
		boolean flag=true;
		
		for(CurrencyMappingBean curFirstList:currencyList) {
			flag=false;
			for(CurrencyMappingBean curSecondList:currencyDetailsList) {
				if(curFirstList.getFeeId()==curSecondList.getFeeId() && curFirstList.getCurrencyId()==curSecondList.getCurrencyId() && curFirstList.getConsumerProgramStructureId()==curSecondList.getConsumerProgramStructureId()) {
					flag=true;
				}			
			}
			if(flag) {
			dublicateList.add(curFirstList);
			}
		}
	
		for(CurrencyMappingBean dublicate:dublicateList) {
		currencyList.remove(dublicate);
		}
		currencyDAO.saveCurrencyDetails(currencyList);
		}catch(Exception e) {
			return dublicateList;	
		}
		return dublicateList;
		 
	}

	@Override
	public HashMap<String, String> updateCurrencyDetails(CurrencyMappingBean bean) {
		// TODO Auto-generated method stub
		return currencyDAO.updateCurrencyDetails(bean);
	}

	@Override
	public ArrayList<ConsumerProgramStructureExam> getConsumerTypeList() {
		// TODO Auto-generated method stub
		return asignmentsDAO.getConsumerTypeList();
	}
//
//	@Override
//	public String saveUniqueCurrencyValue(String currencyName) {
//		Map<Integer,String> currencyMap=currencyDAO.getCurrency();
//		if(currencyMap.containsValue(currencyName)) {
//			return currencyName;
//		}else {
//		 currencyDAO.saveUniqueCurrencyValue(currencyName);
//		 return "currencySaved";
//		}
//	}
//
	@Override
	public HashMap<Integer,CurrencyMappingBean> getMapProgramsById() {
		return currencyDAO.getMapProgramsById();
		
	}



}
