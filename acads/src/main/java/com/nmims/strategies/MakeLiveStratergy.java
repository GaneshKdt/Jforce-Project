package com.nmims.strategies;

import java.util.HashMap;

import com.nmims.beans.ContentAcadsBean;

public interface MakeLiveStratergy {
	
	public abstract HashMap<String,String> makeLiveContent(ContentAcadsBean searchBean);

}
