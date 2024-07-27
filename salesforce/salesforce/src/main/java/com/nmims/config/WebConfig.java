package com.nmims.config;

import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.XmlViewResolver;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.nmims.daos.AmazonS3Dao;
import com.nmims.daos.CallRepositoryDAO;
import com.nmims.daos.CaseDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.ProgramDao;
import com.nmims.daos.RevenueReportsDAO;
import com.nmims.daos.StudentCourseMappingDao;
import com.nmims.daos.StudentSessionCoursesDAO;
import com.nmims.daos.StudentZoneDao;
import com.nmims.daos.UserDao;
import com.nmims.helpers.FedExShipClient;
import com.nmims.helpers.MailSender;

@Configuration
@EnableWebMvc //Enable SpringMVC
@ComponentScan({"com.nmims"})
@PropertySource("file:c:/NMIMS_PROPERTY_FILE/ngasce.properties")
@PropertySource("file:${catalina.base}/conf/application.properties")
@EnableAspectJAutoProxy
public class WebConfig extends WebMvcConfigurerAdapter {

	@Value("${mysql.datasource.driver-class-name}")
	String MYSQL_DATASOURCE_CLASS;
	
	@Value("${mysql.datasource.url}")
	String MYSQL_DATASOURCE_URL;
	
	@Value("${mysql.datasource.username}")
	String MYSQL_DATASOURCE_USERNAME;
	
	@Value("${mysql.datasource.password}")
	String MYSQL_DATASOURCE_PASSWORD;
	
	//LDAP CREDS
	@Value("${LDAP_URL}")
	String LDAP_URL;
	
	@Value("${LDAP_BASE}")
	String LDAP_BASE;
	
	@Value("${LDAP_USER_DN}")
	String LDAP_USER_DN;

	@Value("${LDAP_PASSWORD}")
	String LDAP_PASSWORD;
	
	//SMTP NMIMS CREDS
	@Value("${SMTP_HOST}")
	String SMTP_HOST;
	
	@Value("${SMTP_PORT}")
	int SMTP_PORT;
	
	@Value("${SMTP_PORT_STRING}")
	String SMTP_PORT_STRING;

	@Value("${SMTP_USERNAME}")
	String SMTP_USERNAME;
	
	@Value("${SMTP_PASSWORD}")
	String SMTP_PASSWORD;

	@Value("${SMTP_FROM}")
	String SMTP_FROM;
	
	@Value("${FEDEX_TRACK_WS_URL}")
	String FEDEX_TRACK_WS_URL;
	
	@Value("${FEDEX_SHIP_WS_URL}")
	String FEDEX_SHIP_WS_URL;
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
    }

	@Bean
	public LdapContextSource contextSource() {
		LdapContextSource bean = new LdapContextSource();
		bean.setUrl(LDAP_URL);
		bean.setBase(LDAP_BASE);
		bean.setUserDn(LDAP_USER_DN);
		bean.setPassword(LDAP_PASSWORD);
		return bean;
	}
	
	@Bean
	public DriverManagerDataSource certDataSource() {
		DriverManagerDataSource bean = new DriverManagerDataSource();
		bean.setDriverClassName(MYSQL_DATASOURCE_CLASS);
		bean.setUrl(MYSQL_DATASOURCE_URL +"/jforce_certificate");
		bean.setUsername(MYSQL_DATASOURCE_USERNAME);
		bean.setPassword(MYSQL_DATASOURCE_PASSWORD);
		return bean;
	}

	@Bean(name="mailer")
	public MailSender mailer() {
		MailSender bean = new MailSender();
		bean.setHost(SMTP_HOST);
		bean.setPort(SMTP_PORT_STRING);
		bean.setUsername(SMTP_USERNAME);
		bean.setPassword(SMTP_PASSWORD);
		bean.setFrom(SMTP_FROM);
		return bean;
	}

	@Bean(name = "ldapTemplate")
	public LdapTemplate ldapTemplate(LdapContextSource contextSource) {
		LdapTemplate bean = new LdapTemplate(contextSource);
		return bean;
	}

	@Bean(name = "ldapdao")
	public LDAPDao ldapdao(LdapTemplate ldapTemplate) {
		LDAPDao bean = new LDAPDao();
		bean.setLdapTemplate(ldapTemplate);
		return bean;
	}

	@Bean(name = "callRepositoryDAO")
	public CallRepositoryDAO callRepositoryDAO(BasicDataSource dataSource) {
		CallRepositoryDAO dao = new CallRepositoryDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name = "caseDAO")
	public CaseDAO caseDAO(BasicDataSource dataSource) {
		CaseDAO dao = new CaseDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name = "amazonS3Dao")
	public AmazonS3Dao amazonS3Dao(BasicDataSource dataSource) {
		AmazonS3Dao dao = new AmazonS3Dao();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name = "programDao")
	public ProgramDao programDao(BasicDataSource dataSource) {
		ProgramDao dao = new ProgramDao();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name = "revenueReportsDAO")
	public RevenueReportsDAO revenueReportsDAO(BasicDataSource dataSource) {
		RevenueReportsDAO dao = new RevenueReportsDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name = "studentSessionCoursesDAO")
	public StudentSessionCoursesDAO studentSessionCoursesDAO(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
		StudentSessionCoursesDAO dao = new StudentSessionCoursesDAO();
		dao.setDataSource(dataSource);
		dao.setTransactionManager(transactionManager);
		return dao;
	}

	@Bean(name = "studentZoneDAO")
	public StudentZoneDao studentZoneDao(BasicDataSource dataSource) {
		StudentZoneDao dao = new StudentZoneDao();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name = "userDao")
	public UserDao userDao(BasicDataSource dataSource) {
		UserDao dao = new UserDao();
		dao.setDataSource(dataSource);
		return dao;
	}
	
	@Bean(name = "messageFactory")
	public SaajSoapMessageFactory messageFactory() {
		SaajSoapMessageFactory bean = new SaajSoapMessageFactory();
		return bean;
	}

	@Bean("marshaller")
public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller bean = new Jaxb2Marshaller();
		bean.setContextPath("com.nmims.webservice.fedex.ship");
		return bean;
	}
	
	@Bean("marshallerTrack")
	public Jaxb2Marshaller marshallerTrack() {
		Jaxb2Marshaller bean = new Jaxb2Marshaller();
		bean.setContextPath("com.nmims.webservice.fedex.track");
		return bean;
	}
	
	
	
	@Bean("fedexShipWsTemplate")
	public WebServiceTemplate fedexShipWsTemplate(Jaxb2Marshaller marshaller, Jaxb2Marshaller marshallerTrack,  SaajSoapMessageFactory messageFactory) {
		WebServiceTemplate fedexShipWsTemplate = new WebServiceTemplate();
		fedexShipWsTemplate.setDefaultUri(FEDEX_SHIP_WS_URL);
		fedexShipWsTemplate.setMarshaller(marshaller);
		fedexShipWsTemplate.setUnmarshaller(marshaller);
		HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
		messageSender.setReadTimeout(1200000);
		messageSender.setConnectionTimeout(1200000);
		fedexShipWsTemplate.setMessageSender(messageSender);
		return fedexShipWsTemplate;
	}
	
	
	
	@Bean("fedExShipClient")
	public FedExShipClient fedExShipClient(WebServiceTemplate fedexShipWsTemplate) {
		//fedexShipWsTemplate
		return null;
		
	}
	
	@Bean(name = "studentCourseMappingDao")
	public StudentCourseMappingDao studentCourseMappingDao(BasicDataSource dataSource) {
		StudentCourseMappingDao dao = new StudentCourseMappingDao();
		dao.setDataSource(dataSource);
		return dao;
	}
	
}