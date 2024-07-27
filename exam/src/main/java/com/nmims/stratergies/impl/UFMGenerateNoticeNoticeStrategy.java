package com.nmims.stratergies.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.nmims.beans.UFMIncidentBean;
import com.nmims.beans.UFMNoticeBean;
import com.nmims.helpers.FileUploadHelper;
import com.nmims.stratergies.UFMGenerateNoticeNoticeStrategyInterface;

@Service("ufmGenerateNoticeNoticeStrategy")
public class UFMGenerateNoticeNoticeStrategy implements UFMGenerateNoticeNoticeStrategyInterface {

	Font FONT_BODY_STANDARD = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
	Font FONT_BODY_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
	Font FONT_BODY_BOLD_SMALLER = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD);
	Font FONT_BODY_SMALLER = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.NORMAL);
	Font FONT_BODY_BOLD_ITALIC = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLDITALIC);
	
	@Value("${UFM_NOTICE_PATH}")
	private String UFM_NOTICE_PATH;
	
	@Autowired
	FileUploadHelper fileuploadHelper;
	
	private final static String base_UFMPath = "UFM/";
	
	@Value("${UFM_NOTICE_BUCKET}")
	private String base_UFM;
	
	public static final Logger ufm = LoggerFactory.getLogger("ufm");

	@Override
	public void generatePDF(UFMNoticeBean bean, List<UFMIncidentBean> incidentDetails) throws Exception {
		Document document = new Document(PageSize.A4);
		document.setMargins(75, 75, 40, 30);

		String sapid = bean.getSapid();
		
		String directoryPath = UFM_NOTICE_PATH + bean.getMonth() + bean.getYear();
		File directory = new File(directoryPath);
	    if (! directory.exists()){
	        directory.mkdir();
	    }

		new RandomStringUtils();
		String fileName = sapid + "_" + RandomStringUtils.randomNumeric(6) + ".pdf";
//		bean.set
		
		fileName = fileName.replace(" ", "_");
		PdfWriter.getInstance(document, new FileOutputStream(directoryPath+"/"+fileName));

		document.open();

		generateHeaderTable(document, bean);
		generateStudentInfoTable(document, bean);

		switch(bean.getStage()) {
			case UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_AWAITING_STUDENT_RESPONSE:
				bean.setShowCauseNoticeURL(bean.getMonth() + bean.getYear() + "/" + fileName);
				generateShowCauseNotice(bean, document,incidentDetails);
				break;
			case UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_STUDENT_RESPONDED:
				throw new Exception("No format available for generating notice for this stage!");
			case UFMNoticeBean.UFM_STAGE_PENALTY_ISSUED:
				bean.setDecisionNoticeURL(bean.getMonth() + bean.getYear() + "/" + fileName);
				generatePenaltyNoticeSingleSubject(bean, document);
				break;
			case UFMNoticeBean.UFM_STAGE_WARNING_ISSUED:
				bean.setDecisionNoticeURL(bean.getMonth() + bean.getYear() + "/" + fileName);
				generateWarningNotice(bean, document);
				break;
		}
		generateCOETable(document);
		
		document.close();
		
		//Upload local receipt file to s3
		fileuploadHelper.uploadDocument(directoryPath+ "/"+fileName, base_UFMPath,base_UFM );
		
		
	}
	
	@Override
	public void generateShowCauseNotice(UFMNoticeBean bean, Document document,List<UFMIncidentBean> incidentDetails) throws Exception {
		generateSalutation(document);
		Paragraph title = new Paragraph("Re: " + bean.getMonth() + "-" + bean.getYear() + " Final Examination, Student ID: " + bean.getSapid(),FONT_BODY_STANDARD);
//		title.setIndentationLeft(10);
		document.add(title);
		Paragraph p1 = new Paragraph(""
//			+ "You have appeared for the examination as per the above details. "
			+ "It has been brought to our notice by the Examination proctor, which has also been verified by officials of ", 
			FONT_BODY_STANDARD
		);
		Chunk p1Chunk = new Chunk(""
			+ "NMIMS, that you "
			+ "have violated exam rules. It has been reported as under:\n" 
//			FONT_BODY_BOLD
		);
		p1.add(p1Chunk);
		p1.setSpacingBefore(10);
		p1.setSpacingAfter(10);
		//p1.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p1);
		
//		Paragraph p = new Paragraph();
//		Font zapfdingbats = new Font(FontFamily.ZAPFDINGBATS, 8);
//		Chunk bullet = new Chunk(String.valueOf((char) 108), zapfdingbats);
//		generateTitleAndSubject(document, bean);
		for (UFMIncidentBean UFMincidentbean : incidentDetails) {
			String str = UFMincidentbean.getSubject() + ", Exam Date: " + getFormattedDateString(UFMincidentbean.getExamDate()+".");
			Paragraph subject = new Paragraph("Sub: " + str, FONT_BODY_BOLD);
			document.add(subject);
			Paragraph incidentParagraph = new Paragraph();
			Chunk IncidentBold= new Chunk("Incident  : ", FONT_BODY_BOLD);
			Chunk incidentChunk = new Chunk(UFMincidentbean.getIncident(),FONT_BODY_STANDARD);
			incidentParagraph.add(IncidentBold);
			incidentParagraph.add(incidentChunk);
			document.add(incidentParagraph);
			Paragraph videoDetailAndTimeStamp = new Paragraph();
			Chunk videoNumberChunk = new Chunk("Video Detail : ",FONT_BODY_BOLD );
			Chunk videoChunk = new Chunk(UFMincidentbean.getVideo_Number() ,FONT_BODY_STANDARD);
			Chunk blankSpace = new Chunk("                         ");
			Chunk timeStamp = new Chunk("Time Stamp : ",FONT_BODY_BOLD);
			Chunk timeStampDetails = new Chunk(UFMincidentbean.getTime_Stamp(),FONT_BODY_STANDARD);
			videoDetailAndTimeStamp.add(videoNumberChunk);
			videoDetailAndTimeStamp.add(videoChunk);
			videoDetailAndTimeStamp.add(blankSpace);
			videoDetailAndTimeStamp.add(timeStamp);
			videoDetailAndTimeStamp.add(timeStampDetails);
			videoDetailAndTimeStamp .setSpacingAfter(20);
			document.add(videoDetailAndTimeStamp);
		}
//		String incident =generateCommaSepratedListFromList(incidents);
//		Chunk p1Chunk2 = new Chunk ( "  " + bean.getUfmMarkReason(), FONT_BODY_BOLD);
//		p.add(bullet);
//		p.add(p1Chunk2);
//		p.setSpacingAfter(10);
//		p.setIndentationLeft(10);
//		document.add(p);
		
		Paragraph p2 = new Paragraph(""
			+ "The above act of yours indicates adoptation of unfair means which is in violation of the "
				+ "rules of the examination and guidelines issued. "
				+ "As per NMIMS policy, severe penalty would be imposed on the candidates who are found to be "
				+ "involved in the adoptation of unfair means in the examinations. ", 
			FONT_BODY_STANDARD
		);
		p2.setAlignment(Element.ALIGN_JUSTIFIED);
		p2.setSpacingAfter(10);
		document.add(p2);

		Paragraph p3 = new Paragraph(""
			+ "You are hereby required to provide written explanation and show cause as to why action "
				+ "should not be taken against you as per the rules of the examinations of SVKM's Narsee Monjee "
				+ "Institute of Management Studies (NMIMS). You have to ensure that the said explanation is received "
				+ "by us on or before ", 
			FONT_BODY_STANDARD
		);
		SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		String endDateTime = "" + bean.getShowCauseDeadline();
		Date date;
		try {
			date = sdfIn.parse(endDateTime);
//			SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM, yyyy");
			SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM, yyyy. HH:mm 'hrs'");
			endDateTime = sdfOut.format(date);
		} catch (ParseException e) {
			ufm.info("Exception is:"+e.getMessage());
		}
		
		Chunk p3Chunk = new Chunk(endDateTime, FONT_BODY_BOLD);
		p3.add(p3Chunk);
		p3.setSpacingAfter(10);
		p3.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p3);
		
		Paragraph p4 = new Paragraph(""
			+ "If the above charges are proved, the following penalty could be imposed upon you: ",
			FONT_BODY_STANDARD
		);
		
		List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
		String subjects = "";
		int index = 0;
		for (UFMNoticeBean subjectBean : subjectsList) {
			index++;
			if(index == 1) {
				subjects += subjectBean.getSubject();
			} else {
				subjects += ", " + subjectBean.getSubject();
			}
		}
		
		Chunk p4Chunk = new Chunk(""
			+ "\"Treat your performance in all the subjects appeared in the above - mentioned subject's, as null and void.\"",
			FONT_BODY_BOLD
		);
		p4.add(p4Chunk);
		p4.setSpacingAfter(10);
		p4.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p4);
		
		Paragraph p5 = new Paragraph(""
			+ "Post we review your explanation, if required you may be further requested to be present online"
			+ " before the Unfair Means Inquiry Committee as per dates decided by the University.",
			FONT_BODY_STANDARD
		);
		p5.setSpacingAfter(10);
		p5.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p5);
		
		Paragraph p6 = new Paragraph("Action will be taken as per rules, in case you fail to provide explanation before the deadline.",
				FONT_BODY_STANDARD);
		p6.setSpacingAfter(10);
		p6.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p6);
	}

	@Override
	public void generateWarningNotice(UFMNoticeBean bean, Document document) throws Exception {
		generateSalutation(document);
		generateTitleAndSubject(document, bean);
		generateSalutation(document);
		
		Paragraph p1 = new Paragraph("This has reference to the show cause notice dated ", FONT_BODY_STANDARD);
		
		Chunk p1Chunk1 = new Chunk("" + getFormattedDateTimeString(bean.getShowCauseGenerationDate()), FONT_BODY_BOLD);
		Chunk p1Chunk2 = new Chunk(" issued to you.", FONT_BODY_STANDARD);
		p1.add(p1Chunk1);
		p1.add(p1Chunk2);
		//p1.setSpacingBefore(10);
		p1.setSpacingAfter(10);
		document.add(p1);

		Paragraph p2 = new Paragraph(""
			+ "After considering all the aspects of the case and your written explanation (in case provided), it has been "
			+ "decided to exonerate you from the charges of adoption of unfair means and give you the ", 
			FONT_BODY_STANDARD
		);
		Chunk p2Chunk1 = new Chunk("Benefit of Doubt", FONT_BODY_BOLD);
		Chunk p2Chunk2 = new Chunk(". Your result for the said examination will be declared shortly.", FONT_BODY_STANDARD);
		p2.setAlignment(Element.ALIGN_JUSTIFIED);
		p2.add(p2Chunk1);
		p2.add(p2Chunk2);
		p2.setSpacingAfter(10);
		document.add(p2);
		
		Paragraph p3 = new Paragraph("However, you are warned to avoid any suspicious activity during the examination in future.", FONT_BODY_STANDARD);
		p3.setAlignment(Element.ALIGN_JUSTIFIED);
		p3.setSpacingAfter(10);
		document.add(p3);
	}

	@Override
	public void generatePenaltyNoticeSingleSubject(UFMNoticeBean bean, Document document) throws Exception {
		generateSalutation(document);
		generateTitleAndSubject(document, bean);
		
		Paragraph p1 = new Paragraph("This has reference to the show cause notice dated ", FONT_BODY_STANDARD);
		Chunk p1Chunk1 = new Chunk("" + getFormattedDateString(bean.getShowCauseGenerationDate()), FONT_BODY_BOLD);
		Chunk p1Chunk2 = new Chunk(" issued to you.\n", FONT_BODY_STANDARD);
		Chunk p1Chunk3 = new Chunk("You were also given opportunity to submit your explanation to the University.", FONT_BODY_STANDARD);
		
		p1.add(p1Chunk1);
		p1.add(p1Chunk2);
		p1.add(p1Chunk3);
		p1.setSpacingBefore(10);
		p1.setSpacingAfter(10);
		document.add(p1);

		Paragraph p2 = new Paragraph(""
			+ "After considering all the aspects of the case and your written explanation (in case provided), "
			+ "you are found guilty of using unfair means in the examination "
			+ "and therefore, it has been decided to treat your performance in ", 
			FONT_BODY_STANDARD
		);
		
		List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
		String subjects = "";
		int index = 0;
		for (UFMNoticeBean subjectBean : subjectsList) {
			index++;
			if(index == 1) {
				subjects += subjectBean.getSubject();
			} else {
				subjects += ", " + subjectBean.getSubject();
			}
		}
		
		Chunk p2Chunk1 = new Chunk("\"" + subjects + "\"", FONT_BODY_BOLD);
		Chunk p2Chunk2 = new Chunk(""
			+ " that you have appeared as above, as null and void.", 
			FONT_BODY_STANDARD
		);
		p2.add(p2Chunk1);
		p2.add(p2Chunk2);
		p2.setSpacingAfter(10);
		p2.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p2);

		Paragraph p3 = new Paragraph(""
			+ "You will have to appear at the re-examination of the above subject as and when it "
			+ "is scheduled as per the rules of the University.", 
			FONT_BODY_STANDARD
		);
		p3.setSpacingAfter(10);
		p3.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p3);

		Paragraph p4 = new Paragraph(""
			+ "You are hereby warned to avoid recurrence of such wrongful conduct and indulgence in unfair "
			+ "means in any manner whatsoever in future examinations of Narsee Monjee Institute of Management "
			+ "Studies (NMIMS- Declared as Deemed-to-be University under Section 3 of the UGC Act, 1956).",
			FONT_BODY_STANDARD
		);
		p4.setSpacingAfter(10);
		p4.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p4);
	}	
	
	private void generateHeaderTable(Document document, UFMNoticeBean bean) throws Exception {
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
			
			//Added By shivam.pandey.EXT - for Align left and right in same line - START
			String fileDate = getFormattedDateString(new Date());
			String label = "NMIMS/EXAM/NGA-SCE/NM/UF/2022-23";
			
			Chunk leftContent = new Chunk(label,FONT_BODY_STANDARD);
			Chunk rightContent = new Chunk(fileDate,FONT_BODY_STANDARD);
			
			Paragraph p = new Paragraph();
			p.add(leftContent);
			p.add(new Chunk(new VerticalPositionMark())); //this will create equally space between left and right content
			p.add(rightContent);
			p.setSpacingAfter(10);
			document.add(p);
			//Added By shivam.pandey.EXT - for Align left and right in same line - END
			
			
			/*String fileDate = getFormattedDateString(new Date());
			Paragraph datePara = new Paragraph(fileDate, FONT_BODY_STANDARD);
			datePara.setAlignment(Element.ALIGN_RIGHT);*/
			//document.add(datePara);
			
		} catch (Exception e) {
			ufm.info("Exception :"+e.getMessage());
			throw e;
		}
	}

	private void generateStudentInfoTable(Document document, UFMNoticeBean bean) throws DocumentException {

		Paragraph studentNamePara = new Paragraph(new Chunk(bean.getFirstName() + " " + bean.getLastName(), FONT_BODY_BOLD));
		//Chunk studentNameChunk = new Chunk(bean.getFirstName() + " " + bean.getLastName(), FONT_BODY_BOLD);
		//studentNamePara.add(studentNameChunk);

		Paragraph lcPara = new Paragraph(new Chunk("LC-", FONT_BODY_STANDARD));
		Chunk lcNameChunk = new Chunk("" + bean.getLcName().toUpperCase(), FONT_BODY_STANDARD);
		lcPara.add(lcNameChunk);
		 
		document.add(studentNamePara);
		document.add(new Paragraph(new Chunk("NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION", FONT_BODY_STANDARD)));
		document.add(new Paragraph(new Chunk("SVKM's NMIMS (Deemed-to-be University)", FONT_BODY_STANDARD)));
		document.add(lcPara);
	}
	
	private void generateSalutation(Document document) throws DocumentException {
		Paragraph salutation = new Paragraph("Dear Student,", FONT_BODY_STANDARD);
		salutation.setSpacingAfter(10);
		salutation.setSpacingBefore(10);
		document.add(salutation);
	}
	
	public void generateTitleAndSubject(Document document, UFMNoticeBean bean) throws Exception {
		
		List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
		int index = 0;
		for (UFMNoticeBean subjectBean : subjectsList) {
			index++;
			String str = subjectBean.getSubject() + ", Exam Date: " + getFormattedDateString(subjectBean.getExamDate()+".");
			if(index == 1) {
				Paragraph subject = new Paragraph("Sub: " + str, FONT_BODY_BOLD);
//				subject.setIndentationLeft(10);
				document.add(subject);
			} else {
				Paragraph subject = new Paragraph("Sub: " + str, FONT_BODY_BOLD);
//				subject.setIndentationLeft(10);
				document.add(subject);
			}
			
		}
	}	

	private void generateCOETable(Document document) throws DocumentException {
//		Paragraph coe = new Paragraph("Ashish Apte\nController of Examinations" , FONT_BODY_STANDARD);
		Paragraph coe = new Paragraph("Controller of Examinations" , FONT_BODY_STANDARD);
		coe.setAlignment(Element.ALIGN_LEFT);
		coe.setSpacingBefore(20);
		coe.setSpacingAfter(6);
		document.add(coe);
		
		Paragraph systemGenerated = new Paragraph("(This is system generated letter and no signature is required)", FONT_BODY_SMALLER);
		systemGenerated.setAlignment(Element.ALIGN_LEFT);
		document.add(systemGenerated);
	}
	

	private String getFormattedDateTimeString(String input) {

		SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date;
		try {
			date = sdfIn.parse(input);
			SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM, yyyy");
			return sdfOut.format(date);
		} catch (ParseException e) {
			ufm.info("Exception is:"+e.getMessage());
			return input;
		}
	}
	
	private String getFormattedDateString(String input) {

		SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = sdfIn.parse(input);
			SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM, yyyy");
			return sdfOut.format(date);
		} catch (ParseException e) {
			ufm.info("Exception is:"+e.getMessage());
			return input;
		}
	}
	
	private String getFormattedDateString(Date date) {
		SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM, yyyy");
		return sdfOut.format(date);
	}
	
	
	/**
	 * shivam.pandey.EXT COC - START
	 */
	@Override
	public void generateCOCPDF(UFMNoticeBean bean,List<UFMIncidentBean> incidentDetails) throws Exception {
		Document document = new Document(PageSize.A4);
		document.setMargins(75, 75, 40, 30);

		String sapid = bean.getSapid();
		
		String directoryPath = UFM_NOTICE_PATH + bean.getMonth() + bean.getYear();
		File directory = new File(directoryPath);
	    if (! directory.exists()){
	        directory.mkdir();
	    }

		new RandomStringUtils();
		String fileName = sapid + "_" + RandomStringUtils.randomNumeric(6) + ".pdf";
		
		fileName = fileName.replace(" ", "_");
		PdfWriter.getInstance(document, new FileOutputStream(directoryPath+"/"+fileName));

		document.open();

		generateCOCHeaderTable(document, bean);
		generateCOCStudentInfoTable(document, bean);

		switch(bean.getStage()) {
			case UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_AWAITING_STUDENT_RESPONSE:
				bean.setShowCauseNoticeURL(bean.getMonth() + bean.getYear() + "/" + fileName);
				generateCOCShowCauseNotice(bean, document,incidentDetails);
				break;
			case UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_STUDENT_RESPONDED: 
				throw new Exception("No format available for generating notice for this stage!");
			case UFMNoticeBean.UFM_STAGE_PENALTY_ISSUED:
				bean.setDecisionNoticeURL(bean.getMonth() + bean.getYear() + "/" + fileName);
				generateCOCPenaltyNotice(bean, document);
				break;
			case UFMNoticeBean.UFM_STAGE_WARNING_ISSUED:
				bean.setDecisionNoticeURL(bean.getMonth() + bean.getYear() + "/" + fileName);
				generateCOCWarningNotice(bean, document);
				break;
		}
		generateCOCCOETable(document);
		
		document.close();
		
		//Upload local receipt file to s3
		fileuploadHelper.uploadDocument(directoryPath+ "/"+fileName, base_UFMPath,base_UFM );
		
	}
	
	//To Generate COC Penalty PDF
	public void generateCOCPenaltyNotice(UFMNoticeBean bean, Document document) throws Exception {
		generateCOCSalutation(document);
		generateCOCTitleAndSubject(document, bean);
		
		Paragraph p1 = new Paragraph("This has reference to the show cause notice dated ", FONT_BODY_STANDARD);
		Chunk p1Chunk1 = new Chunk("" + getFormattedDateString(bean.getShowCauseGenerationDate()), FONT_BODY_BOLD);
		Chunk p1Chunk2 = new Chunk(" issued to you.\n", FONT_BODY_STANDARD);
		Chunk p1Chunk3 = new Chunk("You were also given opportunity to submit your explanation to the University.", FONT_BODY_STANDARD);
		
		p1.add(p1Chunk1);
		p1.add(p1Chunk2);
		p1.add(p1Chunk3);
		p1.setSpacingBefore(10);
		p1.setSpacingAfter(10);
		document.add(p1);

		Paragraph p2 = new Paragraph(""
			+ "After considering all the aspects of the case and your written explanation (in case provided), "
			+ "you are found guilty of breaching code of conduct during the examination "
			+ "and therefore, it has been decided to treat your performance in the subject ", 
			FONT_BODY_STANDARD
		);
		
		List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
		String subjects = "";
		int index = 0;
		for (UFMNoticeBean subjectBean : subjectsList) {
			index++;
			if(index == 1) {
				subjects += subjectBean.getSubject();
			} else {
				subjects += ", " + subjectBean.getSubject();
			}
		}
		
		Chunk p2Chunk1 = new Chunk("\"" + subjects + "\"", FONT_BODY_BOLD);
		Chunk p2Chunk2 = new Chunk(""
			+ " that you have appeared as above, as null and void.", 
			FONT_BODY_STANDARD
		);
		p2.add(p2Chunk1);
		p2.add(p2Chunk2);
		p2.setSpacingAfter(10);
		p2.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p2);

		Paragraph p3 = new Paragraph(""
			+ "You will have to appear at the re-examination of the above subject as and when it "
			+ "is scheduled as per the rules of the University.", 
			FONT_BODY_STANDARD
		);
		p3.setSpacingAfter(10);
		p3.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p3);

		Paragraph p4 = new Paragraph(""
			+ "You are hereby warned to avoid recurrence of such wrongful conduct "
			+ "in any manner whatsoever in future examinations of Narsee Monjee Institute of Management "
			+ "Studies (NMIMS-Declared as Deemed-to-be University under Section 3 of the UGC Act, 1956).",
			FONT_BODY_STANDARD
		);
		p4.setSpacingAfter(10);
		p4.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p4);
		
	}
	
	//To Generate COC Show Cause PDF
	public void generateCOCShowCauseNotice(UFMNoticeBean bean, Document document, List<UFMIncidentBean> incidentDetails) throws Exception {
		generateCOCSalutation(document);
		Paragraph title = new Paragraph("Re: " + bean.getMonth() + "-" + bean.getYear() + " Final Examination, Student ID: " + bean.getSapid(), FONT_BODY_STANDARD);
//		title.setIndentationLeft(10);
		document.add(title);
				
		Paragraph p1 = new Paragraph(""
//			+ "You have appeared for the examination as per the above details. "
			+ "It has been brought to our notice by the Examination proctor, which has also been verified by officials of ", 
			FONT_BODY_STANDARD
		);
		Chunk p1Chunk = new Chunk(""
			+ "NMIMS, that you "
			+ "have violated exam code of conduct. It has been reported as under:\n"
//			FONT_BODY_BOLD
		);
		p1.add(p1Chunk);
		p1.setSpacingBefore(10);
		p1.setSpacingAfter(10);
		document.add(p1);
		
//		generateCOCTitleAndSubject(document, bean);
		for (UFMIncidentBean UFMincidentbean : incidentDetails) {
			String str = UFMincidentbean.getSubject() + ", Exam Date: " + getFormattedDateString(UFMincidentbean.getExamDate()+".");
			Paragraph subject = new Paragraph("Sub: " + str, FONT_BODY_BOLD);
			document.add(subject);
			Paragraph incidentParagraph = new Paragraph();
			Chunk incidentBold = new Chunk("Incident  : ", FONT_BODY_BOLD);
			Chunk incidentChunk = new Chunk(UFMincidentbean.getIncident(),FONT_BODY_STANDARD);
			incidentParagraph.add(incidentBold);
			incidentParagraph.add(incidentChunk);
			document.add(incidentParagraph);
			Paragraph videoDetailAndTimeStamp = new Paragraph();
			Chunk videoNumberChunk = new Chunk("Video Detail : ",FONT_BODY_BOLD );
			Chunk videoChunk = new Chunk(UFMincidentbean.getVideo_Number() ,FONT_BODY_STANDARD);
			Chunk blankSpace = new Chunk("                         ");
			Chunk timeStamp = new Chunk("Time Stamp : ",FONT_BODY_BOLD);
			Chunk timeStampDetails = new Chunk(UFMincidentbean.getTime_Stamp(),FONT_BODY_STANDARD);
			videoDetailAndTimeStamp.add(videoNumberChunk);
			videoDetailAndTimeStamp.add(videoChunk);
			videoDetailAndTimeStamp.add(blankSpace);
			videoDetailAndTimeStamp.add(timeStamp);
			videoDetailAndTimeStamp.add(timeStampDetails);
//			videoDetail.setSpacingBefore(10);
			videoDetailAndTimeStamp .setSpacingAfter(20);
			document.add(videoDetailAndTimeStamp );
		}
		// Removed line For Incident Upload :-for appearance at the above examination
		Paragraph p2 = new Paragraph(""
			+ "The above act of yours indicates breach of code of conduct which is in violation of the "
			+ "rules of the examination and guidelines issued. "
			+ "As per NMIMS policy, severe penalty would be imposed on the candidates who are found to have "
			+ "violated the code of conduct during exams. ", 
			FONT_BODY_STANDARD
		);
		p2.setAlignment(Element.ALIGN_JUSTIFIED);
		p2.setSpacingAfter(10);
		document.add(p2);

		Paragraph p3 = new Paragraph(""
			+ "You are hereby required to provide written explanation and show cause as to why action "
			+ "should not be taken against you as per the rules of the examinations of SVKM's Narsee Monjee "
			+ "Institute of Management Studies (NMIMS). You have to ensure that the said explanation is received "
			+ "by us on or before ", 
			FONT_BODY_STANDARD
		);
		SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		String endDateTime = "" + bean.getShowCauseDeadline();
		Date date;
		try {
			date = sdfIn.parse(endDateTime);
			SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM, yyyy. HH:mm 'hrs'");
			endDateTime = sdfOut.format(date);
		} catch (ParseException e) {
			ufm.info("Exception is:"+e.getMessage());
		}
		
		Chunk p3Chunk = new Chunk(endDateTime, FONT_BODY_BOLD);
		p3.add(p3Chunk);
		p3.setSpacingAfter(10);
		p3.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p3);
		
		Paragraph p4 = new Paragraph(""
			+ "If the above charges are proved, the following penalty could be imposed upon you: ",
			FONT_BODY_STANDARD
		);
		
//		List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
//		String subjects = "";
//		int index = 0;
//		for (UFMNoticeBean subjectBean : subjectsList) {
//			index++;
//			if(index == 1) {
//				subjects += subjectBean.getSubject();
//			} else {
//				subjects += ", " + subjectBean.getSubject();
//			}
//		}
		
		Chunk p4Chunk = new Chunk(""
			+ "\"Treat your performance in all the subjects appeared in the above mentioned subject's, as null and void.\"",
			FONT_BODY_BOLD
		);
		p4.add(p4Chunk);
		p4.setSpacingAfter(10);
		p4.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p4);
		
		Paragraph p5 = new Paragraph(""
			+ "Post we review your explanation, if required you may be further requested to be present online"
			+ " before the Unfair Means Inquiry Committee as per dates decided by the University.",
			FONT_BODY_STANDARD
		);
		p5.setSpacingAfter(10);
		p5.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p5);
		
		Paragraph p6 = new Paragraph("Action will be taken as per rules, in case you fail to provide explanation before the deadline.",
				FONT_BODY_STANDARD);
		p6.setSpacingAfter(10);
		p6.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p6);
	}
	
	//To Generate COC Warning PDF
	public void generateCOCWarningNotice(UFMNoticeBean bean, Document document) throws Exception {
		generateCOCSalutation(document);
		generateCOCTitleAndSubject(document, bean);
		generateCOCSalutation(document);
		
		Paragraph p1 = new Paragraph("This has reference to the show cause notice dated ", FONT_BODY_STANDARD);
		
		Chunk p1Chunk1 = new Chunk("" + getFormattedDateTimeString(bean.getShowCauseGenerationDate()), FONT_BODY_BOLD);
		Chunk p1Chunk2 = new Chunk(" issued to you.", FONT_BODY_STANDARD);
		p1.add(p1Chunk1);
		p1.add(p1Chunk2);
		p1.setSpacingAfter(10);
		document.add(p1);

		Paragraph p2 = new Paragraph(""
			+ "After considering all the aspects of the case and your written explanation (in case provided), it has been "
			+ "decided to exonerate you from the charges of breaching exam code of conduct and give you the ", 
			FONT_BODY_STANDARD
		);
		Chunk p2Chunk1 = new Chunk("Benefit of Doubt", FONT_BODY_BOLD);
		Chunk p2Chunk2 = new Chunk(". Your result for the said examination will be declared shortly.", FONT_BODY_STANDARD);
		p2.setAlignment(Element.ALIGN_JUSTIFIED);
		p2.add(p2Chunk1);
		p2.add(p2Chunk2);
		p2.setSpacingAfter(10);
		document.add(p2);
		
		Paragraph p3 = new Paragraph("However, you are warned to avoid any suspicious activity during the examination in future.", FONT_BODY_STANDARD);
		p3.setAlignment(Element.ALIGN_JUSTIFIED);
		p3.setSpacingAfter(10);
		document.add(p3);
		
	}
	
	//To Generate COC PDF Title - RE & SUB
	public void generateCOCTitleAndSubject(Document document, UFMNoticeBean bean) throws Exception {
		
		List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
		int index = 0;
		for (UFMNoticeBean subjectBean : subjectsList) {
			index++;
			String str = subjectBean.getSubject() + ", Exam Date: " + getFormattedDateString(subjectBean.getExamDate()+".");
			if(index == 1) {
				Paragraph subject = new Paragraph("Sub: " + str, FONT_BODY_BOLD);
				document.add(subject);
			} else {
				Paragraph subject = new Paragraph("Sub: " + str, FONT_BODY_BOLD);
				document.add(subject);
			}
			
		}
	}
	
	//To Generate COC Salutation For Student
	private void generateCOCSalutation(Document document) throws DocumentException {
		Paragraph salutation = new Paragraph("Dear Student,", FONT_BODY_STANDARD);
		salutation.setSpacingAfter(10);
		salutation.setSpacingBefore(10);
		document.add(salutation);
	}
	
	//To Generate COC PDF Footer
	private void generateCOCCOETable(Document document) throws DocumentException {
		Paragraph coe = new Paragraph("Controller of Examinations" , FONT_BODY_STANDARD);
		coe.setAlignment(Element.ALIGN_LEFT);
		coe.setSpacingBefore(20);
		coe.setSpacingAfter(6);
		document.add(coe);
		
		Paragraph systemGenerated = new Paragraph("(This is system generated letter and no signature is required)", FONT_BODY_SMALLER);
		systemGenerated.setAlignment(Element.ALIGN_LEFT);
		document.add(systemGenerated);
	}
	
	//To Generate COC PDF Header
	private void generateCOCHeaderTable(Document document, UFMNoticeBean bean) throws Exception {
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
			
			//Added By shivam.pandey.EXT - for Align left and right in same line - START
			String fileDate = getFormattedDateString(new Date());
			String label = "NMIMS/EXAM/NGA-SCE/NM/UF/2022-23";
			
			Chunk leftContent = new Chunk(label,FONT_BODY_STANDARD);
			Chunk rightContent = new Chunk(fileDate,FONT_BODY_STANDARD);
			
			Paragraph p = new Paragraph();
			p.add(leftContent);
			p.add(new Chunk(new VerticalPositionMark())); //this will create equally space between left and right content
			p.add(rightContent);
			p.setSpacingAfter(10);
			document.add(p);
			//Added By shivam.pandey.EXT - for Align left and right in same line - END
			
		} catch (Exception e) {
			ufm.info("Exception :"+e.getMessage());
			throw e;
		}
	}
	
	//To Generate COC PDF Student Info
	private void generateCOCStudentInfoTable(Document document, UFMNoticeBean bean) throws DocumentException {

		Paragraph studentNamePara = new Paragraph(new Chunk(bean.getFirstName() + " " + bean.getLastName(), FONT_BODY_BOLD));

		Paragraph lcPara = new Paragraph(new Chunk("LC-", FONT_BODY_STANDARD));
		Chunk lcNameChunk = new Chunk("" + bean.getLcName().toUpperCase(), FONT_BODY_STANDARD);
		lcPara.add(lcNameChunk);
		 
		document.add(studentNamePara);
		document.add(new Paragraph(new Chunk("NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION", FONT_BODY_STANDARD)));
		document.add(new Paragraph(new Chunk("SVKM's NMIMS (Deemed-to-be University)", FONT_BODY_STANDARD)));
		document.add(lcPara);
	}
	/**
	 * shivam.pandey.EXT COC - END
	 */
	
	
	
	
	
	
	/**
	 * shivam.pandey.EXT Disconnect Above - START
	 */
	@Override
	public void generateDisconnectAbovePDF(UFMNoticeBean bean) throws Exception {
		Document document = new Document(PageSize.A4);
		document.setMargins(75, 75, 40, 30);

		String sapid = bean.getSapid();
		
		String directoryPath = UFM_NOTICE_PATH + bean.getMonth() + bean.getYear();
		File directory = new File(directoryPath);
	    if (! directory.exists()){
	        directory.mkdir();
	    }

		new RandomStringUtils();
		String fileName = sapid + "_" + RandomStringUtils.randomNumeric(6) + ".pdf";
		
		fileName = fileName.replace(" ", "_");
		PdfWriter.getInstance(document, new FileOutputStream(directoryPath+"/"+fileName));

		document.open();

		generateDisconnectAboveHeaderTable(document, bean);
		generateDisconnectAboveStudentInfoTable(document, bean);

		switch(bean.getStage()) {
			case UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_AWAITING_STUDENT_RESPONSE:
				bean.setShowCauseNoticeURL(bean.getMonth() + bean.getYear() + "/" + fileName);
				generateDisconnectAboveShowCauseNotice(bean, document);
				break;
			case UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_STUDENT_RESPONDED: 
				throw new Exception("No format available for generating notice for this stage!");
			case UFMNoticeBean.UFM_STAGE_PENALTY_ISSUED:
				bean.setDecisionNoticeURL(bean.getMonth() + bean.getYear() + "/" + fileName);
				generateDisconnectAbovePenaltyNotice(bean, document);
				break;
			case UFMNoticeBean.UFM_STAGE_WARNING_ISSUED:
				bean.setDecisionNoticeURL(bean.getMonth() + bean.getYear() + "/" + fileName);
				generateDisconnectAboveWarningNotice(bean, document);
				break;
		}
		generateDisconnectAboveCOETable(document);
		
		document.close();
		
		//Upload local receipt file to s3
		fileuploadHelper.uploadDocument(directoryPath+ "/"+fileName, base_UFMPath,base_UFM );
		
	}
	
	//To Generate Disconnect Above Penalty PDF
	public void generateDisconnectAbovePenaltyNotice(UFMNoticeBean bean, Document document) throws Exception {
		
		Paragraph p1 = new Paragraph(""
				+ "This is with reference to the communication dated 20 December 2022, ", FONT_BODY_STANDARD
			);
			
			Chunk p1Chunk2 = new Chunk("wherein you were informed about the policy regarding multiple disruptions during the ", FONT_BODY_STANDARD);
			Chunk p1Chunk3 = new Chunk(bean.getMonth()+" "+bean.getYear(), FONT_BODY_BOLD);
			
			Chunk p1Chunk4 = new Chunk(" Term End Exams.", FONT_BODY_STANDARD);
			
			//p1.add(p1Chunk1);
			p1.add(p1Chunk2);
			p1.add(p1Chunk3);
			p1.add(p1Chunk4);
			p1.setSpacingBefore(10);
			p1.setSpacingAfter(10);
			p1.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p1);
			
//			String subjects = "";
//			int index = 0;
			com.itextpdf.text.List listUnordered= new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
			Chunk bullet = new Chunk("\u2022", FONT_BODY_BOLD);
			listUnordered.setListSymbol(bullet+" ");
			List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
			for (UFMNoticeBean subjectBean : subjectsList) {
				Chunk subjectChunk = new Chunk(subjectBean.getSubject(),FONT_BODY_BOLD);
				listUnordered.add(new ListItem(subjectChunk));

//				index++;
//				if(index == 1) {
//					subjects += subjectBean.getSubject();
//				} else {
//					subjects += ", " + subjectBean.getSubject();
//				}
			}
			
			Paragraph p2 = new Paragraph();
			Chunk p2Chunk1 = new Chunk("As per the communication sent and rules of the exam, it is the responsibility of "
					+ "the student to ensure a stable internet connection and a conducive environment for the successful completion of the exam. "
					+ "It has been brought to our attention that there were multiple disconnections during the term end exam conducted ", FONT_BODY_STANDARD);
			Chunk p2Chunk2 = new Chunk("on "+getFormattedDateString(bean.getExamDate())+" for ", FONT_BODY_BOLD);
			
			Chunk p2Chunk3 = new Chunk("which disrupted the integrity of the test since you were unavailable for monitoring by proctors during the disconnected duration (more than 15 minutes).", FONT_BODY_STANDARD);
			p2.add(p2Chunk1);
			p2.add(p2Chunk2);
			p2.add(listUnordered);
			p2.add(p2Chunk3);
			p2.setSpacingAfter(10);
			p2.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p2);
			
			Paragraph p3 = new Paragraph("Therefore, it has been decided to treat your performance in the subject",FONT_BODY_STANDARD);
			Chunk p3Chunk2 = new Chunk(""
				+ "that you have appeared as above, as null and void.", 
				FONT_BODY_STANDARD
			);
			p3.add(listUnordered);
			p3.add(p3Chunk2);
			p3.setSpacingAfter(10);
			p3.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p3);
			
			Paragraph p4 = new Paragraph(""
				+ "You will have to appear at the re-examination of the above subject as and when it "
				+ "is scheduled as per the rules of the University.", 
				FONT_BODY_STANDARD
			);
			p4.setSpacingAfter(10);
			p4.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p4);
			
			Paragraph p5 = new Paragraph(""
				+ "We hope that you will take this notice seriously and take the necessary steps to ensure that such incidents do not occur in the future.",
				FONT_BODY_STANDARD
			);
			p5.setSpacingAfter(10);
			p5.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p5);
	}
	
	//To Generate Disconnect Above Show Cause PDF
		public void generateDisconnectAboveShowCauseNotice(UFMNoticeBean bean, Document document) throws Exception {
			
			Paragraph p2 = new Paragraph(""
				+ "This is with reference to the communication dated 20 December 2022, ", FONT_BODY_STANDARD
			);
			
			Chunk p2Chunk2 = new Chunk("wherein you were informed about the policy regarding multiple disruptions during the ", FONT_BODY_STANDARD);
			Chunk p2Chunk3 = new Chunk(bean.getMonth()+" "+bean.getYear(), FONT_BODY_BOLD);
			
			Chunk p2Chunk4 = new Chunk(" Term End Exams.", FONT_BODY_STANDARD);
			
			//p2.add(p2Chunk1);
			p2.add(p2Chunk2);
			p2.add(p2Chunk3);
			p2.add(p2Chunk4);
			p2.setSpacingBefore(10);
			p2.setSpacingAfter(10);
			p2.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p2);
			com.itextpdf.text.List listUnordered= new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
			Chunk bullet = new Chunk("\u2022", FONT_BODY_BOLD);
			listUnordered.setListSymbol(bullet+" ");
			List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
//			String subjects = "";
//			int index = 0;
			for (UFMNoticeBean subjectBean : subjectsList) {
				Chunk subjectChunk = new Chunk(subjectBean.getSubject(),FONT_BODY_BOLD);
				listUnordered.add(new ListItem(subjectChunk));

//				index++;
//				if(index == 1) {
//					subjects += subjectBean.getSubject();
//				} else {
//					subjects += ", " + subjectBean.getSubject();
//				}
			}
			Paragraph p3 = new Paragraph();
			//removed subject
			Chunk p3Chunk1 = new Chunk("As per the communication sent and rules of the exam, it is the responsibility of "
					+ "the student to ensure a stable internet connection and a conducive environment for the successful completion of the exam. "
					+ "It has been brought to our attention that there were multiple disconnections during the term end exam conducted ", FONT_BODY_STANDARD);
			Chunk p3Chunk2 = new Chunk("on "+getFormattedDateString(bean.getExamDate())+" for ", FONT_BODY_BOLD);
			
			Chunk p3Chunk3 = new Chunk("which disrupted the integrity of the test since you were unavailable for monitoring by proctors during the disconnected duration (more than 15 minutes).", FONT_BODY_STANDARD);
			p3.add(p3Chunk1);
			p3.add(p3Chunk2);
			p3.add(listUnordered);
			p3.add(p3Chunk3);
			p3.setSpacingAfter(10);
			p3.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p3);
			
			Paragraph p4 = new Paragraph(""
				+ "The above has violated the code of conduct set by SVKM’s NMIMS University for Term End exams which is considered a serious breach of academic integrity.",
				FONT_BODY_STANDARD
			);
			p4.setSpacingAfter(10);
			p4.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p4);

			Paragraph p5 = new Paragraph(""
				+ "You are hereby required to provide written explanation and show cause as to why disciplinary action "
				+ "should not be taken against you as per the rules of the examinations of SVKM's NMIMS University. "
				+ "You have to ensure that the explanation is received "
				+ "by us on or before ", 
				FONT_BODY_STANDARD
			);
			SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			String endDateTime = "" + bean.getShowCauseDeadline();
			Date date;
			try {
				date = sdfIn.parse(endDateTime);
//				SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM, yyyy");
				SimpleDateFormat sdfOut = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:aa");
				endDateTime = sdfOut.format(date);
			} catch (ParseException e) {
				ufm.info("Exception is:"+e.getMessage());
			}
			
			Chunk p3Chunk = new Chunk(endDateTime, FONT_BODY_BOLD);
			p5.add(p3Chunk);
			p5.setSpacingAfter(10);
			p5.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p5);
			
			Paragraph p6 = new Paragraph(""
				+ "Please be informed that failure to submit an explanation within the given time frame or non-satisfactory "
				+ "explanation will result in the following disciplinary action being taken against you, ",
				FONT_BODY_STANDARD
			);
			
			Chunk p6Chunk = new Chunk("\"Treat your performance in all the subjects mentioned above as null and void.\"",FONT_BODY_BOLD);
//			Chunk p7Chunk = new Chunk(" as null and void.\"",FONT_BODY_BOLD);
			p6.add(p6Chunk);
//			p6.add(listUnordered);
//			p6.add(p7Chunk);
			p6.setSpacingAfter(10);
			p6.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p6);
			
			Paragraph p7 = new Paragraph(""
				+ "We hope that you will take this notice seriously and take the necessary steps to ensure that such incidents do not occur in the future.",
				FONT_BODY_STANDARD
			);
			p7.setSpacingAfter(10);
			p7.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p7);
		}
	
	//To Generate Disconnect Above Warning PDF
	public void generateDisconnectAboveWarningNotice(UFMNoticeBean bean, Document document) throws Exception {
		generateDisconnectAboveSalutation(document);
		
		Paragraph p1 = new Paragraph(""
			+ "This is with reference to the communication dated 20 December 2022, ", FONT_BODY_STANDARD
		);
		
		Chunk p1Chunk2 = new Chunk("wherein you were informed about the policy regarding multiple disruptions during the ", FONT_BODY_STANDARD);
		Chunk p1Chunk3 = new Chunk(bean.getMonth()+" "+bean.getYear(), FONT_BODY_BOLD);
		
		Chunk p1Chunk4 = new Chunk(" Term End Exams.", FONT_BODY_STANDARD);
		
		p1.add(p1Chunk2);
		p1.add(p1Chunk3);
		p1.add(p1Chunk4);
		p1.setSpacingAfter(10);
		p1.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p1);
		
		List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
//		String subjects = "";
//		int index = 0;
		com.itextpdf.text.List listUnordered= new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
		Chunk bullet = new Chunk("\u2022", FONT_BODY_BOLD);
		listUnordered.setListSymbol(bullet+" ");
		for (UFMNoticeBean subjectBean : subjectsList) {
			
			Chunk subjectChunk = new Chunk(subjectBean.getSubject(),FONT_BODY_BOLD);
			listUnordered.add(new ListItem(subjectChunk));

//			index++;
//			if(index == 1) {
//				subjects += subjectBean.getSubject();
//			} else {
//				subjects += ", " + subjectBean.getSubject();
//			}
		}
		
		
		Paragraph p2 = new Paragraph("As per the communication sent and rules of the exam, it is the responsibility of "
				+ "the student to ensure a stable internet connection and a conducive environment for the successful completion of the exam. ",
				FONT_BODY_STANDARD
				);
		p2.setSpacingAfter(10);
		p2.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p2);
		
		
		Paragraph p3 = new Paragraph();
		Chunk p3Chunk1 = new Chunk(""
				+ "It has been brought to our attention that there were multiple disconnections during the term end exam conducted ", FONT_BODY_STANDARD);
		Chunk p3Chunk2 = new Chunk("on "+getFormattedDateString(bean.getExamDate())+" for ", FONT_BODY_BOLD);
		
		Chunk p3Chunk3 = new Chunk("which disrupted the integrity of the test since you were unavailable for monitoring by proctors during the disconnected duration.", FONT_BODY_STANDARD);
		p3.add(p3Chunk1);
		p3.add(p3Chunk2);
		p3.add(listUnordered);
		p3.add(p3Chunk3);
		p3.setSpacingAfter(10);
		p3.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p3);
		
		
		Paragraph p4 = new Paragraph(""
			+ "While your results will be declared for this exam cycle, kindly make sure you have stable internet connectivity and required infrastructure to avoid instances of such frequent disconnections, "
			+ "failing which would result in the Term End Exam to be kept in the category of Violation of Exam Code of Conduct and invalidate the attempt of the said TEE. In case you experience any infrastructure"
			+ " or internet issues, you have an option of appearing for your exams from the University campuses.",
			FONT_BODY_STANDARD
		);
		p4.setSpacingAfter(10);
		p4.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p4);
		
		
		Paragraph p5 = new Paragraph(""
			+ "We hope that you will take this notice seriously and take the necessary steps to ensure that such incidents do not occur in the future.",
			FONT_BODY_STANDARD
		);
		p5.setSpacingAfter(10);
		p5.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p5);
		
	}
	
	//To Generate Disconnect Above PDF Title - RE & SUB
	public void generateDisconnectAboveTitleAndSubject(Document document, UFMNoticeBean bean) throws Exception {
		Paragraph title = new Paragraph("Re: " + bean.getMonth() + "-" + bean.getYear() + " Final Examination, Student ID: " + bean.getSapid(), FONT_BODY_BOLD);
		title.setIndentationLeft(10);
		document.add(title);
		List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
		int index = 0;
		for (UFMNoticeBean subjectBean : subjectsList) {
			index++;
			String str = subjectBean.getSubject() + ", Exam Date: " + getFormattedDateString(subjectBean.getExamDate()+".");
			if(index == 1) {
				Paragraph subject = new Paragraph("Sub: " + str, FONT_BODY_BOLD);
				subject.setIndentationLeft(10);
				document.add(subject);
			} else {
				Paragraph subject = new Paragraph("Sub: " + str, FONT_BODY_BOLD);
				subject.setIndentationLeft(10);
				document.add(subject);
			}
			
		}
	}
	
	//To Generate Disconnect Above Salutation For Student
	private void generateDisconnectAboveSalutation(Document document) throws DocumentException {
		Paragraph salutation = new Paragraph("Dear Student,", FONT_BODY_STANDARD);
		salutation.setSpacingAfter(10);
		salutation.setSpacingBefore(10);
		document.add(salutation);
	}
	
	//To Generate Disconnect Above PDF Footer
	private void generateDisconnectAboveCOETable(Document document) throws DocumentException {
		Paragraph coe = new Paragraph("Controller of Examinations" , FONT_BODY_STANDARD);
		coe.setAlignment(Element.ALIGN_LEFT);
		coe.setSpacingBefore(20);
		coe.setSpacingAfter(6);
		document.add(coe);
		
		Paragraph systemGenerated = new Paragraph("(This is system generated letter and no signature is required)", FONT_BODY_SMALLER);
		systemGenerated.setAlignment(Element.ALIGN_LEFT);
		document.add(systemGenerated);
	}
	
	//To Generate Disconnect Above PDF Header
	private void generateDisconnectAboveHeaderTable(Document document, UFMNoticeBean bean) throws Exception {
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
			
			//Added By shivam.pandey.EXT - for Align left and right in same line - START
			String fileDate = getFormattedDateString(new Date());
			String label = "Subject: Disruptions during "+bean.getMonth()+" "+bean.getYear()+" Term End Exam";
			
			Chunk leftContent = new Chunk(label,FONT_BODY_STANDARD);
			Chunk rightContent = new Chunk(fileDate,FONT_BODY_STANDARD);
			
			Paragraph p = new Paragraph();
			p.add(leftContent);
			p.add(new Chunk(new VerticalPositionMark())); //this will create equally space between left and right content
			p.add(rightContent);
			p.setSpacingAfter(10);
			document.add(p);
			//Added By shivam.pandey.EXT - for Align left and right in same line - END
			
		} catch (Exception e) {
			ufm.info("Exception :"+e.getMessage());
			throw e;
		}
	}
	
	//To Generate Disconnect Above PDF Student Info
	private void generateDisconnectAboveStudentInfoTable(Document document, UFMNoticeBean bean) throws DocumentException {

		Paragraph studentNamePara = new Paragraph(new Chunk("Dear "+bean.getFirstName() + " " + bean.getLastName(), FONT_BODY_STANDARD));

		Paragraph lcPara = new Paragraph(new Chunk("LC-", FONT_BODY_STANDARD));
		Chunk lcNameChunk = new Chunk("" + bean.getLcName().toUpperCase(), FONT_BODY_STANDARD);
		lcPara.add(lcNameChunk);
		 
		document.add(studentNamePara);
		document.add(new Paragraph(new Chunk("NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION", FONT_BODY_STANDARD)));
		document.add(new Paragraph(new Chunk("SVKM's NMIMS UNIVERSITY", FONT_BODY_STANDARD)));
		document.add(lcPara);
	}
	/**
	 * shivam.pandey.EXT Disconnect Above - END
	 */
	
	
	
	
	
	
	
	
	/**
	 * shivam.pandey.EXT Disconnect Below - START
	 */
	@Override
	public void generateDisconnectBelowPDF(UFMNoticeBean bean) throws Exception {
		Document document = new Document(PageSize.A4);
		document.setMargins(75, 75, 40, 30);

		String sapid = bean.getSapid();
		
		String directoryPath = UFM_NOTICE_PATH + bean.getMonth() + bean.getYear();
		File directory = new File(directoryPath);
	    if (! directory.exists()){
	        directory.mkdir();
	    }

		new RandomStringUtils();
		String fileName = sapid + "_" + RandomStringUtils.randomNumeric(6) + ".pdf";
		
		fileName = fileName.replace(" ", "_");
		PdfWriter.getInstance(document, new FileOutputStream(directoryPath+"/"+fileName));

		document.open();

		generateDisconnectBelowHeaderTable(document, bean);
		generateDisconnectBelowStudentInfoTable(document, bean);

		switch(bean.getStage()) {
			case UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_AWAITING_STUDENT_RESPONSE:
				bean.setShowCauseNoticeURL(bean.getMonth() + bean.getYear() + "/" + fileName);
				generateDisconnectBelowShowCauseNotice(bean, document);
				break;
			case UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_STUDENT_RESPONDED: 
				throw new Exception("No format available for generating notice for this stage!");
			case UFMNoticeBean.UFM_STAGE_PENALTY_ISSUED:
				bean.setDecisionNoticeURL(bean.getMonth() + bean.getYear() + "/" + fileName);
				generateDisconnectBelowPenaltyNotice(bean, document);
				break;
			case UFMNoticeBean.UFM_STAGE_WARNING_ISSUED:
				bean.setDecisionNoticeURL(bean.getMonth() + bean.getYear() + "/" + fileName);
				generateDisconnectBelowWarningNotice(bean, document);
				break;
		}
		generateDisconnectBelowCOETable(document);
		
		document.close();
		
		//Upload local receipt file to s3
		fileuploadHelper.uploadDocument(directoryPath+ "/"+fileName, base_UFMPath,base_UFM );
		
	}
	
	//To Generate Disconnect Below Penalty PDF
	public void generateDisconnectBelowPenaltyNotice(UFMNoticeBean bean, Document document) throws Exception {
		
		Paragraph p1 = new Paragraph(""
				+ "This is with reference to the communication dated 20 December 2022, ", FONT_BODY_STANDARD
			);
			
			Chunk p1Chunk2 = new Chunk("wherein you were informed about the policy regarding multiple disruptions during the ", FONT_BODY_STANDARD);
			Chunk p1Chunk3 = new Chunk(bean.getMonth()+" "+bean.getYear(), FONT_BODY_BOLD);
			
			Chunk p1Chunk4 = new Chunk(" Term End Exams.", FONT_BODY_STANDARD);
			
			p1.add(p1Chunk2);
			p1.add(p1Chunk3);
			p1.add(p1Chunk4);
			p1.setSpacingBefore(10);
			p1.setSpacingAfter(10);
			p1.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p1);
			
			List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
			String subjects = "";
			int index = 0;
			for (UFMNoticeBean subjectBean : subjectsList) {
				index++;
				if(index == 1) {
					subjects += subjectBean.getSubject();
				} else {
					subjects += ", " + subjectBean.getSubject();
				}
			}
			
			Paragraph p2 = new Paragraph();
			Chunk p2Chunk1 = new Chunk("As per the communication sent and rules of the exam, it is the responsibility of "
					+ "the student to ensure a stable internet connection and a conducive environment for the successful completion of the exam. "
					+ "It has been brought to our attention that there were multiple disconnections during the term end exam conducted ", FONT_BODY_STANDARD);
			Chunk p2Chunk2 = new Chunk("on "+getFormattedDateString(bean.getExamDate())+" for "+subjects, FONT_BODY_BOLD);
			
			Chunk p2Chunk3 = new Chunk(" , which disrupted the integrity of the test since you were unavailable for monitoring by proctors during the disconnected duration (more than 15 minutes).", FONT_BODY_STANDARD);
			p2.add(p2Chunk1);
			p2.add(p2Chunk2);
			p2.add(p2Chunk3);
			p2.setSpacingAfter(10);
			p2.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p2);
			
			Paragraph p3 = new Paragraph("Therefore, it has been decided to treat your performance in the subject",FONT_BODY_STANDARD);
			Chunk p3Chunk1 = new Chunk(" \"" + subjects + "\" ", FONT_BODY_BOLD);
			Chunk p3Chunk2 = new Chunk(""
				+ "that you have appeared as above, as null and void.", 
				FONT_BODY_STANDARD
			);
			p3.add(p3Chunk1);
			p3.add(p3Chunk2);
			p3.setSpacingAfter(10);
			p3.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p3);
			
			Paragraph p4 = new Paragraph(""
				+ "You will have to appear at the re-examination of the above subject as and when it "
				+ "is scheduled as per the rules of the University.", 
				FONT_BODY_STANDARD
			);
			p4.setSpacingAfter(10);
			p4.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p4);
			
			Paragraph p5 = new Paragraph(""
				+ "We hope that you will take this notice seriously and take the necessary steps to ensure that such incidents do not occur in the future.",
				FONT_BODY_STANDARD
			);
			p5.setSpacingAfter(10);
			p5.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p5);
	}
	
	//To Generate Disconnect Below Show Cause PDF
	public void generateDisconnectBelowShowCauseNotice(UFMNoticeBean bean, Document document) throws Exception {
		
		Paragraph p2 = new Paragraph(""
			+ "This is with reference to the communication dated 20 December 2022, ", FONT_BODY_STANDARD
		);
		
		Chunk p2Chunk2 = new Chunk("wherein you were informed about the policy regarding multiple disruptions during the ", FONT_BODY_STANDARD);
		Chunk p2Chunk3 = new Chunk(bean.getMonth()+" "+bean.getYear(), FONT_BODY_BOLD);
		
		Chunk p2Chunk4 = new Chunk(" Term End Exams.", FONT_BODY_STANDARD);
		
		p2.add(p2Chunk2);
		p2.add(p2Chunk3);
		p2.add(p2Chunk4);
		p2.setSpacingBefore(10);
		p2.setSpacingAfter(10);
		p2.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p2);
		
		List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
		String subjects = "";
		int index = 0;
		for (UFMNoticeBean subjectBean : subjectsList) {
			index++;
			if(index == 1) {
				subjects += subjectBean.getSubject();
			} else {
				subjects += ", " + subjectBean.getSubject();
			}
		}
		
		Paragraph p3 = new Paragraph();
		Chunk p3Chunk1 = new Chunk("As per the communication sent and rules of the exam, it is the responsibility of "
				+ "the student to ensure a stable internet connection and a conducive environment for the successful completion of the exam. "
				+ "It has been brought to our attention that there were multiple disconnections during the term end exam conducted ", FONT_BODY_STANDARD);
		Chunk p3Chunk2 = new Chunk("on "+getFormattedDateString(bean.getExamDate())+" for "+subjects, FONT_BODY_BOLD);
		
		Chunk p3Chunk3 = new Chunk(" , which disrupted the integrity of the test since you were unavailable for monitoring by proctors during the disconnected duration (more than 15 minutes).", FONT_BODY_STANDARD);
		p3.add(p3Chunk1);
		p3.add(p3Chunk2);
		p3.add(p3Chunk3);
		p3.setSpacingAfter(10);
		p3.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p3);
		
		Paragraph p4 = new Paragraph(""
			+ "The above has violated the code of conduct set by SVKM’s NMIMS University for Term End exams which is considered a serious breach of academic integrity.",
			FONT_BODY_STANDARD
		);
		p4.setSpacingAfter(10);
		p4.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p4);

		Paragraph p5 = new Paragraph(""
			+ "You are hereby required to provide written explanation and show cause as to why disciplinary action "
			+ "should not be taken against you as per the rules of the examinations of SVKM's NMIMS University. "
			+ "You have to ensure that the explanation is received "
			+ "by us on or before ", 
			FONT_BODY_STANDARD
		);
		SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		String endDateTime = "" + bean.getShowCauseDeadline();
		Date date;
		try {
			date = sdfIn.parse(endDateTime);
			SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM, yyyy");
			endDateTime = sdfOut.format(date);
		} catch (ParseException e) {
			ufm.info("Exception is:"+e.getMessage());
		}
		
		Chunk p3Chunk = new Chunk(endDateTime, FONT_BODY_BOLD);
		p5.add(p3Chunk);
		p5.setSpacingAfter(10);
		p5.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p5);
		
		Paragraph p6 = new Paragraph(""
			+ "Please be informed that failure to submit an explanation within the given time frame or non-satisfactory "
			+ "explanation will result in the following disciplinary action being taken against you, ",
			FONT_BODY_STANDARD
		);
		
		Chunk p6Chunk = new Chunk(""
			+ "\"Treat your performance in all the subjects appeared in the above mentioned cycle / " 
			+ subjects + ", as null and void.\"",
			FONT_BODY_BOLD
		);
		p6.add(p6Chunk);
		p6.setSpacingAfter(10);
		p6.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p6);
		
		Paragraph p7 = new Paragraph(""
			+ "We hope that you will take this notice seriously and take the necessary steps to ensure that such incidents do not occur in the future.",
			FONT_BODY_STANDARD
		);
		p7.setSpacingAfter(10);
		p7.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p7);
	}
	
	//To Generate Disconnect Below Warning PDF
	public void generateDisconnectBelowWarningNotice(UFMNoticeBean bean, Document document) throws Exception {
		generateDisconnectBelowSalutation(document);
		
		Paragraph p1 = new Paragraph(""
			+ "This is with reference to the communication dated 20 December 2022, ", FONT_BODY_STANDARD
		);
		
		Chunk p1Chunk2 = new Chunk("wherein you were informed about the policy regarding multiple disruptions during the ", FONT_BODY_STANDARD);
		Chunk p1Chunk3 = new Chunk(bean.getMonth()+" "+bean.getYear(), FONT_BODY_BOLD);
		
		Chunk p1Chunk4 = new Chunk(" Term End Exams.", FONT_BODY_STANDARD);
		
		p1.add(p1Chunk2);
		p1.add(p1Chunk3);
		p1.add(p1Chunk4);
		p1.setSpacingAfter(10);
		p1.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p1);
		
		List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
		String subjects = "";
		int index = 0;
		for (UFMNoticeBean subjectBean : subjectsList) {
			index++;
			if(index == 1) {
				subjects += subjectBean.getSubject();
			} else {
				subjects += ", " + subjectBean.getSubject();
			}
		}
		
		
		Paragraph p2 = new Paragraph("As per the communication sent and rules of the exam, it is the responsibility of "
				+ "the student to ensure a stable internet connection and a conducive environment for the successful completion of the exam. ",
				FONT_BODY_STANDARD
				);
		p2.setSpacingAfter(10);
		p2.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p2);
		
		
		Paragraph p3 = new Paragraph();
		Chunk p3Chunk1 = new Chunk(""
				+ "It has been brought to our attention that there were multiple disconnections during the term end exam conducted ", FONT_BODY_STANDARD);
		Chunk p3Chunk2 = new Chunk("on "+getFormattedDateString(bean.getExamDate())+" for "+subjects, FONT_BODY_BOLD);
		
		Chunk p3Chunk3 = new Chunk(" , which disrupted the integrity of the test since you were unavailable for monitoring by proctors during the disconnected duration.", FONT_BODY_STANDARD);
		p3.add(p3Chunk1);
		p3.add(p3Chunk2);
		p3.add(p3Chunk3);
		p3.setSpacingAfter(10);
		p3.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p3);
		
		
		Paragraph p4 = new Paragraph(""
			+ "While your results will be declared for this exam cycle, kindly make sure you have stable internet connectivity and required infrastructure to avoid instances of such frequent disconnections, "
			+ "failing which would result in the Term End Exam to be kept in the category of Violation of Exam Code of Conduct and invalidate the attempt of the said TEE. In case you experience any infrastructure"
			+ " or internet issues, you have an option of appearing for your exams from the University campuses.",
			FONT_BODY_STANDARD
		);
		p4.setSpacingAfter(10);
		p4.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p4);
		
		
		Paragraph p5 = new Paragraph(""
			+ "We hope that you will take this notice seriously and take the necessary steps to ensure that such incidents do not occur in the future.",
			FONT_BODY_STANDARD
		);
		p5.setSpacingAfter(10);
		p5.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(p5);
		
	}
	
	//To Generate Disconnect Below PDF Title - RE & SUB
	public void generateDisconnectBelowTitleAndSubject(Document document, UFMNoticeBean bean) throws Exception {
		Paragraph title = new Paragraph("Re: " + bean.getMonth() + "-" + bean.getYear() + " Final Examination, Student ID: " + bean.getSapid(), FONT_BODY_BOLD);
		title.setIndentationLeft(10);
		document.add(title);
		List<UFMNoticeBean> subjectsList = bean.getSubjectsList();
		int index = 0;
		for (UFMNoticeBean subjectBean : subjectsList) {
			index++;
			String str = subjectBean.getSubject() + ", Exam Date: " + getFormattedDateString(subjectBean.getExamDate()+".");
			if(index == 1) {
				Paragraph subject = new Paragraph("Sub: " + str, FONT_BODY_BOLD);
				subject.setIndentationLeft(10);
				document.add(subject);
			} else {
				Paragraph subject = new Paragraph("Sub: " + str, FONT_BODY_BOLD);
				subject.setIndentationLeft(10);
				document.add(subject);
			}
			
		}
	}
	
	//To Generate Disconnect Below Salutation For Student
	private void generateDisconnectBelowSalutation(Document document) throws DocumentException {
		Paragraph salutation = new Paragraph("Dear Student,", FONT_BODY_STANDARD);
		salutation.setSpacingAfter(10);
		salutation.setSpacingBefore(10);
		document.add(salutation);
	}
	
	//To Generate Disconnect Below PDF Footer
	private void generateDisconnectBelowCOETable(Document document) throws DocumentException {
		Paragraph coe = new Paragraph("Controller of Examinations" , FONT_BODY_STANDARD);
		coe.setAlignment(Element.ALIGN_LEFT);
		coe.setSpacingBefore(20);
		coe.setSpacingAfter(6);
		document.add(coe);
		
		Paragraph systemGenerated = new Paragraph("(This is system generated letter and no signature is required)", FONT_BODY_SMALLER);
		systemGenerated.setAlignment(Element.ALIGN_LEFT);
		document.add(systemGenerated);
	}
	
	//To Generate Disconnect Below PDF Header
	private void generateDisconnectBelowHeaderTable(Document document, UFMNoticeBean bean) throws Exception {
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
			
			//Added By shivam.pandey.EXT - for Align left and right in same line - START
			String fileDate = getFormattedDateString(new Date());
			String label = "Subject: Disruptions during "+bean.getMonth()+" "+bean.getYear()+" Term End Exam";
			
			Chunk leftContent = new Chunk(label,FONT_BODY_STANDARD);
			Chunk rightContent = new Chunk(fileDate,FONT_BODY_STANDARD);
			
			Paragraph p = new Paragraph();
			p.add(leftContent);
			p.add(new Chunk(new VerticalPositionMark())); //this will create equally space between left and right content
			p.add(rightContent);
			p.setSpacingAfter(10);
			document.add(p);
			//Added By shivam.pandey.EXT - for Align left and right in same line - END
			
		} catch (Exception e) {
			ufm.info("Exception :"+e.getMessage());
			throw e;
		}
	}
	
	//To Generate Disconnect Below PDF Student Info
	private void generateDisconnectBelowStudentInfoTable(Document document, UFMNoticeBean bean) throws DocumentException {

		Paragraph studentNamePara = new Paragraph(new Chunk("Dear "+bean.getFirstName() + " " + bean.getLastName(), FONT_BODY_STANDARD));

		Paragraph lcPara = new Paragraph(new Chunk("LC-", FONT_BODY_STANDARD));
		Chunk lcNameChunk = new Chunk("" + bean.getLcName().toUpperCase(), FONT_BODY_STANDARD);
		lcPara.add(lcNameChunk);
		 
		document.add(studentNamePara);
		document.add(new Paragraph(new Chunk("NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION", FONT_BODY_STANDARD)));
		document.add(new Paragraph(new Chunk("SVKM's NMIMS UNIVERSITY", FONT_BODY_STANDARD)));
		document.add(lcPara);
	}
	/**
	 * shivam.pandey.EXT Disconnect Below - END
	 */
}
