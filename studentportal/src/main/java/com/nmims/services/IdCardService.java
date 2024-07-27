package com.nmims.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.beans.IdCardStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.daos.StudentDAO;
import com.nmims.helpers.AWSHelper;
import com.nmims.helpers.HashKeyHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.interfaces.IdCardServiceInterface;
import com.sforce.soap.partner.sobject.SObject;

@Service
public class IdCardService implements IdCardServiceInterface{
	
	Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	Font font3 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
	Font font4 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
	Font font9 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
	Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, 10);
	Font font10_Bold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
	Font fontAddress = new Font(Font.FontFamily.TIMES_ROMAN, 11);
	Font fontAddress8 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
	Font descBoldFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD);
	Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 7);
	
	BaseColor portalRed = WebColors.getRGBColor("#cf2727");
	BaseColor bgColour = WebColors.getRGBColor("#FAF9F6");
	BaseColor bgColourprogram = WebColors.getRGBColor("#ce3700");
	
	@Value("${ID_CARD_PATH}") 
	private String ID_CARD_PATH;
	
	@Value("${SERVER_PATH}") 
	private String SERVER_PATH;
	
	@Value("${AWS_DIGITAL_ID_CARD_BUCKET}") 
	private String AWS_DIGITAL_ID_CARD_BUCKET;
	
	@Value("${TIMEBOUND_PORTAL_LIST}") 
	private String TIMEBOUND_PORTAL_LIST;
	
	@Value("${ID_CARD_LINK}") 
	private String ID_CARD_LINK;
	
	private Logger idCardCreationLogger=Logger.getLogger("id_card_creation");
	
	@Autowired
	AWSHelper aWSHelper;
	
	@Autowired
	StudentDAO studentDAO;
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	MailSender mailer;
	
	@Autowired
	SalesforceHelper sfdcHelper;
	
	private HashMap<String, String> mapOfLcName;
	
	private HashMap<String, String> getMapOfLcName() {
		if (this.mapOfLcName == null) {
			this.mapOfLcName = studentDAO.getMapOfLCName();
		}
		return mapOfLcName;
	}

	@Override
	public HashMap<String, String> createIdCardPdf(StudentStudentPortalBean student) {
		HashMap<String, String> response=new HashMap<String, String>();
		
		String fileName = "";
		String filePath="";
		String IdCardS3Url="";
		
		//To check if the pdf file is already created on AWS and avoid redundancy
		String existingFileName=studentExistingFileOnAWS(student.getSapid());
		if(!StringUtils.isBlank(existingFileName)) {
		fileName=existingFileName;	
		}else {
		fileName = student.getSapid()+"_IDCard_"+RandomStringUtils.randomAlphanumeric(6)+".pdf";
		}
		
		try {
		String uniqueHashKey=HashKeyHelper.generateHashKey(student.getSapid());
		String qrCodeURL=SERVER_PATH+"studentportal/public/getIdCardDetails/"+uniqueHashKey;
		idCardCreationLogger.info("Id card creation started for sapid >>"+student.getSapid());
		idCardCreationLogger.info("Student imageURL >>"+student.getImageUrl());
		try {
		if(!StringUtils.isBlank(ID_CARD_PATH)) {
		FileUtils.cleanDirectory(new File(ID_CARD_PATH + "/"));
		}
		}catch (Exception e) {
			// TODO: handle exception
			idCardCreationLogger.error("Error while clean directory!");
		}
		Image logo = Image.getInstance(new URL("https://staticfilesportal.s3.ap-south-1.amazonaws.com/assets/images/ID-CARD-LOGO.bmp"));
		Image studentPhoto = Image.getInstance(new URL(student.getImageUrl()));
		logo.scaleAbsolute(1000, 300);
		logo.setAlignment(Element.ALIGN_LEFT);
		studentPhoto.scaleAbsolute(80, 80);
		studentPhoto.setAlignment(Element.ALIGN_CENTER);


		Document document = new Document(PageSize.A4);
		document.setMargins(20, 20, 60, 40);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(ID_CARD_PATH+"/"+fileName));
		document.open();
		
		if(TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			String programWithSpecialization = checkProgramAndSpecialization(student.getSapid(), student.getProgram());
			student.setProgram(programWithSpecialization);
		}
		
		
		//Front card start
		getFrontIdCard(student, document, studentPhoto, logo);
		document.add(new Paragraph("\n"));
        //Front card start
        
        //Back card start
		getBackIdCard(student, document,qrCodeURL);
        //Back card start
        document.close();
        
        filePath=ID_CARD_PATH+fileName;
        idCardCreationLogger.info("Local filePath for id card : "+filePath);
        IdCardS3Url=uploadPdfToS3(filePath,fileName);
        
        //if s3 url is black
        if("".equals(IdCardS3Url)) {
        	idCardCreationLogger.error("Error in pdf generation and upload");
	        response.put("status", "error");
	        response.put("response", "ID card not created on S3");
	        return response;
        }
        
        insertIdCardDetailsToDB(student.getSapid(), fileName, uniqueHashKey, student.getCreatedBy());
        idCardCreationLogger.info("IdCardUrl after AWS upload : "+IdCardS3Url);
        response.put("status", "success");
        response.put("response", IdCardS3Url);
        
        try {
        String accountId=studentDAO.getAccountIdFromSFDC(student.getSapid());
        //Adding idCardUrl back to sales force
		SObject accountObject = new SObject();
		accountObject.setType("Account");
		accountObject.setId(accountId);
		accountObject.setField("Id_Card_Url__c", ID_CARD_LINK);
        sfdcHelper.updateRecordsToSDFC(accountObject);
        }catch (Exception e) {
        	idCardCreationLogger.error("Error while updating IdCard URL to SFDC : "+e.getMessage());
		}
        
        return response;
		}catch(SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			IdCardS3Url="Error";
			idCardCreationLogger.error("Error while inserting to DB >>>"+sw);
	        response.put("status", "error");
	        response.put("response", "Error while inserting to DB");
			return response;	
		}catch (DuplicateKeyException e) {
			IdCardS3Url="Error";
			idCardCreationLogger.error("Duplicate entry in db >>>"+e.getMessage());
	        response.put("status", "error");
	        response.put("response", "ID card already created");
			return response;	
		}catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			IdCardS3Url="Error";
			idCardCreationLogger.error("Error in pdf generation and upload >>>"+sw);
	        response.put("status", "error");
	        response.put("response", "Error while creating ID card");
			return response;
		}
	}
	

	/*To create front page of the ID card
	 * @Param:- student - StudentStudentPortalBean class object to get students info
	 * @param:- document- Document object of the pdf
	 * @param:- studentPhoto-(Image) student photo
	 * @param:- logo- (Image) student photo
	 */
	public void getFrontIdCard(StudentStudentPortalBean student, Document document, Image studentPhoto,Image logo) throws DocumentException {
		
		String studentName=student.getFirstName()+" "+student.getLastName();
		
		String lc = student.getLc();
		if(StringUtils.isBlank(lc)) {
			HashMap<String, String> mapOfLcNames=getMapOfLcName();
			lc=mapOfLcNames.get(student.getCenterCode());
		}
		
		PdfPTable headerTable = new PdfPTable(1);
		PdfPCell headerCell = new PdfPCell();
		headerCell.setBackgroundColor(portalRed);
		headerCell.setFixedHeight(25f);
		headerCell.setBorder(0);
		headerTable.addCell(headerCell);
		document.add(headerTable);
		
		PdfPTable logoTable = new PdfPTable(1);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bgColour);
        cell.addElement(logo);
        cell.setBorder(0);
        cell.addElement(Chunk.NEWLINE);
        cell.setFixedHeight(85f);
        logoTable.addCell(cell);
        document.add(logoTable);
        
        float[] columnwidths= {1,2};
        PdfPTable studentTable = new PdfPTable(columnwidths);
        PdfPCell studentPhotocell = new PdfPCell();
        studentPhotocell.setBackgroundColor(bgColour);
        studentPhotocell.addElement(studentPhoto);
        studentPhotocell.setBorder(0);
        studentPhotocell.setFixedHeight(125f);
        studentTable.addCell(studentPhotocell);
        
        PdfPCell studentDetailsCell = new PdfPCell();
        studentDetailsCell.setBackgroundColor(bgColour);
        studentDetailsCell.setPaddingLeft(0);
        studentDetailsCell.setBorder(0);
        PdfPCell nameCell = new PdfPCell();
        PdfPTable nestedTable = new PdfPTable(1);
        nameCell.setBackgroundColor(bgColour);
        nameCell.setPaddingLeft(0);
        nameCell.setBorder(0);
        nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        nameCell.addElement(new Paragraph(studentName,font3));
        nameCell.setPaddingBottom(10);
        nestedTable.addCell(nameCell);
        PdfPCell programCell = new PdfPCell();
        programCell.setBackgroundColor(bgColourprogram);
        programCell.setBorder(0);
        Font f = new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.WHITE);
        Paragraph p=new Paragraph(student.getProgram()+"      "+student.getSapid() , f);
        programCell.addElement(p);
        programCell.setFixedHeight(23);
        nestedTable.addCell(programCell);
        
        PdfPCell detailsCell = new PdfPCell();
        detailsCell.setBackgroundColor(bgColour);
        detailsCell.setBorder(0);
        //studentDetailsCell.addElement(Chunk.NEWLINE);
        
        StringBuilder batchName= new StringBuilder();
        if(!StringUtils.isBlank(student.getBatchName())) {
        	batchName.append(student.getBatchName());
        }else {
        	batchName.append(student.getEnrollmentMonth()).append(" ").append(student.getEnrollmentYear());
        }
        
        detailsCell.addElement(new Paragraph("Batch: "+batchName+"   Valid Upto : "
        +student.getValidityEndMonth()+" "+student.getValidityEndYear()
//        		+ "\nAEP "+student.getCenterName()
        		+ "\nUniversity Regional Office & NMAT/NPAT Centre : "+lc,font10));
        nestedTable.addCell(detailsCell);
        studentDetailsCell.addElement(nestedTable);
        nestedTable.setWidthPercentage(100);
        studentTable.addCell(studentDetailsCell);
        document.add(studentTable);
        
        PdfPTable footerTable = new PdfPTable(1);
        PdfPCell footerCell = new PdfPCell();
        footerCell.setBackgroundColor(portalRed);
        footerCell.setFixedHeight(10f);
        footerCell.setBorder(0);
        footerTable.addCell(footerCell);
        document.add(footerTable);
	}
	
	/**To create back page of the ID card
	 * @Param:- student - StudentStudentPortalBean class object to get students info
	 * @param:- document- Document object of the pdf
	 * @param:- idcardUrl- id card URL
	 */
	private void getBackIdCard(StudentStudentPortalBean student, Document document,String idCardURL) throws Exception {
		float[] columnwidths= {1,2};
		PdfPTable headerTable = new PdfPTable(columnwidths);
		PdfPCell qrCodeCell = new PdfPCell();
		String bloodGroup = student.getBloodGroup();
		if(StringUtils.isBlank(bloodGroup)) {
		bloodGroup = getBloodgroupFormSFDC(student.getSapid());
		}
		
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
		Date enrollmentDate = sdformat.parse(student.getRegDate());
		Date dateToBeCheckForRegistrar = sdformat.parse("2022-06-28");
		Image sign=null;
		if(enrollmentDate.compareTo(dateToBeCheckForRegistrar)>0) {
		sign=Image.getInstance("https://testawsfiles.s3.ap-south-1.amazonaws.com/Ravishankar_Kamath_Sign.png");
		sign.scaleAbsolute(75,75);
		sign.setAlignment(Element.ALIGN_CENTER);
		}else {
		sign=Image.getInstance("https://testawsfiles.s3.ap-south-1.amazonaws.com/Meena_Chintamani_Sign.png");
		sign.scaleAbsolute(50,50);
		sign.setAlignment(Element.ALIGN_CENTER);
		}
		qrCodeCell.setBackgroundColor(bgColour);
		/*qrCodeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		qrCodeCell.setVerticalAlignment(Element.ALIGN_CENTER);*/
		
		qrCodeCell.setFixedHeight(165f);
		qrCodeCell.setBorder(0);
		Image qrCode=getQrCodeImage(idCardURL);
		qrCode.setAlignment(Element.ALIGN_CENTER);
		qrCode.scaleAbsolute(200,200);
		qrCodeCell.addElement(qrCode);
		qrCodeCell.setPadding(5);
		headerTable.addCell(qrCodeCell);
		PdfPCell addressCell = new PdfPCell();
		addressCell.setFixedHeight(165f);
		addressCell.setBackgroundColor(bgColour);
		addressCell.setPaddingRight(10f);
		addressCell.setBorder(0);
		//addressCell.setBackgroundColor(BaseColor.YELLOW);
		generateAdddressCell(student.getSapid(), addressCell);
		addressCell.addElement(Chunk.NEWLINE);
		addressCell.addElement(new Paragraph("Contact: "+student.getMobile()
				+ "\nDOB: "+student.getDob()+"          "+"Blood grp : "+bloodGroup+"\n"+student.getProgram(), font10_Bold));
		headerTable.addCell(addressCell);
		document.add(headerTable);
		
		float[] columnwidths2= {2,1};
        PdfPTable footerTable = new PdfPTable(columnwidths2);
        PdfPCell footerCell = new PdfPCell();
        footerCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        footerCell.setBackgroundColor(bgColour);
        footerCell.setFixedHeight(80f);
        footerCell.setBorder(0);
        footerCell.setPaddingLeft(15f);
        footerCell.setPaddingBottom(10f);
        footerCell.addElement(new Paragraph("If found, please return to: "
        		+ "\nSVKM's NMIMS\nNMIMS Global Access School For Continuing Education\n"
        		+ "V.L Mehta Road, Vile Parle, Mumbai, INDIA"
        		+ "\nT. 1800 102 5136(Monday-Saturday) 9 AM-7 PM"
        		+ "\nE. ngasce@nmims.edu * W. online.nmims.edu",descBoldFont));
        footerTable.addCell(footerCell);
        PdfPCell signatureCell = new PdfPCell();
        signatureCell.setFixedHeight(80f);
        signatureCell.setPaddingBottom(10f);
//        signatureCell.setPaddingTop(10f);
        signatureCell.setVerticalAlignment(Element.ALIGN_CENTER);
        signatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        signatureCell.setBackgroundColor(bgColour);
        signatureCell.setBorder(0);
        signatureCell.addElement(sign);
        Paragraph signText=new Paragraph("\nRegistrar",font1);
        signText.setAlignment(Element.ALIGN_CENTER);
        signatureCell.addElement(signText);
        footerTable.addCell(signatureCell);
        document.add(footerTable);
	}
	
	

	/**To create QR code
	 * @Param:- idCardURL - String idCard s3 url
	 */
	private Image getQrCodeImage(String idCardURL) throws Exception {
		BarcodeQRCode qrCode = new BarcodeQRCode(idCardURL, 400, 300, null);
	    Image barcodeImage = qrCode.getImage();
	    Image returnImage = qrCode.getImage();
	    barcodeImage.makeMask();
	    returnImage.setImageMask(barcodeImage);
		return returnImage;
	}
	
	/**To upload pdf on Amazon S3
	 * @Param:- filePath - Local file path like E:\IdCards\\test.pdf
	 * @Param:- fileName - Pdf file name
	 */
	private String uploadPdfToS3(String filePath,String fileName) {
		String folderPath="digitalIdCard/";
		String url="";
		idCardCreationLogger.info("folderPath for aswUpload id card>>"+folderPath);
		HashMap<String, String> s3_response=aWSHelper.uploadLocalFileX(filePath, folderPath+fileName, AWS_DIGITAL_ID_CARD_BUCKET, folderPath);
		aWSHelper.deleteFileFromLocal(filePath);
		url=s3_response.get("url");
		return url;
	}
	
	/**To insert record in DB
	 * @Param:- filePath - Local file path like E:\IdCards\\test.pdf
	 * @Param:- fileName - Pdf file name
	 * * @Param:- uniqueHashKey - uniqueHashKey created for each student
	 */
	private void insertIdCardDetailsToDB(String sapId, String fileName,String uniqueHashKey, String createdBy) {
		studentDAO.saveIdCardDetailsForAStudent(sapId, fileName,uniqueHashKey, createdBy);
	}
	
	/**To fetch student details by uniqueHashKey
	 * @Param:- uniqueHashKey - hashKey created for each unique records
	 */
	public StudentStudentPortalBean getSingleStudentByUniqueHash(String uniqueHashKey) {
		StudentStudentPortalBean studentData=new StudentStudentPortalBean();
		try {
			idCardCreationLogger.info("Fetching details for uniqueHashKey : "+uniqueHashKey);
			studentData=studentDAO.getSingleStudentByUniqueHash(uniqueHashKey);
			HashMap<String, String> mapOfLcNames=getMapOfLcName();
			studentData.setLc(mapOfLcNames.get(studentData.getCenterCode()));
			studentData.setBloodGroup(getBloodgroupFormSFDC(studentData.getSapid()));
		}catch (Exception e) {
			idCardCreationLogger.error("Error while fetching student details by hashKey :"+e.getMessage());
		}
		return studentData;
	}
	
	/**To fetch filePath details by sapid
	 * @Param:- sapid - sapId of the student
	 */
	public String getIdCardFileNameBySapid(String sapid) {
		String fileName="";
		try {
			idCardCreationLogger.info("Fetching fileName for sapId : "+sapid);
			fileName=studentDAO.getIdCardFileNameBySapid(sapid);
			idCardCreationLogger.info("FileName fileName: "+fileName);
		}catch (Exception e) {
			idCardCreationLogger.error("Error while fetching fileName details by sapId :"+e.getMessage());
		}
		return fileName;
	}
	
	public String getBloodgroupFormSFDC(String sapid) {
			idCardCreationLogger.info("Fetching bloodGroup for sapId : "+sapid);
			String bloodgroup="NA";
			try {
				bloodgroup = studentDAO.getBlooadgroupFormSFDC(sapid);
				idCardCreationLogger.info("Bloodgroup : "+bloodgroup);
			} catch (Exception e) {
				idCardCreationLogger.error("Error while fetching bloodGroup from salesforce by sapId :"+e.getMessage());
			}
			return bloodgroup;
	}
	
	/**To check avoid file duplication on S3
	 * @Param:- sapid - sapId of the student
	 */
	public String studentExistingFileOnAWS(String sapid) {
		idCardCreationLogger.info("Fetching fileName for sapId : "+sapid);
		String fileName="";
		try {
			fileName = studentDAO.getIdCardFileNameBySapid(sapid);
			idCardCreationLogger.info("fileName : "+fileName);
		} catch (Exception e) {
			idCardCreationLogger.error("Error while fetching fileName by sapId :"+e.getMessage());
		}
		return fileName;
	}
	
	/**To create IdCard for give enrollment month and year
	 * @Param:- enrollmentMonth - enrollmentMonth for the students
	 * @Param:- enrollmentYear - enrollmentYear for the students
	 */
	@Override
	public String createIdCardByBatchJob(String enrollmentMonth, String enrollmentYear) {
		idCardCreationLogger.info("createIdCardByBatchJob() called!");
		List<StudentStudentPortalBean> studentsList=getStudentsListForEnrollmentMonthAndYear(enrollmentMonth, enrollmentYear);
		List<String> successList=new ArrayList<String>();
		List<String> errorList=new ArrayList<String>();
		HashMap<String, String> mapOfLc=getMapOfLcName();
		
		for (StudentStudentPortalBean studentBean : studentsList) {
			idCardCreationLogger.info("START : Id card creation start for sapid :"+studentBean.getSapid());
			//fetching address from sfdc as address is blank
			/*if(StringUtils.isBlank(studentBean.getAddress())) {
				try {
					studentBean.setAddress(studentDAO.getAddressFormSFDC(studentBean.getSapid()));
				} catch (Exception e) {
					idCardCreationLogger.error("Could not fetch address from SFDC :"+e.getMessage());
				}
			}*/
			
			//fetching bloodGroup from sfdc
			studentBean.setBloodGroup(getBloodgroupFormSFDC(studentBean.getSapid()));
			studentBean.setLc(mapOfLc.get(studentBean.getCenterCode()));
			studentBean.setCreatedBy("Manual");
			
			//creating id card for single Student
			HashMap<String, String> responseMap = createIdCardPdf(studentBean);
			
			if("success".equalsIgnoreCase(responseMap.get("status"))) {
				successList.add(studentBean.getSapid());
			}else {
				errorList.add(studentBean.getSapid());
			}
			idCardCreationLogger.info("END : Id card creation end for sapid :"+studentBean.getSapid());
		}
		String retrunMessage=successList.size()+" out of "+studentsList.size()+" id card created successfully!";
		idCardCreationLogger.info(retrunMessage);
		if (!errorList.isEmpty()) {
			idCardCreationLogger.info("ErrorList for  " + enrollmentMonth + "-" + enrollmentYear +" : "+ errorList);
		}
		//mailSenderHelper.idCardEmailBatchJobAlert(enrollmentMonth, enrollmentYear, studentsList.size(), successList.size());
		return retrunMessage;
	}
	
	public List<StudentStudentPortalBean> getStudentsListForEnrollmentMonthAndYear(String enrollmentMonth, String enrollmentYear) {
		idCardCreationLogger.info("Fetching studentlist for enrollmentMont And Year : "+enrollmentMonth+"/"+enrollmentYear);
		List<StudentStudentPortalBean> studentsList;
		try {
			studentsList=studentDAO.getStudentsListForEnrollmentYearMonth(enrollmentMonth, enrollmentYear);
			
		} catch (Exception e) {
			studentsList=new ArrayList<StudentStudentPortalBean>();
			idCardCreationLogger.error("Error while fetching for enrollmentMont And Year :"+e.getMessage());
		}
		idCardCreationLogger.info("Count of students for  "+enrollmentMonth+"/"+enrollmentYear +" : "+studentsList.size());
		return studentsList;
	}
	
	@Override
	public void updateIdCard(StudentStudentPortalBean studentBean) {
		idCardCreationLogger.info("START : Id card updation start for sapid :"+studentBean.getSapid());
		HashMap<String, String> mapOfLc=getMapOfLcName();
		//fetching bloodGroup from sfdc
		studentBean.setBloodGroup(getBloodgroupFormSFDC(studentBean.getSapid()));
		studentBean.setLc(mapOfLc.get(studentBean.getCenterCode()));
		studentBean.setCreatedBy("Manual");
		
		//creating id card for single Student
		HashMap<String, String> response = createIdCardPdf(studentBean);
		
//		//send email if failed to update id card pdf
//		if("error".equals(response.get("status")) && !"ID card already created".equals(response.get("response"))) {
//			mailer.idCardUpdationFailedEmailAlert(studentBean.getSapid(), response.get("response"));
//		}
		idCardCreationLogger.info("END : Id card updation end for sapid :"+studentBean.getSapid());
	}
	
	public String checkProgramAndSpecialization(String sapid, String program) {
		idCardCreationLogger.info("Fetching sepcialization for sapId : "+sapid);
		IdCardStudentPortalBean bean = new IdCardStudentPortalBean();
		StringBuilder programwithSpecialization = new StringBuilder();
		
		try {
				bean = studentDAO.getSpecialazationFromSFDC(sapid);
				if(!StringUtils.isBlank(bean.getSpecializationType())){
				programwithSpecialization.append(bean.getProgramName());
				idCardCreationLogger.info("sepcialzation : "+bean.getSpecializationType());
				idCardCreationLogger.info("programName : "+bean.getProgramName());
				idCardCreationLogger.info("sepcialzation1 : "+bean.getSpecialization1());
				idCardCreationLogger.info("sepcialzation2 : "+bean.getSpecialization2());
				String replaceString = bean.getProgramName()+" - ";
				
				if("MBA (WX)".equals(bean.getProgramName()))
					replaceString = bean.getProgramName().replace(" ", "")+" - ";

				
				if(!StringUtils.isBlank(bean.getSpecialization1())) {
					String specialization1 = bean.getSpecialization1().replace(replaceString, "");
					programwithSpecialization.append(" - "+specialization1);
				}
				
				if(!StringUtils.isBlank(bean.getSpecialization2())) {
					String specialization2 = bean.getSpecialization2().replace(replaceString, "");
					programwithSpecialization.append(" and "+specialization2);
				}
				
					return programwithSpecialization.toString();
				}else {
					return program;
				}
	
		} catch (Exception e) {
			e.printStackTrace();
			idCardCreationLogger.error("Error while fetching sepcialzation from salesforce by sapId :"+e.getMessage());
			return program;
		}
	}

	@Override
	public String createIdCardBySapId(String sapId) {
		
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean studentBean = pDao.getSingleStudentsData(sapId);
		//To fetch most recent registration in case of program change
		StudentStudentPortalBean recReg = pDao.getStudentsMostRecentRegistrationData(sapId);
		if(studentBean == null) {
			idCardCreationLogger.error("student not found for sapId :"+sapId);
			return "Student not found for sapId : "+sapId;
		}
		
		HashMap<String, String> mapOfLc=getMapOfLcName();
		idCardCreationLogger.info("START : Id card creation start for sapid :"+studentBean.getSapid());
		//fetching address from sfdc as address is blank
		if(StringUtils.isBlank(studentBean.getAddress())) {
			try {
				studentBean.setAddress(studentDAO.getAddressFormSFDC(studentBean.getSapid()));
			} catch (Exception e) {
				idCardCreationLogger.error("Could not fetch address from SFDC :"+e.getMessage());
			}
		}
		studentBean.setProgram(recReg.getProgram());
		studentBean.setConsumerProgramStructureId(recReg.getConsumerProgramStructureId());
		//fetching bloodGroup from sfdc
		studentBean.setBloodGroup(getBloodgroupFormSFDC(studentBean.getSapid()));
		studentBean.setLc(mapOfLc.get(studentBean.getCenterCode()));
		studentBean.setCreatedBy("Manual");
		
		//creating id card for single Student
		HashMap<String, String> responseMap = createIdCardPdf(studentBean);
		String returnMessage = responseMap.get("response");
		idCardCreationLogger.info("END : Id card creation end for sapid :"+studentBean.getSapid());
		return returnMessage;
	}
	
	public void generateAdddressCell(String sapId, PdfPCell addressCell) throws Exception{
		HashMap<String, String> mapOfPermanentAndShippingAddresBySapid = studentDAO.getPermanentAndShippingAddresBySapid(sapId);
		Font addressFont = fontAddress8;
		if (mapOfPermanentAndShippingAddresBySapid.get("shippingAddress").length() > 160 || (mapOfPermanentAndShippingAddresBySapid.get("permanentAddress").length() > 160)) {
			addressFont = font7;
		}
		addressCell.addElement(new Paragraph("Permanent Address : ",font10_Bold));
		addressCell.addElement(new Paragraph(mapOfPermanentAndShippingAddresBySapid.get("permanentAddress").replaceAll("\\<.*?>", "") ,addressFont));
		addressCell.addElement(new Paragraph("\nShipping Address : ",font10_Bold));
		addressCell.addElement(new Paragraph(mapOfPermanentAndShippingAddresBySapid.get("shippingAddress").replaceAll("\\<.*?>", "") ,addressFont));
	}
}
