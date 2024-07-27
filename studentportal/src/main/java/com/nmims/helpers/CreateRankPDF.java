package com.nmims.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentRankBean;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.services.LeaderBoardService;
import com.nmims.daos.LeaderBoardDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component 
public class CreateRankPDF {

	@Value("${RANK_SHARE_FILES_PATH}")
	private String RANK_SHARE_FILES_PATH;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH; 

	@Autowired
	LeaderBoardService leaderBoardService;
	
	@Autowired(required=false)
	ApplicationContext act;

	@Autowired
	private LeaderBoardDAO leaderBoardDAO;

	Font font3 = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
	Font font2 = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
	Font font4 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
	Font font1 = new Font(Font.FontFamily.HELVETICA, 11);
	Font font5 = new Font(Font.FontFamily.HELVETICA, 6);
	Font font6 = new Font(Font.FontFamily.HELVETICA, 8);
	Font font7 = new Font(Font.FontFamily.HELVETICA, 18);
	Font font8 = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
	Font font9 = new Font(Font.FontFamily.HELVETICA, 10);
	Font font10 = new Font(Font.FontFamily.HELVETICA, (float) 9.5, Font.BOLD);
	Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	Font descBoldFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
	Font descFont = new Font(Font.FontFamily.HELVETICA, 9);
	Font footerBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

	private static final Logger logger = LoggerFactory.getLogger(CreateRankPDF.class);
	
	public String createCycleWiseRankPDF(String sapid, String subject, String cycle) throws Exception {

		StudentRankBean rankDetails = new StudentRankBean();   
    	List<StudentRankBean> overallRankList = new ArrayList<StudentRankBean>();
    	StudentRankBean cycleWiseStudentsRank = new StudentRankBean();
    	
		Document document = new Document(PageSize.A4);
		document.setMargins(80, 50, 50, 30);
		String fileName="rank_"+sapid+"_"+cycle;   
		
		File folderPath = new File( RANK_SHARE_FILES_PATH + sapid);
		if (!folderPath.exists()) {

			boolean created = folderPath.mkdirs();

		}  

		String path = RANK_SHARE_FILES_PATH + sapid + "/" + fileName + ".pdf";
		
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream( path ));
		document.open();
		
		StudentStudentPortalBean bean = new StudentStudentPortalBean();
	    bean.setSapid( sapid );
	    bean.setSem( cycle );
	    
		try {
			bean = leaderBoardService.getCycleWiseRankConfigForSharingRank( bean.getSapid(), bean.getSem() );
		} catch (Exception e) {
			//
			logger.error("createCycleWiseRankPDF 1: "+ e.getClass() + " - " + e.getMessage());
		}
    	
        try {

        	rankDetails = leaderBoardService.getDenormalizedCycleWiseRankBySapidForLinkedIn( bean.getConsumerProgramStructureId(), bean.getYear(), 
    				bean.getMonth(), bean.getSem(), bean.getSapid(), bean.getProgram() );

        	cycleWiseStudentsRank = rankDetails.getCycleWiseStudentsRank();
			overallRankList = rankDetails.getOverAllCycleWiseRank();
			
        } catch(Exception ex){
        	
            //ex.printStackTrace();
            logger.error("createCycleWiseRankPDF 2: "+ ex.getClass() + " - " + ex.getMessage());
            
        }

		generateHeaderTable( document, "Sem: " + bean.getSem() + " | Cycle: " + bean.getMonth() + " " + bean.getYear()) ;
		generateStudentsRankDetails( document, overallRankList, cycleWiseStudentsRank, writer);
		generateRankCalculationDescription( document, "cyclewise" );

		document.close(); 
		
		return path;
	}

	public String createSubjectWiseRankPDF(String sapid, String subject, String cycle) throws Exception {

    	StudentRankBean rankDetails = new StudentRankBean();
    	List<StudentRankBean> overallRankList = new ArrayList<StudentRankBean>();
    	StudentRankBean subjectWiseStudentsRank = new StudentRankBean();
		
		Document document = new Document(PageSize.A4);
		document.setMargins(80, 50, 50, 30);
		
		String subjectWithoutSpeciChar = subject.replaceAll("'", "_");
		subjectWithoutSpeciChar = subjectWithoutSpeciChar.replaceAll(",", "_");
		subjectWithoutSpeciChar = subjectWithoutSpeciChar.replaceAll("&", "and");
		subjectWithoutSpeciChar = subjectWithoutSpeciChar.replaceAll(" ", "_");
		subjectWithoutSpeciChar = subjectWithoutSpeciChar.replaceAll(":", "_");
		subjectWithoutSpeciChar = subjectWithoutSpeciChar.replaceAll("'", "_");
		
		String fileName="rank_"+sapid+"_"+cycle+"_"+subjectWithoutSpeciChar;
		
		File folderPath = new File( RANK_SHARE_FILES_PATH + sapid);
		
		if (!folderPath.exists()) {

			boolean created = folderPath.mkdirs();

		}  

		String path = RANK_SHARE_FILES_PATH + sapid + "/" + fileName + ".pdf";

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream( path ));
		document.open();
		
		StudentStudentPortalBean bean = new StudentStudentPortalBean();
	    bean.setSapid( sapid );
	    bean.setSem( cycle );
	    
		try {
			bean = leaderBoardService.getSubjectWiseRankConfigForSharingRank( bean.getSapid(), bean.getSem(), subject );
		} catch (Exception e) {
			//
			logger.error("createSubjectWiseRankPDF : "+ e.getClass() + " - " + e.getMessage());
		}
    	
		bean.setSubject(subject);
		
        try {
        	
        	rankDetails = leaderBoardService.getDenormalizedSubjectWiseRankBySapidForLinkedIn( bean.getConsumerProgramStructureId(), bean.getProgram(),
    				bean.getYear(), bean.getMonth(), bean.getSem(), bean.getSubject(), bean.getSapid() );

        	subjectWiseStudentsRank = rankDetails.getSubjectWiseStudentsRank();
			overallRankList = rankDetails.getOverAllSubjectWiseRank();
			
        } catch(Exception ex){
        	
            //ex.printStackTrace();
        	logger.error("createSubjectWiseRankPDF : "+ ex.getClass() + " - " + ex.getMessage());
            
        }
		
		generateHeaderTable( document, "Subject: " + bean.getSubject() );
		generateStudentsRankDetails( document, overallRankList, subjectWiseStudentsRank, writer);
		generateRankCalculationDescription( document, "subjectwise" );

		document.close(); 
		
		return path;
	}
	
	public void generateRankCalculationDescription(Document document, String type) throws DocumentException {
		
		Chunk section = new Chunk("How Student's Rank is Calculated:", descFont);
		Paragraph sectionPara = new Paragraph();
		sectionPara.add(section);	
		sectionPara.setAlignment(Element.ALIGN_LEFT); 
		sectionPara.setSpacingAfter(8);	
		document.add(sectionPara);  

		Chunk rankCalDesc_1 = new Chunk("-There is one leaderboard for every semester of the student's program.", descFont);
		Paragraph rankCalDescPara_1 = new Paragraph(rankCalDesc_1);	
		rankCalDescPara_1.setAlignment(Element.ALIGN_LEFT); 
		document.add(rankCalDescPara_1); 
		
		if( "cyclewise".equals(type) ) {
			
			Chunk rankCalDesc_2 = new Chunk("-The leaderboard will display the names and scores of the top 5 ranked "
					+ "students across all subjects in that semester.", descFont);
			Paragraph rankCalDescPara_2 = new Paragraph(rankCalDesc_2);	
			rankCalDescPara_2.setAlignment(Element.ALIGN_LEFT); 
			document.add(rankCalDescPara_2); 
		
		}else {
			
			Chunk rankCalDesc_2 = new Chunk("-The leaderboard will display the names and scores of the top 5 ranked "
					+ "students.", descFont);
			Paragraph rankCalDescPara_2 = new Paragraph(rankCalDesc_2);	
			rankCalDescPara_2.setAlignment(Element.ALIGN_LEFT); 
			document.add(rankCalDescPara_2); 
		
		}
		
		if( "cyclewise".equals(type) ) {
			
			Chunk rankCalDesc_3 = new Chunk("-The leaderboard will also display where does the student stand (student's rank) among "
					+ "his/her fellow students in the same semester of his/her program.", descFont);
			Paragraph rankCalDescPara_3 = new Paragraph(rankCalDesc_3);	
			rankCalDescPara_3.setAlignment(Element.ALIGN_LEFT); 
			document.add(rankCalDescPara_3); 
			
		}else {

			Chunk rankCalDesc_3 = new Chunk("-The leaderboard will also display where does the student stand (student's rank) among "
					+ "his/her fellow students in that same subject of the semester.", descFont);
			Paragraph rankCalDescPara_3 = new Paragraph(rankCalDesc_3);	
			rankCalDescPara_3.setAlignment(Element.ALIGN_LEFT); 
			document.add(rankCalDescPara_3); 
			
		}
		
		if( "cyclewise".equals(type) ) {
			
			Chunk rankCalDesc_4 = new Chunk("-The student will see your rank only if he/she has cleared all of his/her subjects "
					+ "in the very first attempt as per his/her semester registration month and year. The ranking will "
					+ "not be displayed for the students that pass a subject in backlog.", descFont);
			Paragraph rankCalDescPara_4 = new Paragraph(rankCalDesc_4);	
			rankCalDescPara_4.setAlignment(Element.ALIGN_LEFT); 
			document.add(rankCalDescPara_4); 

		}else {

			Chunk rankCalDesc_4 = new Chunk("-The student will see his/her rank only if you have cleared the subject in the very"
					+ " first attempt as per his/her semester registration month and year.", descFont);
			Paragraph rankCalDescPara_4 = new Paragraph(rankCalDesc_4);	
			rankCalDescPara_4.setAlignment(Element.ALIGN_LEFT); 
			document.add(rankCalDescPara_4); 

		}
		
		if( "cyclewise".equals(type) ) {
			
			Chunk rankCalDesc_5 = new Chunk("-The scores are calculated based on the Assignment and TEE marks obtained "
					+ "in each subject, out of 100.", descFont);
			Paragraph rankCalDescPara_5 = new Paragraph(rankCalDesc_5);	
			rankCalDescPara_5.setAlignment(Element.ALIGN_LEFT); 
			document.add(rankCalDescPara_5); 
			
		}else {

			Chunk rankCalDesc_5 = new Chunk("-The scores are calculated based on the Assignment and TEE marks obtained "
					+ "in the subject, out of 100.", descFont);
			Paragraph rankCalDescPara_5 = new Paragraph(rankCalDesc_5);	
			rankCalDescPara_5.setAlignment(Element.ALIGN_LEFT); 
			document.add(rankCalDescPara_5); 
			
		}
		
		Chunk rankCalDesc_6 = new Chunk("-Group of students having the same semester registration month/year, "
				+ "program and program structure is considered as a batch for rank calculation.", descFont);
		Paragraph rankCalDescPara_6 = new Paragraph(rankCalDesc_6);	
		rankCalDescPara_6.setAlignment(Element.ALIGN_LEFT); 
		document.add(rankCalDescPara_6); 

	}

	public void generateStudentsRankDetails( Document document, List<StudentRankBean> rank, StudentRankBean studentsRank, PdfWriter writer )
			throws DocumentException {
		
		for( StudentRankBean rankDetails : rank) {

			PdfPTable table = new PdfPTable( 2 ); 
			PdfPCell logoCell = new PdfPCell();
			PdfPCell rankDetailCell = new PdfPCell();
			
			Chunk nameChunk = new Chunk( rankDetails.getName(), font1);
			Paragraph nameParam = new Paragraph(nameChunk);	
			nameParam.setAlignment(Element.ALIGN_LEFT); 

			Chunk rankChunk = null;
			try {
				Double score = Double.parseDouble( rankDetails.getTotal() );
				rankChunk = new Chunk( rankDetails.getRank()+" Rank | Score: " +score.intValue() , headerFont);
			}catch (Exception e) {
				rankChunk = new Chunk( rankDetails.getRank()+" Rank | Score: " +rankDetails.getTotal() , headerFont);
			}
			
			Paragraph rankParam = new Paragraph(rankChunk);	
			rankParam.setAlignment(Element.ALIGN_LEFT); 
			
			rankDetailCell.addElement(nameParam);
			rankDetailCell.addElement(rankParam);
			rankDetailCell.setBorder(0);  
			rankDetailCell.setPaddingTop(10);
			rankDetailCell.setPaddingLeft(20);

			Image logo = null;
			
			try {
				logo = Image.getInstance(new URL( rankDetails.getStudentImage() ));
			} catch (BadElementException e) {
				
			} catch (MalformedURLException e) {
				
			} catch (IOException e) {
				
			};
		
			logo.scaleAbsolute(250, 350);
			logo.setAlignment(Element.ALIGN_CENTER);

			PdfTemplate template = PdfTemplate.createTemplate(writer, 200, 200);
			template.roundRectangle(0, 0, 200, 200, 100);
			template.clip();
			template.newPath();
			template.addImage(logo, 200, 0, 0, 350, 0, -100);
			Image clippedLogo = Image.getInstance(template);
			clippedLogo.setWidthPercentage(20);
			clippedLogo.setAlignment(Element.ALIGN_RIGHT);
			
			logoCell.addElement(clippedLogo);
			logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			logoCell.setBorder(0);  
			logoCell.setPaddingTop(15);
			
			table.addCell(logoCell);
			table.addCell(rankDetailCell);
			table.completeRow();
			table.setHorizontalAlignment(Element.ALIGN_LEFT); 

			document.add(table); 
			
			Chunk dividerChunk = new Chunk( "__________________________________", font7);
			Paragraph dividerParam = new Paragraph(dividerChunk);	
			dividerParam.setAlignment(Element.ALIGN_CENTER); 
			document.add(dividerParam);
			
		}
		
		Chunk studentsRankChunk = null;
		
		if( StringUtils.isBlank( studentsRank.getRank() ) || "null".equalsIgnoreCase( studentsRank.getRank() ) ) {

			studentsRankChunk = new Chunk( studentsRank.getName()+"'s Rank: Not Applicable", headerFont);
			
		}else {

			studentsRankChunk = new Chunk( studentsRank.getName()+"'s Rank: "+studentsRank.getRank(), headerFont);
			
		}
		
		Paragraph studentsRankParam = new Paragraph(studentsRankChunk);	
		studentsRankParam.setAlignment(Element.ALIGN_CENTER); 
		studentsRankParam.setSpacingBefore(10);
		studentsRankParam.setSpacingAfter(10);
		document.add(studentsRankParam); 
		
		Chunk studentsScoreChunk = null;
		
		if( StringUtils.isBlank( studentsRank.getTotal() ) || "null".equalsIgnoreCase( studentsRank.getTotal() ) ) {

			studentsScoreChunk = new Chunk( studentsRank.getName()+"'s Score: Not Applicable", headerFont);
			
		}else {

			studentsScoreChunk = new Chunk( studentsRank.getName()+"'s Score: "+ studentsRank.getTotal(), headerFont);
			
		}

		Paragraph studentsScoreParam = new Paragraph(studentsScoreChunk);	
		studentsScoreParam.setAlignment(Element.ALIGN_CENTER); 
		document.add(studentsScoreParam); 
		
	}

	public void generateHeaderTable( Document document, String topic ) throws Exception {
		Image logo = null;
		try {
			logo = Image.getInstance(new URL("https://distance.nmims.edu/wp-content/themes/NMIMS/html/images/nmims-logo.png"));

			logo.setAlignment(Element.ALIGN_LEFT);  
			PdfPTable headerTable = new PdfPTable(1); 
			PdfPCell logoCell = new PdfPCell();
			logoCell.addElement(logo);
			logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			logoCell.setBorder(0);  
			headerTable.addCell(logoCell);
			headerTable.completeRow();
			headerTable.setHorizontalAlignment(Element.ALIGN_CENTER); 
			headerTable.setWidthPercentage(40);  
			headerTable.completeRow();
			headerTable.setSpacingAfter(10f);
			document.add(headerTable);  

			Chunk subhead = new Chunk( topic, font2 );

			Paragraph subheadPara = new Paragraph();
			subheadPara.add(subhead);
			subheadPara.setAlignment(Element.ALIGN_CENTER);
			subheadPara.setSpacingAfter(8);	
			document.add(subheadPara); 

		} catch (Exception e) {
			//
			logger.error("generateHeaderTable : "+ e.getClass() + " - " + e.getMessage());
			throw e;
		}
	}

}
