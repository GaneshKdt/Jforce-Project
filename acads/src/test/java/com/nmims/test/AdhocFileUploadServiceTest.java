package com.nmims.test;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.ContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.helpers.AWSHelper;
import com.nmims.services.AdhocFileUploadService;

@RunWith(SpringRunner.class)
public class AdhocFileUploadServiceTest {
	
	
	@InjectMocks
	AdhocFileUploadService service;
	
	@Mock
	ContentDAO contentDAO;
	
	@Mock
	AWSHelper awsHelper;
	
	private Logger logger=LoggerFactory.getLogger("test");
	
	@Before
	public void setup(){
	    MockitoAnnotations.initMocks(this);
	}
	
	//@Test
	public void testFileUpload() throws Exception{
		File file=new File("D:\\demo\\test.txt");
		FileInputStream input = new FileInputStream(file);
		MultipartFile multipartFile = new MockMultipartFile("file",
		            file.getName(), "text/plain", IOUtils.toByteArray(input));
		String fileName=file.getName();
		String userId="admin";
		String title= "Test File Title!";
		String baseFolderPath = "demo/";
		String filePathS3=baseFolderPath+fileName;
		when(contentDAO.checkAdhocFileTitle(title)).thenReturn(true);
		boolean check=contentDAO.checkAdhocFileTitle(title);
		System.out.println("CHeck : "+check);
		if(check) {
			when(awsHelper.uploadMultiPartFileToS3(multipartFile, baseFolderPath, "testBucket", fileName, logger)).thenReturn(true);
			Boolean status=awsHelper.uploadMultiPartFileToS3(multipartFile,baseFolderPath , "adhocfilesngasce", filePathS3,logger);
			System.out.println("STatus :"+status);
			if(status) {
			System.out.println("File upload to S3 server!");
			contentDAO.insertAdhocDocument(fileName, userId, title, logger);
			System.out.println("URL saved in Database with title "+title+" and file name :"+fileName);
			}
		}
	}
	

	//@Test
	public void testGetAllUploadedDocumentsURLByAdmin() throws Exception{
		System.out.println("Get All uploaded documents!");
		ArrayList<ContentAcadsBean> list=new ArrayList<>();
		ContentAcadsBean obj1=new ContentAcadsBean();
		obj1.setId("1");
		obj1.setWebFileurl("kashlkhskadad");
		ContentAcadsBean obj2=new ContentAcadsBean();
		obj2.setId("2");
		obj2.setWebFileurl("sadasdassasa");
		ContentAcadsBean obj3=new ContentAcadsBean();
		obj3.setId("3");
		obj3.setWebFileurl("sadasdassasa");
		list.add(obj1);
		list.add(obj2);
		list.add(obj3);
		String userId="NGASCE0001";
	
		when(contentDAO.getAllUploadedDocumentsURL(userId)).thenReturn(list);
			ArrayList<ContentAcadsBean> resultList=contentDAO.getAllUploadedDocumentsURL(userId);
		System.out.println("getAllUploadedDocumentsURL "+resultList);
	}
	

	//@Test
	public void testDeleteFileURLByAdmin() {
		System.out.println("deleteFileURLByAdmin called!)");
		String id="1";
		String bucketName="/academics";
		String keyName="keyName.pdf";
		String message="error";
		when(awsHelper.deleteAdhocFileObjectFromS3(bucketName, keyName, logger)).thenReturn("success");
		message=awsHelper.deleteAdhocFileObjectFromS3(bucketName, keyName, logger);
		
		if("success".equals(message)) {
			boolean flag=false;
			System.out.println("contentDAO.deletAdhocFile(id, logger) called!");
			when(contentDAO.deletAdhocFile(id, logger)).thenReturn(true);
			flag=contentDAO.deletAdhocFile(id, logger);
			System.out.println("File deleted successfully from database! "+flag);
			}
		
	}

	@Test
	public void testGetFilePath() {
	String id="1";
	String filePath=null;
	when(contentDAO.getAdhocFilePath(id)).thenReturn("test/test/filepath.pdf");
	filePath=contentDAO.getAdhocFilePath(id);
	System.out.println("File Path ="+filePath);
	}

}
