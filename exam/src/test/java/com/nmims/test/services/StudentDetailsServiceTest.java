package com.nmims.test.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.assembler.ObjectConverter;
import com.nmims.dto.StudentProfileDetailsExamDto;
import com.nmims.interfaces.StudentDetailsServiceInterface;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentDetailsServiceTest {
	@Autowired
	@Qualifier("studentProfileDetailsService")
	StudentDetailsServiceInterface studentDetailsService;
	
	/**
	 * Unit test of updateStudentProfileDetails() of StudentDetailsService
	 */
	@Test
	public void testUpdateStudentProfileDetails() {
		StudentProfileDetailsExamDto studentProfileDetailsDto = new StudentProfileDetailsExamDto();
		
		boolean actualValue = false;
		String userId = "adminUser";
		String studentDetails = "{\r\n" + 
								"    \"sapid\": 77121100493,\r\n" + 
								"    \"sem\": 1,\r\n" + 
								"	 \"firstName\": \"Shahbaz\",\r\n" + 
								"	 \"middleName\": null,\r\n" + 
								"	 \"lastName\": \"Patel\",\r\n" +
								"    \"fatherName\": \"Anisahemad\",\r\n" + 
								"    \"motherName\": \"Gulshan\",\r\n" + 
								"	 \"spouseName\": null,\r\n" +
								"	 \"gender\": \"Male\",\r\n" +
								"    \"dob\": \"1999-07-09\",\r\n" + 
								"	 \"age\": 29,\r\n" +
								"    \"imageUrl\": \"https://studentdocumentsngasce.s3.ap-south-1.amazonaws.com/StudentDocuments/0012j00000DLxsW/0012j00000DLxsW_TVpn_Picture.jpg\",\r\n" + 
								"    \"emailId\": \"shahebajpatel@gmail.com\",\r\n" + 
								"    \"mobile\": \"8087004013\",\r\n" +
								"    \"altPhone\": null,\r\n" +
								"    \"addressLine1\": \"Flat no 204 courtyard 15 housing society\",\r\n" + 
								"    \"addressLine2\": \"kudalwaadi Chikhali road\",\r\n" + 
								"    \"landMark\": \"Maharashtra\",\r\n" + 
								"    \"addressLine3\": \"Near Kudalwaadi police station\",\r\n" + 
								"    \"pin\": \"411062\",\r\n" + 
								"    \"city\": \"Pune\",\r\n" + 
								"    \"state\": \"Maharashtra\",\r\n" + 
								"    \"country\": \"India\",\r\n" + 
								"    \"centerCode\": \"a022j000002p4Em\",\r\n" + 
								"    \"centerName\": \"Pune - Kothrud\",\r\n" + 
								"    \"program\": \"MBA (OM)\",\r\n" + 
								"    \"programStructure\": \"Jul2019\",\r\n" + 
								"	 \"enrollmentMonth\": \"Jul\",\r\n" + 
								"    \"enrollmentYear\": 2021,\r\n" +
								"    \"validityEndMonth\": \"Jun\",\r\n" + 
								"    \"validityEndYear\": 2025,\r\n" + 
								"    \"programChanged\": null," +
								"    \"oldProgram\": null," +
								"    \"programCleared\": \"N\"," +
								"    \"programStatus\": null," +
								"    \"programRemarks\": null," +
								"	 \"highestQualification\": \"Bachelor Degree\",\r\n" +
								"	 \"industry\": null,\r\n" +
								"	 \"designation\": null\r\n" +
								"}";
		try {
			studentProfileDetailsDto = ObjectConverter.mapFromJson(studentDetails, studentProfileDetailsDto.getClass());
		
			studentDetailsService.updateStudentProfileDetails(studentProfileDetailsDto, userId);
			actualValue = true;
		} 
		catch (Exception ex) {
//			ex.printStackTrace();
		}
		
		assertEquals(true, actualValue);
	}
	
	/**
	 * Unit test of viewStudentProfileDetails() of StudentDetailsService
	 */
	@Test
	public void testViewStudentProfileDetails() {
		long studentSapid = 77121100493L;
		int enrolledSem = 1;
		
		try {
			assertEquals("8087004013", studentDetailsService.viewStudentProfileDetails(studentSapid, enrolledSem).getMobile());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
		}
	}
}
