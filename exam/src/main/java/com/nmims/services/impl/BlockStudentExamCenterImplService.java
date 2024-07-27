package com.nmims.services.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.nmims.beans.BlockStudentExamCenterBean;
import com.nmims.daos.BlockStudentExamCenterDAO;
import com.nmims.helpers.ExcelHelper;
import com.nmims.services.BlockStudentExamCenterService;

@Service("blockStudentExamCenterService")
public class BlockStudentExamCenterImplService implements BlockStudentExamCenterService
{
	/**Variables
	 *----------*/
	@Autowired
	private BlockStudentExamCenterDAO dao;

	
	
	
	/**Methods
	 *----------*/
	
	/* Common Method Logic-[START]
	 * ----------------------------------*/
	
	//To Fetch All Exam Center Detail Except Null Exam Center Name Detail
	@Override
	public List<BlockStudentExamCenterBean> getAllExamCenter() 
	{
		ArrayList<String> examcenterIdList = dao.getAllExamCenterSlotMappingId();
		return dao.getAllExamCenterByCenterId(examcenterIdList);
	}
	
	/* Common Method Logic-[END]
	 * ----------------------------------*/
	
	
/********************************************************************************************************************************************************************************/
	
	
	
	/* Search Students Only Logic-[START]
	 * ----------------------------------*/
	//To Fetch Normal Students List
	@Override
	public ArrayList<BlockStudentExamCenterBean> getStudentsByFilters(
			BlockStudentExamCenterBean searchBean)throws Exception 
	{
		ArrayList<BlockStudentExamCenterBean> studentList = new ArrayList<>();
		
		//Converting From Id to Name
		searchBean.setProgramStructureName(dao.getProgramStructureNameByProgramStructureId(searchBean.getProgramStructureId()));
		searchBean.setProgramName(dao.getProgramNameByProgramId(searchBean.getProgramId()));
		searchBean.setConsumerTypeName(dao.getConsumerTypeNameByConsumerTypeId(searchBean.getConsumerTypeId()));
		searchBean.setMasterKeyId(dao.getMasterKeyByConsumerTypeAndProgramStructureAndProgram(searchBean.getConsumerTypeId(), searchBean.getProgramStructureId(), searchBean.getProgramId()));
		
		//Searching students from exam.students and exam.registrations
		studentList = dao.getStudentsByMasterKey(searchBean.getMasterKeyId());
		
		return studentList;
	}
	/* Search Students Only Logic-[END]
	 * --------------------------------*/
	
	
	
/********************************************************************************************************************************************************************************/		

	
	
	/* Student UFM Students Logic-[Start]
	 * -----------------------------------------------------------------------------------*/
	
	//To Fetch Students Who Have Marked UFM In Their Last Two Exam Cycle
	@Override
	public ArrayList<BlockStudentExamCenterBean> getUFMStudents(BlockStudentExamCenterBean searchBean) throws Exception
	{
		//Fetch All NV Students Sapid From Marks Table
		ArrayList<String> nvSapidList = dao.getNVStudentsSapid();
		//Fetch All Marks Details(sapid,year,month,examorder, and writenscore) Of Above Sapids
		ArrayList<BlockStudentExamCenterBean> marksDetailList = dao.getMarksDetailOfSapid(nvSapidList);
		//Converting All Marks Details List Into HashMap and Every Sapid key Have a List Of Marks Details
		HashMap<String, LinkedList<BlockStudentExamCenterBean>> generatedHashMap = generateHashMapofMarksDetailsOfStudents(marksDetailList);
		//Fetching Students Who Are Marked NV In Their Last Exam Cycle
		ArrayList<BlockStudentExamCenterBean> studentsMarkedUFMLastTwoCycle = sortHashMapByExamorder(generatedHashMap);
		
		return studentsMarkedUFMLastTwoCycle;
	}
	//To Generate HashMap Of All Marks Details of Students
	public HashMap<String,LinkedList<BlockStudentExamCenterBean>> generateHashMapofMarksDetailsOfStudents(
			ArrayList<BlockStudentExamCenterBean> allStudentMarksDetailList)
	{
		//This is the map which hold the sapid as key and list of their marks details as value
		HashMap<String,LinkedList<BlockStudentExamCenterBean>> mapallStudentMarksDetailList = new HashMap<>();		
	
		//Iterate all marks details list of students
		allStudentMarksDetailList.forEach(bean -> {
			//If sapid Not Found in key of HashMap then put new key and value
			if(!mapallStudentMarksDetailList.containsKey(bean.getSapid()))
			{
				//This is temporary list of bean
				LinkedList<BlockStudentExamCenterBean> tempList = new LinkedList<>();
				//Adding new Marks Details in tempbean list
				tempList.add(bean);
				//Adding new Key and Value
				mapallStudentMarksDetailList.put(bean.getSapid(), tempList);
			}
			//If sapid Found in key of HashMap then get existing value of key and overwrite new key and appending new value on old value
			else
			{
				//Getting Value By Key in List
				LinkedList<BlockStudentExamCenterBean> oldMapKeyValue = mapallStudentMarksDetailList.get(bean.getSapid());
				//Appending new marks detail in this old list of marks detail
				oldMapKeyValue.add(bean);
				//Overwrite old key values by new and appending updated list of marks detail in their value
				mapallStudentMarksDetailList.put(bean.getSapid(), oldMapKeyValue);
			}
		});
		return mapallStudentMarksDetailList;
	}
	//To Sort The Students Marks Detail By Their ExamOrder In HashMap Values And Return List of Student Who Have Marked NV In Their Both Or Any One Of Last Two Exam Cycle
	public ArrayList<BlockStudentExamCenterBean> sortHashMapByExamorder(HashMap<String,LinkedList<BlockStudentExamCenterBean>> mapAllStudentsMarksDetails)
	{
		final String UFM_MARKED = "NV";
		//This is list of string and it hold the sapid of students who have marked NV in their last two exam cycle
		ArrayList<BlockStudentExamCenterBean> nvInLastTwoExamCycleStudents = new ArrayList<>();
		
		//Iterating HashMap key
		mapAllStudentsMarksDetails.keySet().forEach(keys -> {
			//Getting the value of key in list of marks detail
			LinkedList<BlockStudentExamCenterBean> studentMarksDetailList = mapAllStudentsMarksDetails.get(keys);
			
			//Sorting the examorder of student marks detail in descending order and returning the list of sorted marks detail
			List<BlockStudentExamCenterBean> sortedStudentsExamorderList = studentMarksDetailList.stream()
					  .sorted(Comparator.comparing(BlockStudentExamCenterBean::getExamorder).reversed())
					  .collect(Collectors.toList());
			
			//This is the HashMap for checking the examorder have changed or not during iterating this list of sorted marks detail
			ArrayList<String> examOrderChangeList = new ArrayList<>();
			//this is the counter for count the number of times examorder changed
			int counter = 0;
			//Iterating the Sorted Student Marks Detail List
			for(BlockStudentExamCenterBean bean : sortedStudentsExamorderList)
			{
				//Checking that map have any key or not
				if(!examOrderChangeList.contains(bean.getExamorder()))
				{
					examOrderChangeList.add(bean.getExamorder());
					counter++;
				}
				//If counter size less than or equal to 2 then check only
				if(counter < 3)
				{
					//Check if students are found NV in their last two examorder then add them in list and break the loop
					if(UFM_MARKED.equalsIgnoreCase(bean.getWritenscore()))
					{
						nvInLastTwoExamCycleStudents.add(bean);
						break;
					}
				}
				//else break the loop and terminate to check the more than 2 examorder and try another key marks detail list
				else
				{
					break;
				}
			}
		});
		
		return nvInLastTwoExamCycleStudents;
	}
	
	/* Student Who Are Marked NV in their Last Two Exam Cycle In Any Subject Logic-[END]
	 * ---------------------------------------------------------------------------------*/
	
	
/********************************************************************************************************************************************************************************/		
	
	
	/* Search Students By Excel Upload Logic-[START]
	 * ---------------------------------------------*/
	
	//To Read Sapid From Excel And Convert It Into List of Sapid To Reflect On UI
	@Override
	public BlockStudentExamCenterBean getExcelData(MultipartFile file)throws Exception 
	{
		//To hold students sapid list and error also if any
		BlockStudentExamCenterBean studentBean = new BlockStudentExamCenterBean();
    	try 
    	{
    		//ExcelHelper Object declaration
        	ExcelHelper excelHelper = new ExcelHelper();
        	//get All sapid list return in bean format from excel format
        	BlockStudentExamCenterBean sapidData =  excelHelper.readCenterNotAllowedExcel(file);
        	//Get all Sapid List only
        	List<BlockStudentExamCenterBean> sapidList = sapidData.getStudentList();
        	//Get all error list only
    		List<BlockStudentExamCenterBean> errorList = sapidData.getErrorList();
    		
    		//Check if any error exist
    		if(errorList.size() > 0)
    		{
    			//return the bean in which error message, status and row position are set
    			studentBean = getExcelErrorMssg(errorList);
    		}
    	
    		//If excel file have blank sapid data list
    		if(!(sapidList.size() > 0) && !(errorList.size() > 0))
				throw new Exception("Excel file cannot be blank, please insert data and try again");
    		
    		if(sapidList.size()>0)
    		{
    			
    		}
    		//Set list of sapid or bean in self bean and it also have error status
    		studentBean.setStudentList(sapidList);
    	}
    	catch (IllegalArgumentException ie) 
    	{
			throw new IllegalArgumentException(ie.getMessage());
		}
    	catch (Exception e) 
    	{
			throw new IllegalArgumentException(e.getMessage());
		}
    	
		return studentBean;
	}
	//To Check If Any Error Found In File(Like Extension miss match or Sapid are not numeric)
	private BlockStudentExamCenterBean getExcelErrorMssg(List<BlockStudentExamCenterBean> errorList) 
	{
		//Created a n Bean to handle error status
		BlockStudentExamCenterBean errorBean = new BlockStudentExamCenterBean();
		//Created String to get all list of error from excel in single String variable
    	String errorMssg = "";
    	//Start apending in above error strings
    	errorMssg += errorList.stream().filter(fl -> fl.isErrorRecord()).map(bean -> bean.getErrorMessage()).collect(Collectors.joining(",<br>"));
    	//Set above error string in errorMessage variable of bean
    	errorBean.setErrorMessage(errorMssg);
    	//Set error true
    	errorBean.setErrorRecord(true);
    	
    	return errorBean;
    }
	
	/* Search Students By Excel Upload Logic-[END]
	 * ---------------------------------------------*/
	
	
	
/********************************************************************************************************************************************************************************/		
	
	
	
	/* Block Center Logic-[START]
	 * --------------------------*/	

	//To Block The Selected Centers For list of Sapids
	@Override
	public int blockStudentsCenter(ArrayList<BlockStudentExamCenterBean> studentList, 
	BlockStudentExamCenterBean searchBean, 
	List<BlockStudentExamCenterBean> allCentersList)throws Exception 
	{
		HashMap<String,String> centersIdToBeBlocked = new HashMap<>();
		//Validate that center have allowed or not allowed and get list of CenterId that should be block
		centersIdToBeBlocked = validateAllowedOrNotAndGetBlockCenterIdList(searchBean.getAllowed(),allCentersList,searchBean.getCenterIdList());

		//Set the list of block CenterId in every Sapid and every Sapid will repeated according to number of CenterId in list
		ArrayList<BlockStudentExamCenterBean> studentsListWithCentersForBlock = assigningCentersToStudentsForBlock(studentList,centersIdToBeBlocked,searchBean.getYear(),searchBean.getMonth());

		//Send list of student in Dao to blocked them from CenterId list
		int count = dao.blockCentersInBulk(studentsListWithCentersForBlock);
		
		return count;
	}
	//This Method Exclude or Include Centers From Center List
	public HashMap<String,String> validateAllowedOrNotAndGetBlockCenterIdList(String allowedStatus, List<BlockStudentExamCenterBean> allCentersList, ArrayList<String> centersIdToBeBlocked)
	{
		//This HashMap hold the centers detail which have to be block for students, key store centerId and value store centerName
		HashMap<String,String> centersList = new HashMap<>();
		//If Allowed Centers
		if(allowedStatus.trim().equalsIgnoreCase("true"))
		{
			//To check center matched or not
			boolean centerFound = false;
			//Iterating All Centers List
			for(BlockStudentExamCenterBean center : allCentersList)
			{
				centerFound = false;
				//Iterating admin selected centers list
				for(String blockId : centersIdToBeBlocked)
				{
					//If center found then true boolean and break the loop
					if(center.getCenterId().trim().equalsIgnoreCase(blockId))
					{
						centerFound = true;
						break;
					}
				}
				//If centerfound return false means center not found in admin selected list of centers then add that centerDetails in HashMap
				if(!centerFound)
				{
					centersList.put(center.getCenterId(),center.getCenterName());
				}
			}
		}
		//If Not Allowed Centers
		else
		{
			//Iterating All centers list
			allCentersList.forEach(center -> {
				//Iterating admin selected center list
				centersIdToBeBlocked.stream().forEach(blockId -> {
					//If admin selected center matched from all centers then add details of center in HashMap
					if(blockId.equalsIgnoreCase(center.getCenterId()))
					{
						centersList.put(center.getCenterId(),center.getCenterName());
					}
				});
			});
		}
		return centersList;
	}
	//Assigning list of centers to every applicable sapid for block and sapid divided according to number of center in list
	public ArrayList<BlockStudentExamCenterBean> assigningCentersToStudentsForBlock(
			ArrayList<BlockStudentExamCenterBean> studentList, 
			HashMap<String,String> centersIdToBeBlocked,
			String year, String month) 
	{
		//This list hold the students detail with their centers detail and every sapid will repeated according to number of centers assigned
		ArrayList<BlockStudentExamCenterBean> studentsListWithCentersForBlock = new ArrayList<>();
		
		//Iterating students list
		studentList.forEach(bean -> {
			//Iterating centers detail list
			centersIdToBeBlocked.forEach((centerId, centerName) -> {
				//Creating tempbean for assigning values
				BlockStudentExamCenterBean tempBean = new BlockStudentExamCenterBean();
				tempBean.setCenterId(centerId);
				tempBean.setCenterName(centerName);
				tempBean.setSapid(bean.getSapid());
				tempBean.setYear(year);
				tempBean.setMonth(month);
				studentsListWithCentersForBlock.add(tempBean);
	        });
		});
		return studentsListWithCentersForBlock;
	}
	/* Block Center Logic-[END]
	 * ------------------------*/
	
	
/********************************************************************************************************************************************************************************/			
	
	
	/* Search Blocked Center Students Logic-[STRAT]
	 * --------------------------------------------*/
	
	//To Fetch Blocked Center Students By Year and Month
	@Override
	public ArrayList<BlockStudentExamCenterBean> getStudentsBlockedCenter(
			BlockStudentExamCenterBean searchBean)throws Exception
	{
		ArrayList<BlockStudentExamCenterBean> blockedCenterStudentsList = dao.getBlockedCenterStudents(searchBean);
		ArrayList<BlockStudentExamCenterBean> allExamCentersList =(ArrayList<BlockStudentExamCenterBean>) getAllExamCenter();
		ArrayList<BlockStudentExamCenterBean> studentsWithCenterNameByCenterId = getStudentsWithCenterNameByCenterId(blockedCenterStudentsList,allExamCentersList);
		return studentsWithCenterNameByCenterId;
	}
	//To Set Center Name Of Students By Center Id
	public ArrayList<BlockStudentExamCenterBean> getStudentsWithCenterNameByCenterId(
			ArrayList<BlockStudentExamCenterBean> studentsList, 
			ArrayList<BlockStudentExamCenterBean> allCenterList)
	{
		studentsList.forEach(bean -> {
			allCenterList.forEach(centers -> {
				if(bean.getCenterId().equalsIgnoreCase(centers.getCenterId()))
				{
					bean.setCenterName(centers.getCenterName());
				}
			});
		});
		return studentsList;
	}
	
	/* Search Blocked Center Students Logic-[END]
	 * ------------------------------------------*/
	
	
	
/********************************************************************************************************************************************************************************/			
	
	
	/* Unblock Students Center Logic-[STRAT]
	 * -------------------------------------*/
	
	//To Unblock The Center For Sapid By Sapid, Year, Month, And CenterId
	@Override
	public boolean unblockStudentsCenter(BlockStudentExamCenterBean searchBean)throws Exception 
	{
		int count = dao.unblockCenterOfStudent(searchBean);
		if(count > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/* Unblock Students Center Logic-[END]
	 * -----------------------------------*/
}
