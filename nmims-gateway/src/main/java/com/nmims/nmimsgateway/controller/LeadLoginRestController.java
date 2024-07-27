package com.nmims.nmimsgateway.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.util.UriComponentsBuilder;

import com.nmims.nmimsgateway.security.LoginSuccessHandler;

import reactor.core.publisher.Mono;

@RestController
public class LeadLoginRestController {
	private static final String LEAD_USER_USERID = "77999999999";
	private static final String LEAD_USER_PASSWORD = "Roger@321";
	
	private final WebSessionServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();
	
	private final ReactiveAuthenticationManager reactiveAuthenticationManager;
	private final LoginSuccessHandler successHandler;
	
	@Autowired
	public LeadLoginRestController(ReactiveAuthenticationManager reactiveAuthenticationManager) {
		Objects.requireNonNull(reactiveAuthenticationManager);			//Fail-fast approach, field is guaranteed be non-null.
		this.reactiveAuthenticationManager = reactiveAuthenticationManager;
		successHandler = new LoginSuccessHandler(createLeadLoginSuccessUri("/studentportal/loginForLeads", "userId", LEAD_USER_USERID));
	}
	
	/**
	 * Method for creating a String URI with the passed path and query parameter
	 * @param path - path of the URI
	 * @param paramName	- name of the query parameter
	 * @param paramValue - value of the query parameter
	 * @return Created URI as String
	 */
	private String createLeadLoginSuccessUri(String path, String paramName, String paramValue) {
		return UriComponentsBuilder.newInstance()
								   .path(path)
							       .queryParam(paramName, paramValue)
							       .build()
							       .toUriString();
	}

	/**
	 * Test method to check if the Principal Object is successfully attached to the session of an Authenticated User
	 * @param principal - Principal Object to retrieve the userId of the user
	 * @return - Greeting with the UserName is returned if the API was hit by an Authenticated user, Anonymous if the user wasn't Authenticated successfully
	 */
    @PreAuthorize("hasAuthority('Student')")
    @GetMapping("/securedStudentCheck")
    public Mono<String> securedStudentCheck(Principal principal) {
        return Mono.just("Hello " + name(principal) + ", from a secured page");
    }

    /**
     * Retrieves name from the Principal Object if present and returns anonymous if not.
     * @param principal - Principal Object of the User
     * @return - name if present or anonymous if null
     */
    private String name(Principal principal) {
        return Optional.ofNullable(principal).map(Principal::getName).orElse("anonymous");
    }
    
    /**
     * A UsernamePasswordAuthenticationToken (Authentication Object) is created with the User Details passed in the method parameters. 
     * The token is then passed to the Authentication Manager for authentication.
     * If the user is authenticated successfully, the authenticated flag in UsernamePasswordAuthenticationToken is set to true.
     * The Authentication Token Object is then passed into the SecurityContext which is set into the WebSession of ServerWebExchange.
     * The Session ID of the WebSession is then changed as an identifier for the changes into the WebSession Object.
     * The Authentication Object is then returned and passed into the SuccessHandler to carry out the post authentication activities.
     * @param exchange - ServerWebExchange containing  HTTP request and response
     * @param userName - User ID of the user
     * @param rawPassword - Password of the user
     * @return - on authentication success the user is redirected
     */
    private Mono<Void> authenticateLeadUser(ServerWebExchange exchange, String userName, String rawPassword) {
        Authentication token = new UsernamePasswordAuthenticationToken(userName, rawPassword);			//Creating an Authentication Token Object with the passed attributes

        Mono<Authentication> authentication = reactiveAuthenticationManager.authenticate(token)			//Authentication token is authenticated
							        									   .filter(auth -> auth.isAuthenticated())		//Filtered on successful authentication
							        									   .flatMap(auth -> {
							        										   	//Authentication Token Object is passed into the SecurityContext Object
																				SecurityContextImpl securityContext = new SecurityContextImpl();
																	            securityContext.setAuthentication(auth);
																	            
																	            //SecurityContext is stored in WebSession and Session ID changed
																	            return securityContextRepository.save(exchange, securityContext)
																            									.then(Mono.just(auth));
																		   });

        //On successful authentication the post authentication activities are carried out
        return authentication.flatMap((auth) -> successHandler.onLeadAuthenticationSuccess(exchange, auth));
    }
    
    /**
     * Lead User is Authenticated and is redirected on successful authentication
     * @param exchange - ServerWebExchange containing  HTTP request and response
     * @return - Redirected to the /loginForLeads page on successful authentication
     */
    @PostMapping("/authLeadUser")
	public Mono<Void> authLeadUser(ServerWebExchange exchange) {
		try {
			return authenticateLeadUser(exchange, LEAD_USER_USERID, LEAD_USER_PASSWORD);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			//return instance of FailureHandler here
			return null;
		}
    }
    
    /**
     * This method is not used for Lead Authentication. authLeadUser() method used.
     * Here a UserDetails Object is created and Manually authenticated using the UsernamePasswordAuthenticationToken by passing the GrantedAuthorities.
     * The Authentication Token Object is then passed into the SecurityContext which is set into the WebSession of ServerWebExchange.
     * The Session ID of the WebSession is then changed as an identifier for the changes into the WebSession Object.
     * The Authentication Object is then returned and passed into the SuccessHandler to carry out the post authentication activities.
     * @param exchange - ServerWebExchange containing  HTTP request and response
     * @return - Success/Error Message
     */
    @GetMapping("/bypassLeadUser")
	public Mono<String> bypassLeadUser(ServerWebExchange exchange) {
		try {
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority("Student"));
			
			PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
			String bcryptEncodedPassword = encoder.encode(LEAD_USER_PASSWORD);

			//Creating UserDetails Object containg the Username, password encrypted and required authorities added
			UserDetails leadUser = User.withUsername(LEAD_USER_USERID)
							            .password(bcryptEncodedPassword)
							            .authorities(authorities)
							            .build();
			
			Mono<String> sessionAuthenticated = exchange.getSession()
										                .doOnNext(session -> {			//triggered when data is passed successfully
										                    SecurityContextImpl securityContext = new SecurityContextImpl();
										                    
										                    //Creating an UsernamePasswordAuthenticationToken, which has isAuthenticated() set to true by default
										        			Authentication authentication = new UsernamePasswordAuthenticationToken(leadUser.getUsername(), 
										        																					LEAD_USER_PASSWORD, 
										        																					leadUser.getAuthorities());
										                    securityContext.setAuthentication(authentication);		//Authentication Token Object is passed into the SecurityContext Object
										                    
										                    //SecurityContext is stored in WebSession
										                    session.getAttributes()
										                    	   .put(WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, securityContext);
										                })
										                .flatMap(WebSession::changeSessionId)		//Session ID is changed
										                .then(Mono.just("You are logged in. You can now Access the Website!"));		//Success Message is returned
			
			//Activites carried out after Successful Authentication
			exchange.getSession().subscribe(session -> session.getAttributes()
															  .put("SESSION", session.getId()));
			
		    ResponseCookie cookie = ResponseCookie.from("username", leadUser.getUsername())
									               .sameSite("None")
									               .secure(true)
									               .path("/")
									               .build();
		    exchange.getResponse().addCookie(cookie);
		    
		    exchange.getRequest()
		    		.mutate()
		    		.header("X-Auth-Username", new String[] { leadUser.getUsername() });
		    
			return sessionAuthenticated;
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			return Mono.just("Failed to log in. Please try again!");
		}
    }
}
