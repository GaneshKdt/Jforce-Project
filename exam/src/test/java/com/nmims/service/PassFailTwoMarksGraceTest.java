package com.nmims.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.PassFailDAO;
import com.nmims.services.PassFailService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PassFailTwoMarksGraceTest {
	
	    @Autowired
	    private PassFailService passFailService;

	    @MockBean
	    private PassFailDAO passFailDao;


	    @Test
	    public void filterGraceMarksApplyGraceTest() {
	        List<PassFailExamBean> passFailStudentList = preparePassFailGraceGivenStudentList();
	        HashMap<String, ProgramSubjectMappingExamBean> configurationMap = prepareConfigurationMap();
	        HashMap<String, StudentExamBean> allStudents = prepareAllStudents();

	        when(passFailDao.getProgramSubjectPassingConfigurationMap()).thenReturn(configurationMap);
	        when(passFailDao.getAllStudents()).thenReturn(allStudents);

	        List<PassFailExamBean> result = passFailService.filterApplyGraceStudents(passFailStudentList);
	        
	        assertEquals(1, result.size());
	        assertEquals("SapidOne", result.get(0).getSapid());

	    }

	    private List<PassFailExamBean> preparePassFailGraceGivenStudentList() {
	        List<PassFailExamBean> passFailStudentList = new ArrayList<>();

	        PassFailExamBean beanOne = createPassFailExamBean("SapidOne", "SubjectOne", "65", "");
	        passFailStudentList.add(beanOne);

	        PassFailExamBean beanTwo = createPassFailExamBean("SapidTwo", "SubjectTwo", "60", "Copy Case");
	        passFailStudentList.add(beanTwo);

	        return passFailStudentList;
	    }

	    private HashMap<String, ProgramSubjectMappingExamBean> prepareConfigurationMap() {
	        HashMap<String, ProgramSubjectMappingExamBean> configurationMap = new HashMap<>();

	        ProgramSubjectMappingExamBean beanOne = createProgramSubjectMappingExamBean("ProgramOne", "SubjectOne", "PrgmStructOne", 70, 5, "Y");
	        configurationMap.put(beanOne.getProgram() + "-" + beanOne.getSubject() + "-" + beanOne.getPrgmStructApplicable(), beanOne);

	        ProgramSubjectMappingExamBean beanTwo = createProgramSubjectMappingExamBean("ProgramTwo", "SubjectTwo", "PrgmStructTwo", 60, 10, "N");
	        configurationMap.put(beanTwo.getProgram() + "-" + beanTwo.getSubject() + "-" + beanTwo.getPrgmStructApplicable(), beanTwo);
	        
	        ProgramSubjectMappingExamBean beanThree = createProgramSubjectMappingExamBean("ProgramThree", "SubjectThree", "PrgmStructThree", 60, 10, "Y");
	        configurationMap.put(beanThree.getProgram() + "-" + beanThree.getSubject() + "-" + beanThree.getPrgmStructApplicable(), beanThree);

	        return configurationMap;
	    }

	    private HashMap<String, StudentExamBean> prepareAllStudents() {
	        HashMap<String, StudentExamBean> allStudents = new HashMap<>();

	        StudentExamBean beanOne = createStudentExamBean("SapidOne", "ProgramOne", "ConsumerTypeOne", "PrgmStructOne");
	        allStudents.put(beanOne.getSapid(), beanOne);

	        StudentExamBean beanTwo = createStudentExamBean("SapidTwo", "ProgramTwo", "ConsumerTypeTwo", "PrgmStructTwo");
	        allStudents.put(beanTwo.getSapid(), beanTwo);

	        return allStudents;
	    }

	    private PassFailExamBean createPassFailExamBean(String sapid, String subject, String total, String remarks) {
	        PassFailExamBean bean = new PassFailExamBean();
	        bean.setSapid(sapid);
	        bean.setSubject(subject);
	        bean.setTotal(total);
	        bean.setRemarks(remarks);
	        return bean;
	    }

	    private ProgramSubjectMappingExamBean createProgramSubjectMappingExamBean(String program, String subject,
	                                                                              String prgmStructApplicable, int passScore,
	                                                                              int maxGraceMarks, String isGraceApplicable) {
	        ProgramSubjectMappingExamBean bean = new ProgramSubjectMappingExamBean();
	        bean.setProgram(program);
	        bean.setSubject(subject);
	        bean.setPrgmStructApplicable(prgmStructApplicable);
	        bean.setPassScore(passScore);
	        bean.setMaxGraceMarks(maxGraceMarks);
	        bean.setIsGraceApplicable(isGraceApplicable);
	        return bean;
	    }

	    private StudentExamBean createStudentExamBean(String sapid, String program, String consumerType,
	                                                  String prgmStructApplicable) {
	        StudentExamBean bean = new StudentExamBean();
	        bean.setSapid(sapid);
	        bean.setProgram(program);
	        bean.setConsumerType(consumerType);
	        bean.setPrgmStructApplicable(prgmStructApplicable);
	        return bean;
	    }
}
