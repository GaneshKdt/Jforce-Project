package com.nmims.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.ParameterParser;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.nmims.helpers.AmazonS3Helper;

@Service
public class DocumentService {
	private AmazonS3Helper amazonS3Helper;
	
	private static final String STUDENT_PROFILE_DOCUMENT_BUCKET = "studentprofiledocuments";
	private static final String STUDENT_PROFILE_DOCUMENT_FOLDER = "StudentProfileDocuments/";
	
	private static final char[] PARAMPARSER_SEPERATORS = new char[] {';', ','};
	private static final String[] supportedMimeTypes = new String[] {"image/jpeg", "image/png", "image/svg+xml", "image/gif", "application/pdf", "application/zip", "application/vnd.rar",
																	"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
																	"application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
																	"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; 
	
	private static final String FOLDER_NAME_VALIDATION_REGEX = "^([a-zA-Z0-9][\\-_]?){1,199}[a-zA-Z0-9]$";
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
	
	/**
	 * Using Constructor Dependency Injection to inject the required dependencies.
	 * @param instances of the required dependencies
	 */
	@Autowired
	public DocumentService(AmazonS3Helper amazonS3Helper) {
		Objects.requireNonNull(amazonS3Helper);			//Fail-fast approach, field is guaranteed be non-null.
		this.amazonS3Helper = amazonS3Helper;
	}
	
	/**
	 * Extract boundary from the Request Header and read the byteArray as a MultiPart Stream, 
	 * read headers and file content from the MultiPart Encapsulation and upload the file into Amazon s3
	 * @param contentTypeReqHeader - Request Header of Content-type used to extract the boundary
	 * @param bodyContent - Content stored as an array of bytes
	 * @return - Map of URL(s) of the uploaded document(s)
	 */
	public Map<String, String> studentProfileDocumentByteArrayUpload(String contentTypeReqHeader, byte[] bodyContent, String folderName) {
		int uploadedFileCount = 0;
		ParameterParser paramParser = new ParameterParser();
        paramParser.setLowerCaseNames(true);
        Map<String, String> uploadedFileUrlMap = new HashMap<>();
        
        validateFolderName(folderName);
        String folderPath = STUDENT_PROFILE_DOCUMENT_FOLDER + folderName + "/";
        logger.info("FolderName validated successfully, created folderPath: {}", folderPath);
        
		try {
			//Extracting the boundary from the Content-type request header
	        Map<String, String> contentTypeHeaderMap = paramParser.parse(contentTypeReqHeader, PARAMPARSER_SEPERATORS);
	        String boundary = contentTypeHeaderMap.get("boundary");
	        
	        if(StringUtils.isBlank(boundary))
	        	boundary = "----------------------------741e90d31eff";
	        logger.info("Multipart-body encapsulation boundary: {}", boundary);
			
	        //Converting byteArray into an InputStream to be used in MultiPart Stream
			InputStream inStream = new ByteArrayInputStream(Objects.requireNonNull(bodyContent));
			MultipartStream multipartStream = new MultipartStream(inStream, boundary.getBytes(), 4096, null);		//4096 is the default buffSize used by the MultipartStream, 
																													//buffSize of boundary.length is sufficient
			//Checks for the first MultiPart Encapsulation
			boolean nextPart = multipartStream.skipPreamble();
			logger.info("Presence of first Multipart Encapsulation in the content passed: {}", nextPart);
			while(nextPart) {
				uploadedFileCount++;
				String fileNoMarker = (uploadedFileCount == 1) ? "" : String.valueOf(uploadedFileCount);			//Used in the response key to denote the file number, number denoted from the second file onwards
				try {
					String uploadFileUrl = readUploadMultipartEncapsulation(multipartStream, paramParser, folderPath);
					logger.info("Document Successfully uploaded on Amazon s3 bucket: {}, in bucketFolder: {} with file Url: {}", STUDENT_PROFILE_DOCUMENT_BUCKET, folderPath, uploadFileUrl);
					uploadedFileUrlMap.put("document" + fileNoMarker + "Url", uploadFileUrl);
				}
				catch(RuntimeException ex) {
					logger.error("Error while uploading the document on Amazon s3, errorMessage returned: {}", ex.getMessage());
					uploadedFileUrlMap.put("document" + fileNoMarker + "ErrorMessage", ex.getMessage());
				}
				
				//Checks for more MultiPart Encapsulation in the Stream
				nextPart = multipartStream.readBoundary();
				logger.info("More Multipart Encapsulations present in stream: {}", nextPart);
			}
			
			return uploadedFileUrlMap;
		}
		catch(IOException ex) {
			logger.error("I/O operation error encountered while processing the byteArray content, Exception thrown: {}", ex.toString());
//			ex.printStackTrace();
			throw new RuntimeException("Failed to read the file contents and upload document(s) to Amazon s3.");
		}
		catch(Exception ex) {
			logger.error("Error encountered while processing the byteArray content, Exception thrown: {}", ex.toString());
//			ex.printStackTrace();
			throw new RuntimeException("Error encountered while uploading document(s) to Amazon s3.");
		}
	}
	
	/**
	 * Method which reads the MultiPart Encapsulation, retrieves headers and uploads file content into Amazon S3
	 * @param multipartStream - multipartStream containing the Encapsulation
	 * @param paramParser - ParameterParser object to parse the headers and read as a key/value pair
	 * @param folderName - name of the folder where the file is to be uploaded
	 * @return - uploaded file URL as String
	 */
	private String readUploadMultipartEncapsulation(MultipartStream multipartStream, ParameterParser paramParser, String folderPath) {
		try {
			logger.info("Reading Headers of the Multipart Encapsulation.");
			String partHeaders = multipartStream.readHeaders().trim();			//reading the file headers
			
			//Splitting out the file headers
			String[] splitHeaders = partHeaders.split("\n");					//Splitting multi-line part headers
			logger.info("Splitting headers using \\n as the delimiter, amount of headers retrieved: {}", splitHeaders.length);
			
			//Storing file headers as a key/value pair
			Map<String, String> headerNameValuePair = new HashMap<>();
			for(String header: splitHeaders) {
				String[] headerNameValue = header.split(":", 2);
				if(headerNameValue.length == 2)
					headerNameValuePair.put(headerNameValue[0].toLowerCase(), headerNameValue[1].trim());
				else
					headerNameValuePair.put(headerNameValue[0].toLowerCase(), null);
			}
			
	        String disposition = headerNameValuePair.get("content-disposition");
	        String mimeType = headerNameValuePair.get("content-type");
            
	        //Extracting required parameters from the content-disposition header
            Map<String, String> dispositionHeaderMap = paramParser.parse(disposition, PARAMPARSER_SEPERATORS);
            String name = dispositionHeaderMap.get("name");
            String fileName = dispositionHeaderMap.get("filename");
            logger.info("Parameters retrieved from the Content-Disposition header - name: {}, filename: {}", name, fileName);
            logger.info("MIME Type Parameter retrieved from the Content-Type header: {}", mimeType);

	        //Check if the file type is Supported
	        if(checkFileMimeType(mimeType)) {
		        ByteArrayOutputStream resourceOutputStream = new ByteArrayOutputStream();
		        int amountOfDataWritten = multipartStream.readBodyData(resourceOutputStream);		//reading the file body
		        logger.info("Amount of body data read from Multipart encapsulation and written into the OutputStream - {} bytes", amountOfDataWritten);
		        
		        byte[] resourceByteArray = resourceOutputStream.toByteArray();
				logger.info("Length of the OutputStream content stored as a byteArray: {}", resourceByteArray.length);
				
				String s3UploadFilePath = createFilePathForS3Upload(fileName, folderPath);			//creating a filePath to be used while uploading the file into Amazon S3
				logger.info("S3 upload path of the file: {}", s3UploadFilePath);
				
				//Uploading file into Amazon S3
				Map<String, String> fileUploadResponse = amazonS3Helper.uploadByteArrayFile(resourceByteArray, fileName, mimeType, STUDENT_PROFILE_DOCUMENT_BUCKET, folderPath, s3UploadFilePath);
				if(fileUploadResponse.get("status").equals("success")) 
					return fileUploadResponse.get("fileUrl");
				else 
					throw new RuntimeException(fileUploadResponse.get("fileUrl"));
	        }
	        else {
	        	logger.info("MIME Type of resource not supported.");
//	            mimeType = "application/octet-stream";
	        	throw new RuntimeException("File MIME Type Not Supported!");
	        }
		}
		catch(IOException ex) {
			logger.error("I/O Operations error while reading the Multipart Encapsulation and uploading the file content to Amazon s3, Exception thrown: {}", ex.toString());
//			ex.printStackTrace();
			throw new RuntimeException("Failed to upload the document, I/O Error encountered.");
		}
		catch(RestClientException ex) {
			logger.error("Error while connecting to the AWS File Upload API using RestTemplate Exchange method, Exception thrown: {}", ex.toString());
//			ex.printStackTrace();
			throw new RuntimeException("Failed to connect to AWS for document upload.");
		}
		catch(RuntimeException ex) {
			logger.error("Manual Exception thrown due to an unexpected result, Error Message: {}", ex.getMessage());
//			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
		catch(Exception ex) {
			logger.error("Error while reading the Multipart Encapsulation and uploading the file content to Amazon s3, Exception thrown: {}", ex.toString());
//			ex.printStackTrace();
			throw new RuntimeException("Error encountered while trying to upload document.");
		}
	}
	
	/**
	 * Validate the folderName passed as the parameter, the name should not be empty or null.
	 * Name should follow the naming convention specified in the FOLDER_NAME_VALIDATION_REGEX.
	 * IllegalArgumentException is thrown if the name does not satisfy the above mentioned validations.
	 * @param folderName - name of the folder
	 */
	private void validateFolderName(String folderName) {
		if(Objects.isNull(folderName) || folderName.isEmpty())
			throw new IllegalArgumentException("Folder Name cannot be null or empty!");
		
		if(!isFolderNameValid(folderName))
			throw new IllegalArgumentException("Folder Name does not follow the naming convention: Aplhanumeric Characters allowed [0-9A-Za-z] " +
												", Special Characters [ - (hyphen) and _ (underscore)] and length range [1-200 characters]");
	}
	
	/**
	 * Checks if the file MIME Type is supported
	 * @param mimeType - MIME Type of the file
	 * @return - boolean value denoting if the MIME Type is supported or not
	 */
	private boolean checkFileMimeType(String mimeType) {
		logger.info("Checking if the MIME Type - {}, is supported.", mimeType);
		return Arrays.asList(supportedMimeTypes).contains(mimeType);
	}
	
	/**
	 * Creating a filePath using the bucketFolder name, date and random alphanumeric to be used for uploading file into Amazon S3
	 * @param fileName - Original FileName of the file to extract the File extension
	 * @return - filePath as a String
	 * @param folderName - name of the folder where the file is to be uploaded
	 */
	private String createFilePathForS3Upload(String fileName, String folderPath) {
		String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
		String randomNumber = RandomStringUtils.randomAlphanumeric(6);
		String fileExtension = FilenameUtils.getExtension(fileName);
		return folderPath + randomNumber + "_" + dateStr + "." + fileExtension;
	}
	
	/**
	 * Validate the folder name using the FOLDER_NAME_VALIDATION_REGEX.
	 * Folder Name can contain alphanumeric characters [0-9a-zA-Z], and special characters [hyphen, underscore].
	 * Accepted length of the name ranges between 1 - 200 characters.
	 * Allows one hyphen/underscore character at a time, no repetition.
	 * @param folderName - name of the folder to be validated
	 * @return boolean value indicating if the specified name follows the naming convention mentioned above.
	 */
	private boolean isFolderNameValid(String folderName) {
		Pattern pattern = Pattern.compile(FOLDER_NAME_VALIDATION_REGEX);
		Matcher matcher = pattern.matcher(folderName);
        return matcher.matches();
    }
}
