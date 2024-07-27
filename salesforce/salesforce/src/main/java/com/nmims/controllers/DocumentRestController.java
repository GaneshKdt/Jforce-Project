package com.nmims.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.services.DocumentService;

/**
 * Contains Rest API's used to upload Documents on Amazon s3.
 * @author Raynal Dcunha
 */
@RestController
@RequestMapping("m")
public class DocumentRestController {
	private DocumentService documentService;
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentRestController.class);
	
	/**
	 * Using Constructor Dependency Injection to inject the required dependencies.
	 * @param instances of the required dependencies
	 */
	@Autowired
	public DocumentRestController(DocumentService documentService) {
		Objects.requireNonNull(documentService);			//Fail-fast approach, field is guaranteed be non-null.
		this.documentService = documentService;
	}
	
	/**
	 * Reads and Uploads the documents sent as an array of bytes into Amazon S3
	 * @param requestHeaderContentType - stores the Request Header of Content-type as a String
	 * @param content - fileContents as an array of bytes
	 * @return - A JSON containing the uploaded file URL or an errorMessage in case of an error
	 */
	@PutMapping(value = "/uploadStudentProfileDocumentByteArray", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<Map<String, String>> uploadStudentProfileDocumentByteArray(@RequestParam String folderName, @RequestBody byte[] content, 
																					@RequestHeader("content-type") String requestHeaderContentType) {
		Map<String, String> responseMap = new HashMap<>();
		try {
			logger.info("Uploading Student Profile Document(s) to Amazon s3 with Content length of {} bytes and Content-Type: {} and folderName: {}", 
						content.length, requestHeaderContentType, folderName);
			responseMap = documentService.studentProfileDocumentByteArrayUpload(requestHeaderContentType, content, folderName);
			logger.info("URL of Student Profile Document(s) uploaded to Amazon s3: {} ", responseMap);
			
			responseMap.put("status", "success");
			responseMap.put("message", "Student document uploaded successfully!");
			return new ResponseEntity<>(responseMap, HttpStatus.OK);
		}
		catch(IllegalArgumentException ex) {
			responseMap.put("status", "fail");
			responseMap.put("message", ex.getMessage());
			return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
		}
		catch(Exception ex) {
			responseMap.put("status", "fail");
			responseMap.put("message", ex.getMessage());
			return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
