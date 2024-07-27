package com.nmims.interfaces;

import java.util.List;

import com.nmims.beans.ContentStudentPortalBean;

public interface ContentServiceInterFace
{
	 List<ContentStudentPortalBean> getContentByPssId(double reg_order,double acadContentLiveOrder,String month,String year,String programSemSubjectId,String sapid
			 ,boolean isCurrent);
	
}
