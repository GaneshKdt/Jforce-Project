package com.nmims.test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.impl.xb.ltgfmt.TestCase.Files;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.ContentFilesSetbean;
import com.nmims.daos.ContentDAO;

import com.nmims.interfaces.ContentInterface;
import com.nmims.services.ContentService;
import com.nmims.services.StudentService;

import static org.junit.Assert.assertEquals;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UploadContentS3Test  extends AcadsTests
{
	
	
	@Autowired
	private ContentService content;
	
	@Autowired
	ContentService contentService;
	
	

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this); //without this you will get NPE
	}
	
	@Test
	public void uploadContent()
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
	}*/

}
