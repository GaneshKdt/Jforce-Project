package com.nmims.nmimsgateway.security;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

//import com.nmims.beans.CaptchaResponse;
//import com.nmims.daos.LDAPDao;


public class LoginSuccessHandler extends RedirectServerAuthenticationSuccessHandler{


	
//	@Value("${WEB_RECAPTCHA_SECRET_KEY}")
//	private String WEB_RECAPTCHA_SECRET_KEY;
//	
//	@Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//    		//FilterChain chain,
//            Authentication authentication) throws IOException, ServletException {
//    	System.out.println("IN onAuthenticationSuccess() : ");
//    	//boolean isCaptchaValidate = isValidCaptcha(request, response, request.getParameter("g-recaptcha-response"));
//    	
//    	//if (isCaptchaValidate) {
//    		getRedirectStrategy().sendRedirect(request, response, "/studentportal/authenticate");
//		//}else {
//			//getRedirectStrategy().sendRedirect(request, response, "/login");
//		//}
//    		
//        
//    
//    }

	private URI location = URI.create("/studentportal/authenticate");

	private ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

	private ServerRequestCache requestCache = new WebSessionServerRequestCache();

	/**
	 * Creates a new instance with location of "/"
	 */
	public LoginSuccessHandler() {
	}

	/**
	 * Creates a new instance with the specified location
	 * @param location the location to redirect if the no request is cached in
	 * {@link #setRequestCache(ServerRequestCache)}
	 */
	public LoginSuccessHandler(String location) {
		this.location = URI.create(location);
	}

	/**
	 * Sets the {@link ServerRequestCache} used to redirect to. Default is
	 * {@link WebSessionServerRequestCache}.
	 * @param requestCache the cache to use
	 */
	public void setRequestCache(ServerRequestCache requestCache) {
		Assert.notNull(requestCache, "requestCache cannot be null");
		this.requestCache = requestCache;
	}

	@Override
	public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
		ServerWebExchange exchange = webFilterExchange.getExchange();
		
		Object principal = authentication.getPrincipal();

		 String username = "";
		if (principal instanceof UserDetails) {
			  username = ((UserDetails)principal).getUsername();
			} else {
			   username = principal.toString();
			}
	//	exchange.getSession().subscribe(session->session.getAttributes().put("userId",username));
        exchange.getSession().subscribe(session->session.getAttributes().put("SESSION",session.getId()));
       
       ResponseCookie cookie = ResponseCookie.from("username", username)
              // .maxAge(!isEmpty(cookieUserId) ? MAX_COOKIE_DURATION : 0)
             //  .domain("localhost")
               .sameSite("None")
               .secure(true)
               .path("/")
               .build();
       
//       ResponseCookie cookie2 = ResponseCookie.from("JSEESIONID", "12049905-aa56-48d8-a0b1-8f9fac31dc4f")
//               // .maxAge(!isEmpty(cookieUserId) ? MAX_COOKIE_DURATION : 0)
//                .domain("localhost")
//                .sameSite("None")
//                .secure(true)
//                .path("/studentportal")
//                .build(); 
       
       exchange.getResponse().addCookie(cookie);
    //   exchange.getResponse().addCookie(cookie2);

       //.getCookies().add("username",
    		 //   cookie );
   // response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        // adds header to proxied request
      //  String username = "username";
        //exchange.getSession().getAttributes().put(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, username);
        exchange.getRequest().mutate().header("X-Auth-Username", new String[] { username });
         //  .header("cookie", session);
      //  webFilterExchange.getExchange().
		return this.requestCache.getRedirectUri(exchange).defaultIfEmpty(this.location)
				.flatMap((location) -> this.redirectStrategy.sendRedirect(exchange, this.location));
	}
	
	/**
	 * Where the user is redirected to upon authentication success
	 * @param location the location to redirect to. The default is "/"
	 */
	public void setLocation(URI location) {
		Assert.notNull(location, "location cannot be null");
		this.location = location;
	}

	/**
	 * The RedirectStrategy to use.
	 * @param redirectStrategy the strategy to use. Default is DefaultRedirectStrategy.
	 */
	public void setRedirectStrategy(ServerRedirectStrategy redirectStrategy) {
		Assert.notNull(redirectStrategy, "redirectStrategy cannot be null");
		this.redirectStrategy = redirectStrategy;
	}
	
//	public boolean isValidCaptcha(HttpServletRequest request, HttpServletResponse response, String captcha) {
//		String url= "https://www.google.com/recaptcha/api/siteverify";
//		String params = "?secret=" + WEB_RECAPTCHA_SECRET_KEY + "&response=" + captcha;
//		String completeUrl = url + params;
//		
//		System.out.println("completeUrl "+completeUrl);
//		RestTemplate restTemplate = new RestTemplate();
//		CaptchaResponse captchaResponse = restTemplate.postForObject(completeUrl, null, CaptchaResponse.class);
//		System.out.println("response "+response);
//		System.out.println("response.isSuccess() "+captchaResponse.isSuccess());
//		return captchaResponse.isSuccess();
//	}
	
	/**
	 * Method containing post authentication activites for the Authenticated Lead User
	 * @param exchange - ServerWebExchange containing  HTTP request and response
	 * @param authentication - Authentication Object containing details of the authenticated lead user
	 * @return - user is redirected to the specified URL
	 */
	public Mono<Void> onLeadAuthenticationSuccess(ServerWebExchange exchange, Authentication authentication) {
		Object principal = authentication.getPrincipal();
		String username = (principal instanceof UserDetails) ? ((UserDetails)principal).getUsername() : principal.toString();
		
		exchange.getSession()
				.subscribe(session-> session.getAttributes()
											.put("SESSION", session.getId()));
		
		ResponseCookie cookie = ResponseCookie.from("username", username)
											  .sameSite("None")
											  .secure(true)
											  .path("/")
											  .build();
		   
		exchange.getResponse().addCookie(cookie);
		
		exchange.getRequest().mutate()
							 .header("X-Auth-Username", new String[] { username });
		 
		return this.requestCache.getRedirectUri(exchange)
				   				.defaultIfEmpty(this.location)
				   				.flatMap((location) -> this.redirectStrategy.sendRedirect(exchange, this.location));
	}
}
