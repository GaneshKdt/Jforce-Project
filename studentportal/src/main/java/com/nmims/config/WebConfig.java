package com.nmims.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.XmlViewResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.nmims.daos.AcadsDAO;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.CaseStudyDao;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.ErrorAnalyticsDAO;
import com.nmims.daos.ForumDAO;
import com.nmims.daos.FreeCourseDAO;
import com.nmims.daos.GatewayTransactionsDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.LeaderBoardDAO;
import com.nmims.daos.LearningResourcesDAO;
import com.nmims.daos.LiveSessionAccessDAO;
import com.nmims.daos.LoginLogDAO;
import com.nmims.daos.MBAWXLiveSettingsDAO;
import com.nmims.daos.OpenBadgesDAO;
import com.nmims.daos.KnowYourPolicyDAO;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ReportsDao;
import com.nmims.daos.ResultDAO;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.SessionPlanPgDao;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.StudentCourseMappingDao;
import com.nmims.daos.StudentDAO;
import com.nmims.daos.StudentInfoCheckDAO;
import com.nmims.daos.StudentSettingDao;
import com.nmims.daos.SupportDao;
import com.nmims.daos.TestDAO;
import com.nmims.helpers.CustomStringToArrayConverter;
import com.nmims.helpers.MailSender;
import com.nmims.interceptors.SessionImageInterceptor;

@Configuration
@EnableWebMvc //Enable SpringMVC
@ComponentScan({"com.nmims"})
@PropertySource("file:c:/NMIMS_PROPERTY_FILE/ngasce.properties")
@PropertySource(value="file:c:/NMIMS_PROPERTY_FILE/application.properties", ignoreResourceNotFound = true)
@PropertySource(value="file:${catalina.base}/conf/application.properties", ignoreResourceNotFound = true)
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
	
	 @Autowired 
	 SessionImageInterceptor sessionImageInterceptor;
	
	@Autowired
	 CustomStringToArrayConverter customStringToArrayConverter;
	
	 
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
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
    }
	
	@Bean("studentPortalRediret")
	public RedirectView studentPortalRediret() {
		RedirectView bean = new RedirectView();
		bean.setUrl("/studentportal/");
		return bean;
	}
	
	@Bean
	public static ConversionService conversionService() {
	    return new DefaultFormattingConversionService();
	}

	  @Override
	    public void addFormatters(FormatterRegistry registry) {
	        registry.addConverter(customStringToArrayConverter);
	    }
	
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(sessionImageInterceptor).addPathPatterns("/sessionImages");
			
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
	
//	@Primary @Bean(name="dataSource", destroyMethod="close")
//	public BasicDataSource dataSource() {
//		
//		BasicDataSource bean = new BasicDataSource();
//		bean.setDriverClassName(MYSQL_DATASOURCE_CLASS);
//		bean.setUrl(MYSQL_DATASOURCE_URL + "/exam");
//		bean.setUsername(MYSQL_DATASOURCE_USERNAME);
//		bean.setPassword(MYSQL_DATASOURCE_PASSWORD);
//		bean.setTestOnBorrow(true);
//		bean.setTestOnReturn(true);
//		bean.setTestWhileIdle(true);
//		bean.setTimeBetweenEvictionRunsMillis(1800000);
//		bean.setNumTestsPerEvictionRun(10);
//		bean.setMinEvictableIdleTimeMillis(1800000);
//		bean.setValidationQuery("SELECT 1");
//		bean.setInitialSize(50);
//		bean.setMaxIdle(50);
//		bean.setMaxTotal(-1);
//		return bean;		
//	}
		
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
	
//	@Bean(name="mailSender")
//	public JavaMailSenderImpl mailSender() {
//		JavaMailSenderImpl bean = new JavaMailSenderImpl();
//		bean.setHost(SMTP_HOST);
//		bean.setPort(SMTP_PORT);
//		bean.setUsername(SMTP_USERNAME);
//		bean.setPassword(SMTP_PASSWORD);
//		Properties javaMailProperties = new Properties();
//		javaMailProperties.setProperty("mail.smtp.auth", "true");
//		javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
//		bean.setJavaMailProperties(javaMailProperties);
//		return bean;
//	}
	
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
	
	@Bean(name = "ldapdao")
	public LDAPDao ldapdao(LdapTemplate ldapTemplate) {
		LDAPDao bean = new LDAPDao();
		bean.setLdapTemplate(ldapTemplate);
		return bean;
	}
	
	@Bean(name = "portalDAO")
	public PortalDao portalDAO(BasicDataSource dataSource) {
		PortalDao bean = new PortalDao();
		bean.setDataSource(dataSource);
		return bean;
	}
	@Bean(name="careerServicesDAO")
	public CareerServicesDAO careerServicesDAO(BasicDataSource dataSource) {
		CareerServicesDAO bean = new CareerServicesDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	
	@Bean(name="resultDAO")
	public ResultDAO ResultDAO(BasicDataSource dataSource) {
		ResultDAO bean = new ResultDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="gatewayTransactionDao")
	public GatewayTransactionsDAO gatewayTransactionDao(BasicDataSource dataSource) {
		GatewayTransactionsDAO bean = new GatewayTransactionsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}


	@Bean(name="AcadsDAO")
	public AcadsDAO AcadsDAO(BasicDataSource dataSource) {
		AcadsDAO bean = new AcadsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="stuentInfoCheckDAO")
	public StudentInfoCheckDAO stuentInfoCheckDAO(BasicDataSource dataSource) {
		StudentInfoCheckDAO bean = new StudentInfoCheckDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="asignmentsDAO")
	public AssignmentsDAO asignmentsDAO(BasicDataSource dataSource) {
		AssignmentsDAO bean = new AssignmentsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="serviceRequestDao")
	public ServiceRequestDao serviceRequestDao(BasicDataSource dataSource) {
		ServiceRequestDao bean = new ServiceRequestDao();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="contentDAO")
	public ContentDAO contentDAO(BasicDataSource dataSource) {
		ContentDAO bean = new ContentDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="forumDAO")
	public ForumDAO forumDAO(BasicDataSource datasource) {
		ForumDAO bean = new ForumDAO();
		bean.setDataSource(datasource);
		return bean;
	}
		
	@Bean(name="caseStudyDAO")
	public CaseStudyDao caseDAO(BasicDataSource datasource) {
		CaseStudyDao bean = new CaseStudyDao();
		bean.setDataSource(datasource);
		return bean;
	}
	
	@Bean(name="learningResourcesDAO")
	public LearningResourcesDAO learningResourcesDAO(BasicDataSource dataSource) {
		LearningResourcesDAO bean = new LearningResourcesDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="testDao") 
	public TestDAO testDao(BasicDataSource dataSource) {
		TestDAO bean = new TestDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name= "errorAnalyticsDAO")
	public ErrorAnalyticsDAO errorAnalyticsDAO(BasicDataSource dataSource) {
		ErrorAnalyticsDAO bean = new ErrorAnalyticsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="mbawxLiveSettingsDAO")
	public MBAWXLiveSettingsDAO mbawxLiveSettingsDAO(BasicDataSource dataSource) {
		MBAWXLiveSettingsDAO bean = new MBAWXLiveSettingsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean("leadDAO")
	public LeadDAO leadDAO(BasicDataSource dataSource) {
		LeadDAO bean = new LeadDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean("FreeCourseDAO")
	public FreeCourseDAO FreeCourseDAO(BasicDataSource dataSource) {
		FreeCourseDAO bean = new FreeCourseDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean("sessionQueryAnswerDAO") 
	public SessionQueryAnswerDAO sessionQueryAnswerDAO(BasicDataSource dataSource) {
		SessionQueryAnswerDAO bean = new SessionQueryAnswerDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
		
	@Bean("openBadgesDAO") 
	public OpenBadgesDAO openBadgesDAO(BasicDataSource dataSource) {
		OpenBadgesDAO bean = new OpenBadgesDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean("studentDAO")
	public StudentDAO studentDAO(BasicDataSource dataSource) {
		StudentDAO bean = new StudentDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean("xmlViewResolver")
	public ViewResolver xmlViewResolver() {
	    XmlViewResolver bean = new XmlViewResolver();
	    bean.setLocation(new ClassPathResource("/WEB-INF/spring/appServlet/spring-excel-views.xml"));
	    bean.setOrder(1);
	    return bean;
	} 
	
	@Bean
	public SpringResourceTemplateResolver thymeleafTemplateResolver(){
	    SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
	   // templateResolver.setApplicationContext(webApplicationContext);
	    templateResolver.setOrder(1);
	   templateResolver.setPrefix("/views/");
	    templateResolver.setSuffix(".html");
	    templateResolver.setTemplateMode("HTML5");
	    return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
	    SpringTemplateEngine springTemplateEngine= new SpringTemplateEngine();
	    springTemplateEngine.setTemplateResolver(thymeleafTemplateResolver());
	   // springTemplateEngine.setEnableSpringELCompiler(true);
	    return springTemplateEngine;
	}

	@Bean
	public ThymeleafViewResolver thymeleafViewResolver(){
	    final ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
	   // viewResolver.setViewNames(new String[] {"*.html"});
	   viewResolver.setViewNames(new String[]{"templates/*"});

	    viewResolver.setExcludedViewNames(new String[] {"*.jsp"});
	    viewResolver.setTemplateEngine(templateEngine());
	    viewResolver.setCharacterEncoding("UTF-8");
	    return viewResolver;
	}
	@Bean("internalResourceViewResolver")
	public ViewResolver internalResourceViewResolver() {
	    InternalResourceViewResolver bean = new InternalResourceViewResolver();
	    bean.setViewClass(JstlView.class);
	    bean.setPrefix("/views/");
	    bean.setSuffix(".jsp");
	    bean.setViewNames("jsp/*");
	    bean.setOrder(2);
	    return bean;
	}
	
	@Bean("liveSessionAccessDAO")
	public LiveSessionAccessDAO liveSessionAccessDAO(BasicDataSource dataSource) {
		LiveSessionAccessDAO bean = new LiveSessionAccessDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean("leaderBoardDAO")
	public LeaderBoardDAO leaderBoardDAO(BasicDataSource dataSource) {
		LeaderBoardDAO bean = new LeaderBoardDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean("loginLogDAO")
	public LoginLogDAO loginLogDAO(BasicDataSource dataSource) {
		LoginLogDAO dao = new LoginLogDAO();
		dao.setDataSource(dataSource);
		return dao;
	}
	
	
	@Bean("studentCourseMappingDao") 
	public StudentCourseMappingDao announcementsDAO(BasicDataSource dataSource) {
		StudentCourseMappingDao bean = new StudentCourseMappingDao();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean("supportDao") 
	public SupportDao supportDao(BasicDataSource dataSource) {
		SupportDao dao = new SupportDao();
		dao.setDataSource(dataSource);
		return dao;
	}
	@Bean("policyDAO")
	public KnowYourPolicyDAO policyDAO(BasicDataSource dataSource) {
		KnowYourPolicyDAO dao=new KnowYourPolicyDAO();
		dao.setDataSource(dataSource);
		return dao;
	}
	@Bean("reportsDAO")
	public ReportsDao reportsDao(BasicDataSource dataSource) {
		ReportsDao dao=new ReportsDao();
		dao.setDataSource(dataSource);
		return dao;
	}
	
	@Bean("sessionPlanPgDao") 
	public SessionPlanPgDao sessionPlanPgDao(BasicDataSource dataSource) {
		SessionPlanPgDao bean = new SessionPlanPgDao();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean("studentSettingDao") 
	public StudentSettingDao studentSettingDao(BasicDataSource dataSource) {
		StudentSettingDao bean = new StudentSettingDao();
		bean.setDataSource(dataSource);
		return bean;
	}
}