package com.nmims.test.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.assembler.ObjectConverter;
import com.nmims.dto.StudentProfileDto;
import com.nmims.services.SfdcApiService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SfdcApiServiceTest {
	@Autowired
	private SfdcApiService sfdcApiService;
	
	/**
	 * Unit test of updateProfileSFDC() method of SfdcApiService Class.
	 */
	@Test
	public void testUpdateProfileSFDC() {
		StudentProfileDto studentProfileDto = new StudentProfileDto();
		
		String studentDetails = "{\r\n" + 
								"    \"sapid\": 77121730057,\r\n" + 
								"	 \"firstName\": \"Nikita\",\r\n" + 
								"	 \"lastName\": \"Bansode\",\r\n" +
								"    \"fatherName\": \"Surendra\",\r\n" + 
								"    \"motherName\": \"Yogini\",\r\n" + 
								"	 \"spouseName\": null,\r\n" +
								"    \"dob\": \"1999-07-09\",\r\n" + 
								"    \"studentImage\": \"https://studentdocumentsngasce.s3.ap-south-1.amazonaws.com/StudentDocuments/00Q2j000007GFdu/00Q2j000007GFdu_0gFb_Picture.jpg\",\r\n" + 
								"    \"emailId\": \"nikkibansode1999@gmail.com\",\r\n" + 
								"    \"mobileNo\": \"8169376125\",\r\n" +
								"    \"addressLine1\": \"Flat no 703 , plot no 92,93 ,\",\r\n" + 
								"    \"addressLine2\": \"Sushil Harmony , kamothe sector 22\",\r\n" + 
								"    \"landMark\": null,\r\n" + 
								"    \"addressLine3\": null,\r\n" + 
								"    \"pin\": \"410209\",\r\n" + 
								"    \"city\": \"Khandeshwar\",\r\n" + 
								"    \"state\": \"Maharashtra\",\r\n" + 
								"    \"country\": \"India\",\r\n" + 
								"    \"centerName\": \"Mumbai - Navi Mumbai - Kharghar Sector 2\",\r\n" + 
								"    \"centerId\": \"a020o00000vQqrw\",\r\n" + 
								"    \"program\": \"MBA (FM)\",\r\n" + 
								"    \"programStructure\": \"Jul2019\",\r\n" + 
								"	 \"enrollmentMonth\": \"Jul\",\r\n" + 
								"    \"enrollmentYear\": 2021,\r\n" +
								"    \"validityEndMonth\": \"Jun\",\r\n" + 
								"    \"validityEndYear\": 2025,\r\n" + 
								"    \"previousProgram\": null," +
								"	 \"highestQualification\": \"Bachelor Degree\"\r\n" +
								"}";
		try {
			studentProfileDto = ObjectConverter.mapFromJson(studentDetails, studentProfileDto.getClass());
		
			assertThat(sfdcApiService.updateProfileSFDC(studentProfileDto).contains("dob : 1999-07-09"));
			
			studentProfileDto.setAltPhone("09967755512");
			assertEquals(true, sfdcApiService.updateProfileSFDC(studentProfileDto).contains("\"altPhone\" : \"09967755512\""));
		} 
		catch (Exception ex) {
//			ex.printStackTrace();
		}
	}
}
