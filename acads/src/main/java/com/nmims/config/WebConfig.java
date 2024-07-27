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
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
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

import com.nmims.beans.ResponseAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.daos.AttendanceFeedbackDAO;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.ConferenceDAO;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.DummyUsersDAO;
import com.nmims.daos.EventsDao;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.FeedPostsDAO;
import com.nmims.daos.ForumDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.LearningResourcesDAO;
import com.nmims.daos.LtiDao;
import com.nmims.daos.MBAXSessionPlanDAO;
import com.nmims.daos.NotificationDAO;
import com.nmims.daos.PCPBookingDAO;
import com.nmims.daos.ReportsDAO;
import com.nmims.daos.SessionPlanDAO;
import com.nmims.daos.SessionPollsDAO;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.SessionReviewDAO;
import com.nmims.daos.StudentCourseMappingDao;
import com.nmims.daos.StudentDAO;
import com.nmims.daos.SyllabusDAO;
import com.nmims.daos.TestDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.UpgradAssessmentDao;
import com.nmims.daos.VideoContentDAO;
import com.nmims.daos.CourseraDao;
import com.nmims.helpers.ConferenceBookingClient;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.MobileNotificationHelper;
import com.nmims.helpers.SMSSender;
import com.nmims.helpers.WebExMeetingManager;
import com.nmims.helpers.ZoomManager;
import com.nmims.listeners.ConferenceBookingScheduler;
import com.nmims.listeners.NotificationScheduler;
import com.nmims.listeners.SessionRecordingScheduler;
import com.nmims.daos.SessionTracksDAO;


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
	
	//ZOOM CREDS
	@Value("${ZOOM_SITE}")
	String ZOOM_SITE; 
	
    public static SoapVersion SOAPVERSION;
    
    //WEBEX CREDS
	@Value("${WEBEX_ID}")
    public String WEBEX_ID;
	
	@Value("${WEBEX_PASS}")
	public String WEBEX_PASS;
	
	@Value("${WEBEX_SITE}")
	public String WEBEX_SITE;

	@Value("${TMS_URL}")
	public String TMS_URL; 
	 
	public static AuthScope AUTH_SCOPE; 

//	  @Bean
//	    public ConversionService myConversionService()
//	    {
//	        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
//	        bean.setConverters( getConverters() );
//	        bean.afterPropertiesSet();
//	        ConversionService object = bean.getObject();
//	        return object;
//	    }
//	 
//	  private Set<Converter<?, ?>> getConverters()
//	    {
//	        Set<Converter<?, ?>> converters = new HashSet<Converter<?, ?>>();
//
//	        converters.add( customStringToArrayConverter );
//	        // add here more custom converters, either as spring bean references or directly instantiated
//
//	        return converters;
//	    }
//	@Autowired
//	private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
//
//	@PostConstruct
//	public void init() {
//	    requestMappingHandlerAdapter.setIgnoreDefaultModelOnRedirect(true);
//	}
	
	/* Handles HTTP GET requests for /resources/** by efficiently serving 
	up static resources in the ${webappRoot}/resources directory */	
	
	@Bean
	public static ConversionService conversionService() {
	    return new DefaultFormattingConversionService();
	}
	
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	    registry.addResourceHandler("/resources_2015/**").addResourceLocations("/resources_2015/");
	}
	
	@Bean(name = "conferenceBookingScheduler")
	public ConferenceBookingScheduler conferenceBookingScheduler() {
		ConferenceBookingScheduler bean = new ConferenceBookingScheduler();
		return bean;
	}
	

	@Bean(name = "SessionRecordingScheduler")
	public SessionRecordingScheduler SessionRecordingScheduler() {
		SessionRecordingScheduler bean = new SessionRecordingScheduler();
		return bean;
	}
	
	@Bean(name="notificationScheduler")
	public NotificationScheduler notificationScheduler() {
		NotificationScheduler bean = new NotificationScheduler();
		return bean;
	}

	@Bean(name = "SMSSender")
	public SMSSender SMSSender() {
		SMSSender bean = new SMSSender();
		return bean;
	}
	/* Resolves views selected for rendering by @Controllers to .jsp resources 
	in the /WEB-INF/views directory */
	@Bean(name="internalResourceViewResolver")
	public ViewResolver internalResourceViewResolver() {
	    InternalResourceViewResolver bean = new InternalResourceViewResolver();
	    bean.setViewClass(JstlView.class);
	    bean.setOrder(2);
	    bean.setPrefix("/views/");
	    bean.setSuffix(".jsp");
	    bean.setExposeContextBeansAsAttributes(true);
	    return bean;
	}
	
	@Bean(name="xmlViewResolver")
	public ViewResolver xmlViewResolver() {
	    XmlViewResolver bean = new XmlViewResolver();
	    bean.setLocation(new ClassPathResource("/WEB-INF/spring/appServlet/spring-excel-views.xml"));
	    bean.setOrder(1);
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
	
	@Bean(name="careerServicesDAO")
	public CareerServicesDAO careerServicesDAO(BasicDataSource dataSource) {
		CareerServicesDAO bean = new CareerServicesDAO();
		bean.setDataSource(dataSource);
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
	
	@Bean(name="timeTableDAO")
	public TimeTableDAO timeTableDAO(BasicDataSource dataSource, PlatformTransactionManager transactionManager) {
		TimeTableDAO bean = new TimeTableDAO();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}
	
	@Bean(name="sessionReviewDAO")
	public SessionReviewDAO sessionReviewDAO(BasicDataSource dataSource) {
		SessionReviewDAO bean = new SessionReviewDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="facultyDAO")
	public FacultyDAO facultyDAO(BasicDataSource dataSource) {
		FacultyDAO bean = new FacultyDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="contentDAO")
	public ContentDAO contentDAO(BasicDataSource dataSource) {
		ContentDAO bean = new ContentDAO();
		bean.setDataSource(dataSource);
		return bean;	
	}

	@Bean(name="videoContentDAO")
	public VideoContentDAO videoContentDAO(BasicDataSource dataSource) {
		VideoContentDAO bean = new VideoContentDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="learningResourcesDAO")
	public LearningResourcesDAO learningResourcesDAO(BasicDataSource dataSource) {
		LearningResourcesDAO bean = new LearningResourcesDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="leadDAO")
	public LeadDAO LeadDAO(BasicDataSource dataSource) {
		LeadDAO bean = new LeadDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="ltiDAO")
	public LtiDao ltiDAO(BasicDataSource dataSource) {
		LtiDao bean = new LtiDao();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="sessionPlanDAO")
	public SessionPlanDAO sessionPlanDAO(BasicDataSource dataSource) {
		SessionPlanDAO bean = new SessionPlanDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="mbaxSessionPlanDAO")
	public MBAXSessionPlanDAO mbaxSessionPlanDAO(BasicDataSource dataSource) {
		MBAXSessionPlanDAO bean = new MBAXSessionPlanDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="conferenceBookingDAO")
	public ConferenceDAO conferenceBookingDAO(BasicDataSource dataSource) {
		ConferenceDAO bean = new ConferenceDAO();
		bean.setDataSource(dataSource);
		return bean;
		
	}

	@Bean(name="eventsDao")
	public EventsDao eventsDao(BasicDataSource dataSource) {
		EventsDao bean = new EventsDao();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="notificationDAO")
	public NotificationDAO notificationDAO(BasicDataSource dataSource) {
		NotificationDAO bean = new NotificationDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="reportsDAO")
	public ReportsDAO reportsDAO(BasicDataSource dataSource) {
		ReportsDAO bean = new ReportsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	

	@Bean(name="attendanceFeedbackDAO")
	public AttendanceFeedbackDAO attendanceFeedbackDAO(BasicDataSource dataSource) {
		AttendanceFeedbackDAO bean = new AttendanceFeedbackDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="forumDAO")
	public ForumDAO forumDAO(BasicDataSource dataSource) {
		ForumDAO bean = new ForumDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="sessionQueryAnswerDAO")
	public SessionQueryAnswerDAO sessionQueryAnswerDAO(BasicDataSource dataSource) {
		SessionQueryAnswerDAO bean = new SessionQueryAnswerDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="pcpBookingDAO")
	public PCPBookingDAO pcpBookingDAO(BasicDataSource dataSource) {
		PCPBookingDAO bean = new PCPBookingDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver bean = new CommonsMultipartResolver();
		bean.setMaxUploadSize(200000000);
		return bean;
	}
	
	@Bean(name="transactionManager")
	public DataSourceTransactionManager transactionManager(BasicDataSource dataSource) {
		DataSourceTransactionManager bean = new DataSourceTransactionManager();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="transactionTemplate")
	public TransactionTemplate	transactionTemplate(DataSourceTransactionManager transactionManager) {
		TransactionTemplate bean = new TransactionTemplate();
		bean.setTransactionManager(transactionManager);
		return bean;
	}

//	@Bean(name="examBookingTransactionManager")
//	public DataSourceTransactionManager examBookingTransactionManager(BasicDataSource dataSource) {
//		DataSourceTransactionManager bean = new DataSourceTransactionManager();
//		bean.setDataSource(dataSource);
//		return bean;
//	}
		
	@Bean(name = "responseBean" )
	public ResponseAcadsBean responseBean() {
		ResponseAcadsBean bean = new ResponseAcadsBean();
		return bean;
	}
	
	@Bean(name="feedPostsDAO")
	public FeedPostsDAO feedPostsDAO(BasicDataSource dataSource) {
		FeedPostsDAO bean = new FeedPostsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="dummyUsersDAO")
	public DummyUsersDAO dummyUsersDAO(BasicDataSource dataSource) {
		DummyUsersDAO bean = new DummyUsersDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="testDao")
	public TestDAO testDao(BasicDataSource dataSource) {
		TestDAO bean = new TestDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="upgradAssessmentDao")
	public UpgradAssessmentDao upgradAssessmentDao(BasicDataSource dataSource) {
		UpgradAssessmentDao bean = new UpgradAssessmentDao();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="studentDAO")
	public StudentDAO studentDAO(BasicDataSource dataSource) {
		StudentDAO bean = new StudentDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name="syllabusDao")
	public SyllabusDAO syllabusDao(BasicDataSource dataSource) {
		SyllabusDAO bean = new SyllabusDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean("messageFactory")
	SaajSoapMessageFactory messageFactory() {
		SaajSoapMessageFactory bean = new SaajSoapMessageFactory();
		bean.setSoapVersion(SOAPVERSION.SOAP_12);
		return bean;
	}
	
	@Bean(name="marshaller")
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller bean = new Jaxb2Marshaller();
		bean.setContextPath("bookingservice.wsdl");
		return bean;
	}
	
	@Bean(name="conferenceWsTemplate") 
	public WebServiceTemplate conferenceWsTemplate(SaajSoapMessageFactory messageFactory,Jaxb2Marshaller marshaller) {
		WebServiceTemplate bean = new WebServiceTemplate(messageFactory);
		bean.setDefaultUri(TMS_URL);
		bean.setMarshaller(marshaller);
		bean.setUnmarshaller(marshaller);
		HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
		NTCredentials credentials = new NTCredentials("testuser","pass@123" , "svkmgrp", "");
		messageSender.setCredentials(credentials);
		messageSender.setReadTimeout(1200000);
		messageSender.setConnectionTimeout(1200000);
		AuthScope authScope = new AuthScope(AUTH_SCOPE.ANY_HOST, AUTH_SCOPE.ANY_PORT, "122.170.126.150", "NTLM");
		messageSender.setAuthScope(authScope);
		bean.setMessageSender(messageSender);
		return bean;
	}
	
	@Bean("conferenceBookingClient")
	public ConferenceBookingClient conferenceBookingClient(Jaxb2Marshaller marshaller, WebServiceTemplate conferenceWsTemplate) {
		ConferenceBookingClient bean = new ConferenceBookingClient();
		return bean;
	}

	@Bean(name="MobileNotificationHelper")
	public MobileNotificationHelper MobileNotificationHelper() {
		MobileNotificationHelper bean = new MobileNotificationHelper();
		return bean;
	}
	
	@Bean(name="zoomManger")
	public ZoomManager zoomManger() {
		ZoomManager bean = new ZoomManager();
		bean.setSite(ZOOM_SITE);
		return bean;
	}
	
	@Bean(name="webExMeetingManager")
	public WebExMeetingManager webExMeetingManager() {
		WebExMeetingManager bean = new WebExMeetingManager();
		bean.setWebExID(WEBEX_ID);
		bean.setPassword(WEBEX_PASS);
		bean.setSite(WEBEX_SITE);
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
	@Bean(name="mailSender")
	public JavaMailSenderImpl mailSender() {
		JavaMailSenderImpl bean = new JavaMailSenderImpl();
		bean.setHost(SMTP_HOST);
		bean.setPort(SMTP_PORT);
		bean.setUsername(SMTP_USERNAME);
		bean.setPassword(SMTP_PASSWORD);
		Properties javaMailProperties = new Properties();
		javaMailProperties.setProperty("mail.smtp.auth", "true");
		javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
		bean.setJavaMailProperties(javaMailProperties);
		return bean;
	}
	@Bean(name="sessionDayTimeBean")
	public SessionDayTimeAcadsBean sessionDayTimeBean() {
		SessionDayTimeAcadsBean bean = new SessionDayTimeAcadsBean();
		return bean;
	}
	
	@Bean(name="sessionPollsDAO")
	public SessionPollsDAO SessionPollsDAO(BasicDataSource dataSource) {
		SessionPollsDAO bean = new SessionPollsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="courseraDao")
	public CourseraDao CourseraDao(BasicDataSource dataSource) {
		CourseraDao bean = new CourseraDao();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean("studentCourseMappingDao") 
	public StudentCourseMappingDao announcementsDAO(BasicDataSource dataSource) {
		StudentCourseMappingDao bean = new StudentCourseMappingDao();
		bean.setDataSource(dataSource);
		return bean;
	}
	@Bean(name="sessionTracksDao")
	public SessionTracksDAO sessionTracksDao(BasicDataSource dataSource) {
		SessionTracksDAO bean = new SessionTracksDAO();
		bean.setDataSource(dataSource);
		return bean;
	}


	
}