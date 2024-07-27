//package com.nmims.security;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.approval.ApprovalStore;
//import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
//import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
//import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
//import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
//import org.springframework.ldap.core.DirContextOperations;
//import org.springframework.ldap.core.support.BaseLdapPathContextSource;
//import org.springframework.ldap.core.support.LdapContextSource;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.GrantedAuthority;
//
//@Configuration
//@EnableWebSecurity
//@Order(1000)
//public class OAuth2SecurityConfiguration  extends WebSecurityConfigurerAdapter {
//
//	 @Autowired
//	 private ClientDetailsService clientDetailsService;
//	 
////	 @Autowired
////	    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
////	        auth.inMemoryAuthentication()
////	        .withUser("bill").password("abc123").roles("ADMIN").and()
////	        .withUser("bob").password("abc123").roles("USER");
////	    }
////	 
//	 
//		
////	 @Override
////	    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////	    	try {
////	    		 auth
////	             .ldapAuthentication().userSearchFilter("(CN={0})") 
////	             .contextSource(ldapContextSource())
////	             .ldapAuthoritiesPopulator(new LdapAuthoritiesPopulator() {
////					
////					@Override
////					public List<GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
////						
////						List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
////						if(userData.getStringAttribute("businessRoles") != null) {
////							String roles = userData.getStringAttribute("businessRoles");
////							
////							String[] rolesList = roles.split(",");
////							for (String role : rolesList) {
////								if(role != null && role.length() > 0) {
////							        authorities.add(new SimpleGrantedAuthority(role));	
////								}
////							}
////						} else {
////					        authorities.add(new SimpleGrantedAuthority("Student"));
////						}
////						return authorities;
////					}
////				}); 	
////	    	}catch(Exception e) {
////	    		e.printStackTrace();
////	    	}
////	           
////	    }
//
////		    @Bean
////		    public BaseLdapPathContextSource ldapContextSource() {
////		      //  LOGGER.info("LDAP: {}", url);
////		        LdapContextSource bean = new LdapContextSource();
////		        bean.setUrl("ldaps://192.168.2.51:636/");
////		        bean.setBase("OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute,DC=SVKMGRP,DC=com");
////		        bean.setUserDn("CN=NGASCEAdmin,OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute,DC=SVKMGRP,DC=COM");
////		        bean.setPassword("distance@nmims1");
////		        bean.setReferral("follow");
////		        return bean;
////		    }
//
//		    
//	 
////	 @Override
////	    protected void configure(HttpSecurity http) throws Exception {
////	        http
////	        .csrf().disable()
////	        .anonymous().disable()
////	        .authorizeRequests()
////	        .antMatchers("/studentportal/oauth/token").permitAll();
////	    }
////	 
//	 @Override
//	    @Bean
//	    public AuthenticationManager authenticationManagerBean() throws Exception {
//	        return super.authenticationManagerBean();
//	    }
//	 
//	 
//	 @Bean
//	    public TokenStore tokenStore() {
//	        return new InMemoryTokenStore();
//	    }
//	 
//	 @Bean
//	    @Autowired
//	    public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore){
//	        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
//	        handler.setTokenStore(tokenStore);
//	        handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
//	        handler.setClientDetailsService(clientDetailsService);
//	        return handler;
//	    }
//	 
//	 	@Bean
//	    @Autowired
//	    public ApprovalStore approvalStore(TokenStore tokenStore) throws Exception {
//	        TokenApprovalStore store = new TokenApprovalStore();
//	        store.setTokenStore(tokenStore);
//	        return store;
//	    }
//	 
//	 	
//	 	
//	 	
//}
