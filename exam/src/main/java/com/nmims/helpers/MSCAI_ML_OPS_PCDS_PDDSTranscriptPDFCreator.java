package com.nmims.helpers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.beans.MBAMarksheetBean;
import com.nmims.beans.MBAPassFailBean;
import com.nmims.beans.MBATranscriptBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.StudentExamBean;

public class MSCAI_ML_OPS_PCDS_PDDSTranscriptPDFCreator {
	Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
	Font font6b = new Font(Font.FontFamily.TIMES_ROMAN, 6, Font.BOLD);

	Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 7);
	Font font7b = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);

	Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
	Font font8b = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);

	Font font9 = new Font(Font.FontFamily.TIMES_ROMAN, 9);
	Font font9b = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);

	Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, 10);
	Font font10b = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);

	Font font11 = new Font(Font.FontFamily.TIMES_ROMAN, 11);
	Font font11b = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);

	Font font12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
	Font font12b = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

	Font font13 = new Font(Font.FontFamily.TIMES_ROMAN, 13);
	Font font13b = new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD);

	Font font14 = new Font(Font.FontFamily.TIMES_ROMAN, 14);
	Font font14b = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);

	public String createTrascripts(MBATranscriptBean transcriptBean, String HALLTICKET_PATH, Map<String, ProgramExamBean> programDetailMap) throws Exception{
		Document document = new Document(PageSize.A4);
		String sapid = transcriptBean.getSapid();

		String fileName = sapid + "_Transcript.pdf";
		Paragraph emptyPara = new Paragraph(" ");
		emptyPara.setAlignment(Element.ALIGN_CENTER);
		try{

			if(transcriptBean.getLogoRequired()!=null && transcriptBean.getLogoRequired().equalsIgnoreCase("Y")) {
				document.setMargins(70, 70, 10, 10);
			}else {
				document.setMargins(70, 70, 100, 25); //Left,right,top,bottom//
			}
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(HALLTICKET_PATH+fileName));
			document.open();
			addDocumentHeader(document, transcriptBean, writer, programDetailMap);
			generateSemWiseMarksTable(document, transcriptBean, 1);

			int pageNum = 1;
			addPageFooter(document, writer, pageNum++, transcriptBean.isSoftCopy(),transcriptBean.getLogoRequired());
			for(int index = 2; index <= transcriptBean.getSemSubjectList().size(); index=index+2) {	
			document.newPage();
			document.add(emptyPara);
			addPageHeader(document, transcriptBean);
			if(index  == transcriptBean.getSemSubjectList().size())
				generateSemWiseMarksTable(document, transcriptBean, index);
			else {
				generateSemWiseMarksTable(document, transcriptBean, index);
				generateSemWiseMarksTable(document, transcriptBean, index+1);
			}
			addPageFooter(document, writer, pageNum++, transcriptBean.isSoftCopy(),transcriptBean.getLogoRequired());
			}
			document.newPage();
			addLogoTable(document, transcriptBean);

			addExplainatoryDetailsPage(document, transcriptBean.getStudent().getConsumerProgramStructureId());
			addPageFooter(document, writer, pageNum++, transcriptBean.isSoftCopy(),transcriptBean.getLogoRequired());
		}catch(Exception e){
			
			throw e;
		}finally{
			document.close(); 
		}
		return fileName;
	}

	private void addLogoTable(Document document, MBATranscriptBean transcriptBean) throws Exception {
		if(transcriptBean.getLogoRequired()!=null && transcriptBean.getLogoRequired().equalsIgnoreCase("Y")) {
			addLogo(document);
		}else {
			addSpacingForLogo(document);
		}
	}

	
	private void addDocumentHeader(Document document, MBATranscriptBean transcriptBean, PdfWriter writer, Map<String, ProgramExamBean> programDetailMap) throws Exception {
		addLogoTable(document, transcriptBean);
		
		SimpleDateFormat sdfr = new SimpleDateFormat("dd MMMM yyyy");
		String fileDate = sdfr.format(new Date());
		Paragraph datePara = new Paragraph(fileDate, font10);
		datePara.setAlignment(Element.ALIGN_RIGHT);
		document.add(datePara);

		Chunk titleChunk = new Chunk("TRANSCRIPT", font14b);
		titleChunk.setUnderline(0.1f, -2f);
		Paragraph titlePara = new Paragraph();
		titlePara.add(titleChunk);
		titlePara.setAlignment(Element.ALIGN_CENTER);
		document.add(titlePara);

		Paragraph emptyPara = new Paragraph(" ");
		emptyPara.setAlignment(Element.ALIGN_CENTER);
		document.add(emptyPara);
		
		generateStudentInfoTable(document, transcriptBean, writer, programDetailMap);
	}
	
	private void addSpacingForLogo(Document document) throws Exception {
		try {
			PdfPTable headerTable = new PdfPTable(2);
			headerTable.completeRow();
			headerTable.setWidthPercentage(100);
			headerTable.setSpacingBefore(10);
			headerTable.setSpacingAfter(10);
			document.add(headerTable);

			Paragraph emptyPara = new Paragraph(" ");
			emptyPara.setAlignment(Element.ALIGN_CENTER);
			document.add(emptyPara);
			
		} catch (Exception e) {
			
			throw e;
		}
	}
	
	private void addPageHeader(Document document, MBATranscriptBean transcriptBean) throws Exception {
		addLogoTable(document, transcriptBean);

		PdfPTable headerTable = new PdfPTable(4);
		StudentExamBean student = transcriptBean.getStudent();
		addHeaderRow("", "Student Name:", student.getFirstName() + " " + student.getLastName(), "", headerTable);
		addHeaderRow("", "Student No:", student.getSapid(), "", headerTable);
		
		headerTable.setWidthPercentage(100);
		headerTable.setSpacingBefore(5f);
		headerTable.setSpacingAfter(5f);
		float[] columnWidths = {1f, 3f, 5f, 7f};
		headerTable.setWidths(columnWidths);
		document.add(headerTable);
	}

	private void addHeaderRow(String col1, String col2, String col3, String col4, PdfPTable table) {
		PdfPCell cell1 = new PdfPCell(new Paragraph(col1, font10));
		cell1.setBorder(0);
		cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(cell1);
		
		PdfPCell cell2 = new PdfPCell(new Paragraph(col2, font10));
		cell2.setBorder(0);
		cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(cell2);

		PdfPCell cell3 = new PdfPCell(new Paragraph(col3, font10));
		cell3.setBorder(0);
		cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(cell3);

		PdfPCell cell4 = new PdfPCell(new Paragraph(col4, font10));
		cell4.setBorder(0);
		cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(cell4);
		
		table.completeRow();
	}

	private void addPageFooter(Document document, PdfWriter writer, int pageNum, boolean isSoftCopy,String logoRequired) throws MalformedURLException, IOException, DocumentException {
		Rectangle pageBounds = document.getPageSize();
		if(isSoftCopy) {
			Phrase watermark = new Phrase("Transcript Downloaded from NGASCE Student Portal", new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
			PdfContentByte canvas = writer.getDirectContent();
			ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 300, 30);
		}

		addCOESignature(document);

		PdfPTable footerTable = new PdfPTable(1);

		Phrase uniNamePhrase = new Phrase();
		uniNamePhrase.add(new Chunk("SVKM’S ", font6));
		uniNamePhrase.add(new Chunk("Narsee Monjee Institute of Management Studies", font12b));
		Paragraph uniNameParagraph = new Paragraph();
		uniNameParagraph.add(uniNamePhrase);

		if(logoRequired!=null && logoRequired.equalsIgnoreCase("Y")) { 
			addFooterTableCell(uniNameParagraph, footerTable);		
			addFooterTableCell(new Paragraph("Deemed to be UNIVERSITY", font10), footerTable);
			addFooterTableCell(new Paragraph("2nd Floor, NMIMS Building, V.L. Mehta Road, Vile Parle (W), Mumbai - 400 056, India.", font10), footerTable);
			addFooterTableCell(new Paragraph("Tel: (91-22) 42355555 | Toll Free: 18001025136 | Email: ngasce@nmims.edu | Web: executive.nmims.edu", font10), footerTable);
		}else {	
		}
		
		PdfPCell pageNumCell = new PdfPCell(new Paragraph("Page " + pageNum + " of 4", font10));
		pageNumCell.setBorder(0);
		pageNumCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		footerTable.addCell(pageNumCell);
		footerTable.completeRow();
		
		footerTable.setTotalWidth(pageBounds.getWidth());
		footerTable.writeSelectedRows( 0, -1, document.leftMargin(), footerTable.getTotalHeight() + document.bottom(document.bottomMargin()), writer.getDirectContent());
	}
	
	private void addFooterTableCell(Paragraph content, PdfPTable table) {
		PdfPCell cell = new PdfPCell(content);
		cell.setBorder(0);
		table.addCell(cell);
		table.completeRow();
	}

	private void addCOESignature(Document document) throws MalformedURLException, IOException, DocumentException {

		Image signature = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/signature.jpg"));
		signature.scaleAbsolute(40, 28);
		signature.setAlignment(Element.ALIGN_LEFT);

		PdfPTable signatureTable = new PdfPTable(1);
		PdfPCell coeSignCell = new PdfPCell();
		coeSignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		coeSignCell.addElement(signature);
		coeSignCell.setBorder(0);

		signatureTable.addCell(coeSignCell);
		signatureTable.completeRow();
		document.add(signatureTable);

		Paragraph coe= new Paragraph("Controller of Examinations" , font12b);
		coe.setAlignment(Element.ALIGN_LEFT);
		document.add(coe);
	}


	private void generateStudentInfoTable(Document document, MBATranscriptBean bean, PdfWriter writer, Map<String, ProgramExamBean> programDetailMap) throws Exception {
		try {
			StudentExamBean student = bean.getStudent();
			// Get program duration from program details
			String duration = programDetailMap.get(student.getProgram()+"-"+student.getPrgmStructApplicable()).getProgramDuration();
			String durationUnits = programDetailMap.get(student.getProgram()+"-"+student.getPrgmStructApplicable()).getProgramDurationUnit();
			//Added `modeOfLearning` instead of `Distance Learning Mode`
			String programDuration = StringUtils.capitalize(duration)+" "+StringUtils.capitalize(durationUnits)+" ("+programDetailMap.get(student.getProgram()+"-"+student.getPrgmStructApplicable()).getModeOfLearning()+")";
			
			String mod = programDetailMap.get(student.getProgram()+"-"+student.getPrgmStructApplicable()).getModeOfLearning();
			
			PdfPTable studentInfoTable = null;
			studentInfoTable = new PdfPTable(2);

			String enrollmentMonth = formatMonthName(student.getEnrollmentMonth());
			String lastExamMonthYear;
			if(StringUtils.isBlank(bean.getPassYearMonth())) {
				lastExamMonthYear = "Pursuing";
			} else {
				lastExamMonthYear = formatMonthName(bean.getPassYearMonth());
			}
			addStudentInfoTableCells("Student Name: ", student.getFirstName().toUpperCase()+" "+student.getLastName().toUpperCase(), studentInfoTable);
			addStudentInfoTableCells("Student No: ", student.getSapid(), studentInfoTable);
			addStudentInfoTableCells("Father Name: ", student.getFatherName().toUpperCase(), studentInfoTable);
			addStudentInfoTableCells("Mother Name: ", student.getMotherName().toUpperCase(), studentInfoTable);
			if  ("131".equals(student.getConsumerProgramStructureId())) {
				addStudentInfoTableCells("Program Name / Instruction Medium: ", "Master of Science in Artificial Intelligence and Machine Learning Ops/English", studentInfoTable);
			}
			else if ("158".equals(student.getConsumerProgramStructureId())) {
				addStudentInfoTableCells("Program Name / Instruction Medium: ", "Master of Science in Artificial Intelligence/English", studentInfoTable);
			}
			else if ("154".equals(student.getConsumerProgramStructureId())) {
				addStudentInfoTableCells("Program Name / Instruction Medium: ", "Professional Certificate in Data Science/English", studentInfoTable);
			}
			else {
				addStudentInfoTableCells("Program Name / Instruction Medium: ", "Professional Diploma in Data Science/English", studentInfoTable);
			}
			addStudentInfoTableCells("Specialisation (Single / Dual):", bean.getSpecialisation(), studentInfoTable);
			addStudentInfoTableCells("Duration of the Program (Mode of Delivery): ", programDuration, studentInfoTable);
			addStudentInfoTableCells("Year of Enrollment/Leaving: ", enrollmentMonth+"-"+student.getEnrollmentYear()+"/"+lastExamMonthYear, studentInfoTable);

			studentInfoTable.setWidthPercentage(100);
			float[] columnWidths = {12.5f, 16f};
			studentInfoTable.setWidths(columnWidths);
			document.add(studentInfoTable);
		} catch (Exception e) {
			
			throw e;
		}
	}
	private void addStudentInfoTableCells(String cell1Content, String cell2Content, PdfPTable studentInfoTable) {
		PdfPCell studentNameLabelCell = new PdfPCell(new Paragraph(cell1Content, font10b));
		PdfPCell studentNameValueCell = new PdfPCell(new Paragraph(cell2Content , font10));
		studentNameLabelCell.setFixedHeight(14f);
		studentNameLabelCell.setBorder(Rectangle.NO_BORDER);
		studentNameValueCell.setBorder(Rectangle.NO_BORDER);
		studentInfoTable.addCell(studentNameLabelCell);
		studentInfoTable.addCell(studentNameValueCell);
		studentInfoTable.completeRow();
	}

	private void generateSemWiseMarksTable(Document document, MBATranscriptBean transcriptBean, int semester) throws Exception {
		try{
			MBAMarksheetBean termInfoBean = transcriptBean.getSemSubjectList().get(semester);
//			StudentBean student = transcriptBean.getStudent();
			String semesterRomanNumeral;
			if(semester == 1) {
				semesterRomanNumeral = "I";
			} else if(semester == 2) {
				semesterRomanNumeral = "II";
			} else if(semester == 3) {
				semesterRomanNumeral = "III";
			} else if(semester == 4) {
				semesterRomanNumeral = "IV";
			} else if(semester == 5) {
				semesterRomanNumeral = "V";
			} else if(semester == 6) {
				semesterRomanNumeral = "VI";
			} else if(semester == 7) {
				semesterRomanNumeral = "VII";	
			} else if(semester == 8) {
				semesterRomanNumeral = "VIII";
			} else {
				semesterRomanNumeral = "I";
			}
			if(!termInfoBean.isAppearedForTerm()){
				Paragraph semesterNamePara = new Paragraph("TERM-" + semesterRomanNumeral + "*", font13b);
				semesterNamePara.setAlignment(Element.ALIGN_LEFT);
				document.add(semesterNamePara);

				Paragraph noticePara = new Paragraph("*The student has still not passed the examination of Term-" + semester, font10);
				noticePara.setAlignment(Element.ALIGN_LEFT);
				document.add(noticePara);

				Paragraph emptyPara = new Paragraph(" ");
				emptyPara.setAlignment(Element.ALIGN_CENTER);
				document.add(emptyPara);
				return;
			}
			Paragraph semesterNamePara = new Paragraph("TERM-" + semesterRomanNumeral, font13b);
			semesterNamePara.setAlignment(Element.ALIGN_LEFT);
			document.add(semesterNamePara);
			PdfPTable subjectsTable = new PdfPTable(5);

			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font10b));
			PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subject", font10b));
			PdfPCell monthYearCell = new PdfPCell(new Paragraph("Month & Year of Examination", font10b));
			PdfPCell credits = new PdfPCell(new Paragraph("Credit/s", font10b));
			PdfPCell grade = new PdfPCell(new Paragraph("Grade", font10b));

			srNoCell.setFixedHeight(30);
			srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
			subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
			monthYearCell.setVerticalAlignment(Element.ALIGN_TOP);
			credits.setVerticalAlignment(Element.ALIGN_TOP);
			grade.setVerticalAlignment(Element.ALIGN_TOP);

			srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			subjectsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			monthYearCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			credits.setHorizontalAlignment(Element.ALIGN_CENTER);
			grade.setHorizontalAlignment(Element.ALIGN_CENTER);

			subjectsCell.setPaddingLeft(5);
			
			subjectsTable.addCell(srNoCell);
			subjectsTable.addCell(subjectsCell);
			subjectsTable.addCell(monthYearCell);
			subjectsTable.addCell(credits);
			subjectsTable.addCell(grade);
			subjectsTable.completeRow();

			Chunk carryFwd = new Chunk("#",font6);
			carryFwd.setTextRise(5f);

			List<MBAPassFailBean> passFailInfo = termInfoBean.getMarksList();
			for (int j = 0; j < passFailInfo.size(); j++) {
				MBAPassFailBean subjectBean = passFailInfo.get(j);

				srNoCell = new PdfPCell(new Paragraph(j+1+"", font10));
				Paragraph subjectName = new Paragraph(subjectBean.getSubject(), font10);
				subjectsCell = new PdfPCell(subjectName);

				String examMonthYear = getMonthYear(subjectBean.getExamMonth(), subjectBean.getExamYear());
				
				monthYearCell = new PdfPCell(new Paragraph(examMonthYear, font10));
				credits = new PdfPCell(new Paragraph(subjectBean.getCredits(), font10));
				
				Paragraph gradeObtained = new Paragraph(subjectBean.getGrade(), font10);

				boolean marksBoughtForward = !(subjectBean.getExamMonth().equals(termInfoBean.getClearExamMonth())) || !(subjectBean.getExamYear().equals(termInfoBean.getClearExamYear()));
				if(marksBoughtForward) {
					gradeObtained.add(carryFwd);
				}
				grade = new PdfPCell(gradeObtained);
				srNoCell.setFixedHeight(20f);
				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER); srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				/*subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER);*/ subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				monthYearCell.setHorizontalAlignment(Element.ALIGN_CENTER); monthYearCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				credits.setHorizontalAlignment(Element.ALIGN_CENTER); credits.setVerticalAlignment(Element.ALIGN_MIDDLE);
				grade.setHorizontalAlignment(Element.ALIGN_CENTER); grade.setVerticalAlignment(Element.ALIGN_MIDDLE);

				subjectsCell.setPaddingLeft(5);
				
				subjectsTable.addCell(srNoCell);
				subjectsTable.addCell(subjectsCell);
				subjectsTable.addCell(monthYearCell);
				subjectsTable.addCell(credits);
				subjectsTable.addCell(grade);
				subjectsTable.completeRow();
			}

			subjectsTable.setWidthPercentage(100);
			subjectsTable.setSpacingBefore(5f);
//			subjectsTable.setSpacingAfter(5f);
			float[] marksColumnWidths = {1f, 8f, 2.5f, 2f, 2f};
			subjectsTable.setWidths(marksColumnWidths);
			document.add(subjectsTable);


			Paragraph broughtForward = new Paragraph("#: Grades brought forward", font10);
			broughtForward.setAlignment(Element.ALIGN_LEFT);
			document.add(broughtForward);


			PdfPTable semStatusTable= new PdfPTable(5);
			addSemStatusRow( semStatusTable, "", "Grade Point Average (GPA):", termInfoBean.getGpa(), "", "" );

			addSemStatusRow( semStatusTable, "", "Cumulative Grade Point Average (CGPA):", termInfoBean.getCgpa(), "", "REMARK: " + termInfoBean.getRemark() );
			semStatusTable.setWidthPercentage(100);
			semStatusTable.setSpacingBefore(10f);
			semStatusTable.setSpacingAfter(15f);
			float[] semStatusColumnWidths = {1f, 7f, 2f, 0.5f, 7.5f };
			semStatusTable.setWidths(semStatusColumnWidths);
			document.add(semStatusTable);

		}catch(Exception e){
			throw e;
		}
	}

	private void addSemStatusRow(PdfPTable row, String col1, String col2, String col3, String col4, String col5) {
		addSemStatusCell(row, col1);
		addSemStatusCell(row, col2);
		addSemStatusCell(row, col3);
		addSemStatusCell(row, col4);
		addSemStatusCell(row, col5);
		row.completeRow();
	}

	private void addSemStatusCell(PdfPTable row, String data) {
		PdfPCell cell = new PdfPCell(new Paragraph(data, font10));
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(0);
		row.addCell(cell);
	}

	private String getMonthYear(String month, String year) {
		return formatMonthName(month + "-" + year);
	}

	private void addLogo(Document document) throws Exception {
		Image logo = null;
		try {
			logo = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nmims_logo_new.jpg"));
			logo.scaleAbsolute(142,40);
			logo.setAlignment(Element.ALIGN_CENTER);
			
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
			
		} catch (Exception e) {
			
			throw e;
		}
	}

	private void addExplainatoryDetailsPage(Document document, String consumerProgramStructureId) throws DocumentException {

		Paragraph explainatoryNotes = new Paragraph("EXPLANATORY NOTES", font11b);
		explainatoryNotes.setAlignment(Element.ALIGN_LEFT);
		document.add(explainatoryNotes);
		
		addGradePointInfoTable(document);
		
		Paragraph creditStructrueHeader = new Paragraph("CREDIT STRUCTURE", font11b);
		creditStructrueHeader.setAlignment(Element.ALIGN_LEFT);
		document.add(creditStructrueHeader);

		Paragraph creditStructrue = new Paragraph(""
			+ "Credit structure is defined in terms of contact hours assigned for various academic components of a programme.\n"
			+ "This includes online live lectures, internal assessments, class interactions, project work, term-end examination "
			+ "and any other academic activity for which contact hours are assigned in the curriculum. The details are as follows:", font10
		);
		creditStructrue.setAlignment(Element.ALIGN_LEFT);
		document.add(creditStructrue);
		
		Paragraph trimesterPatternHeader = new Paragraph("TRIMESTER PATTERN", font11b);
		trimesterPatternHeader.setAlignment(Element.ALIGN_LEFT);
		document.add(trimesterPatternHeader);
		
		addTrimesterPatternInfoTable(document, consumerProgramStructureId);	
	}

	private void addGradePointInfoTable(Document document) throws DocumentException {

		PdfPTable gpaInfoTable = new PdfPTable(4);
		addGPAInfoTableHeader(gpaInfoTable);
		
		addGradePointsTableRow("A+","4.00","100%","85%", gpaInfoTable);
		addGradePointsTableRow("A","3.75","84.99%","81%", gpaInfoTable);
		addGradePointsTableRow("A-","3.50","80.99%","77%", gpaInfoTable);
		addGradePointsTableRow("B+","3.25","76.99%","73%", gpaInfoTable);
		addGradePointsTableRow("B","3.00","72.99%","69%", gpaInfoTable);
		addGradePointsTableRow("B-","2.75","68.99%","65%", gpaInfoTable);
		addGradePointsTableRow("C+","2.50","64.99%","61%", gpaInfoTable);
		addGradePointsTableRow("C","2.25","60.99%","57%", gpaInfoTable);
		addGradePointsTableRow("C-","2.00","56.99%","50%", gpaInfoTable);
		addGradePointsTableRow("F","0.00","49.99%","0", gpaInfoTable);

		gpaInfoTable.setWidthPercentage(60);
		gpaInfoTable.setSpacingBefore(10);
		gpaInfoTable.setSpacingAfter(10);
		float[] columnWidths = {1f, 1f, 1f, 1f };
		gpaInfoTable.setWidths(columnWidths);
		
		document.add(gpaInfoTable);
	}
	
	private void addGradePointsTableRow(String grade, String points, String ciStart, String ciEnd, PdfPTable gpaInfoTable) {

		PdfPCell gradeCell = new PdfPCell(new Paragraph(grade, font11));
		gradeCell.setFixedHeight(20f);
		gradeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		gradeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		gpaInfoTable.addCell(gradeCell);

		PdfPCell pointsCell = new PdfPCell(new Paragraph(points, font11));
		pointsCell.setFixedHeight(20f);
		pointsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		pointsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		gpaInfoTable.addCell(pointsCell);

		PdfPCell ciMarksStartCell = new PdfPCell(new Paragraph(ciStart, font11));
		ciMarksStartCell.setFixedHeight(20f);
		ciMarksStartCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		ciMarksStartCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		gpaInfoTable.addCell(ciMarksStartCell);
		
		PdfPCell ciMarksEndCell = new PdfPCell(new Paragraph(ciEnd, font11));
		ciMarksEndCell.setFixedHeight(20f);
		ciMarksEndCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		ciMarksEndCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		gpaInfoTable.addCell(ciMarksEndCell);

		gpaInfoTable.completeRow();
	}

	private void addGPAInfoTableHeader(PdfPTable gpaInfoTable) {
		  
		PdfPCell grade = new PdfPCell(new Paragraph("Grade", font11b));
		grade.setFixedHeight(30f);
		grade.setVerticalAlignment(Element.ALIGN_MIDDLE);
		grade.setHorizontalAlignment(Element.ALIGN_CENTER);
		gpaInfoTable.addCell(grade);

		PdfPCell points = new PdfPCell(new Paragraph("Points", font11b));
		points.setFixedHeight(30f);
		points.setVerticalAlignment(Element.ALIGN_MIDDLE);
		points.setHorizontalAlignment(Element.ALIGN_CENTER);
		gpaInfoTable.addCell(points);

		PdfPCell ciMarks = new PdfPCell(new Paragraph("Class Interval of Marks", font11b));
		ciMarks.setFixedHeight(30f);
		ciMarks.setVerticalAlignment(Element.ALIGN_MIDDLE);
		ciMarks.setHorizontalAlignment(Element.ALIGN_CENTER);
		ciMarks.setColspan(2);
		gpaInfoTable.addCell(ciMarks);

		gpaInfoTable.completeRow();
	}
	


	private void addTrimesterPatternInfoTable(Document document, String consumerProgramStructureId) throws DocumentException {

		PdfPTable trimesterPatternTable = new PdfPTable(3);
		addTrimesterPatternTableHeader(trimesterPatternTable);			
		addTrimesterPatternTableData("Academic Learning Activities", "1 credit", "30 hrs", trimesterPatternTable);			
		trimesterPatternTable.setWidthPercentage(85);
		trimesterPatternTable.setSpacingBefore(10);
		trimesterPatternTable.setSpacingAfter(10);
		float[] columnWidths = { 1.5f, 1f, 1f };
		trimesterPatternTable.setWidths(columnWidths);
		
		document.add(trimesterPatternTable);
	}
	

	private void addTrimesterPatternTableHeader(PdfPTable trimesterPatternTable) {

		PdfPCell details = new PdfPCell(new Paragraph("Details", font10b));
		details.setFixedHeight(30f);
		details.setVerticalAlignment(Element.ALIGN_MIDDLE);
		details.setHorizontalAlignment(Element.ALIGN_LEFT);
		details.setPaddingLeft(5);
		trimesterPatternTable.addCell(details);

		PdfPCell credits = new PdfPCell(new Paragraph("Credits", font10b));
		credits.setFixedHeight(30f);
		credits.setVerticalAlignment(Element.ALIGN_MIDDLE);
		credits.setHorizontalAlignment(Element.ALIGN_LEFT);
		credits.setPaddingLeft(5);
		trimesterPatternTable.addCell(credits);

		PdfPCell eqInHrs = new PdfPCell(new Paragraph("Equivalence in hrs", font10b));
		eqInHrs.setFixedHeight(30f);
		eqInHrs.setVerticalAlignment(Element.ALIGN_MIDDLE);
		eqInHrs.setHorizontalAlignment(Element.ALIGN_LEFT);
		eqInHrs.setPaddingLeft(5);
		trimesterPatternTable.addCell(eqInHrs);

		trimesterPatternTable.completeRow();
		
	}
	private void addTrimesterPatternTableData(String details, String credits, String eqInHrs, PdfPTable trimesterPatternTable) {

		PdfPCell detailsCell = new PdfPCell(new Paragraph(details, font10));
		detailsCell.setFixedHeight(20f);
		detailsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		detailsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		detailsCell.setPaddingLeft(5);
		trimesterPatternTable.addCell(detailsCell);

		PdfPCell creditsCell = new PdfPCell(new Paragraph(credits, font10));
		creditsCell.setFixedHeight(20f);
		creditsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		creditsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		creditsCell.setPaddingLeft(5);
		trimesterPatternTable.addCell(creditsCell);

		PdfPCell eqInHrsCell = new PdfPCell(new Paragraph(eqInHrs, font10));
		eqInHrsCell.setFixedHeight(20f);
		eqInHrsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		eqInHrsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		eqInHrsCell.setPaddingLeft(5);
		trimesterPatternTable.addCell(eqInHrsCell);

		trimesterPatternTable.completeRow();
	}
	
	private String formatMonthName(String s) {
		s = s.replaceAll("Jan", "January");
		s = s.replaceAll("Feb", "February");
		s = s.replaceAll("Mar", "March");
		s = s.replaceAll("Apr", "April");
		s = s.replaceAll("May", "May");
		s = s.replaceAll("Jun", "June");
		s = s.replaceAll("Jul", "July");
		s = s.replaceAll("Aug", "August");
		s = s.replaceAll("Sep", "September");
		s = s.replaceAll("Oct", "October");
		s = s.replaceAll("Nov", "November");
		s = s.replaceAll("Dec", "December");
		return s;
	}
	
	}
	



