package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.CurrencyMappingBean;


public interface CurrencyMDMServiceInterface {
	
	public ArrayList<CurrencyMappingBean> getAllCurrencyValue(HashMap<Integer, CurrencyMappingBean> masterkeyProgramMapping, HashMap<Integer, String> feeTypeList, Map<Integer, String> currencyMap);
	public Map<Integer,String> getCurrency();
	public ArrayList<CurrencyMappingBean> getMasterKey(String consumerType,ArrayList<String> programId,ArrayList<String> programStructureID, CurrencyMappingBean currency);
	public HashMap<Integer,String> getFeeType();
	public ArrayList<CurrencyMappingBean> saveCurrencyDetails(ArrayList<CurrencyMappingBean> currencyList) ;
	public HashMap<String, String> updateCurrencyDetails(CurrencyMappingBean bean);
	public ArrayList<ConsumerProgramStructureExam> getConsumerTypeList();
//	public int saveUniqueCurrencyValue(String currencyName);
	public HashMap<Integer,CurrencyMappingBean> getMapProgramsById();


}
