package com.nmims.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.XmlViewResolver;

import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.ConsumerProgramStructureDAO;
import com.nmims.daos.CounsellingDAO;
import com.nmims.daos.EntitlementActivationDAO;
import com.nmims.daos.EntitlementManagementDAO;
import com.nmims.daos.EntitlementCheckerDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.InterviewDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.LoginDAO;
import com.nmims.daos.NotificationDAO;
import com.nmims.daos.PackageAdminDAO;
import com.nmims.daos.PackageApplicabilityDAO;
import com.nmims.daos.PaymentManagerDAO;
import com.nmims.daos.ProgressDetailsDAO;
import com.nmims.daos.SessionAttendanceDao;
import com.nmims.daos.SessionFeedbackDAO;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.SessionSchedulerDao;
import com.nmims.daos.SessionsDAO;
import com.nmims.daos.StudentDataManagementDAO;
import com.nmims.daos.VideoRecordingDao;
import com.nmims.daos.WebinarSchedulerDAO;
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
	
	@Value("${LDAP_URL}")
	String LDAP_URL;
	
	@Value("${LDAP_BASE}")
	String LDAP_BASE;
	
	@Value("${LDAP_USER_DN}")
	String LDAP_USER_DN;

	@Value("${LDAP_PASSWORD}")
	String LDAP_PASSWORD;
	
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
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
    }
	
	@Bean(name="filterMultipartResolver")
	public CommonsMultipartResolver filterMultipartResolver() {
		CommonsMultipartResolver bean = new CommonsMultipartResolver();
		bean.setMaxUploadSize(10000000);
		return bean;
	}
	
	@Bean(name="multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver bean = new CommonsMultipartResolver();
		bean.setMaxUploadSize(10000000);
		return bean;
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
	
	/* Handles HTTP GET requests for /resources/** by efficiently serving 
	up static resources in the ${webappRoot}/resources directory */	
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	    registry.addResourceHandler("/resources_2015/**").addResourceLocations("/resources_2015/");
	    registry.addResourceHandler("/assets/**").addResourceLocations("/assets/");
	}
	
	@Bean("internalResourceViewResolver")
	public ViewResolver internalResourceViewResolver() {
	    InternalResourceViewResolver bean = new InternalResourceViewResolver();
	    bean.setViewClass(JstlView.class);
	    bean.setPrefix("/views/");
	    bean.setSuffix(".jsp");
	    bean.setOrder(2);
	    return bean;
	}

	@Bean(name = "ldapdao")
	public LDAPDao ldapdao(LdapTemplate ldapTemplate) {
		LDAPDao dao = new LDAPDao();
		dao.setLdapTemplate(ldapTemplate);
		return dao;
	}

	@Bean(name="careerServicesDAO")
	public CareerServicesDAO careerServicesDAO(BasicDataSource dataSource) {
		CareerServicesDAO dao = new CareerServicesDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="consumerProgramStructureDAO")
	public ConsumerProgramStructureDAO ConsumerProgramStructureDAO(BasicDataSource dataSource) {
		ConsumerProgramStructureDAO dao = new ConsumerProgramStructureDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="counsellingDAO")
	public CounsellingDAO counsellingDAO(BasicDataSource dataSource) {
		CounsellingDAO dao = new CounsellingDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="entitlementActivationDAO")
	public EntitlementActivationDAO entitlementActivationDAO(BasicDataSource dataSource) {
		EntitlementActivationDAO dao = new EntitlementActivationDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="facultyDAO")
	public FacultyDAO facultyDAO(BasicDataSource dataSource) {
		FacultyDAO dao = new FacultyDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="interviewDAO")
	public InterviewDAO interviewDAO(BasicDataSource dataSource) {
		InterviewDAO dao = new InterviewDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="loginDAO")
	public LoginDAO loginDAO(BasicDataSource dataSource) {
		LoginDAO dao = new LoginDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="notificationDAO")
	public NotificationDAO notificationDAO(BasicDataSource dataSource) {
		NotificationDAO dao = new NotificationDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="packageAdminDAO")
	public PackageAdminDAO packageAdminDAO(BasicDataSource dataSource) {
		PackageAdminDAO dao = new PackageAdminDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="packageApplicabilityDAO")
	public PackageApplicabilityDAO packageApplicabilityDAO(BasicDataSource dataSource) {
		PackageApplicabilityDAO dao = new PackageApplicabilityDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="paymentManagerDAO")
	public PaymentManagerDAO paymentManagerDAO(BasicDataSource dataSource) {
		PaymentManagerDAO dao = new PaymentManagerDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="progressDetailsDAO")
	public ProgressDetailsDAO progressDetailsDAO(BasicDataSource dataSource) {
		ProgressDetailsDAO dao = new ProgressDetailsDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="sessionAttendanceDao")
	public SessionAttendanceDao sessionAttendanceDao(BasicDataSource dataSource) {
		SessionAttendanceDao dao = new SessionAttendanceDao();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="sessionFeedbackDAO")
	public SessionFeedbackDAO SessionFeedbackDAO(BasicDataSource dataSource) {
		SessionFeedbackDAO dao = new SessionFeedbackDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="sessionSchedulerDAO")
	public SessionSchedulerDao sessionSchedulerDao(BasicDataSource dataSource) {
		SessionSchedulerDao dao = new SessionSchedulerDao();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="sessionsDAO")
	public SessionsDAO sessionsDAO(BasicDataSource dataSource) {
		SessionsDAO dao = new SessionsDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="studentDataManagementDAO")
	public StudentDataManagementDAO studentDataManagementDAO(BasicDataSource dataSource) {
		StudentDataManagementDAO dao = new StudentDataManagementDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="videoRecordingDao")
	public VideoRecordingDao videoRecordingDao(BasicDataSource dataSource) {
		VideoRecordingDao dao = new VideoRecordingDao();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="webinarSchedulerDAO")
	public WebinarSchedulerDAO webinarSchedulerDAO(BasicDataSource dataSource) {
		WebinarSchedulerDAO dao = new WebinarSchedulerDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean(name="entitlementManagementDAO")
	public EntitlementManagementDAO entitlementManagementDAO(BasicDataSource dataSource) {
		EntitlementManagementDAO dao = new EntitlementManagementDAO();
		dao.setDataSource(dataSource);
		return dao;
	}

	@Bean("sessionQueryAnswerDAO") 
	public SessionQueryAnswerDAO sessionQueryAnswerDAO(BasicDataSource dataSource) {
		SessionQueryAnswerDAO dao = new SessionQueryAnswerDAO();
		dao.setDataSource(dataSource);
		return dao;
	}
	@Bean("entitlementCheckerDAO") 
	public EntitlementCheckerDAO entitlementCheckerDAO(BasicDataSource dataSource) {
		EntitlementCheckerDAO dao = new EntitlementCheckerDAO();
		dao.setDataSource(dataSource);
		return dao;
	}
}