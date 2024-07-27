package com.nmims.helpers;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.MBAPassFailBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.SubjectResultBean;
import com.nmims.beans.TimetableBean;
import com.nmims.daos.StudentMarksDAO;

public class PDDMTranscriptPDFCreator {
	Font font3 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
	Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	Font font4 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
	Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
	Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
	Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 10);
	Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 18);
	Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
	Font font9 = new Font(Font.FontFamily.TIMES_ROMAN, 10);
	Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, 7);
	Font font11 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
	Font font12= new Font(Font.FontFamily.TIMES_ROMAN, 9);
	
	private int marksObtained = 0;
	private double totalMarks = 0;

	public String createTrascriptForPDDM(List<PassFailExamBean> passList, StudentExamBean student, HashMap<String, ProgramExamBean> programCodeNameMap, 
			String HALLTICKET_PATH, String lastExamMonth, HashMap<String, BigDecimal> examOrderMap,HashMap<String,ArrayList<String>> semWiseSubjectMap, 
			Map<String, ProgramExamBean> programDetailMap,StudentMarksDAO dao, boolean isSoftcopy) throws Exception{
		Document document = new Document(PageSize.A4);
		String sapid = student.getSapid();
		String fileName = sapid + "_Trascript.pdf";
		try{ 
			String key = student.getProgram() + "-" + student.getPrgmStructApplicable(); 
			ProgramExamBean programDetails = programCodeNameMap.get(key); 
			String programType = programDetails.getProgramType();
			if(student.getLogoRequired()!=null && student.getLogoRequired().equalsIgnoreCase("Y")) {
				document.setMargins(30, 30, 10, 10);
			}else {
				document.setMargins(30, 30, 100, 25); //Left,right,top,bottom//
			}
			
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(HALLTICKET_PATH+fileName));

			document.open();
			if(student.getLogoRequired()!=null && student.getLogoRequired().equalsIgnoreCase("Y")) {
				generateHeaderTable(document, student);
			}else {
				generateHeaderTableWithoutLogo(document, student);
			}
			generateStudentInfoTable(passList, document, student, programCodeNameMap, writer, lastExamMonth,
					programDetailMap.get(student.getProgram()+"-"+student.getPrgmStructApplicable()).getProgramDuration(),
					programDetailMap.get(student.getProgram()+"-"+student.getPrgmStructApplicable()).getProgramDurationUnit(),dao);

			int noOfSemesters = Integer.valueOf(programDetailMap.get(student.getProgram()+"-"+student.getPrgmStructApplicable()).getNoOfSemesters());
				for(int i = 1; i <= noOfSemesters ;i++){
					generateSemWiseMarksTable(document, passList, student,i, examOrderMap,semWiseSubjectMap,dao);
				}
				generateFinalPercentage(document);
				if(isSoftcopy) {
					generateWatermarkAndCOESign(document, writer);
				}
				generateCOESign(document);

		}catch(Exception e){
			
			throw e;
		}finally{
			document.close(); 
		}
		return fileName;
	}


	private void generateWatermarkAndCOESign(Document document, PdfWriter writer) throws DocumentException {

		Phrase watermark = new Phrase("Transcript Downloaded from NGASCE Student Portal", new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
		PdfContentByte canvas = writer.getDirectContent();
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 300, 30);
	}


	private void generateFinalPercentage(Document document) throws DocumentException {
		double percentage = (double)((marksObtained * 100) / totalMarks);
	//	percentage = Math.round(percentage * 100.0) / 100.0;
		Paragraph remarksPara = new Paragraph("Percentage Obtained: " + percentage , font9);
		remarksPara.setAlignment(Element.ALIGN_LEFT);
		document.add(remarksPara);
		
	}


	private void generateCOETable(Document document) throws DocumentException {
		Paragraph coe = new Paragraph("Controller of Examinations " , font4);
		coe.setAlignment(Element.ALIGN_RIGHT);
		document.add(coe);
	}

	

	private void generateCOESign(Document document) throws Exception {
		try {
			Image signature = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/signature.jpg"));
			signature.scaleAbsolute(35, 22);
			
			
			signature.setAlignment(Element.ALIGN_LEFT);
			
			
			
			
			PdfPTable signatureTable = new PdfPTable(1);
			
		

			PdfPCell coeSignCell = new PdfPCell();
			coeSignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			coeSignCell.addElement(signature);

			coeSignCell.setBorder(0);

			
			signatureTable.addCell(coeSignCell);
			
			signatureTable.completeRow();
			
			document.add(signatureTable);
			
			
			Paragraph coe= new Paragraph("Controller of Examinations" , font4);
			coe.setAlignment(Element.ALIGN_LEFT);
			document.add(coe);

		} catch (Exception e) {
			
			throw e;
		}

	}

	private void generateNoteAndSignatureTable(Document document) throws Exception {
		try {
			PdfPTable noteTable = new PdfPTable(1);
			noteTable.getDefaultCell().setBorder(0);

			PdfPCell noteCell = new PdfPCell(new Paragraph("Disclaimer: I confirm that I have submitted the assignments of all the "
					+ "subjects for which I wish to appear at the examination. I understand that without submission of assignment/s, "
					+ "my appearance will be treated as null and void. In case I am desirous of instituting any legal proceedings "
					+ "against the University, I hereby agree that such legal proceedings shall be instituted "
					+ "only in courts at Mumbai under the jurisdiction of which the application is submitted by me and not in any other court.\n", font6));
			noteCell.setBorder(0);
			noteCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);



			noteTable.addCell(noteCell);

			noteTable.completeRow();

			noteTable.setWidthPercentage(100);
			noteTable.setSpacingAfter(5);

			PdfPTable signatureTable = new PdfPTable(1);

			PdfPCell emptyCell = new PdfPCell();
			emptyCell.setBorder(0);
			emptyCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			signatureTable.addCell(emptyCell);
			signatureTable.completeRow();

			PdfPCell signatureCell = new PdfPCell(new Paragraph("Signature of Student", font9));
			signatureCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

			signatureCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		

			signatureCell.setBorder(0);
		

		
			signatureTable.addCell(signatureCell);

			signatureTable.completeRow();

		
			signatureTable.setWidthPercentage(100);
			signatureTable.setSpacingBefore(20);

			document.add(noteTable);
			document.add(signatureTable);

		} catch (Exception e) {
			
			throw e;
		}
	}


	private void generateStudentInfoTable(List<PassFailExamBean> passList, Document document, StudentExamBean student,HashMap<String, ProgramExamBean> programCodeNameMap, 
			 PdfWriter writer, String lastExamMonthYear,String duration, String durationUnits,StudentMarksDAO dao) throws Exception {
		try {
			PdfPTable studentInfoTable = null;
			studentInfoTable = new PdfPTable(2);
			
			
			PdfPCell studentNameLabelCell = new PdfPCell(new Paragraph("Student Name: ", font11));
			PdfPCell studentNameValueCell = new PdfPCell(new Paragraph(student.getFirstName().toUpperCase()+" "+student.getLastName().toUpperCase() , font12));
			studentNameLabelCell.setFixedHeight(15f);
			studentInfoTable.addCell(studentNameLabelCell);
			studentInfoTable.addCell(studentNameValueCell);
			studentInfoTable.completeRow();
			
			
			PdfPCell studentNoLabelCell = new PdfPCell(new Paragraph("Student No: ", font11));
			PdfPCell studentNoValueCell = new PdfPCell(new Paragraph(student.getSapid(), font12));
			studentNoLabelCell.setFixedHeight(15f);
			studentInfoTable.addCell(studentNoLabelCell);
			studentInfoTable.addCell(studentNoValueCell);
			studentInfoTable.completeRow();
			
			PdfPCell studentFatherNameLabelCell = new PdfPCell(new Paragraph("Father Name: ", font11));
			PdfPCell studentFatherNameValueCell = new PdfPCell(new Paragraph(student.getFatherName().toUpperCase() , font12));
			studentFatherNameLabelCell.setFixedHeight(15f);
			studentInfoTable.addCell(studentFatherNameLabelCell);
			studentInfoTable.addCell(studentFatherNameValueCell);
			studentInfoTable.completeRow();
			
			PdfPCell studentMotherNameLabelCell = new PdfPCell(new Paragraph("Mother Name: ", font11));
			PdfPCell studentMotherNameValueCell = new PdfPCell(new Paragraph(student.getMotherName().toUpperCase() , font12));
			studentMotherNameLabelCell.setFixedHeight(15f);
			studentInfoTable.addCell(studentMotherNameLabelCell);
			studentInfoTable.addCell(studentMotherNameValueCell);
			studentInfoTable.completeRow();
			
			
			String key = student.getProgram() + "-" + student.getPrgmStructApplicable(); 
			ProgramExamBean programDetails = programCodeNameMap.get(key); 
			String programFullName = programDetails.getProgramname();
			PdfPCell programNameLabelCell = new PdfPCell(new Paragraph("Name of the Program/Instruction Medium", font11));
			//PdfPCell programNameValueCell = new PdfPCell(new Paragraph(programFullName+"/English", font12));
			PdfPCell programNameValueCell = new PdfPCell(new Paragraph(programFullName, font12));
			programNameLabelCell.setFixedHeight(15f);
			studentInfoTable.addCell(programNameLabelCell);
			studentInfoTable.addCell(programNameValueCell);
			studentInfoTable.completeRow();
			String programDuration = StringUtils.capitalize(duration)+" "+StringUtils.capitalize(durationUnits)+" (Distance Learning Mode)";		
			PdfPCell programDurationLabelCell = new PdfPCell(new Paragraph("Duration of the Program: ", font11));
			PdfPCell programDurationValueCell = new PdfPCell(new Paragraph(programDuration, font12));
			programDurationLabelCell.setFixedHeight(15f);
			studentInfoTable.addCell(programDurationLabelCell);
			studentInfoTable.addCell(programDurationValueCell);
			studentInfoTable.completeRow();
			
			String enrollmentMonth = student.getEnrollmentMonth();
			enrollmentMonth = enrollmentMonth.substring(0, 3);
			enrollmentMonth = enrollmentMonth.replaceAll("Jul", "July");
			enrollmentMonth = enrollmentMonth.replaceAll("Jan", "January");
			enrollmentMonth = enrollmentMonth.replaceAll("Apr", "April");
			if(student.getProgramCleared().equalsIgnoreCase("N")) {
				lastExamMonthYear = " - ";
			}
			PdfPCell enrollmentValueCell = new PdfPCell();
			enrollmentValueCell = new PdfPCell(new Paragraph(enrollmentMonth+"-"+student.getEnrollmentYear()+"/"+lastExamMonthYear, font12));
			PdfPCell enrollmentLabelCell = new PdfPCell(new Paragraph("Year of  Enrollment/Leaving: ", font11));
			enrollmentLabelCell.setFixedHeight(15f);
			studentInfoTable.addCell(enrollmentLabelCell);
			studentInfoTable.addCell(enrollmentValueCell);
			studentInfoTable.completeRow();
			studentInfoTable.setWidthPercentage(100);
			float[] columnWidths = {7f, 12.5f};
			studentInfoTable.setWidths(columnWidths);
			

			studentInfoTable.setSpacingBefore(4);
			studentInfoTable.setSpacingAfter(4);
			document.add(studentInfoTable);

			
			
		} catch (Exception e) {
			
			throw e;
		}
	}


	private void generateSemWiseMarksTable(Document document, List<PassFailExamBean> passList, StudentExamBean student, int semester, HashMap<String, BigDecimal> examOrderMap,HashMap<String,ArrayList<String>> semWiseSubjectMap,StudentMarksDAO dao) throws Exception {
		 
		try{
			int noOfSubjectsPassed = 0;
			int numberOfWaivedOffSubject = 0;
			int numberOfApplicableSubjectForSem = 0;
			String sem = String.valueOf(semester);
			for (int j = 0; j < passList.size(); j++) {
				PassFailExamBean bean = passList.get(j);
				if(!sem.equals(bean.getSem())){
					continue;
				}else{
					noOfSubjectsPassed++;
				}
			}

			if(semWiseSubjectMap.containsKey(sem)){
			numberOfApplicableSubjectForSem = semWiseSubjectMap.get(sem).size();
			}
			String semesterRomanNumeral;
			if(semester == 1) {
				semesterRomanNumeral = "I";
			} else if(semester == 2) {
				semesterRomanNumeral = "II";
			} else if(semester == 3) {
				semesterRomanNumeral = "III";
			} else if(semester == 4) {
				semesterRomanNumeral = "IV";
			}else {
				semesterRomanNumeral = "I";
			}
			Paragraph semesterNamePara = new Paragraph("Semester: " + semesterRomanNumeral, font8);
			semesterNamePara.setAlignment(Element.ALIGN_LEFT);
			document.add(semesterNamePara);
			if(noOfSubjectsPassed == 0 ){	
				Paragraph noticePara = new Paragraph("The student has still not passed the examination of Semester " + sem, font6);
				semesterNamePara.setAlignment(Element.ALIGN_LEFT);
				document.add(noticePara);
				return;
			}

			PdfPTable subjectsTable = new PdfPTable(6);

			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font4));
			PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subject", font4));
			PdfPCell monthYearCell = new PdfPCell(new Paragraph("Month & Year of Examination", font4));
			PdfPCell maxMarksCell = new PdfPCell(new Paragraph("Maximum Marks", font4));
			PdfPCell minMarksCell = new PdfPCell(new Paragraph("Minimum Marks", font4));
			PdfPCell totalMarksCell = new PdfPCell(new Paragraph("Marks Obtained", font4));

			srNoCell.setFixedHeight(30);
			srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
			subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
			monthYearCell.setVerticalAlignment(Element.ALIGN_TOP);
			maxMarksCell.setVerticalAlignment(Element.ALIGN_TOP);
			minMarksCell.setVerticalAlignment(Element.ALIGN_TOP);
			totalMarksCell.setVerticalAlignment(Element.ALIGN_TOP);


			srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			monthYearCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			maxMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			minMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			subjectsTable.addCell(srNoCell);
			subjectsTable.addCell(subjectsCell);
			subjectsTable.addCell(monthYearCell);
			subjectsTable.addCell(maxMarksCell);
			subjectsTable.addCell(minMarksCell);
			subjectsTable.addCell(totalMarksCell);
			subjectsTable.completeRow();


			int count = 1;
			int numberOfSubjectsPassed = 0;
			int total=0;
			for (int j = 0; j < passList.size(); j++) {
				PassFailExamBean bean = passList.get(j);
				if(!sem.equals(bean.getSem())){
					continue;
				}
				//if(bean.getTotal().isEmpty() || StringUtils.isBlank(bean.getTotal())){
				if( StringUtils.isNumeric(bean.getTotal())){
					marksObtained += 0;
				}else {
					marksObtained += (int)Double.parseDouble(bean.getTotal());

				}
				totalMarks += 100.0;
				
				srNoCell = new PdfPCell(new Paragraph(count+"", font6));
				subjectsCell = new PdfPCell(new Paragraph(bean.getSubject(), font6));
				String examMonthYear = getLatestExamMonthYear( bean);
				monthYearCell = new PdfPCell(new Paragraph(examMonthYear, font6));
				
				maxMarksCell = new PdfPCell(new Paragraph("100", font6));
				minMarksCell = new PdfPCell(new Paragraph("50", font6));
				//totalMarksCell = new PdfPCell(new Paragraph(bean.getTotal(), font6));
			    int	totalMarksObtain= (int)Double.parseDouble(bean.getTotal());
				totalMarksCell = new PdfPCell(new Paragraph("" + totalMarksObtain ,font6));
				
				
				
				srNoCell.setFixedHeight(18f);
				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER); srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				monthYearCell.setHorizontalAlignment(Element.ALIGN_CENTER); monthYearCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				maxMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER); maxMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				minMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER); minMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);  totalMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE); 

				subjectsTable.addCell(srNoCell);
				subjectsTable.addCell(subjectsCell);
				subjectsTable.addCell(monthYearCell);
				subjectsTable.addCell(maxMarksCell);
				subjectsTable.addCell(minMarksCell);
				subjectsTable.addCell(totalMarksCell);
				subjectsTable.completeRow();

				count++;
				
				if("Y".equalsIgnoreCase(bean.getIsPass())){
					numberOfSubjectsPassed++;
				}
			}

			subjectsTable.setWidthPercentage(100);
			subjectsTable.setSpacingBefore(5f);
			subjectsTable.setSpacingAfter(5f);
			float[] marksColumnWidths = {1f, 6f, 5f, 2.5f, 2.5f,2.5f};
			subjectsTable.setWidths(marksColumnWidths);
			document.add(subjectsTable);
			Paragraph remarksPara = new Paragraph("Remarks: " + getPassFailStatus(student, numberOfSubjectsPassed, sem,numberOfWaivedOffSubject, dao) , font9);
			remarksPara.setAlignment(Element.ALIGN_LEFT);
			document.add(remarksPara);

		}catch(Exception e){
			
			throw e;
		}
	}

	private String getLatestExamMonthYear(PassFailExamBean bean) {
		String examMonthYearToConsiderForTranscript = "";
			examMonthYearToConsiderForTranscript =  bean.getExamMonth()+ "-" + bean.getExamYear();
		examMonthYearToConsiderForTranscript = examMonthYearToConsiderForTranscript.replaceAll("Apr", "April");
		examMonthYearToConsiderForTranscript = examMonthYearToConsiderForTranscript.replaceAll("Mar", "March");
		examMonthYearToConsiderForTranscript = examMonthYearToConsiderForTranscript.replaceAll("Jun", "June");
		examMonthYearToConsiderForTranscript = examMonthYearToConsiderForTranscript.replaceAll("Jul", "July");
		examMonthYearToConsiderForTranscript = examMonthYearToConsiderForTranscript.replaceAll("Sep", "September");
		examMonthYearToConsiderForTranscript = examMonthYearToConsiderForTranscript.replaceAll("Oct", "October");
		examMonthYearToConsiderForTranscript = examMonthYearToConsiderForTranscript.replaceAll("Dec", "December");
		return examMonthYearToConsiderForTranscript;
	}


	private String getPassFailStatus(StudentExamBean student,int numberOfSubjectsPassed,String sem,int numberOfWaivedOffSubject,StudentMarksDAO dao) {
		String program = student.getProgram();
		String programStructure = student.getPrgmStructApplicable();
		int noOfSubjectsToClearSem = 0;
		int noOfApplicableSubjectsToClearSem = 0;
		HashMap<String,ProgramsBean> programInfoList = dao.getProgramDetailsMap();
		ProgramsBean bean = programInfoList.get(student.getConsumerProgramStructureId());
		noOfApplicableSubjectsToClearSem = Integer.parseInt(bean.getNoOfSubjectsToClearSem().trim());	
		if(noOfApplicableSubjectsToClearSem == numberOfSubjectsPassed){
			return "Pass";
		}else {
			return "Fail";
		}
	}


	private void generateHeaderTable(Document document, StudentExamBean student) throws Exception {
		Image logo = null;
		try {
			logo = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nmims_logo_new.jpg"));

			logo.scaleAbsolute(142,40);
			logo.setAlignment(Element.ALIGN_CENTER);
			SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
			String fileDate = sdfr.format(new Date());
			Paragraph datePara = new Paragraph("Date: "+fileDate, font6);
			
			
			Paragraph emptyPara = new Paragraph(" ");
			datePara.setAlignment(Element.ALIGN_CENTER);
			document.add(emptyPara);
			
			datePara.setAlignment(Element.ALIGN_RIGHT);
			document.add(datePara);

			PdfPTable headerTable = new PdfPTable(1);

			PdfPCell logoCell = new PdfPCell();
			logoCell.addElement(logo);
			logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			logoCell.setBorder(0);
			headerTable.addCell(logoCell);
			headerTable.completeRow();
			headerTable.setWidthPercentage(100);
			headerTable.setSpacingBefore(10);
			headerTable.setSpacingAfter(10);
			document.add(headerTable);
			
			String title = "TRANSCRIPT";
			
			Chunk titleChunk = new Chunk(title, font2);
			titleChunk.setUnderline(0.1f, -2f);
			Paragraph titlePara = new Paragraph();
			titlePara.add(titleChunk);
			titlePara.setAlignment(Element.ALIGN_CENTER);
			document.add(titlePara);
			
			

		} catch (Exception e) {
			
			throw e;
		}
	}
	private void generateHeaderTableWithoutLogo(Document document, StudentExamBean student) throws Exception {
		Image logo = null;
		try {
			SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
			String fileDate = sdfr.format(new Date());
			Paragraph datePara = new Paragraph("Date: "+fileDate, font6);
			
			Paragraph emptyPara = new Paragraph(" ");
			datePara.setAlignment(Element.ALIGN_CENTER);
			document.add(emptyPara);
			
			datePara.setAlignment(Element.ALIGN_RIGHT);
			document.add(datePara);

			PdfPTable headerTable = new PdfPTable(2);
			headerTable.completeRow();
			headerTable.setWidthPercentage(100);
			headerTable.setSpacingBefore(10);
			headerTable.setSpacingAfter(10);
			document.add(headerTable);
			datePara.setAlignment(Element.ALIGN_CENTER);
			document.add(emptyPara);
			
			
			String title = "TRANSCRIPT";
			
			Chunk titleChunk = new Chunk(title, font2);
			titleChunk.setUnderline(0.1f, -2f);
			Paragraph titlePara = new Paragraph();
			titlePara.add(titleChunk);
			titlePara.setAlignment(Element.ALIGN_CENTER);
			document.add(titlePara);
			
			

		} catch (Exception e) {
			
			throw e;
		}
	}
	public String createAttendanceSheet(ArrayList<ExamBookingTransactionBean> bookingsList, String mostRecentTimetablePeriod, String HALLTICKET_PATH,
			String userId, ExamBookingTransactionBean bean) throws Exception{
		Document document = new Document(PageSize.A4);
		String subject = bean.getSubject().replaceAll("," , "_");
		String centerName = bean.getExamCenterName().replaceAll("," , "_");

		
		String fileName = centerName + "_"+ bean.getYear() + "_"+ bean.getMonth() + "_" + subject + "_Attendance_Sheet.pdf";
		try{

			document.setMargins(30, 30, 20, 10);

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(HALLTICKET_PATH+"\\"+fileName));

			document.open();
			String examMonth = null;
			if(mostRecentTimetablePeriod.contains("Dec")){
				examMonth = mostRecentTimetablePeriod.replace("Dec", "December");
			}else if(mostRecentTimetablePeriod.contains("Jun")){
				examMonth = mostRecentTimetablePeriod.replace("Jun", "June");
			}
			int totalPages = (bookingsList.size() / 15) +  ((bookingsList.size() % 15) == 0 ? 0 : 1);
			int pageNo = 1;
			for (int i = 0; i < bookingsList.size(); i=i+15) {
				document.newPage();
				generateAttandanceHeaderTable(document, examMonth);
				generateSubjectInformationTable(document, bean);
				generateStudentsListTable((i+1),document, bookingsList.subList(i, (bookingsList.size() < i+15)? bookingsList.size() : (i+15)));
				
				Phrase pageNoPhase = new Phrase("Page "+pageNo + " of "+ totalPages, new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK));
				PdfContentByte canvas = writer.getDirectContent();
				ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, pageNoPhase, 300, 10, 0);
				pageNo++;
			}
			
			generateAttendanceFooter(document);


		}catch(Exception e){
			
			throw e;
		}finally{
			document.close(); 
		}
		return fileName;
		
	}
	
	private void generateAttendanceFooter(Document document) {
		try {
			PdfPTable footerTable = new PdfPTable(4);
			
			PdfPCell supervisedByLabelCell = new PdfPCell(new Paragraph("Supervised By:", font9));
			PdfPCell supervisedByCell = new PdfPCell();
			PdfPCell noOfStudentsLabelCell = new PdfPCell(new Paragraph("A) No. Of Students Present:", font9));
			PdfPCell noOfStudentsCell = new PdfPCell();
			
			supervisedByLabelCell.setBorder(0);
			supervisedByCell.setBorder(0);
			noOfStudentsLabelCell.setBorder(0);
			noOfStudentsCell.setBorder(0);
			
			supervisedByLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			noOfStudentsLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			supervisedByLabelCell.setFixedHeight(40);
			
			footerTable.addCell(supervisedByLabelCell);
			footerTable.addCell(supervisedByCell);
			footerTable.addCell(noOfStudentsLabelCell);
			footerTable.addCell(noOfStudentsCell);
			footerTable.completeRow();
			
			//Second Row
			PdfPCell nameLabelCell = new PdfPCell(new Paragraph("Name:", font9));
			PdfPCell nameCell = new PdfPCell();
			PdfPCell noOfStudentsAbsentLabelCell = new PdfPCell(new Paragraph("B) No. Of Students Absent:", font9));
			PdfPCell noOfStudentsAbsentCell = new PdfPCell();
			
			nameLabelCell.setBorder(0);
			nameCell.setBorder(0);
			noOfStudentsAbsentLabelCell.setBorder(0);
			noOfStudentsAbsentCell.setBorder(0);
			
			nameLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			noOfStudentsAbsentLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			nameLabelCell.setFixedHeight(40);
			
			footerTable.addCell(nameLabelCell);
			footerTable.addCell(nameCell);
			footerTable.addCell(noOfStudentsAbsentLabelCell);
			footerTable.addCell(noOfStudentsAbsentCell);
			footerTable.completeRow();
			
			
			//Third Row
			PdfPCell signatureLabelCell = new PdfPCell(new Paragraph("Signature:", font9));
			PdfPCell signatureCell = new PdfPCell();
			PdfPCell noOfOnTheSpotLabelCell = new PdfPCell(new Paragraph("C) No. Of On-Spot Regn:", font9));
			PdfPCell noOfOnTheSpotCell = new PdfPCell();
			
			signatureLabelCell.setBorder(0);
			signatureCell.setBorder(0);
			noOfOnTheSpotLabelCell.setBorder(0);
			noOfOnTheSpotCell.setBorder(0);
			
			signatureLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			noOfOnTheSpotLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			signatureLabelCell.setFixedHeight(40);
			
			footerTable.addCell(signatureLabelCell);
			footerTable.addCell(signatureCell);
			footerTable.addCell(noOfOnTheSpotLabelCell);
			footerTable.addCell(noOfOnTheSpotCell);
			footerTable.completeRow();
			
			//Forth Row
			PdfPCell dateLabelCell = new PdfPCell(new Paragraph("Date:", font9));
			PdfPCell dateCell = new PdfPCell();
			PdfPCell totalStudentsLabelCell = new PdfPCell(new Paragraph("Total No. Of Students Appeared (A+B+C):", font9));
			PdfPCell totalStudentsCell = new PdfPCell();
			
			dateLabelCell.setBorder(0);
			dateCell.setBorder(0);
			totalStudentsLabelCell.setBorder(0);
			totalStudentsCell.setBorder(0);
			
			dateLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			totalStudentsLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			dateLabelCell.setFixedHeight(40);
			
			footerTable.addCell(dateLabelCell);
			footerTable.addCell(dateCell);
			footerTable.addCell(totalStudentsLabelCell);
			footerTable.addCell(totalStudentsCell);
			footerTable.completeRow();
			

			//Fifth Row
			PdfPCell centerHeadLabelCell = new PdfPCell(new Paragraph("Centre Head:", font9));
			PdfPCell centerHeadCell = new PdfPCell();
			PdfPCell centreSealLabelCell = new PdfPCell(new Paragraph("Centre Seal:", font9));
			PdfPCell centreSealCell = new PdfPCell();
			
			centerHeadLabelCell.setBorder(0);
			centerHeadCell.setBorder(0);
			centreSealLabelCell.setBorder(0);
			centreSealCell.setBorder(0);
			
			centerHeadLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			centreSealLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			centerHeadLabelCell.setFixedHeight(40);
			
			
			footerTable.addCell(centerHeadLabelCell);
			footerTable.addCell(centerHeadCell);
			footerTable.addCell(centreSealLabelCell);
			footerTable.addCell(centreSealCell);
			footerTable.completeRow();
			
			
			
			float[] columnWidths = {3f, 4f, 6f, 3f};
			footerTable.setWidths(columnWidths);
			footerTable.setWidthPercentage(100);

			
			footerTable.setSpacingBefore(10);
			footerTable.setSpacingAfter(10);
			document.add(footerTable);
			
		} catch (Exception e) {
			
		}
		
	}


	private void generateStudentsListTable(int count, Document document,List<ExamBookingTransactionBean> bookingsList) {
		try{

			PdfPTable studentsTable = new PdfPTable(6);

			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font2));
			PdfPCell sapidCell = new PdfPCell(new Paragraph("Student No", font2));
			PdfPCell programCell = new PdfPCell(new Paragraph("Program", font2));
			PdfPCell studentNameCell = new PdfPCell(new Paragraph("Student Name", font2));
			PdfPCell signatureCell = new PdfPCell(new Paragraph("Signature", font2));
			PdfPCell supplementsCell = new PdfPCell(new Paragraph("No. of Suppl.", font2));


			srNoCell.setFixedHeight(30);
			srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
			sapidCell.setVerticalAlignment(Element.ALIGN_TOP);
			programCell.setVerticalAlignment(Element.ALIGN_TOP);
			studentNameCell.setVerticalAlignment(Element.ALIGN_TOP);
			signatureCell.setVerticalAlignment(Element.ALIGN_TOP);
			supplementsCell.setVerticalAlignment(Element.ALIGN_TOP);

			srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			sapidCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			programCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			studentNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			signatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			supplementsCell.setHorizontalAlignment(Element.ALIGN_CENTER);


			studentsTable.addCell(srNoCell);
			studentsTable.addCell(sapidCell);
			studentsTable.addCell(programCell);
			studentsTable.addCell(studentNameCell);
			studentsTable.addCell(signatureCell);
			studentsTable.addCell(supplementsCell);

			studentsTable.completeRow();
	
			for (int j = 0; j < bookingsList.size(); j++) {
				ExamBookingTransactionBean bean = bookingsList.get(j);

				srNoCell = new PdfPCell(new Paragraph(count+"", font9));
				sapidCell = new PdfPCell(new Paragraph(bean.getSapid(), font9));
				programCell = new PdfPCell(new Paragraph(bean.getProgram(), font9));
				studentNameCell = new PdfPCell(new Paragraph(bean.getLastName().toUpperCase()+" "+bean.getFirstName().toUpperCase(), font9));
				signatureCell = new PdfPCell();
				supplementsCell = new PdfPCell();

				srNoCell.setFixedHeight(30);
				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER); srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				sapidCell.setHorizontalAlignment(Element.ALIGN_CENTER); sapidCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				programCell.setHorizontalAlignment(Element.ALIGN_CENTER); programCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				studentNameCell.setHorizontalAlignment(Element.ALIGN_LEFT); studentNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

				studentsTable.addCell(srNoCell);
				studentsTable.addCell(sapidCell);
				studentsTable.addCell(programCell);
				studentsTable.addCell(studentNameCell);
				studentsTable.addCell(signatureCell);
				studentsTable.addCell(supplementsCell);

				studentsTable.completeRow();


				count++;
			}

			studentsTable.setWidthPercentage(100);
			studentsTable.setSpacingBefore(5f);
			studentsTable.setSpacingAfter(1f);
			float[] marksColumnWidths = {1f, 3f, 2f, 4f, 5f, 2f};
			studentsTable.setWidths(marksColumnWidths);
			document.add(studentsTable);

		}catch(Exception e){
			
		}
		
	}


	private void generateSubjectInformationTable(Document document,	ExamBookingTransactionBean bean) throws DocumentException {
		PdfPTable headerTable = new PdfPTable(4);
		
		PdfPCell centerLabelCell = new PdfPCell(new Paragraph("Center:", font9));
		PdfPCell centerCell = new PdfPCell(new Paragraph(bean.getExamCenterName(), font9));
		PdfPCell semLabelCell = new PdfPCell(new Paragraph("SEMESTER:", font9));
		PdfPCell semCell = new PdfPCell(new Paragraph(bean.getSem(), font9));
		
		centerLabelCell.setBorder(0);
		centerCell.setBorder(0);
		semLabelCell.setBorder(0);
		semCell.setBorder(0);
		
		centerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		semCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		headerTable.addCell(centerLabelCell);
		headerTable.addCell(centerCell);
		headerTable.addCell(semLabelCell);
		headerTable.addCell(semCell);
		headerTable.completeRow();
		
		
		PdfPCell subjectLabelCell = new PdfPCell(new Paragraph("Subject:", font9));
		PdfPCell subjectCell = new PdfPCell(new Paragraph(bean.getSubject(), font9));
		PdfPCell dateTimeLabelCell = new PdfPCell(new Paragraph("Date & Time:", font9));
		PdfPCell dateTimeCell = new PdfPCell(new Paragraph(bean.getExamDate() +", " + bean.getExamTime().substring(0,5) +" To "+ 
		bean.getExamEndTime().substring(0,5), font9));
		
		subjectLabelCell.setBorder(0);
		subjectCell.setBorder(0);
		dateTimeLabelCell.setBorder(0);
		dateTimeCell.setBorder(0);
		
		subjectCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		dateTimeCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		headerTable.addCell(subjectLabelCell);
		headerTable.addCell(subjectCell);
		headerTable.addCell(dateTimeLabelCell);
		headerTable.addCell(dateTimeCell);
		headerTable.completeRow();
		
		float[] columnWidths = {0.8f, 5f, 1.3f, 4f};
		headerTable.setWidths(columnWidths);
		headerTable.setWidthPercentage(100);

		
		headerTable.setSpacingBefore(10);
		headerTable.setSpacingAfter(5);
		document.add(headerTable);
	}


	private void generateAttandanceHeaderTable(Document document, String mostRecentTimetablePeriod) {
		try {


			Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font3);
			collegNameChunk.setUnderline(0.1f, -2f);
			Paragraph collegeName = new Paragraph();
			collegeName.add(collegNameChunk);
			collegeName.setAlignment(Element.ALIGN_CENTER);
			document.add(collegeName);

			Chunk stmtOfMarksChunk = new Chunk("ATTENDANCE SHEET OF  "+mostRecentTimetablePeriod+" Exam", font2);
			stmtOfMarksChunk.setUnderline(0.1f, -2f);
			Paragraph stmtOfMarksPara = new Paragraph();
			stmtOfMarksPara.add(stmtOfMarksChunk);
			stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
			document.add(stmtOfMarksPara);

		} catch (Exception e) {
			
		}

	}
	
	public String createEvaluationSheet(ArrayList<ExamBookingTransactionBean> bookingsList, String mostRecentTimetablePeriod, String HALLTICKET_PATH,
			String userId, ExamBookingTransactionBean bean) throws Exception{
		Document document = new Document(PageSize.A4);
		
		String subject = bean.getSubject().replaceAll("," , "_");
		String centerName = bean.getExamCenterName().replaceAll("," , "_");
		
		String fileName = centerName + "_"+ bean.getYear() + "_"+ bean.getMonth() + "_" + subject + "_Evaluation_Sheet.pdf";
		try{

			document.setMargins(30, 30, 20, 10);

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(HALLTICKET_PATH+"\\"+fileName));

			document.open();
			String examMonth = null;
			if(mostRecentTimetablePeriod.contains("Dec")){
				examMonth = mostRecentTimetablePeriod.replace("Dec", "December");
			}else if(mostRecentTimetablePeriod.contains("Jun")){
				examMonth = mostRecentTimetablePeriod.replace("Jun", "June");
			}
			int totalPages = (bookingsList.size() / 20) +  ((bookingsList.size() % 20) == 0 ? 0 : 1);
			int pageNo = 1;
			for (int i = 0; i < bookingsList.size(); i=i+20) {
				document.newPage();
				generateEvaluationHeaderTable(document, examMonth);
				generateSubjectInformationTable(document, bean);
				generateStudentsMarksTable(bean, (i+1),document, bookingsList.subList(i, (bookingsList.size() < i+20)? bookingsList.size() : (i+20)));
				
				Phrase pageNoPhase = new Phrase("Page "+pageNo + " of "+ totalPages, new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK));
				PdfContentByte canvas = writer.getDirectContent();
				ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, pageNoPhase, 300, 10, 0);
				pageNo++;
			}
			generateEvaluationFooter(document);

		}catch(Exception e){
			
			throw e;
		}finally{
			document.close(); 
		}
		return fileName;
		
	}
	
	private void generateEvaluationHeaderTable(Document document, String mostRecentTimetablePeriod) {
		try {

			Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font3);
			collegNameChunk.setUnderline(0.1f, -2f);
			Paragraph collegeName = new Paragraph();
			collegeName.add(collegNameChunk);
			collegeName.setAlignment(Element.ALIGN_CENTER);
			document.add(collegeName);

			Chunk stmtOfMarksChunk = new Chunk("EVALUATION SHEET OF  "+mostRecentTimetablePeriod+" Exam", font2);
			stmtOfMarksChunk.setUnderline(0.1f, -2f);
			Paragraph stmtOfMarksPara = new Paragraph();
			stmtOfMarksPara.add(stmtOfMarksChunk);
			stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
			document.add(stmtOfMarksPara);

		} catch (Exception e) {
			
		}

	}
	
	private void generateStudentsMarksTable(ExamBookingTransactionBean searchBean, int count, Document document,List<ExamBookingTransactionBean> bookingsList) {
		try{

			PdfPTable studentsTable = new PdfPTable(5);

			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font2));
			PdfPCell sapidCell = new PdfPCell(new Paragraph("Student No", font2));
			PdfPCell programCell = new PdfPCell(new Paragraph("Program", font2));
			PdfPCell studentNameCell = new PdfPCell(new Paragraph("Student Name", font2));
			
			String program = searchBean.getProgram();
			PdfPCell marksCell = null;
			
			if("PGDMM-MLI".equals(program)){
				marksCell = new PdfPCell(new Paragraph("Marks out of 50", font2));
			}else{
				marksCell = new PdfPCell(new Paragraph("Marks out of 70", font2));
			}
			

			srNoCell.setFixedHeight(25);
			srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
			sapidCell.setVerticalAlignment(Element.ALIGN_TOP);
			programCell.setVerticalAlignment(Element.ALIGN_TOP);
			studentNameCell.setVerticalAlignment(Element.ALIGN_TOP);
			marksCell.setVerticalAlignment(Element.ALIGN_TOP);


			srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			sapidCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			programCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			studentNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			marksCell.setHorizontalAlignment(Element.ALIGN_CENTER);


			studentsTable.addCell(srNoCell);
			studentsTable.addCell(sapidCell);
			studentsTable.addCell(programCell);
			studentsTable.addCell(studentNameCell);
			studentsTable.addCell(marksCell);

			studentsTable.completeRow();
	
			for (int j = 0; j < bookingsList.size(); j++) {
				ExamBookingTransactionBean bean = bookingsList.get(j);

				srNoCell = new PdfPCell(new Paragraph(count+"", font9));
				sapidCell = new PdfPCell(new Paragraph(bean.getSapid(), font9));
				programCell = new PdfPCell(new Paragraph(bean.getProgram(), font9));
				studentNameCell = new PdfPCell(new Paragraph(bean.getLastName().toUpperCase()+" "+bean.getFirstName().toUpperCase(), font9));
				marksCell = new PdfPCell(new Paragraph(bean.getWritenscore(), font1));

				srNoCell.setFixedHeight(25);
				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER); srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				sapidCell.setHorizontalAlignment(Element.ALIGN_CENTER); sapidCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				programCell.setHorizontalAlignment(Element.ALIGN_CENTER); programCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				studentNameCell.setHorizontalAlignment(Element.ALIGN_LEFT); studentNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				marksCell.setHorizontalAlignment(Element.ALIGN_CENTER); marksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

				studentsTable.addCell(srNoCell);
				studentsTable.addCell(sapidCell);
				studentsTable.addCell(programCell);
				studentsTable.addCell(studentNameCell);
				studentsTable.addCell(marksCell);

				studentsTable.completeRow();


				count++;
			}

			studentsTable.setWidthPercentage(100);
			studentsTable.setSpacingBefore(5f);
			studentsTable.setSpacingAfter(2f);
			float[] marksColumnWidths = {1f, 3f, 2f, 4f, 5f};
			studentsTable.setWidths(marksColumnWidths);
			document.add(studentsTable);

		}catch(Exception e){
			
		}
		
	}
	
	private void generateEvaluationFooter(Document document) {
		try {
			PdfPTable footerTable = new PdfPTable(2);
			
			PdfPCell nameOfFacultyLabelCell = new PdfPCell(new Paragraph("NAME OF THE FACULTY:", font9));
			PdfPCell nameOfFacultyCell = new PdfPCell();

			
			nameOfFacultyLabelCell.setBorder(0);
			nameOfFacultyCell.setBorder(0);

			
			nameOfFacultyLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			nameOfFacultyLabelCell.setFixedHeight(40);
			
			footerTable.addCell(nameOfFacultyLabelCell);
			footerTable.addCell(nameOfFacultyCell);
			footerTable.completeRow();
			
			//Second Row
			PdfPCell facultySignatureLabelCell = new PdfPCell(new Paragraph("SIGNATURE OF THE FACULTY:", font9));
			PdfPCell facultySignatureCell = new PdfPCell();

			
			facultySignatureLabelCell.setBorder(0);
			facultySignatureCell.setBorder(0);

			facultySignatureLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			facultySignatureLabelCell.setFixedHeight(40);
			
			footerTable.addCell(facultySignatureLabelCell);
			footerTable.addCell(facultySignatureCell);
			footerTable.completeRow();
			
			
			//Third Row
			PdfPCell dateLabelCell = new PdfPCell(new Paragraph("DATE:", font9));
			PdfPCell dateCell = new PdfPCell();

			dateLabelCell.setBorder(0);
			dateCell.setBorder(0);

			
			dateLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			dateLabelCell.setFixedHeight(40);
			
			footerTable.addCell(dateLabelCell);
			footerTable.addCell(dateCell);
			footerTable.completeRow();
			
			float[] columnWidths = {5f, 10f};
			footerTable.setWidths(columnWidths);
			footerTable.setWidthPercentage(100);

			
			footerTable.setSpacingBefore(10);
			footerTable.setSpacingAfter(10);
			document.add(footerTable);
			
		} catch (Exception e) {
			
		}
		
	}


	public String createMarksCheckingSheet(	ArrayList<ExamBookingTransactionBean> marksList, String mostRecentTimetablePeriod, String HALLTICKET_PATH, String userId, ExamBookingTransactionBean bean) throws Exception {
		Document document = new Document(PageSize.A4);
		
		String subject = bean.getSubject().replaceAll("," , "_");
		String centerName = bean.getExamCenterName().replaceAll("," , "_");
		
		String fileName = centerName + "_"+ bean.getYear() + "_"+ bean.getMonth() + "_" + subject + "_Marks_Check_Sheet.pdf";
		try{

			document.setMargins(30, 30, 20, 10);

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(HALLTICKET_PATH+"\\"+fileName));

			document.open();
			String examMonth = null;
			if(mostRecentTimetablePeriod.contains("Dec")){
				examMonth = mostRecentTimetablePeriod.replace("Dec", "December");
			}else if(mostRecentTimetablePeriod.contains("Jun")){
				examMonth = mostRecentTimetablePeriod.replace("Jun", "June");
			}
			int totalPages = (marksList.size() / 20) +  ((marksList.size() % 20) == 0 ? 0 : 1);
			int pageNo = 1;
			for (int i = 0; i < marksList.size(); i=i+20) {
				document.newPage();
				generateMarksCheckHeaderTable(document, examMonth);
				generateSubjectInformationTable(document, bean);
				generateStudentsMarksTable(bean, (i+1),document, marksList.subList(i, (marksList.size() < i+20)? marksList.size() : (i+20)));
				
				Phrase pageNoPhase = new Phrase("Page "+pageNo + " of "+ totalPages, new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK));
				PdfContentByte canvas = writer.getDirectContent();
				ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, pageNoPhase, 300, 10, 0);
				pageNo++;
			}
			generateMarksCheckFooter(document);

		}catch(Exception e){
			
			throw e;
		}finally{
			document.close(); 
		}
		return fileName;
	}
	
	
	private void generateMarksCheckHeaderTable(Document document, String mostRecentTimetablePeriod) {
		try {

			Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font3);
			collegNameChunk.setUnderline(0.1f, -2f);
			Paragraph collegeName = new Paragraph();
			collegeName.add(collegNameChunk);
			collegeName.setAlignment(Element.ALIGN_CENTER);
			document.add(collegeName);

			Chunk stmtOfMarksChunk = new Chunk("TERM END MARKS CHECKING REPORT OF "+mostRecentTimetablePeriod+" Exam", font2);
			stmtOfMarksChunk.setUnderline(0.1f, -2f);
			Paragraph stmtOfMarksPara = new Paragraph();
			stmtOfMarksPara.add(stmtOfMarksChunk);
			stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
			document.add(stmtOfMarksPara);

		} catch (Exception e) {
			
		}

	}
	
	
	private void generateMarksCheckFooter(Document document) {
		try {
			PdfPTable footerTable = new PdfPTable(1);
			
			PdfPCell nameOfFacultyLabelCell = new PdfPCell(new Paragraph("CALL BY: NAME :_____________________________________  SIGNATURE :_______________________________", font9));

			
			nameOfFacultyLabelCell.setBorder(0);

			
			nameOfFacultyLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			nameOfFacultyLabelCell.setFixedHeight(40);
			
			footerTable.addCell(nameOfFacultyLabelCell);
			footerTable.completeRow();
			
			//Second Row
			PdfPCell facultySignatureLabelCell = new PdfPCell(new Paragraph("CHECK BY: NAME :___________________________________  SIGNATURE :_______________________________", font9));

			
			facultySignatureLabelCell.setBorder(0);

			facultySignatureLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			facultySignatureLabelCell.setFixedHeight(40);
			
			footerTable.addCell(facultySignatureLabelCell);
			footerTable.completeRow();
			
			
			//Third Row
			PdfPCell dateLabelCell = new PdfPCell(new Paragraph("DATE:________________________________", font9));

			dateLabelCell.setBorder(0);

			
			dateLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			dateLabelCell.setFixedHeight(40);
			
			footerTable.addCell(dateLabelCell);
			footerTable.completeRow();
			footerTable.setWidthPercentage(100);

			
			footerTable.setSpacingBefore(30);
			footerTable.setSpacingAfter(10);
			document.add(footerTable);
			
		} catch (Exception e) {
			
		}
		
	}
}
