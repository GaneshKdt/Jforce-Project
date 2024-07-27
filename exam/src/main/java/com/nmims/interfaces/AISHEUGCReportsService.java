package com.nmims.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.nmims.beans.AISHEUGCExcelReportBean;
import com.nmims.beans.AISHEUGCReportsBean;

@Service
public interface AISHEUGCReportsService {
	
	
	
	public ArrayList<AISHEUGCReportsBean> getListOfAppreadStudentByFirstLetterOfPrograms(String enrollmentYear , String enrollmentMonth , String sem , String firstLetterOfProgram)throws Exception;
	public ArrayList<AISHEUGCReportsBean> getListOfFemalesAppearedByFirstLetterOfPrograms(String enrollmentYear , String enrollmentMonth , String sem ,String firstLetterOfProgram)throws Exception;
	public ArrayList<AISHEUGCReportsBean> getListOfStudentPassesByFirstLetterOfPrograms(String enrollmentYear , String enrollmentMonth , String sem , String firstLetterOfProgramList)throws Exception;
	public ArrayList<AISHEUGCReportsBean> getListOfFemalePassesByFirstLetterOfProgram(String enrollmentYear , String enrollmentMonth , String sem , String firstLetterOfProgramList)throws Exception;
	public HashMap<String, Integer> getMapOfStudentsTotalMarksByFirstLetterOfProgram (String enrollmentYear , String enrollmentMonth , String sem, String firstLetterOfProgramList )throws Exception;
	public HashMap<String, Integer> createhashmapFromListOfNoOfStudentAbove60ByFirstLetterOfProgram(ArrayList<AISHEUGCReportsBean> listOfNoOfStudentAbove60ByFirstLetterOfProgram)throws Exception;
	public HashMap<String, Integer> getMapOfFemaleStudentsTotalMarksByFirstLetterOfProgram (String enrollmentYear , String enrollmentMonth , String sem,  String firstLetterOfProgramList)throws Exception;
	public HashMap<String, Integer> createhashmapFromListOfNoOfFemaleStudentAbove60ByFirstLetterOfProgram(ArrayList<AISHEUGCReportsBean> listOfNoOfFeamleStudentAbove60ByFirstLetterOfProgram)throws Exception;
	public ArrayList<AISHEUGCReportsBean> getapplicableSubjectByProgramAndMasterKey()throws Exception;
	public HashMap<String, Integer> getMapFromApplicableSubjectByProgramAndMasterKey(ArrayList<AISHEUGCReportsBean> listOfapplicableSubject)throws Exception;
	public   HashMap<String, String> studentIdByconsumerProgramStructureId()throws Exception;
	public ArrayList<AISHEUGCExcelReportBean> getAllListOfProgram(String enrollmentYear, String enrollmentMonth,String sem)throws Exception;
	public ArrayList<AISHEUGCExcelReportBean> getExcelListByFirstLetterOfProgram(String enrollmentYear,String enrollmentMonth, String sem,String firstLetterOfProgram)throws Exception;
	public ArrayList<AISHEUGCExcelReportBean> createDataForAllStudentInAllProgram(ArrayList<AISHEUGCReportsBean> allListOfProgram)throws Exception;
	
}