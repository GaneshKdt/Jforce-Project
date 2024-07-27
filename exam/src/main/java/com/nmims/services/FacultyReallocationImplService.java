package com.nmims.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nmims.beans.FacultyReallocationBean;
import com.nmims.daos.FacultyReallocationDao;

@Service("facultyReallocationService")
public class FacultyReallocationImplService implements FacultyReallocationService
{
	/*Variables*/
	@Autowired
	private FacultyReallocationDao facultyReallocationDao;
	
	
	/*Methods*/
	@Override	//This method are used to fetch all tha faculty list with their name and facultyId from faculty table
	public ArrayList<FacultyReallocationBean> getProjectsAllocatedToFacultyByYearAndMonthOrFacultyId(String year, String month, 
		String facultyId)throws IOException,NullPointerException,SQLException
	{
		ArrayList<FacultyReallocationBean> allocatedCount = facultyReallocationDao.getProjectsAllocatedToFacultyByYearAndMonthOrFacultyId(year, month, facultyId);
		return allocatedCount;
	} 

	@Override	//This method are used to fetch total Allocated data to each faculty with their facultyId
	public int reallocateProjectsToFaculty(FacultyReallocationBean bean)throws Exception
	{
		int reallocated = facultyReallocationDao.reallocateProjectsToFaculty(bean.getFacultyId(), bean.getToFacultyId(), 
				bean.getYear(), bean.getMonth(), bean.getSapids(), bean.getUser());
		return reallocated;
	}

	@Override	//This method are used to fetch total Not Evaluated data to each faculty with their facultyId
	public HashMap<String,FacultyReallocationBean> getProjectsNotEvaluatedByYearAndMonthOrFacultyId(String year, String month,
		String facultyId) throws IOException, NullPointerException, SQLException 
	{
		HashMap<String, FacultyReallocationBean> realocatedCount = facultyReallocationDao.getProjectsNotEvaluatedToFacultyByYearAndMonthOrFacultyId(year, month, facultyId);
		return realocatedCount;
	}
	
	@Override	//This Method is used for setting allocatedCounts and yet to evaluatedCounts in facultyList
	public ArrayList<FacultyReallocationBean> getSearchedFacultyList(HashMap<String, FacultyReallocationBean> mapRealocatedCount, 
		ArrayList<FacultyReallocationBean> allocatedCount, Map<String, FacultyReallocationBean> mapfacultyList)
	{
		if(!allocatedCount.isEmpty())
		{
			allocatedCount.forEach(bean -> {
				if(mapfacultyList.containsKey(bean.getFacultyId().trim()))
				{
					FacultyReallocationBean facultyReallocationBean = mapfacultyList.get(bean.getFacultyId());
					bean.setFirstName(facultyReallocationBean.getFirstName());
					bean.setLastName(facultyReallocationBean.getLastName());
					if(mapRealocatedCount.containsKey(bean.getFacultyId().trim()))
					{
						FacultyReallocationBean realocatedCount = mapRealocatedCount.get(bean.getFacultyId());
						bean.setYetEvaluated(realocatedCount.getYetEvaluated());
					}
					else
					{
						bean.setYetEvaluated("0");
					}
				}
			});
		}
		return allocatedCount;
	}
	
	@Override	//This method are used to fetch total Not Evaluated data to each faculty with their facultyId
	public List<FacultyReallocationBean> getStudentsByFacultyIdAndYearAndMonth(String facultyId, String year, String month)throws Exception
	{
		List<FacultyReallocationBean> studentsDetailList = new ArrayList<FacultyReallocationBean>();
		List<String> sapids = facultyReallocationDao.getSapidsByFacultyIdAndYearAndMonth(facultyId,year,month);
		if(sapids.size() > 0)
		{
			List<FacultyReallocationBean> studentsList = facultyReallocationDao.getMasterKeysDetailList(sapids);
			List<FacultyReallocationBean> allProgramDetailsList = facultyReallocationDao.getAllProgramDetails();
			Map<String, FacultyReallocationBean> allProgramDetailsMap = allProgramDetailsList.stream().collect(Collectors.toMap(bean ->  bean.getConsumerProgramStructureId()+bean.getProgramStructure(), bean -> bean,(oldEntry, newEntry) -> newEntry));
			studentsDetailList = setProgramDetailsToStudent(studentsList, allProgramDetailsMap);
		}
		return studentsDetailList;
	}
	
	public List<FacultyReallocationBean> setProgramDetailsToStudent(List<FacultyReallocationBean> studentsList, Map<String, FacultyReallocationBean> allProgramDetailsMap)
	{
		studentsList.stream().forEach(bean -> {
			if(allProgramDetailsMap.containsKey(bean.getConsumerProgramStructureId()+bean.getProgramStructure()))
			{
				FacultyReallocationBean porogramDetails = allProgramDetailsMap.get(bean.getConsumerProgramStructureId()+bean.getProgramStructure());
				bean.setProgramCode(porogramDetails.getProgramCode());
				bean.setProgramName(porogramDetails.getProgramName());
			}
		});
		return studentsList;
	}
}
