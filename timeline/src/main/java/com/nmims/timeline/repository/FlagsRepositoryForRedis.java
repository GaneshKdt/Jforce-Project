package com.nmims.timeline.repository;

import java.util.List;

import com.nmims.beans.FlagBean;

public interface FlagsRepositoryForRedis {
	FlagBean getByKey(String key);
	List<FlagBean> getByKeysCommaSeperated(String keys);
	List<FlagBean> getAll();
	String save(FlagBean flagBean);
	String deleteByKey(String key);
	
}
