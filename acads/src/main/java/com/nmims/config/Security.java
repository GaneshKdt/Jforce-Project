//package com.nmims.config;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.ldap.core.DirContextOperations;
//import org.springframework.ldap.core.support.BaseLdapPathContextSource;
//import org.springframework.ldap.core.support.LdapContextSource;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
//import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
//import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
//import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
//import org.springframework.security.ldap.search.LdapUserSearch;
//import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
//import org.springframework.security.web.session.HttpSessionEventPublisher;
////import org.springframework.security.oauth2.provider.ClientDetailsService;
////import org.springframework.security.oauth2.provider.approval.ApprovalStore;
////import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
////import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
////import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
////import org.springframework.security.oauth2.provider.token.TokenStore;
////import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
//
//import com.nmims.helpers.PersonAttributesMapper;
//
//@Configuration
//@EnableWebSecurity
//public class Security extends WebSecurityConfigurerAdapter {
//	
////	 @Autowired
////	 private ClientDetailsService clientDetailsService;
//	
//	
//	
//
//	 
//	public Security() {
//		super();
//	}
//	
//	@Override
//	protected void configure(final HttpSecurity http) throws Exception {
//		
//		http
//        .csrf().disable()
//        .headers().frameOptions().sameOrigin()
//        .and()
//        .authorizeRequests()
//        .antMatchers("/resources/**","/resources_2015/**", "/assets/**" ).permitAll()
//        .antMatchers("/loginAsForm","/invalidSession*", "/sessionExpired*").permitAll()
//        .antMatchers("/logoutforSSO").permitAll()
//        .antMatchers("/emailTemplate").access("hasRole('Portal Admin')")
//        .antMatchers("/student/**").access("hasRole('Student')")
//        .antMatchers("/admin/**").access("hasRole('Portal Admin')")
//        .antMatchers("/m/*").permitAll()
//        .anyRequest().authenticated()
//        .and()
//        .formLogin()
////      .loginPage("/")
//        .loginProcessingUrl("/login")
//        .permitAll()
//        .usernameParameter("userId")
//        .passwordParameter("password") 
//        .defaultSuccessUrl("/loginforSSO")
//        .failureUrl("/login.html?error=true")
//        .and()
//        .logout().logoutUrl("logoutforSSO").deleteCookies("JSESSIONID")
//        .and()
//        .sessionManagement()
//        .sessionFixation().migrateSession()
//        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//        .invalidSessionUrl("/invalidSession.html")
//        .maximumSessions(1)
//        .expiredUrl("/sessionExpired.html");
//	
//	}
//	
//	@Bean
//	public HttpSessionEventPublisher httpSessionEventPublisher() {
//	    return new HttpSessionEventPublisher();
//	}
//	
// @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    	try {
//    		 auth
//             .ldapAuthentication().userSearchFilter("(CN={0})") 
//             .contextSource(ldapContextSource())
//             .ldapAuthoritiesPopulator(new LdapAuthoritiesPopulator() {
//				
//				@Override
//				public List<GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
//					
//					List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//					if(userData.getStringAttribute("businessRoles") != null) {
//						String roles = userData.getStringAttribute("businessRoles");
//						
//						String[] rolesList = roles.split(",");
//						for (String role : rolesList) {
//							if(role != null && role.length() > 0) {
//						        authorities.add(new SimpleGrantedAuthority(role));	
//							}
//						}
//					} else {
//				        authorities.add(new SimpleGrantedAuthority("Student"));
//					}
//					return authorities;
//				}
//			}); 	
//    	}catch(Exception e) {
//    		  
//    	}
//           
//    }
//
//	    @Bean
//	    public BaseLdapPathContextSource ldapContextSource() {
//	      //  LOGGER.info("LDAP: {}", url);
//	        LdapContextSource bean = new LdapContextSource();
//	        bean.setUrl("ldaps://192.168.2.51:636/");
//	        bean.setBase("OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute,DC=SVKMGRP,DC=com");
//	        bean.setUserDn("CN=NGASCEAdmin,OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute,DC=SVKMGRP,DC=COM");
//	        bean.setPassword("distance@nmims1");
//	        bean.setReferral("follow");
//	        return bean;
//	    }
//
//	    
////	    //OAUTH2 Security Config For Rest APIs
////	    
////	    @Bean
////	    @Autowired
////	    public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore){
////	        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
////	        handler.setTokenStore(tokenStore);
////	        handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
////	        handler.setClientDetailsService(clientDetailsService);
////	        return handler;
////	    } 
//	    
//	    @Bean
//	    public SwitchUserFilter switchUserFilter() {
//	    	SwitchUserFilter filter = new SwitchUserFilter();
//	    	 
//	    	LdapUserSearch userSearch = new FilterBasedLdapUserSearch("","(CN={0})",ldapContextSource());
//	    	
//	    	LdapUserDetailsService ldapUserDetailsService = new LdapUserDetailsService(userSearch,
//new LdapAuthoritiesPopulator() {
//				
//				@Override
//				public List<GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
//					
//					List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//					if(userData.getStringAttribute("businessRoles") != null) {
//						String roles = userData.getStringAttribute("businessRoles");
//						
//						String[] rolesList = roles.split(",");
//						for (String role : rolesList) {
//							if(role != null && role.length() > 0) {
//						        authorities.add(new SimpleGrantedAuthority(role));	
//							}
//						}
//					} else {
//				        authorities.add(new SimpleGrantedAuthority("Student"));
//					}
//					return authorities;
//				}
//			}
//	    			
//	    			
//	    			);
//	    	filter.setUserDetailsService(ldapUserDetailsService);
//	    	filter.setSwitchUserUrl("/impersonate");
//	    	filter.setSwitchFailureUrl("/switchUser");
//	    	filter.setTargetUrl("/loginAs");
//	    	filter.setExitUserUrl("/home");
//	    	return filter;
//	    	
//	    }
//
//}
