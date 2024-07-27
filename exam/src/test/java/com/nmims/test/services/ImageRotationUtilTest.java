package com.nmims.test.services;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.itextpdf.text.Image;
import com.nmims.util.ImageRotationUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageRotationUtilTest {

	
	@Test
	public void studentNullData() {
		String studentUrl = null;
		Image studentPhoto = null;
		boolean expected = true;
		boolean actual = false;
		try {
			ImageRotationUtil.getImageRotatedByOrientation(studentPhoto, studentUrl);
			actual =  false;
		} catch (Exception e) {
		actual = true;
		}
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void studentData() {
		boolean expected = true;
		boolean actual = false;
		try {
		String studentUrl = "https://studentdocumentsngasce.s3-ap-south-1.amazonaws.com/StudentDocuments/00Q0o00001WOs8C/00Q0o00001WOs8C_CeAu_Picture.jpeg";
		Image studentPhoto = Image.getInstance(new URL(studentUrl));
	
	
			ImageRotationUtil.getImageRotatedByOrientation(studentPhoto, studentUrl);
			actual =  true;
		} catch (Exception e) {
		actual = false;
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void studentDataWithNoImage() {
		boolean expected = true;
		boolean actual = false;
		try {
		String studentUrl = "https://studentdocumentsngasce.s3-ap-south-1.amazonaws.com/StudentDocuments/00Q0o00001WOs8C/00Q0o00001WOs8C_CeAu_Picture.jpeg";
		Image studentPhoto =null;
	
	
			ImageRotationUtil.getImageRotatedByOrientation(studentPhoto, studentUrl);
			actual =  false;
		} catch (Exception e) {
		actual = true;
		}
		assertEquals(expected, actual);
	}
	
}
