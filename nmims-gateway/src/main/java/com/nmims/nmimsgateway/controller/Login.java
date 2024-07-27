package com.nmims.nmimsgateway.controller;

import java.security.Principal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;


@Controller
public class Login {

	

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@GetMapping("/")
	public String login() {
		//request.getSession().invalidate();
		//request.setAttribute("SERVER_PATH", SERVER_PATH);

		//PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		//if("PROD".equalsIgnoreCase(ENVIRONMENT)) {
			//industryList = pDao.getIndustryList();
		//}
		//String sessionExpired = request.getParameter("sessionExpired");
		//if("true".equals(sessionExpired)){
			//setError(request, "Session Expired, Please login again.");
	//	}

		return "login";
	}
	
	@GetMapping("/logout")
	public String logout(ServerWebExchange exchange) {
		exchange.getSession().subscribe( session -> {
			session.invalidate();
		});
		return "login";
	}
	
	@GetMapping("/forgotPasswordForm")
	public String forgotPasswordForm() {
		return "forgotPassword";
	}
	
	@GetMapping("/loginForLeadsForm")
	public String loginForLeadsForm() {
		return "loginForLeads";
	}
	
	@GetMapping("/registerForLeadsForm")
	public String registerForLeadsForm() {
		return "registerForLeads";
	}
	
	@GetMapping("/principal")
	public String principal(Principal principal) {
		return "Hello " + principal.getName();
	}

	@GetMapping("/principal/mono")
	public Mono<String> principalMono(Mono<Principal> principal) {
		return principal.map( p -> "Hello " + p.getName());
	}

	@GetMapping("/userdetails")
	public String principal(@AuthenticationPrincipal UserDetails userDetails) {
		return "Hello " + userDetails.getUsername();
	}

	@GetMapping("/userdetails/mono")
	public Mono<String> userDetailsMono(@AuthenticationPrincipal Mono<UserDetails> userDetails) {
		return userDetails.map( p -> "Hello " + p.getUsername());
	}

	@GetMapping("/exchange/principal")
	public Mono<String> exchangePrincipal(ServerWebExchange exchange) {
		return exchange.getPrincipal().map( p -> "Hello " + p.getName());
	}
}
