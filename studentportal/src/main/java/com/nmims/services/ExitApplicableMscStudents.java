package com.nmims.services;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.StudentDAO;
import com.nmims.interfaces.ExitSrApplicableInterface;

@Service("exitApplicableMscStudents")
public class ExitApplicableMscStudents implements ExitSrApplicableInterface {

	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Autowired
	private StudentDAO studentDAO;
	
	@Override
	public ServiceRequestStudentPortal checkIfStudentApplicableForExitProgram(StudentStudentPortalBean student) {
		
		ServiceRequestStudentPortal servicerequest = new ServiceRequestStudentPortal();	
		List<ProgramsStudentPortalBean> listOfnewProgramToBeMappedByCPSid=serviceRequestDao.getListOfProgramsbymasterKey(student.getConsumerProgramStructureId());
		
		if(!listOfnewProgramToBeMappedByCPSid.isEmpty()) {
			String latestSem = serviceRequestDao.getLatestSemBySapId(student.getSapid());
			boolean isNotclearedProgram=isProgramCleared(student.getSapid(),student.getConsumerProgramStructureId(),latestSem);          //if program has cleared than should not raise for exit Sr
	
			if(isNotclearedProgram) {		
				int semester=Integer.parseInt(latestSem);                    
				
				for(int i=semester;i>0;i--) {                                 
					String sem=String.valueOf(i);
						
					Optional<ProgramsStudentPortalBean> dataMatched=listOfnewProgramToBeMappedByCPSid.stream().filter(l->l.getNoOfSemesters().equals(sem)).findAny();
				
					if(dataMatched.isPresent()) {
						boolean isPass=isPassBySem(student,sem);            
						if(isPass) {     
							servicerequest.setIsCertificate(true);
							servicerequest.setConsumerProgramStructureId(dataMatched.get().getNewConsumerProgramStructureId());
							return servicerequest;
						}else {                                               
							continue;
						}				
					}
				}
			}
		}
		servicerequest.setIsCertificate(false);
		servicerequest.setConsumerProgramStructureId("");
		return servicerequest;
	}

	//Purpose :- To Check Pass on Given Semester
   public boolean isPassBySem(StudentStudentPortalBean student,String sem) {
		  List<String> totalSubjectbysem = studentDAO.getAllPSSIdByCPSIdtillSem(student.getConsumerProgramStructureId(),sem);
	  	  int countOfPassedSubjectforparticularSem = studentDAO.getCountOfPassedSubjectforparticularSemForMsc(student.getSapid(),totalSubjectbysem);
	  	  
	  	  if(student.getConsumerProgramStructureId().equals("131")&& sem.equals("4")) {
	  		 return (countOfPassedSubjectforparticularSem==13)? true: false;	
	  	  }else {
	  		 return (countOfPassedSubjectforparticularSem==totalSubjectbysem.size())? true: false;	
	  	   }
	  	  
	   }	
   
   //Purpose :- To Check Program has been cleared or not
   public boolean isProgramCleared(String sapid,String CpsID,String latestSem) {
		  int noOfSubjectToBeClear=serviceRequestDao.noOfSubjectsToClear(sapid);
		
		  List<String> totalSubjectbysem = studentDAO.getAllPSSIdByCPSIdtillSem(CpsID,latestSem);
	  	  int countOfPassedSubjectforparticularSem = studentDAO.getCountOfPassedSubjectforparticularSemForMsc(sapid,totalSubjectbysem);	  	  				  	
	  	 
	  	  return (noOfSubjectToBeClear==countOfPassedSubjectforparticularSem)? false: true;	
   }  
}