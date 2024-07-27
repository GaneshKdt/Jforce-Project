package com.nmims.repository;

import java.util.List;
import java.util.Set;

import com.nmims.dto.CustomCourseWaiverDTO;


public interface CustomCourseWaiverRepository {

	public int getStudentCurrentSem(String sapid);
	
	public List<Integer> getWaivedInPss(String sapid, int sem);

	public List<CustomCourseWaiverDTO> getSubjectCodeId(List<Integer> waivedInPssId);

	public List<CustomCourseWaiverDTO> getSubjectName(Set<Integer> subjectCodeIds);

	public List<Integer> getWaivedOffPss(String sapid);

	public int checkSapidExist(String sapid);

	public int getStudentMasterKey(String sapid);

	List<CustomCourseWaiverDTO> getSubjectCodeIdByMasterKey(int masterKey);

	public List<CustomCourseWaiverDTO> getApplicableSubject(List<Integer> subjectCodeId);

	public List<Integer> getTotalNumberOfSem(int masterKey);

	public int saveWaivedInSubject(int pssId, int sem, Long sapid, String loggedUser);

	public int saveWaivedOffSubject(int pssId, Long sapid, String loggerUser);

	public List<CustomCourseWaiverDTO> getWaivedInPss(Long sapid);

	public int deleteFromWaivedIN(long sapid, int pssId);

	public int deleteFromWaivedOff(long sapid, int pssId);

	public List<CustomCourseWaiverDTO> getAllSubjectCodeId();

	public int upsertWaivedIn(List<CustomCourseWaiverDTO> waivedIn,String loggedUser);

	public int upsertWaivedOff(List<CustomCourseWaiverDTO> waivedOff,String loggedUser);

	public int upsertInDelhivery(List<CustomCourseWaiverDTO> waivedIn, String loggedUser);

	public int deleteStudentCurrentSubject(long sapid, int pssId);

	public void upsertInStudentCourseMapping(CustomCourseWaiverDTO customCourseWaiverDTO, String loggerUser);

	public CustomCourseWaiverDTO getAcadMonthAndYear(long sapid);

	public int deleteFromStudentCourseMapping(long sapid, int pssId);

	public int deleteFromDelhivery(List<CustomCourseWaiverDTO> waivedOff);




	

	
}
