package com.nmims.helpers;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.EmbaMarksheetBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.MBAWXExamResultForSubject;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.SubjectResultBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.util.NumberUtility;
import com.nmims.util.SubjectsCerditsConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;

import com.nmims.beans.ProgramExamBean;
public class CreatePDF {

	
	@Autowired(required=false)
	ApplicationContext act;
	
	public static final Logger logger = LoggerFactory.getLogger(CreatePDF.class);
	public final Map<String,Chunk> notationsMap= new HashMap<>();

	Font font3 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
	Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
	Font font4 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
	Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 11);
	Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
	Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
	Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 18);
	Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
	Font font9 = new Font(Font.FontFamily.TIMES_ROMAN, 10);
	Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, (float) 9.5, Font.BOLD);
	Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	Font descBoldFont = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
	Font descFont = new Font(Font.FontFamily.TIMES_ROMAN, 9);
	Font footerBoldFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
	Font font11 = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);

	public void test(){}
	
	public void createPDF(List<MarksheetBean> marksheetList, String resultDeclarationDate, HttpServletRequest request, 
			HashMap<String, String> programMap, HashMap<String, CenterExamBean> centersMap, String MARKSHEETS_PATH,
			Map<String, ProgramExamBean> modProgramMap) throws FileNotFoundException, DocumentException, IOException {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat(".##");
		HashMap<String, CenterExamBean> centerNameCenterMap = generateCenterNameCenterMap(centersMap);//This will make another map with key as Name insteadof code/id
		
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		String markSheetDate = sdfr.format(new Date());
		String fileName = null;

		Image signature = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/signature.jpg"));
		//Image signature = Image.getInstance("d:/signature.jpg");
		signature.scaleAbsolute(60, 42);
		String folderPath = "";
		if(marksheetList.size() > 0){
			MarksheetBean bean = marksheetList.get(0);
			String sem = bean.getSem();
			String year = bean.getExamYear();
			String month = bean.getExamMonth();
			fileName = month + "_" + year + "_" + bean.getProgram() + "_Sem" + sem + ".pdf";
			fileName = fileName.toUpperCase();
			folderPath = MARKSHEETS_PATH + month + "-" + year + "/";
			
			File folder = new File(folderPath);
			if (!folder.exists()) {   
				folder.mkdirs();   
			}  
		}
		Document document = new Document(PageSize.A4);
		
		if(request.getAttribute("logoRequired")!=null && ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
			document.setMargins(50, 50, 20, 40);  
		}  else {
			document.setMargins(50, 50, 100, 40);    
		}
		
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath+fileName));
		request.getSession().setAttribute("fileName", folderPath+fileName);
		document.open();
		Image logo=null;
		Paragraph blankLine = new Paragraph("\n");
		if(request.getAttribute("logoRequired")!=null && ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
					logo = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nm_logo2.jpg")); 
					logo.scaleAbsolute(164,59);    
					logo.setIndentationLeft(-17); 
		}
		Chunk failure = new Chunk("*",font6);
		failure.setTextRise(5f);
		
		Chunk resultOnHold = new Chunk("**",font6);
		resultOnHold.setTextRise(5f);

		
		Chunk carryFwd = new Chunk("#",font6);
		carryFwd.setTextRise(5f);

		Chunk grace = new Chunk("~",font6);
		grace.setTextRise(5f);
		
		//generateSignatureSheet(document, marksheetList, programMap, centersMap,centerNameCenterMap);
		 
			//document.setMargins(50, 50, 100, 40); 
		String learningCenter = "None"; 
		for (int i = 0; i < marksheetList.size(); i++) {
//			if(request.getAttribute("logoRequired")!=null && ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
//				document.setMargins(50, 50, 20, 40);  
//			}  else {
//				document.setMargins(50, 50, 100, 40);    
//			}  
			MarksheetBean bean = marksheetList.get(i);
			String program = programMap.get(bean.getProgram().trim());
//			String currentLearningCenter = bean.getLc();
//			if(!learningCenter.equalsIgnoreCase(currentLearningCenter)){
//				CenterExamBean centerBean = centersMap.get(bean.getCenterCode()) ;
//
//				//Paragraph centerCodePara = null;
//				Paragraph centerNamePara = null;
//				Paragraph centerAddress = null;
//
//				if(centerBean != null){
//					//centerCodePara = new Paragraph("Center Code: " + centerBean.getCenterCode(), font7);
//					centerNamePara = new Paragraph("Learning Center: " + centerBean.getLc(), font1);
//					String lcAddress = "";
//					if(centerNameCenterMap.get(centerBean.getLc()) != null){
//						lcAddress = centerNameCenterMap.get(centerBean.getLc()).getAddress();
//					}
//					
//					centerAddress = new Paragraph("Center Address: " + lcAddress, font1);
//				}else{
//					//centerCodePara = new Paragraph("Center Code: Not Available", font7);
//					centerNamePara = new Paragraph("Center Name: Not Available", font7);
//					centerAddress = new Paragraph("Center Address: Not Available", font7);
//				}
//
//				//Paragraph programPara = new Paragraph("Program: " + program, font7);
//				//Paragraph semPara = new Paragraph("Sem: " + bean.getSem(), font7);
//
//				//document.add(programPara);
//				//document.add(semPara);
////				document.add(centerCodePara);
//				document.add(centerNamePara);
//				document.add(centerAddress);
//				learningCenter = currentLearningCenter;
//				document.newPage();
//			}
			try {
				
				
				if(request.getAttribute("logoRequired")!=null && ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
					document.add(logo);  
					document.add(blankLine);
				} else {
					//document.setMargins(50, 50, 170, 35);
				}
				
				Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font3);
				collegNameChunk.setUnderline(0.1f, -2f);
				Paragraph collegeName = new Paragraph();
				collegeName.add(collegNameChunk);
				collegeName.setAlignment(Element.ALIGN_CENTER);
				document.add(collegeName);



				Chunk stmtOfMarksChunk = new Chunk("STATEMENT OF MARKS", font2);
				stmtOfMarksChunk.setUnderline(0.1f, -2f);
				Paragraph stmtOfMarksPara = new Paragraph();
				stmtOfMarksPara.add(stmtOfMarksChunk);
				stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
				document.add(stmtOfMarksPara);


				PdfPTable headerTable = new PdfPTable(2); // 2 columns.
				headerTable.getDefaultCell().setBorder(0);
				
				String fatherName = bean.getFatherName().toUpperCase();
				
				if(fatherName == null || "".equals(fatherName.trim())){
					fatherName = "";
				}
				
				String middleName = bean.getMiddleName().toUpperCase();
				if(middleName == null || "".equals(middleName.trim())){
					middleName = bean.getFatherName().trim().toUpperCase();
				}
				
				String motherName = bean.getMotherName().toUpperCase();
				if(motherName == null || "".equals(motherName.trim())){
					motherName = "";
				}
				
				PdfPCell nameCell = new PdfPCell(new Paragraph("NAME: "+(bean.getFirstName().trim().toUpperCase()+" "+bean.getLastName().trim()).toUpperCase().replace(".", " "),font1));

				nameCell.setBorder(0);
				nameCell.setColspan(2);
				//nameCell.setPaddingBottom(10);
				headerTable.addCell(nameCell);

				headerTable.completeRow();
				
				PdfPCell fatherNameCell = new PdfPCell(new Paragraph("Father's Name: "+(fatherName).toUpperCase(),font1));
				fatherNameCell.setBorder(0);
				fatherNameCell.setColspan(2);
				//fatherNameCell.setPaddingBottom(10);
				headerTable.addCell(fatherNameCell);
				headerTable.completeRow();
				
				PdfPCell motherNameCell = new PdfPCell(new Paragraph("Mother's Name: "+(motherName).toUpperCase(),font1));
				motherNameCell.setBorder(0);
				motherNameCell.setColspan(2);
				motherNameCell.setPaddingBottom(5);
				headerTable.addCell(motherNameCell);
				headerTable.completeRow();
				
				
				PdfPCell programCell = new PdfPCell(new Paragraph("Program: "+program,font1));
				PdfPCell studentNoCell = new PdfPCell(new Paragraph("Student No: "+bean.getSapid(),font1));
				studentNoCell.setBorder(0);

				programCell.setBorder(0);
				programCell.setPaddingBottom(10);
				headerTable.addCell(programCell);
				headerTable.addCell(studentNoCell);
				headerTable.completeRow();

				//Added Mode Of Delivery - START
				ProgramExamBean modProgram = modProgramMap.get(bean.getProgram());
				String mod = modProgram.getModeOfLearning();
				PdfPCell modCell = new PdfPCell(new Paragraph("Mode of Delivery: "+mod,font1));
				modCell.setBorder(0);
				modCell.setColspan(2);
				modCell.setPaddingBottom(10);
				headerTable.addCell(modCell);
				headerTable.completeRow();
				//Added Mode Of Delivery - END

				PdfPCell rgstnCell = new PdfPCell(new Paragraph("Month and Year of Registration: "+bean.getEnrollmentMonth()
						+"-"+bean.getEnrollmentYear(),font1));

				String romanSem = "";

				if("1".equals(bean.getSem().trim())){
					romanSem = "I";
				}else if("2".equals(bean.getSem().trim())){
					romanSem = "II";
				}else if("3".equals(bean.getSem().trim())){
					romanSem = "III";
				}else if("4".equals(bean.getSem().trim())){
					romanSem = "IV";
				}else if("5".equals(bean.getSem().trim())){
					romanSem = "V";
				}else if("6".equals(bean.getSem().trim())){
					romanSem = "VI";
				}

				PdfPCell semCell = new PdfPCell(new Paragraph("Semester: "+romanSem,font1));
				rgstnCell.setBorder(0);
				semCell.setBorder(0);

				headerTable.addCell(rgstnCell);
				headerTable.addCell(semCell);
				headerTable.completeRow();

				PdfPCell examYearCell = new PdfPCell(new Paragraph("Month and Year of Examination: "+bean.getExamMonth()+"-"+bean.getExamYear(),font1));
				examYearCell.setBorder(0);
				headerTable.addCell(examYearCell);
				headerTable.completeRow();

				headerTable.setWidthPercentage(100);
				headerTable.setSpacingBefore(10f);
				headerTable.setSpacingAfter(5f);
				float[] columnWidths = {3.2f, 1.3f};
				headerTable.setWidths(columnWidths);
				document.add(headerTable);

				PdfPTable marksTable = new PdfPTable(5);

				PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font8));
				PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subjects", font8));
				String finalMarksTitle = "Final Exam Marks";
				if("PGDMM-MLI".equalsIgnoreCase(bean.getProgram())){
					finalMarksTitle = finalMarksTitle + " (50)";
				}else if("ACBM".equalsIgnoreCase(bean.getProgram())){
					finalMarksTitle = finalMarksTitle + " (60)";
				}else{
					finalMarksTitle = finalMarksTitle + " (70)";
				}
				PdfPCell finalExamMarksCell = new PdfPCell(new Paragraph(finalMarksTitle, font8));

				String assignmentMarksTitle = "Assignment Marks";
				if("PGDMM-MLI".equalsIgnoreCase(bean.getProgram())){
					assignmentMarksTitle = assignmentMarksTitle + " (50)";
				}else if("ACBM".equalsIgnoreCase(bean.getProgram())){
					assignmentMarksTitle = assignmentMarksTitle + " (40)";
				}else{
					assignmentMarksTitle = assignmentMarksTitle + " (30)";
				}
				PdfPCell assignmentMarksCell = new PdfPCell(new Paragraph(assignmentMarksTitle, font8));
				PdfPCell totalMarksCell = new PdfPCell(new Paragraph("Total Marks (100)", font8));

				srNoCell.setFixedHeight(30);
				srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
				subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
				finalExamMarksCell.setVerticalAlignment(Element.ALIGN_TOP);
				assignmentMarksCell.setVerticalAlignment(Element.ALIGN_TOP);
				totalMarksCell.setVerticalAlignment(Element.ALIGN_TOP);

				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				finalExamMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				assignmentMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				marksTable.addCell(srNoCell);
				marksTable.addCell(subjectsCell);
				marksTable.addCell(finalExamMarksCell);
				marksTable.addCell(assignmentMarksCell);
				marksTable.addCell(totalMarksCell);
				marksTable.completeRow();

				int count = 1;
				ArrayList<SubjectResultBean> subjectList = bean.getSubjects();
				ArrayList<SubjectResultBean> projectMarksList = new ArrayList<SubjectResultBean>();
				for(SubjectResultBean sb : subjectList){// to print project at the end of the marks table
					if("Project".equalsIgnoreCase(sb.getSubject())){
						sb.setSubject("Project (Out of 100 marks)");
						projectMarksList.add(sb);
					}
					if("Module 4 - Project".equalsIgnoreCase(sb.getSubject())){
						sb.setSubject("Module 4 - Project (Out of 100 marks)");
						projectMarksList.add(sb);
					}
					if("Simulation: Mimic Pro".equalsIgnoreCase(sb.getSubject())){
						sb.setSubject("Simulation: Mimic Pro (Out of 100 marks)");
						projectMarksList.add(sb);
					}
					if("Simulation: Mimic Social".equalsIgnoreCase(sb.getSubject())){
						sb.setSubject("Simulation: Mimic Social (Out of 100 marks)");
						projectMarksList.add(sb);
					}
				}
				subjectList.removeAll(projectMarksList);
				
				//By Vilpesh on 2022-04-09, order Subject names.
				if(null != subjectList) {
					List<SubjectResultBean> subjectListOrdered = this.orderSubject(subjectList);
					subjectList.clear();
					subjectList.addAll(subjectListOrdered);
				}
				
				subjectList.addAll(projectMarksList);
				// Aggregate Percentage of ACBM student 
				int numberOfSubjects = subjectList.size();
				int subjectTotalForCalculation = 0;
				double aggregateTotalOfMarks =0.0;
				for (int j = 0; j < subjectList.size(); j++) {
					SubjectResultBean subject = subjectList.get(j);

					srNoCell = new PdfPCell(new Paragraph(count+"", font1));
					subjectsCell = new PdfPCell(new Paragraph(subject.getSubject(), font1));
					subjectsCell.setPaddingLeft(5);

					String writtenScore = subject.getWrittenScore();
					String assignmentScore = subject.getAssignmentScore();
					if(writtenScore != null && "".equals(writtenScore.trim())){
						writtenScore = "--";
					}

					/*if("Project".equalsIgnoreCase(subject.getSubject())){
						assignmentScore = "--";
					}*/
					if("Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
 						assignmentScore = "--";
 					}
					if("Module 4 - Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
 						assignmentScore = "--";
 					}
					if("Simulation: Mimic Pro (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
 						assignmentScore = "--";
 					}
					if("Simulation: Mimic Social (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
 						assignmentScore = "--";
 					}
					if("Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
						writtenScore = "--";
					}
					if("Module 4 - Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
						writtenScore = "--";
					}
					if("Simulation: Mimic Pro (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
						writtenScore = "--";
					}
					if("Simulation: Mimic Social (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
						writtenScore = "--";
					}
					if("Copy Case".equals(subject.getRemarks()) && "0".equals(assignmentScore) ) {
						assignmentScore = "CC";
					}

					String totalMarksValue = subject.getTotal();

					if(subject.isWaivedOff()) {
						writtenScore="##";
						assignmentScore="##";
						totalMarksValue="##";
					}
					
					Paragraph finalMarks = new Paragraph(writtenScore, font1);
					Paragraph assignmentMarks = new Paragraph(new Paragraph(assignmentScore, font1));		
					
					if(totalMarksValue != null && "".equals(totalMarksValue.trim())){
						totalMarksValue = "--";
					}
					if("AB".equals(writtenScore) && "--".equals(assignmentScore)){
						totalMarksValue = "AB";
					}
					
					//Added by Sanket 26-Jan-2017 to handle blank total
					try {
						subjectTotalForCalculation = Integer.parseInt(totalMarksValue);
					} catch (Exception e) {
						subjectTotalForCalculation = 0;
					}
					
					if("ACBM".equals(bean.getProgram())){
						aggregateTotalOfMarks = aggregateTotalOfMarks + subjectTotalForCalculation;
					}
					
					Paragraph totalMarks = new Paragraph(new Paragraph(totalMarksValue, font1));

					boolean isModuleProject = "Module 4 - Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject());
					boolean isProject = "Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject());
					boolean isMimicPro = "Simulation: Mimic Pro (Out of 100 marks)".equalsIgnoreCase(subject.getSubject());
					boolean isMimicSocial = "Simulation: Mimic Social (Out of 100 marks)".equalsIgnoreCase(subject.getSubject());
					
					if (!subject.isWaivedOff()) {
						if (subject.isGraceApplied()) {
							if (isModuleProject || isProject || isMimicPro || isMimicSocial) {
								totalMarks.add(grace);
							} else {
								finalMarks.add(grace);
							}
						}
						/*if( subject.isAssignmentCarryForward() && (!"Project".equalsIgnoreCase(subject.getSubject())) ){
							assignmentMarks.add(carryFwd);
						}*/

						if (subject.isAssignmentCarryForward() && !isModuleProject && !isProject && !isMimicPro
								&& !isMimicSocial) {
							assignmentMarks.add(carryFwd);
						}

						/*if(subject.isWrittenCarryForward()){
							finalMarks.add(carryFwd);
						}*/
						if (subject.isWrittenCarryForward() && !isModuleProject && !isProject && !isMimicPro
								&& !isMimicSocial) {
							finalMarks.add(carryFwd);
						}

						if (subject.isTotalCarryForward()) {
							totalMarks.add(carryFwd);
						}

						if (subject.isResultOnHold()) {
							totalMarks.add(resultOnHold);
						} else if (!subject.isPass()) {
							totalMarks.add(failure);
						}

						/*if(writtenScore != null && (!"".equals(writtenScore))){
							Chunk monthYear = new Chunk(bean.getWrittenMonth()+"-"+bean.getWrittenYear(), font5);
							monthYear.setTextRise(-5f);
							finalMarks.add(monthYear);
						}
						
						if(assignmentScore != null && (!"".equals(assignmentScore))){
							Chunk monthYear = new Chunk(bean.getAssignmentMonth()+"-"+bean.getAssignmentYear(), font5);
							monthYear.setTextRise(-5f);
							assignmentMarks.add(monthYear);
						}*/

						if ("RIA".equals(writtenScore) || "RIA".equals(assignmentScore)) {
							finalMarks.clear();

							finalMarks.add("RIA");
						}

						if ("NV".equals(writtenScore) || "NV".equals(assignmentScore)) {
							finalMarks.clear();

							finalMarks.add("NV");
						}
					}
					
					finalExamMarksCell = new PdfPCell(finalMarks);
					assignmentMarksCell = new PdfPCell(assignmentMarks);
					totalMarksCell = new PdfPCell(totalMarks);

					srNoCell.setFixedHeight(25);
					srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					finalExamMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					assignmentMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					totalMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

					srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					finalExamMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					assignmentMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);

					marksTable.addCell(srNoCell);
					marksTable.addCell(subjectsCell);
					marksTable.addCell(finalExamMarksCell);
					marksTable.addCell(assignmentMarksCell);
					marksTable.addCell(totalMarksCell);
					marksTable.completeRow();

					count++;
				}



				marksTable.setWidthPercentage(100);
				marksTable.setSpacingBefore(5f);
				marksTable.setSpacingAfter(5f);
				float[] marksColumnWidths = {1.5f, 11f, 3f, 3f, 3f};
				marksTable.setWidths(marksColumnWidths);
				document.add(marksTable);


				PdfPTable footerTable1 = new PdfPTable(2);
				footerTable1.getDefaultCell().setBorder(0);
				/*PdfPCell studyCenterCell = new PdfPCell(new Paragraph("STUDY CENTER CODE: "+bean.getCenterCode(),font1));
				studyCenterCell.setBorder(0);
				footerTable1.addCell(studyCenterCell);
				footerTable1.completeRow();*/
			/*	if("ACBM".equals(bean.getProgram())){
					double aggregate =aggregateTotalOfMarks/numberOfSubjects;
					PdfPCell aggregateCell = new PdfPCell(new Paragraph("Aggregate Percentage : "+df.format(aggregate)+" %",font1));
					
					aggregateCell.setBorder(0);
					aggregateCell.setColspan(2);
					footerTable1.addCell(aggregateCell);
					footerTable1.completeRow();	
				}
*/
				PdfPCell attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: "+bean.getValidityEndMonth()+"-"+bean.getValidityEndYear()+ " Examination.",font1));
				attemptsCell.setBorder(0);
				//attemptsCell.setColspan(2);
				
				Paragraph resultDeclaredPara = new Paragraph("                      Result declared on         : "+bean.getResultDeclarationDate(), font1);
				PdfPCell resultDeclaredCell = new PdfPCell(resultDeclaredPara);
				resultDeclaredCell.setBorder(0);
				
				footerTable1.addCell(attemptsCell);
				footerTable1.addCell(resultDeclaredCell);
				footerTable1.completeRow();




				footerTable1.setWidthPercentage(100);
				footerTable1.setSpacingBefore(3f);
				footerTable1.setSpacingAfter(3f);
				float[] footer1ColumnWidths = {3f, 3f};
				footerTable1.setWidths(footer1ColumnWidths);
				document.add(footerTable1);

				PdfPCell prepredByCell=null;
				PdfPCell checkedByCell=null;
				PdfPTable footerTable2 = new PdfPTable(3);
				if(request.getAttribute("logoRequired")!=null && ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
					prepredByCell = new PdfPCell(new Paragraph("", font1));
					prepredByCell.setBorder(0);
					checkedByCell = new PdfPCell(new Paragraph("", font1));
//					footerTable2.setSpacingBefore(15f);
//					prepredByCell.setFixedHeight(25);
//					prepredByCell.setBorder(0);
//					footerTable2.addCell(prepredByCell);
//					footerTable2.completeRow();
				}else {
					prepredByCell = new PdfPCell(new Paragraph("Prepared by: ________________________", font1));
					checkedByCell = new PdfPCell(new Paragraph("Checked by: ________________________", font1));
					footerTable2.setSpacingBefore(10f);
					prepredByCell.setFixedHeight(25);
					prepredByCell.setBorder(0);
				}							
//				prepredByCell.setFixedHeight(25);
//				prepredByCell.setBorder(0);
//				PdfPCell resultDeclaredCell = new PdfPCell(new Paragraph("Result declared on", font1));
//				resultDeclaredCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//				resultDeclaredCell.setBorder(0);
//
//				PdfPCell resultDeclaredValueCell = new PdfPCell(new Paragraph(": "+bean.getResultDeclarationDate(), font1));
//				resultDeclaredValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//				resultDeclaredValueCell.setBorder(0);

				//footerTable2.addCell(prepredByCell);
				//footerTable2.addCell(resultDeclaredCell);
				//footerTable2.addCell(resultDeclaredValueCell);
//				footerTable2.completeRow();


				
				checkedByCell.setBorder(0);
				PdfPCell marksheetIssuedCell = new PdfPCell(new Paragraph("Marksheet issued on", font1));
				marksheetIssuedCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				marksheetIssuedCell.setBorder(0);

				PdfPCell marksheetIssuedValueCell = new PdfPCell(new Paragraph(": "+markSheetDate, font1));
				marksheetIssuedValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				marksheetIssuedValueCell.setBorder(0);

				footerTable2.addCell(prepredByCell);
				footerTable2.addCell(marksheetIssuedCell);
				footerTable2.addCell(marksheetIssuedValueCell);
				footerTable2.completeRow();

//				PdfPCell emptyCell1 = new PdfPCell(new Paragraph("", font1));
//				PdfPCell emptyCell2 = new PdfPCell(new Paragraph("", font1));
//				footerTable2.addCell(checkedByCell);
//				footerTable2.addCell(new P);
//				footerTable2.addCell(checkedByCell);
//				footerTable2.completeRow();
				
				footerTable2.setWidthPercentage(100);
				footerTable2.setSpacingBefore(15f);
				footerTable2.setSpacingAfter(5f);
				float[] footer2ColumnWidths = {12.3f, 4.2f, 3.2f};
				footerTable2.setWidths(footer2ColumnWidths);
				document.add(footerTable2);


				PdfPTable footerTable3 = new PdfPTable(2);
				footerTable3.getDefaultCell().setBorder(0);



				PdfPCell emptyCell = new PdfPCell();
				emptyCell.setBorder(0);
				footerTable3.addCell(checkedByCell);

				PdfPCell signatureCell = new PdfPCell();
				signatureCell.addElement(signature);
				signatureCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
				//signature.setAlignment(Element.ALIGN_RIGHT);
				signatureCell.setBorder(0);
				signatureCell.setVerticalAlignment(Element.ALIGN_BOTTOM);

				footerTable3.addCell(signatureCell);
				footerTable3.completeRow();

				footerTable3.setWidthPercentage(100);
				footerTable3.setSpacingBefore(5f);
				//footerTable3.setSpacingAfter(15f);

				float[] footer3ColumnWidths = {6f, 2f};
				footerTable3.setWidths(footer3ColumnWidths);
				document.add(footerTable3);



				PdfPTable footerTable4 = new PdfPTable(2);
				footerTable4.getDefaultCell().setBorder(0);
				footerTable4.addCell(emptyCell);

				PdfPCell coeCell = new PdfPCell(new Paragraph("CONTROLLER OF EXAMINATIONS", font4));
				coeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				coeCell.setBorder(0);
				coeCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				coeCell.setFixedHeight(15);
				footerTable4.addCell(coeCell);
				footerTable4.completeRow();

				footerTable4.setWidthPercentage(100);
				//footerTable4.setSpacingBefore(5f);
				footerTable4.setSpacingAfter(0f);

				float[] footer4ColumnWidths = {3f, 3f};
				footerTable4.setWidths(footer4ColumnWidths);

				document.add(footerTable4);


				/*document.add(new Paragraph("1)  ANS :  Assignment Not Submitted.", font4));
				document.add(new Paragraph("2)  NA   :  Not Eligible due to non submission of assignment.", font4));
				document.add(new Paragraph("3)  *       :  Failures.", font4));
				document.add(new Paragraph("4)  **      :  Result on Hold due to Non Submission of Assignment.", font4));
				document.add(new Paragraph("5)  #       :  Marks brought forward.", font4));
				document.add(new Paragraph("6)  ~       :  Grace Marks given.", font4));
				document.add(new Paragraph(" ", font4));
				document.add(new Paragraph("Note:", font4));
				document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 50", font4));
				document.add(new Paragraph("2)  Submission of assignment is compulsory to be declared as pass in a subject in addition "
						+ "to \nappearance at the Term-End Examination.", font4));
*/
				
				document.add(new Paragraph("1)  ANS :  Assignment Not Submitted.", font11));
				document.add(new Paragraph("2)  NA   :  Not Eligible due to non submission of assignment.", font11));
				document.add(new Paragraph("3)  *       :  Failures.", font11));
				document.add(new Paragraph("4)  **      :  Result on Hold due to Non Submission of Assignment.", font11));
				document.add(new Paragraph("5)  #       :  Marks brought forward.", font11));
				document.add(new Paragraph("6)  ~       :  Grace Marks given.", font11));
				document.add(new Paragraph("7)  NV      :  Null And Void", font11));
				document.add(new Paragraph("8)  ##      :  Course waiver subjects with marks brought forward", font11));
				document.add(new Paragraph("9)  CC      :  Copy case marked in Assignment", font11));
				/*document.add(new Paragraph(" ", font4));*/
				document.add(new Paragraph("Note:", font8));
				if("ACBM".equals(bean.getProgram())){
					document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 40", font4));
					document.add(new Paragraph("2)  Minimum aggregate marks for passing overall semester is 40%", font4));
					document.add(new Paragraph("3)  Submission of assignment is compulsory to be declared as pass in a subject in addition "
							+ "to \nappearance at the Term-End Examination.", font10));
				}else{
					if("JUL2017".equalsIgnoreCase(bean.getPrgmStructApplicable())){
						document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 40", font4));
					}else{
						document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 50", font4));
					}
					document.add(new Paragraph("2)  Submission of assignment is compulsory to be declared as pass in a subject in addition "
							+ "to \nappearance at the Term-End Examination.", font10));
				}
				
				
				/*if("PGDMM-MLI".equalsIgnoreCase(bean.getProgram())){
					document.add(new Paragraph("8)  Maximum Final Exam Marks: 50, Maximum Assignment Marks: 50", font4));
				}else{
					document.add(new Paragraph("8)  Maximum Final Exam Marks: 70, Maximum Assignment Marks: 30", font4));
				}*/

				document.newPage();

			} catch (DocumentException e) {
				
			} catch (Exception e) {
				
			}


		}
		document.close(); // no need to close PDFwriter?

	}
	
	
	private HashMap<String, CenterExamBean> generateCenterNameCenterMap(HashMap<String, CenterExamBean> centersMap) {
		HashMap<String, CenterExamBean> centerNameCenterMap = new HashMap<String, CenterExamBean>();
		for (String centerCode : centersMap.keySet()) {
			CenterExamBean center = centersMap.get(centerCode);
			centerNameCenterMap.put(center.getCenterName(), center);
		}
		return centerNameCenterMap;
	}

	public void createStudentSelfMarksheetPDF(List<MarksheetBean> marksheetList, String resultDeclarationDate, HttpServletRequest request, 
			HashMap<String, ProgramExamBean> programMap, HashMap<String, CenterExamBean> centersMap, String MARKSHEETS_PATH,
			Map<String, ProgramExamBean> modProgramMap) throws Exception {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat(".##");
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		String markSheetDate = sdfr.format(new Date());
		String fileName = null;
		//Image signature = Image.getInstance(new URL("https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/signature.jpg"));
		//Image signature = Image.getInstance("d:/signature.jpg");
		//signature.scaleAbsolute(60, 42);
		String folderPath = "";
		if(marksheetList.size() > 0){
			MarksheetBean bean = marksheetList.get(0);
			String studentId = bean.getSapid();
			String sem = bean.getSem();
			String year = bean.getExamYear();
			String month = bean.getExamMonth();
			fileName = studentId + "_" + month + "_" + year + "_" + bean.getProgram() + "_Sem" + sem + "_"  + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
			fileName = fileName.toUpperCase();
			
			
			folderPath = MARKSHEETS_PATH + month + "-" + year + "/";
			
			File folder = new File(folderPath);
			if (!folder.exists()) {   
				folder.mkdirs();   
			}  
		}
		Document document = new Document(PageSize.A4);
		document.setMargins(50, 50, 40, 40);

		
		
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath+fileName));
		request.getSession().setAttribute("fileName", folderPath+fileName);
		document.open();

		
		Phrase watermark = new Phrase("Marksheet Downloaded from NGASCE Student Portal", new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
		PdfContentByte canvas = writer.getDirectContent();
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 300, 30);
		
		generateHeaderTable(document);
		
		
		
		Chunk failure = new Chunk("*",font6);
		failure.setTextRise(5f);
		
		Chunk resultOnHold = new Chunk("**",font6);
		resultOnHold.setTextRise(5f);

		Chunk carryFwd = new Chunk("#",font6);
		carryFwd.setTextRise(5f);

		Chunk grace = new Chunk("~",font6);
		grace.setTextRise(5f);

		
		for (int i = 0; i < marksheetList.size(); i++) {

			MarksheetBean bean = marksheetList.get(i);
			ProgramExamBean programDetails = programMap.get(bean.getProgram().trim()+"-"+bean.getPrgmStructApplicable());
			String program = programDetails.getProgramname();
			String programType = programDetails.getProgramType();
			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
			double aggregateTotalOfMarks = 0.0;
			try {

			/*	Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font3);
				collegNameChunk.setUnderline(0.1f, -2f);
				Paragraph collegeName = new Paragraph();
				collegeName.add(collegNameChunk);
				collegeName.setAlignment(Element.ALIGN_CENTER);
				document.add(collegeName);*/

				
				Chunk stmtOfMarksChunk = new Chunk("STATEMENT OF MARKS", font2);
				stmtOfMarksChunk.setUnderline(0.1f, -2f);
				Paragraph stmtOfMarksPara = new Paragraph();
				stmtOfMarksPara.add(stmtOfMarksChunk);
				stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
				document.add(stmtOfMarksPara);


				PdfPTable headerTable = new PdfPTable(2); // 2 columns.
				headerTable.getDefaultCell().setBorder(0);
				
				String fatherName = bean.getFatherName().toUpperCase();
				
				if(fatherName == null || "".equals(fatherName.trim())){
					fatherName = "";
				}
				
				String middleName = bean.getMiddleName().toUpperCase();
				if(middleName == null || "".equals(middleName.trim())){
					middleName = bean.getFatherName().trim().toUpperCase();
				}
				
				String motherName = bean.getMotherName().toUpperCase();
				if(motherName == null || "".equals(motherName.trim())){
					motherName = "";
				}
				
				PdfPCell nameCell = new PdfPCell(new Paragraph("NAME: "+(bean.getFirstName().trim().toUpperCase()+" "+bean.getLastName().trim()).toUpperCase().replace(".", " "),font1));

				nameCell.setBorder(0);
				nameCell.setColspan(2);
				//nameCell.setPaddingBottom(10);
				headerTable.addCell(nameCell);

				headerTable.completeRow();
				
				PdfPCell fatherNameCell = new PdfPCell(new Paragraph("Father's Name: "+(fatherName).toUpperCase(),font1));
				fatherNameCell.setBorder(0);
				fatherNameCell.setColspan(2);
				//fatherNameCell.setPaddingBottom(10);
				headerTable.addCell(fatherNameCell);
				headerTable.completeRow();
				
				PdfPCell motherNameCell = new PdfPCell(new Paragraph("Mother's Name: "+(motherName).toUpperCase(),font1));
				motherNameCell.setBorder(0);
				motherNameCell.setColspan(2);
				motherNameCell.setPaddingBottom(5);
				headerTable.addCell(motherNameCell);
				headerTable.completeRow();
				
				
				PdfPCell programCell = new PdfPCell(new Paragraph("Program: "+program,font1));
				PdfPCell studentNoCell = new PdfPCell(new Paragraph("Student No: "+bean.getSapid(),font1));
				studentNoCell.setBorder(0);

				programCell.setBorder(0);
				programCell.setPaddingBottom(10);
				headerTable.addCell(programCell);
				headerTable.addCell(studentNoCell);
				headerTable.completeRow();

				//Added Mode Of Delivery - START
				ProgramExamBean modProgram = modProgramMap.get(bean.getProgram());
				String mod = modProgram.getModeOfLearning();				
				PdfPCell modCell = new PdfPCell(new Paragraph("Mode of Delivery: "+mod,font1));
				modCell.setBorder(0);
				modCell.setColspan(2);
				modCell.setPaddingBottom(10);
				headerTable.addCell(modCell);
				headerTable.completeRow();
				//Added Mode Of Delivery - END
				
				PdfPCell rgstnCell = new PdfPCell(new Paragraph("Month and Year of Registration: "+bean.getEnrollmentMonth()
						+"-"+bean.getEnrollmentYear(),font1));

				String romanSem = "";

				if("1".equals(bean.getSem().trim())){
					romanSem = "I";
				}else if("2".equals(bean.getSem().trim())){
					romanSem = "II";
				}else if("3".equals(bean.getSem().trim())){
					romanSem = "III";
				}else if("4".equals(bean.getSem().trim())){
					romanSem = "IV";
				}

				PdfPCell semCell = new PdfPCell(new Paragraph("Semester: "+romanSem,font1));
				rgstnCell.setBorder(0);
				semCell.setBorder(0);

				headerTable.addCell(rgstnCell);
				headerTable.addCell(semCell);
				headerTable.completeRow();

				PdfPCell examYearCell = new PdfPCell(new Paragraph("Month and Year of Examination: "+bean.getExamMonth()+"-"+bean.getExamYear(),font1));
				examYearCell.setBorder(0);
				headerTable.addCell(examYearCell);
				headerTable.completeRow();

				headerTable.setWidthPercentage(100);
				headerTable.setSpacingBefore(10f);
				headerTable.setSpacingAfter(5f);
				float[] columnWidths = {3.2f, 1.3f};
				headerTable.setWidths(columnWidths);
				document.add(headerTable);

				PdfPTable marksTable = new PdfPTable(5);

				PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font8));
				PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subjects", font8));
				String finalMarksTitle = "Final Exam Marks";
				if("PGDMM-MLI".equalsIgnoreCase(bean.getProgram())){
					finalMarksTitle = finalMarksTitle + " (50)";
				}else if("ACBM".equalsIgnoreCase(bean.getProgram())){
					finalMarksTitle = finalMarksTitle + " (60)";
				}else{
					finalMarksTitle = finalMarksTitle + " (70)";
				}
				PdfPCell finalExamMarksCell = new PdfPCell(new Paragraph(finalMarksTitle, font8));

				String assignmentMarksTitle = "Assignment Marks";
				if("PGDMM-MLI".equalsIgnoreCase(bean.getProgram())){
					assignmentMarksTitle = assignmentMarksTitle + " (50)";
				}else if("ACBM".equalsIgnoreCase(bean.getProgram())){
					assignmentMarksTitle = assignmentMarksTitle + " (40)";
				}else{
					assignmentMarksTitle = assignmentMarksTitle + " (30)";
				}
				PdfPCell assignmentMarksCell = new PdfPCell(new Paragraph(assignmentMarksTitle, font8));
				PdfPCell totalMarksCell = new PdfPCell(new Paragraph("Total Marks (100)", font8));

				srNoCell.setFixedHeight(30);
				srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
				subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
				finalExamMarksCell.setVerticalAlignment(Element.ALIGN_TOP);
				assignmentMarksCell.setVerticalAlignment(Element.ALIGN_TOP);
				totalMarksCell.setVerticalAlignment(Element.ALIGN_TOP);

				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				finalExamMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				assignmentMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				marksTable.addCell(srNoCell);
				marksTable.addCell(subjectsCell);
				marksTable.addCell(finalExamMarksCell);
				marksTable.addCell(assignmentMarksCell);
				marksTable.addCell(totalMarksCell);
				marksTable.completeRow();

				int count = 1;
				ArrayList<SubjectResultBean> subjectList = bean.getSubjects();
				ArrayList<SubjectResultBean> projectMarksList = new ArrayList<SubjectResultBean>();
								for(SubjectResultBean sb : subjectList){// to print project at the end of the marks table
								if("Project".equalsIgnoreCase(sb.getSubject())){
									sb.setSubject("Project (Out of 100 marks)");
									projectMarksList.add(sb);
								}
								if("Module 4 - Project".equalsIgnoreCase(sb.getSubject())){
									sb.setSubject("Module 4 - Project (Out of 100 marks)");
									projectMarksList.add(sb);
								}
								if("Simulation: Mimic Pro".equalsIgnoreCase(sb.getSubject())){
									sb.setSubject("Simulation: Mimic Pro (Out of 100 marks)");
									projectMarksList.add(sb);
								}
								if("Simulation: Mimic Social".equalsIgnoreCase(sb.getSubject())){
									sb.setSubject("Simulation: Mimic Social (Out of 100 marks)");
									projectMarksList.add(sb);
								}
								
								}
								subjectList.removeAll(projectMarksList);
								
								//By Vilpesh on 2022-04-08, order Subject names.
								if(null != subjectList) {
									List<SubjectResultBean> subjectListOrdered = this.orderSubject(subjectList);
									subjectList.clear();
									subjectList.addAll(subjectListOrdered);
								}
								
								subjectList.addAll(projectMarksList);
								
				int numberOfSubjects = subjectList.size();
				for (int j = 0; j < subjectList.size(); j++) {
					SubjectResultBean subject = subjectList.get(j);
					srNoCell = new PdfPCell(new Paragraph(count+"", font1));
					subjectsCell = new PdfPCell(new Paragraph(subject.getSubject(), font1));
					subjectsCell.setPaddingLeft(5);

					String writtenScore = subject.getWrittenScore();
					String assignmentScore = subject.getAssignmentScore();

					if(writtenScore != null && "".equals(writtenScore.trim())){
						writtenScore = "--";
					}

					/*if("Project".equalsIgnoreCase(subject.getSubject())){
						assignmentScore = "--";
					}*/
					if("Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
 						assignmentScore = "--";
 					}

					if("Module 4 - Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
 						assignmentScore = "--";
 					}
					if("Simulation: Mimic Pro (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
 						assignmentScore = "--";
 					}
					if("Simulation: Mimic Social (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
 						assignmentScore = "--";
 					}
					if("Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
						writtenScore = "--";
					}
					if("Module 4 - Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
						writtenScore = "--";
					}
					if("Simulation: Mimic Pro (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
						writtenScore = "--";
					}
					if("Simulation: Mimic Social (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
						writtenScore = "--";
					}
					
					if("Copy Case".equals(subject.getRemarks()) && "0".equals(assignmentScore) ) {
						assignmentScore = "CC";
					}

					Paragraph finalMarks = new Paragraph(writtenScore, font1);
					Paragraph assignmentMarks = new Paragraph(new Paragraph(assignmentScore, font1));		
					String totalMarksValue = subject.getTotal();
					
					
					//Added by Sanket 26-Jan-2017 to handle blank total
					int subjectTotalForCalculation = 0;
					try {
						subjectTotalForCalculation = Integer.parseInt(totalMarksValue);
					} catch (Exception e) {
						subjectTotalForCalculation = 0;
					}
					
					if("ACBM".equals(student.getProgram())){
						aggregateTotalOfMarks = aggregateTotalOfMarks + subjectTotalForCalculation;
					}
					
					if(totalMarksValue != null && "".equals(totalMarksValue.trim())){
						totalMarksValue = "--";
					}
					if("AB".equals(writtenScore) && "--".equals(assignmentScore)){
						totalMarksValue = "AB";
					}
					
					
					Paragraph totalMarks = new Paragraph(new Paragraph(totalMarksValue, font1));
					

					boolean isModuleProject = "Module 4 - Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject());
					boolean isProject = "Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject());
					boolean isMimicPro = "Simulation: Mimic Pro (Out of 100 marks)".equalsIgnoreCase(subject.getSubject());
					boolean isMimicSocial = "Simulation: Mimic Social (Out of 100 marks)".equalsIgnoreCase(subject.getSubject());
					
					if(subject.isGraceApplied()){
						if(isModuleProject || isProject || isMimicPro || isMimicSocial){
							totalMarks.add(grace);
						}else {
							finalMarks.add(grace);
						} 
					}
					/*if( subject.isAssignmentCarryForward() && (!"Project".equalsIgnoreCase(subject.getSubject())) ){
						assignmentMarks.add(carryFwd);
					}*/
					if( subject.isAssignmentCarryForward() && !isModuleProject && !isProject && !isMimicPro && !isMimicSocial){
 						assignmentMarks.add(carryFwd);
 					}
					
					/*if(subject.isWrittenCarryForward()){
						finalMarks.add(carryFwd);
					}*/
					if(subject.isWrittenCarryForward() && !isModuleProject && !isProject && !isMimicPro && !isMimicSocial){
 						finalMarks.add(carryFwd);
 					}
				
					if(subject.isTotalCarryForward()){
						totalMarks.add(carryFwd);
					}
					if(subject.isResultOnHold()){
						totalMarks.add(resultOnHold);
					}else if(!subject.isPass()){
						totalMarks.add(failure);
					}

					/*if(writtenScore != null && (!"".equals(writtenScore))){
						Chunk monthYear = new Chunk(bean.getWrittenMonth()+"-"+bean.getWrittenYear(), font5);
						monthYear.setTextRise(-5f);
						finalMarks.add(monthYear);
					}

					if(assignmentScore != null && (!"".equals(assignmentScore))){
						Chunk monthYear = new Chunk(bean.getAssignmentMonth()+"-"+bean.getAssignmentYear(), font5);
						monthYear.setTextRise(-5f);
						assignmentMarks.add(monthYear);
					}*/

					if("RIA".equals(writtenScore) || "RIA".equals(assignmentScore))
					{
						finalMarks.clear();
						
						finalMarks.add("RIA");
					}
					
					if("NV".equals(writtenScore) || "NV".equals(assignmentScore))
					{
						finalMarks.clear();
						
						finalMarks.add("NV");
					}
					
					finalExamMarksCell = new PdfPCell(finalMarks);
					assignmentMarksCell = new PdfPCell(assignmentMarks);
					totalMarksCell = new PdfPCell(totalMarks);




					srNoCell.setFixedHeight(25);
					srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					finalExamMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					assignmentMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					totalMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

					srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					finalExamMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					assignmentMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);

					marksTable.addCell(srNoCell);
					marksTable.addCell(subjectsCell);
					marksTable.addCell(finalExamMarksCell);
					marksTable.addCell(assignmentMarksCell);
					marksTable.addCell(totalMarksCell);
					marksTable.completeRow();

					count++;
				}

				marksTable.setWidthPercentage(100);
				marksTable.setSpacingBefore(5f);
				marksTable.setSpacingAfter(10f);
				float[] marksColumnWidths = {1.5f, 11f, 3f, 3f, 3f};
				marksTable.setWidths(marksColumnWidths);
				document.add(marksTable);


				PdfPTable footerTable1 = new PdfPTable(2);
				footerTable1.getDefaultCell().setBorder(0);
				/*PdfPCell studyCenterCell = new PdfPCell(new Paragraph("STUDY CENTER CODE: "+bean.getCenterCode(),font1));
				studyCenterCell.setBorder(0);
				footerTable1.addCell(studyCenterCell);
				footerTable1.completeRow();*/
				/*if("ACBM".equals(student.getProgram())){
					PdfPCell aggregateCell = new PdfPCell(new Paragraph("Aggregate Percentage : "+df.format(aggregateTotalOfMarks/numberOfSubjects)+" %",font1));
					
					aggregateCell.setBorder(0);
					aggregateCell.setColspan(2);
					footerTable1.addCell(aggregateCell);
					footerTable1.completeRow();	
				}*/
				
				PdfPCell attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: "+bean.getValidityEndMonth()+"-"+bean.getValidityEndYear()+ " Examination.",font1));
				PdfPCell resultDeclaredCell = new PdfPCell(new Paragraph("                      Result declared on         : "+resultDeclarationDate, font1));
				//resultDeclaredCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				//resultDeclaredCell.setPaddingLeft();
				resultDeclaredCell.setBorder(0);
				attemptsCell.setBorder(0);
				//attemptsCell.setColspan(2);
				footerTable1.addCell(attemptsCell);
				footerTable1.addCell(resultDeclaredCell);
				footerTable1.completeRow();

				footerTable1.setWidthPercentage(100);
				footerTable1.setSpacingBefore(5f);
				footerTable1.setSpacingAfter(5f);
				float[] footer1ColumnWidths = {3f, 3f};
				footerTable1.setWidths(footer1ColumnWidths);
				document.add(footerTable1);


				PdfPTable footerTable2 = new PdfPTable(3);
//				PdfPCell prepredByCell = new PdfPCell(new Paragraph("", font1));
//				prepredByCell.setFixedHeight(25);
//				prepredByCell.setBorder(0);
//				PdfPCell resultDeclaredCell = new PdfPCell(new Paragraph("Result declared on", font1));
//				resultDeclaredCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//				resultDeclaredCell.setBorder(0);
//
//				PdfPCell resultDeclaredValueCell = new PdfPCell(new Paragraph(": "+resultDeclarationDate, font1));
//				resultDeclaredValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//				resultDeclaredValueCell.setBorder(0);
//
//				footerTable2.addCell(prepredByCell);
//				footerTable2.addCell(resultDeclaredCell);
//				footerTable2.addCell(resultDeclaredValueCell);
//				footerTable2.completeRow();


				PdfPCell checkedByCell = new PdfPCell(new Paragraph("", font1));
				checkedByCell.setBorder(0);
				PdfPCell marksheetIssuedCell = new PdfPCell(new Paragraph("Marksheet issued on", font1));
				marksheetIssuedCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				marksheetIssuedCell.setBorder(0);

				PdfPCell marksheetIssuedValueCell = new PdfPCell(new Paragraph(": "+markSheetDate, font1));
				marksheetIssuedValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				marksheetIssuedValueCell.setBorder(0);

				footerTable2.addCell(checkedByCell);
				footerTable2.addCell(marksheetIssuedCell);
				footerTable2.addCell(marksheetIssuedValueCell);
				footerTable2.completeRow();

				footerTable2.setWidthPercentage(100);
				footerTable2.setSpacingBefore(15f);
				footerTable2.setSpacingAfter(5f);
				float[] footer2ColumnWidths = {12.3f, 4.2f, 3.2f};
				footerTable2.setWidths(footer2ColumnWidths);
				document.add(footerTable2);


				PdfPTable footerTable3 = new PdfPTable(2);
				footerTable3.getDefaultCell().setBorder(0);



				PdfPCell emptyCell = new PdfPCell();
				emptyCell.setBorder(0);
				footerTable3.addCell(emptyCell);

				PdfPCell signatureCell = new PdfPCell();
				//signatureCell.addElement(signature);
				signatureCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
				//signature.setAlignment(Element.ALIGN_RIGHT);
				signatureCell.setBorder(0);
				signatureCell.setVerticalAlignment(Element.ALIGN_BOTTOM);

				footerTable3.addCell(signatureCell);
				footerTable3.completeRow();

				footerTable3.setWidthPercentage(100);
				footerTable3.setSpacingBefore(15f);
				//footerTable3.setSpacingAfter(15f);

				float[] footer3ColumnWidths = {6f, 2f};
				footerTable3.setWidths(footer3ColumnWidths);
				document.add(footerTable3);



				PdfPTable footerTable4 = new PdfPTable(2);
				footerTable4.getDefaultCell().setBorder(0);
				footerTable4.addCell(emptyCell);

				PdfPCell coeCell = new PdfPCell(new Paragraph("CONTROLLER OF EXAMINATIONS", font4));
				coeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				coeCell.setBorder(0);
				coeCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				coeCell.setFixedHeight(15);
				footerTable4.addCell(coeCell);
				footerTable4.completeRow();

				footerTable4.setWidthPercentage(100);
				footerTable4.setSpacingBefore(15f);
				footerTable4.setSpacingAfter(0f);

				float[] footer4ColumnWidths = {3f, 3f};
				footerTable4.setWidths(footer4ColumnWidths);

				document.add(footerTable4);


				document.add(new Paragraph("1)  ANS :  Assignment Not Submitted.", font11));
				document.add(new Paragraph("2)  NA   :  Not Eligible due to non submission of assignment.", font11));
				document.add(new Paragraph("3)  *       :  Failures.", font11));
				document.add(new Paragraph("4)  **      :  Result on Hold due to Non Submission of Assignment.", font11));
				document.add(new Paragraph("5)  #       :  Marks brought forward.", font11));
				document.add(new Paragraph("6)  ~       :  Grace Marks given.", font11));
				document.add(new Paragraph("7)  NV      :  Null And Void", font11));
				document.add(new Paragraph("8)  ##      :  Course waiver subjects with marks brought forward", font11)); 
				document.add(new Paragraph("9)  CC      :  Copy case marked in Assignment", font11));
				/*document.add(new Paragraph(" ", font4));*/
				document.add(new Paragraph("Note:", font4));
				if("Certificate".equals(programType)){
					document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 40", font4));
					document.add(new Paragraph("2)  Minimum aggregate marks for passing overall semester is 40%", font4));
					document.add(new Paragraph("3)  Submission of assignment is compulsory to be declared as pass in a subject in addition "
							+ "to \nappearance at the Term-End Examination.", font4));
					document.add(new Paragraph("4)  This statement of marks is generated through Student Portal of NMIMS Global Access School For Continuing Education.", font4));
					document.add(new Paragraph("5)  To verify authenticity of this marksheet, please refer to original marksheet issued by University.", font4));
				}else{
					if("JUL2017".equalsIgnoreCase(bean.getPrgmStructApplicable())){
						document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 40", font4));
					}else{
						document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 50", font4));
					}
					document.add(new Paragraph("2)  Submission of assignment is compulsory to be declared as pass in a subject in addition "
							+ "to \nappearance at the Term-End Examination.", font4));
					document.add(new Paragraph("3)  This statement of marks is generated through Student Portal of NMIMS Global Access School For Continuing Education.", font4));
					document.add(new Paragraph("4)  To verify authenticity of this marksheet, please refer to original marksheet issued by University.", font4));
				}
				
				
				
				/*if("PGDMM-MLI".equalsIgnoreCase(bean.getProgram())){
					document.add(new Paragraph("8)  Maximum Final Exam Marks: 50, Maximum Assignment Marks: 50", font4));
				}else{
					document.add(new Paragraph("8)  Maximum Final Exam Marks: 70, Maximum Assignment Marks: 30", font4));
				}*/

				
				
				document.newPage();

			} catch (DocumentException e) {
				
			} catch (Exception e) {
				
			}


		}
		document.close(); // no need to close PDFwriter?

	}
	
	/**
	 * Reorders subject names in alphabetically order.
	 * @param listBean
	 * @return
	 */
	protected List<SubjectResultBean> orderSubject(List<SubjectResultBean> listBean) {
		Set<String> setSubject;
		List<SubjectResultBean> listOrdered = null;
		List<SubjectResultBean> listOrderedSubjectWise = null;
		Map<String, List<SubjectResultBean>> mapSubjectResult = null;
		
		if(null != listBean && !listBean.isEmpty()) {
			//Distinct Subjects
			setSubject = listBean.stream().map(g -> g.getSubject()).collect(Collectors.toCollection(TreeSet::new));
			//logger.info("orderSubject : Distinct Subject(s): "+ setSubject.size());
			if(null != setSubject && !setSubject.isEmpty()) {
				//Create Map of each Subject and its list of beans
				mapSubjectResult = listBean.stream().collect(Collectors.groupingBy(h -> h.getSubject()));
				
				listOrderedSubjectWise = new LinkedList<SubjectResultBean>();
				for(String subject : setSubject) {
					listOrdered = mapSubjectResult.get(subject);
					listOrderedSubjectWise.addAll(listOrdered);
				}
				
				if(null != mapSubjectResult) {
					mapSubjectResult.clear();
				}
				setSubject.clear();
			}
		} else {
			listOrderedSubjectWise = listBean;
		}
		return listOrderedSubjectWise;
	}
	
//	create marksheet pdf for mobile api
	
	public MarksheetBean mCreateStudentSelfMarksheetPDF(List<MarksheetBean> marksheetList, String resultDeclarationDate, MarksheetBean msBean, 
			HashMap<String, ProgramExamBean> programMap, HashMap<String, CenterExamBean> centersMap, String MARKSHEETS_PATH) throws Exception {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat(".##");
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		String markSheetDate = sdfr.format(new Date());
		String fileName = null;
		//Image signature = Image.getInstance(new URL("https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/signature.jpg"));
		//Image signature = Image.getInstance("d:/signature.jpg");
		//signature.scaleAbsolute(60, 42);
		String folderPath = "";
		if(marksheetList.size() > 0){
			MarksheetBean bean = marksheetList.get(0);
			String studentId = bean.getSapid();
			String sem = bean.getSem();
			String year = bean.getExamYear();
			String month = bean.getExamMonth();
			fileName = studentId + "_" + month + "_" + year + "_" + bean.getProgram() + "_Sem" + sem + "_"  + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
			fileName = fileName.toUpperCase();
			
			
			folderPath = MARKSHEETS_PATH + month + "-" + year + "/";
			
			File folder = new File(folderPath);
			if (!folder.exists()) {   
				folder.mkdirs();   
			}  
		}
		Document document = new Document(PageSize.A4);
		document.setMargins(50, 50, 40, 40);

		
		
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath+fileName));
//		request.getSession().setAttribute("fileName", folderPath+fileName);
		msBean.setFileName(folderPath+fileName);
		document.open();

		
		Phrase watermark = new Phrase("Marksheet Downloaded from NGASCE Student Portal", new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
		PdfContentByte canvas = writer.getDirectContent();
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 300, 30);
		
		generateHeaderTable(document);
		
		
		
		Chunk failure = new Chunk("*",font6);
		failure.setTextRise(5f);
		
		Chunk resultOnHold = new Chunk("**",font6);
		resultOnHold.setTextRise(5f);

		Chunk carryFwd = new Chunk("#",font6);
		carryFwd.setTextRise(5f);

		Chunk grace = new Chunk("~",font6);
		grace.setTextRise(5f);

		
		for (int i = 0; i < marksheetList.size(); i++) {

			MarksheetBean bean = marksheetList.get(i);
			ProgramExamBean programDetails = programMap.get(bean.getProgram().trim()+"-"+bean.getPrgmStructApplicable());
			String program = programDetails.getProgramname();
			String programType = programDetails.getProgramType();
//			StudentBean student = (StudentBean)request.getSession().getAttribute("student");
			double aggregateTotalOfMarks = 0.0;
			try {

			/*	Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font3);
				collegNameChunk.setUnderline(0.1f, -2f);
				Paragraph collegeName = new Paragraph();
				collegeName.add(collegNameChunk);
				collegeName.setAlignment(Element.ALIGN_CENTER);
				document.add(collegeName);*/


				Chunk stmtOfMarksChunk = new Chunk("STATEMENT OF MARKS", font2);
				stmtOfMarksChunk.setUnderline(0.1f, -2f);
				Paragraph stmtOfMarksPara = new Paragraph();
				stmtOfMarksPara.add(stmtOfMarksChunk);
				stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
				document.add(stmtOfMarksPara);


				PdfPTable headerTable = new PdfPTable(2); // 2 columns.
				headerTable.getDefaultCell().setBorder(0);
				
				String fatherName = bean.getFatherName().toUpperCase();
				
				if(fatherName == null || "".equals(fatherName.trim())){
					fatherName = "";
				}
				
				String middleName = bean.getMiddleName().toUpperCase();
				if(middleName == null || "".equals(middleName.trim())){
					middleName = bean.getFatherName().trim().toUpperCase();
				}
				
				String motherName = bean.getMotherName().toUpperCase();
				if(motherName == null || "".equals(motherName.trim())){
					motherName = "";
				}
				
				PdfPCell nameCell = new PdfPCell(new Paragraph("NAME: "+(bean.getFirstName().trim().toUpperCase()+" "+bean.getLastName().trim()).toUpperCase().replace(".", " "),font1));

				nameCell.setBorder(0);
				nameCell.setColspan(2);
				//nameCell.setPaddingBottom(10);
				headerTable.addCell(nameCell);

				headerTable.completeRow();
				
				PdfPCell fatherNameCell = new PdfPCell(new Paragraph("Father's Name: "+(fatherName).toUpperCase(),font1));
				fatherNameCell.setBorder(0);
				fatherNameCell.setColspan(2);
				//fatherNameCell.setPaddingBottom(10);
				headerTable.addCell(fatherNameCell);
				headerTable.completeRow();
				
				PdfPCell motherNameCell = new PdfPCell(new Paragraph("Mother's Name: "+(motherName).toUpperCase(),font1));
				motherNameCell.setBorder(0);
				motherNameCell.setColspan(2);
				motherNameCell.setPaddingBottom(5);
				headerTable.addCell(motherNameCell);
				headerTable.completeRow();
				
				
				PdfPCell programCell = new PdfPCell(new Paragraph("Program: "+program,font1));
				PdfPCell studentNoCell = new PdfPCell(new Paragraph("Student No: "+bean.getSapid(),font1));
				studentNoCell.setBorder(0);

				programCell.setBorder(0);
				programCell.setPaddingBottom(10);
				headerTable.addCell(programCell);
				headerTable.addCell(studentNoCell);
				headerTable.completeRow();


				PdfPCell rgstnCell = new PdfPCell(new Paragraph("Month and Year of Registration: "+bean.getEnrollmentMonth()
						+"-"+bean.getEnrollmentYear(),font1));

				String romanSem = "";

				if("1".equals(bean.getSem().trim())){
					romanSem = "I";
				}else if("2".equals(bean.getSem().trim())){
					romanSem = "II";
				}else if("3".equals(bean.getSem().trim())){
					romanSem = "III";
				}else if("4".equals(bean.getSem().trim())){
					romanSem = "IV";
				}

				PdfPCell semCell = new PdfPCell(new Paragraph("Semester: "+romanSem,font1));
				rgstnCell.setBorder(0);
				semCell.setBorder(0);

				headerTable.addCell(rgstnCell);
				headerTable.addCell(semCell);
				headerTable.completeRow();

				PdfPCell examYearCell = new PdfPCell(new Paragraph("Month and Year of Examination: "+bean.getExamMonth()+"-"+bean.getExamYear(),font1));
				examYearCell.setBorder(0);
				headerTable.addCell(examYearCell);
				headerTable.completeRow();

				headerTable.setWidthPercentage(100);
				headerTable.setSpacingBefore(10f);
				headerTable.setSpacingAfter(5f);
				float[] columnWidths = {3.2f, 1.3f};
				headerTable.setWidths(columnWidths);
				document.add(headerTable);

				PdfPTable marksTable = new PdfPTable(5);

				PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font8));
				PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subjects", font8));
				String finalMarksTitle = "Final Exam Marks";
				if("PGDMM-MLI".equalsIgnoreCase(bean.getProgram())){
					finalMarksTitle = finalMarksTitle + " (50)";
				}else if("ACBM".equalsIgnoreCase(bean.getProgram())){
					finalMarksTitle = finalMarksTitle + " (60)";
				}else{
					finalMarksTitle = finalMarksTitle + " (70)";
				}
				PdfPCell finalExamMarksCell = new PdfPCell(new Paragraph(finalMarksTitle, font8));

				String assignmentMarksTitle = "Assignment Marks";
				if("PGDMM-MLI".equalsIgnoreCase(bean.getProgram())){
					assignmentMarksTitle = assignmentMarksTitle + " (50)";
				}else if("ACBM".equalsIgnoreCase(bean.getProgram())){
					assignmentMarksTitle = assignmentMarksTitle + " (40)";
				}else{
					assignmentMarksTitle = assignmentMarksTitle + " (30)";
				}
				PdfPCell assignmentMarksCell = new PdfPCell(new Paragraph(assignmentMarksTitle, font8));
				PdfPCell totalMarksCell = new PdfPCell(new Paragraph("Total Marks (100)", font8));

				srNoCell.setFixedHeight(30);
				srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
				subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
				finalExamMarksCell.setVerticalAlignment(Element.ALIGN_TOP);
				assignmentMarksCell.setVerticalAlignment(Element.ALIGN_TOP);
				totalMarksCell.setVerticalAlignment(Element.ALIGN_TOP);

				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				finalExamMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				assignmentMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				marksTable.addCell(srNoCell);
				marksTable.addCell(subjectsCell);
				marksTable.addCell(finalExamMarksCell);
				marksTable.addCell(assignmentMarksCell);
				marksTable.addCell(totalMarksCell);
				marksTable.completeRow();

				int count = 1;
				ArrayList<SubjectResultBean> subjectList = bean.getSubjects();
				ArrayList<SubjectResultBean> projectMarksList = new ArrayList<SubjectResultBean>();
								for(SubjectResultBean sb : subjectList){// to print project at the end of the marks table
								if("Project".equalsIgnoreCase(sb.getSubject())){
									sb.setSubject("Project (Out of 100 marks)");
									projectMarksList.add(sb);
								}
								if("Module 4 - Project".equalsIgnoreCase(sb.getSubject())){
									sb.setSubject("Module 4 - Project (Out of 100 marks)");
									projectMarksList.add(sb);
								}
								
								if("Simulation: Mimic Pro".equalsIgnoreCase(sb.getSubject())){
									sb.setSubject("Simulation: Mimic Pro (Out of 100 marks)");
									projectMarksList.add(sb);
								}
								
								if("Simulation: Mimic Social".equalsIgnoreCase(sb.getSubject())){
									sb.setSubject("Simulation: Mimic Social (Out of 100 marks)");
									projectMarksList.add(sb);
								}
								
								
							}
								
								
								
								subjectList.removeAll(projectMarksList);
								subjectList.addAll(projectMarksList);	
				int numberOfSubjects = subjectList.size();
				for (int j = 0; j < subjectList.size(); j++) {
					SubjectResultBean subject = subjectList.get(j);
					srNoCell = new PdfPCell(new Paragraph(count+"", font1));
					subjectsCell = new PdfPCell(new Paragraph(subject.getSubject(), font1));
					subjectsCell.setPaddingLeft(5);

					String writtenScore = subject.getWrittenScore();
					String assignmentScore = subject.getAssignmentScore();

					if(writtenScore != null && "".equals(writtenScore.trim())){
						writtenScore = "--";
					}

					/*if("Project".equalsIgnoreCase(subject.getSubject())){
						assignmentScore = "--";
					}*/
					if("Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
 						assignmentScore = "--";
 					}
					if("Module 4 - Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
 						assignmentScore = "--";
 					}
					
					if("Simulation: Mimic Pro (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
 						assignmentScore = "--";
 					}
					
					if("Simulation: Mimic Social (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
 						assignmentScore = "--";
 					}

					if("Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
						writtenScore = "--";
					}

					if("Module 4 - Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
						writtenScore = "--";
 					}
					
					if("Simulation: Mimic Pro (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
						writtenScore = "--";
					}
					
					if("Simulation: Mimic Social (Out of 100 marks)".equalsIgnoreCase(subject.getSubject())){
						writtenScore = "--";
					}
					

					Paragraph finalMarks = new Paragraph(writtenScore, font1);
					Paragraph assignmentMarks = new Paragraph(new Paragraph(assignmentScore, font1));		
					String totalMarksValue = subject.getTotal();
					
					
					//Added by Sanket 26-Jan-2017 to handle blank total
					int subjectTotalForCalculation = 0;
					try {
						subjectTotalForCalculation = Integer.parseInt(totalMarksValue);
					} catch (Exception e) {
						subjectTotalForCalculation = 0;
					}
					
					if("ACBM".equals(msBean.getProgram())){
						aggregateTotalOfMarks = aggregateTotalOfMarks + subjectTotalForCalculation;
					}
					
					if(totalMarksValue != null && "".equals(totalMarksValue.trim())){
						totalMarksValue = "--";
					}
					if("AB".equals(writtenScore) && "--".equals(assignmentScore)){
						totalMarksValue = "AB";
					}
					
					boolean isProject = "Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject());
					boolean isModuleProject = "Module 4 - Project (Out of 100 marks)".equalsIgnoreCase(subject.getSubject());
					boolean isMimicPro = "Simulation: Mimic Pro (Out of 100 marks)".equalsIgnoreCase(subject.getSubject());
					boolean isMimicSocial = "Simulation: Mimic Pro (Out of 100 marks)".equalsIgnoreCase(subject.getSubject());
					
					Paragraph totalMarks = new Paragraph(new Paragraph(totalMarksValue, font1));


					// if grace is applied
					if(subject.isGraceApplied()) {
						

						// if simulation or project, add to total marks. else final marks
						if(isProject || isMimicPro || isMimicSocial || isModuleProject) {
							totalMarks.add(grace);
						} else {
							finalMarks.add(grace);
						}
					}
					/*if( subject.isAssignmentCarryForward() && (!"Project".equalsIgnoreCase(subject.getSubject())) ){
						assignmentMarks.add(carryFwd);
					}*/
					
					// if carry forward and not project or simulation
					if( subject.isAssignmentCarryForward() && !isProject && !isMimicPro && !isMimicSocial && !isModuleProject){
 						assignmentMarks.add(carryFwd);
 					}
					/*if(subject.isWrittenCarryForward()){
						finalMarks.add(carryFwd);
					}*/
					if(subject.isWrittenCarryForward() && !isProject  && !isMimicPro && !isMimicSocial && !isModuleProject){
 						finalMarks.add(carryFwd);
 					}
				
					if(subject.isTotalCarryForward() ){
						totalMarks.add(carryFwd);
					}
					if(subject.isResultOnHold()){
						totalMarks.add(resultOnHold);
					}else if(!subject.isPass()){
						totalMarks.add(failure);
					}

					/*if(writtenScore != null && (!"".equals(writtenScore))){
						Chunk monthYear = new Chunk(bean.getWrittenMonth()+"-"+bean.getWrittenYear(), font5);
						monthYear.setTextRise(-5f);
						finalMarks.add(monthYear);
					}

					if(assignmentScore != null && (!"".equals(assignmentScore))){
						Chunk monthYear = new Chunk(bean.getAssignmentMonth()+"-"+bean.getAssignmentYear(), font5);
						monthYear.setTextRise(-5f);
						assignmentMarks.add(monthYear);
					}*/

					if("RIA".equals(writtenScore) || "RIA".equals(assignmentScore))
					{
						finalMarks.clear();
						
						finalMarks.add("RIA");
					}
					
					if("NV".equals(writtenScore) || "NV".equals(assignmentScore))
					{
						finalMarks.clear();
						
						finalMarks.add("NV");
					}
					
					finalExamMarksCell = new PdfPCell(finalMarks);
					assignmentMarksCell = new PdfPCell(assignmentMarks);
					totalMarksCell = new PdfPCell(totalMarks);




					srNoCell.setFixedHeight(25);
					srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					finalExamMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					assignmentMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					totalMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

					srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					finalExamMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					assignmentMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);

					marksTable.addCell(srNoCell);
					marksTable.addCell(subjectsCell);
					marksTable.addCell(finalExamMarksCell);
					marksTable.addCell(assignmentMarksCell);
					marksTable.addCell(totalMarksCell);
					marksTable.completeRow();

					count++;
				}

				marksTable.setWidthPercentage(100);
				marksTable.setSpacingBefore(5f);
				marksTable.setSpacingAfter(10f);
				float[] marksColumnWidths = {1.5f, 11f, 3f, 3f, 3f};
				marksTable.setWidths(marksColumnWidths);
				document.add(marksTable);


				PdfPTable footerTable1 = new PdfPTable(2);
				footerTable1.getDefaultCell().setBorder(0);
				/*PdfPCell studyCenterCell = new PdfPCell(new Paragraph("STUDY CENTER CODE: "+bean.getCenterCode(),font1));
				studyCenterCell.setBorder(0);
				footerTable1.addCell(studyCenterCell);
				footerTable1.completeRow();*/
				/*if("ACBM".equals(student.getProgram())){
					PdfPCell aggregateCell = new PdfPCell(new Paragraph("Aggregate Percentage : "+df.format(aggregateTotalOfMarks/numberOfSubjects)+" %",font1));
					
					aggregateCell.setBorder(0);
					aggregateCell.setColspan(2);
					footerTable1.addCell(aggregateCell);
					footerTable1.completeRow();	
				}*/
				
				
				PdfPCell attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: "+bean.getValidityEndMonth()+"-"+bean.getValidityEndYear()+ " Examination.",font1));
				attemptsCell.setBorder(0);
				attemptsCell.setColspan(2);
				footerTable1.addCell(attemptsCell);
				footerTable1.completeRow();

				footerTable1.setWidthPercentage(100);
				footerTable1.setSpacingBefore(5f);
				footerTable1.setSpacingAfter(5f);
				float[] footer1ColumnWidths = {3f, 3f};
				footerTable1.setWidths(footer1ColumnWidths);
				document.add(footerTable1);


				PdfPTable footerTable2 = new PdfPTable(3);
				PdfPCell prepredByCell = new PdfPCell(new Paragraph("", font1));
				prepredByCell.setFixedHeight(25);
				prepredByCell.setBorder(0);
				PdfPCell resultDeclaredCell = new PdfPCell(new Paragraph("Result declared on", font1));
				resultDeclaredCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				resultDeclaredCell.setBorder(0);

				PdfPCell resultDeclaredValueCell = new PdfPCell(new Paragraph(": "+resultDeclarationDate, font1));
				resultDeclaredValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				resultDeclaredValueCell.setBorder(0);

				footerTable2.addCell(prepredByCell);
				footerTable2.addCell(resultDeclaredCell);
				footerTable2.addCell(resultDeclaredValueCell);
				footerTable2.completeRow();


				PdfPCell checkedByCell = new PdfPCell(new Paragraph("", font1));
				checkedByCell.setBorder(0);
				PdfPCell marksheetIssuedCell = new PdfPCell(new Paragraph("Marksheet issued on", font1));
				marksheetIssuedCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				marksheetIssuedCell.setBorder(0);

				PdfPCell marksheetIssuedValueCell = new PdfPCell(new Paragraph(": "+markSheetDate, font1));
				marksheetIssuedValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				marksheetIssuedValueCell.setBorder(0);

				footerTable2.addCell(checkedByCell);
				footerTable2.addCell(marksheetIssuedCell);
				footerTable2.addCell(marksheetIssuedValueCell);
				footerTable2.completeRow();

				footerTable2.setWidthPercentage(100);
				footerTable2.setSpacingBefore(15f);
				footerTable2.setSpacingAfter(5f);
				float[] footer2ColumnWidths = {12.3f, 4.2f, 3.2f};
				footerTable2.setWidths(footer2ColumnWidths);
				document.add(footerTable2);


				PdfPTable footerTable3 = new PdfPTable(2);
				footerTable3.getDefaultCell().setBorder(0);



				PdfPCell emptyCell = new PdfPCell();
				emptyCell.setBorder(0);
				footerTable3.addCell(emptyCell);

				PdfPCell signatureCell = new PdfPCell();
				//signatureCell.addElement(signature);
				signatureCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
				//signature.setAlignment(Element.ALIGN_RIGHT);
				signatureCell.setBorder(0);
				signatureCell.setVerticalAlignment(Element.ALIGN_BOTTOM);

				footerTable3.addCell(signatureCell);
				footerTable3.completeRow();

				footerTable3.setWidthPercentage(100);
				footerTable3.setSpacingBefore(15f);
				//footerTable3.setSpacingAfter(15f);

				float[] footer3ColumnWidths = {6f, 2f};
				footerTable3.setWidths(footer3ColumnWidths);
				document.add(footerTable3);



				PdfPTable footerTable4 = new PdfPTable(2);
				footerTable4.getDefaultCell().setBorder(0);
				footerTable4.addCell(emptyCell);

				PdfPCell coeCell = new PdfPCell(new Paragraph("CONTROLLER OF EXAMINATIONS", font4));
				coeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				coeCell.setBorder(0);
				coeCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				coeCell.setFixedHeight(15);
				footerTable4.addCell(coeCell);
				footerTable4.completeRow();

				footerTable4.setWidthPercentage(100);
				footerTable4.setSpacingBefore(15f);
				footerTable4.setSpacingAfter(0f);

				float[] footer4ColumnWidths = {3f, 3f};
				footerTable4.setWidths(footer4ColumnWidths);

				document.add(footerTable4);


				document.add(new Paragraph("1)  ANS :  Assignment Not Submitted.", font4));
				document.add(new Paragraph("2)  NA   :  Not Eligible due to non submission of assignment.", font4));
				document.add(new Paragraph("3)  *       :  Failures.", font4));
				document.add(new Paragraph("4)  **      :  Result on Hold due to Non Submission of Assignment.", font4));
				document.add(new Paragraph("5)  #       :  Marks brought forward.", font4));
				document.add(new Paragraph("6)  ~       :  Grace Marks given.", font4));
				document.add(new Paragraph("7)  NV      :  Null And Void", font4));
				/*document.add(new Paragraph(" ", font4));*/
				document.add(new Paragraph("Note:", font4));
				if("Certificate".equals(programType)){
					document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 40", font4));
					document.add(new Paragraph("2)  Minimum aggregate marks for passing overall semester is 40%", font4));
					document.add(new Paragraph("3)  Submission of assignment is compulsory to be declared as pass in a subject in addition "
							+ "to \nappearance at the Term-End Examination.", font4));
					document.add(new Paragraph("4)  This statement of marks is generated through Student Portal of NMIMS Global Access School For Continuing Education.", font4));
					document.add(new Paragraph("5)  To verify authenticity of this marksheet, please refer to original marksheet issued by University.", font4));
				}else{
					if("JUL2017".equalsIgnoreCase(bean.getPrgmStructApplicable())){
						document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 40", font4));
					}else{
						document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 50", font4));
					}
					document.add(new Paragraph("2)  Submission of assignment is compulsory to be declared as pass in a subject in addition "
							+ "to \nappearance at the Term-End Examination.", font4));
					document.add(new Paragraph("3)  This statement of marks is generated through Student Portal of NMIMS Global Access School For Continuing Education.", font4));
					document.add(new Paragraph("4)  To verify authenticity of this marksheet, please refer to original marksheet issued by University.", font4));
				}
				 
				
				
				/*if("PGDMM-MLI".equalsIgnoreCase(bean.getProgram())){
					document.add(new Paragraph("8)  Maximum Final Exam Marks: 50, Maximum Assignment Marks: 50", font4));
				}else{
					document.add(new Paragraph("8)  Maximum Final Exam Marks: 70, Maximum Assignment Marks: 30", font4));
				}*/

				
				
				document.newPage();

			} catch (DocumentException e) {
				
			} catch (Exception e) {
				
			}


		}
		document.close(); // no need to close PDFwriter?
		return msBean;

	}
	
	// createStudentSelfMarksheetPDFForExecutive Start
	public void createStudentSelfMarksheetPDFForExecutive(List<MarksheetBean> marksheetList, String resultDeclarationDate, HttpServletRequest request, 
			HashMap<String, String> programMap, HashMap<String, CenterExamBean> centersMap, String MARKSHEETS_PATH,
			Map<String, ProgramExamBean> modProgramMap) throws Exception {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat(".##");
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		String markSheetDate = sdfr.format(new Date());
		String fileName = null;
		//Image signature = Image.getInstance(new URL("https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/signature.jpg"));
		//Image signature = Image.getInstance("d:/signature.jpg");
		//signature.scaleAbsolute(60, 42);
		String folderPath = "";
		if(marksheetList.size() > 0){
			MarksheetBean bean = marksheetList.get(0);
			String studentId = bean.getSapid();
			String sem = bean.getSem();
			String year = bean.getExamYear();
			String month = bean.getExamMonth();
			fileName = studentId + "_" + month + "_" + year + "_" + bean.getProgram() + "_Sem" + sem + "_"  + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
			fileName = fileName.toUpperCase();
			
			
			folderPath = MARKSHEETS_PATH + month + "-" + year + "/";
			
			File folder = new File(folderPath);
			if (!folder.exists()) {   
				folder.mkdirs();   
			}  
		}
		Document document = new Document(PageSize.A4);
		document.setMargins(50, 50, 40, 40);

		
		
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath+fileName));
		request.getSession().setAttribute("fileName", folderPath+fileName);
		document.open();

		
		Phrase watermark = new Phrase("Marksheet Downloaded from NGASCE Student Portal", new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
		PdfContentByte canvas = writer.getDirectContent();
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 300, 30);
		
		generateHeaderTable(document);
		
		
		
		Chunk failure = new Chunk("*",font6);
		failure.setTextRise(5f);
		
		Chunk resultOnHold = new Chunk("**",font6);
		resultOnHold.setTextRise(5f);

		Chunk carryFwd = new Chunk("#",font6);
		carryFwd.setTextRise(5f);

		Chunk grace = new Chunk("~",font6);
		grace.setTextRise(5f);

		
		for (int i = 0; i < marksheetList.size(); i++) {

			MarksheetBean bean = marksheetList.get(i);
			String program = programMap.get(bean.getProgram().trim());
			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
			double aggregateTotalOfMarks = 0.0;
			try {

			/*	Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font3);
				collegNameChunk.setUnderline(0.1f, -2f);
				Paragraph collegeName = new Paragraph();
				collegeName.add(collegNameChunk);
				collegeName.setAlignment(Element.ALIGN_CENTER);
				document.add(collegeName);*/


				Chunk stmtOfMarksChunk = new Chunk("STATEMENT OF MARKS", font2);
				stmtOfMarksChunk.setUnderline(0.1f, -2f);
				Paragraph stmtOfMarksPara = new Paragraph();
				stmtOfMarksPara.add(stmtOfMarksChunk);
				stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
				document.add(stmtOfMarksPara);


				PdfPTable headerTable = new PdfPTable(2); // 2 columns.
				headerTable.getDefaultCell().setBorder(0);
				
				String fatherName = bean.getFatherName().toUpperCase();
				
				if(fatherName == null || "".equals(fatherName.trim())){
					fatherName = "";
				}
				
				String middleName = bean.getMiddleName().toUpperCase();
				if(middleName == null || "".equals(middleName.trim())){
					middleName = bean.getFatherName().trim().toUpperCase();
				}
				
				String motherName = bean.getMotherName().toUpperCase();
				if(motherName == null || "".equals(motherName.trim())){
					motherName = "";
				}
				
				PdfPCell nameCell = new PdfPCell(new Paragraph("NAME: "+(bean.getFirstName().trim().toUpperCase()+" "+bean.getLastName().trim()).toUpperCase().replace(".", " "),font1));

				nameCell.setBorder(0);
				nameCell.setColspan(2);
				//nameCell.setPaddingBottom(10);
				headerTable.addCell(nameCell);

				headerTable.completeRow();
				
				PdfPCell fatherNameCell = new PdfPCell(new Paragraph("Father's Name: "+(fatherName).toUpperCase(),font1));
				fatherNameCell.setBorder(0);
				fatherNameCell.setColspan(2);
				//fatherNameCell.setPaddingBottom(10);
				headerTable.addCell(fatherNameCell);
				headerTable.completeRow();
				
				PdfPCell motherNameCell = new PdfPCell(new Paragraph("Mother's Name: "+(motherName).toUpperCase(),font1));
				motherNameCell.setBorder(0);
				motherNameCell.setColspan(2);
				motherNameCell.setPaddingBottom(5);
				headerTable.addCell(motherNameCell);
				headerTable.completeRow();
				
				
				PdfPCell programCell = new PdfPCell(new Paragraph("Program: "+program,font1));
				PdfPCell studentNoCell = new PdfPCell(new Paragraph("Student No: "+bean.getSapid(),font1));
				studentNoCell.setBorder(0);

				programCell.setBorder(0);
				programCell.setPaddingBottom(10);
				headerTable.addCell(programCell);
				headerTable.addCell(studentNoCell);
				headerTable.completeRow();

				//Added Mode Of Delivery - START
				ProgramExamBean modProgram = modProgramMap.get(bean.getProgram());
				String mod = modProgram.getModeOfLearning();				
				PdfPCell modCell = new PdfPCell(new Paragraph("Mode of Delivery: "+mod,font1));
				modCell.setBorder(0);
				modCell.setColspan(2);
				modCell.setPaddingBottom(10);
				headerTable.addCell(modCell);
				headerTable.completeRow();
				//Added Mode Of Delivery - END

				PdfPCell rgstnCell = new PdfPCell(new Paragraph("Month and Year of Registration: "+bean.getEnrollmentMonth()
						+"-"+bean.getEnrollmentYear(),font1));

				String romanSem = "";

				if("1".equals(bean.getSem().trim())){
					romanSem = "I";
				}else if("2".equals(bean.getSem().trim())){
					romanSem = "II";
				}else if("3".equals(bean.getSem().trim())){
					romanSem = "III";
				}else if("4".equals(bean.getSem().trim())){
					romanSem = "IV";
				}

				PdfPCell semCell = new PdfPCell(new Paragraph("Semester: "+romanSem,font1));
				rgstnCell.setBorder(0);
				semCell.setBorder(0);

				headerTable.addCell(rgstnCell);
				headerTable.addCell(semCell);
				headerTable.completeRow();

				PdfPCell examYearCell = new PdfPCell(new Paragraph("Month and Year of Examination: "+bean.getExamMonth()+"-"+bean.getExamYear(),font1));
				examYearCell.setBorder(0);
				headerTable.addCell(examYearCell);
				headerTable.completeRow();

				headerTable.setWidthPercentage(100);
				headerTable.setSpacingBefore(10f);
				headerTable.setSpacingAfter(5f);
				float[] columnWidths = {3.2f, 1.3f};
				headerTable.setWidths(columnWidths);
				document.add(headerTable);

				PdfPTable marksTable = new PdfPTable(3);

				PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font8));
				PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subjects", font8));
				
				PdfPCell totalMarksCell = new PdfPCell(new Paragraph("Total Marks (100)", font8));

				srNoCell.setFixedHeight(30);
				srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
				subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
				totalMarksCell.setVerticalAlignment(Element.ALIGN_TOP);

				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				marksTable.addCell(srNoCell);
				marksTable.addCell(subjectsCell);
				marksTable.addCell(totalMarksCell);
				marksTable.completeRow();

				int count = 1;
				ArrayList<SubjectResultBean> subjectList = bean.getSubjects();
				int numberOfSubjects = subjectList.size();
				for (int j = 0; j < subjectList.size(); j++) {
					SubjectResultBean subject = subjectList.get(j);
					srNoCell = new PdfPCell(new Paragraph(count+"", font1));
					subjectsCell = new PdfPCell(new Paragraph(subject.getSubject(), font1));
					subjectsCell.setPaddingLeft(5);

					String writtenScore = subject.getWrittenScore();
					String assignmentScore = subject.getAssignmentScore();
					
					if(writtenScore != null && "".equals(writtenScore.trim())){
						writtenScore = "--";
					}
					if(StringUtils.isBlank(assignmentScore)){
						assignmentScore = "--";
					}

					 


					String totalMarksValue = subject.getTotal();
					
					
					//Added by Sanket 26-Jan-2017 to handle blank total
					int subjectTotalForCalculation = 0;
					try {
						subjectTotalForCalculation = Integer.parseInt(totalMarksValue);
					} catch (Exception e) {
						subjectTotalForCalculation = 0;
					}
					
					if(totalMarksValue != null && "".equals(totalMarksValue.trim())){
						totalMarksValue = "--";
					}
					if("AB".equals(writtenScore) && "--".equals(assignmentScore)){
						totalMarksValue = "AB";
					}
					
					Paragraph totalMarks = new Paragraph(new Paragraph(totalMarksValue, font1));

					
					if(subject.isResultOnHold()){
						totalMarks.add(resultOnHold);
					}else if(!subject.isPass()){
						totalMarks.add(failure);
					}

					/*if(writtenScore != null && (!"".equals(writtenScore))){
						Chunk monthYear = new Chunk(bean.getWrittenMonth()+"-"+bean.getWrittenYear(), font5);
						monthYear.setTextRise(-5f);
						finalMarks.add(monthYear);
					}

					if(assignmentScore != null && (!"".equals(assignmentScore))){
						Chunk monthYear = new Chunk(bean.getAssignmentMonth()+"-"+bean.getAssignmentYear(), font5);
						monthYear.setTextRise(-5f);
						assignmentMarks.add(monthYear);
					}*/

					totalMarksCell = new PdfPCell(totalMarks);




					srNoCell.setFixedHeight(25);
					srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					totalMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

					srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);

					marksTable.addCell(srNoCell);
					marksTable.addCell(subjectsCell);
					marksTable.addCell(totalMarksCell);
					marksTable.completeRow();

					count++;
				}

				marksTable.setWidthPercentage(100);
				marksTable.setSpacingBefore(5f);
				marksTable.setSpacingAfter(10f);
				float[] marksColumnWidths = {1.5f, 11f, 3f};
				marksTable.setWidths(marksColumnWidths);
				document.add(marksTable);


				PdfPTable footerTable1 = new PdfPTable(2);
				footerTable1.getDefaultCell().setBorder(0);
				/*PdfPCell studyCenterCell = new PdfPCell(new Paragraph("STUDY CENTER CODE: "+bean.getCenterCode(),font1));
				studyCenterCell.setBorder(0);
				footerTable1.addCell(studyCenterCell);
				footerTable1.completeRow();*/
				/*if("ACBM".equals(student.getProgram())){
					PdfPCell aggregateCell = new PdfPCell(new Paragraph("Aggregate Percentage : "+df.format(aggregateTotalOfMarks/numberOfSubjects)+" %",font1));
					
					aggregateCell.setBorder(0);
					aggregateCell.setColspan(2);
					footerTable1.addCell(aggregateCell);
					footerTable1.completeRow();	
				}*/
				
				
				PdfPCell attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: "+bean.getValidityEndMonth()+"-"+bean.getValidityEndYear()+ " Examination.",font1));
				attemptsCell.setBorder(0);
				attemptsCell.setColspan(2);
				footerTable1.addCell(attemptsCell);
				footerTable1.completeRow();

				footerTable1.setWidthPercentage(100);
				footerTable1.setSpacingBefore(5f);
				footerTable1.setSpacingAfter(5f);
				float[] footer1ColumnWidths = {3f, 3f};
				footerTable1.setWidths(footer1ColumnWidths);
				document.add(footerTable1);


				PdfPTable footerTable2 = new PdfPTable(3);
				PdfPCell prepredByCell = new PdfPCell(new Paragraph("", font1));
				prepredByCell.setFixedHeight(25);
				prepredByCell.setBorder(0);
				PdfPCell resultDeclaredCell = new PdfPCell(new Paragraph("Result declared on", font1));
				resultDeclaredCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				resultDeclaredCell.setBorder(0);

				PdfPCell resultDeclaredValueCell = new PdfPCell(new Paragraph(": "+resultDeclarationDate, font1));
				resultDeclaredValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				resultDeclaredValueCell.setBorder(0);

				footerTable2.addCell(prepredByCell);
				footerTable2.addCell(resultDeclaredCell);
				footerTable2.addCell(resultDeclaredValueCell);
				footerTable2.completeRow();


				PdfPCell checkedByCell = new PdfPCell(new Paragraph("", font1));
				checkedByCell.setBorder(0);
				PdfPCell marksheetIssuedCell = new PdfPCell(new Paragraph("Marksheet issued on", font1));
				marksheetIssuedCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				marksheetIssuedCell.setBorder(0);

				PdfPCell marksheetIssuedValueCell = new PdfPCell(new Paragraph(": "+markSheetDate, font1));
				marksheetIssuedValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				marksheetIssuedValueCell.setBorder(0);

				footerTable2.addCell(checkedByCell);
				footerTable2.addCell(marksheetIssuedCell);
				footerTable2.addCell(marksheetIssuedValueCell);
				footerTable2.completeRow();

				footerTable2.setWidthPercentage(100);
				footerTable2.setSpacingBefore(15f);
				footerTable2.setSpacingAfter(5f);
				float[] footer2ColumnWidths = {12.3f, 4.2f, 3.2f};
				footerTable2.setWidths(footer2ColumnWidths);
				document.add(footerTable2);


				PdfPTable footerTable3 = new PdfPTable(2);
				footerTable3.getDefaultCell().setBorder(0);



				PdfPCell emptyCell = new PdfPCell();
				emptyCell.setBorder(0);
				footerTable3.addCell(emptyCell);

				PdfPCell signatureCell = new PdfPCell();
				//signatureCell.addElement(signature);
				signatureCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
				//signature.setAlignment(Element.ALIGN_RIGHT);
				signatureCell.setBorder(0);
				signatureCell.setVerticalAlignment(Element.ALIGN_BOTTOM);

				footerTable3.addCell(signatureCell);
				footerTable3.completeRow();

				footerTable3.setWidthPercentage(100);
				footerTable3.setSpacingBefore(15f);
				//footerTable3.setSpacingAfter(15f);

				float[] footer3ColumnWidths = {6f, 2f};
				footerTable3.setWidths(footer3ColumnWidths);
				document.add(footerTable3);



				PdfPTable footerTable4 = new PdfPTable(2);
				footerTable4.getDefaultCell().setBorder(0);
				footerTable4.addCell(emptyCell);

				PdfPCell coeCell = new PdfPCell(new Paragraph("CONTROLLER OF EXAMINATIONS", font4));
				coeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				coeCell.setBorder(0);
				coeCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				coeCell.setFixedHeight(15);
				footerTable4.addCell(coeCell);
				footerTable4.completeRow();

				footerTable4.setWidthPercentage(100);
				footerTable4.setSpacingBefore(15f);
				footerTable4.setSpacingAfter(0f);

				float[] footer4ColumnWidths = {3f, 3f};
				footerTable4.setWidths(footer4ColumnWidths);

				document.add(footerTable4);


				document.add(new Paragraph("1)  *       :  Failures.", font4));
				document.add(new Paragraph("2)  #       :  Marks brought forward.", font4));
				document.add(new Paragraph("3)  NV      :  Null And Void.", font4));
				document.add(new Paragraph("4)  AB      :  Absent.", font4));
				/*document.add(new Paragraph(" ", font4));*/
				document.add(new Paragraph("Note:", font4));
				document.add(new Paragraph("1)  This statement of marks is generated through Student Portal of NMIMS Global Access School For Continuing Education.", font4));
				document.add(new Paragraph("2)  To verify authenticity of this marksheet, please refer to original marksheet issued by University.", font4));
								
				document.newPage();

			} catch (DocumentException e) {
				
			} catch (Exception e) {
				
			}


		}
		document.close(); // no need to close PDFwriter?

	}
	//End
	
	
	private void generateHeaderTable(Document document) throws Exception {
		Image logo = null;
		try {
			logo = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nmims_logo.jpg"));
			//Image signature = Image.getInstance(new URL("http://admissions-ngasce.nmims.edu:4001/StudentDocuments/studentPhotos/signature.jpg"));

			logo.scaleAbsolute(142,40);

			/*SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
			String fileDate = sdfr.format(new Date());
			Paragraph datePara = new Paragraph("Date: "+fileDate, font6);
			datePara.setAlignment(Element.ALIGN_RIGHT);
			document.add(datePara);*/

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

			/*mostRecentTimetablePeriod =  mostRecentTimetablePeriod.replaceAll("Dec", "December");
			mostRecentTimetablePeriod =  mostRecentTimetablePeriod.replaceAll("Jun", "June");
			mostRecentTimetablePeriod =  mostRecentTimetablePeriod.replaceAll("Sep", "September");
			mostRecentTimetablePeriod =  mostRecentTimetablePeriod.replaceAll("Apr", "April");
*/
			/*String title = "HALL TICKET for "+mostRecentTimetablePeriod;
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
			document.add(stmtOfMarksPara);*/

		} catch (Exception e) {
			
			throw e;
		}
	}

	private void generateSignatureSheet(Document document,	List<MarksheetBean> marksheetList, 
			HashMap<String, String> programMap, HashMap<String, CenterExamBean> centersMap, HashMap<String, CenterExamBean> centerNameCenterMap) throws DocumentException {
		
		//document.setMargins(50, 50, 20, 40);
		int studentCount = 1;
		String learningCenter = "None";
		PdfPTable signatureTable = new PdfPTable(6);
		for (int i = 0; i < marksheetList.size(); i++) {

			MarksheetBean bean = marksheetList.get(i);
			String examMonth = bean.getExamMonth();
			String examYear = bean.getExamYear();
			
			String program = programMap.get(bean.getProgram().trim());
			
			
			String currentLearningCenter = bean.getLc();
			if(!learningCenter.equalsIgnoreCase(currentLearningCenter)){
				studentCount = 1;
				
				float[] marksColumnWidths = {1.5f, 5f, 8f, 3f,2f, 8f};
				signatureTable.setWidths(marksColumnWidths);
				
				signatureTable.setWidthPercentage(100);
				signatureTable.setSpacingBefore(15f);
				signatureTable.setSpacingAfter(10f);
				
				document.add(signatureTable);
				document.newPage();
				
				signatureTable = new PdfPTable(6);
				CenterExamBean centerBean = centersMap.get(bean.getCenterCode()) ;

				//Paragraph centerCodePara = null;
				Paragraph centerNamePara = null;
				Paragraph centerAddress = null;

				if(centerBean != null){
					//centerCodePara = new Paragraph("Center Code: " + centerBean.getCenterCode(), font9);
					centerNamePara = new Paragraph("Learning Center: " + centerBean.getLc(), font1);
					String lcAddress = "";
					if(centerNameCenterMap.get(centerBean.getLc()) != null){
						lcAddress = centerNameCenterMap.get(centerBean.getLc()).getAddress();
					}
					centerAddress = new Paragraph("Center Address: " + lcAddress, font1);
				}else{
					//centerCodePara = new Paragraph("Center Code: Not Available", font9);
					centerNamePara = new Paragraph("Learning Center: Not Available", font1);
					centerAddress = new Paragraph("Center Address: Not Available", font1);
				}

				Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font2);
				collegNameChunk.setUnderline(0.1f, -2f);
				Paragraph collegeName = new Paragraph();
				collegeName.add(collegNameChunk);
				collegeName.setAlignment(Element.ALIGN_CENTER);
				document.add(collegeName);
				
				Chunk titleChunk = new Chunk("SIGNATURE SHEET", font2);
				titleChunk.setUnderline(0.1f, -2f);
				Paragraph titlePara = new Paragraph();
				titlePara.add(titleChunk);
				titlePara.setAlignment(Element.ALIGN_CENTER);
				titlePara.setSpacingAfter(10f);
				document.add(titlePara);
				
				//Paragraph programPara = new Paragraph("Program: " + program, font1);
				//Paragraph semPara = new Paragraph("Sem: " + bean.getSem(), font1);

				//document.add(programPara);
				//document.add(semPara);
				//document.add(centerCodePara);
				document.add(centerNamePara);
				document.add(centerAddress);
				learningCenter = currentLearningCenter;
				//document.newPage();
				
				
				
				
				PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font9));
				PdfPCell sapIdCell = new PdfPCell(new Paragraph("SAP ID", font9));
				PdfPCell studentNameCell = new PdfPCell(new Paragraph("Student Name", font9));
				PdfPCell examYearMonthCell = new PdfPCell(new Paragraph("Exam Year-Month", font9));
				PdfPCell semCell = new PdfPCell(new Paragraph("Sem", font9));
				PdfPCell signatureCell = new PdfPCell(new Paragraph("Signature", font9));
				srNoCell.setFixedHeight(25);
				
				signatureTable.addCell(srNoCell);
				signatureTable.addCell(sapIdCell);
				signatureTable.addCell(studentNameCell);
				signatureTable.addCell(examYearMonthCell);
				signatureTable.addCell(semCell);
				signatureTable.addCell(signatureCell);

				signatureTable.completeRow();
				
			}
			
			PdfPCell srNoCell = new PdfPCell(new Paragraph((studentCount++)+"", font9));
			PdfPCell sapIdCell = new PdfPCell(new Paragraph(bean.getSapid(), font9));
			PdfPCell studentNameCell = new PdfPCell(new Paragraph(bean.getFirstName().trim().toUpperCase() + " "+ bean.getLastName().trim().toUpperCase().replace(".", " "), font9));
			PdfPCell examYearMonthCell = new PdfPCell(new Paragraph(bean.getExamMonth() + "-" + bean.getExamYear(), font9));
			PdfPCell semCell = new PdfPCell(new Paragraph(bean.getSem(), font9));
			
			PdfPCell signatureCell = new PdfPCell(new Paragraph(" ", font9));
			srNoCell.setFixedHeight(25);
			srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			
			signatureTable.addCell(srNoCell);
			signatureTable.addCell(sapIdCell);
			signatureTable.addCell(studentNameCell);
			signatureTable.addCell(examYearMonthCell);
			signatureTable.addCell(semCell);
			signatureTable.addCell(signatureCell);

			signatureTable.completeRow();
			
			if(i == marksheetList.size()- 1){
				float[] marksColumnWidths = {1.5f, 5f, 8f, 3f,2f, 8f};
				signatureTable.setWidths(marksColumnWidths);
				
				signatureTable.setWidthPercentage(100);
				signatureTable.setSpacingBefore(15f);
				signatureTable.setSpacingAfter(10f);
				
				document.add(signatureTable);
				document.newPage();
			}
			
		}
		//document.setMargins(50, 50, 120, 40);
		
	}


	public List<MarksheetBean> generateMarksheetList(List<PassFailExamBean> studentMarksList, HashMap<String, MarksheetBean> studentMap, 
			String examMonth, String examYear, HashMap<String, BigDecimal> examOrderMap, String resultDeclarationDate, HashMap<String, CenterExamBean> centersMap) {


		//double currentExamOrder = examOrderMap.get(examMonth+examYear).doubleValue();
		//double lastAttemptOrder = 0;
		List<MarksheetBean> resultList = new ArrayList<>();
		for (int i = 0; i < studentMarksList.size(); i++) {
			PassFailExamBean bean = studentMarksList.get(i);
			String sapId = bean.getSapid().trim();

			SubjectResultBean result = new SubjectResultBean(bean.getSubject(), bean.getWrittenscore(),
					bean.getAssignmentscore(), bean.getGracemarks(), bean.getTotal(), bean.getWrittenMonth(), bean.getWrittenYear());
			String graceMarks = bean.getGracemarks();
			if("1".equals(graceMarks) || "2".equals(graceMarks) || (graceMarks != null && (!"".equals(graceMarks)) )  ){
				result.setGraceApplied(true);
			}else{
				result.setGraceApplied(false);
			}

			if("ANS".equalsIgnoreCase(bean.getAssignmentscore()) && NumberUtils.isNumber(bean.getWrittenscore())){
				result.setResultOnHold(true);
			}else{
				result.setResultOnHold(false);
			}
			
			if(examMonth.equals(bean.getAssignmentMonth()) && examYear.equals(bean.getAssignmentYear())){
				result.setAssignmentCarryForward(false);
			}else{
				result.setAssignmentCarryForward(true);
			}

			if(examMonth.equals(bean.getWrittenMonth()) && examYear.equals(bean.getWrittenYear())){
				result.setWrittenCarryForward(false);
			}else{
				result.setWrittenCarryForward(true);
			}

			if(result.isAssignmentCarryForward() && result.isWrittenCarryForward()){
				result.setTotalCarryForward(true);
			}else{
				result.setTotalCarryForward(false);
			}
			/*if( (examMonth.equals(bean.getAssignmentMonth()) && examYear.equals(bean.getAssignmentYear())) || 
					(examMonth.equals(bean.getWrittenMonth()) && examYear.equals(bean.getWrittenYear()))	){
				result.setTotalCarryForward(false);

				if(examMonth.equals(bean.getAssignmentMonth()) && examYear.equals(bean.getAssignmentYear())){
					result.setCarryForward(false);
				}else{
					result.setCarryForward(true);
				}

			}else{
				result.setTotalCarryForward(true);
				result.setCarryForward(true);
				result.setWrittenCarryForward(true);
			}*/

			if("Y".equals(bean.getIsPass())){
				result.setPass(true);
			}else{
				result.setPass(false);
			}


			MarksheetBean marksSheetBean = studentMap.get(sapId);
			marksSheetBean.setResultDeclarationDate(resultDeclarationDate);
			CenterExamBean center = centersMap.get(marksSheetBean.getCenterCode());
			if(center != null){
				marksSheetBean.setLc(center.getLc());
			}
			
			
			/*if("79".startsWith(sapId)){
				continue;
			}
			//Comment below section. Added temporarily.
			if("79".startsWith(sapId) || marksSheetBean == null){
				continue;
			}*/
			/*if(marksSheetBean == null){
				continue;
			}*/
			marksSheetBean.getSubjects().add(result);

			if(!resultList.contains(marksSheetBean)){

				/*try{
					lastAttemptOrder = examOrderMap.get(marksSheetBean.getValidityEndMonth().trim()+ marksSheetBean.getValidityEndYear().trim()).doubleValue();
					if("Jul2014".equals(marksSheetBean.getPrgmStructApplicable())){
						marksSheetBean.setAttemptsRemaining(Integer.valueOf( ((lastAttemptOrder - currentExamOrder)/0.5) + "") + "") ;
					}else{
						marksSheetBean.setAttemptsRemaining(Integer.valueOf( (lastAttemptOrder - currentExamOrder) + "") + "");
					}
				}catch(Exception e)
				{
					marksSheetBean.setAttemptsRemaining("Not Available");
					
				}*/

				marksSheetBean.setProgram(bean.getProgram());
				marksSheetBean.setWrittenMonth(bean.getWrittenMonth());
				marksSheetBean.setWrittenYear(bean.getWrittenYear());
				marksSheetBean.setAssignmentMonth(bean.getAssignmentMonth());
				marksSheetBean.setAssignmentYear(bean.getAssignmentYear());
				marksSheetBean.setSem(bean.getSem());
				resultList.add(marksSheetBean);
			}

		}


		return resultList;
	}



	public MarksheetBean generateSingleStudentMarksheet(List<PassFailExamBean> studentMarksList, MarksheetBean studentData, 
			String examMonth, String examYear, HashMap<String, BigDecimal> examOrderMap, HashMap<String, CenterExamBean> centersMap) {


		//double currentExamOrder = examOrderMap.get(examMonth+examYear).doubleValue();
		//double lastAttemptOrder = 0.0;

		for (int i = 0; i < studentMarksList.size(); i++) {
			PassFailExamBean bean = studentMarksList.get(i);
			String sapId = bean.getSapid().trim();

			SubjectResultBean result = new SubjectResultBean(bean.getSubject(), bean.getWrittenscore(),
					bean.getAssignmentscore(), bean.getGracemarks(), bean.getTotal(), bean.getWrittenMonth(), bean.getWrittenYear());

			String graceMarks = bean.getGracemarks();
			if("1".equals(graceMarks) || "2".equals(graceMarks) || (graceMarks != null && (!"".equals(graceMarks)) )  ){
				result.setGraceApplied(true);
			}else{
				result.setGraceApplied(false);
			}
			
			if("ANS".equalsIgnoreCase(bean.getAssignmentscore()) && NumberUtils.isNumber(bean.getWrittenscore())){
				result.setResultOnHold(true);
			}else{
				result.setResultOnHold(false);
			}
			/*			
			if("1".equals(bean.getGracemarks()) || "2".equals(bean.getGracemarks())){
				result.setGraceApplied(true);
			}else{
				result.setGraceApplied(false);
			}
			 */

			if(examMonth.equals(bean.getAssignmentMonth()) && examYear.equals(bean.getAssignmentYear())){
				result.setAssignmentCarryForward(false);
			}else{
				result.setAssignmentCarryForward(true);
			}

			if(examMonth.equals(bean.getWrittenMonth()) && examYear.equals(bean.getWrittenYear())){
				result.setWrittenCarryForward(false);
			}else{
				result.setWrittenCarryForward(true);
			}

			if(result.isAssignmentCarryForward() && result.isWrittenCarryForward()){
				result.setTotalCarryForward(true);
			}else{
				result.setTotalCarryForward(false);
			}

			/*if( (examMonth.equals(bean.getAssignmentMonth()) && examYear.equals(bean.getAssignmentYear())) || 
					(examMonth.equals(bean.getWrittenMonth()) && examYear.equals(bean.getWrittenYear()))	){
				result.setTotalCarryForward(false);

				if(examMonth.equals(bean.getAssignmentMonth()) && examYear.equals(bean.getAssignmentYear())){
					result.setCarryForward(false);
				}else{
					result.setCarryForward(true);
				}

			}else{
				result.setTotalCarryForward(true);
			}*/

			if(bean.isWaivedOff()){
				result.setWaivedOff(true);
			}

			if("Y".equals(bean.getIsPass())){
				result.setPass(true);
			}else{
				result.setPass(false);
				result.setGraceApplied(false); // If student is failed do not show the grace symbol
			}
			
			result.setRemarks(bean.getRemarks());
			
			studentData.getSubjects().add(result);
			
			
			CenterExamBean center = centersMap.get(studentData.getCenterCode());
			if(center != null){
				studentData.setLc(center.getLc());
			}
			
			
			/*try{
				lastAttemptOrder = examOrderMap.get(studentData.getValidityEndMonth().trim()+ studentData.getValidityEndYear().trim()).doubleValue();
				
				if("Jul2014".equals(studentData.getPrgmStructApplicable())){
					studentData.setAttemptsRemaining(Integer.valueOf( ((lastAttemptOrder - currentExamOrder)/0.5) + "") + "") ;
				}else{
					studentData.setAttemptsRemaining(Integer.valueOf( (lastAttemptOrder - currentExamOrder) + "") + "");
				}
				
			}catch(Exception e)
			{
				studentData.setAttemptsRemaining("Not Available");
				
			}*/

			studentData.setProgram(bean.getProgram());
			studentData.setWrittenMonth(bean.getWrittenMonth());
			studentData.setWrittenYear(bean.getWrittenYear());
			studentData.setAssignmentMonth(bean.getAssignmentMonth());
			studentData.setAssignmentYear(bean.getAssignmentYear());
			studentData.setSem(bean.getSem());
			studentData.setExamMonth(examMonth);
			studentData.setExamYear(examYear);

		}


		return studentData;
	}
	
	// generateSingleStudentMarksheetForExecutive Start
	public MarksheetBean generateSingleStudentMarksheetForExecutive(List<PassFailExamBean> studentMarksList, MarksheetBean studentData, 
			String examMonth, String examYear, HashMap<String, BigDecimal> examOrderMap, HashMap<String, CenterExamBean> centersMap) {


		//double currentExamOrder = examOrderMap.get(examMonth+examYear).doubleValue();
		//double lastAttemptOrder = 0.0;

		for (int i = 0; i < studentMarksList.size(); i++) {
			PassFailExamBean bean = studentMarksList.get(i);
			//String sapId = bean.getSapid().trim();

			SubjectResultBean result = new SubjectResultBean(bean.getSubject(), bean.getWrittenscore(),
					bean.getTotal(), bean.getWrittenMonth(),bean.getWrittenYear());

			/*
			 * Not required now 23may but has to be implemented soon and data should be from db
			 * 
			 *String graceMarks = bean.getGracemarks();
			if("1".equals(graceMarks) || "2".equals(graceMarks) || (graceMarks != null && (!"".equals(graceMarks)) )  ){
				result.setGraceApplied(true);
			}else{
				result.setGraceApplied(false);
			}*/
			
			

			

			/* discuss with sir 23may PS
			 * 
			 * if(examMonth.equals(bean.getWrittenMonth()) && examYear.equals(bean.getWrittenYear())){
				result.setWrittenCarryForward(false);
			}else{
				result.setWrittenCarryForward(true);
			}*/

			  




			if("Y".equals(bean.getIsPass())){
				result.setPass(true);
			}else{
				result.setPass(false);
			}

			studentData.getSubjects().add(result);
			
			
			CenterExamBean center = centersMap.get(studentData.getCenterCode());
			if(center != null){
				studentData.setLc(center.getLc());
			}
			
			
			

			studentData.setProgram(bean.getProgram());
			studentData.setWrittenMonth(bean.getWrittenMonth());
			studentData.setWrittenYear(bean.getWrittenYear());
			studentData.setSem(bean.getSem());
			studentData.setExamMonth(examMonth);
			studentData.setExamYear(examYear);

		}


		return studentData;
	}
	// generateSingleStudentMarksheetForExecutive end
	
	public void createMarksheetPDFForSAS(List<MarksheetBean> marksheetList, String resultDeclarationDate, HttpServletRequest request, 
						HashMap<String, String> programMap, HashMap<String, CenterExamBean> centersMap, String MARKSHEETS_PATH,
						Map<String, ProgramExamBean> modProgramMap) throws FileNotFoundException, DocumentException, IOException {
					// TODO Auto-generated method stub
					DecimalFormat df = new DecimalFormat(".##");
					HashMap<String, CenterExamBean> centerNameCenterMap = generateCenterNameCenterMap(centersMap);//This will make another map with key as Name insteadof code/id
					
					SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
					String markSheetDate = sdfr.format(new Date());
					String fileName = null;
			
					Image signature = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/signature.jpg"));
					//Image signature = Image.getInstance("d:/signature.jpg");
					signature.scaleAbsolute(60, 42);
					String folderPath = "";
					if(marksheetList.size() > 0){
						MarksheetBean bean = marksheetList.get(0);
						String sem = bean.getSem();
						String year = bean.getExamYear();
						String month = bean.getExamMonth();
						fileName = month + "_" + year + "_" + bean.getProgram() + "_Sem" + sem + ".pdf";
					fileName = fileName.toUpperCase();
						
						folderPath = MARKSHEETS_PATH + month + "-" + year + "/";
						
						File folder = new File(folderPath);
						if (!folder.exists()) {   
							folder.mkdirs();   
						}  
					}
					Document document = new Document(PageSize.A4);
					document.setMargins(50, 50, 115, 30);
			
					
					
					PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath+fileName));
					request.getSession().setAttribute("fileName", folderPath+fileName);
					document.open();
			
					Chunk failure = new Chunk("*",font6);
					failure.setTextRise(5f);
					
					Chunk resultOnHold = new Chunk("**",font6);
					resultOnHold.setTextRise(5f);
			
					
					Chunk carryFwd = new Chunk("#",font6);
					carryFwd.setTextRise(5f);
			
					Chunk grace = new Chunk("~",font6);
					grace.setTextRise(5f);
			
					
					//generateSignatureSheet(document, marksheetList, programMap, centersMap,centerNameCenterMap);
					document.newPage();
				     document.setMargins(50, 50, 100, 40);
					
					//String learningCenter = "None";
					for (int i = 0; i < marksheetList.size(); i++) {
			
						MarksheetBean bean = marksheetList.get(i);
						String program = programMap.get(bean.getProgram().trim());
						/*Commented as requested by tee team : learning center page should be removed.
						 * String currentLearningCenter = bean.getLc();
						if(!learningCenter.equalsIgnoreCase(currentLearningCenter)){
							CenterBean centerBean = centersMap.get(bean.getCenterCode()) ;
			
							//Paragraph centerCodePara = null;
							Paragraph centerNamePara = null;
							Paragraph centerAddress = null;
			
							if(centerBean != null){
								//centerCodePara = new Paragraph("Center Code: " + centerBean.getCenterCode(), font7);
								centerNamePara = new Paragraph("Learning Center: " + centerBean.getLc(), font1);
								String lcAddress = "";
								if(centerNameCenterMap.get(centerBean.getLc()) != null){
									lcAddress = centerNameCenterMap.get(centerBean.getLc()).getAddress();
								}
								
								centerAddress = new Paragraph("Center Address: " + lcAddress, font1);
							}else{
								//centerCodePara = new Paragraph("Center Code: Not Available", font7);
								centerNamePara = new Paragraph("Center Name: Not Available", font7);
								centerAddress = new Paragraph("Center Address: Not Available", font7);
							}
			
							//Paragraph programPara = new Paragraph("Program: " + program, font7);
							//Paragraph semPara = new Paragraph("Sem: " + bean.getSem(), font7);
			
							//document.add(programPara);
							//document.add(semPara);
			//				document.add(centerCodePara);
							document.add(centerNamePara);
							document.add(centerAddress);
							learningCenter = currentLearningCenter;
							document.newPage();
						}*/
						try {
			
							
							Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font3);
							collegNameChunk.setUnderline(0.1f, -2f);
							Paragraph collegeName = new Paragraph();
							collegeName.add(collegNameChunk);
							collegeName.setAlignment(Element.ALIGN_CENTER);
							document.add(collegeName);
			
			
							Chunk stmtOfMarksChunk = new Chunk("STATEMENT OF MARKS", font2);
							stmtOfMarksChunk.setUnderline(0.1f, -2f);
							Paragraph stmtOfMarksPara = new Paragraph();
							stmtOfMarksPara.add(stmtOfMarksChunk);
						stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
							document.add(stmtOfMarksPara);
			
										PdfPTable headerTable = new PdfPTable(2); // 2 columns.
							headerTable.getDefaultCell().setBorder(0);
							
						String fatherName = bean.getFatherName().toUpperCase();
							
							if(fatherName == null || "".equals(fatherName.trim())){
								fatherName = "";
							}
							
							String middleName = bean.getMiddleName().toUpperCase();
							if(middleName == null || "".equals(middleName.trim())){
								middleName = bean.getFatherName().trim().toUpperCase();
							}
							
							String motherName = bean.getMotherName().toUpperCase();
							if(motherName == null || "".equals(motherName.trim())){
							motherName = "";
							}
							
							PdfPCell nameCell = new PdfPCell(new Paragraph("NAME: "+(bean.getFirstName().trim().toUpperCase()+" "+bean.getLastName().trim()).toUpperCase().replace(".", " "),font1));
			
							nameCell.setBorder(0);
							nameCell.setColspan(2);
							//nameCell.setPaddingBottom(10);
							headerTable.addCell(nameCell);
			
							headerTable.completeRow();
							
							PdfPCell fatherNameCell = new PdfPCell(new Paragraph("Father's Name: "+(fatherName).toUpperCase(),font1));
							fatherNameCell.setBorder(0);
							fatherNameCell.setColspan(2);
							//fatherNameCell.setPaddingBottom(10);
							headerTable.addCell(fatherNameCell);
							headerTable.completeRow();
							
							PdfPCell motherNameCell = new PdfPCell(new Paragraph("Mother's Name: "+(motherName).toUpperCase(),font1));
							motherNameCell.setBorder(0);
							motherNameCell.setColspan(2);
							motherNameCell.setPaddingBottom(5);
							headerTable.addCell(motherNameCell);
							headerTable.completeRow();
						
							
							PdfPCell programCell = new PdfPCell(new Paragraph("Program: "+program,font1));
							PdfPCell studentNoCell = new PdfPCell(new Paragraph("Student No: "+bean.getSapid(),font1));
							studentNoCell.setBorder(0);
			
							programCell.setBorder(0);
							programCell.setPaddingBottom(10);
							headerTable.addCell(programCell);
						headerTable.addCell(studentNoCell);
						headerTable.completeRow();
			
							//Added Mode Of Delivery - START
							ProgramExamBean modProgram = modProgramMap.get(bean.getProgram());
							String mod = modProgram.getModeOfLearning();
							PdfPCell modCell = new PdfPCell(new Paragraph("Mode of Delivery: "+mod,font1));
							modCell.setBorder(0);
							modCell.setColspan(2);
							modCell.setPaddingBottom(10);
							headerTable.addCell(modCell);
							headerTable.completeRow();
							//Added Mode Of Delivery - End
			
							PdfPCell rgstnCell = new PdfPCell(new Paragraph("Month and Year of Registration: "+bean.getEnrollmentMonth()
									+"-"+bean.getEnrollmentYear(),font1));
			
							String romanSem = "";
			
							if("1".equals(bean.getSem().trim())){
								romanSem = "I";
							}else if("2".equals(bean.getSem().trim())){
								romanSem = "II";
							}else if("3".equals(bean.getSem().trim())){
								romanSem = "III";
							}else if("4".equals(bean.getSem().trim())){
								romanSem = "IV";
							}
			
							PdfPCell semCell = new PdfPCell(new Paragraph("Semester: "+romanSem,font1));
							rgstnCell.setBorder(0);
							semCell.setBorder(0);
			
							headerTable.addCell(rgstnCell);
							headerTable.addCell(semCell);
							headerTable.completeRow();
			
							PdfPCell examYearCell = new PdfPCell(new Paragraph("Month and Year of Examination: "+bean.getExamMonth()+"-"+bean.getExamYear(),font1));
							examYearCell.setBorder(0);
							headerTable.addCell(examYearCell);
							headerTable.completeRow();
			
							headerTable.setWidthPercentage(100);
							headerTable.setSpacingBefore(10f);
							headerTable.setSpacingAfter(5f);
							float[] columnWidths = {3.2f, 1.3f};
							headerTable.setWidths(columnWidths);
						document.add(headerTable);
						PdfPTable marksTable = new PdfPTable(3);
			
							PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font8));
							PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subjects", font8));
							
						PdfPCell totalMarksCell = new PdfPCell(new Paragraph("Total Marks (100)", font8));
			
							srNoCell.setFixedHeight(30);
							srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
							subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
							
							totalMarksCell.setVerticalAlignment(Element.ALIGN_TOP);
			
							srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							
							totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			
							marksTable.addCell(srNoCell);
							marksTable.addCell(subjectsCell);
							
							marksTable.addCell(totalMarksCell);
							marksTable.completeRow();
			
						int count = 1;
						ArrayList<SubjectResultBean> subjectList = bean.getSubjects();
							// Aggregate Percentage of ACBM student 
							int numberOfSubjects = subjectList.size();
							int subjectTotalForCalculation = 0;
							//double aggregateTotalOfMarks =0.0;
							for (int j = 0; j < subjectList.size(); j++) {
							SubjectResultBean subject = subjectList.get(j);
			
								srNoCell = new PdfPCell(new Paragraph(count+"", font1));
								subjectsCell = new PdfPCell(new Paragraph(subject.getSubject(), font1));
								subjectsCell.setPaddingLeft(5);
			
								String writtenScore = subject.getWrittenScore();
								//String assignmentScore = subject.getAssignmentScore();
								if(writtenScore != null && "".equals(writtenScore.trim())){
									writtenScore = "--";
								}
			
								/*if("Project".equalsIgnoreCase(subject.getSubject())){
									assignmentScore = "--";
								}*/
			
			
									
								String totalMarksValue = subject.getTotal();
								
								if(totalMarksValue != null && "".equals(totalMarksValue.trim())){
									totalMarksValue = "--";
								}
								if("AB".equals(writtenScore)){
									totalMarksValue = "AB";
								}
								//Added by Sanket 26-Jan-2017 to handle blank total
								
								try {
									subjectTotalForCalculation = Integer.parseInt(totalMarksValue);
								} catch (Exception e) {
									subjectTotalForCalculation = 0;
								}
								
								
								
								Paragraph totalMarks = new Paragraph(new Paragraph(totalMarksValue, font1));
			
								
								
								if(subject.isResultOnHold()){
									totalMarks.add(resultOnHold);
							}else if(!subject.isPass()){
									totalMarks.add(failure);
								}
			
								
								totalMarksCell = new PdfPCell(totalMarks);
			
								srNoCell.setFixedHeight(25);
								srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
								subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
								
								totalMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			
								srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							
								totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			
								marksTable.addCell(srNoCell);
								marksTable.addCell(subjectsCell);
								
								marksTable.addCell(totalMarksCell);
								marksTable.completeRow();
			
								count++;
							}
			
			
			
							marksTable.setWidthPercentage(100);
							marksTable.setSpacingBefore(5f);
							marksTable.setSpacingAfter(5f);
							float[] marksColumnWidths = {1.5f, 11f, 3f};
							marksTable.setWidths(marksColumnWidths);
							document.add(marksTable);
			
			
							PdfPTable footerTable1 = new PdfPTable(2);
							footerTable1.getDefaultCell().setBorder(0);
							
							//PdfPCell attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: "+bean.getValidityEndMonth()+"-"+bean.getValidityEndYear()+ " Examination.",font1));
							PdfPCell attemptsCell = new PdfPCell();
							if("EPBM".equalsIgnoreCase(bean.getProgram()) && "Jan".equalsIgnoreCase(bean.getEnrollmentMonth()) && "2018".equalsIgnoreCase(bean.getEnrollmentYear())){
								 attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: Jan-2019",font1));
							}else if("EPBM".equalsIgnoreCase(bean.getProgram()) && "Jul".equalsIgnoreCase(bean.getEnrollmentMonth()) && "2018".equalsIgnoreCase(bean.getEnrollmentYear())){
								 attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: May-2019",font1));
							}else{
								 attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: "+bean.getValidityEndMonth()+"-"+bean.getValidityEndYear(),font1));
							}
							
							attemptsCell.setBorder(0);
							attemptsCell.setColspan(2);
						footerTable1.addCell(attemptsCell);
						footerTable1.completeRow();
			
			
			
							footerTable1.setWidthPercentage(100);
							footerTable1.setSpacingBefore(3f);
							footerTable1.setSpacingAfter(6f);
							float[] footer1ColumnWidths = {3f, 3f};
							footerTable1.setWidths(footer1ColumnWidths);
							document.add(footerTable1);
			
			
							PdfPTable footerTable2 = new PdfPTable(3);
							
							  PdfPCell prepredByCell = new PdfPCell(new
							  Paragraph("Prepared by: ________________________", font1));
							  prepredByCell.setFixedHeight(25); prepredByCell.setBorder(0);
							 
							PdfPCell resultDeclaredCell = new PdfPCell(new Paragraph("Result declared on", font1));
							resultDeclaredCell.setHorizontalAlignment(Element.ALIGN_LEFT);
							resultDeclaredCell.setBorder(0);
			
							PdfPCell resultDeclaredValueCell = new PdfPCell(new Paragraph(": "+bean.getResultDeclarationDate(), font1));
							resultDeclaredValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
							resultDeclaredValueCell.setBorder(0);
			
							footerTable2.addCell(prepredByCell);
							footerTable2.addCell(resultDeclaredCell);
							footerTable2.addCell(resultDeclaredValueCell);
							footerTable2.completeRow();
			
			
							PdfPCell checkedByCell = new PdfPCell(new Paragraph("Checked by: ________________________", font1));
							checkedByCell.setBorder(0);
							PdfPCell marksheetIssuedCell = new PdfPCell(new Paragraph("Marksheet issued on", font1));
							marksheetIssuedCell.setHorizontalAlignment(Element.ALIGN_LEFT);
							marksheetIssuedCell.setBorder(0);
			
							PdfPCell marksheetIssuedValueCell = new PdfPCell(new Paragraph(": "+markSheetDate, font1));
							marksheetIssuedValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
							marksheetIssuedValueCell.setBorder(0);
			
							footerTable2.addCell(checkedByCell);
							footerTable2.addCell(marksheetIssuedCell);
							footerTable2.addCell(marksheetIssuedValueCell);
							footerTable2.completeRow();
			
						footerTable2.setWidthPercentage(100);
							footerTable2.setSpacingBefore(10f);
					footerTable2.setSpacingAfter(5f);
						float[] footer2ColumnWidths = {12.3f, 4.2f, 3.2f};
							footerTable2.setWidths(footer2ColumnWidths);
							document.add(footerTable2);
			
			
							PdfPTable footerTable3 = new PdfPTable(2);
							footerTable3.getDefaultCell().setBorder(0);
			
			
							PdfPCell emptyCell = new PdfPCell();
							emptyCell.setBorder(0);
							footerTable3.addCell(emptyCell);
			
							PdfPCell signatureCell = new PdfPCell();
							signatureCell.addElement(signature);
							signatureCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
							//signature.setAlignment(Element.ALIGN_RIGHT);
							signatureCell.setBorder(0);
							signatureCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
									footerTable3.addCell(signatureCell);
							footerTable3.completeRow();
			
							footerTable3.setWidthPercentage(100);
							footerTable3.setSpacingBefore(10f);
							//footerTable3.setSpacingAfter(15f);
			
							float[] footer3ColumnWidths = {6f, 2f};
							footerTable3.setWidths(footer3ColumnWidths);
							document.add(footerTable3);
			
			
			
							PdfPTable footerTable4 = new PdfPTable(2);
							footerTable4.getDefaultCell().setBorder(0);
							footerTable4.addCell(emptyCell);
			
							PdfPCell coeCell = new PdfPCell(new Paragraph("CONTROLLER OF EXAMINATIONS", font4));
							coeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							coeCell.setBorder(0);
							coeCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
							coeCell.setFixedHeight(15);
							footerTable4.addCell(coeCell);
							footerTable4.completeRow();
			
							footerTable4.setWidthPercentage(100);
							//footerTable4.setSpacingBefore(5f);
							footerTable4.setSpacingAfter(0f);
			
							float[] footer4ColumnWidths = {3f, 3f};
							footerTable4.setWidths(footer4ColumnWidths);
			
							document.add(footerTable4);
			
			
							/*document.add(new Paragraph("1)  ANS :  Assignment Not Submitted.", font4));
							document.add(new Paragraph("2)  NA   :  Not Eligible due to non submission of assignment.", font4));
							document.add(new Paragraph("3)  *       :  Failures.", font4));
							document.add(new Paragraph("4)  **      :  Result on Hold due to Non Submission of Assignment.", font4));
							document.add(new Paragraph("5)  #       :  Marks brought forward.", font4));
							document.add(new Paragraph("6)  ~       :  Grace Marks given.", font4));
							document.add(new Paragraph(" ", font4));
							document.add(new Paragraph("Note:", font4));
							document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 50", font4));
							document.add(new Paragraph("2)  Submission of assignment is compulsory to be declared as pass in a subject in addition "
									+ "to \nappearance at the Term-End Examination.", font4));
			*/
							
							if("MPDV".equalsIgnoreCase(bean.getProgram())){
								document.add(new Paragraph("1)  *       :  Failures.", font4));
								//document.add(new Paragraph("7)  NV      :  Null And Void", font4));
								document.add(new Paragraph(" ", font4));
								document.add(new Paragraph("Note:", font8));
								document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 50", font4));
							}
							if("EPBM".equalsIgnoreCase(bean.getProgram())){
								document.add(new Paragraph(" ", font4));
													PdfPTable legendTable = new PdfPTable(2);
													legendTable.getDefaultCell().setBorder(0);
													PdfPCell titleCell1 = new PdfPCell(new Paragraph("1)  *", font4));
																		titleCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
																		titleCell1.setBorder(0);
																		titleCell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		titleCell1.setFixedHeight(15);
																		legendTable.addCell(titleCell1);
																		
																		PdfPCell descriptionCell1 = new PdfPCell(new Paragraph(": Failures.", font4));
																		descriptionCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
																		descriptionCell1.setBorder(0);
																		descriptionCell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		descriptionCell1.setFixedHeight(15);
																		legendTable.addCell(descriptionCell1);
																		
																	legendTable.completeRow();
													
																		
																		
																		PdfPCell titleCell2 = new PdfPCell(new Paragraph("2)  Case Study", font4));
																		titleCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
																		titleCell2.setBorder(0);
																		titleCell2.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		titleCell2.setFixedHeight(15);
																		legendTable.addCell(titleCell2);
																	
																	PdfPCell descriptionCell2 = new PdfPCell(new Paragraph(": The case study grading is as per below criteria.", font4));
																		descriptionCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
																		descriptionCell2.setBorder(0);
																		descriptionCell2.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		descriptionCell2.setFixedHeight(15);
																		legendTable.addCell(descriptionCell2);
																		
																		legendTable.completeRow();
																		
																		
																		
																		PdfPCell titleCell3 = new PdfPCell(new Paragraph("      Grade ", font4));
																		titleCell3.setHorizontalAlignment(Element.ALIGN_LEFT);
																		titleCell3.setBorder(0);
																		titleCell3.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		titleCell3.setFixedHeight(15);
																		legendTable.addCell(titleCell3);
																		
																		PdfPCell descriptionCell3 = new PdfPCell(new Paragraph("   Marks ", font4));
																		descriptionCell3.setHorizontalAlignment(Element.ALIGN_LEFT);
																		descriptionCell3.setBorder(0);
																		descriptionCell3.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		descriptionCell3.setFixedHeight(15);
																		legendTable.addCell(descriptionCell3);
																		
																		legendTable.completeRow();
																		
																		
																		PdfPCell titleCell4 = new PdfPCell(new Paragraph("        A ", font4));
																		titleCell4.setHorizontalAlignment(Element.ALIGN_LEFT);
																		titleCell4.setBorder(0);
																		titleCell4.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		titleCell4.setFixedHeight(15);
																		legendTable.addCell(titleCell4);
																		
																		PdfPCell descriptionCell4 = new PdfPCell(new Paragraph("   90 and above ", font4));
																		descriptionCell4.setHorizontalAlignment(Element.ALIGN_LEFT);
																		descriptionCell4.setBorder(0);
																		descriptionCell4.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		descriptionCell4.setFixedHeight(15);
																		legendTable.addCell(descriptionCell4);
																		
																		legendTable.completeRow();
																		
																		PdfPCell titleCell5 = new PdfPCell(new Paragraph("        B ", font4));
																		titleCell5.setHorizontalAlignment(Element.ALIGN_LEFT);
																		titleCell5.setBorder(0);
																		titleCell5.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		titleCell5.setFixedHeight(15);
																		legendTable.addCell(titleCell5);
																		
																		PdfPCell descriptionCell5 = new PdfPCell(new Paragraph("   75-89 ", font4));
																		descriptionCell5.setHorizontalAlignment(Element.ALIGN_LEFT);
																		descriptionCell5.setBorder(0);
																		descriptionCell5.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		descriptionCell5.setFixedHeight(15);
																		legendTable.addCell(descriptionCell5);
																		
																		legendTable.completeRow();
																		
																	PdfPCell titleCell6 = new PdfPCell(new Paragraph("        C ", font4));
																		titleCell6.setHorizontalAlignment(Element.ALIGN_LEFT);
																	titleCell6.setBorder(0);
																		titleCell6.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		titleCell6.setFixedHeight(15);
																		legendTable.addCell(titleCell6);
																		
																		PdfPCell descriptionCell7 = new PdfPCell(new Paragraph("   50-74 ", font4));
																		descriptionCell7.setHorizontalAlignment(Element.ALIGN_LEFT);
																		descriptionCell7.setBorder(0);
																		descriptionCell7.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		descriptionCell7.setFixedHeight(15);
																		legendTable.addCell(descriptionCell7);
																		
																		legendTable.completeRow();
																		
																		PdfPCell titleCell8 = new PdfPCell(new Paragraph("        D ", font4));
																		titleCell8.setHorizontalAlignment(Element.ALIGN_LEFT);
																		titleCell8.setBorder(0);
																		titleCell8.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		titleCell8.setFixedHeight(15);
																		legendTable.addCell(titleCell8);
																		
																		PdfPCell descriptionCell8 = new PdfPCell(new Paragraph("   Below 50 ", font4));
																		descriptionCell8.setHorizontalAlignment(Element.ALIGN_LEFT);
																		descriptionCell8.setBorder(0);
																		descriptionCell8.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		descriptionCell8.setFixedHeight(15);
																		legendTable.addCell(descriptionCell8);
																		
																		legendTable.completeRow();
																		
																		PdfPCell titleCell9 = new PdfPCell(new Paragraph("        E ", font4));
																		titleCell9.setHorizontalAlignment(Element.ALIGN_LEFT);
																		titleCell9.setBorder(0);
																		titleCell9.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		titleCell9.setFixedHeight(15);
																		legendTable.addCell(titleCell9);
																		
																		PdfPCell descriptionCell9 = new PdfPCell(new Paragraph("   Not Submitted, Blank Submission, Submitted Question Paper ", font4));
																		descriptionCell9.setHorizontalAlignment(Element.ALIGN_LEFT);
																		descriptionCell9.setBorder(0);
																		descriptionCell9.setVerticalAlignment(Element.ALIGN_BOTTOM);
																		descriptionCell9.setFixedHeight(15);
																		legendTable.addCell(descriptionCell9);
																		
																		legendTable.completeRow();
																		
													
																		legendTable.setWidthPercentage(100);
																		//legendTable.setSpacingBefore(5f);
																		legendTable.setSpacingAfter(0f);
																		
																		float[] legendTableColumnWidths = {0.5f, 3f};
																		legendTable.setWidths(legendTableColumnWidths);
													
																		
																		document.add(legendTable);

													/*document.add(new Paragraph("1)  *       :  Failures.", font4));
													document.add(new Paragraph("2)  Case Study    :  The case study grading is as per below criteria.", font4));
													document.add(new Paragraph(" Grade Marks   ", font4));
													document.add(new Paragraph(" A    90 and above.", font4));
												    document.add(new Paragraph(" B    75-89.", font4));
												    document.add(new Paragraph(" C    50-74.", font4));
													document.add(new Paragraph(" D    Below 50.", font4));
													document.add(new Paragraph(" E    Not Submitted, Blank Submission, Submitted Question Paper.", font4));*/
													document.add(new Paragraph(" ", font4));
													document.add(new Paragraph("Note:", font8));
														document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 50", font4));
														
												}
							document.newPage();
			
						} catch (DocumentException e) {
							
						} catch (Exception e) {
							
						}
			
			
					}
					document.close(); // no need to close PDFwriter?
			
				}

	private String getShortMonthName(String examMonth) {
		switch(examMonth) {
			case "January" : return "Jan";
			case "February" : return "Feb";
			case "March" : return "Mar";
			case "April" : return "Apr";
			case "May" : return "May";
			case "June" : return "Jun";
			case "July" : return "Jul";
			case "August" : return "Aug";
			case "September" : return "Sep";
			case "October" : return "Oct";
			case "November" : return "Nov";
			case "December" : return "Dec";
			default : return null;
		}
	}
//	
	public EmbaMarksheetBean mCreateStudentSelfMarksheetPDFforMbaWX(List<EmbaMarksheetBean> marksheetList,EmbaMarksheetBean msBean, 
			 String MARKSHEETS_PATH,List<EmbaPassFailBean> passFailDataList,List<EmbaPassFailBean> passFailDataListAllSem, 
			 String consumerProgramStructureId, Map<String, ProgramExamBean> modProgramMap) throws Exception {
		// TODO Auto-generated method stub		
		
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		String markSheetDate = sdfr.format(new Date());
		String fileName = null;
		//Image signature = Image.getInstance(new URL("https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/signature.jpg"));
		//Image signature = Image.getInstance("d:/signature.jpg");
		//signature.scaleAbsolute(60, 42);
		String folderPath = "";

		String exam_year = null;
		String exam_month = null;
		String acad_year = null;
		String acad_month = null; 
		if(marksheetList.size() > 0){
			EmbaMarksheetBean bean = marksheetList.get(0);
			String studentId = bean.getSapid();
		
			exam_year = bean.getExamYear();
			exam_month = bean.getExamMonth();
			acad_year = bean.getYear();
			acad_month = bean.getMonth(); 
			fileName = studentId + "_" + exam_month + "_" + exam_year + "_" 
			// .replace added by Ashutosh. This is because program MBA - WX has space in program name
			+ bean.getProgram().replace(" ", "_") 
			+ "_Term" +   bean.getSem()  + "_"  + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
			fileName = fileName.toUpperCase();
			
			
			folderPath = MARKSHEETS_PATH + exam_month + "-" + exam_year + "/";
			
			File folder = new File(folderPath);
			if (!folder.exists()) {   
				folder.mkdirs();   
			}  
		}
		Document document = new Document(PageSize.A4);
		document.setMargins(60, 60, 60, 60);

		
		
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath+fileName));
//		request.getSession().setAttribute("fileName", folderPath+fileName);
		msBean.setFileName(folderPath+fileName);
		document.open();

		
		Phrase watermark = new Phrase("Marksheet Downloaded from NGASCE Student Portal", new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
		PdfContentByte canvas = writer.getDirectContent();
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 420, 30);
		
		generateHeaderTable(document);		
		
		for (int i = 0; i < marksheetList.size(); i++) {

			EmbaMarksheetBean bean = marksheetList.get(i);
		
			try {

				
//				Chunk collegeNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", headerFont);	
//				
//				Paragraph collegeNameChunkPara = new Paragraph();
//				collegeNameChunkPara.add(collegeNameChunk);
//				collegeNameChunkPara.setAlignment(Element.ALIGN_CENTER);
//				collegeNameChunkPara.setSpacingAfter(8);
//				document.add(collegeNameChunkPara);
				
				
				Chunk stmtOfMarksChunk = new Chunk("STATEMENT OF GRADES", headerFont);		
				Paragraph stmtOfMarksPara = new Paragraph();				
				stmtOfMarksPara.add(stmtOfMarksChunk);
				stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
				stmtOfMarksPara.setSpacingAfter(8);			
				
				document.add(stmtOfMarksPara);

				PdfPTable headerTable = new PdfPTable(2); // 2 columns.
				headerTable.getDefaultCell().setBorder(0);
				
				String fatherName = "";
				String motherName = ""; 
				
				if(!StringUtils.isBlank(bean.getFatherName())) {
								
					if(bean.getFatherName().equals("null")) {
						fatherName = "";
					}else {
						fatherName = bean.getFatherName().trim().toUpperCase();
					}
								
				}
				
				if(!StringUtils.isBlank(bean.getMotherName())) {
					
					if(bean.getMotherName().equals("null")) {
						motherName = "";
					}else {
						motherName = bean.getMotherName().trim().toUpperCase();
					}
					
				}
				
				Chunk nameHeadChunk = new Chunk("NAME: ", descFont);	
				Chunk nameChunk = new Chunk((bean.getFirstName().trim().toUpperCase()+" "+bean.getLastName().trim()).toUpperCase().replace(".", " "), descBoldFont);
				Paragraph namePara = new Paragraph();				
				namePara.add(nameHeadChunk);
				namePara.add(nameChunk);
				
				Chunk rollNoHeadChunk = new Chunk("ROLL NO.: ", descFont);	
				Chunk rollNoChunk = new Chunk((bean.getSapid()).toUpperCase(), descBoldFont);
				Paragraph rollNoPara = new Paragraph();				
				rollNoPara.add(rollNoHeadChunk);
				rollNoPara.add(rollNoChunk);				
				
				PdfPCell rollNoCell = new PdfPCell(rollNoPara);
				rollNoCell.setBorder(0);
				rollNoCell.setPaddingRight(5);
				rollNoCell.setPaddingLeft(5);			
				
			
				PdfPCell nameCell = new PdfPCell(namePara);
				nameCell.setBorder(0);
				nameCell.setPaddingRight(5);
				nameCell.setPaddingLeft(5);
				headerTable.addCell(nameCell);				
				headerTable.addCell(rollNoCell);
				headerTable.completeRow();
								
				Chunk fatherNameHeadChunk = new Chunk("FATHER'S NAME: ", descFont);	
				Chunk fatherNameChunk = new Chunk((fatherName).toUpperCase(), descBoldFont);
				Paragraph fatherNamePara = new Paragraph();				
				fatherNamePara.add(fatherNameHeadChunk);
				fatherNamePara.add(fatherNameChunk);
				
				PdfPCell fatherNameCell = new PdfPCell(fatherNamePara);
				fatherNameCell.setBorder(0);
				fatherNameCell.setPaddingRight(5);
				fatherNameCell.setPaddingLeft(5);
				headerTable.addCell(fatherNameCell);
				headerTable.completeRow();
				
				Chunk motherNameHeadChunk = new Chunk("MOTHER'S NAME: ", descFont);	
				Chunk motherNameChunk = new Chunk((motherName).toUpperCase(), descBoldFont);
				Paragraph motherNamePara = new Paragraph();				
				motherNamePara.add(motherNameHeadChunk);
				motherNamePara.add(motherNameChunk);					
				
				PdfPCell motherNameCell = new PdfPCell(motherNamePara);
				motherNameCell.setBorder(0);
				motherNameCell.setPaddingBottom(5);
				motherNameCell.setPaddingRight(5);
				motherNameCell.setPaddingLeft(5);
				headerTable.addCell(motherNameCell);
				headerTable.completeRow();
				
				headerTable.setWidthPercentage(100);
				headerTable.setSpacingBefore(10f);
				headerTable.setSpacingAfter(5f);
				float[] columnWidths = {3.2f, 1.3f};
				headerTable.setWidths(columnWidths);
				document.add(headerTable);
				

				PdfPTable programTable = new PdfPTable(1);
				
				Chunk programHeadChunk = new Chunk("PROGRAMME: ", descFont);
				Chunk programChunk = new Chunk(bean.getProgramname().toUpperCase(), descBoldFont);
				if(bean.getProgram().equals("MBA - WX")) {
					programChunk = new Chunk(("MASTER OF BUSINESS ADMINISTRATION (WORKING EXECUTIVES)").toUpperCase(), descBoldFont);
				}else if("M.Sc. (AI & ML Ops)".equals(bean.getProgram())) {
					programChunk = new Chunk(("MASTER OF SCIENCE IN ARTIFICIAL INTELLIGENCE AND MACHINE LEARNING OPS").toUpperCase(), descBoldFont);
				} else if("MBA - X".equals(bean.getProgram())){
					programChunk = new Chunk(("MBA (EXECUTIVE) WITH SPECIALISATION IN BUSINESS ANALYTICS").toUpperCase(), descBoldFont);				
				}
				

				Paragraph programPara = new Paragraph();				
				programPara.add(programHeadChunk);
				programPara.add(programChunk);		
				
				PdfPCell programCell = new PdfPCell(programPara);	

				programCell.setBorder(0);
				programCell.setPaddingBottom(5);
				programCell.setPaddingRight(5);
               programCell.setPaddingLeft(5);
               
				programTable.addCell(programCell);
				programTable.completeRow();				
				programTable.setWidthPercentage(100);
				programTable.setSpacingAfter(5f);
				float[] programcolumnWidths = {5f};
				programTable.setWidths(programcolumnWidths);
				document.add(programTable);
				
				//Added Mode Of Delivery - START
				ProgramExamBean programExamBean = modProgramMap.get(bean.getProgram());
				String mod = programExamBean.getModeOfLearning();
				PdfPTable modTable = new PdfPTable(2); // 2 columns.
				modTable.getDefaultCell().setBorder(0);
				Chunk modHeadChunk = new Chunk("MODE OF DELIVERY: ", descFont);	
				Chunk modChunk = new Chunk((mod), descBoldFont);
				Paragraph modPara = new Paragraph();				
				modPara.add(modHeadChunk);
				modPara.add(modChunk);
				PdfPCell modCell = new PdfPCell(modPara);
				modCell.setBorder(0);
				modCell.setPaddingRight(5);
				modCell.setPaddingLeft(5);
				modTable.addCell(modCell);
				modTable.completeRow();
				modTable.setWidthPercentage(100);
				modTable.setSpacingAfter(5f);
				float[] modcolumnWidths = {3.2f, 1.3f};
				modTable.setWidths(modcolumnWidths);
				document.add(modTable);
				//Added Mode Of Delivery - END
				
				PdfPTable termTable = new PdfPTable(2);
				
				Chunk termDurationHeadChunk = new Chunk("TERM DURATION: ", descFont);	
				Chunk termDurationChunk = new Chunk((bean.getEnrollmentMonth()
						+" "+bean.getEnrollmentYear()+"-"+exam_month+" "+exam_year).toUpperCase(), descBoldFont);
				Paragraph termDurationPara = new Paragraph();				
				termDurationPara.add(termDurationHeadChunk);
				termDurationPara.add(termDurationChunk);	
				
				PdfPCell rgstnCell = new PdfPCell(new Paragraph(termDurationPara));		
				
				
				

				Chunk termHeadChunk = new Chunk("TERM: ", descFont);
				Chunk termChunk = new Chunk("");
				if("1".equalsIgnoreCase(msBean.getSem())) {
					termChunk = new Chunk("I", descBoldFont);

				}

				if("2".equalsIgnoreCase(msBean.getSem())) {
					termChunk = new Chunk("II", descBoldFont);

				}	
				
				if("3".equalsIgnoreCase(msBean.getSem())) {
					termChunk = new Chunk("III", descBoldFont);

				}
				
				if("4".equalsIgnoreCase(msBean.getSem())) {
					termChunk = new Chunk("IV", descBoldFont);

				}
				
				if("5".equalsIgnoreCase(msBean.getSem())) {
					termChunk = new Chunk("V", descBoldFont);

				}
				
				if("6".equalsIgnoreCase(msBean.getSem())) {
					termChunk = new Chunk("VI", descBoldFont);

				}
				
				if("7".equalsIgnoreCase(msBean.getSem())) {
					termChunk = new Chunk("VII", descBoldFont);

				}
				
				if("8".equalsIgnoreCase(msBean.getSem())) {
					termChunk = new Chunk("VIII", descBoldFont);

				}
				Paragraph termPara = new Paragraph();				
				termPara.add(termHeadChunk);
				termPara.add(termChunk);	
				
				PdfPCell semCell = new PdfPCell(termPara);
				rgstnCell.setBorder(0);
				semCell.setBorder(0);
				semCell.setPaddingLeft(5);
				rgstnCell.setPaddingLeft(5);
				termTable.addCell(rgstnCell);
				termTable.addCell(semCell);
				termTable.completeRow();			
				
				termTable.setWidthPercentage(100);
				//termTable.setSpacingBefore(10f);
				termTable.setSpacingAfter(12f);
				
				float[] termColumnWidths = {3.2f, 1.3f};
				termTable.setWidths(termColumnWidths);
				document.add(termTable);
				
				PdfContentByte canvasLine = writer.getDirectContent();			// for hr(line)		
			
				canvasLine.moveTo(50,573);		
				canvasLine.lineTo(550,573);           
				canvasLine.closePathStroke();	
		        
				PdfPTable marksTable = new PdfPTable(4);				

				PdfPCell srNoCell = new PdfPCell(new Paragraph("SR. NO.", descBoldFont));
				PdfPCell subjectsCell = new PdfPCell(new Paragraph("COURSE/S", descBoldFont));			
				PdfPCell creditsCell = new PdfPCell(new Paragraph("CREDIT/S", descBoldFont));	
				PdfPCell gradesCell = new PdfPCell(new Paragraph("GRADE/S", descBoldFont));

				srNoCell.setFixedHeight(30);
				srNoCell.setVerticalAlignment(Element.ALIGN_TOP);				
				subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
				creditsCell.setVerticalAlignment(Element.ALIGN_TOP);
				gradesCell.setVerticalAlignment(Element.ALIGN_TOP);

				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				subjectsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				creditsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				gradesCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				
				srNoCell.setBorder(0);
				subjectsCell.setBorder(0);
				subjectsCell.setBorder(0);
				creditsCell.setBorder(0);
				gradesCell.setBorder(0);
				
				srNoCell.setFixedHeight(15);
				subjectsCell.setFixedHeight(15);
				creditsCell.setFixedHeight(15);
				gradesCell.setFixedHeight(15);
				
				marksTable.addCell(srNoCell);
				marksTable.addCell(subjectsCell);
				marksTable.addCell(creditsCell);
				marksTable.addCell(gradesCell);
				marksTable.completeRow();
				
				int count = 1;
				double gradePoint = 0.0;
				double gradeCalculation = 0.0;
				double total = 0.0;
				int passCount = 0;
				double sumOftotalCredits = 0.0;
				
				for (EmbaPassFailBean e : passFailDataList) {		
					gradePoint = Float.parseFloat(e.getPoints());
//					gradeCalculation = gradePoint * 4;
//					total = total + gradeCalculation;			
					srNoCell = new PdfPCell(new Paragraph(count+"", descFont));
					if(exam_year.equalsIgnoreCase(e.getExamYear()) && getShortMonthName(exam_month).equalsIgnoreCase(e.getExamMonth()) )
					{
						subjectsCell = new PdfPCell(new Paragraph(e.getSubject().toUpperCase(), descFont));	
						gradesCell = new PdfPCell(new Paragraph(e.getGrade().toUpperCase(), descFont));
					}else {
						subjectsCell = new PdfPCell(new Paragraph(e.getSubject().toUpperCase(), descFont));	
						gradesCell = new PdfPCell(new Paragraph(e.getGrade().toUpperCase()  + " #", descFont));
					}
					
					if(e.getPrgm_sem_subj_id() == 1883) { // Added by Abhay for MBA - WX Subject
						creditsCell = new PdfPCell(new Paragraph("20.00", descFont));
						sumOftotalCredits = sumOftotalCredits + 20;
						gradeCalculation = gradePoint * 20;
					}else if(e.getPrgm_sem_subj_id() == 2351) { // Added by Abhay for New Program Structure of MBA - WX Capstone Project
						creditsCell = new PdfPCell(new Paragraph("12.00", descFont));
						sumOftotalCredits = sumOftotalCredits + 12;
						gradeCalculation = gradePoint * 12;
					}
					/*Commented by Siddheshwar_Khanse as subject credits getting dynamically 
					  else if(e.getPrgm_sem_subj_id() == 1959) { // Added by Abhay for M.Sc. (AI & ML Ops) Subject
						creditsCell = new PdfPCell(new Paragraph("5.00", descFont));
						sumOftotalCredits = sumOftotalCredits + 5;
						gradeCalculation = gradePoint * 5;
					}else if(e.getPrgm_sem_subj_id() == 1961) { // Added by Abhay for M.Sc. (AI & ML Ops) Subject
						creditsCell = new PdfPCell(new Paragraph("3.00", descFont));
						sumOftotalCredits = sumOftotalCredits + 3;
						gradeCalculation = gradePoint * 3;
					}*/
					else if("131".equals(consumerProgramStructureId)) {
						double subjectCredits = SubjectsCerditsConfiguration.getMSCAIOpsSubjectCredits(e.getSubject());
						creditsCell = new PdfPCell(new Paragraph(""+subjectCredits, descFont));
						sumOftotalCredits = sumOftotalCredits + subjectCredits;
						gradeCalculation = gradePoint * subjectCredits;
					}
					else if("154".equals(consumerProgramStructureId) || "155".equals(consumerProgramStructureId) || "158".equals(consumerProgramStructureId)) {
						double subjectCredits = SubjectsCerditsConfiguration.getMSCAISubjectCredits(e.getSubject());
						creditsCell = new PdfPCell(new Paragraph(""+subjectCredits, descFont));
						sumOftotalCredits = sumOftotalCredits + subjectCredits;
						gradeCalculation = gradePoint * subjectCredits;
					}
					else if("160".equals(consumerProgramStructureId)) {
						double subjectCredits = SubjectsCerditsConfiguration.getMBAWXSubjectCredits(e.getPrgm_sem_subj_id());
						creditsCell = new PdfPCell(new Paragraph(""+subjectCredits, descFont));
						sumOftotalCredits = sumOftotalCredits + subjectCredits;
						gradeCalculation = gradePoint * subjectCredits;
					}
					else {
						creditsCell = new PdfPCell(new Paragraph("4.00", descFont));
						sumOftotalCredits = sumOftotalCredits + 4;
						gradeCalculation = gradePoint * 4;
					}
					
					total = total + gradeCalculation;
					
					srNoCell.setBorder(0);
					subjectsCell.setBorder(0);
					gradesCell.setBorder(0);
					creditsCell.setBorder(0);
	 
					subjectsCell.setPaddingLeft(5);
				
//					srNoCell.setFixedHeight(15);
//					subjectsCell.setFixedHeight(15);
//					gradesCell.setFixedHeight(15);
//					creditsCell.setFixedHeight(15);
					
					
					srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					creditsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					gradesCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					
					srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
					creditsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					gradesCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	
					
					marksTable.addCell(srNoCell);
					marksTable.addCell(subjectsCell);
					marksTable.addCell(creditsCell);
					marksTable.addCell(gradesCell);
					marksTable.completeRow();
					if(e.getIsPass().equals("Y")) {
						passCount++;
					}
					
					count++;
				}	
				
				marksTable.setWidthPercentage(100);
				marksTable.setSpacingBefore(15f);
				marksTable.setSpacingAfter(30f);
				float[] marksColumnWidths = {2.5f, 12f, 3.5f, 3.5f};
				marksTable.setWidths(marksColumnWidths);
				document.add(marksTable);

				
				int sum = passFailDataList.size();		
//				int sumOftotalCredits = sum * 4;
				
				double gpa = total/sumOftotalCredits;
				
				
				//float cgpa = calulateCGPA();
				PdfPTable footerTable1 = new PdfPTable(2);
				footerTable1.getDefaultCell().setBorder(0);				
				
			//	PdfPCell attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: "+bean.getValidityEndMonth()+"-"+bean.getValidityEndYear()+ " Examination.",font1));
				
				Chunk gradeHeadChunk = new Chunk("GRADE POINT AVERAGE (GPA): ", descFont);	
				Chunk gradeChunk = new Chunk(String.format ("%,.2f", gpa)+"", descBoldFont);
				Paragraph gradePara = new Paragraph();				
				gradePara.add(gradeHeadChunk);
				gradePara.add(gradeChunk);	
				
				PdfPCell attemptsCell = new PdfPCell(gradePara);				
				attemptsCell.setBorder(0);
				attemptsCell.setColspan(2);
				attemptsCell.setPaddingLeft(22);
				footerTable1.addCell(attemptsCell);
				footerTable1.completeRow();
				
				Chunk cgpaHeadChunk = new Chunk("CUMULATIVE GRADE POINT AVERAGE (CGPA): ", descFont);	
				
				Chunk cgpaChunk = new Chunk();
				Paragraph cgpaPara = new Paragraph();				

				if("1".equalsIgnoreCase(msBean.getSem())) {
					cgpaChunk = new Chunk(String.format ("%,.2f", gpa)+"", descBoldFont);
					cgpaPara.add(cgpaHeadChunk);
					cgpaPara.add(cgpaChunk);	
				} else {
				
				double cgradeCalculation = 0.0;
				double ctotal = 0.0;
				double cgradePoint = 0.0;
//				int csum = passFailDataListAllSem.size();		
				double csumOftotalCredits = 0.0;
				 
//				if("5".equalsIgnoreCase(msBean.getSem())) {
//					 csumOftotalCredits = csumOftotalCredits + 16;
//					}
				for (EmbaPassFailBean e : passFailDataListAllSem) {		
					cgradePoint = Float.parseFloat(e.getPoints());
					
//					Start Commented by Abhay
					/*
					 * if("Capstone Project".equalsIgnoreCase(e.getSubject())) { cgradeCalculation =
					 * cgradePoint * 20; } else { cgradeCalculation = cgradePoint * 4; }
					 */
//					End Commented by Abhay
					
					 if(e.getPrgm_sem_subj_id() == 1883) { // Added by Abhay for MBA WX Subject
						    csumOftotalCredits = csumOftotalCredits + 20;
							cgradeCalculation = cgradePoint * 20;
						}else if(e.getPrgm_sem_subj_id() == 2351) { // Added by Abhay for New Program Structure of MBA - WX Capstone Project
							csumOftotalCredits = csumOftotalCredits + 12;
							cgradeCalculation = cgradePoint * 12;
						}
						/*Commented by Siddheshwar_Khanse as subject credits getting dynamically 
						 else if(e.getPrgm_sem_subj_id() == 1959) { // Added by Abhay for M.Sc. (AI & ML Ops) Subject
							csumOftotalCredits = csumOftotalCredits + 5;
							cgradeCalculation = cgradePoint * 5;
						}else if(e.getPrgm_sem_subj_id() == 1961) { // Added by Abhay for M.Sc. (AI & ML Ops) Subject
							csumOftotalCredits = csumOftotalCredits + 3;
							cgradeCalculation = cgradePoint * 3;
						}*/
						else if("131".equals(consumerProgramStructureId)) {
							double subjectCredits = SubjectsCerditsConfiguration.getMSCAIOpsSubjectCredits(e.getSubject());
							csumOftotalCredits = csumOftotalCredits + subjectCredits;
							cgradeCalculation = cgradePoint * subjectCredits;
						}
						else if("154".equals(consumerProgramStructureId) || "155".equals(consumerProgramStructureId) || "158".equals(consumerProgramStructureId)) {
							double subjectCredits = SubjectsCerditsConfiguration.getMSCAISubjectCredits(e.getSubject());
							csumOftotalCredits = csumOftotalCredits + subjectCredits;
							cgradeCalculation = cgradePoint * subjectCredits;
						}
						else if("160".equals(consumerProgramStructureId)) {
							double subjectCredits = SubjectsCerditsConfiguration.getMBAWXSubjectCredits(e.getPrgm_sem_subj_id());
							csumOftotalCredits = csumOftotalCredits + subjectCredits;
							cgradeCalculation = cgradePoint * subjectCredits;
						}
						else {
							csumOftotalCredits = csumOftotalCredits + 4;
							cgradeCalculation = cgradePoint * 4;
						}
					
					ctotal = ctotal + cgradeCalculation;			
//					if(e.getIsPass().equals("Y")) {
//						passCount++;
//					}
//				 	
//					count++;
				}	

				double cgpa = ctotal/csumOftotalCredits;
		
				cgpaChunk = new Chunk(String.format ("%,.2f", cgpa)+"", descBoldFont);
				cgpaPara.add(cgpaHeadChunk);
				cgpaPara.add(cgpaChunk);	
		
				}

			
		       PdfPCell attemptsCell2 = new PdfPCell(cgpaPara);				
				attemptsCell2.setBorder(0);
				attemptsCell2.setColspan(2);
				attemptsCell2.setPaddingLeft(22);
				footerTable1.addCell(attemptsCell2);
				footerTable1.completeRow();
				
				footerTable1.setWidthPercentage(100);
				footerTable1.setSpacingBefore(25f);
				footerTable1.setSpacingAfter(5f);
				float[] footer1ColumnWidths = {3f, 3f};
				footerTable1.setWidths(footer1ColumnWidths);
				document.add(footerTable1);

				
				String remark = "";
				if(gpa >= 3.5 && sum == passCount) {
					remark = "PASS WITH DISTINCTION";
				}
				
				if(gpa < 3.5 && sum == passCount) {
					remark = "PASS";
				}
				
				if(sum != passCount){
					remark = "FAIL";
				}
				Chunk remarkHeadChunk = new Chunk("REMARK: ", descFont);	
				Chunk remarkChunk = new Chunk(remark, descBoldFont);
				Paragraph remarkPara = new Paragraph();				
				remarkPara.add(remarkHeadChunk);
				remarkPara.add(remarkChunk);	
				
				PdfPTable remarkFooterTable = new PdfPTable(2);
				remarkFooterTable.getDefaultCell().setBorder(0);
				
				PdfPCell remarkCell = new PdfPCell(remarkPara);				
				remarkCell.setBorder(0);
				remarkCell.setColspan(2);
				remarkCell.setPaddingLeft(22);
				remarkFooterTable.addCell(remarkCell);
				remarkFooterTable.completeRow();			    
		    
				
				remarkFooterTable.setWidthPercentage(100);
				remarkFooterTable.setSpacingBefore(25f);
				remarkFooterTable.setSpacingAfter(5f);
				document.add(remarkFooterTable);
			
				PdfPTable footerTable3 = new PdfPTable(2);
				footerTable3.getDefaultCell().setBorder(0);
				PdfPCell emptyCell = new PdfPCell();
				emptyCell.setBorder(0);
				emptyCell.setPaddingLeft(22);

				footerTable3.addCell(emptyCell);

				PdfPCell signatureCell = new PdfPCell();
				//signatureCell.addElement(signature);
				signatureCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
				//signature.setAlignment(Element.ALIGN_RIGHT);
				signatureCell.setBorder(0);
				signatureCell.setVerticalAlignment(Element.ALIGN_BOTTOM);

				footerTable3.addCell(signatureCell);
				footerTable3.completeRow();

				footerTable3.setWidthPercentage(100);
				footerTable3.setSpacingBefore(15f);
				//footerTable3.setSpacingAfter(15f);

				float[] footer3ColumnWidths = {6f, 2f};
				footerTable3.setWidths(footer3ColumnWidths);
				document.add(footerTable3);



				PdfPTable footerTable4 = new PdfPTable(2);
				footerTable4.getDefaultCell().setBorder(0);
				footerTable4.addCell(emptyCell);
				footerTable4.addCell(emptyCell);


				Chunk issuedHeadChunk = new Chunk("GRADE SHEET ISSUED ON: ", descFont);	
				Chunk issuedChunk = new Chunk(markSheetDate, descBoldFont);
				Paragraph issuedPara = new Paragraph();				
				issuedPara.add(issuedHeadChunk);
				issuedPara.add(issuedChunk);	
				
				PdfPCell issuedCell = new PdfPCell(issuedPara);
				issuedCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				issuedCell.setBorder(0);
				issuedCell.setPaddingLeft(22);

			//	issuedCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				issuedCell.setFixedHeight(15);
				
				//PdfPCell coeCell = new PdfPCell(new Paragraph("CONTROLLER OF EXAMINATIONS", font4));
				PdfPCell coeCell = new PdfPCell();
				
				coeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				coeCell.setBorder(0);
				coeCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				coeCell.setFixedHeight(15);
				coeCell.setPaddingLeft(22);
				
				footerTable4.addCell(issuedCell);
				footerTable4.addCell(coeCell);
				footerTable4.completeRow();

				footerTable4.setWidthPercentage(100);
				footerTable4.setSpacingBefore(15f);
				footerTable4.setSpacingAfter(18f);

				float[] footer4ColumnWidths = {3f, 3f};
				footerTable4.setWidths(footer4ColumnWidths);

				document.add(footerTable4);
				
				

				PdfPTable footerTable5 = new PdfPTable(1);
				footerTable5.getDefaultCell().setBorder(0);
				
				footerTable5.setWidthPercentage(100);
				footerTable5.setSpacingBefore(10f);
				//footerTable3.setSpacingAfter(15f);
				
				
				PdfPCell carryforwardCell = new PdfPCell(new Paragraph(" \"#\" INDICATES CARRY FORWARD SUBJECTS AND GRADES", font6));		
				carryforwardCell.setBorder(0);
				carryforwardCell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				carryforwardCell.setPaddingLeft(10);
				footerTable5.addCell(carryforwardCell);
				PdfPCell descCell = new PdfPCell(new Paragraph(" \"-\" INDICATES THAT GPA/CGPA WILL BE CALCULATED AFTER CLEARANCE OF EXISTING F GRADES", font6));		
				descCell.setBorder(0);
				descCell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				descCell.setPaddingLeft(10);
				
				footerTable5.addCell(descCell);
				

				document.add(footerTable5);				
				
				PdfPTable footerTable6 = new PdfPTable(1);
				footerTable6.getDefaultCell().setBorder(0);
				
				footerTable6.setWidthPercentage(100);
				footerTable6.setSpacingBefore(5f);
				footerTable6.setSpacingAfter(10f);				
				
				PdfPCell desc2Cell = new PdfPCell(new Paragraph("GRADE POINTS: A+ : 4; A : 3.75; A- : 3.5; B+ :3.25; B: 3; B- : 2.75; C+ : 2.5; C : 2.25; C- : 2; F : 0",footerBoldFont));		
				desc2Cell.setBorder(0);
				desc2Cell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				desc2Cell.setPaddingLeft(10);
				
				footerTable6.addCell(desc2Cell);				
				
				document.add(footerTable6);
				document.add(new Paragraph("Note:", font4));
				document.add(new Paragraph("1)  This statement of grades is generated through Student Portal of NMIMS Global Access School For Continuing Education.", font4));
				document.add(new Paragraph("2)  To verify authenticity of this marksheet, please refer to original marksheet issued by University.\n", font4));				
				document.add(new Phrase("\n"));

//				PdfPTable footerTable7 = new PdfPTable(1);
//				footerTable7.getDefaultCell().setBorder(0);
//				
//				footerTable7.setWidthPercentage(100);
//				footerTable7.setSpacingBefore(5f);
//				footerTable7.setSpacingAfter(25f);
//				
//				PdfPCell desc3Cell = new PdfPCell(new Paragraph("PREPARED BY: ",footerBoldFont));		
//				desc3Cell.setBorder(0);
//				desc3Cell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
//				desc3Cell.setPaddingLeft(45);
//				
//				footerTable7.addCell(desc3Cell);				
//				
//				document.add(footerTable7);
//				
//				PdfPTable footerTable8 = new PdfPTable(1);
//				footerTable8.getDefaultCell().setBorder(0);
//				
//				footerTable8.setWidthPercentage(100);
//				footerTable8.setSpacingBefore(10f);
//				
//				PdfPCell desc4Cell = new PdfPCell(new Paragraph("CHECKED BY: ",footerBoldFont));		
//				desc4Cell.setBorder(0);
//				desc4Cell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
//				desc4Cell.setPaddingLeft(45);
//				
//				footerTable8.addCell(desc4Cell);				
//				
//				document.add(footerTable8);				
				
				document.newPage();

			} catch (DocumentException e) {
				
			} catch (Exception e) {
				
			}


		}
		document.close(); // no need to close PDFwriter?
		return msBean;

	}
	
	
	
//	
//	infoSheet(){
//		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
//		String markSheetDate = sdfr.format(new Date());
//		String fileName = null;
//		HashMap<String, CenterBean> centerNameCenterMap = generateCenterNameCenterMap(centersMap);//This will make another map with key as Name insteadof code/id
//		Image signature = Image.getInstance(new URL("https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/signature.jpg"));
//
//		//Image signature = Image.getInstance("d:/signature.jpg");
//				signature.scaleAbsolute(60, 42);
//				String folderPath = "";
//				
//				if(marksheetList.size() > 0){
//					EmbaMarksheetBean bean = marksheetList.get(0);
////					String studentId = bean.getSapid();	
//					String year = bean.getExamYear();
//					String month = bean.getExamMonth();
//					fileName = month + "_" + year + "_" + bean.getProgram() + "_Term" + "_1" + "_"  + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
//					fileName = fileName.toUpperCase();
//					
//					
//					folderPath = MARKSHEETS_PATH + month + "-" + year + "/";
//					
//					File folder = new File(folderPath);
//					if (!folder.exists()) {   
//						folder.mkdirs();   
//					}  
//				}
//	}
	
	public EmbaMarksheetBean generateNonGradedMarksheetPDF(List<EmbaMarksheetBean> marksheetList,
			HashMap<String, String> programMap, HashMap<String, CenterExamBean> centersMap, String MARKSHEETS_PATH,
			List<EmbaPassFailBean> passFailDataListAllSapidsAllSems, String logoRequired) throws Exception {
		
		logger.info("CreatePDF.generateNonGradedMarksheetPDF() - START");
		String markSheetDate = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
		String folderPath = "";
		String fileName = null;
		EmbaMarksheetBean embaMarksheetBean = new EmbaMarksheetBean();
		Image logo=null;
		
		HashMap<String, CenterExamBean> centerNameCenterMap = generateCenterNameCenterMap(centersMap);
		
		Image signature = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/signature.jpg"));
		signature.scaleAbsolute(60, 42);
		
		if(marksheetList.size() > 0) {
			EmbaMarksheetBean bean = marksheetList.get(0);
			Map<String,String> fileAndFolderDetails = this.getFileAndFolderDetails(bean.getExamYear(),bean.getExamMonth(),bean.getProgram(),MARKSHEETS_PATH);
			fileName = fileAndFolderDetails.get("FileName");
			folderPath = fileAndFolderDetails.get("FolderPath");
		}
		
		Document document = new Document(PageSize.A4);
		document.setMargins(50, 50, 40, 30);
		
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath+fileName));
		embaMarksheetBean.setFileName(folderPath+fileName);
		
		document.open();
		
		Paragraph blankLine = new Paragraph("\n");
		
		
		if(logoRequired!=null && logoRequired.equalsIgnoreCase("Y")) {
			logo = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nm_logo2.jpg")); 
			logo.scaleAbsolute(164,59);    
			logo.setIndentationLeft(-17);  
		}
		
		String learningCenter = "None"; 
		for (int i = 0; i < marksheetList.size(); i++) {
			
			if(logoRequired!=null && logoRequired.equalsIgnoreCase("Y")) {
				document.setMargins(50, 50, 20, 40);
			}  else {
				document.setMargins(50, 50, 100, 40);    
			}
			
			EmbaMarksheetBean bean = marksheetList.get(i);
			
			logger.info("Started Marksheet printing for sapid:"+bean.getSapid());
			String program = programMap.get(bean.getProgram().trim());
			String currentLearningCenter = bean.getLc();
			
			if(!learningCenter.equalsIgnoreCase(currentLearningCenter)){
				CenterExamBean centerBean = centersMap.get(bean.getCenterCode()) ;
				
				Paragraph centerNamePara = null;
				Paragraph centerAddress = null;
				
				if(centerBean != null){
					centerNamePara = new Paragraph("Learning Center: " + centerBean.getLc(), font1);
					String lcAddress = "";
					if(centerNameCenterMap.get(centerBean.getLc()) != null){
						lcAddress = centerNameCenterMap.get(centerBean.getLc()).getAddress();
					}
					
					centerAddress = new Paragraph("Center Address: " + lcAddress, font1);
				}else{
					centerNamePara = new Paragraph("Center Name: Not Available", font7);
					centerAddress = new Paragraph("Center Address: Not Available", font7);
				}
				
				document.add(centerNamePara);
				document.add(centerAddress);
				learningCenter = currentLearningCenter;
				document.newPage();
			}//if
			
			try {
				if(logoRequired!=null && logoRequired.equalsIgnoreCase("Y")) {
					document.add(logo);  
					document.add(blankLine);
				}
				
				Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font3);
				collegNameChunk.setUnderline(0.1f, -2f);
				Paragraph collegeName = new Paragraph();
				collegeName.add(collegNameChunk);
				collegeName.setAlignment(Element.ALIGN_CENTER);
				document.add(collegeName);


				Chunk stmtOfMarksChunk = new Chunk("STATEMENT OF MARKS", font2);
				stmtOfMarksChunk.setUnderline(0.1f, -2f);
				Paragraph stmtOfMarksPara = new Paragraph();
				stmtOfMarksPara.add(stmtOfMarksChunk);
				stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
				document.add(stmtOfMarksPara);
				
				PdfPTable headerTable = new PdfPTable(2); // 2 columns.
				headerTable.getDefaultCell().setBorder(0);
				
				String fatherName="";
				String motherName="";
				String middleName = bean.getMiddleName();
				
				if(!StringUtils.isBlank(bean.getFatherName())) {
					
					if(bean.getFatherName().equals("null")) {
						fatherName = "";
					}else {
						fatherName = bean.getFatherName().trim().toUpperCase();
					}
								
				}
				
				if(!StringUtils.isBlank(bean.getMotherName())) {
					
					if(bean.getMotherName().equals("null")) {
						motherName = "";
					}else {
						motherName = bean.getMotherName().trim().toUpperCase();
					}
					
				}
				
				if (!StringUtils.isBlank(middleName)) {

					if (middleName.equals("null")) {
						middleName = "";
					} else {
						middleName = bean.getMiddleName().trim().toUpperCase();
					}
				} else {
					middleName = fatherName;
				}
				
				PdfPCell nameCell = new PdfPCell(new Paragraph("NAME: "+(bean.getFirstName().trim().toUpperCase()+" "+bean.getLastName().trim()).toUpperCase().replace(".", " "),font1));

				nameCell.setBorder(0);
				nameCell.setColspan(2);
				//nameCell.setPaddingBottom(10);
				headerTable.addCell(nameCell);

				headerTable.completeRow();
				
				PdfPCell fatherNameCell = new PdfPCell(new Paragraph("Father's Name: "+(fatherName).toUpperCase(),font1));
				fatherNameCell.setBorder(0);
				fatherNameCell.setColspan(2);
				//fatherNameCell.setPaddingBottom(10);
				headerTable.addCell(fatherNameCell);
				headerTable.completeRow();
				
				PdfPCell motherNameCell = new PdfPCell(new Paragraph("Mother's Name: "+(motherName).toUpperCase(),font1));
				motherNameCell.setBorder(0);
				motherNameCell.setColspan(2);
				motherNameCell.setPaddingBottom(5);
				headerTable.addCell(motherNameCell);
				headerTable.completeRow();
				
				
				PdfPCell programCell = new PdfPCell(new Paragraph("Program: "+program,font1));
				PdfPCell studentNoCell = new PdfPCell(new Paragraph("Student No: "+bean.getSapid(),font1));
				studentNoCell.setBorder(0);

				programCell.setBorder(0);
				programCell.setPaddingBottom(10);
				headerTable.addCell(programCell);
				headerTable.addCell(studentNoCell);
				headerTable.completeRow();
				
				PdfPCell rgstnCell = new PdfPCell(new Paragraph("Month and Year of Registration: "+bean.getEnrollmentMonth()
				+"-"+bean.getEnrollmentYear(),font1));

				//Get equivalent Roman number to semester number.
				String romanSem = NumberUtility.getRomanNumber(Integer.parseInt(bean.getSem().trim()));
				
				logger.info("Strudent's sem in natural:"+bean.getSem().trim());
				logger.info("Strudent's sem in roman:"+romanSem);

				PdfPCell semCell = new PdfPCell(new Paragraph("Semester: "+romanSem,font1));
				rgstnCell.setBorder(0);
				semCell.setBorder(0);

				headerTable.addCell(rgstnCell);
				headerTable.addCell(semCell);
				headerTable.completeRow();

				PdfPCell examYearCell = new PdfPCell(new Paragraph("Month and Year of Examination: "+bean.getExamMonth()+"-"+bean.getExamYear(),font1));
				examYearCell.setBorder(0);
				headerTable.addCell(examYearCell);
				headerTable.completeRow();

				headerTable.setWidthPercentage(100);
				headerTable.setSpacingBefore(10f);
				headerTable.setSpacingAfter(5f);
				float[] columnWidths = {3.2f, 1.3f};
				headerTable.setWidths(columnWidths);
				document.add(headerTable);
				
				PdfPTable marksTable = new PdfPTable(5);

				PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font8));
				PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subjects", font8));
				String finalMarksTitle = "Final Exam Marks";
				if("PGDMM-MLI".equalsIgnoreCase(bean.getProgram())){
					finalMarksTitle = finalMarksTitle + " (50)";
				}else if("ACBM".equalsIgnoreCase(bean.getProgram())){
					finalMarksTitle = finalMarksTitle + " (60)";
				}else{
					finalMarksTitle = finalMarksTitle + " (70)";
				}
				PdfPCell finalExamMarksCell = new PdfPCell(new Paragraph(finalMarksTitle, font8));

				String assignmentMarksTitle = "Assignment Marks";
				if("PGDMM-MLI".equalsIgnoreCase(bean.getProgram())){
					assignmentMarksTitle = assignmentMarksTitle + " (50)";
				}else if("ACBM".equalsIgnoreCase(bean.getProgram())){
					assignmentMarksTitle = assignmentMarksTitle + " (40)";
				}else{
					assignmentMarksTitle = assignmentMarksTitle + " (30)";
				}
				PdfPCell assignmentMarksCell = new PdfPCell(new Paragraph(assignmentMarksTitle, font8));
				PdfPCell totalMarksCell = new PdfPCell(new Paragraph("Total Marks (100)", font8));

				srNoCell.setFixedHeight(30);
				srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
				subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
				finalExamMarksCell.setVerticalAlignment(Element.ALIGN_TOP);
				assignmentMarksCell.setVerticalAlignment(Element.ALIGN_TOP);
				totalMarksCell.setVerticalAlignment(Element.ALIGN_TOP);

				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				finalExamMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				assignmentMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				marksTable.addCell(srNoCell);
				marksTable.addCell(subjectsCell);
				marksTable.addCell(finalExamMarksCell);
				marksTable.addCell(assignmentMarksCell);
				marksTable.addCell(totalMarksCell);
				marksTable.completeRow();
				
				int count = 1;
				
				for (EmbaPassFailBean e : passFailDataListAllSapidsAllSems) {
					
					if(bean.getSapid().equals(e.getSapid()) && bean.getSem().equals(e.getSem())) {
						srNoCell = new PdfPCell(new Paragraph(count+"", font1));
						subjectsCell = new PdfPCell(new Paragraph(e.getSubject(), font1));
						subjectsCell.setPaddingLeft(5);
						
					
						Paragraph finalMarks = new Paragraph();
						String assignmentScore = e.getIaScore();
						String totalMarksValue = e.getTotal();
						String writtenScores = "";
						Integer writtenScore = 0;
						if(e.getTeeScore()==null) {
							writtenScores = "-";
							finalMarks = new Paragraph(""+writtenScores, font1);
							logger.info("Written Score:"+writtenScores);
						}else {
							writtenScore = e.getTeeScore();
							finalMarks = new Paragraph(""+writtenScore, font1);
							logger.info("Written Score:"+writtenScore);
						}
						logger.info("Assignment Score:"+assignmentScore);
						logger.info("Total Marks:"+totalMarksValue);
						
						Paragraph assignmentMarks = new Paragraph(new Paragraph(assignmentScore, font1));
						
						Paragraph totalMarks = new Paragraph(new Paragraph(totalMarksValue, font1));
						
						if(Integer.parseInt(e.getGraceMarks()) > 0) 
							finalMarks.add(this.getNotationsChunkMap().get("GRACE"));
					
								
						
						finalExamMarksCell = new PdfPCell(finalMarks);
						assignmentMarksCell = new PdfPCell(assignmentMarks);
						totalMarksCell = new PdfPCell(totalMarks);
						
						srNoCell.setFixedHeight(25);
						srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						finalExamMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						assignmentMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						totalMarksCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

						srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						finalExamMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						assignmentMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						totalMarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);

						marksTable.addCell(srNoCell);
						marksTable.addCell(subjectsCell);
						marksTable.addCell(finalExamMarksCell);
						marksTable.addCell(assignmentMarksCell);
						marksTable.addCell(totalMarksCell);
						marksTable.completeRow();

						count++;
					}
				}//inner for loop
				
				marksTable.setWidthPercentage(100);
				marksTable.setSpacingBefore(5f);
				marksTable.setSpacingAfter(5f);
				float[] marksColumnWidths = {1.5f, 11f, 3f, 3f, 3f};
				marksTable.setWidths(marksColumnWidths);
				document.add(marksTable);

					PdfPTable footerTable1 = new PdfPTable(2);
					footerTable1.getDefaultCell().setBorder(0);
					
					PdfPCell attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: "+bean.getValidityEndMonth()+"-"+bean.getValidityEndYear()+ " Examination.",font1));
					attemptsCell.setBorder(0);
					attemptsCell.setColspan(2);
					footerTable1.addCell(attemptsCell);
					footerTable1.completeRow();
					
					footerTable1.setWidthPercentage(100);
					footerTable1.setSpacingBefore(3f);
					footerTable1.setSpacingAfter(3f);
					float[] footer1ColumnWidths = {3f, 3f};
					footerTable1.setWidths(footer1ColumnWidths);
					document.add(footerTable1);
					
					
					PdfPCell prepredByCell=null;
					PdfPCell checkedByCell=null;
					PdfPTable footerTable2 = new PdfPTable(3);
					
					if(logoRequired!=null && logoRequired.equalsIgnoreCase("Y")) {
						prepredByCell = new PdfPCell(new Paragraph("", font1));
						checkedByCell = new PdfPCell(new Paragraph("", font1));
						footerTable2.setSpacingBefore(15f);
					}else {
						prepredByCell = new PdfPCell(new Paragraph("Prepared by: ________________________", font1));
						checkedByCell = new PdfPCell(new Paragraph("Checked by: ________________________", font1));
						footerTable2.setSpacingBefore(10f);
					}	
					
					prepredByCell.setFixedHeight(25);
					prepredByCell.setBorder(0);
					PdfPCell resultDeclaredCell = new PdfPCell(new Paragraph("Result declared on", font1));
					resultDeclaredCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					resultDeclaredCell.setBorder(0);
					
					PdfPCell resultDeclaredValueCell = new PdfPCell(new Paragraph(": "+bean.getResultDeclarationDate(), font1));
					resultDeclaredValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					resultDeclaredValueCell.setBorder(0);

					footerTable2.addCell(prepredByCell);
					footerTable2.addCell(resultDeclaredCell);
					footerTable2.addCell(resultDeclaredValueCell);
					footerTable2.completeRow();
					
					checkedByCell.setBorder(0);
					PdfPCell marksheetIssuedCell = new PdfPCell(new Paragraph("Marksheet issued on", font1));
					marksheetIssuedCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					marksheetIssuedCell.setBorder(0);

					PdfPCell marksheetIssuedValueCell = new PdfPCell(new Paragraph(": "+markSheetDate, font1));
					marksheetIssuedValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					marksheetIssuedValueCell.setBorder(0);

					footerTable2.addCell(checkedByCell);
					footerTable2.addCell(marksheetIssuedCell);
					footerTable2.addCell(marksheetIssuedValueCell);
					footerTable2.completeRow();

					footerTable2.setWidthPercentage(100);
					footerTable2.setSpacingBefore(15f);
					footerTable2.setSpacingAfter(5f);
					float[] footer2ColumnWidths = {12.3f, 4.2f, 3.2f};
					footerTable2.setWidths(footer2ColumnWidths);
					document.add(footerTable2);


					PdfPTable footerTable3 = new PdfPTable(2);
					footerTable3.getDefaultCell().setBorder(0);
					
					PdfPCell emptyCell = new PdfPCell();
					emptyCell.setBorder(0);
					footerTable3.addCell(emptyCell);

					PdfPCell signatureCell = new PdfPCell();
					signatureCell.addElement(signature);
					signatureCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
					//signature.setAlignment(Element.ALIGN_RIGHT);
					signatureCell.setBorder(0);
					signatureCell.setVerticalAlignment(Element.ALIGN_BOTTOM);

					footerTable3.addCell(signatureCell);
					footerTable3.completeRow();

					footerTable3.setWidthPercentage(100);
					footerTable3.setSpacingBefore(5f);

					float[] footer3ColumnWidths = {6f, 2f};
					footerTable3.setWidths(footer3ColumnWidths);
					document.add(footerTable3);



					PdfPTable footerTable4 = new PdfPTable(2);
					footerTable4.getDefaultCell().setBorder(0);
					footerTable4.addCell(emptyCell);
					
					PdfPCell coeCell = new PdfPCell(new Paragraph("CONTROLLER OF EXAMINATIONS", font4));
					coeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					coeCell.setBorder(0);
					coeCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
					coeCell.setFixedHeight(15);
					footerTable4.addCell(coeCell);
					footerTable4.completeRow();

					footerTable4.setWidthPercentage(100);
					//footerTable4.setSpacingBefore(5f);
					footerTable4.setSpacingAfter(0f);

					float[] footer4ColumnWidths = {3f, 3f};
					footerTable4.setWidths(footer4ColumnWidths);

					document.add(footerTable4);
					
					//Include the Remarks and Note information on the mark sheet
					this.addRemarksAndNoteInfoOnMarksheet(document, bean.getPrgmStructApplicable(), bean.getProgram());
					
					document.newPage();
				
			} catch (DocumentException e) {
				logger.error("Error occurred while generating the marksheet for:"+bean.getSapid()+" Error Message is:"+e.getMessage());
			} catch (Exception e) {
				logger.error("Error occurred while generating the marksheet for:"+bean.getSapid()+" Error Message is:"+e.getMessage());
			}//catch block
			
		}//outer for loop
		
		document.close();
		
		logger.info("CreatePDF.generateNonGradedMarksheetPDF() - END");
		return embaMarksheetBean;
	}
	
	private Map<String,String> getFileAndFolderDetails(String year,String month,String program, String MARKSHEETS_PATH){
		Map<String,String> fileAndFolderPath = new HashMap<>(2);
		
		fileAndFolderPath.put("FileName",program+ "_"  + RandomStringUtils.randomAlphanumeric(12) + ".pdf".toUpperCase());
		
		fileAndFolderPath.put("FolderPath",MARKSHEETS_PATH + month + "-" + year + "/");
		
		//Create a folders if already dosen't exist.
		File folder = new File(fileAndFolderPath.get("FolderPath"));
		if (!folder.exists()) {   
			folder.mkdirs();   
		}  
		
		//return file and folder details map.
		return fileAndFolderPath;
	}
	
	private void addRemarksAndNoteInfoOnMarksheet(Document document, String prgmStructApplicable, String progarm) throws DocumentException {
		//1. Add the Remarks information on the mark sheet
		this.addRemarksInfoOnMarksheet(document);
		
		//2. Add the Note information on the mark sheet
		this.addNoteInfoOnMarksheet(document, prgmStructApplicable, progarm);
	}
	
	private void addRemarksInfoOnMarksheet(Document document) throws DocumentException {
		document.add(new Paragraph("1)  ANS :  Assignment Not Submitted.", font4));
		document.add(new Paragraph("2)  NA   :  Not Eligible due to non submission of assignment.", font4));
		document.add(new Paragraph("3)  *       :  Failures.", font4));
		document.add(new Paragraph("4)  **      :  Result on Hold due to Non Submission of Assignment.", font4));
		document.add(new Paragraph("5)  #       :  Marks brought forward.", font4));
		document.add(new Paragraph("6)  ~       :  Grace Marks given.", font4));
		document.add(new Paragraph("7)  NV      :  Null And Void", font4));
		document.add(new Paragraph("8)  ##      :  Course waiver subjects with marks brought forward", font4));
		
	}//addRemarksInfoOnMarksheet(-)
	
	private void addNoteInfoOnMarksheet(Document document, String prgmStructApplicable, String progarm) throws DocumentException{
		document.add(new Paragraph("Note:", font8));
		if("ACBM".equals(progarm)){
			document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 40", font4));
			document.add(new Paragraph("2)  Minimum aggregate marks for passing overall semester is 40%", font4));
			document.add(new Paragraph("3)  Submission of assignment is compulsory to be declared as pass in a subject in addition "
					+ "to \nappearance at the Term-End Examination.", font10));
		}else{
			if("JUL2017".equalsIgnoreCase(prgmStructApplicable)){
				document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 40", font4));
			}else{
				document.add(new Paragraph("1)  Maximum marks: 100. Minimum marks for passing in each subject: 50", font4));
			}
			document.add(new Paragraph("2)  Submission of assignment is compulsory to be declared as pass in a subject in addition "
					+ "to \nappearance at the Term-End Examination.", font10));
		}
	}//addNoteInfoOnMarksheet(-,-)
	
	public Map<String,Chunk> getNotationsChunkMap(){
		
		if(this.notationsMap==null || this.notationsMap.isEmpty()) {
			Chunk failure = new Chunk("*",font6);
			failure.setTextRise(5f);
			
			notationsMap.put("FAILURE", failure);
			
			Chunk resultOnHold = new Chunk("**",font6);
			resultOnHold.setTextRise(5f);
			notationsMap.put("RESULTONHOLD", resultOnHold);
	
			
			Chunk carryFwd = new Chunk("#",font6);
			carryFwd.setTextRise(5f);
			notationsMap.put("CARRYFWD", carryFwd);
	
			Chunk grace = new Chunk("~",font6);
			grace.setTextRise(5f);
			notationsMap.put("GRACE", grace);
			
		}//if
		
		//return notations chunk map. 
		return this.notationsMap;
	}
	
	public EmbaMarksheetBean generateMarksheetPDFForMBAWX(List<EmbaMarksheetBean> marksheetList,EmbaMarksheetBean msBean, 
			HashMap<String, String> programMap, HashMap<String, CenterExamBean> centersMap,String MARKSHEETS_PATH,
			List<EmbaPassFailBean> passFailDataListAllSapidsAllSems,HttpServletRequest request, Map<String, ProgramExamBean> modProgramMap) throws Exception {	
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		String markSheetDate = sdfr.format(new Date());
		String fileName = null;
		HashMap<String, CenterExamBean> centerNameCenterMap = generateCenterNameCenterMap(centersMap);//This will make another map with key as Name insteadof code/id
		Image signature = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/signature.jpg"));
		//Image signature = Image.getInstance("d:/signature.jpg");
		signature.scaleAbsolute(60, 42);
		String folderPath = "";
		if(marksheetList.size() > 0){
			EmbaMarksheetBean bean = marksheetList.get(0);
//			String studentId = bean.getSapid();	
			String year = bean.getExamYear();
			String month = bean.getExamMonth();
			fileName = bean.getProgram()  + "_"  + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
			fileName = fileName.toUpperCase();
			
			
			folderPath = MARKSHEETS_PATH + month + "-" + year + "/";
			
			File folder = new File(folderPath);
			if (!folder.exists()) {   
				folder.mkdirs();   
			}  
		}
		Document document = new Document(PageSize.A4);
		

		
		
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath+fileName));
//		request.getSession().setAttribute("fileName", folderPath+fileName);
		msBean.setFileName(folderPath+fileName);
		document.open();
		
		if(request.getAttribute("logoRequired")!=null && ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
			document.setMargins(50, 50, 20, 40);
		}  else {
			document.setMargins(50, 50, 105, 40);    
		} 
		 
		generateSignatureSheetForMBAWX(document,marksheetList, programMap, centersMap,centerNameCenterMap );
		//document.setMargins(50, 50, 40, 30);
		  
	//	Phrase watermark = new Phrase("Marksheet Downloaded from NGASCE Student Portal", new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
		PdfContentByte canvas = writer.getDirectContent();
//		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 300, 30);
		Image logo=null;
		if(request.getAttribute("logoRequired")!=null && ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
			logo = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nm_logo2.jpg")); 
			logo.scaleAbsolute(164,59);    
			logo.setIndentationLeft(-17); 
		}
			
		for (int i = 0; i < marksheetList.size(); i++) {
			
			String exam_year = null;
			String exam_month = null;
			String acad_year = null;
			String acad_month = null;
			
			
			
			EmbaMarksheetBean bean = marksheetList.get(i);
			
			
			exam_year = bean.getExamYear();
			exam_month = bean.getExamMonth();
			acad_year = bean.getYear();
			acad_month = bean.getMonth(); 
			
			Paragraph blankLine = new Paragraph("\n");
			try {

				
				
				if(request.getAttribute("logoRequired")!=null && ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
					document.add(logo);  
					document.add(blankLine);
				} else {
					//document.setMargins(50, 50, 170, 35);
				}
//				generateHeaderTable(document);	
				Chunk collegeNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", headerFont);	
				
				Paragraph collegeNameChunkPara = new Paragraph();
				collegeNameChunkPara.add(collegeNameChunk);
				collegeNameChunkPara.setAlignment(Element.ALIGN_CENTER);
				collegeNameChunkPara.setSpacingAfter(8);
				document.add(collegeNameChunkPara);
				
				Chunk stmtOfMarksChunk = new Chunk("STATEMENT OF GRADES", headerFont);		
				Paragraph stmtOfMarksPara = new Paragraph();				
				stmtOfMarksPara.add(stmtOfMarksChunk);
				stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
				stmtOfMarksPara.setSpacingAfter(8);			
				
				document.add(stmtOfMarksPara);

				PdfPTable headerTable = new PdfPTable(2); // 2 columns.
				headerTable.getDefaultCell().setBorder(0);
				
				String fatherName = "";
				String motherName = ""; 
				if(!StringUtils.isBlank(bean.getFatherName())) {
								
					if(bean.getFatherName().equals("null")) {
						fatherName = "";
					}else {
						fatherName = bean.getFatherName().trim().toUpperCase();
					}
								
				}
				if(!StringUtils.isBlank(bean.getMotherName())) {
					
					if(bean.getMotherName().equals("null")) {
						motherName = "";
					}else {
						motherName = bean.getMotherName().trim().toUpperCase();
					}
					
				}
				Chunk nameHeadChunk = new Chunk("NAME: ", descFont);	
				Chunk nameChunk = new Chunk((bean.getFirstName().trim().toUpperCase()+" "+bean.getLastName().trim()).toUpperCase().replace(".", " "), descBoldFont);
				Paragraph namePara = new Paragraph();				
				namePara.add(nameHeadChunk);
				namePara.add(nameChunk);
				
				Chunk rollNoHeadChunk = new Chunk("ROLL NO.: ", descFont);	
				Chunk rollNoChunk = new Chunk((bean.getSapid()).toUpperCase(), descBoldFont);
				Paragraph rollNoPara = new Paragraph();				
				rollNoPara.add(rollNoHeadChunk);
				rollNoPara.add(rollNoChunk);				
				
				PdfPCell rollNoCell = new PdfPCell(rollNoPara);
				rollNoCell.setBorder(0);
				rollNoCell.setPaddingRight(5);
				rollNoCell.setPaddingLeft(5);			
				
				PdfPCell nameCell = new PdfPCell(namePara);
				nameCell.setBorder(0);
				nameCell.setPaddingRight(5);
				nameCell.setPaddingLeft(5);
				headerTable.addCell(nameCell);				
				headerTable.addCell(rollNoCell);
				headerTable.completeRow();
								
				Chunk fatherNameHeadChunk = new Chunk("FATHER'S NAME: ", descFont);	
				Chunk fatherNameChunk = new Chunk((fatherName).toUpperCase(), descBoldFont);
				Paragraph fatherNamePara = new Paragraph();				
				fatherNamePara.add(fatherNameHeadChunk);
				fatherNamePara.add(fatherNameChunk);
				PdfPCell fatherNameCell = new PdfPCell(fatherNamePara);
				fatherNameCell.setBorder(0);
				fatherNameCell.setPaddingRight(5);
				fatherNameCell.setPaddingLeft(5);
				headerTable.addCell(fatherNameCell);
				headerTable.completeRow();
				
				Chunk motherNameHeadChunk = new Chunk("MOTHER'S NAME: ", descFont);	
				Chunk motherNameChunk = new Chunk((motherName).toUpperCase(), descBoldFont);
				Paragraph motherNamePara = new Paragraph();				
				motherNamePara.add(motherNameHeadChunk);
				motherNamePara.add(motherNameChunk);					
				
				PdfPCell motherNameCell = new PdfPCell(motherNamePara);
				motherNameCell.setBorder(0);
				motherNameCell.setPaddingBottom(5);
				motherNameCell.setPaddingRight(5);
				motherNameCell.setPaddingLeft(5);
				headerTable.addCell(motherNameCell);
				headerTable.completeRow();
				
				headerTable.setWidthPercentage(100);
				headerTable.setSpacingBefore(10f);
				headerTable.setSpacingAfter(5f);
				float[] columnWidths = {3.2f, 1.3f};
				headerTable.setWidths(columnWidths);
				document.add(headerTable);
				
				PdfPTable programTable = new PdfPTable(1);
				
				Chunk programHeadChunk = new Chunk("PROGRAMME: ", descFont);	
				Chunk programChunk = new Chunk( bean.getProgramname().toUpperCase(), descBoldFont);
				if("MBA - WX".equals(bean.getProgram())) {
					programChunk = new Chunk(("MASTER OF BUSINESS ADMINISTRATION (WORKING EXECUTIVES)").toUpperCase(), descBoldFont);
				}else if("M.Sc. (AI & ML Ops)".equals(bean.getProgram())) {
					programChunk = new Chunk(("MASTER OF SCIENCE IN ARTIFICIAL INTELLIGENCE AND MACHINE LEARNING OPS").toUpperCase(), descBoldFont);
				}
				Paragraph programPara = new Paragraph();				
				programPara.add(programHeadChunk);
				programPara.add(programChunk);		
				
				PdfPCell programCell = new PdfPCell(programPara);	

				programCell.setBorder(0);
				programCell.setPaddingBottom(5);
				programCell.setPaddingRight(5);
                programCell.setPaddingLeft(5);
              
				programTable.addCell(programCell);
				programTable.completeRow();				
				programTable.setWidthPercentage(100);
				programTable.setSpacingAfter(5f);
				float[] programcolumnWidths = {5f};
				programTable.setWidths(programcolumnWidths);
				document.add(programTable);
				
				//Add Mode of Delivery - START
				ProgramExamBean modProgram = modProgramMap.get(bean.getProgram());
				String mod = modProgram.getModeOfLearning();
				PdfPTable modTable = new PdfPTable(2);
				modTable.getDefaultCell().setBorder(0);
				Chunk modHeadChunk = new Chunk("MODE OF DELIVERY: ", descFont);	
				Chunk modChunk = new Chunk((mod).toUpperCase(), descBoldFont);
				Paragraph modPara = new Paragraph();				
				modPara.add(modHeadChunk);
				modPara.add(modChunk);
				PdfPCell modCell = new PdfPCell(modPara);
				modCell.setBorder(0);
				modCell.setPaddingRight(5);
				modCell.setPaddingLeft(5);
				modTable.addCell(modCell);
				modTable.completeRow();
				modTable.setWidthPercentage(100);
				modTable.setSpacingAfter(5f);
				float[] modcolumnWidths = {3.2f, 1.3f};
				modTable.setWidths(modcolumnWidths);
				document.add(modTable);
				//Add Mode of Delivery - END
				
				PdfPTable termTable = new PdfPTable(2);
				Chunk termDurationHeadChunk = new Chunk("TERM DURATION: ", descFont);	
				Chunk termDurationChunk = new Chunk((bean.getMonth() 
						+" "+bean.getYear()+"-"+bean.getExamMonth()+" "+bean.getExamYear()).toUpperCase(), descBoldFont);
				Paragraph termDurationPara = new Paragraph();				
				termDurationPara.add(termDurationHeadChunk);
				termDurationPara.add(termDurationChunk);	
				
				PdfPCell rgstnCell = new PdfPCell(new Paragraph(termDurationPara));		
				
		

				Chunk termHeadChunk = new Chunk("TERM: ", descFont);	
				Chunk termChunk = new Chunk("");
				
				if("1".equalsIgnoreCase(bean.getSem())) {
					termChunk = new Chunk("I", descBoldFont);

				}

				if("2".equalsIgnoreCase(bean.getSem())) {
					termChunk = new Chunk("II", descBoldFont);

				}	
				
				if("3".equalsIgnoreCase(bean.getSem())) {
					termChunk = new Chunk("III", descBoldFont);

				}
				
				if("4".equalsIgnoreCase(bean.getSem())) {
					termChunk = new Chunk("IV", descBoldFont);

				}
				
				if("5".equalsIgnoreCase(bean.getSem())) {
					termChunk = new Chunk("V", descBoldFont);

				}
				
				if("6".equalsIgnoreCase(bean.getSem())) {
					termChunk = new Chunk("VI", descBoldFont);

				}
				
				if("7".equalsIgnoreCase(bean.getSem())) {
					termChunk = new Chunk("VII", descBoldFont);

				}
				
				if("8".equalsIgnoreCase(bean.getSem())) {
					termChunk = new Chunk("VIII", descBoldFont);

				}
				
				Paragraph termPara = new Paragraph();				
				termPara.add(termHeadChunk);
				termPara.add(termChunk);	
				
				PdfPCell semCell = new PdfPCell(termPara);
				rgstnCell.setBorder(0);
				semCell.setBorder(0);
				semCell.setPaddingLeft(5);
				rgstnCell.setPaddingLeft(5);
				termTable.addCell(rgstnCell);
				termTable.addCell(semCell);
				termTable.completeRow();			
				
				termTable.setWidthPercentage(100);
				//termTable.setSpacingBefore(10f);
				termTable.setSpacingAfter(12f);
				
				float[] termColumnWidths = {3.2f, 1.3f};
				termTable.setWidths(termColumnWidths);
				document.add(termTable);
				
				PdfContentByte canvasLine = writer.getDirectContent();			// for hr(line)		
			
				canvasLine.moveTo(50,568);		
				canvasLine.lineTo(550,568);           
				canvasLine.closePathStroke();	
		        
				PdfPTable marksTable = new PdfPTable(4);				

				PdfPCell srNoCell = new PdfPCell(new Paragraph("SR. NO.", descBoldFont));
				PdfPCell subjectsCell = new PdfPCell(new Paragraph("COURSE/S", descBoldFont));			
				PdfPCell creditsCell = new PdfPCell(new Paragraph("CREDIT/S", descBoldFont));	
				PdfPCell gradesCell = new PdfPCell(new Paragraph("GRADE/S", descBoldFont));

				srNoCell.setFixedHeight(30);
				srNoCell.setVerticalAlignment(Element.ALIGN_TOP);				
				subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
				creditsCell.setVerticalAlignment(Element.ALIGN_TOP);
				gradesCell.setVerticalAlignment(Element.ALIGN_TOP);

				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				subjectsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				creditsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				gradesCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				
				srNoCell.setBorder(0);
				subjectsCell.setBorder(0);
				subjectsCell.setBorder(0);
				creditsCell.setBorder(0);
				gradesCell.setBorder(0);
				
				srNoCell.setFixedHeight(15);
				subjectsCell.setFixedHeight(15);
				creditsCell.setFixedHeight(15);
				gradesCell.setFixedHeight(15);
				
				marksTable.addCell(srNoCell);
				marksTable.addCell(subjectsCell);
				marksTable.addCell(creditsCell);
				marksTable.addCell(gradesCell);
				marksTable.completeRow();
				
				int count = 1;
				double gradePoint = 0.0;
				double gradeCalculation = 0.0;
				double total = 0;
				int passCount = 0;
//				int sum = passFailDataList.size();
				int sum = 0;
				double sumOftotalCredits = 0.0;
				List<EmbaPassFailBean> passFailDataListSapidAllSem = new ArrayList<EmbaPassFailBean>(); 

				for (EmbaPassFailBean e : passFailDataListAllSapidsAllSems) {		

					if(bean.getSapid().equals(e.getSapid())) {
						if(e.getPoints() != null && !e.getPoints().trim().isEmpty()  && Integer.parseInt(e.getSem()) <= Integer.parseInt(bean.getSem())) {
							passFailDataListSapidAllSem.add(e);
						}
						
						if(e.getPoints() != null && !e.getPoints().trim().isEmpty()  && bean.getSem().equals(e.getPsssem()) ) {
							e.setSem(e.getPsssem());
							gradePoint = Float.parseFloat(e.getPoints());
//							gradeCalculation = gradePoint * 4;
//							total = total + gradeCalculation;			
							srNoCell = new PdfPCell(new Paragraph(count+"", descFont));
							if(exam_year.equalsIgnoreCase(e.getExamYear()) && exam_month.equalsIgnoreCase(e.getExamMonth()) )
							{
								subjectsCell = new PdfPCell(new Paragraph(e.getSubject().toUpperCase(), descFont));	
								gradesCell = new PdfPCell(new Paragraph(e.getGrade().toUpperCase(), descFont));
							}else {
								subjectsCell = new PdfPCell(new Paragraph(e.getSubject().toUpperCase(), descFont));	
								gradesCell = new PdfPCell(new Paragraph(e.getGrade().toUpperCase()  + " #", descFont));
							}
							
							if(e.getPrgm_sem_subj_id() == 1883) { // Added by Abhay for MBA - WX Subject
								creditsCell = new PdfPCell(new Paragraph("20.00", descFont));
								sumOftotalCredits = sumOftotalCredits + 20;
								gradeCalculation = gradePoint * 20;
							}else if(e.getPrgm_sem_subj_id() == 2351) { //  Added by Abhay for New Program Structure of MBA - WX Capstone Project
								creditsCell = new PdfPCell(new Paragraph("12.00", descFont));
								sumOftotalCredits = sumOftotalCredits + 12;
								gradeCalculation = gradePoint * 12;
							}
							
							/*Commented by Siddheshwar_Khanse as subject credits getting dynamically 
							else if(e.getPrgm_sem_subj_id() == 1959) { // Added by Abhay for M.Sc. (AI & ML Ops) Subject
								creditsCell = new PdfPCell(new Paragraph("5.00", descFont));
								sumOftotalCredits = sumOftotalCredits + 5;
								gradeCalculation = gradePoint * 5;
							}else if(e.getPrgm_sem_subj_id() == 1961) { // Added by Abhay for M.Sc. (AI & ML Ops) Subject
								creditsCell = new PdfPCell(new Paragraph("3.00", descFont));
								sumOftotalCredits = sumOftotalCredits + 3;
								gradeCalculation = gradePoint * 3;
							}*/ 
							else if(bean.getConsumerProgramStructureId()==131) {
								double subjectCredits = SubjectsCerditsConfiguration.getMSCAIOpsSubjectCredits(e.getSubject());
								creditsCell = new PdfPCell(new Paragraph(""+subjectCredits, descFont));
								sumOftotalCredits = sumOftotalCredits + subjectCredits;
								gradeCalculation = gradePoint * subjectCredits;
							}
							else if(bean.getConsumerProgramStructureId()==154 || bean.getConsumerProgramStructureId()==155 || bean.getConsumerProgramStructureId()==158) {
								double subjectCredits = SubjectsCerditsConfiguration.getMSCAISubjectCredits(e.getSubject());
								creditsCell = new PdfPCell(new Paragraph(""+subjectCredits, descFont));
								sumOftotalCredits = sumOftotalCredits + subjectCredits;
								gradeCalculation = gradePoint * subjectCredits;
							}
							else if(bean.getConsumerProgramStructureId()==160) {
								double subjectCredits = SubjectsCerditsConfiguration.getMBAWXSubjectCredits(e.getPrgm_sem_subj_id());
								creditsCell = new PdfPCell(new Paragraph(""+subjectCredits, descFont));
								sumOftotalCredits = sumOftotalCredits + subjectCredits;
								gradeCalculation = gradePoint * subjectCredits;
							}
							else {
								creditsCell = new PdfPCell(new Paragraph("4.00", descFont));
								sumOftotalCredits = sumOftotalCredits + 4;
								gradeCalculation = gradePoint * 4;
							}
							
							total = total + gradeCalculation;
							
							srNoCell.setBorder(0);
							subjectsCell.setBorder(0);
							gradesCell.setBorder(0);
							creditsCell.setBorder(0);
							
							subjectsCell.setPaddingLeft(5);
						
//							srNoCell.setFixedHeight(15);
//							subjectsCell.setFixedHeight(15);
//							gradesCell.setFixedHeight(15);
//							creditsCell.setFixedHeight(15);
							
							
							srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							creditsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							gradesCell.setHorizontalAlignment(Element.ALIGN_LEFT);
							
							srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
							creditsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							gradesCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			
							
							marksTable.addCell(srNoCell);
							marksTable.addCell(subjectsCell);
							marksTable.addCell(creditsCell);
							marksTable.addCell(gradesCell);
							marksTable.completeRow();
							if(e.getIsPass().equals("Y")) {
								passCount++;
							}
							
							count++;
							sum++;
							
						}
						
					}
					
				}		 
				marksTable.setWidthPercentage(100);
				marksTable.setSpacingBefore(15f);
				marksTable.setSpacingAfter(30f);
				float[] marksColumnWidths = {2.5f, 12f, 3.5f, 3.5f};
				marksTable.setWidths(marksColumnWidths);
				document.add(marksTable);

				
						
//				int sumOftotalCredits = sum * 4;
				double gpa = total/sumOftotalCredits;
				
				PdfPTable footerTable1 = new PdfPTable(2);
				footerTable1.getDefaultCell().setBorder(0);				
				
			//	PdfPCell attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: "+bean.getValidityEndMonth()+"-"+bean.getValidityEndYear()+ " Examination.",font1));
				
				Chunk gradeHeadChunk = new Chunk("GRADE POINT AVERAGE (GPA): ", descFont);	
				Chunk gradeChunk = new Chunk(String.format ("%,.2f", gpa)+"", descBoldFont);
				Paragraph gradePara = new Paragraph();				
				gradePara.add(gradeHeadChunk);
				gradePara.add(gradeChunk);	
				
				PdfPCell attemptsCell = new PdfPCell(gradePara);				
				attemptsCell.setBorder(0);
				attemptsCell.setColspan(2);
				attemptsCell.setPaddingLeft(22);
				footerTable1.addCell(attemptsCell);
				footerTable1.completeRow();
				
				
				Chunk cgpaHeadChunk = new Chunk("CUMULATIVE GRADE POINT AVERAGE (CGPA): ", descFont);	
				
				Chunk cgpaChunk = new Chunk();
				Paragraph cgpaPara = new Paragraph();	
				
				if("1".equalsIgnoreCase(bean.getSem())) {
					cgpaChunk = new Chunk(String.format ("%,.2f", gpa)+"", descBoldFont);
					cgpaPara.add(cgpaHeadChunk);
					cgpaPara.add(cgpaChunk);	
				} else {
				
				double cgradeCalculation = 0;
				double ctotal = 0;
				double cgradePoint = 0;
//				int csum = passFailDataListSapidAllSem.size();	
//				int csumOftotalCredits = csum * 4;
				double csumOftotalCredits = 0;
				
				
				/*
				 * if("5".equalsIgnoreCase(bean.getSem())) { csumOftotalCredits =
				 * csumOftotalCredits + 16; }
				 */
				
				
				for (EmbaPassFailBean e : passFailDataListSapidAllSem) {		
					
					cgradePoint = Float.parseFloat(e.getPoints());
					
					if(e.getPrgm_sem_subj_id() == 1883) { // Added by Abhay for MBA - WX Subject
					    csumOftotalCredits = csumOftotalCredits + 20;
						cgradeCalculation = cgradePoint * 20;
					}else if(e.getPrgm_sem_subj_id() == 2351) { // Added by Abhay for New Program Structure of MBA - WX Capstone Project
					    csumOftotalCredits = csumOftotalCredits + 12;
						cgradeCalculation = cgradePoint * 12;
					}
					/*Commented by Siddheshwar_Khanse as subject credits getting dynamically
					else if(e.getPrgm_sem_subj_id() == 1959) { // Added by Abhay for M.Sc. (AI & ML Ops) Subject
						csumOftotalCredits = csumOftotalCredits + 5;
						cgradeCalculation = cgradePoint * 5;
					}else if(e.getPrgm_sem_subj_id() == 1961) { // Added by Abhay for M.Sc. (AI & ML Ops) Subject
						csumOftotalCredits = csumOftotalCredits + 3;
						cgradeCalculation = cgradePoint * 3;
					}*/ 
					else if(bean.getConsumerProgramStructureId()==131) {
						double subjectCredits = SubjectsCerditsConfiguration.getMSCAIOpsSubjectCredits(e.getSubject());
						csumOftotalCredits = csumOftotalCredits + subjectCredits;
						cgradeCalculation = cgradePoint * subjectCredits;
					}
					else if(bean.getConsumerProgramStructureId()==154 || bean.getConsumerProgramStructureId()==155 || bean.getConsumerProgramStructureId()==158) {
						double subjectCredits = SubjectsCerditsConfiguration.getMSCAISubjectCredits(e.getSubject());
						csumOftotalCredits = csumOftotalCredits + subjectCredits;
						cgradeCalculation = cgradePoint * subjectCredits;
					}
					else if(bean.getConsumerProgramStructureId()==160) {
						double subjectCredits = SubjectsCerditsConfiguration.getMBAWXSubjectCredits(e.getPrgm_sem_subj_id());
						csumOftotalCredits = csumOftotalCredits + subjectCredits;
						cgradeCalculation = cgradePoint * subjectCredits;
					}
					else {
						csumOftotalCredits = csumOftotalCredits + 4;
						cgradeCalculation = cgradePoint * 4;
					}
					ctotal = ctotal + cgradeCalculation;			
				}	
				double cgpa = ctotal/csumOftotalCredits;
		
				cgpaChunk = new Chunk(String.format ("%,.2f", cgpa)+"", descBoldFont);
				cgpaPara.add(cgpaHeadChunk);
				cgpaPara.add(cgpaChunk);	
				}		
			
			    
		       PdfPCell attemptsCell2 = new PdfPCell(cgpaPara);				
				attemptsCell2.setBorder(0);
				attemptsCell2.setColspan(2);
				attemptsCell2.setPaddingLeft(22);
				footerTable1.addCell(attemptsCell2);
				footerTable1.completeRow();
				
				footerTable1.setWidthPercentage(100);
				footerTable1.setSpacingBefore(25f);
				footerTable1.setSpacingAfter(5f);
				float[] footer1ColumnWidths = {3f, 3f};
				footerTable1.setWidths(footer1ColumnWidths);
				document.add(footerTable1);

				
				String remark = "";
				if(gpa >= 3.5 && sum == passCount) {
					remark = "PASS WITH DISTINCTION";
				}
				
				if(gpa < 3.5 && sum == passCount) {
					remark = "PASS";
				}
				
				if(sum != passCount){
					remark = "FAIL";
				}
				Chunk remarkHeadChunk = new Chunk("REMARK: ", descFont);	
				Chunk remarkChunk = new Chunk(remark, descBoldFont);
				Paragraph remarkPara = new Paragraph();				
				remarkPara.add(remarkHeadChunk);
				remarkPara.add(remarkChunk);	
				
				PdfPTable remarkFooterTable = new PdfPTable(2);
				remarkFooterTable.getDefaultCell().setBorder(0);
				
				PdfPCell remarkCell = new PdfPCell(remarkPara);				
				remarkCell.setBorder(0);
				remarkCell.setColspan(2);
				remarkCell.setPaddingLeft(22);
				remarkFooterTable.addCell(remarkCell);
				remarkFooterTable.completeRow();			    
		    
				
				remarkFooterTable.setWidthPercentage(100);
				remarkFooterTable.setSpacingBefore(25f);
				remarkFooterTable.setSpacingAfter(5f);
				document.add(remarkFooterTable);
			
				PdfPTable footerTable3 = new PdfPTable(2);
				footerTable3.getDefaultCell().setBorder(0);
				PdfPCell emptyCell = new PdfPCell();
				emptyCell.setBorder(0);
				emptyCell.setPaddingLeft(22);

				footerTable3.addCell(emptyCell);

				PdfPCell signatureCell = new PdfPCell();
				signatureCell.addElement(signature);
				signatureCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
				//signature.setAlignment(Element.ALIGN_RIGHT);
				signatureCell.setBorder(0);
				signatureCell.setVerticalAlignment(Element.ALIGN_BOTTOM);

				footerTable3.addCell(signatureCell);
				footerTable3.completeRow();

				footerTable3.setWidthPercentage(100);
				footerTable3.setSpacingBefore(15f);
				//footerTable3.setSpacingAfter(15f);

				float[] footer3ColumnWidths = {6f, 2f};
				footerTable3.setWidths(footer3ColumnWidths);
				document.add(footerTable3);



				PdfPTable footerTable4 = new PdfPTable(2);
				footerTable4.getDefaultCell().setBorder(0);
				footerTable4.addCell(emptyCell);
				footerTable4.addCell(emptyCell);


				Chunk issuedHeadChunk = new Chunk("GRADE SHEET ISSUED ON: ", descFont);	
				Chunk issuedChunk = new Chunk(markSheetDate, descBoldFont);
				Paragraph issuedPara = new Paragraph();				
				issuedPara.add(issuedHeadChunk);
				issuedPara.add(issuedChunk);	
				
				PdfPCell issuedCell = new PdfPCell(issuedPara);
				issuedCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				issuedCell.setBorder(0);
				issuedCell.setPaddingLeft(22);

			//	issuedCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				issuedCell.setFixedHeight(15);
				
				PdfPCell coeCell = new PdfPCell(new Paragraph("CONTROLLER OF EXAMINATIONS", font4));
				coeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				coeCell.setBorder(0);
				coeCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				coeCell.setFixedHeight(15);
				coeCell.setPaddingLeft(22);
				
				footerTable4.addCell(issuedCell);
				footerTable4.addCell(coeCell);
				footerTable4.completeRow();

				footerTable4.setWidthPercentage(100);
				footerTable4.setSpacingBefore(10f);
				footerTable4.setSpacingAfter(10f);

				float[] footer4ColumnWidths = {3f, 3f};
				footerTable4.setWidths(footer4ColumnWidths);

				document.add(footerTable4);
				
				

				PdfPTable footerTable5 = new PdfPTable(1);
				footerTable5.getDefaultCell().setBorder(0);
				
				footerTable5.setWidthPercentage(100);
				footerTable5.setSpacingBefore(10f);
				//footerTable3.setSpacingAfter(15f);
				
				PdfPCell carryforwardCell = new PdfPCell(new Paragraph(" \"#\" INDICATES CARRY FORWARD SUBJECTS AND GRADES", font6));		
				carryforwardCell.setBorder(0);
				carryforwardCell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				carryforwardCell.setPaddingLeft(25);
				footerTable5.addCell(carryforwardCell);
				
				
				PdfPCell descCell = new PdfPCell(new Paragraph(" \"-\" INDICATES THAT GPA/CGPA WILL BE CALCULATED AFTER CLEARANCE OF EXISTING F GRADES", font6));		
				descCell.setBorder(0);
				descCell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				descCell.setPaddingLeft(25);
				
				footerTable5.addCell(descCell);
				

				document.add(footerTable5);				
				
				PdfPTable footerTable6 = new PdfPTable(1);
				footerTable6.getDefaultCell().setBorder(0);
				
				footerTable6.setWidthPercentage(100);
				footerTable6.setSpacingBefore(5f);
				footerTable6.setSpacingAfter(10f);			
				
				PdfPCell desc2Cell = new PdfPCell(new Paragraph("GRADE POINTS: A+ : 4; A : 3.75; A- : 3.5; B+ :3.25; B: 3; B- : 2.75; C+ : 2.5; C : 2.25; C- : 2; F : 0",footerBoldFont));		
				desc2Cell.setBorder(0);
				desc2Cell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				desc2Cell.setPaddingLeft(25);
				
				footerTable6.addCell(desc2Cell);				
				
				document.add(footerTable6);
		
				
				PdfPTable footerTable7 = new PdfPTable(1);
				footerTable7.getDefaultCell().setBorder(0);
				
				footerTable7.setWidthPercentage(100);
				footerTable7.setSpacingBefore(25f);
				footerTable7.setSpacingAfter(25f);
				
				PdfPCell desc3Cell = new PdfPCell(new Paragraph("PREPARED BY: ",footerBoldFont));		
				desc3Cell.setBorder(0);
				desc3Cell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				desc3Cell.setPaddingLeft(45);
				
				footerTable7.addCell(desc3Cell);				
				
				document.add(footerTable7);
				
				PdfPTable footerTable8 = new PdfPTable(2);
				footerTable8.getDefaultCell().setBorder(0);
				
				footerTable8.setWidthPercentage(100);
				footerTable8.setSpacingBefore(5f);
				
				PdfPCell desc4Cell = new PdfPCell(new Paragraph("CHECKED BY: ",footerBoldFont));		
				desc4Cell.setBorder(0);
				desc4Cell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				desc4Cell.setPaddingLeft(45);
				
				footerTable8.addCell(desc4Cell);
//				footerTable8.addCell(desc3Cell);
				footerTable8.completeRow();
				float[] footer8ColumnWidths = {3f, 3f};
				footerTable8.setWidths(footer8ColumnWidths);
				
				
				document.add(footerTable8);				
				
				document.newPage();

			} catch (DocumentException e) {
				
			} catch (Exception e) {
				
			}
		}
		document.close(); // no need to close PDFwriter?
		return msBean;

	}
	
	private void generateSignatureSheetForMBAWX(Document document, List<EmbaMarksheetBean> marksheetList,	
			HashMap<String, String> programMap, HashMap<String, CenterExamBean> centersMap, HashMap<String, CenterExamBean> centerNameCenterMap) throws DocumentException {
		//document.setMargins(50, 50, 20, 40);
		int studentCount = 1;
		String learningCenter = "None";
		PdfPTable signatureTable = new PdfPTable(6);
		for (int i = 0; i < marksheetList.size(); i++) { 
			EmbaMarksheetBean bean = marksheetList.get(i);
			String examMonth = bean.getExamMonth();
			String examYear = bean.getExamYear();
			
			String program = bean.getProgram().trim();
			
			String currentLearningCenter = bean.getLc();
			
			if(!learningCenter.equalsIgnoreCase(currentLearningCenter)){
				studentCount = 1;
				
				float[] marksColumnWidths = {1.5f, 5f, 8f, 3f,2f, 8f};
				signatureTable.setWidths(marksColumnWidths);
				
				signatureTable.setWidthPercentage(100);
				signatureTable.setSpacingBefore(15f);
				signatureTable.setSpacingAfter(10f);
				
				document.add(signatureTable);
				document.newPage();
				
				signatureTable = new PdfPTable(6);
				CenterExamBean centerBean = centersMap.get(bean.getCenterCode());

				//Paragraph centerCodePara = null;
				Paragraph centerNamePara = null;
				Paragraph centerAddress = null;

				if(centerBean != null){
					//centerCodePara = new Paragraph("Center Code: " + centerBean.getCenterCode(), font9);
					centerNamePara = new Paragraph("Learning Center: " + centerBean.getLc(), font1);
					String lcAddress = "";
					if(centerNameCenterMap.get(centerBean.getLc()) != null){
						lcAddress = centerNameCenterMap.get(centerBean.getLc()).getAddress();
					}
					centerAddress = new Paragraph("Center Address: " + lcAddress, font1);
				}else{
					//centerCodePara = new Paragraph("Center Code: Not Available", font9);
					centerNamePara = new Paragraph("Learning Center: Not Available", font1);
					centerAddress = new Paragraph("Center Address: Not Available", font1);
				}

				Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font2);
				collegNameChunk.setUnderline(0.1f, -2f);
				Paragraph collegeName = new Paragraph();
				collegeName.add(collegNameChunk);
				collegeName.setAlignment(Element.ALIGN_CENTER);
				document.add(collegeName);
				
				Chunk titleChunk = new Chunk("SIGNATURE SHEET", font2);
				titleChunk.setUnderline(0.1f, -2f);
				Paragraph titlePara = new Paragraph();
				titlePara.add(titleChunk);
				titlePara.setAlignment(Element.ALIGN_CENTER);
				titlePara.setSpacingAfter(10f);
				document.add(titlePara);
//				
				//Paragraph programPara = new Paragraph("Program: " + program, font1);
				//Paragraph semPara = new Paragraph("Sem: " + bean.getSem(), font1);

				//document.add(programPara);
				//document.add(semPara);
				//document.add(centerCodePara);
				document.add(centerNamePara);
				document.add(centerAddress);
				learningCenter = currentLearningCenter;
				//document.newPage();
				
				
				
				
				PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font9));
				PdfPCell sapIdCell = new PdfPCell(new Paragraph("SAP ID", font9));
				PdfPCell studentNameCell = new PdfPCell(new Paragraph("Student Name", font9));
				PdfPCell examYearMonthCell = new PdfPCell(new Paragraph("Exam Year-Month", font9));
				PdfPCell semCell = new PdfPCell(new Paragraph("Sem", font9));
				PdfPCell signatureCell = new PdfPCell(new Paragraph("Signature", font9));
				srNoCell.setFixedHeight(25);
				
				signatureTable.addCell(srNoCell);
				signatureTable.addCell(sapIdCell);
				signatureTable.addCell(studentNameCell);
				signatureTable.addCell(examYearMonthCell);
				signatureTable.addCell(semCell);
				signatureTable.addCell(signatureCell);

				signatureTable.completeRow();
				
			}
			PdfPCell srNoCell = new PdfPCell(new Paragraph((studentCount++)+"", font9));
			PdfPCell sapIdCell = new PdfPCell(new Paragraph(bean.getSapid(), font9));
			PdfPCell studentNameCell = new PdfPCell(new Paragraph(bean.getFirstName().trim().toUpperCase() + " "+ bean.getLastName().trim().toUpperCase().replace(".", " "), font9));
			PdfPCell examYearMonthCell = new PdfPCell(new Paragraph(bean.getExamMonth() + "-" + bean.getExamYear(), font9));
//			PdfPCell semCell = new PdfPCell(new Paragraph(bean.getSem(), font9));
			PdfPCell semCell = new PdfPCell(new Paragraph(bean.getSem(), font9));
			
			PdfPCell signatureCell = new PdfPCell(new Paragraph(" ", font9));
			srNoCell.setFixedHeight(25);
			srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			
			signatureTable.addCell(srNoCell);
			signatureTable.addCell(sapIdCell);
			signatureTable.addCell(studentNameCell);
			signatureTable.addCell(examYearMonthCell);
			signatureTable.addCell(semCell);
			signatureTable.addCell(signatureCell);

			signatureTable.completeRow();
			
			if(i == marksheetList.size()- 1){
				float[] marksColumnWidths = {1.5f, 5f, 8f, 3f,2f, 8f};
				signatureTable.setWidths(marksColumnWidths);
				
				signatureTable.setWidthPercentage(100);
				signatureTable.setSpacingBefore(15f);
				signatureTable.setSpacingAfter(50f);
				//document.setMargins(60, 60, 100, 100);
				document.add(signatureTable);
				document.newPage();
			}
			
		}
//		document.setMargins(50, 50, 120, 40);
		
	}
	public EmbaMarksheetBean mCreateStudentSelfMarksheetPDFforMbaX(List<EmbaMarksheetBean> marksheetList,EmbaMarksheetBean msBean, 
			 String MARKSHEETS_PATH,List<EmbaPassFailBean> passFailDataList, List<EmbaPassFailBean> passFailDataListAllSem,
			 Map<String, ProgramExamBean> modProgramMap) throws Exception {
		// TODO Auto-generated method stub		
		
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		String markSheetDate = sdfr.format(new Date());
		String fileName = null;
		//Image signature = Image.getInstance(new URL("https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/signature.jpg"));
		//Image signature = Image.getInstance("d:/signature.jpg");
		//signature.scaleAbsolute(60, 42);
		String folderPath = "";

		String exam_year = null;
		String exam_month = null;
		String acad_year = null;
		String acad_month = null; 
		if(marksheetList.size() > 0){
			EmbaMarksheetBean bean = marksheetList.get(0);
			String studentId = bean.getSapid();
		
			exam_year = bean.getExamYear();
			exam_month = bean.getExamMonth();
			acad_year = bean.getYear();
			acad_month = bean.getMonth(); 
			fileName = studentId + "_" + exam_month + "_" + exam_year + "_" 
			// .replace added by Ashutosh. This is because program MBA - WX has space in program name
			+ bean.getProgram().replace(" ", "_") 
			+ "_Term" +   bean.getSem()  + "_"  + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
			fileName = fileName.toUpperCase();
			
			
			folderPath = MARKSHEETS_PATH + exam_month + "-" + exam_year + "/";
			
			File folder = new File(folderPath);
			if (!folder.exists()) {   
				folder.mkdirs();   
			}  
		}
		Document document = new Document(PageSize.A4);
		document.setMargins(60, 60, 60, 60);

		
		
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath+fileName));
//		request.getSession().setAttribute("fileName", folderPath+fileName);
		msBean.setFileName(folderPath+fileName);
		document.open();

		
		Phrase watermark = new Phrase("Marksheet Downloaded from NGASCE Student Portal", new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
		PdfContentByte canvas = writer.getDirectContent();
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 420, 30);
		
		generateHeaderTable(document);		
		
		for (int i = 0; i < marksheetList.size(); i++) {

			EmbaMarksheetBean bean = marksheetList.get(i);
		
			try {

				
//				Chunk collegeNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", headerFont);	
//				
//				Paragraph collegeNameChunkPara = new Paragraph();
//				collegeNameChunkPara.add(collegeNameChunk);
//				collegeNameChunkPara.setAlignment(Element.ALIGN_CENTER);
//				collegeNameChunkPara.setSpacingAfter(8);
//				document.add(collegeNameChunkPara);
				
				
				Chunk stmtOfMarksChunk = new Chunk("STATEMENT OF GRADES", headerFont);		
				Paragraph stmtOfMarksPara = new Paragraph();				
				stmtOfMarksPara.add(stmtOfMarksChunk);
				stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
				stmtOfMarksPara.setSpacingAfter(8);			
				
				document.add(stmtOfMarksPara);

				PdfPTable headerTable = new PdfPTable(2); // 2 columns.
				headerTable.getDefaultCell().setBorder(0);
				
				String fatherName = "";
				String motherName = ""; 
				
				if(!StringUtils.isBlank(bean.getFatherName())) {
								
					if(bean.getFatherName().equals("null")) {
						fatherName = "";
					}else {
						fatherName = bean.getFatherName().trim().toUpperCase();
					}
								
				}
				
				if(!StringUtils.isBlank(bean.getMotherName())) {
					
					if(bean.getMotherName().equals("null")) {
						motherName = "";
					}else {
						motherName = bean.getMotherName().trim().toUpperCase();
					}
					
				}
				
				Chunk nameHeadChunk = new Chunk("NAME: ", descFont);	
				Chunk nameChunk = new Chunk((bean.getFirstName().trim().toUpperCase()+" "+bean.getLastName().trim()).toUpperCase().replace(".", " "), descBoldFont);
				Paragraph namePara = new Paragraph();				
				namePara.add(nameHeadChunk);
				namePara.add(nameChunk);
				
				Chunk rollNoHeadChunk = new Chunk("ROLL NO.: ", descFont);	
				Chunk rollNoChunk = new Chunk((bean.getSapid()).toUpperCase(), descBoldFont);
				Paragraph rollNoPara = new Paragraph();				
				rollNoPara.add(rollNoHeadChunk);
				rollNoPara.add(rollNoChunk);				
				
				PdfPCell rollNoCell = new PdfPCell(rollNoPara);
				rollNoCell.setBorder(0);
				rollNoCell.setPaddingRight(5);
				rollNoCell.setPaddingLeft(5);			
				
			
				PdfPCell nameCell = new PdfPCell(namePara);
				nameCell.setBorder(0);
				nameCell.setPaddingRight(5);
				nameCell.setPaddingLeft(5);
				headerTable.addCell(nameCell);				
				headerTable.addCell(rollNoCell);
				headerTable.completeRow();
								
				Chunk fatherNameHeadChunk = new Chunk("FATHER'S NAME: ", descFont);	
				Chunk fatherNameChunk = new Chunk((fatherName).toUpperCase(), descBoldFont);
				Paragraph fatherNamePara = new Paragraph();				
				fatherNamePara.add(fatherNameHeadChunk);
				fatherNamePara.add(fatherNameChunk);
				
				PdfPCell fatherNameCell = new PdfPCell(fatherNamePara);
				fatherNameCell.setBorder(0);
				fatherNameCell.setPaddingRight(5);
				fatherNameCell.setPaddingLeft(5);
				headerTable.addCell(fatherNameCell);
				headerTable.completeRow();
				
				Chunk motherNameHeadChunk = new Chunk("MOTHER'S NAME: ", descFont);	
				Chunk motherNameChunk = new Chunk((motherName).toUpperCase(), descBoldFont);
				Paragraph motherNamePara = new Paragraph();				
				motherNamePara.add(motherNameHeadChunk);
				motherNamePara.add(motherNameChunk);					
				
				PdfPCell motherNameCell = new PdfPCell(motherNamePara);
				motherNameCell.setBorder(0);
				motherNameCell.setPaddingBottom(5);
				motherNameCell.setPaddingRight(5);
				motherNameCell.setPaddingLeft(5);
				headerTable.addCell(motherNameCell);
				headerTable.completeRow();
				
				headerTable.setWidthPercentage(100);
				headerTable.setSpacingBefore(10f);
				headerTable.setSpacingAfter(5f);
				float[] columnWidths = {3.2f, 1.3f};
				headerTable.setWidths(columnWidths);
				document.add(headerTable);
				

				PdfPTable programTable = new PdfPTable(1);
				
				Chunk programHeadChunk = new Chunk("PROGRAMME: ", descFont);
				Chunk programChunk = new Chunk(("MBA (EXECUTIVE) WITH SPECIALISATION IN BUSINESS ANALYTICS").toUpperCase(), descBoldFont);
				Paragraph programPara = new Paragraph();				
				programPara.add(programHeadChunk);
				programPara.add(programChunk);		
				
				PdfPCell programCell = new PdfPCell(programPara);	

				programCell.setBorder(0);
				programCell.setPaddingBottom(5);
				programCell.setPaddingRight(5);
               programCell.setPaddingLeft(5);
               
				programTable.addCell(programCell);
				programTable.completeRow();				
				programTable.setWidthPercentage(100);
				programTable.setSpacingAfter(5f);
				float[] programcolumnWidths = {5f};
				programTable.setWidths(programcolumnWidths);
				document.add(programTable);
				
				//Added Mode Of Delivery - START
				ProgramExamBean programExamBean = modProgramMap.get(bean.getProgram());
				String mod = programExamBean.getModeOfLearning();
				PdfPTable modTable = new PdfPTable(2);
				modTable.getDefaultCell().setBorder(0);
				Chunk modHeadChunk = new Chunk("MODE OF DELIVERY: ", descFont);	
				Chunk modChunk = new Chunk((mod), descBoldFont);
				Paragraph modPara = new Paragraph();				
				modPara.add(modHeadChunk);
				modPara.add(modChunk);
				PdfPCell modCell = new PdfPCell(modPara);
				modCell.setBorder(0);
				modCell.setPaddingRight(5);
				modCell.setPaddingLeft(5);
				modTable.addCell(modCell);
				modTable.completeRow();
				modTable.setWidthPercentage(100);
				modTable.setSpacingAfter(5f);
				float[] modcolumnWidths = {3.2f, 1.3f};
				modTable.setWidths(modcolumnWidths);
				document.add(modTable);
				//Added Mode Of Delivery - END
				
				PdfPTable termTable = new PdfPTable(2);
				
				Chunk termDurationHeadChunk = new Chunk("TERM DURATION: ", descFont);	
				Chunk termDurationChunk = new Chunk((bean.getEnrollmentMonth()
						+" "+bean.getEnrollmentYear()+"-"+exam_month+" "+exam_year).toUpperCase(), descBoldFont);
				Paragraph termDurationPara = new Paragraph();				
				termDurationPara.add(termDurationHeadChunk);
				termDurationPara.add(termDurationChunk);	
				
				PdfPCell rgstnCell = new PdfPCell(new Paragraph(termDurationPara));		
				
				
				

				Chunk termHeadChunk = new Chunk("TERM: ", descFont);
				Chunk termChunk = new Chunk(convertIntegerToRoman(Integer.parseInt(msBean.getSem())), descBoldFont);
				Paragraph termPara = new Paragraph();				
				termPara.add(termHeadChunk);
				termPara.add(termChunk);	
				
				PdfPCell semCell = new PdfPCell(termPara);
				rgstnCell.setBorder(0);
				semCell.setBorder(0);
				semCell.setPaddingLeft(5);
				rgstnCell.setPaddingLeft(5);
				termTable.addCell(rgstnCell);
				termTable.addCell(semCell);
				termTable.completeRow();			
				
				termTable.setWidthPercentage(100);
				//termTable.setSpacingBefore(10f);
				termTable.setSpacingAfter(12f);
				
				float[] termColumnWidths = {3.2f, 1.3f};
				termTable.setWidths(termColumnWidths);
				document.add(termTable);
				
				PdfContentByte canvasLine = writer.getDirectContent();			// for hr(line)		
			
				canvasLine.moveTo(50,573);		
				canvasLine.lineTo(550,573);           
				canvasLine.closePathStroke();	
		        
				PdfPTable marksTable = new PdfPTable(4);				

				PdfPCell srNoCell = new PdfPCell(new Paragraph("SR. NO.", descBoldFont));
				PdfPCell subjectsCell = new PdfPCell(new Paragraph("COURSE/S", descBoldFont));			
				PdfPCell creditsCell = new PdfPCell(new Paragraph("CREDIT/S", descBoldFont));	
				PdfPCell gradesCell = new PdfPCell(new Paragraph("GRADE/S", descBoldFont));

				srNoCell.setFixedHeight(30);
				srNoCell.setVerticalAlignment(Element.ALIGN_TOP);				
				subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
				creditsCell.setVerticalAlignment(Element.ALIGN_TOP);
				gradesCell.setVerticalAlignment(Element.ALIGN_TOP);

				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				subjectsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				creditsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				gradesCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				
				srNoCell.setBorder(0);
				subjectsCell.setBorder(0);
				subjectsCell.setBorder(0);
				creditsCell.setBorder(0);
				gradesCell.setBorder(0);
				
				srNoCell.setFixedHeight(15);
				subjectsCell.setFixedHeight(15);
				creditsCell.setFixedHeight(15);
				gradesCell.setFixedHeight(15);
				
				marksTable.addCell(srNoCell);
				marksTable.addCell(subjectsCell);
				marksTable.addCell(creditsCell);
				marksTable.addCell(gradesCell);
				marksTable.completeRow();
				
				int count = 1;
				float gradePoint = 0;
				float gradeCalculation = 0;
				float total = 0;
				int passCount = 0;
				int sumOftotalCredits = 0;
				for (EmbaPassFailBean e : passFailDataList) {		

					srNoCell = new PdfPCell(new Paragraph(count+"", descFont));
					if(exam_year.equalsIgnoreCase(e.getExamYear()) && getShortMonthName(exam_month).equalsIgnoreCase(e.getExamMonth()) )
					{
						subjectsCell = new PdfPCell(new Paragraph(e.getSubject().toUpperCase(), descFont));	
						gradesCell = new PdfPCell(new Paragraph(e.getGrade().toUpperCase(), descFont));
					}else {
						subjectsCell = new PdfPCell(new Paragraph(e.getSubject().toUpperCase(), descFont));	
						gradesCell = new PdfPCell(new Paragraph(e.getGrade().toUpperCase()  + " #", descFont));
					}

					
					gradePoint = Float.parseFloat(e.getPoints());
					
					if(1958 == e.getPrgm_sem_subj_id() || 1806 == e.getPrgm_sem_subj_id() ) { // Added by Abhay for Capstone Project Subject
						creditsCell = new PdfPCell(new Paragraph("20.00", descFont));
						sumOftotalCredits = sumOftotalCredits + 20;
						gradeCalculation = gradePoint * 20;
					}else if(1789 == e.getPrgm_sem_subj_id() ) { // Added by Abhay for Basics of Python Subject 
						creditsCell = new PdfPCell(new Paragraph("2.00", descFont));
						sumOftotalCredits = sumOftotalCredits + 2;	
						gradeCalculation = gradePoint * 2;
					}  else {
						creditsCell = new PdfPCell(new Paragraph("4.00", descFont));
						sumOftotalCredits = sumOftotalCredits + 4;
						gradeCalculation = gradePoint * 4;
					}
				
					total = total + gradeCalculation;
					
					srNoCell.setBorder(0);
					subjectsCell.setBorder(0);
					gradesCell.setBorder(0);
					creditsCell.setBorder(0);
	 
					subjectsCell.setPaddingLeft(5);
				
//					srNoCell.setFixedHeight(15);
//					subjectsCell.setFixedHeight(15);
//					gradesCell.setFixedHeight(15);
//					creditsCell.setFixedHeight(15);
					
					
					srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					creditsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					gradesCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					
					srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
					creditsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					gradesCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	
					
					marksTable.addCell(srNoCell);
					marksTable.addCell(subjectsCell);
					marksTable.addCell(creditsCell);
					marksTable.addCell(gradesCell);
					marksTable.completeRow();
					if(e.getIsPass().equals("Y")) {
						passCount++;
					}
					
					count++;
				}	
				
				
				marksTable.setWidthPercentage(100);
				marksTable.setSpacingBefore(15f);
				marksTable.setSpacingAfter(30f);
				float[] marksColumnWidths = {2.5f, 12f, 3.5f, 3.5f};
				marksTable.setWidths(marksColumnWidths);
				document.add(marksTable);

				
				int sum = passFailDataList.size();		
				
				float gpa = total/sumOftotalCredits;
				
				
				//float cgpa = calulateCGPA();
				PdfPTable footerTable1 = new PdfPTable(2);
				footerTable1.getDefaultCell().setBorder(0);				
				
			//	PdfPCell attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: "+bean.getValidityEndMonth()+"-"+bean.getValidityEndYear()+ " Examination.",font1));
				
				Chunk gradeHeadChunk = new Chunk("GRADE POINT AVERAGE (GPA): ", descFont);	
				Chunk gradeChunk = new Chunk(String.format ("%,.2f", gpa)+"", descBoldFont);
				Paragraph gradePara = new Paragraph();				
				gradePara.add(gradeHeadChunk);
				gradePara.add(gradeChunk);	
				
				PdfPCell attemptsCell = new PdfPCell(gradePara);				
				attemptsCell.setBorder(0);
				attemptsCell.setColspan(2);
				attemptsCell.setPaddingLeft(22);
				footerTable1.addCell(attemptsCell);
				footerTable1.completeRow();
				
				Chunk cgpaHeadChunk = new Chunk("CUMULATIVE GRADE POINT AVERAGE (CGPA): ", descFont);	
				
				Chunk cgpaChunk = new Chunk();
				Paragraph cgpaPara = new Paragraph();				

				if("1".equalsIgnoreCase(msBean.getSem())) {
					cgpaChunk = new Chunk(String.format ("%,.2f", gpa)+"", descBoldFont);
					cgpaPara.add(cgpaHeadChunk);
					cgpaPara.add(cgpaChunk);	
				}else {
				
				float cgradeCalculation = 0;
				float ctotal = 0;
				float cgradePoint = 0;
				int csumOftotalCredits = 0;
				 
				
				for (EmbaPassFailBean e : passFailDataListAllSem) {		
					cgradePoint = Float.parseFloat(e.getPoints());

					if(1958 == e.getPrgm_sem_subj_id() || 1806 == e.getPrgm_sem_subj_id() ) { // Added by Abhay for Capstone Project Subject
						csumOftotalCredits = csumOftotalCredits + 20;
						cgradeCalculation = cgradePoint * 20;
					}else if(1789 == e.getPrgm_sem_subj_id() ) { // Added by Abhay for Basics of Python Subject 
						csumOftotalCredits = csumOftotalCredits + 2;	
						cgradeCalculation = cgradePoint * 2;
					}  else {
						csumOftotalCredits = csumOftotalCredits + 4;
						cgradeCalculation = cgradePoint * 4;
					}
					
					ctotal = ctotal + cgradeCalculation;			
//					if(e.getIsPass().equals("Y")) {
//						passCount++;
//					}
//				 	
//					count++;
				}	
				
				

				float cgpa = ctotal/csumOftotalCredits;
		
				cgpaChunk = new Chunk(String.format ("%,.2f", cgpa)+"", descBoldFont);
				cgpaPara.add(cgpaHeadChunk);
				cgpaPara.add(cgpaChunk);	
		
				}

			
			
			
		       PdfPCell attemptsCell2 = new PdfPCell(cgpaPara);				
				attemptsCell2.setBorder(0);
				attemptsCell2.setColspan(2);
				attemptsCell2.setPaddingLeft(22);
				footerTable1.addCell(attemptsCell2);
				footerTable1.completeRow();
				
				footerTable1.setWidthPercentage(100);
				footerTable1.setSpacingBefore(25f);
				footerTable1.setSpacingAfter(5f);
				float[] footer1ColumnWidths = {3f, 3f};
				footerTable1.setWidths(footer1ColumnWidths);
				document.add(footerTable1);

				
				String remark = "";
				if(gpa >= 3.5 && sum == passCount) {
					remark = "PASS WITH DISTINCTION";
				}
				
				if(gpa < 3.5 && sum == passCount) {
					remark = "PASS";
				}
				
				if(sum != passCount){
					remark = "FAIL";
				}
				Chunk remarkHeadChunk = new Chunk("REMARK: ", descFont);	
				Chunk remarkChunk = new Chunk(remark, descBoldFont);
				Paragraph remarkPara = new Paragraph();				
				remarkPara.add(remarkHeadChunk);
				remarkPara.add(remarkChunk);	
				
				PdfPTable remarkFooterTable = new PdfPTable(2);
				remarkFooterTable.getDefaultCell().setBorder(0);
				
				PdfPCell remarkCell = new PdfPCell(remarkPara);				
				remarkCell.setBorder(0);
				remarkCell.setColspan(2);
				remarkCell.setPaddingLeft(22);
				remarkFooterTable.addCell(remarkCell);
				remarkFooterTable.completeRow();			    
		    
				
				remarkFooterTable.setWidthPercentage(100);
				remarkFooterTable.setSpacingBefore(25f);
				remarkFooterTable.setSpacingAfter(5f);
				document.add(remarkFooterTable);
			
				PdfPTable footerTable3 = new PdfPTable(2);
				footerTable3.getDefaultCell().setBorder(0);
				PdfPCell emptyCell = new PdfPCell();
				emptyCell.setBorder(0);
				emptyCell.setPaddingLeft(22);

				footerTable3.addCell(emptyCell);

				PdfPCell signatureCell = new PdfPCell();
				//signatureCell.addElement(signature);
				signatureCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
				//signature.setAlignment(Element.ALIGN_RIGHT);
				signatureCell.setBorder(0);
				signatureCell.setVerticalAlignment(Element.ALIGN_BOTTOM);

				footerTable3.addCell(signatureCell);
				footerTable3.completeRow();

				footerTable3.setWidthPercentage(100);
				footerTable3.setSpacingBefore(15f);
				//footerTable3.setSpacingAfter(15f);

				float[] footer3ColumnWidths = {6f, 2f};
				footerTable3.setWidths(footer3ColumnWidths);
				document.add(footerTable3);



				PdfPTable footerTable4 = new PdfPTable(2);
				footerTable4.getDefaultCell().setBorder(0);
				footerTable4.addCell(emptyCell);
				footerTable4.addCell(emptyCell);


				Chunk issuedHeadChunk = new Chunk("GRADE SHEET ISSUED ON: ", descFont);	
				Chunk issuedChunk = new Chunk(markSheetDate, descBoldFont);
				Paragraph issuedPara = new Paragraph();				
				issuedPara.add(issuedHeadChunk);
				issuedPara.add(issuedChunk);	
				
				PdfPCell issuedCell = new PdfPCell(issuedPara);
				issuedCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				issuedCell.setBorder(0);
				issuedCell.setPaddingLeft(22);

			//	issuedCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				issuedCell.setFixedHeight(15);
				
				//PdfPCell coeCell = new PdfPCell(new Paragraph("CONTROLLER OF EXAMINATIONS", font4));
				PdfPCell coeCell = new PdfPCell();
				
				coeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				coeCell.setBorder(0);
				coeCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				coeCell.setFixedHeight(15);
				coeCell.setPaddingLeft(22);
				
				footerTable4.addCell(issuedCell);
				footerTable4.addCell(coeCell);
				footerTable4.completeRow();

				footerTable4.setWidthPercentage(100);
				footerTable4.setSpacingBefore(15f);
				footerTable4.setSpacingAfter(18f);

				float[] footer4ColumnWidths = {3f, 3f};
				footerTable4.setWidths(footer4ColumnWidths);

				document.add(footerTable4);
				
				

				PdfPTable footerTable5 = new PdfPTable(1);
				footerTable5.getDefaultCell().setBorder(0);
				
				footerTable5.setWidthPercentage(100);
				footerTable5.setSpacingBefore(10f);
				//footerTable3.setSpacingAfter(15f);
				
				
				PdfPCell carryforwardCell = new PdfPCell(new Paragraph(" \"#\" INDICATES CARRY FORWARD SUBJECTS AND GRADES", font6));		
				carryforwardCell.setBorder(0);
				carryforwardCell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				carryforwardCell.setPaddingLeft(10);
				footerTable5.addCell(carryforwardCell);
				PdfPCell descCell = new PdfPCell(new Paragraph(" \"-\" INDICATES THAT GPA/CGPA WILL BE CALCULATED AFTER CLEARANCE OF EXISTING F GRADES", font6));		
				descCell.setBorder(0);
				descCell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				descCell.setPaddingLeft(10);
				
				footerTable5.addCell(descCell);
				

				document.add(footerTable5);				
				
				PdfPTable footerTable6 = new PdfPTable(1);
				footerTable6.getDefaultCell().setBorder(0);
				
				footerTable6.setWidthPercentage(100);
				footerTable6.setSpacingBefore(5f);
				footerTable6.setSpacingAfter(10f);				
				
				PdfPCell desc2Cell = new PdfPCell(new Paragraph("GRADE POINTS: A+ : 4; A : 3.75; A- : 3.5; B+ :3.25; B: 3; B- : 2.75; C+ : 2.5; C : 2.25; C- : 2; F : 0",footerBoldFont));		
				desc2Cell.setBorder(0);
				desc2Cell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				desc2Cell.setPaddingLeft(10);
				
				footerTable6.addCell(desc2Cell);				
				
				document.add(footerTable6);
				document.add(new Paragraph("Note:", font4));
				document.add(new Paragraph("1)  This statement of grades is generated through Student Portal of NMIMS Global Access School For Continuing Education.", font4));
				document.add(new Paragraph("2)  To verify authenticity of this marksheet, please refer to original marksheet issued by University.\n", font4));				
				document.add(new Phrase("\n"));
				
				document.newPage();

			} catch (DocumentException e) {
				
			} catch (Exception e) {
				
			}


		}
		document.close(); // no need to close PDFwriter?
		return msBean;

	}
	
	
	public EmbaMarksheetBean generateMarksheetPDFForMBAX(List<EmbaMarksheetBean> marksheetList,EmbaMarksheetBean msBean, 
			HashMap<String, String> programMap, HashMap<String, CenterExamBean> centersMap,String MARKSHEETS_PATH,
			List<EmbaPassFailBean> passFailDataListAllSapidsAllSems,HttpServletRequest request, Map<String, ProgramExamBean> modProgramMap) throws Exception {	
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		String markSheetDate = sdfr.format(new Date());
		String fileName = null;
		HashMap<String, CenterExamBean> centerNameCenterMap = generateCenterNameCenterMap(centersMap);//This will make another map with key as Name insteadof code/id
		Image signature = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/signature.jpg"));
		//Image signature = Image.getInstance("d:/signature.jpg");
		signature.scaleAbsolute(60, 42);
		String folderPath = "";
		if(marksheetList.size() > 0){
			EmbaMarksheetBean bean = marksheetList.get(0);
//			String studentId = bean.getSapid();	
			String year = bean.getExamYear();
			String month = bean.getExamMonth();
			fileName = bean.getProgram()  + "_"  + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
			fileName = fileName.toUpperCase();
			
			
			folderPath = MARKSHEETS_PATH + month + "-" + year + "/";
			
			File folder = new File(folderPath);
			if (!folder.exists()) {   
				folder.mkdirs();   
			}  
		}
		Document document = new Document(PageSize.A4);
		

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath+fileName));
//		request.getSession().setAttribute("fileName", folderPath+fileName);
		msBean.setFileName(folderPath+fileName);
		document.open();
		
		
		if(request.getAttribute("logoRequired")!=null && ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
			document.setMargins(60, 60, 17, 40);
		}  else {
			document.setMargins(60, 60, 100, 40);    
		} 
		Image logo=null;
		Paragraph blankLine = new Paragraph("\n");
		if(request.getAttribute("logoRequired")!=null && ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
					logo = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nm_logo2.jpg")); 
					logo.scaleAbsolute(164,59);    
					logo.setIndentationLeft(-17); 
		}
		generateSignatureSheetForMBAWX(document,marksheetList, programMap, centersMap,centerNameCenterMap );
		
		
		
		//document.setMargins(60, 60, 100, 100);
	//	Phrase watermark = new Phrase("Marksheet Downloaded from NGASCE Student Portal", new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
		PdfContentByte canvas = writer.getDirectContent();
//		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 300, 30);
		
			
		for (int i = 0; i < marksheetList.size(); i++) {
			
			String exam_year = null;
			String exam_month = null;
			String acad_year = null;
			String acad_month = null;
			
			
			
			EmbaMarksheetBean bean = marksheetList.get(i);
			
			
			exam_year = bean.getExamYear();
			exam_month = bean.getExamMonth();
			acad_year = bean.getYear();
			acad_month = bean.getMonth(); 
			

			try {
				if(request.getAttribute("logoRequired")!=null && ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
					document.add(logo);  
					document.add(blankLine);
				} else {
					//document.setMargins(50, 50, 170, 35);
				}
//				generateHeaderTable(document);	
				Chunk collegeNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", headerFont);	
				
				Paragraph collegeNameChunkPara = new Paragraph();
				collegeNameChunkPara.add(collegeNameChunk);
				collegeNameChunkPara.setAlignment(Element.ALIGN_CENTER);
				collegeNameChunkPara.setSpacingAfter(8);
				document.add(collegeNameChunkPara);
				
				Chunk stmtOfMarksChunk = new Chunk("STATEMENT OF GRADES", headerFont);		
				Paragraph stmtOfMarksPara = new Paragraph();				
				stmtOfMarksPara.add(stmtOfMarksChunk);
				stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
				stmtOfMarksPara.setSpacingAfter(8);			
				
				document.add(stmtOfMarksPara);

				PdfPTable headerTable = new PdfPTable(2); // 2 columns.
				headerTable.getDefaultCell().setBorder(0);
				
				String fatherName = "";
				String motherName = ""; 
				if(!StringUtils.isBlank(bean.getFatherName())) {
								
					if(bean.getFatherName().equals("null")) {
						fatherName = "";
					}else {
						fatherName = bean.getFatherName().trim().toUpperCase();
					}
								
				}
				if(!StringUtils.isBlank(bean.getMotherName())) {
					
					if(bean.getMotherName().equals("null")) {
						motherName = "";
					}else {
						motherName = bean.getMotherName().trim().toUpperCase();
					}
					
				}
				Chunk nameHeadChunk = new Chunk("NAME: ", descFont);	
				Chunk nameChunk = new Chunk((bean.getFirstName().trim().toUpperCase()+" "+bean.getLastName().trim()).toUpperCase().replace(".", " "), descBoldFont);
				Paragraph namePara = new Paragraph();				
				namePara.add(nameHeadChunk);
				namePara.add(nameChunk);
				
				Chunk rollNoHeadChunk = new Chunk("ROLL NO.: ", descFont);	
				Chunk rollNoChunk = new Chunk((bean.getSapid()).toUpperCase(), descBoldFont);
				Paragraph rollNoPara = new Paragraph();				
				rollNoPara.add(rollNoHeadChunk);
				rollNoPara.add(rollNoChunk);				
				
				PdfPCell rollNoCell = new PdfPCell(rollNoPara);
				rollNoCell.setBorder(0);
				rollNoCell.setPaddingRight(5);
				rollNoCell.setPaddingLeft(5);			
				
				PdfPCell nameCell = new PdfPCell(namePara);
				nameCell.setBorder(0);
				nameCell.setPaddingRight(5);
				nameCell.setPaddingLeft(5);
				headerTable.addCell(nameCell);				
				headerTable.addCell(rollNoCell);
				headerTable.completeRow();
								
				Chunk fatherNameHeadChunk = new Chunk("FATHER'S NAME: ", descFont);	
				Chunk fatherNameChunk = new Chunk((fatherName).toUpperCase(), descBoldFont);
				Paragraph fatherNamePara = new Paragraph();				
				fatherNamePara.add(fatherNameHeadChunk);
				fatherNamePara.add(fatherNameChunk);
				PdfPCell fatherNameCell = new PdfPCell(fatherNamePara);
				fatherNameCell.setBorder(0);
				fatherNameCell.setPaddingRight(5);
				fatherNameCell.setPaddingLeft(5);
				headerTable.addCell(fatherNameCell);
				headerTable.completeRow();
				
				Chunk motherNameHeadChunk = new Chunk("MOTHER'S NAME: ", descFont);	
				Chunk motherNameChunk = new Chunk((motherName).toUpperCase(), descBoldFont);
				Paragraph motherNamePara = new Paragraph();				
				motherNamePara.add(motherNameHeadChunk);
				motherNamePara.add(motherNameChunk);					
				
				PdfPCell motherNameCell = new PdfPCell(motherNamePara);
				motherNameCell.setBorder(0);
				motherNameCell.setPaddingBottom(5);
				motherNameCell.setPaddingRight(5);
				motherNameCell.setPaddingLeft(5);
				headerTable.addCell(motherNameCell);
				headerTable.completeRow();
				
				headerTable.setWidthPercentage(100);
				headerTable.setSpacingBefore(10f);
				headerTable.setSpacingAfter(5f);
				float[] columnWidths = {3.2f, 1.3f};
				headerTable.setWidths(columnWidths);
				document.add(headerTable);
				
				PdfPTable programTable = new PdfPTable(1);
				
				Chunk programHeadChunk = new Chunk("PROGRAMME: ", descFont);	
				Chunk programChunk = new Chunk(("MBA (EXECUTIVE) WITH SPECIALISATION IN BUSINESS ANALYTICS").toUpperCase(), descBoldFont);
				Paragraph programPara = new Paragraph();				
				programPara.add(programHeadChunk);
				programPara.add(programChunk);		
				
				PdfPCell programCell = new PdfPCell(programPara);	

				programCell.setBorder(0);
				programCell.setPaddingBottom(5);
				programCell.setPaddingRight(5);
                programCell.setPaddingLeft(5);
              
				programTable.addCell(programCell);
				programTable.completeRow();				
				programTable.setWidthPercentage(100);
				programTable.setSpacingAfter(5f);
				float[] programcolumnWidths = {5f};
				programTable.setWidths(programcolumnWidths);
				document.add(programTable);
				
				//Add Mode of Delivery - START
				ProgramExamBean modProgram = modProgramMap.get(bean.getProgram());
				String mod = modProgram.getModeOfLearning();
				PdfPTable modTable = new PdfPTable(2);
				modTable.getDefaultCell().setBorder(0);
				Chunk modHeadChunk = new Chunk("MODE OF DELIVERY: ", descFont);	
				Chunk modChunk = new Chunk((mod).toUpperCase(), descBoldFont);
				Paragraph modPara = new Paragraph();				
				modPara.add(modHeadChunk);
				modPara.add(modChunk);
				PdfPCell modCell = new PdfPCell(modPara);
				modCell.setBorder(0);
				modCell.setPaddingRight(5);
				modCell.setPaddingLeft(5);
				modTable.addCell(modCell);
				modTable.completeRow();
				modTable.setWidthPercentage(100);
				modTable.setSpacingAfter(5f);
				float[] modcolumnWidths = {3.2f, 1.3f};
				modTable.setWidths(modcolumnWidths);
				document.add(modTable);
				//Add Mode of Delivery - END
				
				PdfPTable termTable = new PdfPTable(2);
				Chunk termDurationHeadChunk = new Chunk("TERM DURATION: ", descFont);	
				Chunk termDurationChunk = new Chunk((bean.getMonth() 
						+" "+bean.getYear()+"-"+bean.getExamMonth()+" "+bean.getExamYear()).toUpperCase(), descBoldFont);
				Paragraph termDurationPara = new Paragraph();				
				termDurationPara.add(termDurationHeadChunk);
				termDurationPara.add(termDurationChunk);	
				
				PdfPCell rgstnCell = new PdfPCell(new Paragraph(termDurationPara));		
				
		

				Chunk termHeadChunk = new Chunk("TERM: ", descFont);	
				Chunk termChunk = new Chunk(convertIntegerToRoman(Integer.parseInt(bean.getSem())), descBoldFont);

				Paragraph termPara = new Paragraph();				
				termPara.add(termHeadChunk);
				termPara.add(termChunk);	
				
				PdfPCell semCell = new PdfPCell(termPara);
				rgstnCell.setBorder(0);
				semCell.setBorder(0);
				semCell.setPaddingLeft(5);
				rgstnCell.setPaddingLeft(5);
				termTable.addCell(rgstnCell);
				termTable.addCell(semCell);
				termTable.completeRow();			
				
				termTable.setWidthPercentage(100);
				//termTable.setSpacingBefore(10f);
				termTable.setSpacingAfter(12f);
				
				float[] termColumnWidths = {3.2f, 1.3f};
				termTable.setWidths(termColumnWidths);
				document.add(termTable);
				
				PdfContentByte canvasLine = writer.getDirectContent();			// for hr(line)		
			
				canvasLine.moveTo(50,573);		
				canvasLine.lineTo(550,573);           
				canvasLine.closePathStroke();	
		        
				PdfPTable marksTable = new PdfPTable(4);				

				PdfPCell srNoCell = new PdfPCell(new Paragraph("SR. NO.", descBoldFont));
				PdfPCell subjectsCell = new PdfPCell(new Paragraph("COURSE/S", descBoldFont));			
				PdfPCell creditsCell = new PdfPCell(new Paragraph("CREDIT/S", descBoldFont));	
				PdfPCell gradesCell = new PdfPCell(new Paragraph("GRADE/S", descBoldFont));

				srNoCell.setFixedHeight(30);
				srNoCell.setVerticalAlignment(Element.ALIGN_TOP);				
				subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
				creditsCell.setVerticalAlignment(Element.ALIGN_TOP);
				gradesCell.setVerticalAlignment(Element.ALIGN_TOP);

				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				subjectsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				creditsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				gradesCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				
				srNoCell.setBorder(0);
				subjectsCell.setBorder(0);
				subjectsCell.setBorder(0);
				creditsCell.setBorder(0);
				gradesCell.setBorder(0);
				
				srNoCell.setFixedHeight(15);
				subjectsCell.setFixedHeight(15);
				creditsCell.setFixedHeight(15);
				gradesCell.setFixedHeight(15);
				
				marksTable.addCell(srNoCell);
				marksTable.addCell(subjectsCell);
				marksTable.addCell(creditsCell);
				marksTable.addCell(gradesCell);
				marksTable.completeRow();
				
				int count = 1;
				float gradePoint = 0;
				float gradeCalculation = 0;
				float total = 0;
				int passCount = 0;
//				int sum = passFailDataList.size();
				int sum = 0;
				int sumOftotalCredits = 0;
				List<EmbaPassFailBean> passFailDataListSapidAllSem = new ArrayList<EmbaPassFailBean>(); 

				for (EmbaPassFailBean e : passFailDataListAllSapidsAllSems) {			

					if(bean.getSapid().equals(e.getSapid())) {
						if(e.getPoints() != null && !e.getPoints().trim().isEmpty() && Integer.parseInt(e.getSem()) <= Integer.parseInt(bean.getSem())) {
							passFailDataListSapidAllSem.add(e);
						}
						if(e.getPoints() != null && !e.getPoints().trim().isEmpty()  && bean.getSem().equals(e.getPsssem())) {

							e.setSem(e.getPsssem());			
							srNoCell = new PdfPCell(new Paragraph(count+"", descFont));
							if(exam_year.equalsIgnoreCase(e.getExamYear()) && exam_month.equalsIgnoreCase(e.getExamMonth()) )
							{
								subjectsCell = new PdfPCell(new Paragraph(e.getSubject().toUpperCase(), descFont));	
								gradesCell = new PdfPCell(new Paragraph(e.getGrade().toUpperCase(), descFont));
							}else {
								subjectsCell = new PdfPCell(new Paragraph(e.getSubject().toUpperCase(), descFont));	
								gradesCell = new PdfPCell(new Paragraph(e.getGrade().toUpperCase()  + " #", descFont));
							}
							
							

							gradePoint = Float.parseFloat(e.getPoints());
							
							if(1958 == e.getPrgm_sem_subj_id() || 1806 == e.getPrgm_sem_subj_id() ) { // Added by Abhay for Capstone Project Subject
								creditsCell = new PdfPCell(new Paragraph("20.00", descFont));
								sumOftotalCredits = sumOftotalCredits + 20;
								gradeCalculation = gradePoint * 20;
							}else if(1789 == e.getPrgm_sem_subj_id() ) { // Added by Abhay for Basics of Python Subject 
								creditsCell = new PdfPCell(new Paragraph("2.00", descFont));
								sumOftotalCredits = sumOftotalCredits + 2;	
								gradeCalculation = gradePoint * 2;
							}  else {
								creditsCell = new PdfPCell(new Paragraph("4.00", descFont));
								sumOftotalCredits = sumOftotalCredits + 4;
								gradeCalculation = gradePoint * 4;
							}
						
							total = total + gradeCalculation;
							
							srNoCell.setBorder(0);
							subjectsCell.setBorder(0);
							gradesCell.setBorder(0);
							creditsCell.setBorder(0);
							
							subjectsCell.setPaddingLeft(5);
						
//							srNoCell.setFixedHeight(15);
//							subjectsCell.setFixedHeight(15);
//							gradesCell.setFixedHeight(15);
//							creditsCell.setFixedHeight(15);
							
							
							srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							creditsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							gradesCell.setHorizontalAlignment(Element.ALIGN_LEFT);
							
							srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
							creditsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							gradesCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			
							
							marksTable.addCell(srNoCell);
							marksTable.addCell(subjectsCell);
							marksTable.addCell(creditsCell);
							marksTable.addCell(gradesCell);
							marksTable.completeRow();
							if(e.getIsPass().equals("Y")) {
								passCount++;
							}
							
							count++;
							sum++;
							
						}
						
					}
					
				}		 
				marksTable.setWidthPercentage(100);
				marksTable.setSpacingBefore(15f);
				marksTable.setSpacingAfter(30f);
				float[] marksColumnWidths = {2.5f, 12f, 3.5f, 3.5f};
				marksTable.setWidths(marksColumnWidths);
				document.add(marksTable);

				
						
//				int sumOftotalCredits = sum * 4;
				float gpa = total/sumOftotalCredits;
				
				PdfPTable footerTable1 = new PdfPTable(2);
				footerTable1.getDefaultCell().setBorder(0);				
				
			//	PdfPCell attemptsCell = new PdfPCell(new Paragraph("End of Program Validity: "+bean.getValidityEndMonth()+"-"+bean.getValidityEndYear()+ " Examination.",font1));
				
				Chunk gradeHeadChunk = new Chunk("GRADE POINT AVERAGE (GPA): ", descFont);	
				Chunk gradeChunk = new Chunk(String.format ("%,.2f", gpa)+"", descBoldFont);
				Paragraph gradePara = new Paragraph();				
				gradePara.add(gradeHeadChunk);
				gradePara.add(gradeChunk);	
				
				PdfPCell attemptsCell = new PdfPCell(gradePara);				
				attemptsCell.setBorder(0);
				attemptsCell.setColspan(2);
				attemptsCell.setPaddingLeft(22);
				footerTable1.addCell(attemptsCell);
				footerTable1.completeRow();
				
				
				Chunk cgpaHeadChunk = new Chunk("CUMULATIVE GRADE POINT AVERAGE (CGPA): ", descFont);	
				
				Chunk cgpaChunk = new Chunk();
				Paragraph cgpaPara = new Paragraph();	
				
				if("1".equalsIgnoreCase(bean.getSem())) {
					cgpaChunk = new Chunk(String.format ("%,.2f", gpa)+"", descBoldFont);
					cgpaPara.add(cgpaHeadChunk);
					cgpaPara.add(cgpaChunk);	
				}else {
				
				float cgradeCalculation = 0;
				float ctotal = 0;
				float cgradePoint = 0;
				int csumOftotalCredits = 0;
				 
				for (EmbaPassFailBean e : passFailDataListSapidAllSem) {		
					cgradePoint = Float.parseFloat(e.getPoints());

					if(1958 == e.getPrgm_sem_subj_id() || 1806 == e.getPrgm_sem_subj_id() ) { // Added by Abhay for Capstone Project Subject
						csumOftotalCredits = csumOftotalCredits + 20;
						cgradeCalculation = cgradePoint * 20;
					}else if(1789 == e.getPrgm_sem_subj_id() ) { // Added by Abhay for Basics of Python Subject 
						csumOftotalCredits = csumOftotalCredits + 2;	
						cgradeCalculation = cgradePoint * 2;
					}  else {
						csumOftotalCredits = csumOftotalCredits + 4;
						cgradeCalculation = cgradePoint * 4;
					}
					
					ctotal = ctotal + cgradeCalculation;

				}	
				
				float cgpa = ctotal/csumOftotalCredits;
		
				cgpaChunk = new Chunk(String.format ("%,.2f", cgpa)+"", descBoldFont);
				cgpaPara.add(cgpaHeadChunk);
				cgpaPara.add(cgpaChunk);	
				}		
			
			    
		       PdfPCell attemptsCell2 = new PdfPCell(cgpaPara);				
				attemptsCell2.setBorder(0);
				attemptsCell2.setColspan(2);
				attemptsCell2.setPaddingLeft(22);
				footerTable1.addCell(attemptsCell2);
				footerTable1.completeRow();
				
				footerTable1.setWidthPercentage(100);
				footerTable1.setSpacingBefore(25f);
				footerTable1.setSpacingAfter(5f);
				float[] footer1ColumnWidths = {3f, 3f};
				footerTable1.setWidths(footer1ColumnWidths);
				document.add(footerTable1);

				
				String remark = "";
				if(gpa >= 3.5 && sum == passCount) {
					remark = "PASS WITH DISTINCTION";
				}
				
				if(gpa < 3.5 && sum == passCount) {
					remark = "PASS";
				}
				
				if(sum != passCount){
					remark = "FAIL";
				}
				Chunk remarkHeadChunk = new Chunk("REMARK: ", descFont);	
				Chunk remarkChunk = new Chunk(remark, descBoldFont);
				Paragraph remarkPara = new Paragraph();				
				remarkPara.add(remarkHeadChunk);
				remarkPara.add(remarkChunk);	
				
				PdfPTable remarkFooterTable = new PdfPTable(2);
				remarkFooterTable.getDefaultCell().setBorder(0);
				
				PdfPCell remarkCell = new PdfPCell(remarkPara);				
				remarkCell.setBorder(0);
				remarkCell.setColspan(2);
				remarkCell.setPaddingLeft(22);
				remarkFooterTable.addCell(remarkCell);
				remarkFooterTable.completeRow();			    
		    
				
				remarkFooterTable.setWidthPercentage(100);
				remarkFooterTable.setSpacingBefore(25f);
				remarkFooterTable.setSpacingAfter(5f);
				document.add(remarkFooterTable);
			
				PdfPTable footerTable3 = new PdfPTable(2);
				footerTable3.getDefaultCell().setBorder(0);
				PdfPCell emptyCell = new PdfPCell();
				emptyCell.setBorder(0);
				emptyCell.setPaddingLeft(22);

				footerTable3.addCell(emptyCell);

				PdfPCell signatureCell = new PdfPCell();
				signatureCell.addElement(signature);
				signatureCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
				//signature.setAlignment(Element.ALIGN_RIGHT);
				signatureCell.setBorder(0);
				signatureCell.setVerticalAlignment(Element.ALIGN_BOTTOM);

				footerTable3.addCell(signatureCell);
				footerTable3.completeRow();

				footerTable3.setWidthPercentage(100);
				footerTable3.setSpacingBefore(15f);
				//footerTable3.setSpacingAfter(15f);

				float[] footer3ColumnWidths = {6f, 2f};
				footerTable3.setWidths(footer3ColumnWidths);
				document.add(footerTable3);



				PdfPTable footerTable4 = new PdfPTable(2);
				footerTable4.getDefaultCell().setBorder(0);
				footerTable4.addCell(emptyCell);
				footerTable4.addCell(emptyCell);


				Chunk issuedHeadChunk = new Chunk("GRADE SHEET ISSUED ON: ", descFont);	
				Chunk issuedChunk = new Chunk(markSheetDate, descBoldFont);
				Paragraph issuedPara = new Paragraph();				
				issuedPara.add(issuedHeadChunk);
				issuedPara.add(issuedChunk);	
				
				PdfPCell issuedCell = new PdfPCell(issuedPara);
				issuedCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				issuedCell.setBorder(0);
				issuedCell.setPaddingLeft(22);

			//	issuedCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				issuedCell.setFixedHeight(15);
				
				PdfPCell coeCell = new PdfPCell(new Paragraph("CONTROLLER OF EXAMINATIONS", font4));
				coeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				coeCell.setBorder(0);
				coeCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				coeCell.setFixedHeight(15);
				coeCell.setPaddingLeft(22);
				
				footerTable4.addCell(issuedCell);
				footerTable4.addCell(coeCell);
				footerTable4.completeRow();

				footerTable4.setWidthPercentage(100);
				footerTable4.setSpacingBefore(10f);
				footerTable4.setSpacingAfter(10f);

				float[] footer4ColumnWidths = {3f, 3f};
				footerTable4.setWidths(footer4ColumnWidths);

				document.add(footerTable4);
				
				

				PdfPTable footerTable5 = new PdfPTable(1);
				footerTable5.getDefaultCell().setBorder(0);
				
				footerTable5.setWidthPercentage(100);
				footerTable5.setSpacingBefore(10f);
				//footerTable3.setSpacingAfter(15f);
				
				PdfPCell carryforwardCell = new PdfPCell(new Paragraph(" \"#\" INDICATES CARRY FORWARD SUBJECTS AND GRADES", font6));		
				carryforwardCell.setBorder(0);
				carryforwardCell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				carryforwardCell.setPaddingLeft(25);
				footerTable5.addCell(carryforwardCell);
				
				
				PdfPCell descCell = new PdfPCell(new Paragraph(" \"-\" INDICATES THAT GPA/CGPA WILL BE CALCULATED AFTER CLEARANCE OF EXISTING F GRADES", font6));		
				descCell.setBorder(0);
				descCell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				descCell.setPaddingLeft(25);
				
				footerTable5.addCell(descCell);
				

				document.add(footerTable5);				
				
				PdfPTable footerTable6 = new PdfPTable(1);
				footerTable6.getDefaultCell().setBorder(0);
				
				footerTable6.setWidthPercentage(100);
				footerTable6.setSpacingBefore(5f);
				footerTable6.setSpacingAfter(10f);			
				
				PdfPCell desc2Cell = new PdfPCell(new Paragraph("GRADE POINTS: A+ : 4; A : 3.75; A- : 3.5; B+ :3.25; B: 3; B- : 2.75; C+ : 2.5; C : 2.25; C- : 2; F : 0",footerBoldFont));		
				desc2Cell.setBorder(0);
				desc2Cell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				desc2Cell.setPaddingLeft(25);
				
				footerTable6.addCell(desc2Cell);				
				
				document.add(footerTable6);
		
				
				PdfPTable footerTable7 = new PdfPTable(1);
				footerTable7.getDefaultCell().setBorder(0);
				
				footerTable7.setWidthPercentage(100);
				footerTable7.setSpacingBefore(25f);
				footerTable7.setSpacingAfter(25f);
				
				PdfPCell desc3Cell = new PdfPCell(new Paragraph("PREPARED BY: ",footerBoldFont));		
				desc3Cell.setBorder(0);
				desc3Cell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				desc3Cell.setPaddingLeft(45);
				
				footerTable7.addCell(desc3Cell);				
				
				document.add(footerTable7);
				
				PdfPTable footerTable8 = new PdfPTable(2);
				footerTable8.getDefaultCell().setBorder(0);
				
				footerTable8.setWidthPercentage(100);
				footerTable8.setSpacingBefore(5f);
				
				PdfPCell desc4Cell = new PdfPCell(new Paragraph("CHECKED BY: ",footerBoldFont));		
				desc4Cell.setBorder(0);
				desc4Cell.setVerticalAlignment(Element.ALIGN_BOTTOM);			
				desc4Cell.setPaddingLeft(45);
				
				footerTable8.addCell(desc4Cell);
//				footerTable8.addCell(desc3Cell);
				footerTable8.completeRow();
				float[] footer8ColumnWidths = {3f, 3f};
				footerTable8.setWidths(footer8ColumnWidths);
				
				
				document.add(footerTable8);				
				
				document.newPage();
			} catch (DocumentException e) {
				
			} catch (Exception e) {
				
			}
		}
		document.close(); // no need to close PDFwriter?
		return msBean;

	}
	
	

	private String convertIntegerToRoman(int num) {

        int[] values = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String[] romanLiterals = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};

        StringBuilder roman = new StringBuilder();

        for(int i=0;i<values.length;i++) {
            while(num >= values[i]) {
                num -= values[i];
                roman.append(romanLiterals[i]);
            }
        }
		return roman.toString();
	}

	private float getCgpaByTermForMbaX(MBAWXExamResultForSubject studentMarks, ExamsAssessmentsDAO examsAssessmentsDAO) {
		
		int currentTerm = Integer.parseInt(studentMarks.getTerm());		
		float t = 0;
		int s = 0;
		float cgpa = 0;
		try {
			
			for (int i = currentTerm; i > 0; --i) {	//decrement for loop for sem 3,2,1
				
				List<EmbaPassFailBean> passFailDataList = examsAssessmentsDAO.getMbaXPassFailBySapidTermMonthYear(studentMarks.getSapid(),Integer.toString(i),studentMarks.getAcadsMonth(),studentMarks.getAcadsYear());	
				
				float gpaTotal = getGpaTotalByPassFailDataForMbaX(passFailDataList);
				int sumOfTotalCredits = getSumOfTotalCreditsForMbaX(passFailDataList);
			
				t = t+gpaTotal;
				s = s + sumOfTotalCredits;
				
			}
			 cgpa = t/s; 
		}catch(Exception e) {
						
		}
		return cgpa;
	}

	private int getSumOfTotalCreditsForMbaX(List<EmbaPassFailBean> passFailDataList) {
		int sum = passFailDataList.size();		
		int sumOftotalCredits = sum * 4;		
		return sumOftotalCredits;
	}

	private float getGpaTotalByPassFailDataForMbaX(List<EmbaPassFailBean> passFailDataList) {
		float gradePoint = 0;
		float gradeCalculation = 0;		
		float total = 0;
		for (EmbaPassFailBean e : passFailDataList) {		
			
			gradePoint = Float.parseFloat(e.getPoints());
			gradeCalculation = gradePoint * 4;
			total = total + gradeCalculation;	
		}
		return total;
	}
	
}
