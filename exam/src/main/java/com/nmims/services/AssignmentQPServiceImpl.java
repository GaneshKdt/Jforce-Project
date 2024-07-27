package com.nmims.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile; 

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;  
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;  
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable; 
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentFilesSetbean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.QpHeaderPageEvent; 
import com.nmims.beans.ResponseBean;

@Component
public class AssignmentQPServiceImpl implements QpAdmin,QpFaculty,QpReviewer  {
	
	@Autowired 
	AssignmentsDAO asignmentsDAO;
	@Autowired 
	StudentMarksDAO sDao;
	@Autowired 
	FacultyDAO fDao;
	
	private ArrayList<String> currentYearList = new ArrayList<String>(Arrays.asList("2014", "2015","2016", "2017" , "2018" , "2019", "2020","2021" )); 
	private ArrayList<String> subjectList = null;
	
	Font font8 = new Font(Font.FontFamily.HELVETICA, 9);
	Font font8Bold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
	
	
	Font font9 = new Font(Font.FontFamily.HELVETICA, 10);
	Font font9Bold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD); 
	Font font9Italic = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
	Font font9BoldItalic = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC);
	Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, (float) 11);
	Font font10Bold = new Font(Font.FontFamily.TIMES_ROMAN, (float) 11, Font.BOLD);
	Font font12 = new Font(Font.FontFamily.HELVETICA, 13);
	Font font12Bold = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
	
	@Value( "${ASSIGNMENT_FILES_PATH}" )
	private String ASSIGNMENT_FILES_PATH;
	
	@Value( "${ASSIGNMENT_REPORTS_GENERATE_PATH}" )
	private String ASSIGNMENT_REPORTS_GENERATE_PATH; 
	
	@Autowired
	AmazonS3Helper amazonS3Helper;
	
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			this.subjectList = sDao.getActiveSubjects();
		}
		return subjectList;
	}
	public HashMap<String, String> getFacultyList(){
		HashMap<String, String> map = fDao.getFacultyIdNameMap();
		return (HashMap<String, String>)sortByValue(map);
	}
	public  <K, V extends Comparable<? super V>> Map<K, V> 
	sortByValue( Map<K, V> map )
	{
		List<Map.Entry<K, V>> list =
				new LinkedList<Map.Entry<K, V>>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>()
				{
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
			{
				return (o1.getValue()).compareTo( o2.getValue() );
			}
				} );

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}
	public AssignmentFilesSetbean batchUpdateQpFacultyMapping(List<AssignmentFilesSetbean> facultyQpBeanList) {
		int row=0;
		
		AssignmentFilesSetbean response = new AssignmentFilesSetbean();
		
		for(AssignmentFilesSetbean bean : facultyQpBeanList) {
			if(bean.getExamYear().equalsIgnoreCase("") || bean.getExamMonth().equalsIgnoreCase("") || 
					 bean.getFacultyId().equalsIgnoreCase("") || bean.getReviewer().equalsIgnoreCase("")
					|| bean.getStartDate().length()<5  || bean.getEndDate().length()<5  || bean.getDueDate().length()<5 ) {
				
				response.setStatus("error");
				response.setMessage("Invalid input");
				return response;
			}
			try {
				bean =checkStartDateEndDateValid(bean);  
				if(bean.getStatus().equalsIgnoreCase("error")) { 
					response.setStatus("error");
					response.setMessage(bean.getMessage());
					return response;
				} 
			} catch (Exception e) {
			}
		}
		int alreadyExistsCount=0;
		for(AssignmentFilesSetbean bean : facultyQpBeanList) {
			ArrayList<String>  pssIdList =new ArrayList<String>();  
			if(bean.getPss_id()==null) {//for excel upload find pssid 
				pssIdList = asignmentsDAO.getPssIdAllTypeForExcelUpload(bean);
			}else {//for form upload
				if(bean.getProgram().split(",").length>1|| bean.getProgramStructure().split(",").length>1||bean.getConsumerType().split(",").length>1 )
				{
					//for all type
					pssIdList = asignmentsDAO.getPssIdForAll(bean.getProgram(),bean.getProgramStructure(),bean.getConsumerType(),bean.getPss_id());
				}else {
					//specific select
					pssIdList.add(bean.getPss_id());
				}
			}
			for(String pssid : pssIdList) {
		    	bean.setPss_id(pssid); 
		    	List<AssignmentFilesSetbean> mappingData = asignmentsDAO.checkIfEntryExistsInQpTable(bean.getExamYear(),bean.getExamMonth(),bean.getPss_id());
		    	if(mappingData.size()==0) {
		    		row = row +asignmentsDAO.saveAssignmentSubjectFacultyMapping(bean);
		    	}else {
		    		alreadyExistsCount++;
		    	}
		    }
		}
		if(alreadyExistsCount>0) {
			response.setStatus("error");
			response.setMessage(alreadyExistsCount+" Faculty Mapping Already Exists.");;
			return response;
		}
		else if(row>0) {
			response.setStatus("success");
			response.setMessage("Saved Successfully");;
			return response;
		}else {
			response.setStatus("error");
			response.setMessage("Failed to Save");
			return response;
		}
		
	}
	public AssignmentFilesSetbean checkStartDateEndDateValid(AssignmentFilesSetbean filesSet) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		Date startDate=sdf.parse(filesSet.getStartDate().replaceAll("T", " "));
		Date endDate=sdf.parse(filesSet.getEndDate().replaceAll("T", " "));
		Date dueDate=sdf.parse(filesSet.getDueDate().replaceAll("T", " "));
		Calendar now = Calendar.getInstance();
		Calendar sDate = Calendar.getInstance();
		Calendar eDate = Calendar.getInstance();
		Calendar dDate = Calendar.getInstance();
		sDate.setTime(startDate);
		eDate.setTime(endDate);
		dDate.setTime(dueDate);

		if(eDate.before(now) || sDate.before(now)) { 
			filesSet.setStatus("error"); 
			filesSet.setMessage("Error in Assignment config, Invalid Start Date or End Date");
		}else if(eDate.before(sDate)) {
			filesSet.setStatus("error");
			filesSet.setMessage("Error in Assignment config, EndDate : "+filesSet.getEndDate()+" is before StartDate : "+filesSet.getStartDate());
			
		}else if(dDate.before(now)) {
			filesSet.setStatus("error");
			filesSet.setMessage("Error in test config, DueDate is invalid");
		}
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			filesSet.setStartDate(sdf1.format(startDate));
			filesSet.setEndDate(sdf1.format(endDate));	
			filesSet.setDueDate(sdf1.format(dueDate));	 	
		return filesSet;
	}
	public AssignmentFilesSetbean createFacultyReviewerMappingFromForm(AssignmentFilesSetbean formData){
		if(formData.getAssignmentFilesSet() != null ) {
			//setting common parameters like examYear examMonth to list of SubjectFacultyMap.
			for(AssignmentFilesSetbean subFacMap : formData.getAssignmentFilesSet()) {
				subFacMap.setExamYear(formData.getExamYear());
				subFacMap.setExamMonth(formData.getExamMonth());
				subFacMap.setStartDate(formData.getStartDate());
				subFacMap.setEndDate(formData.getEndDate());
				subFacMap.setConsumerType(formData.getConsumerTypeId());
				subFacMap.setProgramStructure(formData.getProgramStructureId());
				subFacMap.setProgram(formData.getProgramId());
				subFacMap.setDueDate(formData.getDueDate());
			}
		}else {
			AssignmentFilesSetbean response = new AssignmentFilesSetbean();
			response.setStatus("error");
			response.setMessage("Please fill atleast 1 subject faculty mapping");
			return response;
		}
		
		
		return batchUpdateQpFacultyMapping(formData.getAssignmentFilesSet());
		
	}
	public AssignmentFilesSetbean createFacultyReviewerMappingFromExcel(AssignmentFilesSetbean filesSet){
		
		ExcelHelper excelHelper = new ExcelHelper();
		ArrayList<List> excelData = excelHelper.readAssignmentQpExcel(filesSet);
		List<AssignmentFilesSetbean> facultyQpBeanList = (List<AssignmentFilesSetbean>) excelData.get(0);
		
		return batchUpdateQpFacultyMapping(facultyQpBeanList);
		
	}
	public List<AssignmentFilesSetbean> getPendingUploadList(String facultyId) {
		List<AssignmentFilesSetbean> beanList = asignmentsDAO.getQpAssignedForFaculty(facultyId);
		return beanList;
	}
	public List<AssignmentFilesSetbean> getPendingReviewList(String facultyId){
		List<AssignmentFilesSetbean> beanList = asignmentsDAO.getQpToReviewForFaculty(facultyId);
		return beanList;
	}
	public AssignmentFilesSetbean sendFeedback(AssignmentFilesSetbean filesSet){
		AssignmentFilesSetbean response = new AssignmentFilesSetbean();
		try {
			asignmentsDAO.saveQpFeedback(filesSet);
			response.setStatus("success");
			response.setMessage("FeedBack Send Successfully");
		} catch (Exception e) {
			
			response.setStatus("error");
			response.setMessage("Failed to Send Feedback");
		}
		return response;
	}
	public AssignmentFilesSetbean addRemark(AssignmentFilesSetbean filesSet){
		AssignmentFilesSetbean response = new AssignmentFilesSetbean();
		try {
			asignmentsDAO.saveOverallRemark(filesSet);
			response.setStatus("success");
			response.setMessage("Review Updated Successfully");
		} catch (Exception e) {
			
			response.setStatus("error");
			response.setMessage("Failed to Send Feedback");
		}
		return response;
	}
	public AssignmentFilesSetbean countOfQpNotUploaded(String facultyId) {
		AssignmentFilesSetbean response =  new AssignmentFilesSetbean();
		
		try {
			Integer count1 = asignmentsDAO.getQpNotUploadedCount(facultyId); 
			Integer count2 = asignmentsDAO.getQpNotReviewedCount(facultyId);
			Integer count3 = asignmentsDAO.getQpNotResolvedCount(facultyId);
			Integer count4 = asignmentsDAO.getQpApprovedCount(facultyId);
			response.setCountOfUpload(count1+"");
			response.setCountOfReview(count2+"");
			response.setCountOfResolution(count3+"");
			response.setCountOfApprove(count4+"");
		}catch (Exception e) {
			
			response.setCountOfUpload("0");
			response.setCountOfReview("0");
			response.setCountOfResolution("0");
			response.setCountOfApprove("0");
		}	
		return response;
	}
	/*
	 * public String uploadAssignmentQpFile(AssignmentFileBean bean, String year,
	 * String month) {
	 * 
	 * String errorMessage = null; InputStream inputStream = null; OutputStream
	 * outputStream = null;
	 * 
	 * CommonsMultipartFile file = bean.getFileData(); String fileName =
	 * file.getOriginalFilename();
	 * 
	 * //Replace special characters in file fileName = fileName.replaceAll("'",
	 * "_"); fileName = fileName.replaceAll(",", "_"); fileName =
	 * fileName.replaceAll("&", "and"); fileName = fileName.replaceAll(" ", "_");
	 * 
	 * fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" +
	 * RandomStringUtils.randomAlphanumeric(10) +
	 * fileName.substring(fileName.lastIndexOf("."), fileName.length());
	 * 
	 * if(!(fileName.toUpperCase().endsWith(".PDF") ) ){ errorMessage =
	 * "File type not supported. Please upload .pdf file."; return errorMessage; }
	 * try { inputStream = file.getInputStream(); String filePath =
	 * ASSIGNMENT_FILES_PATH + month + year + "/" +fileName;
	 * 
	 * String previewPath = month + year + "/" + fileName; //Check if Folder exists
	 * which is one folder per Exam (Jun2015, Dec2015 etc.) File folderPath = new
	 * File(ASSIGNMENT_FILES_PATH + month + year ); if (!folderPath.exists()) {
	 * folderPath.mkdirs(); }
	 * 
	 * File(filePath);
	 * 
	 * outputStream = new FileOutputStream(newFile); int read = 0; byte[] bytes =
	 * new byte[1024];
	 * 
	 * while ((read = inputStream.read(bytes)) != -1) { outputStream.write(bytes, 0,
	 * read); } bean.setFilePath(filePath);
	 * bean.setQuestionFilePreviewPath(previewPath); outputStream.close();
	 * inputStream.close(); } catch (IOException e) { errorMessage =
	 * "Error in uploading file for "+bean.getSubject() + " : "+ e.getMessage();
	 *  } catch(Exception e){  }
	 * 
	 * return errorMessage; }
	 */
	
	public AssignmentFilesSetbean checkPendingUploadAndReviewList() {
		AssignmentFilesSetbean bean = new AssignmentFilesSetbean();
		bean.setTotalQpNotUploadedCount(asignmentsDAO.getTotalQpNotUploadedCount());
		bean.setTotalQpNotReviewedCount(asignmentsDAO.getTotalQpNotReviewedCount());
		bean.setUploadList(asignmentsDAO.getAllPendingListofQpUpload());
		bean.setReviewList(asignmentsDAO.getAllPendingListofQpReview());
		bean.setResolutionList(asignmentsDAO.getAllPendingListofQpResolution());
		bean.setCompletedList(asignmentsDAO.getAllQpReviewCompletedList());
		return bean;
	}
	public AssignmentFilesSetbean approveOrRejectFile(AssignmentFilesSetbean filesSet) {
		AssignmentFilesSetbean response = new AssignmentFilesSetbean();
		
		try {
			asignmentsDAO.approveQp(filesSet);
			response.setStatus("success");
			response.setMessage("Reviewed Successfully");
			return response;
		} catch (Exception e) {
			
			response.setStatus("error");
			response.setMessage("Failed to Approve");
			return response;
		}
		
	}
	public ResponseBean adminApproveQp(List<AssignmentFilesSetbean> fileSet) { 
		ResponseBean response = new ResponseBean();
		List<String> filePathList=new ArrayList<String>();
		Boolean flag =false;
		for(AssignmentFilesSetbean bean : fileSet) {
			String filePath="";
			List<AssignmentFilesSetbean> questionsBean = asignmentsDAO.getQuestionsForApproval(bean.getPss_id(),bean.getYear(),bean.getMonth());
			List<AssignmentFilesSetbean> masterTable= asignmentsDAO.findQpFileFromStagingTable(bean.getYear(),bean.getMonth(),bean.getPss_id());
			//String cps_id = masterTable.get(0).getConsumerProgramStructureId();
			String previewPath ="";
			try {
				filePath = createQuestionFilePdf(bean.getYear(),bean.getMonth(),masterTable.get(0).getSubject(),questionsBean); 
				HashMap<String,String> s3_response = null;
				if(filePath.length() > 0)
			    {
					String folderName =   ASSIGNMENT_REPORTS_GENERATE_PATH  + filePath ;
					
					String baseUrl =  bean.getMonth() + bean.getYear() + "/" ;
					String fileName = FilenameUtils.getBaseName(folderName)
					                + "." + FilenameUtils.getExtension(folderName);
					
					
					fileName = baseUrl + fileName; 
					//Upload local receipt file to s3
	             	s3_response = amazonS3Helper.uploadLocalFile(folderName,fileName,"assignment-files",baseUrl);
	             	if(!s3_response.get("status").equals("success")) { 
	             						response.setStatus("error");
	            						response.setMessage("Error. Failed to insert in S3 ");
	            						return response;  
		             }
			    }
			} catch (DocumentException | IOException e) {
				// TODO Auto-generated catch block
				
				response.setStatus("error");
				response.setMessage("Failed to Create Question PDF");
				return response;
			}
			
			flag = asignmentsDAO.adminApproveQp(masterTable,previewPath,filePath);
			if(flag==false) {
				response.setStatus("error");
				response.setMessage("Failed to Approve");
				return response;
			}
			try {
				asignmentsDAO.saveQpInAssignmentTable(masterTable,previewPath,filePath);
			} catch (Exception e) {
				
				response.setStatus("error");
				response.setMessage("Failed to save Assignments File");
				return response;
			}
			filePathList.add(previewPath); 
		} 
		response.setListOfStringData(filePathList);
		response.setStatus("success"); 
		return response;
	}
	public AssignmentFilesSetbean saveAssignmentFileInStagingTable(AssignmentFilesSetbean bean) {
		
		/*
		 * AssignmentFileBean bean = filesSet.getAssignmentFiles().get(0); String
		 * fileName = bean.getFileData().getOriginalFilename();
		 */
		AssignmentFilesSetbean response = new AssignmentFilesSetbean(); 
		
		if(bean.getQuestions().get(0) == null || "".equals(bean.getQuestions().get(0)) || "".equals(bean.getSubject()) || "".equals(bean.getConsumerTypeId())
			|| bean.getMarks().get(0) == null || "".equals(bean.getMarks().get(0)) 
				||  bean.getConsumerTypeId()  == null || bean.getSubject() == null || "".equals(bean.getSubject().trim()))
		{
			response.setStatus("error");
			response.setMessage("Invalid Qp File");
			return response;
		}
		String errorMessage=null;
		//String errorMessage = uploadAssignmentQpFile(bean, bean.getYear(), bean.getMonth());
		try { 
			//errorMessage = createQuestionFilePdf(bean,bean.getYear(), bean.getMonth());
		} catch (Exception e) {
			
			errorMessage=e.getMessage();
		}  
		try {  
			if(errorMessage==null) {
				asignmentsDAO.updateAssignmentQpFile(bean);
				response.setStatus("success");
				response.setMessage("Uploaded Successfully");
				return response;
			}
		} catch (Exception e) { 
			
		}
		response.setStatus("error");
		response.setMessage(errorMessage);
		return response;
	}
	private String uploadAssignmentQpFile(AssignmentFilesSetbean bean, String year, String month) {
		// TODO Auto-generated method stub
		return null;
	}
	public AssignmentFilesSetbean updateAssignmentStartDateEndDate(AssignmentFilesSetbean fileset) {
		AssignmentFilesSetbean response = new AssignmentFilesSetbean();
		try {
			asignmentsDAO.updateAssignmentStartDateEndDate(fileset);
			response.setStatus("success");
			response.setMessage("Approved Successfully");
			return response;
		} catch (Exception e) {
			
		} 
		response.setStatus("error");
		response.setMessage("Failed to Set Date");
		return response;
	}
	public AssignmentFilesSetbean setStartDateEndDateForStudent(AssignmentFilesSetbean item) {
		
		AssignmentFilesSetbean response = new AssignmentFilesSetbean();
		try {
			AssignmentFilesSetbean bean = checkStudentStartDateEndDateValid(item);
			if( bean.getStatus()!=null && bean.getStatus().equalsIgnoreCase("error")) {  
				response.setStatus("error");
				response.setMessage(bean.getMessage());
				return response;
			}
			asignmentsDAO.updateQpDateForStudent(item); 
		} catch (Exception e) { 
			
		} 
		return response;
	}
	 public AssignmentFilesSetbean checkStudentStartDateEndDateValid(AssignmentFilesSetbean filesSet) throws java.text.ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		Date startDate=sdf.parse(filesSet.getStudentStartDate().replaceAll("T", " "));
		Date endDate=sdf.parse(filesSet.getStudentEndDate().replaceAll("T", " "));
		Date dueDate=sdf.parse(filesSet.getDueDate().replaceAll("T", " "));
		Calendar now = Calendar.getInstance();
		Calendar sDate = Calendar.getInstance();
		Calendar eDate = Calendar.getInstance();
		Calendar dDate = Calendar.getInstance();
		sDate.setTime(startDate);
		eDate.setTime(endDate);
		dDate.setTime(dueDate); 
		if(eDate.before(now) || sDate.before(now)) { 
			filesSet.setStatus("error"); 
			filesSet.setMessage("Error in Assignment config, Invalid Start Date or End Date");
		}else if(eDate.before(sDate)) {
			filesSet.setStatus("error");
			filesSet.setMessage("Error in Assignment config, EndDate is before StartDate");
			
		}else if(sDate.before(dDate)) {
			filesSet.setStatus("error");
			filesSet.setMessage("Error in Assignment config, StartDate is before DueDate ");
		}
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			filesSet.setStudentStartDate(sdf1.format(startDate));
			filesSet.setStudentEndDate(sdf1.format(endDate));	  	
		return filesSet;
	} 
	 public String createQuestionFilePdf(String year,String month,String subject,List<AssignmentFilesSetbean> bean) throws DocumentException, IOException {
			String errorMsg=null; 
			String previewPath =month + year+"/" + RandomStringUtils.randomAlphanumeric(12) + ".pdf";;
		    
		 try {  
			    String filePath=ASSIGNMENT_REPORTS_GENERATE_PATH  +previewPath;
			    //bean.setFilePath(filePath);
				//bean.setQuestionFilePreviewPath(previewPath);
			 
				String folderPath = ASSIGNMENT_REPORTS_GENERATE_PATH + month + year + "/";
				
				File folder = new File(folderPath);
				if (!folder.exists()) {   
					folder.mkdirs();   
				}  
				
				OutputStream file = new FileOutputStream(new File(filePath));
				Document document = new Document(PageSize.A4);
				document.setMargins(80, 80, 30, 30);
				
				QpHeaderPageEvent event = new QpHeaderPageEvent();
				event.setSubject(subject);
				event.setYear(year);
				event.setMonth(month);
				PdfWriter writer = PdfWriter.getInstance(document, file); 
		        writer.setPageEvent(event);
		        document.open();
		        Paragraph marksPara = new Paragraph("Assignment Marks: 30", font9); 
				marksPara.setAlignment(Element.ALIGN_LEFT);
				document.add(marksPara);
				Paragraph blankLine = new Paragraph("\n");
				LineSeparator line = new LineSeparator();
				line.setOffset(-12);
		        document.add(line);
		        document.add(blankLine);
		         
		        Chunk insPara = new Chunk("Instructions", font9BoldItalic);
				document.add(insPara);
				
				Font zapfdingbats = new Font(FontFamily.ZAPFDINGBATS, 6); 
				Chunk bullet = new Chunk(String.valueOf((char) 108), zapfdingbats);
				 
				PdfPTable table = new PdfPTable(2);
				table.getDefaultCell().setBorder(0);
				table.setWidthPercentage(100f); 
				float[] columnWidths = new float[]{8f, 200f};
				table.setWidths(columnWidths); 
				table.setSpacingBefore(0);
				table.setHorizontalAlignment(Element.ALIGN_LEFT); 
				Paragraph bulletPara = new Paragraph();
				bulletPara.add(bullet);
				PdfPCell bulletCell = new PdfPCell(); 
				bulletCell.addElement(bulletPara);
				bulletCell.setBorder(0);
				bulletCell.setPadding(5);
				
				table.addCell(bulletCell); 
				Paragraph line1 = new Paragraph("All Questions carry equal marks", font9Italic);
				PdfPCell cell1 =new PdfPCell(); 
				cell1.addElement(line1);
				cell1.setBorder(0);
				cell1.setPadding(5);
				table.addCell(cell1); 
				table.completeRow();
				
				table.addCell(bulletCell);
				Paragraph line2 = new Paragraph("All Questions are compulsory", font9Italic);
				PdfPCell cell2 =new PdfPCell(); 
				cell2.addElement(line2);
				cell2.setBorder(0);
				cell2.setPadding(5);
				table.addCell(cell2); 
				table.completeRow();
				
				table.addCell(bulletCell);
				Paragraph line3 = new Paragraph("All answers to be explained in not more than 1000 words for question 1 and 2 and for " + 
			    		"question 3 in not more than 500 words for each subsection. Use relevant examples,illustrations as far as possible", font9Italic);
				PdfPCell cell3 =new PdfPCell(); 
				cell3.addElement(line3);
				cell3.setBorder(0);
				cell3.setPadding(5);
				table.addCell(cell3); 
				table.completeRow();
				
				
				table.addCell(bulletCell); 
				Paragraph line4 =new Paragraph(new Paragraph("All answers to be written individually. Discussion and group work is not advisable.", font9Italic)); 
				PdfPCell cell4 =new PdfPCell();
				cell4.addElement(line4);
				cell4.setBorder(0);
				cell4.setPadding(5);
				table.addCell(cell4);
				table.completeRow();
				
				table.addCell(bulletCell); 
				Paragraph line5 =new Paragraph("Students are free to refer to any books/reference material/website/internet for attempting " + 
						"their assignments, but are not allowed to copy the matter as it is from the source of " + 
						"reference. ", font9Italic); 
				PdfPCell cell5 =new PdfPCell(); 
				cell5.addElement(line5);
				cell5.setBorder(0);
				cell5.setPadding(5);
				table.addCell(cell5);
				table.completeRow();
				
				table.addCell(bulletCell); 
				Paragraph line6 =new Paragraph("Students should write the assignment in their own words. Copying of assignments from other " + 
						"students is not allowed ", font9Italic); 
				PdfPCell cell6 =new PdfPCell(); 
				cell6.addElement(line6);
				cell6.setBorder(0);
				cell6.setPadding(5);
				table.addCell(cell6);
				table.completeRow();
				
				table.addCell(bulletCell); 
				Paragraph line7 =new Paragraph("Students should follow the following parameter for answering the assignment questions", font9Italic); 
				PdfPCell cell7 =new PdfPCell(line7);
				cell7.addElement(line7);
				cell7.setBorder(0);
				cell7.setPadding(5);
				table.addCell(cell7);
				table.completeRow();
				
				document.add(table);
				document.add(blankLine);
				document.add(blankLine);
				 
				PdfPTable table1 = new PdfPTable(2); 
				table1.setWidthPercentage(40f); 
				float[] t1ColumnWidths = new float[]{70f, 30f};
				table1.setWidths(t1ColumnWidths); 
				table1.setSpacingBefore(0);
				table1.setHorizontalAlignment(Element.ALIGN_LEFT);  
				Paragraph theor = new Paragraph("For Theoretical Answer",font9Bold);
				theor.setAlignment(Element.ALIGN_CENTER);
				PdfPCell celltheor = new PdfPCell();
				celltheor.addElement(theor);
				celltheor.setColspan(2);
				table1.addCell(celltheor);
				Paragraph assess = new Paragraph("Assessment Parameter",font9Bold);
				table1.addCell(assess); 
				Paragraph weig = new Paragraph("Weightage",font9Bold);
				table1.addCell(weig);
				table1.completeRow();
				Paragraph intro = new Paragraph("Introduction",font9);
				table1.addCell(intro);
				Paragraph per1 = new Paragraph("20%",font9);
				table1.addCell(per1);
				table1.completeRow();
				PdfPCell cellcons = new PdfPCell();
				cellcons.addElement(new Paragraph("Concepts and Application\r\n"
						+ "related to the question",font9));
				table1.addCell(cellcons);
				Paragraph per2 = new Paragraph("60%",font9);
				table1.addCell(per2);
				table1.completeRow();
				PdfPCell cellconc = new PdfPCell();
				cellconc.setRowspan(2);
				cellconc.addElement(new Paragraph("Conclusion\n",font9));
				table1.addCell(cellconc);
				Paragraph per3 = new Paragraph("20%",font9);
				table1.addCell(per3);
				table1.completeRow(); 
				
				PdfPTable table2 = new PdfPTable(2); 
				table2.setWidthPercentage(40f); 
				float[] t2ColumnWidths = new float[]{70f, 30f};
				table2.setWidths(t2ColumnWidths); 
				table2.setSpacingBefore(0);
				table2.setHorizontalAlignment(Element.ALIGN_LEFT);  
				Paragraph numer = new Paragraph("For Numerical Answer",font9Bold);
				numer.setAlignment(Element.ALIGN_CENTER);
				PdfPCell cellnumer = new PdfPCell();
				cellnumer.addElement(numer);
				cellnumer.setColspan(2);
				table2.addCell(cellnumer); 
				table2.addCell(assess);  
				table2.addCell(weig);
				table2.completeRow();
				PdfPCell cellunder = new PdfPCell();
				cellunder.addElement( new Paragraph("Understanding and usage\r\n" + 
						"of the formula",font9));
				table2.addCell(cellunder); 
				table2.addCell(per1);
				table2.completeRow();
				PdfPCell cellproc = new PdfPCell(new Paragraph("Procedure / Steps ",font9));  
				table2.addCell(cellproc);
				Paragraph per4 = new Paragraph("50%",font9);
				table2.addCell(per4);
				table2.completeRow();
				Paragraph corr = new Paragraph("Correct Answer &\r\n" + 
						"Interpretation",font9);
				PdfPCell cellCorr = new PdfPCell();
				cellCorr.addElement(corr);
				table2.addCell(cellCorr);
				Paragraph per5 = new Paragraph("30%",font9);
				table2.addCell(per5);
				table2.completeRow(); 
				
				PdfPTable outerTable = new PdfPTable(3);  
				outerTable.setWidthPercentage(100f);
				float[] tmainColumnWidths = new float[]{50f,10F, 50f};
				outerTable.setWidths(tmainColumnWidths); 
				outerTable.getDefaultCell().setBorder(0); 
				table1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				outerTable.addCell(table1);
				PdfPCell emptyCell = new PdfPCell(new Paragraph());
				emptyCell.setBorder(0);
				outerTable.addCell(emptyCell);
				outerTable.addCell(table2);
				outerTable.completeRow();
				document.add(outerTable);
				
			    document.add(line);
		        document.add(blankLine);
		         
		        Chunk chunk1 = new Chunk("PLEASE NOTE: ",font10Bold);
				Chunk chunk2 = new Chunk(" This assignment is application based, you have to apply what you have\r\n" + 
		        		"learnt in this subject into real life scenario. You will find most of the information through\r\n" + 
		        		"internet search and the remaining from your common sense. None of the answers appear\r\n" + 
		        		"directly in the textbook chapters but are based on the content in the chapter",font10);
				chunk1.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
				Paragraph notePara = new Paragraph();
				notePara.add(chunk1);
				notePara.add(chunk2); 
				notePara.setSpacingAfter(4);
				document.add(notePara);
				document.add(blankLine);
				document.add(blankLine);
				document.add(blankLine); 
				PdfPTable tableQns = new PdfPTable(2);
				float[] tqColumnWidths = new float[]{5f,100F};
				tableQns.setWidths(tqColumnWidths);  
				tableQns.setWidthPercentage(100f); 
				for(AssignmentFilesSetbean qnBean : bean){
					Paragraph qnNoPara = new Paragraph(qnBean.getQnNo(),font10Bold);
					qnNoPara.setAlignment(Element.ALIGN_RIGHT);
					String question = qnBean.getQuestion();
					String mark = qnBean.getMark();
					
					PdfPCell qnNoCell = new PdfPCell(qnNoPara);
					PdfPCell qnCell = new PdfPCell();
					qnNoCell.setBorder(0);
					qnCell.setBorder(0);
					qnNoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					 
				    for (Element e : XMLWorkerHelper.parseToElementList(question, null)) {
				    	qnCell.addElement(e);
				    }
				    tableQns.addCell(qnNoCell);
				    tableQns.addCell(qnCell);
				    tableQns.completeRow(); 
					//document.add(tableMarks1);  
					Paragraph markPara = new Paragraph("("+mark+" Marks)",font10Bold);
					markPara.setAlignment(Element.ALIGN_RIGHT);
					PdfPCell marksCell = new PdfPCell(markPara); 
					marksCell.setBorder(0);
					marksCell.setColspan(2);
					marksCell.setHorizontalAlignment(Element.ALIGN_RIGHT); 
					tableQns.addCell(marksCell);
					tableQns.completeRow();  
			    }
				document.add(tableQns); 
				Paragraph endingPara = new Paragraph("***********",font10); 
				endingPara.setAlignment(Element.ALIGN_CENTER);
				document.add(endingPara);
		        document.close(); 
			} catch (FileNotFoundException e) {
				return e.getMessage();
			} catch (DocumentException e) {
				return e.getMessage();
			} catch (IOException e) {
				return e.getMessage();
			} catch (Exception e) {
				return e.getMessage();
			} 
	        return previewPath;
		} 
	 public AssignmentFilesSetbean saveAssignmentQuestionsInQpTable(AssignmentFilesSetbean bean) {
			
			AssignmentFilesSetbean response = new AssignmentFilesSetbean(); 
			
			if(bean.getQuestions()==null || bean.getQuestions().get(0) == null || "".equals(bean.getQuestions().get(0)) || "".equals(bean.getSubject())
				|| bean.getMarks().get(0) == null || "".equals(bean.getMarks().get(0)) || bean.getQnNos().get(0) == null || "".equals(bean.getQnNos().get(0))
					|| bean.getSubject() == null || "".equals(bean.getSubject().trim()))
			{
				response.setStatus("error");
				response.setMessage("Invalid Qp File");
				return response;
			}
			String errorMessage=null;
			//String errorMessage = uploadAssignmentQpFile(bean, bean.getYear(), bean.getMonth());
			try { 
				//errorMessage = createQuestionFilePdf(bean,bean.getYear(), bean.getMonth()); 
				if(bean.getQpId()!=null && !(bean.getQpId().equalsIgnoreCase(""))) {
			    	asignmentsDAO.deleteAssignmentQuestions(bean.getQpId());
			    }else {
			    	long primaryKey = asignmentsDAO.saveAssignmentQP(bean);
			    	bean.setQpId(primaryKey+"");
				    asignmentsDAO.updateQpIdInAsgStagingTable(bean);
			    }
			    asignmentsDAO.saveAssignmentQuestions(bean);
			} catch (Exception e) {
				
				errorMessage=e.getMessage();
			}  
			try {  
				if(errorMessage==null) {
					asignmentsDAO.updateUploadStatusInAsgStagingTable(bean);
					response.setStatus("success");
					response.setMessage("Uploaded Successfully");
					return response;
				}
			} catch (Exception e) { 
				
			}
			response.setStatus("error");
			response.setMessage(errorMessage);
			return response;
		}
	@Override
	public List<AssignmentFilesSetbean> getQuestionsByQpId(String qpId) {
		return asignmentsDAO.getQuestionsByQpId(qpId);
	} 
}
