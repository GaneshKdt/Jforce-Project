package com.nmims.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
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
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAHallTicketBean;
import com.nmims.beans.StudentExamBean;

@Component
public class HallTicketPDFCreatorMBAX {
	

	@Value("${HALLTICKET_PATH}")
	private String HALLTICKET_PATH;

	@Value("${STUDENT_PHOTOS_PATH}")
	private String STUDENT_PHOTOS_PATH;
	
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
	
	public String createHallTicket(MBAHallTicketBean hallTicketBean) throws Exception{
		
		StudentExamBean student = hallTicketBean.getStudent();

		List<MBAExamBookingRequest> examBookings = hallTicketBean.getExamBookings();
	
		Document document = new Document(PageSize.A4);
		
		String sapid = student.getSapid();
		
		String bookingMonth = hallTicketBean.getMonth();
		String bookingYear = hallTicketBean.getYear();

		String fileName = sapid + "_Hall_Ticket_"+bookingMonth+"_"+bookingYear+".pdf";
		String filePathToSaveHallTicket = HALLTICKET_PATH + bookingMonth + bookingYear;

		try{
			document.setMargins(30, 30, 25, 25);
			File folderPath = new File(filePathToSaveHallTicket);
			if (!folderPath.exists()) {
				boolean created = folderPath.mkdirs();
			}
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePathToSaveHallTicket+"/"+fileName));
			document.open();
			generateHeaderTable(document, student, hallTicketBean.getTitle());

			generateStudentInfoTable(document, student, hallTicketBean, "", writer, STUDENT_PHOTOS_PATH);

			generateSubjectsTable(document, examBookings);

			generateNoteAndSignatureTable(document);

			generateInstructions(document, student);

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

	private void generateInstructions(Document document, StudentExamBean student) throws Exception {
		try {

			Paragraph blankLine = new Paragraph("\n");

			document.add(new Paragraph("Important Instructions:	", font4));
			com.itextpdf.text.List orderedList = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);

			orderedList.add(new ListItem("It is Mandatory for the student to carry Student ID Card and Hall Ticket to the center. Student will not be allowed to the Examination Hall without both the documents.", font6));
			orderedList.add(new ListItem("This Hall Ticket is only valid for the subject/s mentioned in the Hall Ticket for the dates and Exam location displayed.", font6));
			orderedList.add(new ListItem("Students are expected to report atleast one hour before actual exam time.", font6));
			orderedList.add(new ListItem("Exam will start and end as per the scheduled time mentioned in the hall ticket.", font6));
			orderedList.add(new ListItem(" If the examination does not commence at the scheduled time or is interrupted midway due to any technical difficulty or for any other reason, candidates should follow the instructions of the exam officials. Students may have to wait patiently till the issue is suitably addressed and resolved. In case, the problem is major and cannot be resolved for any reason, their examination may be rescheduled for which the candidates would be duly intimated.", font6));
			orderedList.add(new ListItem("NMIMS will not be liable or accountable for any technical failure prior or during exams. NMIMS will however try to provide suitable solution as it deems fit. NMIMS resolution in this case will be binding on the student.", font6));
			orderedList.add(new ListItem("Carrying and/or use of any communication devices like any cell phones, PDAs and smartwatches and other electronic, recording, listening, scanning or photographic devices in switched off / on or any other mode carried intentionally or unintentionally is strictly prohibited in the examination hall. Non adherence may result in examination getting Null and Void. Please ensure your communication devices are not in your person during the exam and kept secured in your bag or at designated place inside the lab.", font6));
			orderedList.add(new ListItem("On-screen calculator is available. However, if required students can carry their own calculators (Standard/Scientific). Students will not be allowed to borrow calculators during the examination. Non adherence will result in examination getting Null and Void.", font6));
			orderedList.add(new ListItem("Student will be permitted to use the washroom only after two hours of exam commencement. In case of medical issue, student to seek two days prior approval from NMIMS University by sending detailed medical certificate.", font6));
			orderedList.add(new ListItem("Students should come in proper dress code to appear for examination. Shorts, Bermuda's, caps etc. will not be permitted in the examination hall.", font6));
			orderedList.add(new ListItem("Indiscipline / Unfair Means / Impersonation / Malpractice adoption will be dealt strictly by the University.", font6));
			orderedList.add(new ListItem("In all cases the decision of NMIMS University will be final and binding on all students. For any queries do call us on 1-800-1025-136 (Mon-Sat) 10am - 6pm, during exams the operational hours are from 9am - 6pm.", font11));
			
			document.add(orderedList);
			document.add(blankLine);
		} catch (Exception e) {
			
			throw e;
		}
	}

	private void generateNoteAndSignatureTable(Document document) throws Exception {
		try {

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

	private void generateStudentInfoTable(
			Document document, StudentExamBean student, MBAHallTicketBean hallTicketBean,
			String password, PdfWriter writer, String STUDENT_PHOTOS_PATH
	) throws Exception {
		
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
			
			studentPhotoCell = new PdfPCell(studentPhoto);
			studentPhotoCell.setRowspan(6);
			studentPhotoCell.setBorder(0);
			studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			studentInfoTable.addCell(studentPhotoCell);

			studentInfoTable.completeRow();
			PdfPCell studentNameCell = new PdfPCell(new Paragraph("Student Name: "+student.getLastName().toUpperCase()+" "+student.getFirstName().toUpperCase(), font6));
			studentNameCell.setColspan(2);
			studentNameCell.setFixedHeight(20);
			studentInfoTable.addCell(studentNameCell);
			studentInfoTable.completeRow();

			PdfPCell programCell = new PdfPCell(new Paragraph("Program :  " + hallTicketBean.getProgramFullName(), font6));
			PdfPCell ExamCell = new PdfPCell(new Paragraph("Examination :  " + hallTicketBean.getExamination(), font6));


			programCell.setFixedHeight(40);
			ExamCell.setFixedHeight(40);

			studentInfoTable.addCell(programCell);
			studentInfoTable.addCell(ExamCell);

			studentInfoTable.completeRow();

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

			studentInfoTable.setWidthPercentage(100);

			studentInfoTable.setSpacingBefore(7);
			studentInfoTable.setSpacingAfter(7);
			document.add(studentInfoTable);

			Phrase watermark = new Phrase("AUTHENTIC PHOTOGRAPH", new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK));
			PdfContentByte canvas = writer.getDirectContent();
			ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 490, 590, 20);

		} catch (Exception e) {
			
			throw e;
		}
	}

	private void generateSubjectsTable(Document document, List<MBAExamBookingRequest> subjectsBooked) throws Exception {
		try{

			PdfPTable subjectsTable = new PdfPTable(8);

			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font4));
			PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subject", font4));
			PdfPCell semesterCell = new PdfPCell(new Paragraph("Term", font4));
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
			for (MBAExamBookingRequest bean : subjectsBooked) {
//				MBAXExamBookingRequest bean = subjectsBooked.get(j);
				
				if("Project".equals(bean.getSubjectName())){
					continue;
				}

				srNoCell = new PdfPCell(new Paragraph(count+"", font6));
				subjectsCell = new PdfPCell(new Paragraph(bean.getSubjectName(), font6));
				semesterCell = new PdfPCell(new Paragraph(bean.getTerm(), font6));
				dateCell = new PdfPCell(new Paragraph(bean.getExamDate(), font6));
				startTimeCell = new PdfPCell(new Paragraph(bean.getExamStartTime().substring(0, 5), font6));
				endTimeCell = new PdfPCell(new Paragraph(bean.getExamEndTime().substring(0, 5), font6));
				remarkCell = new PdfPCell(new Paragraph("", font6));
				
				String address = bean.getCenterAddress().replaceAll("\\r\\n", " ");
				examCenterCell = new PdfPCell(new Paragraph(bean.getCenterName()+","+address, font10));
				
				alignCell(srNoCell);
				/*subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER);*/ 
				subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				alignCell(semesterCell);
				alignCell(dateCell);
				alignCell(startTimeCell);
				alignCell(endTimeCell);
				alignCell(examCenterCell);
				alignCell(remarkCell);
				
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
	
	private void alignCell(PdfPCell cell) {
		cell.setHorizontalAlignment(Element.ALIGN_CENTER); 
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	}

	private void generateHeaderTable(Document document, StudentExamBean student, String title) throws Exception {
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

}
