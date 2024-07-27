package com.nmims.helpers;


import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.LinkedInAddCertToProfileBean;
import com.nmims.beans.LinkedInAddCertToProfileBean.SpecificContent;
import com.nmims.beans.LinkedInAddCertToProfileBean.SpecificContent.Media;
import com.nmims.controllers.HomeController;
import com.nmims.daos.ServiceRequestDao;

@Service("linkedInManager")
public class LinkedInManager {
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	ServiceRequestDao serviceRequestDao;
	
	@Value("${LINKED_IN_CLIENT_ID}")
	private String LINKED_IN_CLIENT_ID;
	
	@Value("${LINKED_IN_CLIENT_SECRET}")
	private String LINKED_IN_CLIENT_SECRET;
	
	@Value("${LINKED_IN_SCOPE}")
	private String LINKED_IN_SCOPE;
	
	@Value("${LINKED_IN_CODE}")
	private String LINKED_IN_CODE;

	@Value("${LINKED_IN_RANK_REDIRECT_URI}")
	private String LINKED_IN_RANK_REDIRECT_URI; 
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Value("${LINKED_IN_REDIRECT_URI}")
	private String LINKED_IN_REDIRECT_URI; 
	
	@Value( "${LEARNING_RESOURCES_BASE_PATH}" )
	private String LEARNING_RESOURCES_BASE_PATH;
	
	@Value( "${LINKED_IN_REGISTER_IMAGE_URL}" )
	private String LINKED_IN_REGISTER_IMAGE_URL;
	
	@Value( "${LINKED_IN_ORGANIZATION_ID}" )
	private String LINKED_IN_ORGANIZATION_ID;
	
	@Value( "${MARKSHEETS_PATH}" )
	private String MARKSHEETS_PATH;
	
	@Value("${LINKED_IN_SHARE_URL}")
	private String LINKED_IN_SHARE_URL;
	
	@Value("${LINKED_IN_SHARE_IMAGES_PATH}")
	private String LINKED_IN_SHARE_IMAGES_PATH;

	@Value("${CERTIFICATES_SOURCE_IMAGE_PATH}")
	private String CERTIFICATES_SOURCE_IMAGE_PATH;

	@Value("${LOCAL_CERTIFICATES_PATH}")
	private String LOCAL_CERTIFICATES_PATH;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	public LinkedInAddCertToProfileBean sharePostAsURLOnLinkedIn( String shareText, String shareURL, String userId ) {
		
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Content-Type", "application/json");
		RestTemplate restTemplate = new RestTemplate();	
		LinkedInAddCertToProfileBean linkedInBean = new LinkedInAddCertToProfileBean(); 
		
		try {

			linkedInBean = serviceRequestDao.getLinkedInProfile( userId );
			linkedInBean.setLinkedInAuthorized(true);
			headers.add("X-Restli-Protocol-Version", "2.0.0");
			headers.add("Authorization", "Bearer " + linkedInBean.getAccess_token());
			
			String requestBody = " { "
					+ "    \"author\": \"urn:li:person:" +  linkedInBean.getPersonId() + "\" , " 
					+   " \"lifecycleState\": \"PUBLISHED\", "
					+  "  \"specificContent\": { "
					+  "     \"com.linkedin.ugc.ShareContent\": { "
					+  "        \"shareCommentary\": { "
					+ " \"attributes\": [ " 
					+          "   { "
					+              "  \"length\":51 , "
					+            "  \"start\": 25, "
					+          "  \"value\": { "
					+            "  \"com.linkedin.common.CompanyAttributedEntity\": { "
					+            "   \"company\": \"urn:li:organization:13365633\" "
					+         "  } "
					+      " } "
					+   " } "
					+       " ], "
					+  "           \"text\": \" "+shareText+"\" "
					+ "      }, "
					+ "     \"shareMediaCategory\": \"ARTICLE\", "
					+ "    \"media\": [ "
					+ "       { "
					+ "          \"status\": \"READY\", "
					+ "         \"description\": { "	                        
					+ "  \"text\": \"Get detailed information about Certificate.\" "
					+  "     }, "
					+  "       \"originalUrl\": \"" + shareURL + "\", "
					+    "      \"title\": { "
					+      "         \"text\": \"View Certificate\" "
					+        "    } "
					+        " } "
					+      " ] "
					+   " } "
					+ " }, "
					+ " \"visibility\": { "
					+   " \"com.linkedin.ugc.MemberNetworkVisibility\": \"CONNECTIONS\" "
					+ " } " 
					+		" } ";

			HttpEntity<String> entity = new HttpEntity<String>(requestBody, headers);
			
			try {
				linkedInBean.setLinkedInShareStatus(true);
				ResponseEntity<LinkedInAddCertToProfileBean> response  = 
						restTemplate.postForEntity("https://api.linkedin.com/v2/ugcPosts", entity ,LinkedInAddCertToProfileBean.class);
				return linkedInBean;
			}catch (Exception e) {
				//e.printStackTrace();
				linkedInBean.setLinkedInAuthorized(true);
				linkedInBean.setLinkedInShareStatus(false);
				return linkedInBean;
			}	
			 
		}catch (Exception e) {

			linkedInBean.setLinkedInAuthorized(false);
			logger.info("casue: "+e.getMessage());
			return linkedInBean;
			
		}
		
	}
	
	public void linkedinAuthCallback( String code, String sapid, String shareURL, String shareText, String redirect_uri, String link_path ) throws Exception{
		
		HttpHeaders headers =  new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		
		LinkedInAddCertToProfileBean linkedInAddCertToProfileBean = new LinkedInAddCertToProfileBean();
		linkedInAddCertToProfileBean.setGrant_type("authorization_code");
		linkedInAddCertToProfileBean.setCode(code);
		linkedInAddCertToProfileBean.setClient_id("86fga8klurs8cq");
		linkedInAddCertToProfileBean.setClient_secret("p5XFLmbqVDxpHRu1");
		linkedInAddCertToProfileBean.setRedirect_uri( redirect_uri );
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type","authorization_code");
		map.add("code",code);
		map.add("redirect_uri", redirect_uri);
		map.add("client_id","86fga8klurs8cq");
		map.add("client_secret","p5XFLmbqVDxpHRu1");
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map,headers);
		ResponseEntity<LinkedInAddCertToProfileBean> response  = 
		restTemplate.postForEntity("https://www.linkedin.com/oauth/v2/accessToken", entity ,LinkedInAddCertToProfileBean.class);
		headers =  new HttpHeaders();
		headers.add("Authorization", "Bearer " + response.getBody().getAccess_token());
	    headers.add("Accept", MediaType.APPLICATION_JSON.toString());

	    HttpEntity<String> entity3 = new HttpEntity<String>(headers);
	    String aurthor = null;
		
		try {
			
			ResponseEntity<String> response2   = restTemplate.exchange("https://api.linkedin.com/v2/me",  HttpMethod.GET, entity3 ,String.class);
			JsonObject jsonObj = new JsonParser().parse(response2.getBody()).getAsJsonObject();
			aurthor = jsonObj.get("id").getAsString();
			int  success =  serviceRequestDao.createLinkedInProfile(aurthor, sapid, code, response.getBody().getAccess_token(), response.getBody().getExpires_in());	
			logger.info("success: "+success);
			
		} catch (RestClientException e) {
			//e.printStackTrace();
		}

		headers =  new HttpHeaders();
		headers.add("X-Restli-Protocol-Version", "2.0.0");
		headers.add("Authorization", "Bearer " + response.getBody().getAccess_token());
		linkedInAddCertToProfileBean.setAuthor("urn:li:person:" + aurthor);
		linkedInAddCertToProfileBean.setLifecycleState("PUBLISHED");
		SpecificContent  sp = linkedInAddCertToProfileBean.new SpecificContent();
		sp.setShareMediaCategory("ARTICLE");
		HashMap<String,String> shareCommentary = new HashMap<String,String>();
		shareCommentary.put("text", "Learning more about LinkedIn by reading the LinkedIn Blog! #NMIMS #NGASCE @Shiv Golani");
		sp.setShareCommentary(shareCommentary);
		
		Media md = sp.new Media();
		md.setStatus("READY");
		HashMap<String,String> description = new HashMap<String,String>();
		description.put("text", "Official LinkedIn Blog - Your source for insights and information about LinkedIn.");
	
		md.setDescription(description);

		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		headers =  new HttpHeaders();
		headers.add("X-Restli-Protocol-Version", "2.0.0");
		headers.add("Authorization", "Bearer " + response.getBody().getAccess_token());
		
		registerUploadPDF( sapid, aurthor, link_path, response.getBody().getAccess_token(), shareText);
		
//		String requestJson =  " { "
//				+ "    \"author\": \"urn:li:person:" +  aurthor + "\" , " 
//				+   " \"lifecycleState\": \"PUBLISHED\", "
//				+  "  \"specificContent\": { "
//				+  "     \"com.linkedin.ugc.ShareContent\": { "
//				+  "        \"shareCommentary\": { "
//				+ " \"attributes\": [ " 
//				+          "   { "
//				+              "  \"length\":51 , "
//				+            "  \"start\": 25, "
//				+          "  \"value\": { "
//				+            "  \"com.linkedin.common.CompanyAttributedEntity\": { "
//				+            "   \"company\": \"urn:li:organization:13365633\" "
//				+         "  } "
//				+      " } "
//				+   " } "
//				+       " ], "
//				+  "           \"text\": \" "+shareText+"\" "
//				+ "      }, "
//				+ "     \"shareMediaCategory\": \"ARTICLE\", "
//				+ "    \"media\": [ "
//				+ "       { "
//				+ "          \"status\": \"READY\", "
//				+ "         \"description\": { "	                        
//				+ "  \"text\": \"Get detailed information about Certificate.\" "
//				+  "     }, "
//				+  "       \"originalUrl\": \"" + shareURL + "\", "
//				+    "      \"title\": { "
//				+      "         \"text\": \"View Certificate\" "
//				+        "    } "
//				+        " } "
//				+      " ] "
//				+   " } "
//				+ " }, "
//				+ " \"visibility\": { "
//				+   " \"com.linkedin.ugc.MemberNetworkVisibility\": \"CONNECTIONS\" "
//				+ " } " 
//				+		" } ";
//
//		logger.info("requestJson: "+requestJson);
//		HttpEntity<String> entity2 = new HttpEntity<String>( requestJson,headers );
//
//		ResponseEntity<LinkedInAddCertToProfileBean> response2  = 
//				restTemplate.postForEntity("https://api.linkedin.com/v2/ugcPosts", entity2 ,LinkedInAddCertToProfileBean.class);

	}

	
	private LinkedInAddCertToProfileBean getAccessToken(final String code) {
		
		HttpHeaders headers =  new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();		
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		
		
		map.add("grant_type","authorization_code");
		map.add("code",code);
		map.add("redirect_uri", SERVER_PATH + "studentportal/linkedin/auth/callback/registration");
		map.add("client_id","86fga8klurs8cq");
		map.add("client_secret","p5XFLmbqVDxpHRu1");
		
		
		
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map,headers);
		
		ResponseEntity<LinkedInAddCertToProfileBean> access_token_object  = 
			restTemplate.postForEntity("https://www.linkedin.com/oauth/v2/accessToken", entity ,LinkedInAddCertToProfileBean.class);
		
		return access_token_object.getBody();
	}
	
	private LinkedInAddCertToProfileBean getProfile(String access_token){
		
		LinkedInAddCertToProfileBean linkedin_profile_object = new LinkedInAddCertToProfileBean(); 

		String aurthor = null;
		String localizedFirstName = null;
		String lastName = null;
		String localizedLastName = null; 
		String profilePicture = null;
		
		HttpHeaders headers =  new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();	
		
		 headers =  new HttpHeaders();
		 headers.add("Authorization", "Bearer " + access_token);

	     headers.add("Accept", MediaType.APPLICATION_JSON.toString());

			HttpEntity<String> entity3 = new HttpEntity<String>(headers);
		ResponseEntity<String> response2   = restTemplate.exchange("https://api.linkedin.com/v2/me",  HttpMethod.GET, entity3 ,String.class);
		
		logger.info("Person Profile");

		logger.info(response2.toString());
		JsonObject jsonObj = new JsonParser().parse(response2.getBody()).getAsJsonObject();
		aurthor = jsonObj.get("id").getAsString();
		localizedFirstName = jsonObj.get("localizedFirstName").getAsString();
		linkedin_profile_object.setAuthor(aurthor); 
		linkedin_profile_object.setFirstName(localizedFirstName);
		return linkedin_profile_object;
		
		
	}
	
	
	
	
	public void linkedinAuthCallbackV2(String code, String sapid) throws Exception{
	
		LinkedInAddCertToProfileBean access_token_object = new LinkedInAddCertToProfileBean(); 
		LinkedInAddCertToProfileBean linkedin_profile_object = new LinkedInAddCertToProfileBean(); 

		try {
			access_token_object = getAccessToken(code);
			linkedin_profile_object = getProfile(access_token_object.getAccess_token());
			int  success =  serviceRequestDao.createLinkedInProfile(linkedin_profile_object.getAuthor(), sapid, code, access_token_object.getAccess_token(), access_token_object.getExpires_in());	
	
		} catch (Exception e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
		}
		
	}
		
	
	
	public void registerUploadPDF(final String user_id, final String person_id, final String source_dir, 
			final String access_token, final String share_text) {
		
		logger.info("registerUploadPDF ==> Register Upload Called");
		logger.info(source_dir);
		
		try {
            String sourceDir = source_dir; // Pdf files are read from this folder
            String destinationDir = LINKED_IN_SHARE_IMAGES_PATH; // converted images from pdf document are saved here

            File sourceFile = new File(sourceDir);
            File destinationFile = new File(destinationDir);
            if (!destinationFile.exists()) {
                destinationFile.mkdir();
                logger.info("Folder Created -> "+ destinationFile.getAbsolutePath());
            }
            if (sourceFile.exists()) {
                logger.info("Images copied to Folder Location: "+ destinationFile.getAbsolutePath());             
                PDDocument document = PDDocument.load(sourceFile);
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                int numberOfPages = document.getNumberOfPages();
                logger.info("Total files to be converting -> "+ numberOfPages);

                String fileName = sourceFile.getName().replace(".pdf", "");             
                String fileExtension= "png";
                /*
                 * 600 dpi give good image clarity but size of each image is 2x times of 300 dpi.
                 * Ex:  1. For 300dpi 04-Request-Headers_2.png expected size is 797 KB
                 *      2. For 600dpi 04-Request-Headers_2.png expected size is 2.42 MB
                 */
                int dpi = 600;// use less dpi for to save more space in harddisk. For professional usage you can use more than 300dpi 
                File outPutFile = null;
                
                for (int i = 0; i < numberOfPages; ++i) {
                    outPutFile = new File(destinationDir + fileName +"_"+ (i+1) +"."+ fileExtension);
                    BufferedImage bImage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
                    ImageIO.write(bImage, fileExtension, outPutFile);
                }

                document.close();
                logger.info("Converted Images are saved at -> "+ destinationFile.getAbsolutePath());
                
                registerUploadImage(user_id, person_id, outPutFile.getAbsolutePath(),access_token, share_text);
                
            } else {
                System.err.println(sourceFile.getName() +" File not exists");
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
			
//		try {
//			
//            PDDocument document = PDDocument.load(physical_path);
//            List<PDPage> list = document.getDocumentCatalog().getAllPages();
//            
//            for (PDPage page : list) {
//            	
//                BufferedImage image = page.convertToImage();
//                String fileName =  physical_path.substring(physical_path.lastIndexOf('/') + 1);
//                fileName = fileName.substring(0, fileName.lastIndexOf('.'));
//
//                
//	             File outputfile = new File(LINKED_IN_SHARE_IMAGES_PATH + fileName+".png");
//	             logger.info("Image Created -> "+ outputfile.getName());
//	             ImageIO.write(image, "png", outputfile);
//	             
//	             logger.info(outputfile.getAbsolutePath());
//	             registerUploadImage(user_id, person_id, outputfile.getAbsolutePath(),access_token, share_text);
//          
//            }
//		}catch(Exception e){		//restTemplate.
//			e.printStackTrace();
//		}
//		
//	}
	

public void registerUploadImage(final String user_id, final String person_id, final String link_path, final String access_token,  final String shareText) {
	
	logger.info("registerUploadImage ==>Register Upload Called");
	RestTemplate restTemplate = new RestTemplate();
	HttpHeaders headers = new HttpHeaders();
	
	headers.add("Content-Type", "application/json");
	headers.add("X-Restli-Protocol-Version", "2.0.0");
	headers.add("Authorization", "Bearer " + access_token);
	
	
	HttpHeaders headers2 = new HttpHeaders();
	
	try {
		
		//Step 1 Register Image
		String register_image_request_json = " { "
				  + " \"registerUploadRequest\": { "
		       + " \"recipes\": [ "
		           + " \"urn:li:digitalmediaRecipe:feedshare-image\" "
		       + " ],"
		       + " \"owner\": \"urn:li:person:"  + person_id +    "\", "
		       + " \"serviceRelationships\": [ "
		          + " { "
		               + " \"relationshipType\": \"OWNER\", "
		               + " \"identifier\": \"urn:li:userGeneratedContent\" "
		           + " } "
		       + " ] "
		   + " } "
		+ " } ";
		
		HttpEntity<String> entity = new HttpEntity<String>(register_image_request_json, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(LINKED_IN_REGISTER_IMAGE_URL , entity, String.class);
		
		
		//Step 2 Upload Image
		JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
;			jsonObj = jsonObj.getAsJsonObject("value");
		String asset = jsonObj.get("asset").getAsString();
		jsonObj = jsonObj.getAsJsonObject("uploadMechanism");
		jsonObj = jsonObj.getAsJsonObject("com.linkedin.digitalmedia.uploading.MediaUploadHttpRequest");
		String uploadUrl = jsonObj.get("uploadUrl").getAsString();
		headers2.add("Authorization", "Bearer " + access_token);

    
        
   
//       logger.info("Image Created -> "+ outputfile.getName());

         
//         logger.info(link_path);
         byte[] fileContents = Files.readAllBytes( new 
                 File(link_path).toPath() );
         
         HttpEntity<byte[]> entity2 = new HttpEntity<>(fileContents, headers2);

           ResponseEntity<String> response2 = restTemplate.exchange(uploadUrl,HttpMethod.PUT, entity2, String.class);
           
//           logger.info(response2);

          // String shareText = "In my quest to become a better professional, I have successfully completed a program from NMIMS Global Access School for Continuing Education - India\'s Largest Ed-Tech University #NMIMSGlobalAccess #ContinuingEducation";
           String word = "NMIMS Global Access School for Continuing Education";

//          	logger.info("shareText: "+shareText);

           	int start =  shareText.indexOf(word); // prints "4"
//           	logger.info(" start: "+start);
           //Step 3 Create Image Share
	   		String image_share_request_json =  " { "
	   				+ "    \"author\": \"urn:li:person:"  + person_id + "\" , " 
	   				+   " \"lifecycleState\": \"PUBLISHED\", "
	   				+  "  \"specificContent\": { "
	   				+  "     \"com.linkedin.ugc.ShareContent\": { "
	   				
	   			//	+ " \"primaryLandingPageUrl\": \"" + link_path +"\" ,  " 

  +  "        \"shareCommentary\": { "
	+ " \"attributes\": [ " 
	       +          "   { "
	          +              "  \"length\":51 , "
	          +            "  \"start\": " + start + " , "
	            +          "  \"value\": { "
	              +            "  \"com.linkedin.common.CompanyAttributedEntity\": { "
	                 +            "   \"company\": \"urn:li:organization:" + LINKED_IN_ORGANIZATION_ID + "\" "
	                 +         "  } "
	                  +      " } "
	                +   " } "
	         +       " ], "
			   +  "           \"text\": "+"\""+ shareText+ "\" "
	       + "      }, "
	   				
	   			
	   				+ "     \"shareMediaCategory\": \"IMAGE\", "
	   				+ "    \"media\": [ "
	   				+ "       { "
	   				+ "          \"status\": \"READY\", "
	   				+ "         \"description\": { "	                        
	   				+ "  \"text\": \"Get detailed information about Certificate.\" "
	   				+  "     }, "
	   				+  "       \"media\": \"" + asset + "\", "
	   			//	+  "       \"originalUrl\": \"" + link_path + "\", "
	   				+    "      \"title\": { "
	   				+      "         \"text\": \"View Certificate\" "
	   				+        "    } "
	   				+        " } "
	   				+      " ] "
	   				+   " } "
	   				+ " }, "
	   				+ " \"visibility\": { "
	   				+   " \"com.linkedin.ugc.MemberNetworkVisibility\": \"PUBLIC\" "
	   				+ " } " 
	   				+		" } ";
//           
      

//	   		logger.info("image_share_request_json: "+image_share_request_json);
   		HttpEntity<String> entity4 = new HttpEntity<String>(image_share_request_json,headers);

   		ResponseEntity<LinkedInAddCertToProfileBean> response4  = 
   				restTemplate.postForEntity(LINKED_IN_SHARE_URL, entity4 ,LinkedInAddCertToProfileBean.class);

//     logger.info(response4.getBody().toString());
        

	
	}catch(Exception e)
	{		//restTemplate.
		//e.printStackTrace();
	}
	

	
	
}

	public void registerUploadPDF_V2(final String user_id, final String person_id, final String awsUploadURL, 
			final String access_token, final String share_text) {
		
		logger.info("registerUploadPDF_V2 ==> Register Upload Called");
		
		try {
			
	        String destinationDir = LINKED_IN_SHARE_IMAGES_PATH; // converted images from pdf document are saved here
	        String sourceDir = CERTIFICATES_SOURCE_IMAGE_PATH;
	        
	        File sourceDirectory= new File(sourceDir);
	        
	        if (!sourceDirectory.exists()) {
	        	sourceDirectory.mkdir();
	        }

	    	URL url = new URL(awsUploadURL);
            String fullPathOnmachine = LOCAL_CERTIFICATES_PATH + url.getPath();//Add folder and drive to get path on machine

	    	File localFile = new File(fullPathOnmachine);
            localFile.createNewFile();

			FileUtils.copyURLToFile(url, localFile);

	        File sourceFile = new File(fullPathOnmachine);
	        File destinationFile = new File(destinationDir);
	        
	        if (!destinationFile.exists()) {
	            destinationFile.mkdir();
	        }
	        
	        if (sourceFile.exists()) {
	        	            
	            PDDocument document = PDDocument.load(sourceFile);
	            PDFRenderer pdfRenderer = new PDFRenderer(document);
	
	            int numberOfPages = document.getNumberOfPages();
	
	            String fileName = sourceFile.getName().replace(".pdf", "");             
	            String fileExtension= "png";
	            /*
	             * 600 dpi give good image clarity but size of each image is 2x times of 300 dpi.
	             * Ex:  1. For 300dpi 04-Request-Headers_2.png expected size is 797 KB
	             *      2. For 600dpi 04-Request-Headers_2.png expected size is 2.42 MB
	             */
	            int dpi = 600;// use less dpi for to save more space in harddisk. For professional usage you can use more than 300dpi 
	            File outPutFile = null;
	            
	            for (int i = 0; i < numberOfPages; ++i) {
	                outPutFile = new File(destinationDir + fileName +"_"+ (i+1) +"."+ fileExtension);
	                BufferedImage bImage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
	                ImageIO.write(bImage, fileExtension, outPutFile);
	            }
	
	            document.close();
	            registerUploadImage(user_id, person_id, outPutFile.getAbsolutePath(),access_token, share_text);
	            
	        } else {
	            System.err.println(sourceFile.getName() +" File not exists");
	        }
	        
	        try {
	        	sourceFile.delete();
	        }catch (Exception e) {
				// TODO: handle exception
	        	e.printStackTrace();
			}
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
