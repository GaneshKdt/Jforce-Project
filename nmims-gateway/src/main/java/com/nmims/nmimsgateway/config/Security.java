package com.nmims.nmimsgateway.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.SwitchUserWebFilter;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.server.WebSession;

import com.nmims.nmimsgateway.security.LoginSuccessHandler;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.approval.ApprovalStore;
//import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
//import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
//import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import reactor.core.publisher.Mono;

//import com.nmims.nmimsgateway.security.LoginSuccessHandler;


@Configuration
@EnableWebFluxSecurity
public class Security {
	private static final String[] ALL_ADMIN_ROLES = { 	"Portal Admin","Faculty","Exam Admin","TEE Admin","Marksheet Admin","Assignment Admin",
														"Read Admin","Acads Admin","Finance","SR Admin","Learning Center","Information Center",
														"Corporate Center","Student Support","SAS Team","Consultant","MBA-WX Admin",
														"Career Services Admin","Diageo Corporate Center","Watch Videos Access"		};
//	@Autowired
//	LoginSuccessHandler loginSuccessHandler;
	
	
//    @Bean
//    public MapReactiveUserDetailsService userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//            .username("user")
//            .password("user")
//            .roles("USER")
//            .build();
//        return new MapReactiveUserDetailsService(user);
//    }
	@Autowired
	ReactiveUserDetailsService reactiveUserDetailsServiceLdapImp;
   
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.redirectToHttps().and().csrf().disable()
        	.headers().frameOptions().disable()
        	.and()   
        	.authorizeExchange().pathMatchers("/","/forgotPasswordForm","/loginForLeadsForm","/registerForLeadsForm","/authLeadUser","/css/**","/js/**",
        			"/studentportal/credentials/**" ,"/studentportal/m/**","/acads/m/**","/exam/m/**",
        			"/timeline/m/*/**","/ltidemo/m/**","/ltidemo/*/**","/ltidemo/**","/careerservices/m/**","/coursera/m/*/**","/studentportal/oauth/token",
        			"/exam/api/*","/timeline/api/results/*","/timeline/api/flag/*","/timeline/api/post/*","/timeline/api/**",
        			"/acads/api/*","/ssoservices/**","/ssoservices/*/**","/salesforce/*/**","/salesforce/**",
        			"/studentportal/loginForLeads","/studentportal/viewStudentDetailsDashBoard",
        			"/studentportal/getPaymentOptions","/studentportal/CheckServiceRequestCount","/studentportal/getAddressDetailsFromPinCode",
        			"/studentportal/requestOTP","/studentportal/getLoginDetailsForLeads","/studentportal/checkIfLeadPresentForEmailAndMobile",
        			"/studentportal/verifyRequestOTP","/studentportal/uploadStudentPhoto","/studentportal/rank","/studentportal/rankBySubject",
        			"/studentportal/checkCanUploadStudentPhoto","/studentportal/getFacultyDetails","/studentportal/getFaqCategoryList",
        			"/studentportal/getSubFaqCategoryList","/studentportal/uploadStudentFiles","/studentportal/setPerspectiveForLeads","/studentportal/student/getFreeCoursesList","/studentportal/public/**", 
        			"/exam/viewTestDetailsForStudentsForAllViewsForLeads","/exam/embaInitiateExamBooking","/exam/generateMarksheetPreviewFromSR",
        			"/exam/viewTestDetailsForStudentsForAllViews","/exam/assignmentGuidelinesForAllViews","/exam/startStudentTestForAllViews","/exam/saveReasonForLostFocus", 
        			"exam/mbaExamBookingCallback","/studentportal/loginAsForm","/studentportal/loginAsNew",
        			"/StudentDocuments/**","/content/**","/submissions/**","/awsfileupload/**","/AEPDocuments/**","/StudentProfileDocuments/**",
        			"/careerservices/csStartupChecks","/careerservices/sessionQueryList","/careerservices/getAllWebinars","/careerservices/getLearningPortalLink",
        			"/careerservices/getStudentDashboardInfo","/careerservices/getUpcomingEventsSchedule","/careerservices/getPendingFeedback","/careerservices/getStudentPackageApplicability",
        			"/careerservices/addAttendanceForPreviousSession","/careerservices/getCareerForumNotViewedEvents","/careerservices/startCheckout","/careerservices/getCareerForumViewedEvents",
        			"/careerservices/getUpcomingCareerForumSchedule","/careerservices/getNumberOfFeedbackPending","/careerservices/attendScheduledSession","/careerservices/postQuery",
        			"/careerservices/getCareerForumActivationInfo","/Marksheets/**", "/metadata/**","/Marksheets/**","/submissions/**","/studentportal/resources_2015/**","/exam/resources_2015/**","/exam/downloadStudentAssignmentFile",
        			"/studentportal/student/adhocPaymentForm","/studentportal/student/saveAdhocPaymentRequest","/studentportal/student/savePendingAmountRequest","/studentportal/student/pendingPaymentFeeResponse","/studentportal/student/adhocFeeResponse",
        			"/acads/upsertSessionPlanModule", "/exam/enterAssessmentScores","/careerservices/logoutforSSO",
        			"/studentportal/logout","/exam/logoutforSSO","/acads/logoutforSSO","/ltidemo/logoutforSSO",
        			"/acads/student/getPostSessionFeedback", "/acads/student/savePostFeedback","/Faculty/**","/salesforcefiles/**","/studentportal/student/payForAdHocPayment","/careerservices/resources_2015/images/**","/acads/resources_2015/images/**","/studentportal/assets/**","/studentportal/saveUserProfileImage","/studentportal/profileImageVerify",
        			"/exam/admin/submitAssignmentLiveDataForAStudent", "/exam/studentsTestPreviewForAllViews", "/exam/mettlStatusDashboard", "/acads/mbax/**" , "/exam/mbax/**",
        			"/paymentgateways/student/getPaymentOptions",
        			"/paymentgateways/student/selectGatewayStageTransaction",
        			"/paymentgateways/student/processTransaction",
        			"/paymentgateways/student/responseTransaction","/paymentgateways/m/getTransactionStatus",
        			"/coursera/coursera_sso", "/coursera/coursera_sso_idp_metadata",
        			"/chats/chatGroupCreation","/chatbotapi/m/**").permitAll()


            .pathMatchers("/studentportal/student/**").hasAuthority("Student")
            .pathMatchers("/studentportal/admin/**").hasAnyAuthority(ALL_ADMIN_ROLES)
            .pathMatchers("/acads/student/**").hasAuthority("Student")
            .pathMatchers("/acads/admin/**").hasAnyAuthority(ALL_ADMIN_ROLES)
            .pathMatchers("/exam/student/**").hasAuthority("Student")
            .pathMatchers("/exam/admin/**").hasAnyAuthority(ALL_ADMIN_ROLES)
            .pathMatchers("/impersonate").hasAnyAuthority("Portal Admin","Faculty","Exam Admin","TEE Admin","Marksheet Admin","Assignment Admin",
														  "Acads Admin","Finance","SR Admin","Student Support")
            .anyExchange().authenticated()
                .and()
            .formLogin()
           .loginPage("/")
            
            

            .authenticationSuccessHandler(new LoginSuccessHandler("/studentportal/authenticate"))
        	
            .and() 
        	.logout();
		/*.logoutSuccessHandler(new ServerLogoutSuccessHandler() {
			@Override
			public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
				
				//Get response object from WebFilterExchange
				ServerHttpResponse response = exchange.getExchange().getResponse();
		
				//Set HttpStatus as Found.
				response.setStatusCode(HttpStatus.FOUND);
				
				//set logout url
				response.getHeaders().setLocation(URI.create("/logout"));
				
				//remove cookies
				response.getCookies().remove("SESSION");
				
				//invalidate session
				return exchange.getExchange().getSession()
						.flatMap(WebSession::invalidate);
			}
		});*/
        
        return http.build();
    }
    
    @Bean
    ReactiveAuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {

        BindAuthenticator ba = new BindAuthenticator(contextSource);
        ba.setUserDnPatterns(new String[] {  "CN={0}"   } );

        LdapAuthenticationProvider lap = new LdapAuthenticationProvider(ba,
        		
        	new LdapAuthoritiesPopulator() {
			
			@Override
			public List<GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
				
				List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
				if(userData.getStringAttribute("businessRoles") != null) {
					String roles = userData.getStringAttribute("businessRoles");
					
					String[] rolesList = roles.split(",");
					for (String role : rolesList) {
						if(role != null && role.length() > 0) {
					        authorities.add(new SimpleGrantedAuthority(role));	
						}
					}
				} else {
			        authorities.add(new SimpleGrantedAuthority("Student"));
				}
				return authorities;
			}
		}
    			

        		
        		
        		);
	



        AuthenticationManager am = new ProviderManager(Arrays.asList(lap));

        return new ReactiveAuthenticationManagerAdapter(am);

    }
    
    
    
    @Bean(name= "contextSource")
    public BaseLdapPathContextSource contextSource() {
     //   LOGGER.info("LDAP: {}", url);
        LdapContextSource bean = new LdapContextSource();
        bean.setUrl("ldaps://192.168.2.52:636/");
        bean.setBase("OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute,DC=SVKMGRP,DC=com");
        bean.setUserDn("CN=NGASCEAdmin,OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute,DC=SVKMGRP,DC=COM");
        bean.setPassword("distance@nmims1");
        bean.setReferral("follow");
        return bean;
    }
    
    
   
@Bean
public SwitchUserWebFilter  switchUserFilter() {
	
	
	LdapUserSearch userSearch = new FilterBasedLdapUserSearch("","(CN={0})",contextSource());
	
	
	
	
	
	
	//ReactiveUserDetailsService reactiveUserDetailsServiceLdapImp = new ReactiveUserDetailsServiceLdapImp();
	

	
	
	
		SwitchUserWebFilter filter = new SwitchUserWebFilter(reactiveUserDetailsServiceLdapImp,new LoginSuccessHandler("/studentportal/loginAs"),null);


		filter.setSwitchUserUrl("/impersonate");
		
		filter.setExitUserUrl("/authenticate");
		return filter;

	
	
}
    
}