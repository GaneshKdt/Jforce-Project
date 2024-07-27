package com.nmims.awsfileupload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import com.amazonaws.services.s3.model.ListObjectsV2Result;

@Component
public class AWSFileUploadHelper {
	
	@Value( "${AWS_ACCESS_KEY}" )
	private String AWS_ACCESS_KEY;
	
	@Value( "${AWS_SECRET_KEY}" )
	private String AWS_SECRET_KEY;
	 
	
	public String uploadIntoSessionAudioRecordingS3Bucket(String filePath,String keyName,String bucketName,String publicFlag) {
		Regions clientRegion = Regions.AP_SOUTH_1;
    	try {
    		AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .build();
            TransferManager tm = TransferManagerBuilder.standard()
                    .withS3Client(s3Client)
                    .build();
            

            // TransferManager processes all transfers asynchronously,
            // so this call returns immediately.
            Upload upload = null;
            if("true".equalsIgnoreCase(publicFlag)) {
            	upload = tm.upload(new PutObjectRequest(bucketName, keyName, new File(filePath)).withCannedAcl(CannedAccessControlList.PublicRead));
            }else {
            	upload = tm.upload(new PutObjectRequest(bucketName, keyName, new File(filePath)));
            }
            System.out.println("Object upload started");

            // Optionally, wait for the upload to finish before continuing.
            upload.waitForCompletion();
            System.out.println("Object upload complete");
            return "true";
            
        } catch (Exception e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
            return e.getMessage();
        }
	}
	
	
	//Create folder if not exists on s3 bucket
		public  String createFolderOns3(String bucketName, String folderName) {
			
			 try {
				 
			Regions clientRegion = Regions.AP_SOUTH_1;
			AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
	        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	                .withRegion(clientRegion)
	                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
	                .build();
	        
	       
			
			//Get the list of folder's File present in that bucket Name
	        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName, folderName);
			
			
			//If the count is 0, it means folder is not Present in that bucket.
			if(result.getKeyCount() == 0) {
			    
					// create meta-data for your folder and set content-length to 0
					ObjectMetadata metadata = new ObjectMetadata();
					metadata.setContentLength(0);
					
					// create empty content
					InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
					
					// create a PutObjectRequest passing the folder name suffixed by /
					PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
		                folderName, emptyContent, metadata);
					
					// send request to S3 to create folder
					s3Client.putObject(putObjectRequest);
				}
			
	        }catch(Exception e)
	        {
	        	 e.printStackTrace();
	        	 return "error";
	        }
	        return "true";
		}
		
		public String uploadMultipartFile(String keyName,String bucketName,MultipartFile file) {
			Regions clientRegion = Regions.AP_SOUTH_1;
	    	try {
	    		AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
	            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	                    .withRegion(clientRegion)
	                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
	                    .build();
	          
	            
	        	
	    		InputStream input =file.getInputStream();
	    		ObjectMetadata metadata=new ObjectMetadata();
	    		metadata.setContentLength(file.getSize());
	    		metadata.setContentType(file.getContentType());
	    		
	    		s3Client.putObject(bucketName,keyName,input,metadata);
	 		    
	    		s3Client.shutdown();
	 		    
	 		   return "true";
	            
	        } catch (Exception e) {
	            // The call was transmitted successfully, but Amazon S3 couldn't process 
	            // it, so it returned an error response.
	            e.printStackTrace();
	            return "error";
	        }
		}
	        
		
		public String uploadFilesWithoutPublicAccess(String filePath,String keyName,String bucketName)
		{
			try {
			
			AWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY,AWS_SECRET_KEY);
			AmazonS3 s3Client = new AmazonS3Client(credentials);
			
			
			TransferManager transfer = TransferManagerBuilder.standard().withS3Client(s3Client).build();
			
			
			Upload upload = transfer.upload(new PutObjectRequest(bucketName, keyName, new File(filePath)));
	        System.out.println("Object upload started");

	        // Optionally, wait for the upload to finish before continuing.
	        upload.waitForCompletion();
	        System.out.println("Object upload complete");
	        return "true";
			
			} catch (Exception e) {
	            // The call was transmitted successfully, but Amazon S3 couldn't process 
	            // it, so it returned an error response.
	            e.printStackTrace();
	            return "error";
	        }
			
			 
		}
	
		public String deleteObjectFromS3(String bucketName, String objectKey) { 
			try {
				final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();
				s3.deleteObject(bucketName, objectKey);
				return "true";
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return "error";
			}
		}
		
		

		public String filePresentOnS3(String bucket, String keyName) {
		    try {
		    	
		    	AWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY,AWS_SECRET_KEY);
				AmazonS3 s3Client = new AmazonS3Client(credentials);
		    	S3Object object = s3Client.getObject(bucket, keyName);
		    	
		        return "true";
		    } catch (Exception e) {
		    	return e.getMessage();
		    }
		}
		
		public String downloadFilesWithPublicAccess(String filePath,String keyName,String bucketName)
		{
			try
			{
				AWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY,AWS_SECRET_KEY);
				AmazonS3 s3Client = new AmazonS3Client(credentials);
				
				FileOutputStream fos = new FileOutputStream(new File(filePath));
				S3Object s3object = s3Client.getObject(bucketName, keyName);
				S3ObjectInputStream inputStream = s3object.getObjectContent();
				byte[] read_buf = new byte[1024];
			    int read_len = 0;
			    while ((read_len = inputStream.read(read_buf)) > 0) {
			        fos.write(read_buf, 0, read_len);
			    }
			    inputStream.close();
			    fos.close();
				return "true";
			}
			catch (Exception e) 
			{
			    //System.err.println(e.getMessage());
			    e.printStackTrace();
			    return "error";
			}
		}
}
