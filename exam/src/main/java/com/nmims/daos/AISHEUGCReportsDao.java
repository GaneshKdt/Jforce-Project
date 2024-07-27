package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.nmims.beans.AISHEUGCReportsBean;

@Component
public interface AISHEUGCReportsDao {

List<String> getListOfSapidForAllStudentsAppearedByExamYearMonthSem(String Year, String Month, String sem, String firstLetterOfProgram)throws Exception;
List<String> getListOfProgramByExamYearMonthSemFirstLetterofProgramForAllStudentsAppeared(String Year, String Month, String sem, String firstLetterOfProgram)throws Exception;
List<AISHEUGCReportsBean> getListOfStudentAppeared(List<String>SapidList, List<String>ProgramList,String firstLetterOfProgram )throws Exception;
List<AISHEUGCReportsBean> getListOfFemaleStudentAppeared(List<String>SapidList,List<String>ProgramList ,String firstLetterOfProgram )throws Exception;	
List<String> getListOfSapidForTotalMarksByExamYearMonthSem(String Year, String Month, String sem, String firstLetterOfProgram)throws Exception;
List<String> getListOfSapidNYForTotalMarks(String resultProcessedYear, String resultProcessedMonth,String firstLetterOfProgram)throws Exception;
List<String> getListOfSapidAndProgramForTotalMarks(List<String> SapidNYlist,List<String> Sapid, String firstLetterOfProgram )throws Exception;
List<AISHEUGCReportsBean> getListOfTotalMarks(List<String>SapidProgramList  )throws Exception;	
List<String> getListOfSapidAndProgramForFemaleTotalmarks(List<String> SapidNYlist,List<String> Sapid, String firstLetterOfProgram )throws Exception;	
List<String> getListOfSapidForAllPassByExamYearMonthSem(String Year, String Month, String sem, String firstLetterOfProgram)throws Exception;
List<String> getListOfSapidNYForAllPass(String resultProcessedYear, String resultProcessedMonth,String firstLetterOfProgram)throws Exception;
List<AISHEUGCReportsBean> getListOfStudentPass(List<String> SapidNYlist,List<String> Sapid, String firstLetterOfProgram )throws Exception;
List<AISHEUGCReportsBean> getListOfFemaleStudentPass(List<String> SapidNYlist,List<String> Sapid, String firstLetterOfProgram )throws Exception;	
ArrayList<AISHEUGCReportsBean>  getSapidAndCpsidForAllStudents()throws Exception;
ArrayList<AISHEUGCReportsBean>   getapplicableSubjectBySemAndconsumerProgramStructureId()throws Exception;
ArrayList<AISHEUGCReportsBean>getProgramIdAndconsumerProgramStructureId()throws Exception;
	
	
}