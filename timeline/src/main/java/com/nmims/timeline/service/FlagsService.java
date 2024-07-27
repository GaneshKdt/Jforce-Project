package com.nmims.timeline.service;

import java.util.List;

import com.nmims.beans.FlagBean;

public interface FlagsService {
	FlagBean getByKey(String key);
	List<FlagBean> getByKeysCommaSeperated(String keys);
	List<FlagBean> getAll();
	String save(FlagBean flagBean);
	String deleteByKey(String key);
	
}
