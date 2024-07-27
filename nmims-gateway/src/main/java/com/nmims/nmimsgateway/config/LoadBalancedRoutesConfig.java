package com.nmims.nmimsgateway.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;

@Profile("local-discovery")
@Configuration
public class LoadBalancedRoutesConfig {
	
	@Bean
	public RouteLocator localHostRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/studentportal*", "/studentportal/**")
			.uri("lb://studentportal-service"))
			.route(r -> r.path("/exam*", "/exam/**")
					.uri("lb://exam-service"))
			.route(r -> r.path("/acads*", "/acads/**")
					.uri("lb://acads-service"))
			.route(r -> r.path("/ltidemo*", "/ltidemo/**")
					.uri("lb://ltidemo-service"))
			.route(r -> r.path("/timeline*", "/timeline/**")
					.uri("lb://timeline-service"))
			.route(r -> r.path("/ssoservices*", "/ssoservices/**")
					.uri("lb://ssoservices-service"))
			.route(r -> r.path("/salesforce*", "/salesforce/**")
					.uri("lb://salesforce-service"))
			.route(r -> r.path("/paymentgateways*", "/paymentgateways/**")
					.uri("lb://paymentgateways-service"))
			.route(r -> r.path("/awsfileupload*", "/awsfileupload/**")
					.uri("lb://awsfileupload-service"))
			.route(r -> r.path("/careerservices*", "/careerservices/**")
					.uri("lb://careerservices-service"))
			.route(r -> r.path("/coursera*", "/coursera/**")
                    .uri("lb://coursera-service"))
			.route(r -> r.path("/chats*", "/chats/**")
					.uri("lb://chat-service"))
			.route(r -> r.path("/chatbotapi*", "/chatbotapi/**")
					.uri("lb://chatbotapi-service"))
			.build();
	}
	
//	@Value("${tomcat.ajp.port}")
//    int ajpPort;
//
//    @Value("${tomcat.ajp.remoteauthentication}")
//    String remoteAuthentication;
//
//    @Value("${tomcat.ajp.enabled}")
//    boolean tomcatAjpEnabled;
//
//    @Bean
//    public TomcatServletWebServerFactory servletContainer() {
//
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
//        if (tomcatAjpEnabled) {
//        	
//            try {
//            	System.out.println("try AJP");
//				Connector ajpConnector = new Connector("AJP/1.3");
//				ajpConnector.setPort(8888);
//				ajpConnector.setSecure(false);
//				ajpConnector.setAllowTrace(false);
//				ajpConnector.setScheme("http");
//				ajpConnector.setProperty("address","0.0.0.0");
//				ajpConnector.setProperty("allowedRequestAttributesPattern",".*");
//				((AbstractAjpProtocol) ajpConnector.getProtocolHandler()).setSecretRequired(false);
//				tomcat.addAdditionalTomcatConnectors(ajpConnector);
//            	System.out.println("Done");
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        }
//
//        return tomcat;
//    }
	
//	@Bean
//    public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
//            ConfigurableApplicationContext context) {
//        return ServiceInstanceListSupplier.builder()
//                    .withDiscoveryClient()
//                    .withRequestBasedStickySession()
//                    .build(context);
//        }
	
//	
//	static final String SESSION_COOKIE_NAME = "JSESSIONID";
//
//	@Bean(name = WebHttpHandlerBuilder.WEB_SESSION_MANAGER_BEAN_NAME)
//	WebSessionManager webSessionManager(WebFluxProperties webFluxProperties) {
//	    DefaultWebSessionManager webSessionManager = new DefaultWebSessionManager();
//	    CookieWebSessionIdResolver webSessionIdResolver = new CookieWebSessionIdResolver();
//	    webSessionIdResolver.setCookieName(SESSION_COOKIE_NAME);
//
//	    webSessionManager.setSessionIdResolver(webSessionIdResolver);
//	    return webSessionManager;
//	}
    }



