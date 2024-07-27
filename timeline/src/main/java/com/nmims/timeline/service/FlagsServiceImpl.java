package com.nmims.timeline.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nmims.beans.FlagBean;
import com.nmims.timeline.repository.FlagsRepositoryForRedis;
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
			/*String[] keysArray = keys.split(",",-1);
			for(String key : keysArray) {
				returnList.add(getByKey(key));
			}
			*/
			
			FlagBean st = new FlagBean();
			FlagBean et = new FlagBean();

			st.setKey("startTimeToShowResultsFromCache");
			st.setValue("2022-01-28 09:00:00");
			et.setKey("endTimeToShowResultsFromCache");
			et.setValue("2022-01-29 17:00:00");
			
			returnList.add(st);
			returnList.add(et);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
