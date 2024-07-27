package com.nmims.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

//import org.apache.commons.fileupload.FileItem;
//import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
 
import com.itextpdf.text.pdf.codec.Base64.InputStream;
import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.ContentFilesSetbean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.services.ContentService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContentForPGTests 
{
	@Autowired
	ContentService contentService;
	
	@Autowired
	ContentDAO contentdao;
	 
	
	 
	
	
	public List<ContentAcadsBean> getContent()
	{
		List<ContentAcadsBean> contents = null;
		try {
			contents = contentdao.getAllActivePGContents();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return contents;
	}
	
	
	//@Test
	public void createContent()
	{
		System.out.println("Create Content Testcase Started");
		int expected = 1;
		int actual = 0;
		try {
		ContentFilesSetbean contentfiles = new ContentFilesSetbean();
		List<ContentAcadsBean> contents = new ArrayList<ContentAcadsBean>();
		contentfiles.setMonth("Jul");
		contentfiles.setYear("2021");
		contentfiles.setSubjectCodeId("1");
		contentfiles.setSubject("Marketing Strategy");
		contentfiles.setCreatedBy("Admin");
		contentfiles.setLastModifiedBy("Admin");
		
		//Add the content files for the above acadcycle , subject
		ContentAcadsBean content = new ContentAcadsBean();
		content.setName("Junit case");
		content.setDescription("Add content using junit case.");
		content.setUrlType("View");
		content.setContentType("Course Material");
		
		//CommonsMultipartFile com2 = new CommonsMultipartFile(getFileDetails());
		CommonsMultipartFile com2 = null;
		if(!(com2.isEmpty()))
		{
			content.setFileData(com2);
			contents.add(content);
			contentfiles.setContentFiles(contents);
			System.out.println("get size "+contentfiles.getContentFiles().size());
			HashMap<String,String> upload_response = contentService.createContent(contentfiles);		
			actual = 1;
		
		}
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		
		System.out.println("Create Content Testcase Ended");
		
	}
	
	
	//Transfer Content
	//@Test
	public void transferContent()
	{
		int actual = 0;
		int expected = 1;
		System.out.println("Transfer Content testcases started");
		try {
		
		List<ContentAcadsBean> contents = getContent();
		
		System.out.println("id1 "+contents.get(1).getId());
		System.out.println("id2 "+contents.get(2).getId());
		
		if(contents.size() > 0)
		{
		ContentAcadsBean content = new ContentAcadsBean();
		ArrayList<String> contentlist = new ArrayList<String>();
		contentlist.add(contents.get(1).getId());
		content.setContentToTransfer(contentlist);
		content.setToMonth("Jan");
		content.setToYear("2070");
		content.setCreatedBy("Admin");
		content.setLastModifiedBy("Admin");
		
		HashMap<String, String> transfer = contentService.transferContent(content);
		
		if(transfer.get("success").equals("true"))
			actual = 1;
		}
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		assertEquals(expected, actual);
		System.out.println("Transfer Content testcases ended");
		
		
	}
	
	//Get the subject Name by PssIds
	//@Test
	public void getSubjectNamesByPssId()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get SubjectName By PssId started");
		try {
			
			String subject = contentService.getSubjectNameByPssId("1");
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get SubjectName By PssId started ended");
		
	}
	
	
	//@Test
	public void getSubjectNamesBySubjectcodeIdd()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get SubjectName By SubjectCodeId started");
		try {
			
			String subject = contentService.getSubjectNameBySubjectCodeId("1");
			System.out.println("SubjectName "+subject);
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get SubjectName By SubjectCodeId started ended");
		
	}
	
	
	//Get the content By SubjectCOdeId
	//@Test
	public void getcontentBySubjectcodeId()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get content By SubjectCodeId started");
		try {
			
			List<ContentAcadsBean> contents = contentService.getContentsBySubjectCodeId("1","Jan","2021");
			System.out.println("Content size "+contents.size());
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get content By SubjectCodeId started ended");
	}
	
	
	//@Test
	public void getVideocontentBySubjectcodeId()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get Video content By SubjectCodeId started");
		try {
			ContentAcadsBean bean = new ContentAcadsBean();
			bean.setMonth("Jul");
			bean.setYear("2021");
			bean.setSubjectCodeId("1");
			
			List<VideoContentAcadsBean> contents = contentService.getVideoContentForSubject(bean);
			System.out.println("Content size "+contents.size());
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get Video content By SubjectCodeId started ended");
	}
	
	//Get Faculty Subject with subjectcodes 
	
	//@Test
	public void getFacultySubjects()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get Faculty subjects started");
		try {
			
			ArrayList<ConsumerProgramStructureAcads> subjects = contentService.getFacultySubjectsCodes("NGASCE00021","Jan","2021");
			System.out.println("subjects size "+subjects.size());
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get Faculty subjects started");
	}
	
	//Get the content by its it
	
	//@Test
	public void getContentById()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get Content by its Id started");
		try {
			
			List<ContentAcadsBean> contents = getContent();
			ContentAcadsBean content = contentService.findById(contents.get(1).getId());
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get Content by its Id started");
	}
	
	//@Test
	public void updateSingleContent()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Update Single Content  started");
		try {
			
			List<ContentAcadsBean> contents = getContent();
			ContentAcadsBean content = contents.get(1);
			System.out.println("Content "+content);
			content.setName("Junit");
			content.setDescription("Updated");
			
			HashMap<String,String> update_response = contentService.updateContentSingleSetup(content);
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Update Single Content  started");
	}
	
	//@Test
	public void deleteSingleContent()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Delete Single Content  started");
		try {
			
			List<ContentAcadsBean> contents = getContent();
			ContentAcadsBean content = contents.get(1);
			System.out.println("Content "+content);
			
			HashMap<String,String> delete_response = contentService.deleteContentSingleSetup(content.getId(),content.getConsumerProgramStructureId());
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Delete Single Content  started");
	}
	
	//@Test
	public void updateContent()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Update  Content  started");
		try {
			
			List<ContentAcadsBean> contents = getContent();
			ContentAcadsBean content = contents.get(1);
			System.out.println("Content "+content);
			content.setName("Junit");
			content.setDescription("Updated");
			
			HashMap<String,String> update_response = contentService.updateContent(content);
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Update  Content  started");
	}
	
	
	//@Test
	public void deleteContent()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Delete  Content  started");
		try {
			
			List<ContentAcadsBean> contents = getContent();
			ContentAcadsBean content = contents.get(1);
			System.out.println("Content "+contents.get(1).getId());
		
			
			HashMap<String,String> delete_response = contentService.deleteContent(contents.get(1).getId());
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Delete  Content  started");
	}
	
	//@Test
	public void searchContent()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("search  Content  started");
		try {
			
			ContentAcadsBean content = new ContentAcadsBean();
			content.setMonth("Jul");
			content.setYear("2021");
			content.setSubjectCodeId("1");
			
		
			
			PageAcads<ContentAcadsBean> content_result = contentService.searchContent(1,content,"distinct");
			System.out.println("content_result "+content_result.getRowCount());
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("search  Content  started");
	}
	
	public void getRecordingForLastCycleBySubjectCode()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get Recording for last cycle  started");
		try {
			
			List<ContentAcadsBean> contents = getContent();
			
			List<ContentAcadsBean> record_list = contentService.getRecordingForLastCycleBySubjectCode("1","Jul","2021");
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get Recording for last cycle  ended");
	}
	
	//@Test
	public void updateContentByDistinct()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Update Single Content By Search Content Started");
		try {
			
			List<ContentAcadsBean> contents = getContent();
			ContentAcadsBean content = contents.get(1);
			System.out.println("Content "+content);
			content.setName("Junit By Search");
			content.setDescription("Updated");
			String masterKey = content.getConsumerProgramStructureId();
			
			 contentService.updateContentByDistinct(content,masterKey);
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Update Single Content  By Search Content Ended");
	}
	
	//@Test
	public void DeleteContentByDistinct()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Delete Single Content By Search Content Started");
		try {
			
			List<ContentAcadsBean> contents = getContent();
			ContentAcadsBean content = contents.get(1);
			System.out.println("Content "+content);
			String masterKey = content.getConsumerProgramStructureId();
			
			 contentService.deleteContentByDistinct(content.getId(),masterKey);
			
			actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Delete Single Content  By Search Content Ended");
	}
	
	
	//@Test
	public void getProgramStructureByConsumerType()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get program Structure By Consumer Type Started");
		try {
			
			
			ArrayList<ConsumerProgramStructureAcads> prgmStructList = contentService.getProgramStructureByConsumerType("6");
			
			
			if(prgmStructList.size() > 0)
				actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get program Structure By Consumer Type Ended");
	}
	
	
	//@Test
	public void getProgramByConsumerType()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get program  By Consumer Type Started");
		try {
			
			
			ArrayList<ConsumerProgramStructureAcads> prgmStructList = contentService.getProgramByConsumerType("6");
			
			
			if(prgmStructList.size() > 0)
				actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get program  By Consumer Type Ended");
	}
	
	
	//@Test
	public void getSubjectByConsumerType()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get Subject  By masterKey Started");
		try {
			
			
			ArrayList<ConsumerProgramStructureAcads> prgmStructList = contentService.getSubjectByConsumerType("6","29","5");
			
			
			if(prgmStructList.size() > 1)
				actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get Subject  By masterKey Ended");
	}
	
	//@Test
	public void getProgramByConsumerTypeAndPrgmStructure()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get Program  By ConsumerType and prgmStruct Started");
		try {
			
			
			ArrayList<ConsumerProgramStructureAcads> prgmStructList = contentService.getProgramByConsumerTypeAndPrgmStructure("6","10");
			
			
			if(prgmStructList.size() > 0)
				actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get Program  By ConsumerType and prgmStruct Ended");
	}
	
	
	//@Test
	public void getProgramsListForCommonContent()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get ProgamList For Common Content Started");
		try {
			
			List<ContentAcadsBean> contents = getContent();
			
			List<ContentAcadsBean> prgmList = contentService.getProgramsListForCommonContent(contents.get(1).getId());
			
			
			if(prgmList.size() > 0)
				actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get ProgamList For Common Content Ended");
	}
	
	//@Test
	public void getCommonGroupProgramList()
	{
		int expected = 1;
		int actual = 0;
		System.out.println("Get ProgamList For Common Content Started");
		try {
			
			List<ContentAcadsBean> contents = getContent();
			
			
			List<ContentAcadsBean> prgmList = contentService.getCommonGroupProgramList(contents.get(1));
			
			
			if(prgmList.size() > 0)
				actual = 1;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		System.out.println("Get ProgamList For Common Content Ended");
	}
	
	
	/*private FileItem getFileDetails() 
	{
		FileItem files = null;
		try {
		File file=new File("F:\\Resume.pdf");
		FileInputStream input = new FileInputStream(file);
		
		 files = new FileItem() {
			
			@Override
			public String getContentType() {
				// TODO Auto-generated method stub
				return "application/pdf";
			}
			
			
			@Override
			public long getSize() {
				// TODO Auto-generated method stub
				return file.length();
			}
			
			@Override
			public OutputStream getOutputStream() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return file.getName();
			}
			
			@Override
			public java.io.InputStream getInputStream() throws IOException {
				// TODO Auto-generated method stub
				return input;
			}
			
			@Override
			public String getFieldName() {return null;}
			
			
			
			@Override
			public byte[] get() {return null;}
			
			@Override
			public void delete() {	}

			@Override
			public void setHeaders(FileItemHeaders headers) {}
			
			@Override
			public FileItemHeaders getHeaders() {return null;}
			
			@Override
			public void write(File file) throws Exception {}
			
			@Override
			public void setFormField(boolean state) {}
			
			@Override
			public void setFieldName(String name) {}
			@Override
			public boolean isInMemory() {return false;}
			
			@Override
			public boolean isFormField() {return false;}
			@Override
			public String getString(String encoding) throws UnsupportedEncodingException {return null;}
			@Override
			public String getString() {return null;}
			
			
		};
		
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return files;

	}
*/

}
