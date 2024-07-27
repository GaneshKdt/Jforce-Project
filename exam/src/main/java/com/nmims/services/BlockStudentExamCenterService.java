package com.nmims.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.nmims.beans.BlockStudentExamCenterBean;

public interface BlockStudentExamCenterService 
{
	public List<BlockStudentExamCenterBean> getAllExamCenter()throws Exception;
	
	public int blockStudentsCenter(ArrayList<BlockStudentExamCenterBean> studentList, 
	BlockStudentExamCenterBean searchBean,
	List<BlockStudentExamCenterBean> allCentersList)throws Exception;
	
	public ArrayList<BlockStudentExamCenterBean> getStudentsByFilters(BlockStudentExamCenterBean searchBean)throws Exception;
	
	public ArrayList<BlockStudentExamCenterBean> getUFMStudents(BlockStudentExamCenterBean searchBean)throws Exception;
	
	public ArrayList<BlockStudentExamCenterBean> getStudentsBlockedCenter(BlockStudentExamCenterBean searchBean)throws Exception;
	
	public boolean unblockStudentsCenter(BlockStudentExamCenterBean searchBean)throws Exception;
	
	public BlockStudentExamCenterBean getExcelData(MultipartFile file)throws Exception;
}
