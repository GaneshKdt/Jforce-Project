package com.nmims.nmimsgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;



@SpringBootApplication


public class NmimsGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(NmimsGatewayApplication.class, args);
	}
	
//	/* Handles HTTP GET requests for /resources/** by efficiently serving 
//	up static resources in the ${webappRoot}/resources directory */	
//	@Override
//	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
//	    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
//	    registry.addResourceHandler("/resources_2015/**").addResourceLocations("/resources_2015/");
//	    registry.addResourceHandler("/assets/**").addResourceLocations("/assets/");
//	}
	 
//	@Bean
//	RouterFunction<ServerResponse> staticResourceRouter(){
//	    return RouterFunctions.resources("/studentportal/resources_2015/**", new ClassPathResource("resources_2015/"));
//	}
//	
//	@Bean
//	RouterFunction<ServerResponse> staticResourceRouterAssets(){
//	    return RouterFunctions.resources("/studentportal/assets/**", new ClassPathResource("assets/"));
//	}  

	
	@Bean
	RouterFunction submissions(){
		return RouterFunctions.resources("/submissions/**", new FileSystemResource("D:\\Submissions/"));
	}
	
	@Bean
	RouterFunction content(){
		return RouterFunctions.resources("/content/**", new FileSystemResource("D:\\Content/"));
	}
	
	@Bean
	RouterFunction assignments(){
		return RouterFunctions.resources("/assignments/**", new FileSystemResource("D:\\AssignmentFiles/"));
	}
	
	@Bean
	RouterFunction lr(){
		return RouterFunctions.resources("/lr/**", new FileSystemResource("D:\\LearningResources/"));
	}
	
	@Bean
	RouterFunction hallticket(){
		return RouterFunctions.resources("/hallticket/**", new FileSystemResource("D:\\HallTicket/"));
	}
	
	@Bean
	RouterFunction CaseStudyFiles(){
		return RouterFunctions.resources("/CaseStudyFiles/**", new FileSystemResource("D:\\CaseStudyFiles/"));
	}
	
	@Bean
	RouterFunction SubmittedCaseStudyFiles(){
		return RouterFunctions.resources("/SubmittedCaseStudyFiles/**", new FileSystemResource("D:\\SubmittedCaseStudyFiles/"));
	}
	
	@Bean
	RouterFunction salesforcefiles(){
		return RouterFunctions.resources("/salesforcefiles/**", new FileSystemResource("D:\\SalesforceFiles/"));
	}

	@Bean
	RouterFunction StudentDocuments(){
		return RouterFunctions.resources("/StudentDocuments/**", new FileSystemResource("D:\\StudentDocuments/"));
	}
	
	@Bean
	RouterFunction Faculty(){
		return RouterFunctions.resources("/Faculty/**", new FileSystemResource("D:\\Faculty/"));
	}
	
	@Bean
	RouterFunction feedfiles(){
		return RouterFunctions.resources("/feedfiles/**", new FileSystemResource("D:\\feedfiles/"));
	}
	
	@Bean
	RouterFunction StudentProfileDocuments(){
		return RouterFunctions.resources("/StudentProfileDocuments/**", new FileSystemResource("D:\\StudentProfileDocuments/"));
	}


	@Bean
	RouterFunction metadata(){
		return RouterFunctions.resources("/metadata/**", new FileSystemResource("D:\\MetaData/"));
	}
	
	@Bean
	RouterFunction Marksheets(){
		return RouterFunctions.resources("/Marksheets/**", new FileSystemResource("D:\\Marksheets/"));
	}
	

	@Bean
	RouterFunction IATestAssignmentFiles(){
		return RouterFunctions.resources("/IATestAssignmentFiles/**", new FileSystemResource("D:\\AssignmentFiles\\IATest/"));
	}
	
	@Bean
	RouterFunction TestQuestionImages(){
		return RouterFunctions.resources("/TestQuestionImages/**", new FileSystemResource("D:\\TEST_QUESTION_IMAGES/"));
	}
	
	@Bean
	RouterFunction MBAWXMarksheet(){
		return RouterFunctions.resources("/MBAWXMarksheet/**", new FileSystemResource("D:\\MBAWXMarksheet/"));
	}
	
	@Bean
	RouterFunction TestAssignmentQuestion(){
		return RouterFunctions.resources("/TestAssignmentQuestion/**", new FileSystemResource("D:\\TEST_QUESTION_ASSIGNMENT/"));
	}
	

	@Bean
	RouterFunction MassEmailAttachments(){
		return RouterFunctions.resources("/MassEmailAttachments/**", new FileSystemResource("D:\\MassEmailAttachments/"));
	}
	
	@Bean
	RouterFunction FeeReceipts(){
		return RouterFunctions.resources("/FeeReceipts/**", new FileSystemResource("D:\\FeeReceipts/"));
	}

	@Bean
	RouterFunction fullbackup(){
		return RouterFunctions.resources("/fullbackup/**", new FileSystemResource("D:\\MySQLBackups\\backupfiles/"));
	}
	
	@Bean
	RouterFunction editorFiles(){
		return RouterFunctions.resources("/editorFiles/**", new FileSystemResource("D:\\CKEDITOR\\UPLOADEDFILES/"));
	}
	
	@Bean
	RouterFunction Certificates(){
		return RouterFunctions.resources("/Certificates/**", new FileSystemResource("D:\\Certificates/"));
	}

	@Bean
	RouterFunction logs(){
		return RouterFunctions.resources("/logs/**", new FileSystemResource("D:\\Java\\Apache24\\logs/"));
	}
	
	@Bean
	RouterFunction ufm(){
		return RouterFunctions.resources("/ufm/**", new FileSystemResource("D:\\UFM/"));
	}

	@Bean
	RouterFunction videoTranscript(){
		return RouterFunctions.resources("/videoTranscript/**", new FileSystemResource("D:\\videoTranscript/"));
	}
	
	@Bean
	RouterFunction AEPDocuments(){
		return RouterFunctions.resources("/AEPDocuments/**", new FileSystemResource("D:\\AEPDocuments/"));
	}
	
	@Bean
	RouterFunction openbadges(){
		return RouterFunctions.resources("/openbadges/**", new FileSystemResource("D:\\OpenBadgesImages/"));
	}
	
	@Bean
	RouterFunction UFM(){
		return RouterFunctions.resources("/UFM/**", new FileSystemResource("D:\\UFM/"));
	}
	@Bean
	RouterFunction LinkedInImages(){
		return RouterFunctions.resources("/LinkedInImages/**", new FileSystemResource("D:\\LinkedInImages/"));
	}
	
}
