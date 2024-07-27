
package com.nmims.interfaces;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.MassUploadTrackingSRBean;

public interface MassUploadTrackingSRInterface {
	
	MassUploadTrackingSRBean saveSrExcelRecord(HttpServletRequest request,MultipartFile file) throws Exception ;
	
	List<MassUploadTrackingSRBean> getSearchTrackingRecords(MassUploadTrackingSRBean searchBean);
	
	boolean deleteMassUploadTrackingSRBySrId(Integer srId);
	
	boolean updateMassUploadTrackingSR(HttpServletRequest request,MassUploadTrackingSRBean massUploadTrackingSRBean);
	
	MassUploadTrackingSRBean getMassUploadTrackingBySRId(Integer srId);
	
	void notifyStudentForTrackingDetails();
}
