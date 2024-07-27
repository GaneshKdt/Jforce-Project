package com.nmims.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FacultyCourseBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.BaseDAO;
import com.nmims.interfaces.FacultyServiceInterface;

@Service("facultyService")
public class FacultyService implements FacultyServiceInterface
{
	@Autowired
	FacultyDAO facultyDao;
	
	private static final boolean yesOption = true;
	
	public List<FacultyCourseBean> populateFacultySubjectCodeInCourseTable() {
		
		//Get All Faculty course List
		List<FacultyCourseBean> allFacultyList = facultyDao.getallFacultyCourseList(yesOption);
		List<FacultyCourseBean> error_list = new ArrayList<FacultyCourseBean>();
		int success = 0;
		int i = 1;
		int  result = 0;
		for(FacultyCourseBean faculty : allFacultyList) {
		try {
			System.out.println("Iteration "+(i)+" / "+allFacultyList.size());
			List<String> subjectcodes = facultyDao.getSubjectCodeBySubjectName(faculty.getSubject());
		
			result = facultyDao.upsertFacultyCourse(faculty,subjectcodes);
			
			if(result > 0) {
				success++;
				facultyDao.deleteFacultySubjectBYMonthAndYear(faculty.getFacultyId(),faculty.getYear(),faculty.getMonth(),faculty.getSubject());
			}else
				error_list.add(faculty);
		}catch(Exception e) {
			e.printStackTrace();
			error_list.add(faculty);
		}
		i++;
		System.out.println("Success/Failure "+(success)+" / "+error_list.size());
		}
		return allFacultyList;
	}

	@Override
	public int deleteFacultyCourse(String facultyId,String year,String month,String subjectCode) {
		// TODO Auto-generated method stub
		return facultyDao.deleteFacultyCourseBYMonthAndYear(facultyId,year,month,subjectCode);
	}

	@Override
	public List<FacultyCourseBean> getallFacultyCourseList(boolean subjectcode) {
		// TODO Auto-generated method stub
		List<FacultyCourseBean> facultyList = facultyDao.getallFacultyCourseList(subjectcode);
		Map<String,FacultyAcadsBean> allFacultyList = facultyDao.getAllFacultyMap();
		
		StringBuilder fullName = new StringBuilder();
		for(FacultyCourseBean faculty : facultyList) {
			fullName = new StringBuilder();
			
			if(!StringUtils.isBlank(allFacultyList.get(faculty.getFacultyId().trim()).getFirstName()))
				fullName.append(allFacultyList.get(faculty.getFacultyId().trim()).getFirstName());
			if(!StringUtils.isBlank(allFacultyList.get(faculty.getFacultyId().trim()).getLastName()))
				fullName.append(" "+allFacultyList.get(faculty.getFacultyId().trim()).getLastName());
			
			faculty.setFullName(fullName.toString());
		}
		return facultyList;
	}

}
