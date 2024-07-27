package com.nmims.services;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.PassFailBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.StudentDAO;
import com.nmims.interfaces.ExitSrApplicableInterface;

@Service("exitApplicableForRetail")
public class ExitApplicableForRetail implements ExitSrApplicableInterface {
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Autowired
	private StudentDAO studentDAO;
	
	@Override
	public ServiceRequestStudentPortal checkIfStudentApplicableForExitProgram(StudentStudentPortalBean student) {
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
			List<ProgramsStudentPortalBean> listOfnewProgramToBeMappedByCPSid = serviceRequestDao.getListOfProgramsbymasterKey(student.getConsumerProgramStructureId());			
			//If Entry In MDM does Not Exist for Students current MasterKey, then Not applicable for raise SR.
			if(listOfnewProgramToBeMappedByCPSid.size() > 0) {
				
				ArrayList<PassFailBean> listOfSemWithIsPass = new  ArrayList<PassFailBean>();
				
				if(student.getIsLateral().equalsIgnoreCase("Y")) {
					listOfSemWithIsPass = studentDAO.getListOfSemWithIsPassLateral(student);
				}else {
					listOfSemWithIsPass = studentDAO.getListOfSemWithIsPass(student.getSapid());
				}
				
				//Check Entry Exist In passFail Table,If Not then can't raise. 
				if(listOfSemWithIsPass.size() == 0) {
					sr.setIsCertificate(false);
					return sr;
				}
				
				String failSemester = null;
				String passSemester = null;
				int noOfSubjectCleared=0;
				
				for(PassFailBean bean:listOfSemWithIsPass) {	
					if(bean.getIsPass().equalsIgnoreCase("N")) {
						failSemester = bean.getSem();
						break;
					}else {
						passSemester = bean.getSem();
						noOfSubjectCleared++;
					}
				}
				
				int noOfSubjectToBeClear=serviceRequestDao.noOfSubjectsToClear(student.getSapid());
				
				//If Student has Passed All subject of all Semester of Program then it should be raise for final certificate not for exit program
				if(noOfSubjectToBeClear==noOfSubjectCleared) {
					sr.setIsCertificate(false);
					sr.setError(" program has cleared.");
					return sr;
				}
				//Student failed in any Semester
				//@Param fsem=If Student has failed in any semester then , it's latest previous semester consider as pass semester
				if(!StringUtils.isEmpty(failSemester)) {
					//If Students Failed In first semester then not applicable for Exit.		
					if(failSemester.equals("1")) {
						sr.setIsCertificate(false);
						sr.setConsumerProgramStructureId("");
						sr.setError(" failed in first semester.");
						return sr;
					}else {	
						String latestPassSem = String.valueOf((Integer.parseInt(failSemester)) - 1);
						String newConsumerProgramStructureId = newProgramToBeMapped(listOfnewProgramToBeMappedByCPSid,latestPassSem);
							if(!newConsumerProgramStructureId.isEmpty()) {
						    sr.setIsCertificate(true);
							sr.setConsumerProgramStructureId(newConsumerProgramStructureId);
							return sr;
							}
				     }//IF Student have passed In all Subject Without any fail
				}else if(Integer.parseInt(passSemester) > 0){ 		
					String newConsumerProgramStructureId= newProgramToBeMapped(listOfnewProgramToBeMappedByCPSid,passSemester);
						if(!newConsumerProgramStructureId.isEmpty()) {
					    sr.setIsCertificate(true);
						sr.setConsumerProgramStructureId(newConsumerProgramStructureId);
						return sr;
						}
				}
		  }
			sr.setIsCertificate(false);
			sr.setConsumerProgramStructureId("");
			return sr;
	}

	public String newProgramToBeMapped(List<ProgramsStudentPortalBean> listOfnewProgramToBeMappedByCPSid, String semester) {
		int sem = Integer.parseInt(semester);		
			for(int i = sem; i > 0; i--){
				String applicableSem = String.valueOf(i);
				Optional<ProgramsStudentPortalBean> program = listOfnewProgramToBeMappedByCPSid.stream().filter(l->l.getNoOfSemesters().equals(applicableSem)).findAny();	
				if(program.isPresent()) {
					return program.get().getNewConsumerProgramStructureId();		
			     }
		      }
		return "";
	   }
	
}
