package com.nmims.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.SubjectResultBean;
import com.nmims.beans.TimetableBean;

@Component
public class HallTicketPDFCreator {
	Font font3 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
	Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	Font font4 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
	Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
	Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
	Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 10);
	Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 18);
	Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	Font font9 = new Font(Font.FontFamily.TIMES_ROMAN, 10);
	Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, 7);
	Font font11 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);

	@Autowired
	MailSender mailer;
	
	
	
	public String createHallTicket(List<TimetableBean> timeTableList, ArrayList<ExamBookingTransactionBean> subjectsBooked, 
			HashMap<String, String> programCodeNameMap, StudentExamBean student, 
			String HALLTICKET_PATH, String mostRecentTimetablePeriod, HashMap<String, ExamCenterBean> examCenterIdCenterMap,
			HashMap<String, ExamBookingTransactionBean> subjectBookingMap, String STUDENT_PHOTOS_PATH,String password) throws Exception{
		Document document = new Document(PageSize.A4);
		String sapid = student.getSapid();
		String bookingMonth = "",bookingYear="";
		bookingMonth = subjectsBooked.get(0).getMonth();
		bookingYear = subjectsBooked.get(0).getYear();
		String fileName = sapid + "_Hall_Ticket_"+bookingMonth+"_"+bookingYear+".pdf";
		String filePathToSaveHallTicket = HALLTICKET_PATH+bookingMonth+bookingYear;
		
		
		try{


			document.setMargins(30, 30, 25, 25);
			File folderPath = new File(filePathToSaveHallTicket);
			if (!folderPath.exists()) {
				boolean created = folderPath.mkdirs();
			}
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePathToSaveHallTicket+"/"+fileName));
			
			document.open();

			generateHeaderTable(document, student, mostRecentTimetablePeriod);

			/*String password = generateStudentPassword(student, subjectsBooked);
			 * */


			generateStudentInfoTable(document, student, programCodeNameMap, mostRecentTimetablePeriod, password, writer, STUDENT_PHOTOS_PATH);

			generateSubjectsTable(document, subjectsBooked, timeTableList, examCenterIdCenterMap, subjectBookingMap);

			generateNoteAndSignatureTable(document);

			generateInstructions(document, student, mostRecentTimetablePeriod);

			generateCOESign(document);
			
			/*Image backgroundWatermark = Image.getInstance(new URL("https://studentzone-ngasce.nmims.edu/exam/resources/images/nmims_watermark.gif"));
            backgroundWatermark.setAbsolutePosition(60, 100);
            backgroundWatermark.setBorder(0);
            writer.getDirectContentUnder().addImage(backgroundWatermark);*/


		}catch(Exception e){
			
			throw e;
		}finally{
			document.close(); 
		}
		return filePathToSaveHallTicket+"/"+fileName;
	}


	private void generateCOESign(Document document) throws Exception {
		try {
			PdfPTable signatureTable = new PdfPTable(2);
			PdfPCell emptyCell = new PdfPCell();
			emptyCell.setBorder(0);
			signatureTable.addCell(emptyCell);
			signatureTable.completeRow();
			emptyCell = new PdfPCell();
			emptyCell.setBorder(0);
			signatureTable.addCell(emptyCell);
			signatureTable.completeRow();
			emptyCell = new PdfPCell();
			emptyCell.setBorder(0);
			signatureTable.addCell(emptyCell);
			signatureTable.completeRow();
			emptyCell = new PdfPCell();
			emptyCell.setBorder(0);
			signatureTable.addCell(emptyCell);
			signatureTable.completeRow();
			
			Image signature = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/signature.jpg"));
			//Image signature = Image.getInstance(new URL("http://localhost:8080/exam/resources/images/signature.jpg"));
			signature.scaleAbsolute(40, 28);
			signature.setAlignment(Element.ALIGN_CENTER);

			//signatureTable = new PdfPTable(2);
			emptyCell = new PdfPCell();
			emptyCell.setBorder(0);

			PdfPCell coeSignCell = new PdfPCell();
			coeSignCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			coeSignCell.addElement(signature);

			coeSignCell.setBorder(0);

			signatureTable.addCell(emptyCell);
			signatureTable.addCell(coeSignCell);
			signatureTable.completeRow();

			PdfPCell coeCell = new PdfPCell(new Paragraph("Controller of Examinations", font9));
			coeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			coeCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			coeCell.setBorder(0);


			emptyCell = new PdfPCell();
			emptyCell.setBorder(0);
			signatureTable.addCell(emptyCell);
			signatureTable.addCell(coeCell);


			signatureTable.completeRow();

			float[] columnWidths = {6f, 3f};
			signatureTable.setWidths(columnWidths);
			signatureTable.setWidthPercentage(100);
			//signatureTable.setSpacingAfter(5);
			//signatureTable.setSpacingBefore(5);

			document.add(signatureTable);

		} catch (Exception e) {
			
			throw e;
		}

	}


	private String generateStudentPassword(StudentExamBean student, ArrayList<ExamBookingTransactionBean> subjectsBooked) throws Exception {
		String examMode = student.getExamMode();
		if("Online".equals(examMode)){
			String maxTrackId = "";
			for (int i = 0; i < subjectsBooked.size(); i++) {
				ExamBookingTransactionBean bean = subjectsBooked.get(i);
				String trackId = bean.getTrackId();
				if(trackId != null && (!"".equals(trackId))){
					if(trackId.compareTo(maxTrackId) > 0){
						maxTrackId = trackId;
					}
				}
			}
			return maxTrackId.substring(11, 19);
		}else{
			return null;
		}
	}


	private void generateInstructions(Document document, StudentExamBean student, String mostRecentTimetablePeriod) throws Exception {
		try {
			String program = student.getProgram();
			String examMode = student.getExamMode();
			
			Paragraph blankLine = new Paragraph("\n");

			document.add(new Paragraph("Important Instructions:	", font4));
			com.itextpdf.text.List orderedList = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);

			if(program.equalsIgnoreCase("MBA - WX")) { 
				orderedList.add(new ListItem("It is Mandatory for the student to carry Student ID Card and Hall Ticket to the center. Student will not be allowed to the Examination Hall without both the documents.", font6));
				orderedList.add(new ListItem("This Hall Ticket is only valid for the subject/s mentioned in the Hall Ticket for the dates and Exam location displayed.", font6));
				orderedList.add(new ListItem("Students are expected to report atleast one hour before actual exam time.", font6));
				orderedList.add(new ListItem("Exam will start and end as per the scheduled time mentioned in the hall ticket.", font6));
				orderedList.add(new ListItem(" If the examination does not commence at the scheduled time or is interrupted midway due to any technical difficulty or for any other reason, candidates should follow the instructions of the exam officials. Students may have to wait patiently till the issue is suitably addressed and resolved. In case, the problem is major and cannot be resolved for any reason, their examination may be rescheduled for which the candidates would be duly intimated.", font6));
				orderedList.add(new ListItem("NMIMS will not be liable or accountable for any technical failure prior or during exams. NMIMS will however try to provide suitable solution as it deems fit. NMIMS resolution in this case will be binding on the student.", font6));
				orderedList.add(new ListItem("Carrying and/or use of any communication devices like any cell phones, PDAs and smartwatches and other electronic, recording, listening, scanning or photographic devices in switched off / on or any other mode carried intentionally or unintentionally is strictly prohibited in the examination hall. Non adherence may result in examination getting Null and Void. Please ensure your communication devices are not in your person during the exam and kept secured in your bag or at designated place inside the lab.", font6));
				orderedList.add(new ListItem("On-screen calculator is available. However, if required students can carry their own calculators (Standard/Scientific). Students will not be allowed to borrow calculators during the examination. Non adherence will result in examination getting Null and Void.", font6));
				orderedList.add(new ListItem("Student will be permitted to use the washroom only after two hours of exam commencement. In case of medical issue, student to seek two days prior approval from NMIMS University by sending medical certificate.", font6));
				orderedList.add(new ListItem("Students should come in proper dress code to appear for examination. Shorts, Bermuda's, caps etc. will not be permitted in the examination hall. Indiscipline / Unfair Means / Impersonation / Malpractice adoption will be dealt strictly by the University.", font6));
				orderedList.add(new ListItem("In all cases the decision of NMIMS University will be final and binding on all students. For any queries do call us on 1-800-1025-136 (Mon-Sat) 10am - 6pm, during exams the operational hours are from 9am - 6pm.", font11));
			}else {
				orderedList.add(new ListItem("It is Mandatory for the student to carry Student ID Card and Hall Ticket at the center. Without both the documents student will strictly not be allowed in the Examination Hall.", font11));
				
				if("Online".equals(examMode)){
					orderedList.add(new ListItem("It is Mandatory for the student to first register at the Registration Desk for each exam. Without Registration student will not be allowed to appear for the exam. Students are expected to report atleast one hour before actual exam time.", font6));
					//orderedList.add(new ListItem("Students would be given Sample Test Questions for practice purpose before they start answering the actual Examination Questions. (Sample Test is available only if the student completes the registration process and reaches 15 minutes early before the start of the actual examination time)", font6));
				}
				
				
				
				/*if("PGDMM-MLI".equalsIgnoreCase(program)){
					orderedList.add(new ListItem("This Hall Ticket is valid for the subject/s mentioned in the Hall Ticket for the above mentioned dates and Exam location displayed.", font6));
				
					orderedList.add(new ListItem(" Exam will start and end as per the scheduled time mentioned in the hall ticket. Exam duration is 2 hours. Students reaching after the start of exam will not be permitted extra exam time. Students will not be allowed entry 30 minutes after the start of the Exam.", font11));
				}else if("Online".equals(examMode)){
					if("Jul2017".equalsIgnoreCase(student.getPrgmStructApplicable())){
						orderedList.add(new ListItem(" Exam will start and end as per the scheduled time mentioned in the hall ticket. Exam duration is 2 hours. Students reaching after the start of exam will not be permitted extra exam time. Students will not be allowed entry 30 minutes after the start of the Exam.", font11));
					}else{
						orderedList.add(new ListItem(" Exam will start and end as per the scheduled time mentioned in the hall ticket. Exam duration is 2 hours. Students reaching after the start of exam will not be permitted extra exam time. Students will not be allowed entry 30 minutes after the start of the Exam.", font11));
					}
				}else{
					orderedList.add(new ListItem("This Hall Ticket is valid for the subject/s mentioned in the Hall Ticket for the above mentioned dates and Exam location displayed.", font6));
					if("Jul2017".equalsIgnoreCase(student.getPrgmStructApplicable())){
						orderedList.add(new ListItem(" Exam will start and end as per the scheduled time mentioned in the hall ticket. Exam duration is 2 hours. Students reaching after the start of exam will not be permitted extra exam time. Students will not be allowed entry 30 minutes after the start of the Exam.", font11));
					}else{
						orderedList.add(new ListItem(" Exam will start and end as per the scheduled time mentioned in the hall ticket. Exam duration is 3 hours. Students reaching after the start of exam will not be permitted extra exam time. Students will not be allowed entry 30 minutes after the start of the Exam.", font11));
					}
				}*/
				
				
				orderedList.add(new ListItem("This Hall Ticket is valid for the subject/s mentioned in the Hall Ticket for the above mentioned dates and Exam location displayed.", font6));
				if("Online".equals(examMode)){
				orderedList.add(new ListItem("Candidates should register themselves and logon to the examination system on or before the scheduled examination time. "
						+ "Exam will start and end as per the scheduled time mentioned in the hall ticket. "
						+ "Students reaching after the start of exam will not be permitted extra exam time. "
						+ "Students will not be allowed entry 30 minutes after the start of the Exam.", font11));
				}else{
					
					orderedList.add(new ListItem(" Exam will start and end as per the scheduled time mentioned in the hall ticket. "
							+ "Students reaching after the start of exam will not be permitted extra exam time. "
							+ "Students will not be allowed entry 30 minutes after the start of the Exam.", font11));
				}
				if("Online".equals(examMode)){
					orderedList.add(new ListItem("If the examination does not commence at the scheduled time or is interrupted midway due to any technical difficulty or for any other reason, "
												+"candidates should follow the instructions of the exam officials. "
												+"Students may have to wait patiently till the issue is suitably addressed and resolved. "
												+"In case, the problem is major and cannot be resolved for any reason,their examination may be rescheduled for which the candidates would be duly intimated.", font6));
				
				}
				
				
				if("Online".equals(examMode)){

					orderedList.add(new ListItem("NMIMS will not be liable or accountable for any technology failure prior or during exams. "
							+ "NMIMS will however try to provide suitable resolution as it deems fit. "
							+ "NMIMS resolution in this case will be binding on the student.",font6));
					
					orderedList.add(new ListItem("Carrying and/or use of any communication devices like any cell phones, PDAs "
							+ "and smartwatches and other electronic, recording, listening, scanning "
							+ "or photographic devices in switched off / on or any other mode carried intentionally or unintentionally "
							+ "is strictly prohibited in the examination hall. Non adherence may result in examination getting Null and Void. "
							+ "Please ensure your communication devices are not in your person during the exam "
							+ "and kept secured in your bag or at designated place inside the lab.",font11));
					
					/*Commented by Steffi
					 * orderedList.add(new ListItem("Only standard 12 digit calculators will be allowed for the Term End Examination."
							+ "It is mandatory for the students to carry their calculators as there is no on-screen calculator available."
							+ "Students will not be allowed to borrow calculators during the examination. "
							+ "Programmable / Scientific calculators are strictly prohibited. "
							+ "Non adherence will result in examination getting Null and Void.",font11));*/
					orderedList.add(new ListItem("It is mandatory for the students to carry their own calculators (Standard/Scientific). "
							+ " Students will not be allowed to borrow calculators during the examination."
							+ " Non adherence will result in examination getting Null and Void.",font11));
					
				}else{
					/*Commented by Steffi
					 * orderedList.add(new ListItem("Only standard 12 digit calculators will be allowed for the Term End Examination. "
							+ "It is mandatory for the students to carry their calculators. "
							+ "Students will not be allowed to borrow calculators during the examination. "
							+ "Programmable / Scientific calculators are strictly prohibited. "
							+ "Non adherence will result in examination getting Null and Void.", font6));*/
					orderedList.add(new ListItem("It is mandatory for the students to carry their own calculators (Standard/Scientific). "
							+ " Students will not be allowed to borrow calculators during the examination. "
							+ " Non adherence will result in examination getting Null and Void.", font6));
				}
				
				orderedList.add(new ListItem("Students will not be allowed to use the washroom during the examination. Student will be permitted to use the washroom only after two hours of exam commencement. In case of medical issue, student to seek one week prior approval from NMIMS University by sending medical certificate.", font6));
				
				if("Online".equals(examMode)){
					/*orderedList.add(new ListItem("This Hall Ticket is valid for the subject/s mentioned in the Hall Ticket for the above mentioned dates and Exam location displayed.", font6));*/
					
					orderedList.add(new ListItem("Students would be given Sample Test Questions for practice purpose before they start "
							+ "answering the actual Examination Questions. (Sample Test is available only if the student completes "
							+ "the registration process and reaches 15 minutes early before the start of the actual examination time)",font6));
					
					/*orderedList.add(new ListItem("Use of any communication devices is strictly prohibited in the examination hall. "
							+ "No cell phones will be permitted in the examination hall even in switched off or any other mode. "
							+ "Please ensure your cell phones are kept secured in your bag or at designated place inside the lab. "
							+ "Non adherence may result in examination getting Null and Void.", font11));*/
				}else{
					orderedList.add(new ListItem("Use of any communication devices is strictly prohibited in the examination hall. "
							+ "No cell phones will be permitted in the examination hall even in switched off or any other mode. "
							+ "Please ensure your cell phones are kept secured in your bag or at designated place inside the classroom. "
							+ "Non adherence may result in examination getting Null and Void.", font11));
				}
				

				orderedList.add(new ListItem("Students should come in proper dress code to appear for examination. Shorts, Bermuda's, caps etc. will not be permitted in the examination hall.", font6));
				
				orderedList.add(new ListItem("Indiscipline / Unfair Means / Impersonation / Malpractice adoption will be dealt strictly by the University.", font6));
				
				/*if(mostRecentTimetablePeriod.contains("Sep") || mostRecentTimetablePeriod.contains("Apr")){
					orderedList.add(new ListItem("For Re-Sit Examination, the previous exam cycle assignment marks will be carry forwarded & latest marks of the Re-Sit term end examination will be considered. Assignment resubmission option is not available in Re-Sit examination. Students who resubmit assignment of the failed subject and also appear for the re-sit term end examination of the failed subject, these assignments will not be considered.", font11));
				}*/
				
				orderedList.add(new ListItem("In all cases the decision of NMIMS University will be final and binding on all students.", font11));
				orderedList.add(new ListItem("For any queries do call us on 1-800-1025-136 (Mon-Sat) 10am - 6pm, during exams the operational hours are from 9am - 6pm.", font11));

			}

			document.add(orderedList);
			
			document.add(blankLine);
			
		} catch (Exception e) {
			
			throw e;
		}
	}


	private void generateNoteAndSignatureTable(Document document) throws Exception {
		try {
			//Image signature = Image.getInstance(new URL("https://studentzone-ngasce.nmims.edu/exam/resources/images/signature.jpg"));
			//Image signature = Image.getInstance(new URL("http://localhost:8080/exam/resources/images/signature.jpg"));
			//signature.scaleAbsolute(60, 42);
			PdfPTable noteTable = new PdfPTable(1);
			noteTable.getDefaultCell().setBorder(0);

			/*PdfPCell noteCell = new PdfPCell(new Paragraph("Disclaimer: I confirm that I have submitted the assignments of all the "
					+ "subjects for which I wish to appear at the examination. I understand that without submission of assignment/s, "
					+ "my appearance will be treated as null and void. In case I am desirous of instituting any legal proceedings "
					+ "against the University, I hereby agree that such legal proceedings shall be instituted "
					+ "only in courts at Mumbai under the jurisdiction of which the application is submitted by me and not in any other court.\n", font6));
			noteCell.setBorder(0);
			noteCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);*/



			//noteTable.addCell(noteCell);

			noteTable.completeRow();

			noteTable.setWidthPercentage(100);
			noteTable.setSpacingAfter(5);

			PdfPTable signatureTable = new PdfPTable(1);

			PdfPCell emptyCell = new PdfPCell();
			emptyCell.setBorder(0);

			//PdfPCell coeSignCell = new PdfPCell();

			//coeSignCell.addElement(signature);
			//coeSignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			//coeSignCell.setBorder(0);


			//noteSignatureTable.addCell(coeSignCell);
			emptyCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			signatureTable.addCell(emptyCell);
			signatureTable.completeRow();

			PdfPCell signatureCell = new PdfPCell(new Paragraph("Signature of Student", font9));
			//PdfPCell coeCell = new PdfPCell(new Paragraph("Controller of Examinations", font9));
			//coeCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			signatureCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

			signatureCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			//coeCell.setVerticalAlignment(Element.ALIGN_BOTTOM);


			signatureCell.setBorder(0);
			//coeCell.setBorder(0);

			//noteSignatureTable.addCell(coeCell);
			signatureTable.addCell(signatureCell);

			signatureTable.completeRow();

			//float[] columnWidths = {3f, 3f};
			//noteSignatureTable.setWidths(columnWidths);
			signatureTable.setWidthPercentage(100);
			//signatureTable.setSpacingAfter(5);
			signatureTable.setSpacingBefore(20);

			document.add(noteTable);
			document.add(signatureTable);

		} catch (Exception e) {
			
			throw e;
		}
	}


	private void generateStudentInfoTable(Document document, StudentExamBean student, HashMap<String, String> programCodeNameMap, 
			String mostRecentTimetablePeriod, String password, PdfWriter writer, String STUDENT_PHOTOS_PATH) throws Exception {
		try {
			Image studentPhoto = null;
			PdfPTable studentInfoTable = null;
			
			if("Online".equals(student.getExamMode())){
				
				studentInfoTable = new PdfPTable(3);
			}else{
				studentInfoTable = new PdfPTable(2);
			}

			PdfPCell studentNoCell = new PdfPCell(new Paragraph("Student No: "+student.getSapid(), font6));
			PdfPCell genderCell = new PdfPCell(new Paragraph("Gender: "+student.getGender(), font6));

			studentNoCell.setFixedHeight(20);
			genderCell.setFixedHeight(20);

			studentInfoTable.addCell(studentNoCell);
			studentInfoTable.addCell(genderCell);
			if("Online".equals(student.getExamMode())){
				//Photo not required for Offline exam.
				
				try {
					studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH +student.getSapid()+".jpg");
				} catch (Exception e) {
					
					//If not .jpg, try .jpeg
					try {
						studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH +student.getSapid()+".jpeg");
					} catch (Exception e2) {
						//IF this is also not found, then take photo from Admission system
						studentPhoto = Image.getInstance(new URL(student.getImageUrl()));
						//mailer.sendPhotoNotFoundEmail(student);
					}
				}
				
				studentPhoto.scaleAbsolute(100, 120);
				PdfPCell studentPhotoCell = null;
				if(studentPhoto == null){
					//In case URL was malformed or was not found
					studentPhotoCell = new PdfPCell();
				}else{
					studentPhotoCell = new PdfPCell(studentPhoto);
				}

				studentPhotoCell.setRowspan(6);
				studentPhotoCell.setBorder(0);
				studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				studentInfoTable.addCell(studentPhotoCell);
			}


			studentInfoTable.completeRow();

			PdfPCell studentNameCell = new PdfPCell(new Paragraph("Student Name: "+student.getLastName().toUpperCase()+" "+student.getFirstName().toUpperCase(), font6));
			studentNameCell.setColspan(2);
			studentNameCell.setFixedHeight(20);
			studentInfoTable.addCell(studentNameCell);
			studentInfoTable.completeRow();
			
			String programFullName = programCodeNameMap.get(student.getProgram());
			if("PGDMM-MLI".equals(student.getProgram())){
				programFullName = programFullName + " � MLI (Max Life Insurance) ";
			}
			PdfPCell programCell = new PdfPCell(new Paragraph("Program: "+programFullName, font6));
			PdfPCell ExamCell = new PdfPCell(new Paragraph("Examination: "+mostRecentTimetablePeriod, font6));


			programCell.setFixedHeight(40);
			ExamCell.setFixedHeight(40);

			studentInfoTable.addCell(programCell);
			studentInfoTable.addCell(ExamCell);

			studentInfoTable.completeRow();


			if("Online".equals(student.getExamMode())){

				
				/*PdfPCell examModeCell = new PdfPCell(new Paragraph("Mode of Examination: Computer Based Examination", font6));//changed
				examModeCell.setColspan(2);
				examModeCell.setFixedHeight(20);
				studentInfoTable.addCell(examModeCell);
				studentInfoTable.completeRow();*/
				

				PdfPCell userIdCell = new PdfPCell(new Paragraph("Exam User ID: "+student.getSapid(), font6));
				userIdCell.setColspan(2);
				userIdCell.setFixedHeight(20);
				studentInfoTable.addCell(userIdCell);
				studentInfoTable.completeRow();


				PdfPCell passwordCell = new PdfPCell(new Paragraph("Exam Password: "+password, font6));
				passwordCell.setColspan(2);
				passwordCell.setFixedHeight(20);
				studentInfoTable.addCell(passwordCell);
				studentInfoTable.completeRow();

				float[] columnWidths = {5.5f, 2.5f, 2.5f};
				studentInfoTable.setWidths(columnWidths);

			}else{
				/*PdfPCell examModeCell = new PdfPCell(new Paragraph("Mode of Examination: Paper Pen Examination", font6));//changed
				examModeCell.setColspan(2);
				examModeCell.setFixedHeight(20);
				studentInfoTable.addCell(examModeCell);
				studentInfoTable.completeRow();*/

				float[] columnWidths = {5.5f, 2.5f};
				studentInfoTable.setWidths(columnWidths);
			}


			studentInfoTable.setWidthPercentage(100);


			studentInfoTable.setSpacingBefore(7);
			studentInfoTable.setSpacingAfter(7);
			document.add(studentInfoTable);

			if(("Online".equals(student.getExamMode())) && studentPhoto != null){
				
				Phrase watermark = new Phrase("AUTHENTIC PHOTOGRAPH", new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK));
				PdfContentByte canvas = writer.getDirectContent();
				ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 490, 590, 20);
			}
			
		} catch (Exception e) {
			
			throw e;
		}
	}


	private void generateSubjectsTable(Document document,ArrayList<ExamBookingTransactionBean> subjectsBooked,
			List<TimetableBean> timeTableList, HashMap<String, ExamCenterBean> examCenterIdCenterMap,
			HashMap<String, ExamBookingTransactionBean> subjectBookingMap) throws Exception {
		try{

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			//SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");


			PdfPTable subjectsTable = new PdfPTable(8);

			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font4));
			PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subject", font4));
			PdfPCell semesterCell = new PdfPCell(new Paragraph("Sem", font4));
			PdfPCell dateCell = new PdfPCell(new Paragraph("Date", font4));
			PdfPCell startTimeCell = new PdfPCell(new Paragraph("Start Time", font4));
			PdfPCell endTimeCell = new PdfPCell(new Paragraph("End Time", font4));
			PdfPCell examCenterCell = new PdfPCell(new Paragraph("Location", font4));
			PdfPCell remarkCell = new PdfPCell(new Paragraph("Remark", font4));

			srNoCell.setFixedHeight(30);
			srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
			subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
			semesterCell.setVerticalAlignment(Element.ALIGN_TOP);
			dateCell.setVerticalAlignment(Element.ALIGN_TOP);
			startTimeCell.setVerticalAlignment(Element.ALIGN_TOP);
			endTimeCell.setVerticalAlignment(Element.ALIGN_TOP);
			examCenterCell.setVerticalAlignment(Element.ALIGN_TOP);
			remarkCell.setVerticalAlignment(Element.ALIGN_TOP);

			srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			semesterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			startTimeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			endTimeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			examCenterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			remarkCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			
			subjectsTable.addCell(srNoCell);
			subjectsTable.addCell(subjectsCell);
			subjectsTable.addCell(semesterCell);
			subjectsTable.addCell(dateCell);
			subjectsTable.addCell(startTimeCell);
			subjectsTable.addCell(endTimeCell);
			subjectsTable.addCell(examCenterCell);
			subjectsTable.addCell(remarkCell);
			subjectsTable.completeRow();


			int count = 1;
			for (int j = 0; j < subjectsBooked.size(); j++) {
				ExamBookingTransactionBean bean = subjectsBooked.get(j);
				String examMode = bean.getExamMode();

				if("Project".equals(bean.getSubject())){
					continue;
				}
				if("Module 4 - Project".equals(bean.getSubject())){
					continue;
				}
				Date formattedDate = formatter.parse(bean.getExamDate());
				String examDate = dateFormatter.format(formattedDate);

				srNoCell = new PdfPCell(new Paragraph(count+"", font6));
				subjectsCell = new PdfPCell(new Paragraph(bean.getSubject(), font6));
				semesterCell = new PdfPCell(new Paragraph(bean.getSem(), font6));
				dateCell = new PdfPCell(new Paragraph(examDate, font6));
				startTimeCell = new PdfPCell(new Paragraph(bean.getExamTime().substring(0, 5), font6));
				endTimeCell = new PdfPCell(new Paragraph(bean.getExamEndTime().substring(0, 5), font6));
				remarkCell = new PdfPCell(new Paragraph("", font6));
				
				String centerId = bean.getCenterId();
				ExamCenterBean  center = examCenterIdCenterMap.get(centerId);
				String address = center.getAddress().replaceAll("\\r\\n", " ");
				String examCenterCity = center.getCity();
				
				if("Offline".equalsIgnoreCase(examMode) && "Mumbai".equalsIgnoreCase(examCenterCity) && 
						("26-Dec-2014".equals(examDate) || "27-Dec-2014".equals(examDate) || "28-Dec-2014".equals(examDate) || 
								"29-Dec-2014".equals(examDate) || "30-Dec-2014".equals(examDate) || "31-Dec-2014".equals(examDate))
						){
					address = "Pravin Gandhi College of Law, 8th Floor, Mithibai College Building, V. L. Mehta Road, Vile Parle (West), Mumbai - 400 056";
					examCenterCell = new PdfPCell(new Paragraph(address, font10));
				}else if("Offline".equalsIgnoreCase(examMode) && "Mumbai".equalsIgnoreCase(examCenterCity) && 
						("02-Jan-2015".equals(examDate) || "03-Jan-2015".equals(examDate) || "04-Jan-2015".equals(examDate) || 
								"05-Jan-2015".equals(examDate) || "06-Jan-2015".equals(examDate) )
						){
					address = "NMIMS University (New Building), Entrance Gate No. 4, Opp. Mithibai College, 4h Floor, V. L. Mehta Road, Vile Parle (West), Mumbai - 400 056";
					examCenterCell = new PdfPCell(new Paragraph(address, font10));
				}else{
					examCenterCell = new PdfPCell(new Paragraph(center.getExamCenterName()+","+address, font10));
				}

				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER); srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				/*subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER);*/ subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				semesterCell.setHorizontalAlignment(Element.ALIGN_CENTER); semesterCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				dateCell.setHorizontalAlignment(Element.ALIGN_CENTER); dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				startTimeCell.setHorizontalAlignment(Element.ALIGN_CENTER); startTimeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				endTimeCell.setHorizontalAlignment(Element.ALIGN_CENTER);  endTimeCell.setVerticalAlignment(Element.ALIGN_MIDDLE); 
				examCenterCell.setHorizontalAlignment(Element.ALIGN_UNDEFINED); examCenterCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				remarkCell.setHorizontalAlignment(Element.ALIGN_UNDEFINED); remarkCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				
				subjectsTable.addCell(srNoCell);
				subjectsTable.addCell(subjectsCell);
				subjectsTable.addCell(semesterCell);
				subjectsTable.addCell(dateCell);
				subjectsTable.addCell(startTimeCell);
				subjectsTable.addCell(endTimeCell);
				subjectsTable.addCell(examCenterCell);
				subjectsTable.addCell(remarkCell);
				subjectsTable.completeRow();

				count++;
			}

			subjectsTable.setWidthPercentage(100);
			subjectsTable.setSpacingBefore(5f);
			subjectsTable.setSpacingAfter(5f);
			float[] marksColumnWidths = {1f, 6f, 1.3f, 2.5f, 1.7f, 1.7f, 11.6f,2.5f};
			subjectsTable.setWidths(marksColumnWidths);
			document.add(subjectsTable);

		}catch(Exception e){
			
			throw e;
		}

	}
	
	
	private void generateHeaderTable(Document document, StudentExamBean student, String mostRecentTimetablePeriod) throws Exception {
		Image logo = null;
		try {
			logo = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nmims_logo.jpg"));
			//Image signature = Image.getInstance(new URL("http://admissions-ngasce.nmims.edu:4001/StudentDocuments/studentPhotos/signature.jpg"));

			logo.scaleAbsolute(142,40);

			SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
			String fileDate = sdfr.format(new Date());
			Paragraph datePara = new Paragraph("Date: "+fileDate, font6);
			datePara.setAlignment(Element.ALIGN_RIGHT);
			document.add(datePara);

			PdfPTable headerTable = new PdfPTable(2);

			PdfPCell logoCell = new PdfPCell();
			logoCell.addElement(logo);
			logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);

			Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font3);
			collegNameChunk.setUnderline(0.1f, -2f);
			Paragraph collegeName = new Paragraph();
			collegeName.add(collegNameChunk);
			collegeName.setAlignment(Element.ALIGN_CENTER);
			//document.add(collegeName);

			PdfPCell collegeNameCell = new PdfPCell();
			collegeNameCell.addElement(collegeName);
			collegeNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);



			headerTable.addCell(logoCell);
			headerTable.addCell(collegeNameCell);

			headerTable.completeRow();
			headerTable.setWidthPercentage(100);
			float[] headerColumnWidths = {2f, 4f};
			headerTable.setWidths(headerColumnWidths);
			headerTable.setSpacingBefore(10);
			headerTable.setSpacingAfter(10);
			document.add(headerTable);

			mostRecentTimetablePeriod =  mostRecentTimetablePeriod.replaceAll("Dec", "December");
			mostRecentTimetablePeriod =  mostRecentTimetablePeriod.replaceAll("Jun", "June");
			mostRecentTimetablePeriod =  mostRecentTimetablePeriod.replaceAll("Sep", "September");
			mostRecentTimetablePeriod =  mostRecentTimetablePeriod.replaceAll("Apr", "April");

			String title = "HALL TICKET for "+mostRecentTimetablePeriod;
			if(mostRecentTimetablePeriod.contains("Sep") || mostRecentTimetablePeriod.contains("Apr")){
				title = title + " Re-Sit Term End Examination";
			}else{
				title = title + " Term End Examination";
			}
			
			Chunk stmtOfMarksChunk = new Chunk(title, font2);
			stmtOfMarksChunk.setUnderline(0.1f, -2f);
			Paragraph stmtOfMarksPara = new Paragraph();
			stmtOfMarksPara.add(stmtOfMarksChunk);
			stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
			document.add(stmtOfMarksPara);

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
			}else if(mostRecentTimetablePeriod.contains("Apr")){
				examMonth = mostRecentTimetablePeriod.replace("Apr", "April");
			}else if(mostRecentTimetablePeriod.contains("Sep")){
				examMonth = mostRecentTimetablePeriod.replace("Sep", "September");
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
			}else if(mostRecentTimetablePeriod.contains("Apr")){
				examMonth = mostRecentTimetablePeriod.replace("Apr", "April");
			}else if(mostRecentTimetablePeriod.contains("Sep")){
				examMonth = mostRecentTimetablePeriod.replace("Sep", "September");
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
			}else if(mostRecentTimetablePeriod.contains("Sep")){
				examMonth = mostRecentTimetablePeriod.replace("Sep", "September");
			}else if(mostRecentTimetablePeriod.contains("Apr")){
				examMonth = mostRecentTimetablePeriod.replace("Apr", "April");
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
			
			/*float[] columnWidths = {5f, 10f};
			footerTable.setWidths(columnWidths);*/
			footerTable.setWidthPercentage(100);

			
			footerTable.setSpacingBefore(30);
			footerTable.setSpacingAfter(10);
			document.add(footerTable);
			
		} catch (Exception e) {
			
		}
		
	}
}
