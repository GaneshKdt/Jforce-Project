package com.nmims.daos;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.nmims.beans.MassUploadTrackingSRBean;;

@Component
public interface MassUploadTrackingSRDAO {
	
	public void saveSrExcelRecord(List<MassUploadTrackingSRBean> srUploadList) throws SQLException;
	public List<MassUploadTrackingSRBean> getTrackingDetailsList(MassUploadTrackingSRBean searchBean);
	public Integer deleteMassUploadTrackingBySrId(Integer srId);
	public Integer updateMassUploadSR(MassUploadTrackingSRBean massUploadTrackingSRBean);
	public MassUploadTrackingSRBean getMassUploadTrackingBySRId(Integer srId);
	public boolean isSRValid(Integer srId,List<String> SRTrackingList);
	public List<Long> getSrIdList(List<Long> srIds);
	public List<MassUploadTrackingSRBean> getListSendEmailNotification();
	public boolean isSrIdExist(Long srId);
	public Integer updateTrackingMailStatus(String status,List<Integer> srIdList);
	public Map<Long, MassUploadTrackingSRBean> getTrackingMailStatus(List<Long> srIds);
	public Map<Integer, String> getMapOfSrIdAndSapId(List<String> srIdList);
	public List<MassUploadTrackingSRBean> getSRDetailsList(List<Integer> srIdList);
	public List<MassUploadTrackingSRBean> getStudentDetailsList(List<String> sapIdList);
	
	}
