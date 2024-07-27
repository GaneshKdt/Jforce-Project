 	 	 package com.nmims.nmimsgateway.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import reactor.core.publisher.Mono;

@Service
public class ReactiveUserDetailsServiceLdapImp implements ReactiveUserDetailsService  {

//	
@Autowired
@Qualifier("contextSource")
BaseLdapPathContextSource    contextSource;
    
//@Bean(name= "ldapContextSource")
//public BaseLdapPathContextSource ldapContextSource() {
//   
//    LdapContextSource bean = new LdapContextSource();
//   bean.setUrl("ldaps:192.168.2.51:636/");
//    bean.setBase("OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute,DC=SVKMGRP,DC=com");
//   bean.setUserDn("CN=NGASCEAdmin,OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute,DC=SVKMGRP,DC=COM");
//   bean.setPassword("distance@nmims1");
//   bean.setReferral("follow");
//  //bean.afterPropertiesSet();
//
//    return bean;
//}

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		// TODO Auto-generated method stub
		  BindAuthenticator ba = new BindAuthenticator(contextSource);
	        ba.setUserDnPatterns(new String[] {  "CN={0}"   } );
		LdapUserSearch userSearch = new FilterBasedLdapUserSearch("","(CN={0})", contextSource);
		
		 	
		
		
		LdapUserDetailsService ldapUserDetailsService = new LdapUserDetailsService(userSearch,
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
		//filter.setUserDetailsService(ldapUserDetailsService);
	
		System.out.println("==>username");

		System.out.println(username);
		System.out.println(ldapUserDetailsService.loadUserByUsername(username).getUsername());
		MapReactiveUserDetailsService b = new MapReactiveUserDetailsService(ldapUserDetailsService.loadUserByUsername(username));
		Mono<UserDetails> data =b.findByUsername(username);
		
		return data;
	
	
	}

}
