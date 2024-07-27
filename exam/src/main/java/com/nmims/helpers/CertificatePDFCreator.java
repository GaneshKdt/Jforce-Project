package com.nmims.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentDAO;
import com.nmims.services.ModeOfDeliveryService;
import com.nmims.util.ImageRotationUtil;

@Component
public class CertificatePDFCreator {
	
	@Autowired
	AmazonS3Helper amazonHelper;
	
	private static final String[] programs =new String[] {"ACBM","ACDM","ACOM","ACWM","CBA","CBM","CCC","CDM","C-DMA","CGM","CITM - DB","CITM - ES","CITM - ET","COM","CPBM","CPM","CP-ME","CP-WL","CWM","C-SEM","C-SEM & DMA","C-SEM & SMM","C-SMM","C-SMM & DMA","PD - DM","PC-DM","PD - WM","PDDM","MBA - WX","MBA - X","M.Sc. (AI & ML Ops)","M.Sc. (AI)","M.Sc. (AI) - DL","M.Sc. (AI) - DO","PC-DS","PD-DS","M.Sc. (App. Fin.)"};
	private static final Set<String> DIRECTOR_SIGNATURE_ELIGIBLE_PROGRAMS=new HashSet<>(Arrays.asList(programs));	
	
	@Value( "${AWS_CERTIFICATES_BUCKET}" )
	String AWS_CERTIFICATES_BUCKET;
	
	@Autowired(required = false)
	ApplicationContext act;
	
	@Autowired
	StudentDAO studentDaos;
	
	@Autowired
	FileUploadHelper fileUploadHelper;
	
	@Autowired
	ModeOfDeliveryService modService;
	
	private static final Logger certificate = (Logger) LoggerFactory.getLogger("finalCertificate");
		
	Font font8 = new Font(Font.FontFamily.HELVETICA, 9);
	Font font8Bold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);

	Font font10 = new Font(Font.FontFamily.HELVETICA, 11);
	Font font10Bold = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
	Font font11Bold = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
	Font font12 = new Font(Font.FontFamily.HELVETICA, 13);
	Font font12Bold = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);

	Font font14 = new Font(Font.FontFamily.HELVETICA, 15);
	Font font14Bold = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);

	Font font15 = new Font(Font.FontFamily.HELVETICA, 16);
	Font font15Bold = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);

	Font font16 = new Font(Font.FontFamily.HELVETICA, 17);
	Font font16Bold = new Font(Font.FontFamily.HELVETICA, 17, Font.BOLD);

	Font font18 = new Font(Font.FontFamily.HELVETICA, 19);
	Font font18Bold = new Font(Font.FontFamily.HELVETICA, 19, Font.BOLD);
	Font font7 = new Font(Font.FontFamily.HELVETICA, 7);
	
	private final List<String> MASTER_PROGRAM_TYPE = Arrays.asList("Master","Bachelor Programs"); //Add `degree` word only for Masters and Bachelor Programs
	private final static List<String> MODULAR_PDDM_MASTERKEYS = Arrays.asList("142","143","144","145","146","147","148","149");	
	private static final List<String> PG_AND_MBA_DISTANCE_MASTERKEYS = Arrays.asList("42","44","47","49","51","53","55","57","59","62","64","65","66","69","99","72","73","74","76","71","84","80","85","81","83","91","87","88","82","79","52","54","68","70","46","48","50","43","45","100","61","58","60","75","77","56","63","114","115","117","120","129","130","132","133","134","135","136","137","138","139","140","141","161");
	
	Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 11);
	
	private static final String MODE_OF_DELIVERY = "ODL";
	private static final String LEARNING_SUPPORT_CENTRE = "-----";
	private static final String EXAMINATION_CENTRE = "HQ - SVKM\'S NMIMS Deemed-to-be UNIVERSITY \n V.L. Mehta Road, VileParle (West), Mumbai 400056";
	
	/*
	 * public String
	 * generateCertificateAndReturnCertificateNumber(List<MarksheetBean>
	 * studentForSRList, HashMap<String,String>
	 * mapOfSapIDAndResultDate,HttpServletRequest request,HashMap<String,
	 * ProgramBean> programAllDetailsMap, HashMap<String, CenterBean> centersMap,
	 * String MARKSHEETS_PATH,String SERVER_PATH,String STUDENT_PHOTOS_PATH) throws
	 * Exception {
	 * 
	 * 
	 * 
	 * String certificateUniqueNumber =""; SimpleDateFormat sdfr = new
	 * SimpleDateFormat("dd-MMM-yyyy"); SimpleDateFormat sdfrForCertNum = new
	 * SimpleDateFormat("ddMM"); String certificateDate = sdfr.format(new Date());
	 * String certificateDateForNumber = sdfrForCertNum.format(new Date()); String
	 * fileName = null;
	 * 
	 * //Image signature = Image.getInstance(new URL(
	 * "https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/director-Signature.jpg"
	 * )); Image signature = Image.getInstance(new
	 * URL(SERVER_PATH+"exam/resources_2015/images/director-Signature.jpg")); Image
	 * signature =
	 * Image.getInstance(SERVER_PATH+"exam/resources_2015/images/vc_signature.jpg");
	 * //Image signature = Image.getInstance("E:/director-Signature.jpg");
	 * 
	 * //Image signatureOfSAS = Image.getInstance("D:/director-Signature.jpg");
	 * //Image logoSAS = Image.getInstance("E:/SAS-109.jpg"); // Image logoSAS =
	 * Image.getInstance("D:/SAS-109.jpg"); signature.scaleAbsolute(90, 42); String
	 * folderPath = ""; fileName = "Certificate_" + certificateDate + "_" +
	 * RandomStringUtils.randomAlphanumeric(5) + ".pdf"; folderPath =
	 * MARKSHEETS_PATH + "Certificates/";
	 * 
	 * File folder = new File(folderPath); if (!folder.exists()) { folder.mkdirs();
	 * }
	 * 
	 * Document document = new Document(PageSize.A4);
	 * if(request.getAttribute("logoRequired")==null || ((String)
	 * request.getAttribute("logoRequired")).equalsIgnoreCase("N")) {
	 * document.setMargins(50, 50, 176, 35); //document.setMargins(50, 50, 70, 35);
	 * }else { document.setMargins(50, 50, 45, 35); }
	 * 
	 * 
	 * PdfWriter writer = PdfWriter.getInstance(document, new
	 * FileOutputStream(folderPath+fileName));
	 * request.getSession().setAttribute("fileName", folderPath+fileName);
	 * document.open(); PdfPTable headerTable = new PdfPTable(1);
	 * if(request.getAttribute("logoRequired")!=null && ((String)
	 * request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) { Image logo =
	 * null; logo = Image.getInstance(new URL(
	 * "https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/nmims_logo_new.jpg"
	 * )); logo.scaleAbsolute(142,40); logo.setAlignment(Element.ALIGN_CENTER);
	 * 
	 * PdfPCell logoCell = new PdfPCell(); logoCell.addElement(logo);
	 * logoCell.setHorizontalAlignment(Element.ALIGN_CENTER); logoCell.setBorder(0);
	 * headerTable.addCell(logoCell); headerTable.completeRow();
	 * headerTable.setWidthPercentage(100); headerTable.setSpacingBefore(10);
	 * headerTable.setSpacingAfter(10); }
	 * 
	 * Paragraph blankLine = new Paragraph("\n");
	 * 
	 * for (MarksheetBean student : studentForSRList) { //
	 * if(request.getAttribute("logoRequired")!=null && ((String)
	 * request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
	 * document.add(headerTable); } String date =
	 * mapOfSapIDAndResultDate.get(student.getSapid()); String declareDate = "";
	 * SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd"); Format
	 * formatterMonth = new SimpleDateFormat("MMMM"); Format formatterYear = new
	 * SimpleDateFormat("yyyy"); Format formatterDay = new SimpleDateFormat("dd");
	 * if(date!=null){ declareDate = formatterMonth.format(s.parse(date)) +" "+
	 * formatterDay.format(s.parse(date))+", "+formatterYear.format(s.parse(date));
	 * }else{ declareDate = ""; }
	 * 
	 * Paragraph svkmManagement = new
	 * Paragraph("We, the Chancellor, Vice Chancellor and Members of Board of Management of SVKM's Narsee Monjee Institute of Management Studies,"
	 * ); svkmManagement.setAlignment(Element.ALIGN_CENTER);
	 * document.add(svkmManagement);
	 * 
	 * Paragraph certifyThatPara = new Paragraph("certify that");
	 * certifyThatPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(certifyThatPara); document.add(blankLine);
	 * 
	 * if("Online".equals(student.getExamMode())){ //Photo not required for Offline
	 * exam. Image studentPhoto = null; PdfPTable studentPhotoTable = new
	 * PdfPTable(1); try { studentPhoto = Image.getInstance(new
	 * URL(student.getImageUrl()));
	 * 
	 * } catch (Exception e) { //If not .jpg, try .jpeg try { studentPhoto =
	 * Image.getInstance(STUDENT_PHOTOS_PATH +student.getSapid()+".jpg");
	 * 
	 * } catch (Exception e2) { //IF this is also not found, then take photo from
	 * Admission system try { studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH
	 * +student.getSapid()+".jpeg"); } catch (Exception e1) { // TODO Auto-generated
	 * catch block throw new
	 * Exception("Error in getting image. Error : "+e.getMessage()); }
	 * //mailer.sendPhotoNotFoundEmail(student); } } final float heightOfImageInCm =
	 * 25; final float pointsValueHeight =
	 * com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);
	 * 
	 * final float widthOfImageInCm = 20; final float pointsValueWidth =
	 * com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);
	 * 
	 * studentPhoto.scaleAbsolute( pointsValueWidth,pointsValueHeight); PdfPCell
	 * studentPhotoCell = null; if(studentPhoto == null){ //In case URL was
	 * malformed or was not found studentPhotoCell = new PdfPCell(); }else{
	 * studentPhotoCell = new PdfPCell(studentPhoto); }
	 * 
	 * studentPhotoCell.setRowspan(6); studentPhotoCell.setBorder(0);
	 * studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * studentPhotoTable.addCell(studentPhotoCell);
	 * studentPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * document.add(studentPhotoTable); }
	 * 
	 * //Added to make first letter capital and rest of them small//
	 * 
	 * String fullNameOfStudent =
	 * student.getFirstName().toUpperCase()+" "+student.getLastName().toUpperCase().
	 * replace(".", " "); String fullNameOfFather =
	 * student.getFatherName().toUpperCase(); String fullNameOfMother =
	 * student.getMotherName().toUpperCase(); String fullNameOfHusband = ""; try {
	 * fullNameOfHusband =student.getHusbandName().toUpperCase(); } catch (Exception
	 * e) { }
	 * 
	 * 
	 * Paragraph studentNamePara = new Paragraph(fullNameOfStudent, font18Bold);
	 * studentNamePara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(studentNamePara); document.add(blankLine);
	 * 
	 * 
	 * 
	 * Paragraph sonDaughterOfPara =null;
	 * if("Spouse".equals(student.getAdditionalInfo1()) &&
	 * "Female".equals(student.getGender())){
	 * 
	 * sonDaughterOfPara = new Paragraph("(Wife of Shri. " + fullNameOfHusband
	 * +" and Daughter of Smt. "+fullNameOfMother+" )"); }else
	 * if("Parent".equals(student.getAdditionalInfo1()) ||
	 * student.getAdditionalInfo1()==null){ sonDaughterOfPara = new
	 * Paragraph("(Son/Daughter of Shri. " + fullNameOfFather +" and Smt. " +
	 * fullNameOfMother + " )"); }
	 * 
	 * sonDaughterOfPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(sonDaughterOfPara); document.add(blankLine); String program =
	 * student.getProgram(); ProgramBean programDetails =
	 * programAllDetailsMap.get(program); String key =
	 * student.getProgram()+"-"+student.getPrgmStructApplicable(); ProgramBean
	 * programDetails = programAllDetailsMap.get(key); String programname =
	 * programDetails.getProgramname(); String programType =
	 * programDetails.getProgramType(); List<String> programTypes =new
	 * ArrayList(Arrays.asList("Professional Program"
	 * ,"Certificate","Certificate Programs","Bachelor Programs")); String
	 * learningMode="Distance Learning mode"; if(programTypes.contains(programType))
	 * { learningMode="Online Learning mode"; }
	 * if(programDetails.getProgram().equalsIgnoreCase("MBA - WX") ||
	 * programDetails.getProgram().equalsIgnoreCase("MBA - X") ) {
	 * programType="degree"; learningMode="Online Learning mode"; }
	 * if(programType.equalsIgnoreCase("Bachelor Programs") ||
	 * programType.equalsIgnoreCase("Executive Programs") ||
	 * programType.equalsIgnoreCase("PG Programs") ||
	 * programType.equalsIgnoreCase("Post Graduate Diploma Programs") ||
	 * programType.equalsIgnoreCase("Post Graduate Diploma") ||
	 * programType.equalsIgnoreCase("Master") ) { programType="degree"; }else
	 * if(programType.equalsIgnoreCase("Certificate Programs") ||
	 * programType.equalsIgnoreCase("Modular Program") ||
	 * programType.equalsIgnoreCase("Certificate") ) { programType="certificate";
	 * }else if(programType.equalsIgnoreCase("Professional Program") ||
	 * programType.equalsIgnoreCase("Diploma")) { programType="diploma"; }else {
	 * programType="degree"; } //Paragraph programDurationPara = new
	 * Paragraph("has been examined and found qualified for the "+
	 * programDetails.getProgramDuration() + " " +
	 * programDetails.getProgramDurationUnit()); Paragraph programDurationPara = new
	 * Paragraph(""); if("EPBM".equalsIgnoreCase(student.getProgram()) ||
	 * "MPDV".equalsIgnoreCase(student.getProgram())) { // programDurationPara = new
	 * Paragraph("has successfully completed the "+
	 * programDetails.getProgramDuration() + " " +
	 * programDetails.getProgramDurationUnit()); if(student.isPassForExceutive()) {
	 * programDurationPara = new Paragraph("has successfully completed the "+
	 * programDetails.getProgramDuration() + " " +
	 * programDetails.getProgramDurationUnit()); }else { programDurationPara = new
	 * Paragraph("has participated in the "+ programDetails.getProgramDuration() +
	 * " " + programDetails.getProgramDurationUnit()); } }else{ programDurationPara
	 * = new Paragraph("has been examined and found qualified for"); }
	 * 
	 * programDurationPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(programDurationPara); document.add(blankLine);
	 * 
	 * if(programDetails.getProgram().equalsIgnoreCase("MBA - X") ||
	 * programDetails.getProgram().equalsIgnoreCase("MBA - WX")) {
	 * 
	 * String programnameLine1
	 * =programDetails.getProgram().equalsIgnoreCase("MBA - WX"
	 * )?"MASTER OF BUSINESS ADMINISTRATION":"MASTER OF BUSINESS ADMINISTRATION";
	 * String programnameLine2
	 * =programDetails.getProgram().equalsIgnoreCase("MBA - WX"
	 * )?"FOR WORKING EXECUTIVES":""; String specialization
	 * =programDetails.getProgram().equalsIgnoreCase("MBA - WX")?"("+student.
	 * getSpecialisation()+")":"FOR EXECUTIVES (Business Analytics)";
	 * 
	 * Paragraph programNamePara1 = new Paragraph(programnameLine1, font16Bold);
	 * programNamePara1.setAlignment(Element.ALIGN_CENTER);
	 * document.add(programNamePara1);
	 * 
	 * Paragraph programNamePara2 = new Paragraph(programnameLine2, font12Bold);
	 * programNamePara2.setAlignment(Element.ALIGN_CENTER);
	 * document.add(programNamePara2);
	 * 
	 * Paragraph specializationPara = new Paragraph(specialization, font12Bold);
	 * specializationPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(specializationPara); }else { Paragraph programNamePara = new
	 * Paragraph(programname, font16Bold);
	 * programNamePara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(programNamePara); } Paragraph collegeNamePara = new
	 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
	 * font16Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(collegeNamePara); document.add(blankLine);
	 * //document.add(blankLine); if("EPBM".equalsIgnoreCase(student.getProgram())
	 * || "MPDV".equalsIgnoreCase(student.getProgram())) { String
	 * countedByParaContent = "conducted jointly by"; String companyNamesContent =
	 * "NMIMS Deemed-to-be University and SAS Institute (l) Pvt. Ltd."; Paragraph
	 * countedByParaContentPara = new Paragraph(countedByParaContent); Paragraph
	 * companyNamesContentPara = new Paragraph(companyNamesContent);
	 * countedByParaContentPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(countedByParaContentPara);
	 * companyNamesContentPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(companyNamesContentPara);
	 * 
	 * }else { Chunk chunk1 = new
	 * Chunk("The said "+programType.toLowerCase()+" in "); Chunk chunk2 = new
	 * Chunk(learningMode,font12Bold); Chunk chunk3 = new Chunk(" under ");
	 * 
	 * Paragraph awardedPara1 = new Paragraph(); awardedPara1.add(chunk1);
	 * awardedPara1.add(chunk2); awardedPara1.add(chunk3); Paragraph awardedPara2 =
	 * new Paragraph(" NMIMS Global Access School for Continuing Education",
	 * font12Bold); Paragraph awardedPara3 = new
	 * Paragraph("has been awarded to him/her in the month of "+
	 * formatterMonth.format(s.parse(date)) +" of the year " +
	 * formatterYear.format(s.parse(date))+".");
	 * 
	 * awardedPara1.setAlignment(Element.ALIGN_CENTER);
	 * awardedPara2.setAlignment(Element.ALIGN_CENTER);
	 * awardedPara3.setAlignment(Element.ALIGN_CENTER); document.add(awardedPara1);
	 * document.add(awardedPara2); document.add(awardedPara3); }
	 * document.add(blankLine);
	 * 
	 * String testimonyParaContent =
	 * "In testimony whereof is set the seal of the said"; //String
	 * deemedToBeParaContent =
	 * "Deemed-to-be University and the signature of the said Director."; String
	 * deemedToBeParaContent = ""; if("EPBM".equalsIgnoreCase(student.getProgram())
	 * || "MPDV".equalsIgnoreCase(student.getProgram())) { deemedToBeParaContent =
	 * "Deemed-to-be University and the signature of the Authorised Signatory.";
	 * }else { deemedToBeParaContent =
	 * "Deemed-to-be University and the signature of the said Vice Chancellor."; }
	 * 
	 * Paragraph testimonyPara = new Paragraph(testimonyParaContent); Paragraph
	 * deemedToBePara = new Paragraph(deemedToBeParaContent);
	 * testimonyPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(testimonyPara);
	 * deemedToBePara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(deemedToBePara);
	 * 
	 * document.add(blankLine); document.add(blankLine); //document.add(blankLine);
	 * 
	 * commented as there is only one director signature
	 * if("EPBM".equalsIgnoreCase(student.getProgram()) ||
	 * "MPDV".equalsIgnoreCase(student.getProgram())) { PdfPTable signatureTable =
	 * new PdfPTable(2); PdfPCell signatureCell = new PdfPCell(signatureOfSAS);
	 * 
	 * 
	 * //signatureCell.setRowspan(6); signatureCell.setBorder(0);
	 * signatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * signatureTable.addCell(signatureCell);
	 * 
	 * //signatureTable.addCell(signatureCell);
	 * 
	 * signatureTable.completeRow();
	 * 
	 * PdfPCell nmCell = new PdfPCell(new Paragraph("Director", font10));
	 * nmCell.setBorder(0); nmCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * //PdfPCell sasCell = new PdfPCell(new
	 * Paragraph("Professional Service Division",font10)); //PdfPCell sasCell = new
	 * PdfPCell(new Paragraph("Director",font10)); //sasCell.setBorder(0);
	 * //sasCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * signatureTable.addCell(nmCell);
	 * 
	 * //signatureTable.addCell(sasCell);
	 * 
	 * signatureTable.completeRow();
	 * 
	 * PdfPCell nmCell2 = new PdfPCell(new
	 * Paragraph("(NMIMS Global Access School for Continuing Education)", font10));
	 * nmCell2.setBorder(0); nmCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * //PdfPCell sasCell2 = new PdfPCell(new
	 * Paragraph("(SAS Institute (l) Pvt. Ltd. )",font10)); //PdfPCell sasCell2 =
	 * new PdfPCell(new
	 * Paragraph("Professional Service Division \n (SAS Institute (l) Pvt. Ltd. )"
	 * ,font10)); //sasCell2.setBorder(0);
	 * //sasCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * signatureTable.addCell(nmCell2);
	 * 
	 * //signatureTable.addCell(sasCell2);
	 * 
	 * signatureTable.completeRow();
	 * 
	 * studentNoCell.setFixedHeight(20); + genderCell.setFixedHeight(20); + +
	 * studentPhotoCell.setBorder(0); +
	 * studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER); +
	 * studentInfoTable.addCell(studentPhotoCell); + +
	 * studentInfoTable.completeRow(); + + studentInfoTable.setWidthPercentage(100);
	 * + + + //studentInfoTable.setSpacingBefore(7); + document.add(signatureTable);
	 * 
	 * }else { signature.setAlignment(Element.ALIGN_CENTER);
	 * document.add(signature);
	 * 
	 * Paragraph directorPara = new Paragraph("Vice Chancellor", font10);
	 * directorPara.setAlignment(Element.ALIGN_CENTER); document.add(directorPara);
	 * //document.add(blankLine);
	 * 
	 * Paragraph collegeNamePara = new
	 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
	 * font10Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(collegeNamePara);
	 * 
	 * //} Paragraph datePara = new Paragraph("Date: " + declareDate, font10);
	 * datePara.setAlignment(Element.ALIGN_CENTER); document.add(datePara);
	 * //document.add(blankLine); //document.add(blankLine);
	 * 
	 * certificateUniqueNumber =
	 * student.getSapid()+"-"+certificateDateForNumber+RandomStringUtils.
	 * randomAlphanumeric(4); //Paragraph studentNumberPara = new
	 * Paragraph("Student No: " +
	 * student.getSapid()+"  "+"Certificate Number:"+certificateUniqueNumber,
	 * font10); Paragraph studentNumberPara = new Paragraph("Student No: " +
	 * student.getSapid()+"  "+"Certificate Number:"+certificateUniqueNumber,
	 * font8); studentNumberPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(studentNumberPara); Paragraph capmusPara = new
	 * Paragraph("Mumbai", font8); capmusPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(capmusPara); document.add(blankLine);
	 * if("EPBM".equalsIgnoreCase(student.getProgram()) ||
	 * "MPDV".equalsIgnoreCase(student.getProgram())){ //Photo not required for
	 * Offline exam.
	 * 
	 * PdfPTable sasPhotoTable = new PdfPTable(1);
	 * 
	 * 
	 * final float heightOfImageInCm = 25; final float pointsValueHeight =
	 * com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);
	 * 
	 * final float widthOfImageInCm = 20; final float pointsValueWidth =
	 * com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);
	 * 
	 * 
	 * 
	 * 
	 * //studentPhoto.scaleAbsolute( pointsValueWidth,pointsValueHeight); PdfPCell
	 * sasPhotoCell = null; // if(logoSAS == null){ // //In case URL was malformed
	 * or was not found // sasPhotoCell = new PdfPCell(); // }else{ // sasPhotoCell
	 * = new PdfPCell(logoSAS); // }
	 * 
	 * sasPhotoCell.setRowspan(6); sasPhotoCell.setBorder(0);
	 * sasPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * sasPhotoTable.addCell(sasPhotoCell);
	 * sasPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * document.add(sasPhotoTable); }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * document.newPage();
	 * 
	 * }
	 * 
	 * document.close();
	 * 
	 * return certificateUniqueNumber;
	 * 
	 * 
	 * 
	 * }
	 */
	public String generateCertificateAndReturnCertificateNumber(List<MarksheetBean> studentForSRList,
			HashMap<String, String> mapOfSapIDAndResultDate, HttpServletRequest request,
			HashMap<String, ProgramExamBean> programAllDetailsMap, HashMap<String, CenterExamBean> centersMap,
			String MARKSHEETS_PATH, String SERVER_PATH, String STUDENT_PHOTOS_PATH) throws Exception {

		String certificateUniqueNumber = "";
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat sdfrForCertNum = new SimpleDateFormat("ddMM");
		String certificateDate = sdfr.format(new Date());
		String certificateDateForNumber = sdfrForCertNum.format(new Date());
		String fileName = null;
		
		boolean isHaveLogo = false;

		// Image signature = Image.getInstance(new
		// URL("https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/director-Signature.jpg"));
		/*
		 * Image signature = Image.getInstance(new
		 * URL(SERVER_PATH+"exam/resources_2015/images/director-Signature.jpg"));
		 */
//		Image signature = Image.getInstance("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/vc_signature.jpg");
		// Image signature = Image.getInstance("E:/director-Signature.jpg");

		// Image signatureOfSAS = Image.getInstance("D:/director-Signature.jpg");
		// Image logoSAS = Image.getInstance("E:/SAS-109.jpg");
//		Image logoSAS = Image.getInstance("D:/SAS-109.jpg");
		//signature.scaleAbsolute(110, 35);
		String folderPath = "";
		fileName = "Certificate_" + certificateDate + "_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
		folderPath = MARKSHEETS_PATH + "Certificates/";

		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		Document document = new Document(PageSize.A4);
		if (request.getAttribute("logoRequired") == null
				|| ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("N")) {
			document.setMargins(50, 50, 176, 35);
			// document.setMargins(50, 50, 70, 35);
		} else {
			document.setMargins(50, 50, 45, 35);
			isHaveLogo = true;
		}

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath + fileName));
		request.getSession().setAttribute("fileName", folderPath + fileName);
		document.open();
		PdfPTable headerTable = new PdfPTable(1);
		if (request.getAttribute("logoRequired") != null
				&& ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
			Image logo = null;
			logo = Image.getInstance(
					new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nmims_logo_new.jpg"));
			logo.scaleAbsolute(142, 40);
			logo.setAlignment(Element.ALIGN_CENTER);

			PdfPCell logoCell = new PdfPCell();
			logoCell.addElement(logo);
			logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			logoCell.setBorder(0);
			headerTable.addCell(logoCell);
			headerTable.completeRow();
			headerTable.setWidthPercentage(100);
			headerTable.setSpacingBefore(10);
			headerTable.setSpacingAfter(10);
		}

		Paragraph blankLine = new Paragraph("\n");

		for (MarksheetBean student : studentForSRList) {
			//
			if (request.getAttribute("logoRequired") != null
					&& ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
				document.add(headerTable);
			}
			String date = mapOfSapIDAndResultDate.get(student.getSapid());
			String declareDate = "";
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
			Format formatterMonth = new SimpleDateFormat("MMMM");
			Format formatterYear = new SimpleDateFormat("yyyy");
			Format formatterDay = new SimpleDateFormat("dd");
			if (date != null) {
				declareDate = formatterMonth.format(s.parse(date)) + " " + formatterDay.format(s.parse(date)) + ", "
						+ formatterYear.format(s.parse(date));
			} else {
				declareDate = "";
			}
			
			Paragraph svkmManagement = new Paragraph(
					"We, the Chancellor, Vice Chancellor and Members of Board of Management of \nSVKM\'s Narsee Monjee Institute of Management Studies,");
			svkmManagement.setAlignment(Element.ALIGN_CENTER);
			document.add(svkmManagement);

			Paragraph certifyThatPara = new Paragraph("certify that");
			certifyThatPara.setAlignment(Element.ALIGN_CENTER);
			document.add(certifyThatPara);
			document.add(blankLine);

			if ("Online".equals(student.getExamMode())) {
				// Photo not required for Offline exam.
				Image studentPhoto = null;
				PdfPTable studentPhotoTable = new PdfPTable(1);
				try {
					studentPhoto = Image.getInstance(new URL(student.getImageUrl()));
					try {
						certificate.info("Certificate Generation"+ student.getSapid());
						 studentPhoto=    ImageRotationUtil.getImageRotatedByOrientation(studentPhoto,student.getImageUrl());
					}catch(Exception e) {
						certificate.info("Error FOund for"+ student.getSapid()+" "+e);
					}
				} catch (Exception e) {
					// If not .jpg, try .jpeg
					try {
						studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + student.getSapid() + ".jpg");

					} catch (Exception e2) {
						// IF this is also not found, then take photo from Admission system
						try {
							studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + student.getSapid() + ".jpeg");
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							throw new Exception("Error in getting image. Error : " + e.getMessage());
						}
						// mailer.sendPhotoNotFoundEmail(student);
					}
				}
				final float heightOfImageInCm = 25;
				final float pointsValueHeight = com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);

				final float widthOfImageInCm = 20;
				final float pointsValueWidth = com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);

				studentPhoto.scaleAbsolute(pointsValueWidth, pointsValueHeight);
				PdfPCell studentPhotoCell = null;
				if (studentPhoto == null) {
					// In case URL was malformed or was not found
					studentPhotoCell = new PdfPCell();
				} else {
					studentPhotoCell = new PdfPCell(studentPhoto);
				}

				studentPhotoCell.setRowspan(6);
				studentPhotoCell.setBorder(0);
				studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				studentPhotoTable.addCell(studentPhotoCell);
				studentPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
				document.add(studentPhotoTable);
			}

			// Added to make first letter capital and rest of them small//

			String fullNameOfStudent = student.getFirstName().trim().toUpperCase() + " "
					+ student.getLastName().trim().toUpperCase().replace(".", " ");
			String fullNameOfFather = student.getFatherName().trim().toUpperCase();
			String fullNameOfMother = student.getMotherName().trim().toUpperCase();
			String fullNameOfHusband = "";
			try {
				fullNameOfHusband = student.getHusbandName().trim().toUpperCase();
			} catch (Exception e) { 
				
			}

			Paragraph studentNamePara = new Paragraph(fullNameOfStudent, font18Bold);
			studentNamePara.setAlignment(Element.ALIGN_CENTER);
			document.add(studentNamePara);
			document.add(blankLine);

			Paragraph sonDaughterOfPara = null;
			if ("Spouse".equals(student.getAdditionalInfo1()) && "Female".equals(student.getGender())) {

				sonDaughterOfPara = new Paragraph(
						"(Wife of Shri. " + fullNameOfHusband + " and Daughter of Smt. " + fullNameOfMother + " )");
			} else  {
				sonDaughterOfPara = new Paragraph(
						"(Son/Daughter of Shri. " + fullNameOfFather + " and Smt. " + fullNameOfMother + " )");
			}

			sonDaughterOfPara.setAlignment(Element.ALIGN_CENTER);
			document.add(sonDaughterOfPara);
			//document.add(blankLine);
			/*
			 * String program = student.getProgram(); ProgramBean programDetails =
			 * programAllDetailsMap.get(program);
			 */
			String key = student.getProgram() + "-" + student.getPrgmStructApplicable();
			ProgramExamBean programDetails = programAllDetailsMap.get(key);
			String programname = programDetails.getProgramname();
			String programType = programDetails.getProgramType();
			String programDurationUnit = programDetails.getProgramDurationUnit();
			String programDuration = programDetails.getProgramDuration();
			String program = programDetails.getProgram();
			Image signature = null;
			Paragraph directorPara = null;
			String deemedToBeParaContent = "";
			
			
			

			if(DIRECTOR_SIGNATURE_ELIGIBLE_PROGRAMS.contains(programDetails.getProgram())) {
				signature = Image.getInstance(
						"https://staticfilesportal.s3.ap-south-1.amazonaws.com/assets/images/Signature.png");
				deemedToBeParaContent = "Deemed-to-be University and the signature of the said Director.";
				directorPara=new Paragraph("Director", font10);
			}
			else {
				
				signature = Image.getInstance(
						"https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/vc_signature.jpg");
				deemedToBeParaContent = "Deemed-to-be University and the signature of the said Vice Chancellor.";
				directorPara=new Paragraph("Vice Chancellor", font10);
				
			}
			signature.scaleAbsolute(80, 39);   
			List<String> programTypes =new ArrayList(Arrays.asList("Bachelor Programs","Master"));
		
			if(programTypes.contains(programType)) {
 				programType="degree";
			}
			else if("Post Graduate Diploma".equals(programType) && program.startsWith("MBA")) {
				programType="degree";
			}
			else
			{
				programType=programType.toLowerCase();
			}
			
			document.add(blankLine);
			
			// Paragraph programDurationPara = new Paragraph("has been examined and found
			// qualified for the "+ programDetails.getProgramDuration() + " " +
			// programDetails.getProgramDurationUnit());
			Paragraph programDurationPara = new Paragraph("");
			if ("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
				// programDurationPara = new Paragraph("has successfully completed the "+
				// programDetails.getProgramDuration() + " " +
				// programDetails.getProgramDurationUnit());
				if (student.isPassForExceutive()) {
					programDurationPara = new Paragraph("has successfully completed the "
							+ programDetails.getProgramDuration() + " " + programDetails.getProgramDurationUnit());
				} else {
					programDurationPara = new Paragraph("has participated in the " + programDetails.getProgramDuration()
							+ " " + programDetails.getProgramDurationUnit());
				}
			}else if(programDetails.getProgram().equalsIgnoreCase("MBA - X") || programDetails.getProgram().equalsIgnoreCase("MBA - WX")) {
				programDurationPara = new Paragraph("has been examined and found qualified for ");
			}
			else if(MODULAR_PDDM_MASTERKEYS.contains(programDetails.getConsumerProgramStructureId()))
			{
				programDurationPara = new Paragraph("has been examined and found qualified for the "
						+ programDetails.getProgramDuration() + " " + programDetails.getProgramDurationUnit());
			}
			else {
				programDurationPara = new Paragraph("has been examined and found qualified for ");
			}
//			programDurationPara = new Paragraph("has been examined and found qualified for ");
//			
			programDurationPara.setAlignment(Element.ALIGN_CENTER);
			document.add(programDurationPara);
			document.add(blankLine);

			if (programDetails.getProgram().equalsIgnoreCase("MBA - X")
					|| programDetails.getProgram().equalsIgnoreCase("MBA - WX")) {

				String programnameLine1 = programDetails.getProgram().equalsIgnoreCase("MBA - WX")
						? "MASTER OF BUSINESS ADMINISTRATION"
						: "MASTER OF BUSINESS ADMINISTRATION";
				String programnameLine2 = programDetails.getProgram().equalsIgnoreCase("MBA - WX")
						? "FOR WORKING EXECUTIVES"
						: "";
				String specialization = programDetails.getProgram().equalsIgnoreCase("MBA - WX")
						? "(" + student.getSpecialisation() + ")"
						: "FOR EXECUTIVES (Business Analytics)";

				Paragraph programNamePara1 = new Paragraph(programnameLine1, font16Bold);
				programNamePara1.setAlignment(Element.ALIGN_CENTER);
				document.add(programNamePara1);

				Paragraph programNamePara2 = new Paragraph(programnameLine2, font12Bold);
				programNamePara2.setAlignment(Element.ALIGN_CENTER);
				document.add(programNamePara2);

				Paragraph specializationPara = new Paragraph(specialization, font12Bold);
				specializationPara.setAlignment(Element.ALIGN_CENTER);
				document.add(specializationPara);

			}else {
				ArrayList<String> programsNeedAlignment = new ArrayList<String>(
						Arrays.asList("PGDBM - SCM", "PGDBM - HRM" ,"PGDBM - BFM"));
				
				if(programsNeedAlignment.contains(student.getProgram()) ) {
					String arr[] = programname.split("\\("); 
					Paragraph programNamePara = new Paragraph(arr[0], font16Bold);
					programNamePara.setAlignment(Element.ALIGN_CENTER);
					document.add(programNamePara);
					Paragraph specializationPara = new Paragraph("("+arr[1], font16Bold);
					specializationPara.setAlignment(Element.ALIGN_CENTER);
					document.add(specializationPara);
				}else {
					Paragraph programNamePara = new Paragraph(programname, font16Bold);
					programNamePara.setAlignment(Element.ALIGN_CENTER);
					document.add(programNamePara);
				}
			}
			/*
			 * Paragraph collegeNamePara = new
			 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
			 * font16Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
			 * document.add(collegeNamePara);
			 */
			document.add(blankLine);
			// document.add(blankLine);

			//document.add(blankLine);
//			if ("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
//				String countedByParaContent = "conducted jointly by";
//				String companyNamesContent = "NMIMS Deemed-to-be University and SAS Institute (l) Pvt. Ltd.";
//				Paragraph countedByParaContentPara = new Paragraph(countedByParaContent);
//				Paragraph companyNamesContentPara = new Paragraph(companyNamesContent);
//				countedByParaContentPara.setAlignment(Element.ALIGN_CENTER);
//				document.add(countedByParaContentPara);
//				companyNamesContentPara.setAlignment(Element.ALIGN_CENTER);
//				document.add(companyNamesContentPara);
//
//			} else if (programDetails.getProgram().equalsIgnoreCase("MBA - X")
//					|| programDetails.getProgram().equalsIgnoreCase("MBA - WX")) {
//				// Font bold =new Font(FontFamily.HELVETICA, 12, Font.BOLD);
//				Chunk chunk1 = new Chunk("The said " + programType + " under ");
//				Chunk chunk2 = new Chunk("NMIMS Global Access School for Continuing Education", font12Bold);
//
//				Paragraph awardedPara1 = new Paragraph();
//				awardedPara1.add(chunk1);
//				awardedPara1.add(chunk2);
//				Paragraph awardedPara2 = new Paragraph(
//						" has been awarded to him/her in the month of " + formatterMonth.format(s.parse(date))
//								+ " of the year " + formatterYear.format(s.parse(date)) + ".");
//
//				// Paragraph awardedPara = new Paragraph();
//				// awardedPara.add(chunk1); awardedPara.add(chunk2); awardedPara.add(chunk3);
//				// new line
//				// String monthAndYearContent = " in the month of " +
//				// formatterMonth.format(s.parse(date)) + " of the year " +
//				// formatterYear.format(s.parse(date))+".";
//				/*
//				 * String monthAndYearContent = " in the month of August of the year " +
//				 * formatterYear.format(s.parse(date))+".";
//				 */
//				// Paragraph monthAndYearPara = new Paragraph(monthAndYearContent);
//				awardedPara1.setAlignment(Element.ALIGN_CENTER);
//				awardedPara2.setAlignment(Element.ALIGN_CENTER);
//				document.add(awardedPara1);
//				document.add(awardedPara2);
//				// monthAndYearPara.setAlignment(Element.ALIGN_CENTER);
//				// document.add(monthAndYearPara);
//			} else {
//					Paragraph awardedPara1 = new Paragraph();
//					Paragraph awardedPara2 = new Paragraph();
//			
//						awardedPara1 = new Paragraph("The said " + programType + " under");
//						awardedPara2 = new Paragraph(" NMIMS Global Access School for Continuing Education",font12Bold);
//					
//					Paragraph awardedPara3 = new Paragraph(
//						"has been awarded to him/her in the month of " + formatterMonth.format(s.parse(date))
//								+ " of the year " + formatterYear.format(s.parse(date)) + ".");
//
//					awardedPara1.setAlignment(Element.ALIGN_CENTER);
//					awardedPara2.setAlignment(Element.ALIGN_CENTER);
//					awardedPara3.setAlignment(Element.ALIGN_CENTER);
//				
//				document.add(awardedPara1);
//				document.add(awardedPara2);
//				document.add(awardedPara3);
//			}
			Paragraph awardedPara1 = new Paragraph();
	
			awardedPara1 = new Paragraph("The said " + programType + " has been awarded to him/her");
			
			Paragraph awardedPara3 = new Paragraph(
				"in the month of " + formatterMonth.format(s.parse(date))
						+ " of the year " + formatterYear.format(s.parse(date)) + ".");

			awardedPara1.setAlignment(Element.ALIGN_CENTER);
			awardedPara3.setAlignment(Element.ALIGN_CENTER);
		
			document.add(awardedPara1);
			document.add(awardedPara3);
		
			document.add(blankLine);

			String testimonyParaContent = "In testimony whereof is set the seal of the said";
			// String deemedToBeParaContent = "Deemed-to-be University and the signature of
			// the said Director.";
		

			Paragraph testimonyPara = new Paragraph(testimonyParaContent);
			Paragraph deemedToBePara = new Paragraph(deemedToBeParaContent);
			testimonyPara.setAlignment(Element.ALIGN_CENTER);
			document.add(testimonyPara);
			deemedToBePara.setAlignment(Element.ALIGN_CENTER);
			document.add(deemedToBePara);

			document.add(blankLine);
			//document.add(blankLine);
			// document.add(blankLine);

			/*
			 * commented as there is only one director signature
			 * if("EPBM".equalsIgnoreCase(student.getProgram()) ||
			 * "MPDV".equalsIgnoreCase(student.getProgram())) { PdfPTable signatureTable =
			 * new PdfPTable(2); PdfPCell signatureCell = new PdfPCell(signatureOfSAS);
			 * 
			 * 
			 * //signatureCell.setRowspan(6); signatureCell.setBorder(0);
			 * signatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * signatureTable.addCell(signatureCell);
			 * 
			 * //signatureTable.addCell(signatureCell);
			 * 
			 * signatureTable.completeRow();
			 * 
			 * PdfPCell nmCell = new PdfPCell(new Paragraph("Director", font10));
			 * nmCell.setBorder(0); nmCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * //PdfPCell sasCell = new PdfPCell(new
			 * Paragraph("Professional Service Division",font10)); //PdfPCell sasCell = new
			 * PdfPCell(new Paragraph("Director",font10)); //sasCell.setBorder(0);
			 * //sasCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * signatureTable.addCell(nmCell);
			 * 
			 * //signatureTable.addCell(sasCell);
			 * 
			 * signatureTable.completeRow();
			 * 
			 * PdfPCell nmCell2 = new PdfPCell(new
			 * Paragraph("(NMIMS Global Access School for Continuing Education)", font10));
			 * nmCell2.setBorder(0); nmCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * //PdfPCell sasCell2 = new PdfPCell(new
			 * Paragraph("(SAS Institute (l) Pvt. Ltd. )",font10)); //PdfPCell sasCell2 =
			 * new PdfPCell(new
			 * Paragraph("Professional Service Division \n (SAS Institute (l) Pvt. Ltd. )"
			 * ,font10)); //sasCell2.setBorder(0);
			 * //sasCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * signatureTable.addCell(nmCell2);
			 * 
			 * //signatureTable.addCell(sasCell2);
			 * 
			 * signatureTable.completeRow();
			 * 
			 * studentNoCell.setFixedHeight(20); + genderCell.setFixedHeight(20); + +
			 * studentPhotoCell.setBorder(0); +
			 * studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER); +
			 * studentInfoTable.addCell(studentPhotoCell); + +
			 * studentInfoTable.completeRow(); + + studentInfoTable.setWidthPercentage(100);
			 * + + + //studentInfoTable.setSpacingBefore(7); + document.add(signatureTable);
			 * 
			 * }else {
			 */
			signature.setAlignment(Element.ALIGN_CENTER);
			document.add(signature);

//			Paragraph directorPara = new Paragraph("Vice Chancellor", font10);
			directorPara.setAlignment(Element.ALIGN_CENTER);
			document.add(directorPara);
			// document.add(blankLine);
			/*
			 * Paragraph collegeNamePara = new
			 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
			 * font10Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
			 * document.add(collegeNamePara);
			 */
			// }
			Paragraph datePara = new Paragraph("Date: " + declareDate, font10);
			datePara.setAlignment(Element.ALIGN_CENTER);
			document.add(datePara);
			// document.add(blankLine);
			// document.add(blankLine);

			certificateUniqueNumber = student.getSapid() + "-" + certificateDateForNumber
					+ RandomStringUtils.randomAlphanumeric(4);
			// Paragraph studentNumberPara = new Paragraph("Student No: " +
			// student.getSapid()+" "+"Certificate Number:"+certificateUniqueNumber,
			// font10);
			Paragraph studentNumberPara = new Paragraph(
					"Student No: " + student.getSapid() + "  " + "Certificate Number:" + certificateUniqueNumber,
					font8);
			studentNumberPara.setAlignment(Element.ALIGN_CENTER);
			document.add(studentNumberPara);
			Paragraph capmusPara = new Paragraph("Mumbai", font8);
			capmusPara.setAlignment(Element.ALIGN_CENTER);
			document.add(capmusPara);
			document.add(blankLine);
			if ("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
				// Photo not required for Offline exam.

				PdfPTable sasPhotoTable = new PdfPTable(1);

				/*
				 * final float heightOfImageInCm = 25; final float pointsValueHeight =
				 * com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);
				 * 
				 * final float widthOfImageInCm = 20; final float pointsValueWidth =
				 * com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);
				 */

				/*
				  * */

				// studentPhoto.scaleAbsolute( pointsValueWidth,pointsValueHeight);
				PdfPCell sasPhotoCell = null;
//				if(logoSAS == null){
//					//In case URL was malformed or was not found
//					sasPhotoCell = new PdfPCell();
//				}else{
//					sasPhotoCell = new PdfPCell(logoSAS);
//				}

				sasPhotoCell.setRowspan(6);
				sasPhotoCell.setBorder(0);
				sasPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				sasPhotoTable.addCell(sasPhotoCell);
				sasPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
				document.add(sasPhotoTable);
			}

			generateCertficateBackSide(document,student.getSapid(),student.getEnrollmentMonth(),
					student.getEnrollmentYear(),date,false,writer,isHaveLogo,student.getProgram());
			
			document.newPage();
			
		}

		document.close();

		return certificateUniqueNumber;

	}

	public String generateParticipationCertificateAndReturnCertificateNumber(List<MarksheetBean> studentForSRList,
			HashMap<String, String> mapOfSapIDAndResultDate, HttpServletRequest request,
			HashMap<String, ProgramExamBean> programAllDetailsMap, HashMap<String, CenterExamBean> centersMap,
			String MARKSHEETS_PATH, String SERVER_PATH, String isPassStudent, String STUDENT_PHOTOS_PATH)
			throws Exception {

		String certificateUniqueNumber = "";
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat sdfrForCertNum = new SimpleDateFormat("ddMM");
		String certificateDate = sdfr.format(new Date());
		String certificateDateForNumber = sdfrForCertNum.format(new Date());
		String fileName = null;

		Image bajajLabel = Image.getInstance("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/bajajImage.png");
		
		String folderPath = "";
		fileName = "Certificate_" + certificateDate + "_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
		folderPath = MARKSHEETS_PATH + "Certificates/";

		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		Document document = new Document(PageSize.A4);
		document.setMargins(50, 50, 170, 35);

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath + fileName));
		request.getSession().setAttribute("fileName", folderPath + fileName);
		document.open();
		Paragraph blankLine = new Paragraph("\n");

		// document.add(blankLine);
		// document.add(blankLine);
		for (MarksheetBean student : studentForSRList) {

			String date = mapOfSapIDAndResultDate.get(student.getSapid());
			String declareDate = "";
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
			Format formatterMonth = new SimpleDateFormat("MMMM");
			Format formatterYear = new SimpleDateFormat("yyyy");
			Format formatterDay = new SimpleDateFormat("dd");
			if (date != null) {
				declareDate = formatterMonth.format(s.parse(date)) + " " + formatterDay.format(s.parse(date)) + ", "
						+ formatterYear.format(s.parse(date));
			} else {
				declareDate = "";
			}
			Paragraph svkmManagement = new Paragraph(
				"We, the Chancellor, Vice Chancellor and Members of Board of Management of \nSVKM\'s Narsee Monjee Institute of Management Studies,");
			svkmManagement.setAlignment(Element.ALIGN_CENTER);
			document.add(svkmManagement);
			
			Paragraph certifyThatPara = new Paragraph("certify that");
			certifyThatPara.setAlignment(Element.ALIGN_CENTER);
			document.add(certifyThatPara);
			document.add(blankLine);

			if ("Online".equals(student.getExamMode())) {
				// Photo not required for Offline exam.
				Image studentPhoto = null;
				PdfPTable studentPhotoTable = new PdfPTable(1);
				try {
					studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + student.getSapid() + ".jpg");
				} catch (Exception e) {
					// If not .jpg, try .jpeg
					try {
						studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + student.getSapid() + ".jpeg");
					} catch (Exception e2) {
						// IF this is also not found, then take photo from Admission system
						try {
							studentPhoto = Image.getInstance(new URL(student.getImageUrl()));
							try {
								certificate.info("Certificate Generation"+ student.getSapid());
								 studentPhoto=    ImageRotationUtil.getImageRotatedByOrientation(studentPhoto,student.getImageUrl());
							}catch(Exception e3) {
								certificate.info("Error FOund for"+ student.getSapid()+" "+e3);
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							throw new Exception("Error in getting image. Error : " + e.getMessage());
						}
						// mailer.sendPhotoNotFoundEmail(student);
					}
				}
				final float heightOfImageInCm = 25;
				final float pointsValueHeight = com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);

				final float widthOfImageInCm = 20;
				final float pointsValueWidth = com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);

				studentPhoto.scaleAbsolute(pointsValueWidth, pointsValueHeight);
				PdfPCell studentPhotoCell = null;
				if (studentPhoto == null) {
					// In case URL was malformed or was not found
					studentPhotoCell = new PdfPCell();
				} else {
					studentPhotoCell = new PdfPCell(studentPhoto);
				}

				studentPhotoCell.setRowspan(6);
				studentPhotoCell.setBorder(0);
				studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				studentPhotoTable.addCell(studentPhotoCell);
				studentPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
				document.add(studentPhotoTable);
			}
			// Added to make first letter capital and rest of them small//

			String fullNameOfStudent = student.getFirstName().trim().toUpperCase() + " "
					+ student.getLastName().toUpperCase().trim().replace(".", " ");
			String fullNameOfFather = student.getFatherName().trim().toUpperCase();
			String fullNameOfMother = student.getMotherName().trim().toUpperCase();
			String fullNameOfHusband = student.getHusbandName().trim().toUpperCase();

			Paragraph studentNamePara = new Paragraph(fullNameOfStudent, font18Bold);
			studentNamePara.setAlignment(Element.ALIGN_CENTER);
			document.add(studentNamePara);
			document.add(blankLine);

			Paragraph sonDaughterOfPara = null;
			if ("Spouse".equals(student.getAdditionalInfo1()) && "Female".equals(student.getGender())) {

				sonDaughterOfPara = new Paragraph(
						"(Wife of Shri. " + fullNameOfHusband + " and Daughter of " + fullNameOfMother + " )");
			} else {
				sonDaughterOfPara = new Paragraph(
						"(Son/Daughter of Shri. " + fullNameOfFather + " and Smt. " + fullNameOfMother + " )");
			}

			sonDaughterOfPara.setAlignment(Element.ALIGN_CENTER);
			document.add(sonDaughterOfPara);
			document.add(blankLine);
			if (student.getConsumerType().equalsIgnoreCase("BAJAJ")) {
				bajajLabel.scaleToFit(100f, 400f);
				bajajLabel.setAlignment(Image.MIDDLE);

				PdfPTable table = new PdfPTable(2);
				PdfPCell cell, imageCell;

				cell = new PdfPCell();
				imageCell = new PdfPCell(bajajLabel, true);

				Paragraph para = new Paragraph("of");
				para.setAlignment(Element.ALIGN_RIGHT);
				cell.addElement(para);
				cell.setBorder(Rectangle.NO_BORDER);

				table.addCell(cell);

				imageCell.setFixedHeight(30.0f);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(imageCell);
				/* table.setWidths(new int[]{1,2}); */
				table.setWidths(new int[] { 2, 3 });

				document.add(table);

				/*
				 * Paragraph bajajImagePara =new Paragraph("of");
				 * bajajImagePara.setAlignment(Element.ALIGN_CENTER);
				 * document.add(bajajImagePara);
				 * bajajLabel.setAlignment(Element.ALIGN_JUSTIFIED); document.add(bajajLabel);
				 */
				document.add(blankLine);
			}
			/*
			 * String program = student.getProgram(); ProgramBean programDetails =
			 * programAllDetailsMap.get(program);
			 */
			String key = student.getProgram() + "-" + student.getPrgmStructApplicable();
			ProgramExamBean programDetails = programAllDetailsMap.get(key);
			String programname = programDetails.getProgramname();
			String programType = programDetails.getProgramType();
//			String modeOfLearning = programDetails.getModeOfLearning();
			Image signature = null;
			Paragraph directorPara = null;
			String deemedToBeParaContent = "";
			
			if(DIRECTOR_SIGNATURE_ELIGIBLE_PROGRAMS.contains(programDetails.getProgram())) {
				signature = Image.getInstance(
						"https://staticfilesportal.s3.ap-south-1.amazonaws.com/assets/images/Signature.png");
				deemedToBeParaContent = "Deemed-to-be University and the signature of the said Director.";
				directorPara=new Paragraph("Director", font10);
			}
			else {
				
				signature = Image.getInstance(
						"https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/vc_signature.jpg");
				deemedToBeParaContent = "Deemed-to-be University and the signature of the said Vice Chancellor.";
				directorPara=new Paragraph("Vice Chancellor", font10);
				
			}
			signature.scaleAbsolute(80, 39); 

			String specializationName = programDetails.getSpecializationName();
			Paragraph programDurationPara = new Paragraph("");
			List<String> programTypes =new ArrayList(Arrays.asList("Bachelor Programs","Executive Programs","PG Programs","Master"));
 			
			if(programTypes.contains(programType)) {
 				programType="degree";
			}
			else
			{
				programType=programType.toLowerCase();
			}
			
			if ("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
				// programDurationPara = new Paragraph("has successfully completed the "+
				// programDetails.getProgramDuration() + " " +
				// programDetails.getProgramDurationUnit());
				if (student.isPassForExceutive()) {
					programDurationPara = new Paragraph("has successfully completed the "
							+ programDetails.getProgramDuration() + " " + programDetails.getProgramDurationUnit());
				} else {
					programDurationPara = new Paragraph("has participated in the " + programDetails.getProgramDuration()
							+ " " + programDetails.getProgramDurationUnit());
				}
			}
			else if(MODULAR_PDDM_MASTERKEYS.contains(programDetails.getConsumerProgramStructureId()))
			{
				programDurationPara = new Paragraph("has been examined and found qualified for the "
						+ programDetails.getProgramDuration() + " " + programDetails.getProgramDurationUnit());
			}
			else {
				programDurationPara = new Paragraph("has been examined and found qualified for");
			}
//			programDurationPara = new Paragraph("has been examined and found qualified for");
			programDurationPara.setAlignment(Element.ALIGN_CENTER);
			document.add(programDurationPara);
			document.add(blankLine);

			if (programDetails.getProgram().equalsIgnoreCase("MBA - X")
					|| programDetails.getProgram().equalsIgnoreCase("MBA - WX")) {

				String programnameLine1 = programDetails.getProgram().equalsIgnoreCase("MBA - WX")
						? "MASTER OF BUSINESS ADMINISTRATION"
						: "MASTER OF BUSINESS ADMINISTRATION";
				String programnameLine2 = programDetails.getProgram().equalsIgnoreCase("MBA - WX")
						? "FOR WORKING EXECUTIVES"
						: "";
				String specialization = programDetails.getProgram().equalsIgnoreCase("MBA - WX")
						? "(" + student.getSpecialisation() + ")"
						: "FOR EXECUTIVES (Business Analytics)";

				Paragraph programNamePara1 = new Paragraph(programnameLine1, font16Bold);
				programNamePara1.setAlignment(Element.ALIGN_CENTER);
				document.add(programNamePara1);

				Paragraph programNamePara2 = new Paragraph(programnameLine2, font12Bold);
				programNamePara2.setAlignment(Element.ALIGN_CENTER);
				document.add(programNamePara2);

				Paragraph specializationPara = new Paragraph(specialization, font12Bold);
				specializationPara.setAlignment(Element.ALIGN_CENTER);
				document.add(specializationPara);
			} else {
				String arr[] = programname.split("[\\(\\-\\)]");
				Paragraph programNamePara = new Paragraph(arr[0].trim(), font16Bold);
				programNamePara.setAlignment(Element.ALIGN_CENTER);
				document.add(programNamePara);
				
				if(arr.length > 1)
				{
					Paragraph middleProgramNamePara = new Paragraph("(" + arr[1].trim()+")", font16Bold);
					middleProgramNamePara.setAlignment(Element.ALIGN_CENTER);
					document.add(middleProgramNamePara);
				}
				
				if (!StringUtils.isEmpty(specializationName)) {
					Paragraph specializationNameP = new Paragraph("("+specializationName.trim()+")",font16Bold);
					specializationNameP.setAlignment(Element.ALIGN_CENTER);
					document.add(specializationNameP);
					
				}
			}

			/*
			 * Paragraph collegeNamePara = new
			 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
			 * font16Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
			 * document.add(collegeNamePara);
			 */
			// document.add(blankLine);
			document.add(blankLine);

//			if ("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
//				String countedByParaContent = "conducted jointly by";
//				String companyNamesContent = "NMIMS Deemed-to-be University and SAS Institute (l) Pvt. Ltd.";
//				Paragraph countedByParaContentPara = new Paragraph(countedByParaContent);
//				Paragraph companyNamesContentPara = new Paragraph(companyNamesContent);
//				countedByParaContentPara.setAlignment(Element.ALIGN_CENTER);
//				document.add(countedByParaContentPara);
//				companyNamesContentPara.setAlignment(Element.ALIGN_CENTER);
//				document.add(companyNamesContentPara);
//
//			} else if (programDetails.getProgram().equalsIgnoreCase("MBA - X")
//					|| programDetails.getProgram().equalsIgnoreCase("MBA - WX")) {
//				// Font bold =new Font(FontFamily.HELVETICA, 12, Font.BOLD);
//				Chunk chunk1 = new Chunk("The said " + programType + " under ");
//				Chunk chunk2 = new Chunk("NMIMS Global Access School for Continuing Education", font12Bold);
//
//				Paragraph awardedPara1 = new Paragraph();
//				awardedPara1.add(chunk1);
//				awardedPara1.add(chunk2);
//				Paragraph awardedPara2 = new Paragraph(
//						" has been awarded to him/her in the month of " + formatterMonth.format(s.parse(date))
//								+ " of the year " + formatterYear.format(s.parse(date)) + ".");
//
//				// Paragraph awardedPara = new Paragraph();
//				// awardedPara.add(chunk1); awardedPara.add(chunk2); awardedPara.add(chunk3);
//				// new line
//				// String monthAndYearContent = " in the month of " +
//				// formatterMonth.format(s.parse(date)) + " of the year " +
//				// formatterYear.format(s.parse(date))+".";
//				/*
//				 * String monthAndYearContent = " in the month of August of the year " +
//				 * formatterYear.format(s.parse(date))+".";
//				 */
//				// Paragraph monthAndYearPara = new Paragraph(monthAndYearContent);
//				awardedPara1.setAlignment(Element.ALIGN_CENTER);
//				awardedPara2.setAlignment(Element.ALIGN_CENTER);
//				document.add(awardedPara1);
//				document.add(awardedPara2);
//				// monthAndYearPara.setAlignment(Element.ALIGN_CENTER);
//				// document.add(monthAndYearPara);
//			} else {
//				Paragraph awardedPara1= new Paragraph("The said " + programType + " under ");
//
//				Paragraph awardedPara2 = new Paragraph(" NMIMS Global Access School for Continuing Education",
//						font12Bold);
//				Paragraph awardedPara3 = new Paragraph(
//						"has been awarded to him/her in the month of " + formatterMonth.format(s.parse(date))
//								+ " of the year " + formatterYear.format(s.parse(date)) + ".");
//
//				awardedPara1.setAlignment(Element.ALIGN_CENTER);
//				awardedPara2.setAlignment(Element.ALIGN_CENTER);
//				awardedPara3.setAlignment(Element.ALIGN_CENTER);
//				document.add(awardedPara1);
//				document.add(awardedPara2);
//				document.add(awardedPara3);
//			}
			Paragraph awardedPara1= new Paragraph("The said " + programType + " has been awarded to him/her");
			Paragraph awardedPara3 = new Paragraph(
					"in the month of " + formatterMonth.format(s.parse(date))
							+ " of the year " + formatterYear.format(s.parse(date)) + ".");

			awardedPara1.setAlignment(Element.ALIGN_CENTER);
			awardedPara3.setAlignment(Element.ALIGN_CENTER);
			document.add(awardedPara1);
			document.add(awardedPara3);
			
			document.add(blankLine);

			String testimonyParaContent = "In testimony whereof is set the seal of the said";
			Paragraph testimonyPara = new Paragraph(testimonyParaContent);
			Paragraph deemedToBePara = new Paragraph(deemedToBeParaContent);
			testimonyPara.setAlignment(Element.ALIGN_CENTER);
			document.add(testimonyPara);
			deemedToBePara.setAlignment(Element.ALIGN_CENTER);
			document.add(deemedToBePara);

			document.add(blankLine);
			document.add(blankLine); 
			// document.add(blankLine);

			signature.setAlignment(Element.ALIGN_CENTER);
			document.add(signature);

//			Paragraph directorPara = new Paragraph("Vice Chancellor", font10);
			directorPara.setAlignment(Element.ALIGN_CENTER);
			document.add(directorPara);
			// document.add(blankLine);
			/*
			 * Paragraph collegeNamePara = new
			 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
			 * font10Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
			 * document.add(collegeNamePara);
			 */

			Paragraph datePara = new Paragraph("Date: " + declareDate, font10);
			datePara.setAlignment(Element.ALIGN_CENTER);
			document.add(datePara);
			// document.add(blankLine);
			// document.add(blankLine);

			certificateUniqueNumber = student.getSapid() + "-" + certificateDateForNumber
					+ RandomStringUtils.randomAlphanumeric(4);
			Paragraph studentNumberPara = new Paragraph(
					"Student No: " + student.getSapid() + "  " + "Certificate Number:" + certificateUniqueNumber,
					font8);
			studentNumberPara.setAlignment(Element.ALIGN_CENTER);
			document.add(studentNumberPara);
			Paragraph capmusPara = new Paragraph("Mumbai", font8);
			capmusPara.setAlignment(Element.ALIGN_CENTER);
			document.add(capmusPara);
			document.add(blankLine);

			generateCertficateBackSide(document,student.getSapid(),student.getEnrollmentMonth(),
					student.getEnrollmentYear(),date,false,writer,false,student.getProgram());
			
			document.newPage();
			
		}

		document.close();

		return certificateUniqueNumber;

	}

	/*
	 * commented by steffi on 23-Sep-2017 for Standardizing public static String
	 * toCamelCase(String givenString) { givenString =givenString.toLowerCase();
	 * String[] arr = givenString.split(" "); StringBuffer sb = new StringBuffer();
	 * for (int i = 0; i < arr.length; i++) {
	 * sb.append(Character.toUpperCase(arr[i].charAt(0)))
	 * .append(arr[i].substring(1)).append(" "); } return sb.toString().trim(); }
	 */

	/*
	 * public String
	 * generateCertificateAndReturnCertificateNumberForSingleStudent(MarksheetBean
	 * marksheet, String resultDeclareDate, HttpServletRequest request,
	 * HashMap<String, ProgramBean> programAllDetailsMap, HashMap<String,
	 * CenterBean> centersMap, String CERTIFICATES_PATH, String SERVER_PATH, String
	 * STUDENT_PHOTOS_PATH) throws Exception { SimpleDateFormat monthFormat = new
	 * SimpleDateFormat("MMMM"); SimpleDateFormat yearFormat = new
	 * SimpleDateFormat("yyyy"); SimpleDateFormat dayFormat = new
	 * SimpleDateFormat("dd"); String certificateUniqueNumber = ""; SimpleDateFormat
	 * sdfr = new SimpleDateFormat("dd-MMM-yyyy"); SimpleDateFormat sdfrForCertNum =
	 * new SimpleDateFormat("ddMM"); String certificateDate = sdfr.format(new
	 * Date()); String certificateDateForNumber = sdfrForCertNum.format(new Date());
	 * String fileName = null;
	 * 
	 * // Image signature = Image.getInstance(new // URL(
	 * "https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/director-Signature.jpg"
	 * )); Image signature = Image.getInstance(new URL(SERVER_PATH +
	 * "exam/resources_2015/images/vc_signature.jpg")); // Image signature =
	 * Image.getInstance("d:/signature.jpg"); signature.scaleAbsolute(90, 42);
	 * String folderPath = ""; fileName = "Certificate_" + certificateDate + "_" +
	 * RandomStringUtils.randomAlphanumeric(5) + ".pdf"; folderPath =
	 * CERTIFICATES_PATH;
	 * 
	 * File folder = new File(folderPath); if (!folder.exists()) { folder.mkdirs();
	 * }
	 * 
	 * Document document = new Document(PageSize.A4); document.setMargins(50, 50,
	 * 170, 35);
	 * 
	 * PdfWriter writer = PdfWriter.getInstance(document, new
	 * FileOutputStream(folderPath + fileName));
	 * request.getSession().setAttribute("fileName", folderPath + fileName);
	 * document.open(); Paragraph blankLine = new Paragraph("\n");
	 * 
	 * SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd"); Format
	 * formatterMonth = new SimpleDateFormat("MMMM"); Format formatterYear = new
	 * SimpleDateFormat("yyyy"); Format formatterDay = new SimpleDateFormat("dd");
	 * String declareDate = formatterMonth.format(s.parse(resultDeclareDate)) + " "
	 * + formatterDay.format(s.parse(resultDeclareDate)) + ",  " +
	 * formatterYear.format(s.parse(resultDeclareDate)); Paragraph svkmManagement =
	 * new Paragraph(
	 * "We, the Chancellor, Vice Chancellor and Members of Board of Management of SVKM's Narsee Monjee Institute of Management Studies,"
	 * ); svkmManagement.setAlignment(Element.ALIGN_CENTER);
	 * document.add(svkmManagement); Paragraph certifyThatPara = new
	 * Paragraph("certify that");
	 * certifyThatPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(certifyThatPara); document.add(blankLine); if
	 * ("Online".equals(marksheet.getExamMode())) { // Photo not required for
	 * Offline exam. Image studentPhoto = null; PdfPTable studentPhotoTable = new
	 * PdfPTable(1); try { studentPhoto = Image.getInstance(new
	 * URL(marksheet.getImageUrl()));
	 * 
	 * } catch (Exception e) { // If not .jpg, try .jpeg try { studentPhoto =
	 * Image.getInstance(STUDENT_PHOTOS_PATH + marksheet.getSapid() + ".jpg"); }
	 * catch (Exception e2) { // IF this is also not found, then take photo from
	 * Admission system try { studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH +
	 * marksheet.getSapid() + ".jpeg"); } catch (Exception e1) { // TODO
	 * Auto-generated catch block throw new
	 * Exception("Error in getting image. Error : " + e.getMessage()); } //
	 * mailer.sendPhotoNotFoundEmail(student); } } final float heightOfImageInCm =
	 * 25; final float pointsValueHeight =
	 * com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);
	 * 
	 * final float widthOfImageInCm = 20; final float pointsValueWidth =
	 * com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);
	 * 
	 * studentPhoto.scaleAbsolute(pointsValueWidth, pointsValueHeight); PdfPCell
	 * studentPhotoCell = null; if (studentPhoto == null) { // In case URL was
	 * malformed or was not found studentPhotoCell = new PdfPCell(); } else {
	 * studentPhotoCell = new PdfPCell(studentPhoto); }
	 * 
	 * studentPhotoCell.setRowspan(6); studentPhotoCell.setBorder(0);
	 * studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * studentPhotoTable.addCell(studentPhotoCell);
	 * studentPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * document.add(studentPhotoTable); }
	 * 
	 * // Added to make first letter capital and rest of them small//
	 * 
	 * // String camelCaseFirstName = marksheet.getFirstName().substring(0, //
	 * 1).toUpperCase() + marksheet.getFirstName().substring(1).toLowerCase(); //
	 * String camelCaseSurname = marksheet.getLastName().substring(0, //
	 * 1).toUpperCase() + marksheet.getLastName().substring(1).toLowerCase(); //
	 * String camelCaseFathersName = marksheet.getFatherName().substring(0, //
	 * 1).toUpperCase() + marksheet.getFatherName().substring(1).toLowerCase(); //
	 * String camelCaseMothersName = marksheet.getMotherName().substring(0, //
	 * 1).toUpperCase() + marksheet.getMotherName().substring(1).toLowerCase();
	 * String camelCaseFirstName = marksheet.getFirstName().toUpperCase(); String
	 * camelCaseMothersName = marksheet.getMotherName().toUpperCase(); String
	 * camelCaseSurname = marksheet.getLastName().toUpperCase().replace(".", " ");
	 * String camelCaseHusbandname = marksheet.getHusbandName().toUpperCase();
	 * String camelCaseFathersName = marksheet.getFatherName().toUpperCase(); //
	 * End// Paragraph studentNamePara = new Paragraph(camelCaseFirstName + " " +
	 * camelCaseSurname, font18Bold);
	 * studentNamePara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(studentNamePara); document.add(blankLine);
	 * 
	 * Paragraph sonDaughterOfPara = null; if
	 * ("Spouse".equals(marksheet.getAdditionalInfo1()) &&
	 * "Female".equals(marksheet.getGender())) { sonDaughterOfPara = new Paragraph(
	 * "(Wife of Shri. " + camelCaseHusbandname + " and Daughter of " +
	 * camelCaseMothersName + " )"); } else if
	 * ("Parent".equals(marksheet.getAdditionalInfo1()) ||
	 * marksheet.getAdditionalInfo1() == null) { sonDaughterOfPara = new Paragraph(
	 * "(Son/Daughter of Shri. " + camelCaseFathersName + " and Smt. " +
	 * camelCaseMothersName + " )"); }
	 * 
	 * sonDaughterOfPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(sonDaughterOfPara); document.add(blankLine);
	 * 
	 * 
	 * String program = marksheet.getProgram(); ProgramBean programDetails =
	 * programAllDetailsMap.get(program);
	 * 
	 * String key = marksheet.getProgram() + "-" +
	 * marksheet.getPrgmStructApplicable(); ProgramBean programDetails =
	 * programAllDetailsMap.get(key);
	 * 
	 * String programname = programDetails.getProgramname(); String programType =
	 * programDetails.getProgramType();
	 * 
	 * Paragraph programDurationPara = new
	 * Paragraph("has been examined and found qualified for");
	 * programDurationPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(programDurationPara); document.add(blankLine); Paragraph
	 * programNamePara = new Paragraph(programname, font16Bold);
	 * programNamePara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(programNamePara);
	 * 
	 * Paragraph collegeNamePara = new
	 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
	 * font16Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(collegeNamePara);
	 * 
	 * 
	 * document.add(blankLine);
	 * 
	 * List<String> programTypes = new ArrayList(
	 * Arrays.asList("Professional Program", "Certificate", "Certificate Programs",
	 * "Bachelor Programs")); String learningMode = "Distance Learning mode"; if
	 * (programTypes.contains(programType)) { learningMode = "Online Learning mode";
	 * } if (programType.equalsIgnoreCase("Bachelor Programs") ||
	 * programType.equalsIgnoreCase("Executive Programs") ||
	 * programType.equalsIgnoreCase("PG Programs") ||
	 * programType.equalsIgnoreCase("Post Graduate Diploma Programs") ||
	 * programType.equalsIgnoreCase("Post Graduate Diploma") ||
	 * programType.equalsIgnoreCase("Master")) { programType = "degree"; } else if
	 * (programType.equalsIgnoreCase("Certificate Programs") ||
	 * programType.equalsIgnoreCase("Modular Program")) { programType =
	 * "certificate"; } else if
	 * (programType.equalsIgnoreCase("Professional Program")) { programType =
	 * "diploma"; } Chunk chunk1 = new Chunk("The said " + programType.toLowerCase()
	 * + " in "); Chunk chunk2 = new Chunk(learningMode, font12Bold); Chunk chunk3 =
	 * new Chunk(" under ");
	 * 
	 * Paragraph awardedPara1 = new Paragraph(); awardedPara1.add(chunk1);
	 * awardedPara1.add(chunk2); awardedPara1.add(chunk3); Paragraph awardedPara2 =
	 * new Paragraph(" NMIMS Global Access School for Continuing Education",
	 * font12Bold); Paragraph awardedPara3 = new Paragraph(
	 * "has been awarded to him/her in the month of " +
	 * formatterMonth.format(s.parse(resultDeclareDate)) + " of the year " +
	 * formatterYear.format(s.parse(resultDeclareDate)) + ".");
	 * 
	 * awardedPara1.setAlignment(Element.ALIGN_CENTER);
	 * awardedPara2.setAlignment(Element.ALIGN_CENTER);
	 * awardedPara3.setAlignment(Element.ALIGN_CENTER); document.add(awardedPara1);
	 * document.add(awardedPara2); document.add(awardedPara3);
	 * 
	 * document.add(blankLine);
	 * 
	 * String testimonyParaContent =
	 * "In testimony whereof is set the seal of the said"; String
	 * deemedToBeParaContent =
	 * "Deemed-to-be University and the signature of the said Vice Chancellor.";
	 * Paragraph testimonyPara = new Paragraph(testimonyParaContent); Paragraph
	 * deemedToBePara = new Paragraph(deemedToBeParaContent);
	 * testimonyPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(testimonyPara);
	 * deemedToBePara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(deemedToBePara);
	 * 
	 * document.add(blankLine); document.add(blankLine);
	 * 
	 * signature.setAlignment(Element.ALIGN_CENTER); document.add(signature);
	 * 
	 * Paragraph directorPara = new Paragraph("Vice Chancellor", font10);
	 * directorPara.setAlignment(Element.ALIGN_CENTER); document.add(directorPara);
	 * // document.add(blankLine); // document.add(blankLine);
	 * 
	 * Paragraph collegeNamePara = new
	 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
	 * font10Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(collegeNamePara);
	 * 
	 * Paragraph datePara = new Paragraph("Date: " + declareDate, font10);
	 * datePara.setAlignment(Element.ALIGN_CENTER); document.add(datePara); //
	 * document.add(blankLine); // document.add(blankLine);
	 * 
	 * certificateUniqueNumber = marksheet.getSapid() + "-" +
	 * certificateDateForNumber + RandomStringUtils.randomAlphanumeric(4); Paragraph
	 * studentNumberPara = new Paragraph( "Student No: " + marksheet.getSapid() +
	 * "  " + "Certificate Number:" + certificateUniqueNumber, font8);
	 * studentNumberPara.setAlignment(Element.ALIGN_CENTER);
	 * document.add(studentNumberPara);
	 * 
	 * Paragraph capmusPara = new Paragraph("Mumbai", font8);
	 * capmusPara.setAlignment(Element.ALIGN_CENTER); document.add(capmusPara);
	 * 
	 * document.newPage();
	 * 
	 * document.close();
	 * 
	 * return certificateUniqueNumber;
	 * 
	 * }
	 */
	public String generateCertificateAndReturnCertificateNumberForSingleStudent(MarksheetBean marksheet,String resultDeclareDate,
			HttpServletRequest request,HashMap<String, ProgramExamBean> programAllDetailsMap, HashMap<String, CenterExamBean> centersMap,
			String CERTIFICATES_PATH,String SERVER_PATH, String STUDENT_PHOTOS_PATH) throws Exception {
		SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
		String certificateUniqueNumber ="";
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat sdfrForCertNum = new SimpleDateFormat("ddMM");
		String certificateDate = sdfr.format(new Date());
		String certificateDateForNumber = sdfrForCertNum.format(new Date());
		String fileName = null;
		
		//Image signature = Image.getInstance(new URL("https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/director-Signature.jpg"));
//		Image signature = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/vc_signature.jpg"));
		//Image signature = Image.getInstance("d:/signature.jpg");
		//signature.scaleAbsolute(110, 35);
		String folderPath = "";
		fileName = "Certificate_" + certificateDate + "_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
		folderPath = CERTIFICATES_PATH;

		File folder = new File(folderPath);
		if (!folder.exists()) {   
			folder.mkdirs();   
		}  
		
		Document document = new Document(PageSize.A4);
		document.setMargins(50, 50, 170, 35);


		
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath+fileName));
		request.getSession().setAttribute("fileName", folderPath+fileName);
		document.open();
		Paragraph blankLine = new Paragraph("\n");
		
		
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		Format formatterMonth = new SimpleDateFormat("MMMM");
		Format formatterYear = new SimpleDateFormat("yyyy");
	    Format formatterDay = new SimpleDateFormat("dd");
	    String declareDate = formatterMonth.format(s.parse(resultDeclareDate)) +" "+ formatterDay.format(s.parse(resultDeclareDate))+",  "+formatterYear.format(s.parse(resultDeclareDate));
	  
		Paragraph certifyThatPara = new Paragraph("Certified that", font14);
		certifyThatPara.setAlignment(Element.ALIGN_CENTER);
		document.add(certifyThatPara);
		document.add(blankLine);
		if("Online".equals(marksheet.getExamMode())){
			//Photo not required for Offline exam.
			Image studentPhoto = null;
			PdfPTable studentPhotoTable = new PdfPTable(1);
			try {
				studentPhoto = Image.getInstance(new URL(marksheet.getImageUrl()));
				try {
					certificate.info("Certificate Generation"+ marksheet.getSapid());
					 studentPhoto=    ImageRotationUtil.getImageRotatedByOrientation(studentPhoto,marksheet.getImageUrl());
				}catch(Exception e) {
					certificate.info("Error FOund for"+ marksheet.getSapid()+" "+e);
				}
				
			} catch (Exception e) {
				//If not .jpg, try .jpeg
				try {
					studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH +marksheet.getSapid()+".jpg");
				} catch (Exception e2) {
					//IF this is also not found, then take photo from Admission system
					try {
						studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH +marksheet.getSapid()+".jpeg");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						throw new Exception("Error in getting image. Error : "+e.getMessage());
					}
					//mailer.sendPhotoNotFoundEmail(student);
				}
			}
			 final float heightOfImageInCm = 25;
			 final float pointsValueHeight = com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);
			 
			 final float widthOfImageInCm = 20;
			 final float pointsValueWidth = com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);
			 
			studentPhoto.scaleAbsolute( pointsValueWidth,pointsValueHeight);
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
			studentPhotoTable.addCell(studentPhotoCell);
			studentPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
			document.add(studentPhotoTable);
		}

		
		//Added to make first letter capital and rest of them small//
		
		//String camelCaseFirstName = marksheet.getFirstName().substring(0, 1).toUpperCase() + marksheet.getFirstName().substring(1).toLowerCase();
		//String camelCaseSurname = marksheet.getLastName().substring(0, 1).toUpperCase() + marksheet.getLastName().substring(1).toLowerCase();
		//String camelCaseFathersName = marksheet.getFatherName().substring(0, 1).toUpperCase() + marksheet.getFatherName().substring(1).toLowerCase();
		//String camelCaseMothersName = marksheet.getMotherName().substring(0, 1).toUpperCase() + marksheet.getMotherName().substring(1).toLowerCase();
		String camelCaseFirstName = marksheet.getFirstName().trim().toUpperCase();
		String camelCaseMothersName = marksheet.getMotherName().trim().toUpperCase();
		String camelCaseSurname = marksheet.getLastName().trim().toUpperCase().replace(".", " ");
		String camelCaseHusbandname = marksheet.getHusbandName().trim().toUpperCase();
		String camelCaseFathersName = marksheet.getFatherName().trim().toUpperCase();
		//End//
		Paragraph studentNamePara = new Paragraph(camelCaseFirstName + " " + camelCaseSurname, font18Bold);
		studentNamePara.setAlignment(Element.ALIGN_CENTER);
		document.add(studentNamePara);
		document.add(blankLine);
		
		Paragraph sonDaughterOfPara =null;
		if("Spouse".equals(marksheet.getAdditionalInfo1()) && "Female".equals(marksheet.getGender())){
			sonDaughterOfPara = new Paragraph("(Wife of Shri. " +camelCaseHusbandname +" and Daughter of "+camelCaseMothersName+" )");
		}else if("Parent".equals(marksheet.getAdditionalInfo1()) || marksheet.getAdditionalInfo1()==null){
			sonDaughterOfPara = new Paragraph("(Son/Daughter of Shri. " + camelCaseFathersName  +" and Smt. " + camelCaseMothersName + " )");
		}
		
		sonDaughterOfPara.setAlignment(Element.ALIGN_CENTER);
		document.add(sonDaughterOfPara);
		document.add(blankLine);
		
		/*String program = marksheet.getProgram();
		ProgramBean programDetails = programAllDetailsMap.get(program);*/
		String key = marksheet.getProgram()+"-"+marksheet.getPrgmStructApplicable();
		ProgramExamBean programDetails = programAllDetailsMap.get(key);
		Image signature = null;
		Paragraph directorPara = null;
		String deemedToBeParaContent = "";
		
		if(DIRECTOR_SIGNATURE_ELIGIBLE_PROGRAMS.contains(programDetails.getProgram())) {
			signature = Image.getInstance(
					"https://staticfilesportal.s3.ap-south-1.amazonaws.com/assets/images/Signature.png");
			deemedToBeParaContent = "Deemed-to-be University and the signature of the said Director.";
			directorPara=new Paragraph("Director", font10);
		}
		else {
			
			signature = Image.getInstance(
					"https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/vc_signature.jpg");
			deemedToBeParaContent = "Deemed-to-be University and the signature of the said Vice Chancellor.";

			directorPara=new Paragraph("Vice Chancellor", font10);
			
		}
		signature.scaleAbsolute(80, 39);

		String programname = programDetails.getProgramname();
		String programType = programDetails.getProgramType();
		String specialization =programDetails.getSpecializationName();
		Paragraph programDurationPara = new Paragraph();
		if(MODULAR_PDDM_MASTERKEYS.contains(programDetails.getConsumerProgramStructureId()))
		{
			programDurationPara = new Paragraph("has been examined and found qualified for the "
					+ programDetails.getProgramDuration() + " " + programDetails.getProgramDurationUnit());
		}
		else
		{
			programDurationPara = new Paragraph("has been examined and found qualified for");
		}
//		Paragraph programDurationPara = new Paragraph("has been examined and found qualified for");
		programDurationPara.setAlignment(Element.ALIGN_CENTER);
		document.add(programDurationPara);
		document.add(blankLine);
		document.add(blankLine);
		
		String arr[] = programname.split("[\\(\\-\\)]");
		Paragraph programNamePara = new Paragraph(arr[0].trim(), font16Bold);
		programNamePara.setAlignment(Element.ALIGN_CENTER);
		document.add(programNamePara);
		
		if(arr.length > 1)
		{
			Paragraph middleProgramNamePara = new Paragraph("(" + arr[1].trim()+")", font16Bold);
			middleProgramNamePara.setAlignment(Element.ALIGN_CENTER);
			document.add(middleProgramNamePara);
		}
		
		if(!StringUtils.isEmpty(specialization)) {
			Paragraph specializationN = new Paragraph("("+specialization.trim()+")", font16Bold);
			specializationN.setAlignment(Element.ALIGN_CENTER);
			document.add(specializationN);
		}
		List<String> programTypes =new ArrayList(Arrays.asList("Bachelor Programs","Executive Programs","PG Programs","Master"));
			
		if(programTypes.contains(programType)) {
				programType="degree";
		}
		else
		{
			programType=programType.toLowerCase();
		}
		
		/*Paragraph collegeNamePara = new Paragraph("(NMIMS Global Access - School for Continuing Education)", font16Bold);
		collegeNamePara.setAlignment(Element.ALIGN_CENTER);
		document.add(collegeNamePara);*/
		document.add(blankLine);
		document.add(blankLine);
		
		String currentMonth = new SimpleDateFormat("MMMM").format(new Date()) + "";
		String currentYear = Calendar.getInstance().get(Calendar.YEAR) + "";
		
		String awardedParaContent = "The said " + programType + " has been awarded to him/her";
		String monthAndYearContent = " in the month of " + formatterMonth.format(s.parse(resultDeclareDate)) + " of the year " + formatterYear.format(s.parse(resultDeclareDate))+".";
		Paragraph awardedPara = new Paragraph(awardedParaContent);
		Paragraph monthAndYearPara = new Paragraph(monthAndYearContent);
		awardedPara.setAlignment(Element.ALIGN_CENTER);
		document.add(awardedPara);
		monthAndYearPara.setAlignment(Element.ALIGN_CENTER);
		document.add(monthAndYearPara);
		document.add(blankLine);
		
		String testimonyParaContent = "In testimony whereof is set the seal of the said";
		Paragraph testimonyPara = new Paragraph(testimonyParaContent);
		Paragraph deemedToBePara = new Paragraph(deemedToBeParaContent);
		testimonyPara.setAlignment(Element.ALIGN_CENTER);
		document.add(testimonyPara);
		deemedToBePara.setAlignment(Element.ALIGN_CENTER);
		document.add(deemedToBePara);
		
		document.add(blankLine);
		document.add(blankLine);
		
		signature.setAlignment(Element.ALIGN_CENTER);
		document.add(signature);
		
//		Paragraph directorPara = new Paragraph("Vice Chancellor", font10);
		directorPara.setAlignment(Element.ALIGN_CENTER);
		document.add(directorPara);
		//document.add(blankLine);
		//document.add(blankLine);
		/*
		 * Paragraph collegeNamePara = new
		 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
		 * font10Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
		 * document.add(collegeNamePara);
		 */
		Paragraph datePara = new Paragraph("Date: " + declareDate, font10);
		datePara.setAlignment(Element.ALIGN_CENTER);
		document.add(datePara);
		//document.add(blankLine);
		//document.add(blankLine);
		
		certificateUniqueNumber = marksheet.getSapid()+"-"+certificateDateForNumber+RandomStringUtils.randomAlphanumeric(4);
		Paragraph studentNumberPara = new Paragraph("Student No: " + marksheet.getSapid()+"  "+"Certificate Number:"+certificateUniqueNumber, font10);
		studentNumberPara.setAlignment(Element.ALIGN_CENTER);
		document.add(studentNumberPara);
		
		generateCertficateBackSide(document,marksheet.getSapid(),marksheet.getEnrollmentMonth(),
				marksheet.getEnrollmentYear(),resultDeclareDate,false,writer,true,marksheet.getProgram());
		
		document.newPage();
		
		document.close();
		
		return certificateUniqueNumber;
		
	}
	
//	public String generateCertificateAndReturnCertificateNumberForSingleStudentSelfMBWX(MarksheetBean marksheet,
//			String resultDeclareDate, HashMap<String, ProgramExamBean> programAllDetailsMap,
//			HashMap<String, CenterExamBean> centersMap, String CERTIFICATES_PATH, String SERVER_PATH,
//			String STUDENT_PHOTOS_PATH) throws Exception {
//
//		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
//		SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
//		String response = null;
//
//		String certificateUniqueNumber = "";
//		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
//		SimpleDateFormat sdfrForCertNum = new SimpleDateFormat("ddMM");
//		String certificateDate = sdfr.format(new Date());
//		String certificateDateForNumber = sdfrForCertNum.format(new Date());
//		String fileName = null;
//
//		String folderPath = "";
//		fileName = "Certificate_Self_" + certificateDate + "_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
//		folderPath = CERTIFICATES_PATH;
//
//		Document document = new Document(PageSize.A4);
//		document.setMargins(50, 50, 170, 35);
//
//		File folder = new File(folderPath);
//		if (!folder.exists()) {
//			folder.mkdirs();
//		}
//
//		SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
//
//		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath + fileName));
//		response = folderPath + fileName;
//		response = SERVER_PATH + "Certificates/" + fileName;
//
//		document.open();
//
//		Phrase watermark = new Phrase("Certificate Downloaded from NGASCE Student Portal",
//				new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
//		PdfContentByte canvas = writer.getDirectContent();
//		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 420, 30);
//		Paragraph blankLine = new Paragraph("\n");
//
//		PdfPTable headerTable = new PdfPTable(1);
//
//		Image logo = null;
//		logo = Image.getInstance(
//				new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nmims_logo_new.jpg"));
//		logo.scaleAbsolute(142, 40);
//		logo.setAlignment(Element.ALIGN_CENTER);
//
//		PdfPCell logoCell = new PdfPCell();
//		logoCell.addElement(logo);
//		logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//		logoCell.setBorder(0);
//		headerTable.addCell(logoCell);
//		headerTable.completeRow();
//		headerTable.setWidthPercentage(100);
//		headerTable.setSpacingBefore(10);
//		headerTable.setSpacingAfter(10);
//		document.add(headerTable);
//
//		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
//		Format formatterMonth = new SimpleDateFormat("MMMM");
//		Format formatterYear = new SimpleDateFormat("yyyy");
//		Format formatterDay = new SimpleDateFormat("dd");
//		String declareDate = formatterMonth.format(s.parse(resultDeclareDate)) + " "
//				+ formatterDay.format(s.parse(resultDeclareDate)) + ",  "
//				+ formatterYear.format(s.parse(resultDeclareDate));
//
//		Paragraph certifyThatPara = new Paragraph("Certified that", font14);
//		certifyThatPara.setAlignment(Element.ALIGN_CENTER);
//		document.add(certifyThatPara);
//		document.add(blankLine);
//		if ("Online".equals(marksheet.getExamMode())) {
//			// Photo not required for Offline exam.
//			Image studentPhoto = null;
//			PdfPTable studentPhotoTable = new PdfPTable(1);
//			try { 
//				
//				studentPhoto = Image.getInstance(new URL(marksheet.getImageUrl()));
//				try {
//					certificate.info("Certificate Generation"+ marksheet.getSapid());
//		        studentPhoto=    ImageRotationUtil.getImageRotatedByOrientation(studentPhoto,marksheet.getImageUrl());
//				}catch(Exception mee) {
//					certificate.info("Error FOund for"+ marksheet.getSapid()+" "+mee);
//				}
//			} catch (Exception e) {
//			
//				// If not .jpg, try .jpeg
//				try {
//					studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + marksheet.getSapid() + ".jpg");
//				} catch (Exception e2) {
//					// IF this is also not found, then take photo from Admission system
//					try {
//						studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + marksheet.getSapid() + ".jpeg");
//					} catch (Exception e1) {
//						// TODO Auto-generated catch block
//						throw new Exception("Error in getting image. Error : " + e.getMessage());
//					
//				}
//					// mailer.sendPhotoNotFoundEmail(student);
//				}
//			}
//			final float heightOfImageInCm = 25;
//			final float pointsValueHeight = com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);
//
//			final float widthOfImageInCm = 20;
//			final float pointsValueWidth = com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);
//
//			studentPhoto.scaleAbsolute(pointsValueWidth, pointsValueHeight);
//			PdfPCell studentPhotoCell = null;
//			if (studentPhoto == null) {
//				// In case URL was malformed or was not found
//				studentPhotoCell = new PdfPCell();
//			} else {
//				studentPhotoCell = new PdfPCell(studentPhoto);
//			}
//
//			studentPhotoCell.setRowspan(6);
//			studentPhotoCell.setBorder(0);
//			studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			studentPhotoTable.addCell(studentPhotoCell);
//			studentPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
//			document.add(studentPhotoTable);
//		}
//
//		// Added to make first letter capital and rest of them small//
//
//		// String camelCaseFirstName = marksheet.getFirstName().substring(0,
//		// 1).toUpperCase() + marksheet.getFirstName().substring(1).toLowerCase();
//		// String camelCaseSurname = marksheet.getLastName().substring(0,
//		// 1).toUpperCase() + marksheet.getLastName().substring(1).toLowerCase();
//		// String camelCaseFathersName = marksheet.getFatherName().substring(0,
//		// 1).toUpperCase() + marksheet.getFatherName().substring(1).toLowerCase();
//		// String camelCaseMothersName = marksheet.getMotherName().substring(0,
//		// 1).toUpperCase() + marksheet.getMotherName().substring(1).toLowerCase();
//		String fullNameOfStudent = marksheet.getFirstName().trim().toUpperCase() + " "
//				+ marksheet.getLastName().trim().toUpperCase().replace(".", " ");
//		String motherName = marksheet.getMotherName().trim().toUpperCase();
//		String fathersName = marksheet.getFatherName().trim().toUpperCase();
//		String husbandName = "";
//		try {
//			husbandName = marksheet.getHusbandName().trim().toUpperCase();
//		} catch (Exception e) {
//			
//		}
//
//		// End//
//		Paragraph studentNamePara = new Paragraph(fullNameOfStudent, font18Bold);
//		studentNamePara.setAlignment(Element.ALIGN_CENTER);
//		document.add(studentNamePara);
//		document.add(blankLine);
//
//		Paragraph sonDaughterOfPara = null;
//		if ("Spouse".equals(marksheet.getAdditionalInfo1()) && "Female".equals(marksheet.getGender())) {
//			sonDaughterOfPara = new Paragraph(
//					"(Wife of Shri. " + husbandName + " and Daughter of " + motherName + " )");
//		} else if ("Parent".equals(marksheet.getAdditionalInfo1()) || marksheet.getAdditionalInfo1() == null) {
//			sonDaughterOfPara = new Paragraph(
//					"(Son/Daughter of Shri. " + fathersName + " and Smt. " + motherName + " )");
//		}
//
//		sonDaughterOfPara.setAlignment(Element.ALIGN_CENTER);
//		document.add(sonDaughterOfPara);
//		document.add(blankLine);
//
//		/*
//		 * String program = marksheet.getProgram(); ProgramBean programDetails =
//		 * programAllDetailsMap.get(program);
//		 */
//		String key = marksheet.getProgram() + "-" + marksheet.getPrgmStructApplicable();
//		ProgramExamBean programDetails = programAllDetailsMap.get(key);
//
//		String programname = programDetails.getProgramname();
//		String programType = programDetails.getProgramType();
//		if (programDetails.getProgram().equalsIgnoreCase("MBA - WX")) {
//			programType = "MBA (Wx)";
//		}
//		// Paragraph programDurationPara = new Paragraph("has been examined and found
//		// qualified for the "+ programDetails.getProgramDuration() + " " +
//		// programDetails.getProgramDurationUnit());
//		Paragraph programDurationPara = new Paragraph("");
//
//		if ("EPBM".equalsIgnoreCase(marksheet.getProgram()) || "MPDV".equalsIgnoreCase(marksheet.getProgram())) {
//			// programDurationPara = new Paragraph("has successfully completed the "+
//			// programDetails.getProgramDuration() + " " +
//			// programDetails.getProgramDurationUnit());
//			if (marksheet.isPassForExceutive()) {
//				programDurationPara = new Paragraph("has successfully completed the "
//						+ programDetails.getProgramDuration() + " " + programDetails.getProgramDurationUnit());
//			} else {
//				programDurationPara = new Paragraph("has participated in the " + programDetails.getProgramDuration()
//						+ " " + programDetails.getProgramDurationUnit());
//			}
//		} else {
//			//commented new template
//			//programDurationPara = new Paragraph("has been examined and found qualified for");
//			programDurationPara = new Paragraph("has been examined and found qualified for the "+ programDetails.getProgramDuration() + " " + programDetails.getProgramDurationUnit());
//			
//		}
////		programDurationPara = new Paragraph("has been examined and found qualified for");
//
//		programDurationPara.setAlignment(Element.ALIGN_CENTER);
//
//		document.add(programDurationPara);
//		document.add(blankLine);
//		document.add(blankLine);
//
//		Paragraph programNamePara = new Paragraph(programname, font16Bold);
//		programNamePara.setAlignment(Element.ALIGN_CENTER);
//		document.add(programNamePara);
//
//		if (programDetails.getProgram().equalsIgnoreCase("MBA - WX")) {
//			Paragraph specialization = new Paragraph("with specialization in " + marksheet.getSpecialisation());
//			specialization.setAlignment(Element.ALIGN_CENTER);
//			document.add(specialization);
//		}
//
//		/*
//		 * Paragraph collegeNamePara = new
//		 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
//		 * font16Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
//		 * document.add(collegeNamePara);
//		 */
//		document.add(blankLine);
//		// document.add(blankLine);
//
//		if ("EPBM".equalsIgnoreCase(marksheet.getProgram()) || "MPDV".equalsIgnoreCase(marksheet.getProgram())) {
//			String countedByParaContent = "conducted jointly by";
//			String companyNamesContent = "NMIMS Deemed-to-be University and SAS Institute (l) Pvt. Ltd.";
//			Paragraph countedByParaContentPara = new Paragraph(countedByParaContent);
//			Paragraph companyNamesContentPara = new Paragraph(companyNamesContent);
//			countedByParaContentPara.setAlignment(Element.ALIGN_CENTER);
//			document.add(countedByParaContentPara);
//			companyNamesContentPara.setAlignment(Element.ALIGN_CENTER);
//			document.add(companyNamesContentPara);
//		} else {
//			String awardedParaContent = "The said " + programType + " degree has been awarded to him/her";
//			String monthAndYearContent = " in the month of " + formatterMonth.format(s.parse(resultDeclareDate))
//					+ " of the year " + formatterYear.format(s.parse(resultDeclareDate)) + ".";
//			/*
//			 * String monthAndYearContent = " in the month of August of the year " +
//			 * formatterYear.format(s.parse(date))+".";
//			 */
//			Paragraph awardedPara = new Paragraph(awardedParaContent);
//			Paragraph monthAndYearPara = new Paragraph(monthAndYearContent);
//			awardedPara.setAlignment(Element.ALIGN_CENTER);
//			document.add(awardedPara);
//			monthAndYearPara.setAlignment(Element.ALIGN_CENTER);
//			document.add(monthAndYearPara);
//		}
////		String awardedParaContent = "The said " + programType + " has been awarded to him/her";
////		String monthAndYearContent = " in the month of " + formatterMonth.format(s.parse(resultDeclareDate))
////				+ " of the year " + formatterYear.format(s.parse(resultDeclareDate)) + ".";
////		/*
////		 * String monthAndYearContent = " in the month of August of the year " +
////		 * formatterYear.format(s.parse(date))+".";
////		 */
////		Paragraph awardedPara = new Paragraph(awardedParaContent);
////		Paragraph monthAndYearPara = new Paragraph(monthAndYearContent);
////		awardedPara.setAlignment(Element.ALIGN_CENTER);
////		document.add(awardedPara);
////		monthAndYearPara.setAlignment(Element.ALIGN_CENTER);
////		document.add(monthAndYearPara);
////		
//		document.add(blankLine);
//
//		String testimonyParaContent = "In testimony whereof is set the seal of the said";
//		String deemedToBeParaContent = "";
//
//		/*
//		 * if("EPBM".equalsIgnoreCase(student.getProgram()) ||
//		 * "MPDV".equalsIgnoreCase(student.getProgram())) { deemedToBeParaContent =
//		 * "Deemed-to-be University and the signature of the Authorised Signatory.";
//		 * }else {
//		 */
//		deemedToBeParaContent = "Deemed-to-be University and the signature of the said Vice Chancellor.";
//		/* } */
//
//		Paragraph testimonyPara = new Paragraph(testimonyParaContent);
//		Paragraph deemedToBePara = new Paragraph(deemedToBeParaContent);
//		testimonyPara.setAlignment(Element.ALIGN_CENTER);
//		// document.add(testimonyPara);
//		deemedToBePara.setAlignment(Element.ALIGN_CENTER);
//		// document.add(deemedToBePara);
//
//		// document.add(blankLine);
//		// document.add(blankLine);
//
//		Paragraph directorPara = new Paragraph("Vice Chancellor", font10);
//		directorPara.setAlignment(Element.ALIGN_CENTER);
//		// document.add(directorPara);
//		// document.add(blankLine);
//		// document.add(blankLine);
//		/*
//		 * Paragraph collegeNamePara = new
//		 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
//		 * font10Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
//		 * document.add(collegeNamePara);
//		 */
//		Paragraph datePara = new Paragraph("Date: " + declareDate, font10);
//		datePara.setAlignment(Element.ALIGN_CENTER);
//		document.add(datePara);
//		// document.add(blankLine);
//		// document.add(blankLine);
//
//		certificateUniqueNumber = marksheet.getSapid() + "-" + certificateDateForNumber
//				+ RandomStringUtils.randomAlphanumeric(4);
//		Paragraph studentNumberPara = new Paragraph(
//				"Student No: " + marksheet.getSapid() + "  " + "Certificate Number:" + certificateUniqueNumber, font10);
//		studentNumberPara.setAlignment(Element.ALIGN_CENTER);
//		document.add(studentNumberPara);
//
//		if ("EPBM".equalsIgnoreCase(marksheet.getProgram()) || "MPDV".equalsIgnoreCase(marksheet.getProgram())) {
//			// Photo not required for Offline exam.
//
//			PdfPTable sasPhotoTable = new PdfPTable(1);
//
//			/*
//			 * final float heightOfImageInCm = 25; final float pointsValueHeight =
//			 * com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);
//			 * 
//			 * final float widthOfImageInCm = 20; final float pointsValueWidth =
//			 * com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);
//			 */
//
//			// studentPhoto.scaleAbsolute( pointsValueWidth,pointsValueHeight);
//			PdfPCell sasPhotoCell = null;
////			if(logoSAS == null){
////				//In case URL was malformed or was not found
////				sasPhotoCell = new PdfPCell();
////			}else{
////				sasPhotoCell = new PdfPCell(logoSAS);
////			}
//
//			sasPhotoCell.setRowspan(6);
//			sasPhotoCell.setBorder(0);
//			sasPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			sasPhotoTable.addCell(sasPhotoCell);
//			sasPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
//			document.add(sasPhotoTable);
//		}
//
////		generateCertficateBackSide(document,marksheet.getSapid(),marksheet.getEnrollmentMonth(),
////				marksheet.getEnrollmentYear(),resultDeclareDate,true,writer);
//		
//		document.newPage();
//
//		document.close();
//
//		try {
//
//			/*To Transfer the local files to s3 server
//			 * @Param:- filePath - Whole Path File on local eg. E:\Content\Management Theory and Practice\TRY.pdf
//			 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
//			 * @param:- bucketName- Name of the bucket present in the s3
//			 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
//			 */
//
//			String filePath = CERTIFICATES_PATH + fileName;
//			String keyName = "Certificates/"+fileName;
//			folderPath = "Certificates/";
//
//			HashMap<String,String> awsUploadResponse = amazonHelper.uploadLocalFile(filePath, keyName, AWS_CERTIFICATES_BUCKET, folderPath);
//	
//			if( !awsUploadResponse.get("status").equals("error") ) {
//
//				File f = new File(filePath);
//				f.delete();
//				response = awsUploadResponse.get("url");
//				
//			}
//			
//		}catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		
//		return response;
//
//	}
	
	public String generateCertificateAndReturnCertificateNumberForSingleStudentSelfMBWX(MarksheetBean marksheet,
			String resultDeclareDate, HashMap<String, ProgramExamBean> programAllDetailsMap,
			HashMap<String, CenterExamBean> centersMap, String CERTIFICATES_PATH, String SERVER_PATH,
			String STUDENT_PHOTOS_PATH) throws Exception {

		String response = null;
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		String certificateDate = sdfr.format(new Date());
		String fileName = null;

		String folderPath = "";
		fileName = "Certificate_Self_" + certificateDate + "_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
		folderPath = CERTIFICATES_PATH;

		Document document = new Document(PageSize.A4);
		document.setMargins(50, 50, 170, 35);

		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath + fileName));
		response = folderPath + fileName;
		response = SERVER_PATH + "Certificates/" + fileName;

		document.open();

		Phrase watermark = new Phrase("Certificate Downloaded from NGASCE Student Portal",
				new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
		PdfContentByte canvas = writer.getDirectContent();
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 420, 30);
		
		//To add static disclaimer at bottom of pdf
		addDisclaimer(document,writer);
		
		Paragraph blankLine = new Paragraph("\n");

		PdfPTable headerTable = new PdfPTable(1);

		Image logo = null;
		logo = Image.getInstance(
				new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nmims_logo_new.jpg"));
		logo.scaleAbsolute(142, 40);
		logo.setAlignment(Element.ALIGN_CENTER);

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

		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		Format formatterMonth = new SimpleDateFormat("MMMM");
		Format formatterYear = new SimpleDateFormat("yyyy");

		Paragraph certifyThatPara = new Paragraph("Certified that", font14);
		certifyThatPara.setAlignment(Element.ALIGN_CENTER);
		document.add(certifyThatPara);
		document.add(blankLine);
		if ("Online".equals(marksheet.getExamMode())) {
			// Photo not required for Offline exam.
			Image studentPhoto = null;
			PdfPTable studentPhotoTable = new PdfPTable(1);
			try { 
				
				studentPhoto = Image.getInstance(new URL(marksheet.getImageUrl()));
				try {
					certificate.info("Certificate Generation"+ marksheet.getSapid());
		        studentPhoto=    ImageRotationUtil.getImageRotatedByOrientation(studentPhoto,marksheet.getImageUrl());
				}catch(Exception mee) {
					certificate.info("Error FOund for"+ marksheet.getSapid()+" "+mee);
				}
			} catch (Exception e) {
			
				// If not .jpg, try .jpeg
				try {
					studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + marksheet.getSapid() + ".jpg");
				} catch (Exception e2) {
					// IF this is also not found, then take photo from Admission system
					try {
						studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + marksheet.getSapid() + ".jpeg");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						throw new Exception("Error in getting image. Error : " + e.getMessage());
					
				}
					// mailer.sendPhotoNotFoundEmail(student);
				}
			}
			final float heightOfImageInCm = 25;
			final float pointsValueHeight = com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);

			final float widthOfImageInCm = 20;
			final float pointsValueWidth = com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);

			studentPhoto.scaleAbsolute(pointsValueWidth, pointsValueHeight);
			PdfPCell studentPhotoCell = null;
			if (studentPhoto == null) {
				// In case URL was malformed or was not found
				studentPhotoCell = new PdfPCell();
			} else {
				studentPhotoCell = new PdfPCell(studentPhoto);
			}

			studentPhotoCell.setRowspan(6);
			studentPhotoCell.setBorder(0);
			studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			studentPhotoTable.addCell(studentPhotoCell);
			studentPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
			document.add(studentPhotoTable);
		}

		String fullNameOfStudent = marksheet.getFirstName().trim().toUpperCase() + " "
				+ marksheet.getLastName().trim().toUpperCase().replace(".", " ");
		String motherName = marksheet.getMotherName().trim().toUpperCase();
		String fathersName = marksheet.getFatherName().trim().toUpperCase();
		String husbandName = "";
		try {
			husbandName = marksheet.getHusbandName().trim().toUpperCase();
		} catch (Exception e) {
			husbandName = "NA";
		}

		// End//
		Paragraph studentNamePara = new Paragraph(fullNameOfStudent, font18Bold);
		studentNamePara.setAlignment(Element.ALIGN_CENTER);
		document.add(studentNamePara);
		document.add(blankLine);

		Paragraph sonDaughterOfPara = null;
		if ("Spouse".equals(marksheet.getAdditionalInfo1()) && "Female".equals(marksheet.getGender())) {
			sonDaughterOfPara = new Paragraph(
					"(Wife of Shri. " + husbandName + " and Daughter of " + motherName + " )");
		} else if ("Parent".equals(marksheet.getAdditionalInfo1()) || marksheet.getAdditionalInfo1() == null) {
			sonDaughterOfPara = new Paragraph(
					"(Son/Daughter of Shri. " + fathersName + " and Smt. " + motherName + " )");
		}

		sonDaughterOfPara.setAlignment(Element.ALIGN_CENTER);
		document.add(sonDaughterOfPara);
		document.add(blankLine);

		String key = marksheet.getProgram() + "-" + marksheet.getPrgmStructApplicable();
		ProgramExamBean programDetails = programAllDetailsMap.get(key);

		String programname = programDetails.getProgramname();
		String programType = programDetails.getProgramType();
		
		Paragraph programDurationPara = new Paragraph("");

		if (MODULAR_PDDM_MASTERKEYS.contains(marksheet.getConsumerProgramStructureId())) {
			programDurationPara = new Paragraph("has been examined and found qualified for the "+ programDetails.getProgramDuration() + " " + programDetails.getProgramDurationUnit());
		} else {
			programDurationPara = new Paragraph("has been examined and found qualified for");
		}

		programDurationPara.setAlignment(Element.ALIGN_CENTER);

		document.add(programDurationPara);
		document.add(blankLine);
		document.add(blankLine);

		if(PG_AND_MBA_DISTANCE_MASTERKEYS.contains(marksheet.getConsumerProgramStructureId()))
		{
			String[] splittedProgram = programname.split("[\\(\\-\\)]");
			
			Paragraph programNamePara = new Paragraph(splittedProgram[0].trim(), font16Bold);
			programNamePara.setAlignment(Element.ALIGN_CENTER);
			document.add(programNamePara);
			if(splittedProgram.length > 1)
			{
				Paragraph specialization = new Paragraph("("+splittedProgram[1].trim()+")",font16Bold);
				specialization.setAlignment(Element.ALIGN_CENTER);
				document.add(specialization);
			}
		}
		else if (programDetails.getProgram().equalsIgnoreCase("MBA - X")
				|| programDetails.getProgram().equalsIgnoreCase("MBA - WX")) {

			String programnameLine1 = programDetails.getProgram().equalsIgnoreCase("MBA - WX")
					? "Master of Business Administration"
					: "MASTER OF BUSINESS ADMINISTRATION";
			String programnameLine2 = programDetails.getProgram().equalsIgnoreCase("MBA - WX")
					? "FOR WORKING EXECUTIVES"
					: "";
			String specialization = programDetails.getProgram().equalsIgnoreCase("MBA - WX")
					? "(" + marksheet.getSpecialisation() + ")"
					: "FOR EXECUTIVES (Business Analytics)";

			Paragraph programNamePara1 = new Paragraph(programnameLine1, font16Bold);
			programNamePara1.setAlignment(Element.ALIGN_CENTER);
			document.add(programNamePara1);

			Paragraph programNamePara2 = new Paragraph(programnameLine2, font12Bold);
			programNamePara2.setAlignment(Element.ALIGN_CENTER);
			document.add(programNamePara2);

			Paragraph specializationPara = new Paragraph(specialization, font12Bold);
			specializationPara.setAlignment(Element.ALIGN_CENTER);
			document.add(specializationPara);
		}
		else
		{
			Paragraph programNamePara = new Paragraph(programname, font16Bold);
			programNamePara.setAlignment(Element.ALIGN_CENTER);
			document.add(programNamePara);
		}
		
		document.add(blankLine);

		if ("EPBM".equalsIgnoreCase(marksheet.getProgram()) || "MPDV".equalsIgnoreCase(marksheet.getProgram())) {
			String countedByParaContent = "conducted jointly by";
			String companyNamesContent = "NMIMS Deemed-to-be University and SAS Institute (l) Pvt. Ltd.";
			Paragraph countedByParaContentPara = new Paragraph(countedByParaContent);
			Paragraph companyNamesContentPara = new Paragraph(companyNamesContent);
			countedByParaContentPara.setAlignment(Element.ALIGN_CENTER);
			document.add(countedByParaContentPara);
			companyNamesContentPara.setAlignment(Element.ALIGN_CENTER);
			document.add(companyNamesContentPara);
		} else {
			String awardedParaContent = "The said " + programType + " has been awarded to him/her";
			if(MASTER_PROGRAM_TYPE.contains(programType))
				awardedParaContent = "The said degree has been awarded to him/her"; // programType and `degree` word only for master programs
			String monthAndYearContent = " in the month of " + formatterMonth.format(s.parse(resultDeclareDate))
					+ " of the year " + formatterYear.format(s.parse(resultDeclareDate)) + ".";
			
			Paragraph awardedPara = new Paragraph(awardedParaContent);
			Paragraph monthAndYearPara = new Paragraph(monthAndYearContent);
			awardedPara.setAlignment(Element.ALIGN_CENTER);
			document.add(awardedPara);
			monthAndYearPara.setAlignment(Element.ALIGN_CENTER);
			document.add(monthAndYearPara);
		}
		
//		generateCertficateBackSide(document,marksheet.getSapid(),marksheet.getEnrollmentMonth(),
//				marksheet.getEnrollmentYear(),resultDeclareDate,true,writer,true);
		
		document.newPage();

		document.close();

		try {

			/*To Transfer the local files to s3 server
			 * @Param:- filePath - Whole Path File on local eg. E:\Content\Management Theory and Practice\TRY.pdf
			 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
			 * @param:- bucketName- Name of the bucket present in the s3
			 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
			 */

			String filePath = CERTIFICATES_PATH + fileName;
			String keyName = "Certificates/"+fileName;
			folderPath = "Certificates/";

			HashMap<String,String> awsUploadResponse = amazonHelper.uploadLocalFile(filePath, keyName, AWS_CERTIFICATES_BUCKET, folderPath);
	
			if( !awsUploadResponse.get("status").equals("error") ) {

				File f = new File(filePath);
				f.delete();
				response = awsUploadResponse.get("url");
				certificate.info("Final Certificate are generated successful to share for sapid: "+marksheet.getSapid());
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			certificate.error("Error in generating Final Certificate for student to share: "+e);
		}
		
		return response;

	}
	
	
	public String generatePDDMCertificateAndReturnCertificateNumber(List<MarksheetBean> studentForSRList,
			HashMap<String, String> mapOfSapIDAndResultDate, HttpServletRequest request,
			HashMap<String, ProgramExamBean> programAllDetailsMap, HashMap<String, CenterExamBean> centersMap,
			String MARKSHEETS_PATH, List<String> PDDM_PROGRAMS_LIST, String STUDENT_PHOTOS_PATH) throws Exception {

		String certificateUniqueNumber = "";
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat sdfrForCertNum = new SimpleDateFormat("ddMM");
		String certificateDate = sdfr.format(new Date());
		String certificateDateForNumber = sdfrForCertNum.format(new Date());
		String fileName = null;

		boolean isHaveLogo = false;
		
		try {
		
		
		String folderPath = "";
		fileName = "Certificate_" + certificateDate + "_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
		folderPath = MARKSHEETS_PATH + "Certificates/";

		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		Document document = new Document(PageSize.A4);
		if (request.getAttribute("logoRequired") == null
				|| ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("N")) {
			document.setMargins(50, 50, 176, 35);
			// document.setMargins(50, 50, 70, 35);
		} else {
			document.setMargins(50, 50, 45, 35);
			isHaveLogo = true;
		}

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath + fileName));
		request.getSession().setAttribute("fileName", folderPath + fileName);
		document.open();
		PdfPTable headerTable = new PdfPTable(1);
		if (request.getAttribute("logoRequired") != null
				&& ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
			Image logo = null;
			logo = Image.getInstance(
					new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nmims_logo_new.jpg"));
			logo.scaleAbsolute(142, 40);
			logo.setAlignment(Element.ALIGN_CENTER);

			PdfPCell logoCell = new PdfPCell();
			logoCell.addElement(logo);
			logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			logoCell.setBorder(0);
			headerTable.addCell(logoCell);
			headerTable.completeRow();
			headerTable.setWidthPercentage(100);
			headerTable.setSpacingBefore(10);
			headerTable.setSpacingAfter(10);
		}

		Paragraph blankLine = new Paragraph("\n");

		for (MarksheetBean student : studentForSRList) {
			//
			if (request.getAttribute("logoRequired") != null
					&& ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
				document.add(headerTable);
			}
			
			String key = student.getProgram() + "-" + student.getPrgmStructApplicable();
			ProgramExamBean programDetails = programAllDetailsMap.get(key);
			String programname = programDetails.getProgramname();
			String programType = programDetails.getProgramType();
			String programDurationUnit = programDetails.getProgramDurationUnit();
			String programDuration = programDetails.getProgramDuration();
			String program = programDetails.getProgram();
			Image signature = null;
			Paragraph directorPara = null;

			String deemedToBeParaContent = "";
			
			if(DIRECTOR_SIGNATURE_ELIGIBLE_PROGRAMS.contains(programDetails.getProgram())) {
				signature = Image.getInstance(
						"https://staticfilesportal.s3.ap-south-1.amazonaws.com/assets/images/Signature.png");
				deemedToBeParaContent = "Deemed-to-be University and the signature of the said Director.";
				directorPara=new Paragraph("Director", font10);
			}
			else {
				
				signature = Image.getInstance(
						"https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/vc_signature.jpg");
				deemedToBeParaContent = "Deemed-to-be University and the signature of the said Vice Chancellor.";
				directorPara=new Paragraph("Vice Chancellor", font10);
				
			}
			signature.scaleAbsolute(80, 39);   
			String date = mapOfSapIDAndResultDate.get(student.getSapid());
			String declareDate = "";
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
			Format formatterMonth = new SimpleDateFormat("MMMM");
			Format formatterYear = new SimpleDateFormat("yyyy");
			Format formatterDay = new SimpleDateFormat("dd");
			if (date != null) {
				declareDate = formatterMonth.format(s.parse(date)) + " " + formatterDay.format(s.parse(date)) + ", "
						+ formatterYear.format(s.parse(date));
			} else {
				declareDate = "";
			}

			if(PDDM_PROGRAMS_LIST.contains(program)) {
				Paragraph svkmManagement = new Paragraph(
						"This certificate is being issued to");
				svkmManagement.setAlignment(Element.ALIGN_CENTER);
				document.add(svkmManagement);
			}else {
				Paragraph svkmManagement = new Paragraph(
						"We, the Chancellor, Vice Chancellor and Members of Board of Management of \nSVKM\'s Narsee Monjee Institute of Management Studies,");
				svkmManagement.setAlignment(Element.ALIGN_CENTER);
				document.add(svkmManagement);
				
				Paragraph certifyThatPara = new Paragraph("certify that");
				certifyThatPara.setAlignment(Element.ALIGN_CENTER);
				document.add(certifyThatPara);
			}

			document.add(blankLine);

			if ("Online".equals(student.getExamMode())) {
				// Photo not required for Offline exam.
				Image studentPhoto = null;
				PdfPTable studentPhotoTable = new PdfPTable(1);
				try {
					studentPhoto = Image.getInstance(new URL(student.getImageUrl()));
					try {
						certificate.info("Certificate Generation"+ student.getSapid());
						 studentPhoto=    ImageRotationUtil.getImageRotatedByOrientation(studentPhoto,student.getImageUrl());
					}catch(Exception e) {
						certificate.info("Error FOund for"+ student.getSapid()+" "+e);
					}

				} catch (Exception e) {
					// If not .jpg, try .jpeg
					try {
						studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + student.getSapid() + ".jpg");

					} catch (Exception e2) {
						// IF this is also not found, then take photo from Admission system
						try {
							studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + student.getSapid() + ".jpeg");
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							throw new Exception("Error in getting image. Error : " + e.getMessage());
						}
						// mailer.sendPhotoNotFoundEmail(student);
					}
				}
				final float heightOfImageInCm = 25;
				final float pointsValueHeight = com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);

				final float widthOfImageInCm = 20;
				final float pointsValueWidth = com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);

				studentPhoto.scaleAbsolute(pointsValueWidth, pointsValueHeight);
				PdfPCell studentPhotoCell = null;
				if (studentPhoto == null) {
					// In case URL was malformed or was not found
					studentPhotoCell = new PdfPCell();
				} else {
					studentPhotoCell = new PdfPCell(studentPhoto);
				}

				studentPhotoCell.setRowspan(6);
				studentPhotoCell.setBorder(0);
				studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				studentPhotoTable.addCell(studentPhotoCell);
				studentPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
				document.add(studentPhotoTable);
			}

			// Added to make first letter capital and rest of them small//

			String fullNameOfStudent = student.getFirstName().trim().toUpperCase() + " "
					+ student.getLastName().trim().toUpperCase().replace(".", " ");
			String fullNameOfFather = student.getFatherName().trim().toUpperCase();
			String fullNameOfMother = student.getMotherName().trim().toUpperCase();
			String fullNameOfHusband = "";
			try {
				fullNameOfHusband = student.getHusbandName().trim().toUpperCase();
			} catch (Exception e) { 
				
			}

			Paragraph studentNamePara = new Paragraph(fullNameOfStudent, font18Bold);
			studentNamePara.setAlignment(Element.ALIGN_CENTER);
			document.add(studentNamePara);
			document.add(blankLine);

			Paragraph sonDaughterOfPara = null;
			if ("Spouse".equals(student.getAdditionalInfo1()) && "Female".equals(student.getGender())) {

				sonDaughterOfPara = new Paragraph(
						"(Wife of Shri. " + fullNameOfHusband + " and Daughter of Smt. " + fullNameOfMother + " )");
			} else  {
				sonDaughterOfPara = new Paragraph(
						"(Son/Daughter of Shri. " + fullNameOfFather + " and Smt. " + fullNameOfMother + " )");
			}

			sonDaughterOfPara.setAlignment(Element.ALIGN_CENTER);
			document.add(sonDaughterOfPara);
			//document.add(blankLine);
			/*
			 * String program = student.getProgram(); ProgramBean programDetails =
			 * programAllDetailsMap.get(program);
			 */
			
			document.add(blankLine);
			
			String completPara = PDDM_PROGRAMS_LIST.contains(program) ? "for completing the ":"has been examined and found qualified for ";
			
			Paragraph programDurationPara = new Paragraph();
			
			if(MODULAR_PDDM_MASTERKEYS.contains(programDetails.getConsumerProgramStructureId()))
			{
				programDurationPara = new Paragraph("has been examined and found qualified for the "
						+ programDetails.getProgramDuration() + " " + programDetails.getProgramDurationUnit());
			}
			else
			{
				programDurationPara = new Paragraph("has been examined and found qualified for");
			}
			programDurationPara.setAlignment(Element.ALIGN_CENTER);
			document.add(programDurationPara);
			document.add(blankLine);

				/* Commented by Siddheshwar_Khanse as part of 'Update PDDM Final Certificates(10732)' requirement
				 String programnameLine1 = PDDMProgramsList.contains(program)
						? "Certificate of Completion"
						: "Certificate of Completion";*/

			if(programname.contains("&")) {
                String[] programsArray = programname.split("&");
                Paragraph programNamePara1 = new Paragraph(programsArray[0]+"\n&\nCertificate in"+programsArray[1], font16Bold);
                programNamePara1.setAlignment(Element.ALIGN_CENTER);
                document.add(programNamePara1);
            }else {
                Paragraph programNamePara1 = new Paragraph(programname, font16Bold);
                programNamePara1.setAlignment(Element.ALIGN_CENTER);
                document.add(programNamePara1);
            }
		
			document.add(blankLine);
			
				/* Commented by Siddheshwar_Khanse as part of 'Update PDDM Final Certificates(10732)' requirement
				 Paragraph proficiencyPara1 = new Paragraph("with proficiency in " + programname );
				
				proficiencyPara1.setAlignment(Element.ALIGN_CENTER);
				document.add(proficiencyPara1);
				
				document.add(blankLine);*/

			//document.add(blankLine);
//					Paragraph awardedPara1 = new Paragraph();
//					Paragraph awardedPara2 = new Paragraph();
//					String saidCnk = "PDDM".equals(program) ? "professional diploma":"certificate";
//					Chunk chunk1 = new Chunk("The said "+saidCnk+" in ");
//						Chunk chunk2 = new Chunk("Online Learning mode ",font12Bold);
//						//Chunk chunk3 = new Chunk("under");
//						//Paragraph awardedPara1 = new Paragraph();
//						//awardedPara2 = new Paragraph(" NMIMS Global Access School for Continuing Education",font12Bold);
//						awardedPara1.add(chunk1);
//						awardedPara1.add(chunk2);
//						//awardedPara1.add(chunk3);
//						awardedPara1.setAlignment(Element.ALIGN_CENTER);
//						//awardedPara2.setAlignment(Element.ALIGN_CENTER);
//					
//					Paragraph awardedPara3 = new Paragraph(
//						"has been awarded to him/her in the month of " + formatterMonth.format(s.parse(date))
//								+ " of the year " + formatterYear.format(s.parse(date)) + ".");
//
//					awardedPara1.setAlignment(Element.ALIGN_CENTER);
//					awardedPara2.setAlignment(Element.ALIGN_CENTER);
//					awardedPara3.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph awardedPara1 = new Paragraph();
			String saidCnk = "PDDM".equals(program) ? "professional diploma":"certificate";
			Chunk chunk1 = new Chunk("The said "+saidCnk+" has been awarded to him/her");
				//Chunk chunk3 = new Chunk("under");
				//Paragraph awardedPara1 = new Paragraph();
				//awardedPara2 = new Paragraph(" NMIMS Global Access School for Continuing Education",font12Bold);
				awardedPara1.add(chunk1);
				//awardedPara1.add(chunk3);
				awardedPara1.setAlignment(Element.ALIGN_CENTER);
				//awardedPara2.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph awardedPara3 = new Paragraph(
				"in the month of " + formatterMonth.format(s.parse(date))
						+ " of the year " + formatterYear.format(s.parse(date)) + ".");

			awardedPara1.setAlignment(Element.ALIGN_CENTER);
			awardedPara3.setAlignment(Element.ALIGN_CENTER);
				
				document.add(awardedPara1);
//				document.add(awardedPara2);
				document.add(awardedPara3);
			
			document.add(blankLine);

			String testimonyParaContent = "In testimony whereof is set the seal of the said";
			// String deemedToBeParaContent = "Deemed-to-be University and the signature of
			// the said Director.";
			
			

			Paragraph testimonyPara = new Paragraph(testimonyParaContent);
			Paragraph deemedToBePara = new Paragraph(deemedToBeParaContent);
			testimonyPara.setAlignment(Element.ALIGN_CENTER);
			document.add(testimonyPara);
			deemedToBePara.setAlignment(Element.ALIGN_CENTER);
			document.add(deemedToBePara);

			document.add(blankLine);
			
			signature.setAlignment(Element.ALIGN_CENTER);
			document.add(signature);

			directorPara.setAlignment(Element.ALIGN_CENTER);
			document.add(directorPara);
			/*
			 * Paragraph collegeNamePara = new
			 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
			 * font10Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
			 * document.add(collegeNamePara);
			 */
			// }
			Paragraph datePara = new Paragraph("Date: " + declareDate, font10);
			datePara.setAlignment(Element.ALIGN_CENTER);
			document.add(datePara);

			certificateUniqueNumber = student.getSapid() + "-" + certificateDateForNumber
					+ RandomStringUtils.randomAlphanumeric(4);
			// Paragraph studentNumberPara = new Paragraph("Student No: " +
			// student.getSapid()+" "+"Certificate Number:"+certificateUniqueNumber,
			// font10);
			Paragraph studentNumberPara = new Paragraph(
					"Student No: " + student.getSapid() + "  " + "Certificate Number:" + certificateUniqueNumber,
					font8);
			studentNumberPara.setAlignment(Element.ALIGN_CENTER);
			document.add(studentNumberPara);
			Paragraph capmusPara = new Paragraph("Mumbai", font8);
			capmusPara.setAlignment(Element.ALIGN_CENTER);
			document.add(capmusPara);
			document.add(blankLine);

			generateCertficateBackSide(document,student.getSapid(),student.getEnrollmentMonth(),
					student.getEnrollmentYear(),date,false,writer,isHaveLogo,student.getProgram()); 
			
			document.newPage();
			
		}
		document.close();
		}catch (Exception e) {
			// TODO: handle exception
			certificate.error("Error in generating Final Certificate for admin: "+e);
		}
		

		return certificateUniqueNumber;

	}

	public HashMap<String, String> generateSRCertificateAndReturnCertificateNumber(StudentExamBean studentInfo,ServiceRequestBean serviceRequest,
		    HashMap<String, ProgramExamBean> programAllDetailsMap, String MARKSHEETS_PATH,
			String exitWithdrawalStatus,boolean isLogoRequired) throws Exception {
		String certificateUniqueNumber = "";
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat sdfrForCertNum = new SimpleDateFormat("ddMM");
		String certificateDate = sdfr.format(new Date());
		String certificateDateForNumber = sdfrForCertNum.format(new Date());
		String fileName = null;
		String folderPath = "";

		fileName = "Certificate_" + certificateDate + "_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
		folderPath = MARKSHEETS_PATH + "Certificates/";
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		Document document = new Document(PageSize.LETTER);
		if (isLogoRequired)
			document.setMargins(55, 55, 20, 40);
		 else {
			 document.setMargins(50, 50, 20, 35);
		}

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath + fileName));
		document.open();
		
		PdfPTable headerTable = new PdfPTable(1);
		if (isLogoRequired) {
			Image logo = null;
			logo = Image.getInstance(
					new URL("https://servicerequestfiles.s3.ap-south-1.amazonaws.com/IssuanceOfBonafide/NMIMS-NGASCE+logo.png"));
//		logo.scaleAbsolute(142, 40);
			logo.scaleAbsolute(500,140);
			logo.setAlignment(Element.ALIGN_CENTER);

			PdfPCell logoCell = new PdfPCell();
			logoCell.addElement(logo);
			logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			logoCell.setBorder(0);
			headerTable.addCell(logoCell);
			headerTable.completeRow();
			headerTable.setWidthPercentage(100);
			headerTable.setSpacingBefore(10);
			headerTable.setSpacingAfter(10);
		}
		document.add(headerTable);

		Paragraph blankLine = new Paragraph("\n");
			DateTimeFormatter Month = DateTimeFormatter.ofPattern("MMMM");
			DateTimeFormatter Year = DateTimeFormatter.ofPattern("yyyy");
			DateTimeFormatter Day = DateTimeFormatter.ofPattern("dd");
			LocalDateTime now = LocalDateTime.now();

			// Added to make first letter capital and rest of them small//
			String fullNameOfStudent = studentInfo.getFirstName().trim().toUpperCase() + " "
					+ studentInfo.getLastName().trim().toUpperCase().replace(".", " ");
			String fullNameOfFather = studentInfo.getFatherName().trim().toUpperCase();
			String fullNameOfMother = studentInfo.getMotherName().trim().toUpperCase();
			String fullNameOfHusband = "";
			try {
				fullNameOfHusband = studentInfo.getHusbandName().trim().toUpperCase();
			} catch (Exception e) {
			}
			String key = studentInfo.getProgram() + "-" + studentInfo.getPrgmStructApplicable();
			ProgramExamBean programDetails = programAllDetailsMap.get(key);
			String programname = programDetails.getProgramname();
			String programType = programDetails.getProgramType();
			String programDurationUnit = programDetails.getProgramDurationUnit();
			String programDuration = programDetails.getProgramDuration();
			String program = programDetails.getProgram();
			String programCode = programDetails.getProgramcode();

			List<String> programTypes = new ArrayList(Arrays.asList("Bachelor Programs", "Executive Programs",
					"PG Programs", "Master", "Professional Diploma"));
			Font fb15 = new Font(FontFamily.TIMES_ROMAN, 15.0f, Font.BOLD);
			Font fb12 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD);
			Font fb13 = new Font(FontFamily.TIMES_ROMAN, 13.0f, Font.BOLD);
			Font fb10 = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.BOLD);
			Font fb11 = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.BOLD);
			Font f12 = new Font(FontFamily.TIMES_ROMAN, 12.0f);
			Font f13 = new Font(FontFamily.TIMES_ROMAN, 13.0f);
			Font f10 = new Font(FontFamily.TIMES_ROMAN, 10.0f);
			Font f11 = new Font(FontFamily.TIMES_ROMAN, 11.0f);

			PdfPTable table = new PdfPTable(2); // 2 columns.
			table.setWidthPercentage(100);
			table.setSpacingBefore(10);
			table.setSpacingAfter(10);

			PdfPCell cell1 = new PdfPCell();
			Paragraph h1 = new Paragraph("Ref. No.: NGA-SCE/BON/2201/" + Year.format(now), f12);
			h1.setAlignment(Element.ALIGN_LEFT);
			cell1.addElement(h1);
			cell1.setBorder(0);

			PdfPCell cell2 = new PdfPCell();
			Paragraph h2 = new Paragraph(Month.format(now) + " " + Day.format(now) + ", " + Year.format(now), f12);
			h2.setAlignment(Element.ALIGN_RIGHT);
			cell2.addElement(h2);
			cell2.setBorder(0);
			table.addCell(cell1);
			table.addCell(cell2);
			document.add(table);
			document.add(blankLine);

			Paragraph headerPara = new Paragraph("\n");
			Chunk headerUnderline = new Chunk("TO WHOMSOEVER IT MAY CONCERN", fb13);
			headerUnderline.setUnderline(0.1f, -2f);
			headerPara.add(headerUnderline);
			headerPara.setAlignment(Element.ALIGN_CENTER);
			document.add(headerPara);
			document.add(blankLine);

			Paragraph p1 = new Paragraph(
					"This is to ceritfy that " + studentInfo.getFirstName() + " " + studentInfo.getLastName() + " Student No."
							+ " (" + studentInfo.getSapid() + ") was a bonafide student of our " + programDuration + " "
							+ programDurationUnit + " " + "in " + programname + " (" + programCode + ")"
							+ " program of NMIMS Global Access - School for Continuing Education. He was enrolled for "
							+ studentInfo.getEnrollmentMonth() + " " + studentInfo.getEnrollmentYear() + " batch.",
					f13);
			document.add(p1);

			if (studentInfo.getSpecialisation() != null) {
				Paragraph p2 = new Paragraph("This is to ceritfy that " + studentInfo.getFirstName() + " "
						+ studentInfo.getLastName() + "(Student No. " + studentInfo.getSapid()
						+ ") was a bonafide student of our " + programDuration + " " + programDurationUnit + " " + "in "
						+ programname + " with specialization in " + studentInfo.getSpecialisation() + " (" + programCode
						+ ")"
						+ " program of NMIMS Global Access � School for Continuing Education. He was enrolled for "
						+ studentInfo.getEnrollmentMonth() + " " + studentInfo.getEnrollmentYear() + " batch. \n", f13);
				document.add(p2);
			}

			boolean isWithdrawal = false;
			if (studentInfo.getProgramStatus() != null && studentInfo.getProgramStatus().equals("Program Withdrawal")
					&& exitWithdrawalStatus.equals("Closed")) {
				if (studentInfo.getProgram().equals("CBM")) {
					isWithdrawal = true;
					Paragraph exitPara = new Paragraph(
							"Student opted for Exit program after completing the " + studentInfo.getSem() + " semester"
									+ " and got " + programDetails.getProgramname() + " issued.\n",
							f13);
					document.add(exitPara);
				} else if (studentInfo.getProgram().equals("DBM")) {
					isWithdrawal = true;
					Paragraph exitPara = new Paragraph("Student opted for Exit program after completing the"
							+ programDetails.getProgramDuration() + " " + programDetails.getProgramDurationUnit() + ""
							+ " and got " + programDetails.getProgramname() + " issued.\n", f13);
					document.add(exitPara);
				}
			}
			document.add(blankLine);

			Paragraph p4 = new Paragraph(
					"This letter is issued on his " + serviceRequest.getAdditionalInfo1() + " request for further studies. \n",
					f13);
			document.add(p4);

			if (isWithdrawal) {
				document.add(blankLine);
//				document.add(blankLine);
			} else {
				document.add(blankLine);
				document.add(blankLine);
//				document.add(blankLine);
//				document.add(blankLine);
//				document.add(blankLine);

			}
			Paragraph p5 = new Paragraph("Regards, ", f13);
			document.add(p5);
//			document.add(blankLine);

			Image signature = Image.getInstance("https://servicerequestfiles.s3.ap-south-1.amazonaws.com/IssuanceOfBonafide/sig.jpg");
			signature.scaleAbsolute(75, 39);
			signature.setAlignment(Element.ALIGN_LEFT);
			document.add(signature);
			document.add(blankLine);

			Paragraph p6 = new Paragraph("Authorized Signatory \n", fb12);
			document.add(p6);

			Paragraph p7 = new Paragraph("NGA-SCE ", fb13);
			document.add(p7);
			document.add(blankLine);
			
			PdfPTable ftable = new PdfPTable(new float[] { 4, 0.5f }); // 2 columns.
			PdfPCell fcell1 = new PdfPCell();
			PdfPCell fcell2 = new PdfPCell();
			ftable.setWidthPercentage(100);
			ftable.completeRow();
			ftable.setSpacingBefore(10);
			ftable.setSpacingAfter(10);

			Paragraph fp1 = new Paragraph();
			Chunk cf1 = new Chunk("SVKM'S  \n", f10);
			fp1.add(cf1);
			
			Chunk cf2 = new Chunk("Narsee Monjee Institute of Management Studies \n", fb15);
			cf2.setUnderline(0.1f, -2f);
			fp1.add(cf2);
			
			Chunk cf3 = new Chunk("Deemed to be UNIVERSITY \n", f12);
			Chunk cf4 = new Chunk(
					"2nd, Floor, NMIMS Building, V.L. Mehta	Road, Vile Parle(West), Mumbai-400 056, India.\n", f11);
			Chunk cf5 = new Chunk("Tel: (91-22) 42355555 | Toll Free: 18001025136 \n", f11);
			Chunk cf6 = new Chunk("Email: ngasce@nmims.edu | Web:", f11);
			Chunk cf7 = new Chunk(" distance.nmims.edu", fb11);
			cf2.setUnderline(0.1f, -2f);
			
			certificateUniqueNumber = studentInfo.getSapid() + "-" + certificateDateForNumber
					+ RandomStringUtils.randomAlphanumeric(4);
			
			fp1.add(cf3);
			fp1.add(cf4);
			fp1.add(cf5);
			fp1.add(cf6);
			fp1.add(cf7);
			fp1.setAlignment(Element.ALIGN_BOTTOM);
			fcell1.addElement(fp1);
			fcell1.setBorder(0);
			ftable.addCell(fcell1);
			
			Image footerLogo2 = null;
			footerLogo2 = Image.getInstance(new URL("https://servicerequestfiles.s3.ap-south-1.amazonaws.com/IssuanceOfBonafide/Category-I+University+1_1%40300x.png"));
			footerLogo2.scaleAbsolute(70,35);
			footerLogo2.setAlignment(Element.ALIGN_CENTER);
			fcell2.addElement(footerLogo2);
			fcell2.setBorder(0);
			fcell2.setPaddingTop(25);
			ftable.addCell(fcell2);
			document.add(ftable);
			
			
			
//			PdfPTable ftable = new PdfPTable(2); // 2 columns.
//			ftable.setWidths(new float[] { 3, 1 });
//			ftable.completeRow();
//			ftable.setWidthPercentage(100);
//			ftable.setSpacingBefore(10);
//			ftable.setSpacingAfter(10);
//			
//			PdfPCell fcell1 = new PdfPCell();
//			Image footerLogo1 = null;
//			footerLogo1 = Image.getInstance(new URL("https://servicerequestfiles.s3.ap-south-1.amazonaws.com/IssuanceOfBonafide/Text+2%40300x.png"));
//			footerLogo1.scaleAbsolute(500,200);
//			footerLogo1.setAlignment(Element.ALIGN_CENTER);
//
//			fcell1.addElement(footerLogo1);
////			fcell1.setBorder(0);
////			fcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			ftable.addCell(fcell1);
//			
//			PdfPCell fcell2 = new PdfPCell();
//			Image footerLogo2 = null;
//			footerLogo2 = Image.getInstance(new URL("https://servicerequestfiles.s3.ap-south-1.amazonaws.com/IssuanceOfBonafide/Category-I+University+1_1%40300x.png"));
//			footerLogo2.scaleAbsolute(70,35);
//			footerLogo2.setAlignment(Element.ALIGN_CENTER);
//
//			fcell2.addElement(footerLogo2);
//			fcell2.setBorder(0);
////			fcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
//			ftable.addCell(fcell2);
//			document.add(ftable);
			
			PdfContentByte canvas = writer.getDirectContent();
			BaseColor brown = new BaseColor(170, 37, 7);
			canvas.setColorStroke(brown);
			canvas.moveTo(3, 6);
			canvas.lineTo(610, 6);
			canvas.setLineWidth(5);
			canvas.closePathStroke();
		document.close();

		HashMap<String, String> map = new HashMap<>();
		map.put("fileName", fileName);
		map.put("filePath", folderPath);
		map.put("certificateNumber", certificateUniqueNumber);
		return map;
	}
	
	

	public String generatePGMBACertificateAndReturnCertificateNumber(MarksheetBean student,
			HashMap<String, String> mapOfSapIDAndResultDate, HttpServletRequest request,
			ProgramExamBean programDetails, HashMap<String, CenterExamBean> centersMap,
			String CERTIFICATES_PATH, String SERVER_PATH, String STUDENT_PHOTOS_PATH,String courseCompletionBadgeUrl,boolean isQRCodeAppliable) throws Exception {

		String certificateNumberAndFilePath = "";
		String barcodeURl = "";
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat sdfrForCertNum = new SimpleDateFormat("ddMM");
		String certificateDate = sdfr.format(new Date());
		String certificateDateForNumber = sdfrForCertNum.format(new Date());
		String fileName = null;
		
		boolean isHaveLogo = false;

		// Image signature = Image.getInstance(new
		// URL("https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/director-Signature.jpg"));
		/*
		 * Image signature = Image.getInstance(new
		 * URL(SERVER_PATH+"exam/resources_2015/images/director-Signature.jpg"));
		 */
		try {
		Image signature = null;
		Paragraph directorPara = null;

		String deemedToBeParaContent = "";
		
		if(DIRECTOR_SIGNATURE_ELIGIBLE_PROGRAMS.contains(programDetails.getProgram())) {
			signature = Image.getInstance(
					"https://staticfilesportal.s3.ap-south-1.amazonaws.com/assets/images/Signature.png");
			deemedToBeParaContent = "Deemed-to-be University and the signature of the said Director.";
			directorPara=new Paragraph("Director", font10);
		}
		else {
			
			signature = Image.getInstance(
					"https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/vc_signature.jpg");
			deemedToBeParaContent = "Deemed-to-be University and the signature of the said Vice Chancellor.";
			directorPara=new Paragraph("Vice Chancellor", font10);
			
		}
		// Image signature = Image.getInstance("E:/director-Signature.jpg");

		// Image signatureOfSAS = Image.getInstance("D:/director-Signature.jpg");
		// Image logoSAS = Image.getInstance("E:/SAS-109.jpg");
//		Image logoSAS = Image.getInstance("D:/SAS-109.jpg");
		// signature.scaleAbsolute(110, 35);
		signature.scaleAbsolute(75, 39);
		String folderPath = "";
		fileName = "Certificate_" + certificateDate + "_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
		folderPath = CERTIFICATES_PATH;

		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		Document document = new Document(PageSize.A4);
		if (request.getAttribute("logoRequired") == null
				|| ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("N")) {
			document.setMargins(50, 50, 176, 35);
			// document.setMargins(50, 50, 70, 35);
		} else {
			document.setMargins(50, 50, 45, 35);
			isHaveLogo = true;
		}

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath + fileName));
		document.open();
		
		PdfPTable headerTable = new PdfPTable(1);
		if (request.getAttribute("logoRequired") != null
				&& ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
			Image logo = null;
			logo = Image.getInstance(new URL(
					"https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nmims_logo_new.jpg"));
			logo.scaleAbsolute(142, 40);
			logo.setAlignment(Element.ALIGN_CENTER);

			PdfPCell logoCell = new PdfPCell();
			logoCell.addElement(logo);
			logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			logoCell.setBorder(0);
			headerTable.addCell(logoCell);
			headerTable.completeRow();
			headerTable.setWidthPercentage(100);
			headerTable.setSpacingBefore(10);
			headerTable.setSpacingAfter(10);
		}
		
		Paragraph blankLine = new Paragraph("\n");
		
		//for (MarksheetBean student : studentForSRList) {
			//
			if (request.getAttribute("logoRequired") != null
					&& ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
				document.add(headerTable);
			}
			
			String date = mapOfSapIDAndResultDate.get(student.getSapid());
			String declareDate = "";
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
			Format formatterMonth = new SimpleDateFormat("MMMM");
			Format formatterYear = new SimpleDateFormat("yyyy");
			Format formatterDay = new SimpleDateFormat("dd");
			if (date != null) {
				
				declareDate = formatterMonth.format(s.parse(date)) + " " + formatterDay.format(s.parse(date)) + ", "
						+ formatterYear.format(s.parse(date));
			} else {
				declareDate = "";
				
			}
			
			Paragraph svkmManagement = new Paragraph(
					"We, the Chancellor, Vice Chancellor and Members of Board of Management of \nSVKM\'s Narsee Monjee Institute of Management Studies,");
			svkmManagement.setAlignment(Element.ALIGN_CENTER);
			document.add(svkmManagement);
			
			Paragraph certifyThatPara = new Paragraph("certify that");
			certifyThatPara.setAlignment(Element.ALIGN_CENTER);
			document.add(certifyThatPara);
			document.add(blankLine);
			
			if ("Online".equals(student.getExamMode())) {
				// Photo not required for Offline exam.
				Image studentPhoto = null;
				
				PdfPTable studentPhotoTable = new PdfPTable(1);
				try {
					studentPhoto = Image.getInstance(new URL(student.getImageUrl()));
					try {
						certificate.info("Certificate Generation"+ student.getSapid());
						 studentPhoto=    ImageRotationUtil.getImageRotatedByOrientation(studentPhoto,student.getImageUrl());
					}catch(Exception e) {
						certificate.info("Error FOund for"+ student.getSapid()+" "+e);
					}
					
				} catch (Exception e) {
					// If not .jpg, try .jpeg
					try {
						studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + student.getSapid() + ".jpg");

					} catch (Exception e2) {
						// IF this is also not found, then take photo from Admission system
						try {
							studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + student.getSapid() + ".jpeg");
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							throw new Exception("Error in getting image. Error : " + e.getMessage());
						}
						// mailer.sendPhotoNotFoundEmail(student);
					}
				}
				final float heightOfImageInCm = 25;
				final float pointsValueHeight = com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);
				
				final float widthOfImageInCm = 20;
				final float pointsValueWidth = com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);
				
				studentPhoto.scaleAbsolute(pointsValueWidth, pointsValueHeight);
				PdfPCell studentPhotoCell = null;
				if (studentPhoto == null) {
					// In case URL was malformed or was not found
					studentPhotoCell = new PdfPCell();
				} else {
					studentPhotoCell = new PdfPCell(studentPhoto);
				}

				studentPhotoCell.setRowspan(6);
				studentPhotoCell.setBorder(0);
				studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				studentPhotoTable.addCell(studentPhotoCell);
				studentPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
				document.add(studentPhotoTable);
				
			}
	
			// Added to make first letter capital and rest of them small//

			String fullNameOfStudent = student.getFirstName().trim().toUpperCase() + " "
					+ student.getLastName().trim().toUpperCase().replace(".", " ");
			String fullNameOfFather = student.getFatherName().trim().toUpperCase();
			String fullNameOfMother = student.getMotherName().trim().toUpperCase();
			String fullNameOfHusband = "";
			
			try {
				fullNameOfHusband = student.getHusbandName().trim().toUpperCase();
			} catch (Exception e) {

			}
		
			Paragraph studentNamePara = new Paragraph(fullNameOfStudent, font18Bold);
			studentNamePara.setAlignment(Element.ALIGN_CENTER);
			document.add(studentNamePara);
			document.add(blankLine);
			
			Paragraph sonDaughterOfPara = null;
			if ("Spouse".equals(student.getAdditionalInfo1()) && "Female".equals(student.getGender())) {

				sonDaughterOfPara = new Paragraph(
						"(Wife of Shri. " + fullNameOfHusband + " and Daughter of Smt. " + fullNameOfMother + " )");
			} else {
				sonDaughterOfPara = new Paragraph(
						"(Son/Daughter of Shri. " + fullNameOfFather + " and Smt. " + fullNameOfMother + " )");
			}
			
			sonDaughterOfPara.setAlignment(Element.ALIGN_CENTER);
			document.add(sonDaughterOfPara);
			//document.add(blankLine);
			/*
			 * String program = student.getProgram(); ProgramBean programDetails =
			 * programAllDetailsMap.get(program);
			 */
			//String key = student.getProgram() + "-" + student.getPrgmStructApplicable();
			//ProgramExamBean programDetails = programAllDetailsMap.get(key);
			String programname = programDetails.getProgramname();
			String programType = programDetails.getProgramType();
			String programDurationUnit = programDetails.getProgramDurationUnit();
			String programDuration = programDetails.getProgramDuration();
			String program = programDetails.getProgram();
//			String modeOfLearning = programDetails.getModeOfLearning();
			String specialization = programDetails.getSpecializationName(); 
//			List<String> programTypes =new ArrayList(Arrays.asList("Bachelor Programs","Executive Programs","PG Programs","Master","Professional Diploma"));
			List<String> programTypes =new ArrayList(Arrays.asList("Master","Bachelor Programs"));
			if(programTypes.contains(programType)) {
 				programType="degree";
			}
			else
			{
				programType= programType.toLowerCase();
			}
//			else if("Post Graduate Diploma".equals(programType) && program.startsWith("MBA")) {
//				programType="degree";
//			}
			
			document.add(blankLine);
			
			// Paragraph programDurationPara = new Paragraph("has been examined and found
			// qualified for the "+ programDetails.getProgramDuration() + " " +
			// programDetails.getProgramDurationUnit());
			Paragraph programDurationPara = new Paragraph("");
			if(MODULAR_PDDM_MASTERKEYS.contains(programDetails.getConsumerProgramStructureId()))
			{
				programDurationPara = new Paragraph("has been examined and found qualified for the "
						+ programDetails.getProgramDuration() + " " + programDetails.getProgramDurationUnit());
			}
			else
			{
				programDurationPara = new Paragraph("has been examined and found qualified for");
			}
			//Exam month duration should removed from FC as per new layout design
//			programDurationPara = new Paragraph("has been examined and found qualified for");
			programDurationPara.setAlignment(Element.ALIGN_CENTER);
			document.add(programDurationPara);
			document.add(blankLine);

			ArrayList<String> programsNeedAlignment = new ArrayList<String>(Arrays.asList("PGDBM - SCM", "PGDBM - HRM", "PGDBM - BFM"));

			if (programsNeedAlignment.contains(student.getProgram())) {
				Paragraph programNamePara = new Paragraph();
				Paragraph specializationPara = new Paragraph();
				if(!StringUtils.isEmpty(specialization)){
					String arr = programname;
					programNamePara = new Paragraph(arr, font16Bold);
					specializationPara= new Paragraph("("+specialization.trim()+")", font16Bold);
				}else {
					String arr[] = programname.split("[\\(\\-\\)]");
					programNamePara = new Paragraph(arr[0].trim(), font16Bold);
					if(arr.length > 1)
					{
						specializationPara = new Paragraph("(" + arr[1].trim()+")", font16Bold);
					}
				}
				programNamePara.setAlignment(Element.ALIGN_CENTER);
				document.add(programNamePara);
//				Paragraph specializationPara = new Paragraph("(" + arr[1], font16Bold);
				
				
				specializationPara.setAlignment(Element.ALIGN_CENTER);
				document.add(specializationPara);
			} else {
				String arr[] = programname.split("[\\(\\-\\)]");
				Paragraph programNamePara = new Paragraph(arr[0].trim(), font16Bold);
				programNamePara.setAlignment(Element.ALIGN_CENTER);
				document.add(programNamePara);
				
				if(arr.length > 1)
				{
					Paragraph middleProgramNamePara = new Paragraph("(" + arr[1].trim()+")", font16Bold);
					middleProgramNamePara.setAlignment(Element.ALIGN_CENTER);
					document.add(middleProgramNamePara);
				}
				
				if (!StringUtils.isEmpty(specialization)) {
				
					Paragraph specializationN = new Paragraph("("+specialization.trim()+")",font16Bold);
					
					specializationN.setAlignment(Element.ALIGN_CENTER);
					
					document.add(specializationN);
				}
			}
			
			/*
			 * Paragraph collegeNamePara = new
			 * Paragraph("(NMIMS Global Access - School for Continuing Education)",
			 * font16Bold); collegeNamePara.setAlignment(Element.ALIGN_CENTER);
			 * document.add(collegeNamePara);
			 */
			document.add(blankLine);
			// document.add(blankLine);

			//document.add(blankLine);
			Paragraph awardedPara1 = new Paragraph();
			Paragraph awardedPara2 = new Paragraph();
//			if("BBA".equals(program) || "B.Com".equals(program) || "BBA-BA".equals(program)) {
//				Phrase p1 = new Phrase();
//				Chunk c1 = new Chunk("The said "+programType+" in");
//				Chunk c2 = new Chunk(" Online Learning mode ",font10Bold);
//				Chunk c3 = new Chunk("under" );
//				p1.add(c1);
//				p1.add(c2);
//				p1.add(c3);
//				awardedPara1.add(p1);
//				
//			}else {
//				awardedPara1 = new Paragraph("The said " + programType + " under ");
//			}
			awardedPara1 = new Paragraph("The said " + programType+" has been awarded to him/her ");
//			awardedPara2 = new Paragraph(" NMIMS Global Access School for Continuing Education",font12Bold);
					
//			Paragraph awardedPara3 = new Paragraph(
//					"has been awarded to him/her in the month of " + formatterMonth.format(s.parse(date))
//					+ " of the year " + formatterYear.format(s.parse(date)) + ".");
			Paragraph awardedPara3 = new Paragraph(
					"in the month of " + formatterMonth.format(s.parse(date))
					+ " of the year " + formatterYear.format(s.parse(date)) + ".");

				awardedPara1.setAlignment(Element.ALIGN_CENTER);
				awardedPara2.setAlignment(Element.ALIGN_CENTER);
				awardedPara3.setAlignment(Element.ALIGN_CENTER);
				
			document.add(awardedPara1);
			document.add(awardedPara2);
			document.add(awardedPara3);
			
			document.add(blankLine);

			String testimonyParaContent = "In testimony whereof is set the seal of the said";
			// String deemedToBeParaContent = "Deemed-to-be University and the signature of
			// the said Director.";
			Paragraph testimonyPara = new Paragraph(testimonyParaContent);
			Paragraph deemedToBePara = new Paragraph(deemedToBeParaContent);
			testimonyPara.setAlignment(Element.ALIGN_CENTER);
			document.add(testimonyPara);
			deemedToBePara.setAlignment(Element.ALIGN_CENTER);
			document.add(deemedToBePara);

			document.add(blankLine);
			
			signature.setAlignment(Element.ALIGN_CENTER);
			document.add(signature);

//			Paragraph directorPara = new Paragraph("Vice Chancellor", font10);
//			directorPara.setAlignment(Element.ALIGN_CENTER);
//			document.add(directorPara);
//			
//			Paragraph datePara = new Paragraph("Date: " + declareDate, font10);
//			datePara.setAlignment(Element.ALIGN_CENTER);
//			document.add(datePara);
//			
//			String certificateUniqueNumber = student.getSapid() + "-" + certificateDateForNumber
//					+ RandomStringUtils.randomAlphanumeric(4);
//		
//			Paragraph studentNumberPara = new Paragraph(
//					"Student No: " + student.getSapid() + "  " + "Certificate Number:" + certificateUniqueNumber,
//					font8);
//			studentNumberPara.setAlignment(Element.ALIGN_CENTER);
//			document.add(studentNumberPara);
//			Paragraph capmusPara = new Paragraph("Mumbai", font8);
//			capmusPara.setAlignment(Element.ALIGN_CENTER);
//			document.add(capmusPara);
//			//document.add(blankLine);
//
//			// Added QR Code for only applicable programs for program completion badge 
//			if(isQRCodeAppliable) {
//				BarcodeQRCode qrcode = new BarcodeQRCode(courseCompletionBadgeUrl, 50, 50, null);
//				Image qr = qrcode.getImage();
//				qr.setBorder(1);
//				//qr.scaleToFit(50, 80);
//				//qr.scaleAbsolute(50, 50);
//				qr.setAlignment(Element.ALIGN_LEFT);
//				document.add(qr);
//				Paragraph barcodeverify = new Paragraph("Scan here to verify",font7);
//				barcodeverify.setAlignment(Element.ALIGN_LEFT);
//				document.add(barcodeverify);
//			}
			
//			Code by tushar- to set QR code beside bottom content updated- 29/10/22
			
			float[] columnwidths= {20f,60f};
			PdfPTable headerTable1 = new PdfPTable(columnwidths);
			PdfPCell qrCodeCell = new PdfPCell();
			qrCodeCell.setBorder(0);
			qrCodeCell.setBorder(Rectangle.NO_BORDER);
			
			directorPara.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph datePara = new Paragraph("Date: " + declareDate, font10);
			datePara.setAlignment(Element.ALIGN_CENTER);
			
			
			
			String certificateUniqueNumber = student.getSapid() + "-" + certificateDateForNumber
					+ RandomStringUtils.randomAlphanumeric(4);

			Paragraph studentNumberPara = new Paragraph(
					"Student No: " + student.getSapid() + "  " + "Certificate Number:" + certificateUniqueNumber,
					font8);
			studentNumberPara.setAlignment(Element.ALIGN_CENTER);
			Paragraph capmusPara = new Paragraph("Mumbai", font8);
			capmusPara.setAlignment(Element.ALIGN_CENTER);
			
			// Added QR Code for only applicable programs for program completion badge 
			if(isQRCodeAppliable) {
				BarcodeQRCode qrcode = new BarcodeQRCode(courseCompletionBadgeUrl, 50, 50, null);
				Image qr = qrcode.getImage();
				qr.scaleAbsolute(70, 70);
				qrCodeCell.addElement(qr);
//				qrCodeCell.setBorder(0);
				//qr.scaleToFit(50, 80);
				//qr.scaleAbsolute(50, 50);
				qr.setAlignment(Element.ALIGN_LEFT);

				Paragraph barcodeverify = new Paragraph("  Scan here to verify",font7);
				qrCodeCell.addElement(barcodeverify);
				barcodeverify.setAlignment(Element.ALIGN_LEFT);
				headerTable1.addCell(qrCodeCell);
				PdfPCell bottomParaCell = new PdfPCell();
				bottomParaCell.addElement(directorPara);
				bottomParaCell.addElement(datePara);
				studentNumberPara.setAlignment(Element.ALIGN_CENTER);
				bottomParaCell.addElement(studentNumberPara);
				bottomParaCell.addElement(capmusPara);
				bottomParaCell.setBorder(Rectangle.NO_BORDER);
				headerTable1.addCell(bottomParaCell);
				headerTable1.setHorizontalAlignment(Element.ALIGN_LEFT);
				document.add(headerTable1);
			}
			else {
			
			document.add(directorPara);
			document.add(datePara);
			document.add(studentNumberPara);
			document.add(capmusPara);
			}		
			
//			tushar Code ends here
			
			
			generateCertficateBackSide(document,student.getSapid(),
					student.getEnrollmentMonth(),student.getEnrollmentYear(),
					date,false,writer,isHaveLogo,student.getProgram());
			
			String filePath=folderPath+fileName;
			certificateNumberAndFilePath = certificateUniqueNumber + filePath;
			document.close();
			return certificateNumberAndFilePath;
		
		}
		catch(Exception e){
//			e.printStackTrace();
			certificate.error("Error in generating Final Certificate for admin: "+e);
		throw new Exception(e);
		}

	}
	
	public void generateCertficateBackSide(Document document, String sapid, 
			String enrollMonth, String enrollYear, String completionDate, 
			boolean haveThumbnail, PdfWriter writer, boolean isHaveLogo, String program)throws Exception
	{
		String mod = null;
		Map<String, ProgramExamBean> modProgramMap = modService.getModProgramMap();
		
		if(modProgramMap.containsKey(program))
			mod = modProgramMap.get(program).getModeOfLearning();
		else
			throw new Exception("Error in generating Mode of Delivery in FC for sapid("+sapid+")");
			
		String enrollformattedYear = getFormattedTwoDigitYear(enrollYear);
		
		String completionformattedYear = getFormattedTwoDigitYear(completionDate);
		String completionformattedMonth = getFormattedMonthOnly(completionDate);
		
		//Creating certificate back side content list
		Map<String, String> pdfBackSideContentMap = new LinkedHashMap<>();
		//Setting contents
		pdfBackSideContentMap.put("Student Number", sapid);
		pdfBackSideContentMap.put("Mode of Delivery", mod);
		pdfBackSideContentMap.put("Date of Admission", enrollMonth+"-"+enrollformattedYear);
		pdfBackSideContentMap.put("Date of Completion", completionformattedMonth+"-"+completionformattedYear);
		pdfBackSideContentMap.put("Name & Address of Learning Support Centre", LEARNING_SUPPORT_CENTRE);
		pdfBackSideContentMap.put("Name & Address of Examination Centre", EXAMINATION_CENTRE);
		
		//PDF Writing Start
		//Change margins only for back page
		document.setMargins(50, 50, 176, 35);
				
		//created new pdf page
		document.newPage();
		
		if(haveThumbnail)
		{
			Phrase watermark = new Phrase("Certificate Downloaded from NGASCE Student Portal",
					new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
			PdfContentByte canvas = writer.getDirectContent();
			ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 580, 30);
		}
		//created table structure with - 2 column
		PdfPTable backSideContentTable = new PdfPTable(2);
		//Iterate contents hash map and setting them into column of above table
		for (Map.Entry<String, String> entry : pdfBackSideContentMap.entrySet())
		{
			PdfPCell left = new PdfPCell(new Paragraph(entry.getKey(), font1));// it contain heading
			PdfPCell right = new PdfPCell(new Paragraph(entry.getValue(), font1));// it contain value
			left.setFixedHeight(25);
			left.setVerticalAlignment(Element.ALIGN_MIDDLE);
			left.setPaddingLeft(5);
			right.setVerticalAlignment(Element.ALIGN_MIDDLE);
			right.setHorizontalAlignment(Element.ALIGN_CENTER);
			right.setPadding(5);
			backSideContentTable.addCell(left);
			backSideContentTable.addCell(right);
			backSideContentTable.completeRow();
		}
		//Setting table attributes
		backSideContentTable.setWidthPercentage(100);
		backSideContentTable.setSpacingBefore(10f);
		backSideContentTable.setSpacingAfter(10f);
		float[] marksColumnWidths = {8f, 10f};
		backSideContentTable.setWidths(marksColumnWidths);
		
		//added table in new pdf page 
		document.add(backSideContentTable);
		
		//Reset margins
		if (!isHaveLogo)
			document.setMargins(50, 50, 176, 35);
		else
			document.setMargins(50, 50, 45, 35);
	}
	
	public String getFormattedTwoDigitYear(String date)throws Exception
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		Format formatYear = new SimpleDateFormat("yy");
		String twoDigitYear = formatYear.format(format.parse(date));
		return twoDigitYear;
	}

	public String getFormattedMonthOnly(String date)throws Exception
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Format formatMonth = new SimpleDateFormat("MMM");
		String formattedMonth = formatMonth.format(format.parse(date));
		return formattedMonth;
	}
	
	
	public String generateCertificateAndReturnCertificateNumberForMSCAI(List<MarksheetBean> studentForSRList,
			HashMap<String, String> mapOfSapIDAndResultDate, HttpServletRequest request,
			HashMap<String, ProgramExamBean> programAllDetailsMap, HashMap<String, CenterExamBean> centersMap,
			String MARKSHEETS_PATH, String SERVER_PATH, String STUDENT_PHOTOS_PATH) throws Exception {
		String certificateUniqueNumber = "";
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat sdfrForCertNum = new SimpleDateFormat("ddMM");
		String certificateDate = sdfr.format(new Date());
		String certificateDateForNumber = sdfrForCertNum.format(new Date());
		String fileName = null;
		boolean isHaveLogo = false;

//		Image signature = Image.getInstance("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/vc_signature.jpg");		
		Image signature = Image.getInstance("https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/director-Signature.jpg");
		signature.scaleAbsolute(75, 39); 
		
		String folderPath = "";
		fileName = "Certificate_MSCAI_" + certificateDate + "_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
		folderPath = MARKSHEETS_PATH + "Certificates/";

		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		Document document = new Document(PageSize.A4);
		
		if (request.getAttribute("logoRequired") == null|| ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("N"))
		{
			document.setMargins(50, 50, 176, 35);
		}
		else
		{
			document.setMargins(50, 50, 45, 35);
			isHaveLogo = true;
		}

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath + fileName));
		
		request.getSession().setAttribute("fileName", folderPath + fileName);
		
		document.open();
		
		PdfPTable headerTable = new PdfPTable(1);
		if (request.getAttribute("logoRequired") != null
				&& ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
			Image logo = null;
			logo = Image.getInstance(
					new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nmims_logo_new.jpg"));
			logo.scaleAbsolute(142, 40);
			logo.setAlignment(Element.ALIGN_CENTER);

			PdfPCell logoCell = new PdfPCell();
			logoCell.addElement(logo);
			logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			logoCell.setBorder(0);
			headerTable.addCell(logoCell);
			headerTable.completeRow();
			headerTable.setWidthPercentage(100);
			headerTable.setSpacingBefore(10);
			headerTable.setSpacingAfter(10);
		}

		Paragraph blankLine = new Paragraph("\n");
		
		for (MarksheetBean student : studentForSRList) {
			if (request.getAttribute("logoRequired") != null
					&& ((String) request.getAttribute("logoRequired")).equalsIgnoreCase("Y")) {
				document.add(headerTable);
			}
			String date = mapOfSapIDAndResultDate.get(student.getSapid());
			String declareDate = "";
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
			Format formatterMonth = new SimpleDateFormat("MMMM");
			Format formatterYear = new SimpleDateFormat("yyyy");
			Format formatterDay = new SimpleDateFormat("dd");
			if (date != null) {
				declareDate = formatterMonth.format(s.parse(date)) + " " + formatterDay.format(s.parse(date)) + ", "
						+ formatterYear.format(s.parse(date));
			} else {
				declareDate = "";
			}
			
			Paragraph svkmManagement = new Paragraph(
					"We, the Chancellor, Vice Chancellor and Members of Board of Management of \nSVKM\'s Narsee Monjee Institute of Management Studies,");
			svkmManagement.setAlignment(Element.ALIGN_CENTER);
			document.add(svkmManagement);

			Paragraph certifyThatPara = new Paragraph("certify that");
			certifyThatPara.setAlignment(Element.ALIGN_CENTER);
			document.add(certifyThatPara);
			document.add(blankLine);

			if ("Online".equals(student.getExamMode())) {
				// Photo not required for Offline exam.
				Image studentPhoto = null;
				PdfPTable studentPhotoTable = new PdfPTable(1);
				try {
					studentPhoto = Image.getInstance(new URL(student.getImageUrl()));
					try {
						certificate.info("Certificate Generation"+ student.getSapid());
						 studentPhoto=    ImageRotationUtil.getImageRotatedByOrientation(studentPhoto,student.getImageUrl());
					}catch(Exception e) {
						certificate.info("Error FOund for"+ student.getSapid()+" "+e);
					}
				} catch (Exception e) {
					// If not .jpg, try .jpeg
					try {
						studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + student.getSapid() + ".jpg");

					} catch (Exception e2) {
						// IF this is also not found, then take photo from Admission system
						try {
							studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + student.getSapid() + ".jpeg");
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							throw new Exception("Error in getting image. Error : " + e.getMessage());
						}
						// mailer.sendPhotoNotFoundEmail(student);
					}
				}
				final float heightOfImageInCm = 25;
				final float pointsValueHeight = com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);

				final float widthOfImageInCm = 20;
				final float pointsValueWidth = com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);

				studentPhoto.scaleAbsolute(pointsValueWidth, pointsValueHeight);
				PdfPCell studentPhotoCell = null;
				if (studentPhoto == null) {
					// In case URL was malformed or was not found
					studentPhotoCell = new PdfPCell();
				} else {
					studentPhotoCell = new PdfPCell(studentPhoto);
				}

				studentPhotoCell.setRowspan(6);
				studentPhotoCell.setBorder(0);
				studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				studentPhotoTable.addCell(studentPhotoCell);
				studentPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
				document.add(studentPhotoTable);
			}

			// Added to make first letter capital and rest of them small//
			String fullNameOfStudent = student.getFirstName().trim().toUpperCase() + " "
					+ student.getLastName().trim().toUpperCase().replace(".", " ");
			String fullNameOfFather = student.getFatherName().trim().toUpperCase();
			String fullNameOfMother = student.getMotherName().trim().toUpperCase();
			String fullNameOfHusband = "";
			try{
				fullNameOfHusband = student.getHusbandName().trim().toUpperCase();
			}catch (Exception e) { 
				
			}

			Paragraph studentNamePara = new Paragraph(fullNameOfStudent, font18Bold);
			studentNamePara.setAlignment(Element.ALIGN_CENTER);
			document.add(studentNamePara);
			document.add(blankLine);

			Paragraph sonDaughterOfPara = null;
			if ("Spouse".equals(student.getAdditionalInfo1()) && "Female".equals(student.getGender())) {
				sonDaughterOfPara = new Paragraph(
						"(Wife of Shri. " + fullNameOfHusband + " and Daughter of Smt. " + fullNameOfMother + " )");
			} else  {
				sonDaughterOfPara = new Paragraph(
						"(Son/Daughter of Shri. " + fullNameOfFather + " and Smt. " + fullNameOfMother + " )");
			}

			sonDaughterOfPara.setAlignment(Element.ALIGN_CENTER);
			document.add(sonDaughterOfPara);
			
			String key = student.getProgram() + "-" + student.getPrgmStructApplicable();
			ProgramExamBean programDetails = programAllDetailsMap.get(key);
			String programname = programDetails.getProgramname();
//			String programType = programDetails.getProgramType();
//			String program = programDetails.getProgram();
//			List<String> programTypes = new ArrayList<String>(Arrays.asList("Bachelor Programs","Executive Programs","PG Programs","Master","Professional Diploma"));
			
			document.add(blankLine);
			
			Paragraph programDurationPara = new Paragraph("has been examined and found qualified for");		
			programDurationPara.setAlignment(Element.ALIGN_CENTER);
			document.add(programDurationPara);
			
			document.add(blankLine);

			String arr[] = programname.split(" in ");
			Paragraph programNamePara = new Paragraph(arr[0], font16Bold);
			programNamePara.setAlignment(Element.ALIGN_CENTER);
			document.add(programNamePara);
			Paragraph specializationPara = new Paragraph("("+arr[1]+")", font16Bold);
			specializationPara.setAlignment(Element.ALIGN_CENTER);
			document.add(specializationPara);
			
			document.add(blankLine);

			Paragraph awardedPara1 = new Paragraph();
	
			awardedPara1 = new Paragraph("The said degree has been awarded to him/her"
				+" in the month of " + formatterMonth.format(s.parse(date))
						+ " of the year " + formatterYear.format(s.parse(date)) + ".");

			awardedPara1.setAlignment(Element.ALIGN_CENTER);
		
			document.add(awardedPara1);
		
			document.add(blankLine);

			String testimonyParaContent = "In testimony whereof is set the seal of the said";
			String deemedToBeParaContent = "";
			deemedToBeParaContent = "Deemed-to-be University and the signature of the said Director.";
			Paragraph testimonyPara = new Paragraph(testimonyParaContent);
			Paragraph deemedToBePara = new Paragraph(deemedToBeParaContent);
			testimonyPara.setAlignment(Element.ALIGN_CENTER);
			document.add(testimonyPara);
			deemedToBePara.setAlignment(Element.ALIGN_CENTER);
			document.add(deemedToBePara);

			document.add(blankLine);
			
			signature.setAlignment(Element.ALIGN_CENTER);
			document.add(signature);

			Paragraph directorPara = new Paragraph("Director", font10);
			directorPara.setAlignment(Element.ALIGN_CENTER);
			document.add(directorPara);
			
			Paragraph datePara = new Paragraph("Date: " + declareDate, font10);
			datePara.setAlignment(Element.ALIGN_CENTER);
			document.add(datePara);

			certificateUniqueNumber = student.getSapid() + "-" + certificateDateForNumber
					+ RandomStringUtils.randomAlphanumeric(4);
			
			Paragraph studentNumberPara = new Paragraph(
					"Student No: " + student.getSapid() + "  " + "Certificate Number:" + certificateUniqueNumber,
					font8);
			studentNumberPara.setAlignment(Element.ALIGN_CENTER);
			document.add(studentNumberPara);
			Paragraph capmusPara = new Paragraph("Mumbai", font8);
			capmusPara.setAlignment(Element.ALIGN_CENTER);
			document.add(capmusPara);
			document.add(blankLine);
			
			generateCertficateBackSide(document,student.getSapid(),student.getEnrollmentMonth(),
					student.getEnrollmentYear(),date,false,writer,isHaveLogo,student.getProgram());
			
			document.newPage();
			
		}
		
		document.close();

		return certificateUniqueNumber;

	}
	
	public String generateCertificateAndReturnCertificateNumberForSingleStudentSelfMSCAI(MarksheetBean marksheet,
			String resultDeclareDate, HashMap<String, ProgramExamBean> programAllDetailsMap,
			HashMap<String, CenterExamBean> centersMap, String CERTIFICATES_PATH, String SERVER_PATH,
			String STUDENT_PHOTOS_PATH) throws Exception {

//		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
//		SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
		String response = null;

//		String certificateUniqueNumber = "";
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
//		SimpleDateFormat sdfrForCertNum = new SimpleDateFormat("ddMM");
		String certificateDate = sdfr.format(new Date());
//		String certificateDateForNumber = sdfrForCertNum.format(new Date());
		String fileName = null;
		boolean isHaveLogo = false;

		String folderPath = "";
		fileName = "Certificate_Self_MSCAI_" + certificateDate + "_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
		folderPath = CERTIFICATES_PATH;

		Document document = new Document(PageSize.A4);
		document.setMargins(50, 50, 170, 35);

		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		//SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath + fileName));
		response = folderPath + fileName;
		response = SERVER_PATH + "Certificates/" + fileName;

		document.open();

		Phrase watermark = new Phrase("Certificate Downloaded from NGASCE Student Portal",
				new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.LIGHT_GRAY));
		PdfContentByte canvas = writer.getDirectContent();
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 290, 420, 30);
		
		//To add static disclaimer at bottom of pdf
		addDisclaimer(document,writer);
		
		Paragraph blankLine = new Paragraph("\n");

		PdfPTable headerTable = new PdfPTable(1);

		Image logo = null;
		logo = Image.getInstance(
				new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/nmims_logo_new.jpg"));
		logo.scaleAbsolute(142, 40);
		logo.setAlignment(Element.ALIGN_CENTER);

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

		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		Format formatterMonth = new SimpleDateFormat("MMMM");
		Format formatterYear = new SimpleDateFormat("yyyy");
//		Format formatterDay = new SimpleDateFormat("dd");
//		String declareDate = formatterMonth.format(s.parse(resultDeclareDate)) + " "
//				+ formatterDay.format(s.parse(resultDeclareDate)) + ",  "
//				+ formatterYear.format(s.parse(resultDeclareDate));

		Paragraph certifyThatPara = new Paragraph("Certified that", font14);
		certifyThatPara.setAlignment(Element.ALIGN_CENTER);
		document.add(certifyThatPara);
		document.add(blankLine);
		if ("Online".equals(marksheet.getExamMode())) {
			// Photo not required for Offline exam.
			Image studentPhoto = null;
			PdfPTable studentPhotoTable = new PdfPTable(1);
			try { 
				
				studentPhoto = Image.getInstance(new URL(marksheet.getImageUrl()));
				try {
					certificate.info("Certificate Generation"+ marksheet.getSapid());
		        studentPhoto=    ImageRotationUtil.getImageRotatedByOrientation(studentPhoto,marksheet.getImageUrl());
				}catch(Exception mee) {
					certificate.info("Error FOund for"+ marksheet.getSapid()+" "+mee);
				}
			} catch (Exception e) {
			
				// If not .jpg, try .jpeg
				try {
					studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + marksheet.getSapid() + ".jpg");
				} catch (Exception e2) {
					// IF this is also not found, then take photo from Admission system
					try {
						studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH + marksheet.getSapid() + ".jpeg");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						throw new Exception("Error in getting image. Error : " + e.getMessage());
					
				}
					// mailer.sendPhotoNotFoundEmail(student);
				}
			}
			final float heightOfImageInCm = 25;
			final float pointsValueHeight = com.itextpdf.text.Utilities.millimetersToPoints(heightOfImageInCm);

			final float widthOfImageInCm = 20;
			final float pointsValueWidth = com.itextpdf.text.Utilities.millimetersToPoints(widthOfImageInCm);

			studentPhoto.scaleAbsolute(pointsValueWidth, pointsValueHeight);
			PdfPCell studentPhotoCell = null;
			if (studentPhoto == null) {
				// In case URL was malformed or was not found
				studentPhotoCell = new PdfPCell();
			} else {
				studentPhotoCell = new PdfPCell(studentPhoto);
			}

			studentPhotoCell.setRowspan(6);
			studentPhotoCell.setBorder(0);
			studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			studentPhotoTable.addCell(studentPhotoCell);
			studentPhotoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
			document.add(studentPhotoTable);
		}

		String fullNameOfStudent = marksheet.getFirstName().trim().toUpperCase() + " "
				+ marksheet.getLastName().trim().toUpperCase().replace(".", " ");
		String motherName = marksheet.getMotherName().trim().toUpperCase();
		String fathersName = marksheet.getFatherName().trim().toUpperCase();
		String husbandName = "";
		try {
			husbandName = marksheet.getHusbandName().trim().toUpperCase();
		} catch (Exception e) {
			
		}

		// End//
		Paragraph studentNamePara = new Paragraph(fullNameOfStudent, font18Bold);
		studentNamePara.setAlignment(Element.ALIGN_CENTER);
		document.add(studentNamePara);
		document.add(blankLine);

		Paragraph sonDaughterOfPara = null;
		if ("Spouse".equals(marksheet.getAdditionalInfo1()) && "Female".equals(marksheet.getGender())) {
			sonDaughterOfPara = new Paragraph(
					"(Wife of Shri. " + husbandName + " and Daughter of " + motherName + " )");
		} else if ("Parent".equals(marksheet.getAdditionalInfo1()) || marksheet.getAdditionalInfo1() == null) {
			sonDaughterOfPara = new Paragraph(
					"(Son/Daughter of Shri. " + fathersName + " and Smt. " + motherName + " )");
		}

		sonDaughterOfPara.setAlignment(Element.ALIGN_CENTER);
		document.add(sonDaughterOfPara);
		document.add(blankLine);

		String key = marksheet.getProgram() + "-" + marksheet.getPrgmStructApplicable();
		ProgramExamBean programDetails = programAllDetailsMap.get(key);

		String programname = programDetails.getProgramname();
		//String programType = programDetails.getProgramType();
		Paragraph programDurationPara = new Paragraph("");

		programDurationPara = new Paragraph("has been examined and found qualified for");

		programDurationPara.setAlignment(Element.ALIGN_CENTER);

		document.add(programDurationPara);
		document.add(blankLine);
		document.add(blankLine);

		String arr[] = programname.split(" in ");
		Paragraph programNamePara = new Paragraph(arr[0], font16Bold);
		programNamePara.setAlignment(Element.ALIGN_CENTER);
		document.add(programNamePara);
		Paragraph specializationPara = new Paragraph("("+arr[1]+")", font16Bold);
		specializationPara.setAlignment(Element.ALIGN_CENTER);
		document.add(specializationPara);
		
		document.add(blankLine);

		String awardedParaContent = "The said degree has been awarded to him/her";
		String monthAndYearContent = " in the month of " + formatterMonth.format(s.parse(resultDeclareDate))
				+ " of the year " + formatterYear.format(s.parse(resultDeclareDate)) + ".";
		Paragraph awardedPara = new Paragraph(awardedParaContent);
		Paragraph monthAndYearPara = new Paragraph(monthAndYearContent);
		awardedPara.setAlignment(Element.ALIGN_CENTER);
		document.add(awardedPara);
		monthAndYearPara.setAlignment(Element.ALIGN_CENTER);
		document.add(monthAndYearPara);
	
		document.add(blankLine);

//		generateCertficateBackSide(document,marksheet.getSapid(),marksheet.getEnrollmentMonth(),
//				marksheet.getEnrollmentYear(),resultDeclareDate,true,writer,isHaveLogo);
		
		document.newPage();

		document.close();

		try {

			/*To Transfer the local files to s3 server
			 * @Param:- filePath - Whole Path File on local eg. E:\Content\Management Theory and Practice\TRY.pdf
			 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
			 * @param:- bucketName- Name of the bucket present in the s3
			 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
			 */

			String filePath = CERTIFICATES_PATH + fileName;
			String keyName = "Certificates/"+fileName;
			folderPath = "Certificates/";

			HashMap<String,String> awsUploadResponse = amazonHelper.uploadLocalFile(filePath, keyName, AWS_CERTIFICATES_BUCKET, folderPath);
	
			if( !awsUploadResponse.get("status").equals("error") ) {

				File f = new File(filePath);
				f.delete();
				response = awsUploadResponse.get("url");
				certificate.info("Final Certificate are generated successful for MSCAI to share for sapid: "+marksheet.getSapid());
			}
			
		}catch (Exception e) {
			// TODO: handle exception 
			certificate.error("Error in uploading Final Certificate for MSCAI student to share: "+e);
			throw new Exception("Error in uploading certficate for MSCAI on aws: "+e);
		}
		
		return response;
	}
	
	//To add disclaimer at absolute position on pdf
	public void addDisclaimer(Document document, PdfWriter writer) throws Exception
	{
		final String disclaimerLine1 = "The above result is provisional in nature. The Gradesheet/Marksheet/Transcript/Final Certificate issued";
		final String disclaimerLine2 = "by the University will be the authentic document.";
		
		Paragraph watermarkLine1 = new Paragraph(disclaimerLine1,new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK));		
		Paragraph watermarkLine2 = new Paragraph(disclaimerLine2,new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK));
		
		PdfContentByte canvas = writer.getDirectContent();
		ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, watermarkLine1, 60, 50, 0);
		ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, watermarkLine2, 60, 38, 0);
	}
}