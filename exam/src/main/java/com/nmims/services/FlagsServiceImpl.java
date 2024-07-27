package com.nmims.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nmims.beans.FlagBean;
import com.nmims.repository.FlagsRepositoryForRedis;
@Service
public class FlagsServiceImpl implements FlagsService {
	
	private FlagsRepositoryForRedis FlagsRepositoryForRedis;
	
	public FlagsServiceImpl(FlagsRepositoryForRedis FlagsRepositoryForRedis) {
		this.FlagsRepositoryForRedis =FlagsRepositoryForRedis;
	}
	
	@Override
	public FlagBean getByKey(String key) {
		FlagBean bean = FlagsRepositoryForRedis.getByKey(key);
		if(bean != null) {

			return bean;	
		}else {
			FlagBean returnBean = new FlagBean();
			returnBean.setKey(key);
			return returnBean;
		}
	}

	@Override
	public List<FlagBean> getByKeysCommaSeperated(String keys) {
		List<FlagBean> returnList = new ArrayList<>();
		
		try {
			String[] keysArray = keys.split(",",-1);
			for(String key : keysArray) {
				returnList.add(getByKey(key));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
		
		return returnList;
	}

	@Override
	public List<FlagBean> getAll() {
		return FlagsRepositoryForRedis.getAll();
	}

	@Override
	public String save(FlagBean flagBean) {
		return FlagsRepositoryForRedis.save(flagBean);
	}

	@Override
	public String deleteByKey(String key) {
		return FlagsRepositoryForRedis.deleteByKey(key);
	}

}
