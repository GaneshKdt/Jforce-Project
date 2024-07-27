package com.nmims.strategies;
import com.nmims.beans.*;

public interface searchContent {
	public PageAcads<ContentAcadsBean> searchContent(int pageNo,ContentAcadsBean bean,String searchType);

}
