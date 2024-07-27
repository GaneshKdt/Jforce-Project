package com.nmims.interfaces;



import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.AnnouncementStudentPortalBean;
import com.nmims.beans.AnnouncementMasterBean;
import com.nmims.beans.PageStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;

public interface AnnouncementServiceInterface
{
	public List<AnnouncementMasterBean> getConsumerProgramStructureData();
	
	public String uploadAnnouncementFile(MultipartFile file);
	
	public int insertAnnouncement(List<String> consumerProgramIdsList ,AnnouncementStudentPortalBean bean)throws Exception;
	
	
	public void updateAnnouncement(List<String> consumerProgramIdsList,AnnouncementStudentPortalBean bean)throws Exception;
	
	public int deleteAnnouncement(String id) throws Exception;
	
	public PageStudentPortal<AnnouncementStudentPortalBean> getAllAnnouncements(int pageNo, int pageSize);
	
	public PageStudentPortal<AnnouncementStudentPortalBean> getAllAnnouncementByUserId(String userId,int pageNo,int pageSize);
	
	//public Page<AnnouncementBean> getAllAnnouncementsOfStudentPage(int pageNo, int pageSize);
	
	//public Page<AnnouncementBean> getAllAnnouncementsPage(int pageNo, int pageSize);
	
	public AnnouncementStudentPortalBean AnnouncementDetailsfindById(String id);
	
	public ArrayList<AnnouncementMasterBean> getCommonAnnouncementProgramsList(AnnouncementStudentPortalBean announcementbean);
	
	//public List<AnnouncementMasterBean> getProgramListByAnnouncement(AnnouncementBean announcementbean);
	
	public AnnouncementStudentPortalBean editAnnouncementProgram(String id,String masterKey);
	
	public int addAnotherAnnouncement(AnnouncementStudentPortalBean bean , String masterId,String oldAnnouncementId)throws Exception;
	
	public List<AnnouncementStudentPortalBean>getAllActiveAnnouncements(String program,String consumerProgramStructureId);
	
	public int deleteAnnouncementProgram(String masterKey,String announcementId) throws Exception;
	
	public AnnouncementStudentPortalBean findById(String announcementId);
	
	public abstract void updateAnnouncementInHistory(List<String> consumerProgramIdsList,AnnouncementStudentPortalBean bean)throws Exception;
	
	List<String> getMasterKeyByAnnouncementId(String announcementId);
	
	List<AnnouncementStudentPortalBean> getAllStudentAnnouncements(String userId);
}