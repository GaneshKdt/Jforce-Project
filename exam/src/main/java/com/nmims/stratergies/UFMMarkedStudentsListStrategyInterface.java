package com.nmims.stratergies;

import com.nmims.beans.Page;
import com.nmims.beans.UFMNoticeBean;

public interface UFMMarkedStudentsListStrategyInterface {

	public Page<UFMNoticeBean> getListOfStudentsMarked(UFMNoticeBean inputBean, int pageNo, int pageSize) throws Exception;
}
