package com.nmims.config;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.auth.AuthScope;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.XmlViewResolver;

import com.nmims.beans.ResponseBean;
import com.nmims.daos.AnalyticsApiDAO;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.AuditTrailsDAO;
import com.nmims.daos.BlockStudentExamCenterDAO;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.CaseStudyDao;
import com.nmims.daos.CurrencyDAO;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.DissertationQ7DAO;
import com.nmims.daos.DissertationQ8ResultDaoImpl;
import com.nmims.daos.DivisionDetailsDao;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamBookingEligibilityDAO;
import com.nmims.daos.ExamBookingMettlMappingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.ExamResultChecklistDao;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.ExecutiveConfigurationDao;
import com.nmims.daos.ExecutiveExamBookingDao;
import com.nmims.daos.ExecutiveExamDao;
import com.nmims.daos.ExecutiveTimeTableDao;
import com.nmims.daos.ExitSrMdmDao;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.FreeCertificateCourseDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.LevelBasedProjectDAO;
import com.nmims.daos.LiveSessionReportAdminDAO;
import com.nmims.daos.MBAStudentDetailsDAO;
import com.nmims.daos.MBAWXCentersDAO;
import com.nmims.daos.MBAWXExamBookingDAO;
import com.nmims.daos.MBAWXExamResultsDAO;
import com.nmims.daos.MBAWXHallTicketDAO;
import com.nmims.daos.MBAWXLiveSettingsDAO;
import com.nmims.daos.MBAWXPaymentsDao;
import com.nmims.daos.MBAWXReRegistrationDAO;
import com.nmims.daos.MBAWXReportsDAO;
import com.nmims.daos.MBAWXSlotDAO;
import com.nmims.daos.MBAWXTeeDAO;
import com.nmims.daos.MBAWXTimeTableDAO;
import com.nmims.daos.MBAXAuditTrailsDAO;
import com.nmims.daos.MBAXCentersDAO;
import com.nmims.daos.MBAXExamBookingDAO;
import com.nmims.daos.MBAXHallTicketDAO;
import com.nmims.daos.MBAXIADAO;
import com.nmims.daos.MBAXLiveSettingsDAO;
import com.nmims.daos.MBAXPaymentsDao;
import com.nmims.daos.MBAXReportsDAO;
import com.nmims.daos.MBAXSlotDAO;
import com.nmims.daos.MBAXTeeDAO;
import com.nmims.daos.MBAXTimeTableDAO;
import com.nmims.daos.MDMSubjectCodeDAO;
import com.nmims.daos.MettlDAO;
import com.nmims.daos.MettlPGResultProcessingDAO;
import com.nmims.daos.MettlTeeDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.PassFailTransferDao;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.daos.ProjectTitleDAO;
import com.nmims.daos.RedisResultsDAO;
import com.nmims.daos.RemarksGradeDAO;
import com.nmims.daos.RemarksGradeResultsDAO;
import com.nmims.daos.ReportsDAO;
import com.nmims.daos.ResitExamBookingDAO;
import com.nmims.daos.ServiceRequestDAO;
import com.nmims.daos.SifyDAO;
import com.nmims.daos.SpecialisationDAO;
import com.nmims.daos.StudentDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.StudentTestDAO;
import com.nmims.daos.TCSApiDAO;
import com.nmims.daos.TestDAO;
import com.nmims.daos.UFMNoticeDAO;
import com.nmims.daos.UpgradAssessmentDao;
import com.nmims.daos.UpgradResultProcessingDao;
import com.nmims.helpers.CustomStringToArrayConverter;
import com.nmims.helpers.LevelBasedProjectHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.MettlHelper;
import com.nmims.repository.StudentRepository;
import com.nmims.stratergies.RemarkResultsDisplayStrategyInterface;
import com.nmims.stratergies.impl.RemarkResultsDisplayStrategy;

@Configuration
@EnableWebMvc //Enable SpringMVC 
@ComponentScan({"com.nmims"})
@PropertySource(value = "file:c:/NMIMS_PROPERTY_FILE/ngasce.properties",ignoreResourceNotFound = true)
@PropertySource(value = "file:${catalina.base}/conf/application.properties",ignoreResourceNotFound = true)
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
	
	@Value("${MSC_METTL_BASE_URL}")
	public String MSC_METTL_BASE_URL;
	@Value("${MSC_METTL_PRIVATE_KEY}")
	public String MSC_METTL_PRIVATE_KEY;
	@Value("${MSC_METTL_PUBLIC_KEY}")
	public String MSC_METTL_PUBLIC_KEY;
	
	@Value("${MettlBaseUrl}")
	public String MettlBaseUrl;
	@Value("${MettlPrivateKey}")
	public String MettlPrivateKey;
	@Value("${MettlPublicKey}")
	public String MettlPublicKey;
	
	@Value("${MBA_X_METTL_CHILD_BASE_URL}")
	public String MBA_X_METTL_CHILD_BASE_URL;
	@Value("${MBA_X_METTL_CHILD_PRIVATE_KEY}")
	public String MBA_X_METTL_CHILD_PRIVATE_KEY;
	@Value("${MBA_X_METTL_CHILD_PUBLIC_KEY}")
	public String MBA_X_METTL_CHILD_PUBLIC_KEY; 
	
	
	@Value("${MODULAR_PDDM_METTL_PUBLIC_KEY}")
	public String MODULAR_PDDM_METTL_PUBLIC_KEY;
	
	@Value("${MODULAR_PDDM_METTL_PRIVATE_KEY}")
	public String MODULAR_PDDM_METTL_PRIVATE_KEY;
	
	@Value("${EXECUTOR_BEAN_CORE_SIZE}")
	private int EXECUTOR_BEAN_CORE_SIZE;
	
	@Value("${EXECUTOR_BEAN_MAX_POOL_SIZE}")
	private int EXECUTOR_BEAN_MAX_POOL_SIZE;
	
	@Value("${EXECUTOR_BEAN_QUEUE_CAPACITY}")
	private int EXECUTOR_BEAN_QUEUE_CAPACITY;
	
	@Value("${EXECUTOR_BEAN_THREAD_NAME_PREFIX}")
	private String EXECUTOR_BEAN_THREAD_NAME_PREFIX;
	
	
	/* Handles HTTP GET requests for /resources/** by efficiently serving 
	up static resources in the ${webappRoot}/resources directory */	
	
	@Bean
	public static ConversionService conversionService() {
	    return new DefaultFormattingConversionService();
	}
	
	  @Override
	  public void addFormatters(FormatterRegistry registry) {
	        registry.removeConvertible(String.class, ArrayList.class);
	        registry.addConverter(new CustomStringToArrayConverter());
	  }
	
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	    registry.addResourceHandler("/resources_2015/**").addResourceLocations("/resources_2015/");
	    registry.addResourceHandler("/assets/**").addResourceLocations("/assets/");

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
	
	@Bean(name = "mettlTeeDAO" )
	public MettlTeeDAO mettlTeeDAO(BasicDataSource dataSource) {
		MettlTeeDAO bean = new MettlTeeDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean("jdbcTemplate")
	public JdbcTemplate jdbcTemplate(BasicDataSource dataSource) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate;
	}
	
	@Bean(name = "careerServicesDAO" )
	public CareerServicesDAO careerServicesDAO(BasicDataSource dataSource) {
		CareerServicesDAO bean = new CareerServicesDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "mbaWXTeeDAO" )
	public MBAWXTeeDAO mbaWXTeeDAO(BasicDataSource dataSource) {
		MBAWXTeeDAO bean = new MBAWXTeeDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "mbaXTeeDAO" )
	public MBAXTeeDAO mbaXTeeDAO(BasicDataSource dataSource) {
		MBAXTeeDAO bean = new MBAXTeeDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "mbaWxPaymentsDao" )
	public MBAWXPaymentsDao mbaWxPaymentsDao(BasicDataSource dataSource) {
		MBAWXPaymentsDao bean = new MBAWXPaymentsDao();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name = "mbawxReRegistrationDAO" )
	public MBAWXReRegistrationDAO mbawxReRegistrationDAO(BasicDataSource dataSource) {
		MBAWXReRegistrationDAO bean = new MBAWXReRegistrationDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "mbaWxExamBookingDAO" )
	public MBAWXExamBookingDAO mbaWxExamBookingDAO(BasicDataSource dataSource) {
		MBAWXExamBookingDAO bean = new MBAWXExamBookingDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "examBookingMettlMappingDAO" )
	public ExamBookingMettlMappingDAO examBookingMettlMappingDAO(BasicDataSource dataSource) {
		ExamBookingMettlMappingDAO bean = new ExamBookingMettlMappingDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name = "mbaWxExamResultsDAO" )
	public MBAWXExamResultsDAO mbaWxExamResultsDAO(BasicDataSource dataSource) {
		MBAWXExamResultsDAO bean = new MBAWXExamResultsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "studentMarksDAO" )
	public StudentMarksDAO studentMarksDAO(BasicDataSource dataSource) {
		StudentMarksDAO bean = new StudentMarksDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "passFailDAO" )
	public PassFailDAO passFailDAO(BasicDataSource dataSource) {
		PassFailDAO bean = new PassFailDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "examCenterDAO" )
	public ExamCenterDAO examCenterDAO(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
		ExamCenterDAO bean = new ExamCenterDAO();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}
	
	@Bean(name = "mbawxexamCenterDAO" )
	public MBAWXCentersDAO mbawxexamCenterDAO(BasicDataSource dataSource) {
		MBAWXCentersDAO bean = new MBAWXCentersDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "mbawxTimeTableDAO" )
	public MBAWXTimeTableDAO mbawxTimeTableDAO(BasicDataSource dataSource) {
		MBAWXTimeTableDAO bean = new MBAWXTimeTableDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "mbawxSlotDAO" )
	public MBAWXSlotDAO mbawxSlotDAO(BasicDataSource dataSource) {
		MBAWXSlotDAO bean = new MBAWXSlotDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "examBookingDAO" )
	public ExamBookingDAO examBookingDAO(BasicDataSource dataSource) {
		ExamBookingDAO bean = new ExamBookingDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "resitExamBookingDAO" )
	public ResitExamBookingDAO resitExamBookingDAO(BasicDataSource dataSource) {
		ResitExamBookingDAO bean = new ResitExamBookingDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "reportsDAO" )
	public ReportsDAO reportsDAO(BasicDataSource dataSource) {
		ReportsDAO bean = new ReportsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "asignmentsDAO" )
	public AssignmentsDAO asignmentsDAO(BasicDataSource dataSource) {
		AssignmentsDAO bean = new AssignmentsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "examsAssessmentsDAO" )
	public ExamsAssessmentsDAO examsAssessmentsDAO(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
		ExamsAssessmentsDAO bean = new ExamsAssessmentsDAO();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}
	
	@Bean(name = "facultyDAO" )
	public FacultyDAO facultyDAO(BasicDataSource dataSource) {
		FacultyDAO bean = new FacultyDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "projectSubmissionDAO" )
	public ProjectSubmissionDAO projectSubmissionDAO(BasicDataSource dataSource) {
		ProjectSubmissionDAO bean = new ProjectSubmissionDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "sifyDAO" )
	public SifyDAO sifyDAO(BasicDataSource dataSource) {
		SifyDAO bean = new SifyDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "dashboardDAO" )
	public DashboardDAO dashboardDAO(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
		DashboardDAO bean = new DashboardDAO();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}
	
	@Bean(name = "mdmSubjectCodeDAO" )
	public MDMSubjectCodeDAO mdmSubjectCodeDAO(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
		MDMSubjectCodeDAO bean = new MDMSubjectCodeDAO();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}
	
	@Bean(name = "caseDAO" )
	public CaseStudyDao caseDAO(BasicDataSource dataSource) {
		CaseStudyDao bean = new CaseStudyDao();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "leadDAO" )
	public LeadDAO leadDAO(BasicDataSource dataSource) {
		LeadDAO bean = new LeadDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "ufmNoticeDAO" )
	public UFMNoticeDAO ufmNoticeDAO(BasicDataSource dataSource) {
		UFMNoticeDAO bean = new UFMNoticeDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "remarksGradeResultsDAO" )
	public RemarksGradeResultsDAO remarksGradeResultsDAO(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
		RemarksGradeResultsDAO bean = new RemarksGradeResultsDAO();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}
	
	@Bean(name = "mettlDAO" )
	public MettlDAO mettlDAO(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
		MettlDAO bean = new MettlDAO();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}
	
	@Bean(name = "remarksGradeDAO" )
	public RemarksGradeDAO remarksGradeDAO(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
		RemarksGradeDAO bean = new RemarksGradeDAO();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}
	
	@Bean(name = "liveSessionReportAdminDAO" )
	public LiveSessionReportAdminDAO liveSessionReportAdminDAO(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
		LiveSessionReportAdminDAO bean = new LiveSessionReportAdminDAO();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}
	
	@Bean(name = "executiveTimeTableDao" )
	public ExecutiveTimeTableDao executiveTimeTableDao(BasicDataSource dataSource) {
		ExecutiveTimeTableDao bean = new ExecutiveTimeTableDao();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "executiveConfigurationDao" )
	public ExecutiveConfigurationDao executiveConfigurationDao(BasicDataSource dataSource) {
		ExecutiveConfigurationDao bean = new ExecutiveConfigurationDao();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "executiveExamDao" )
	public ExecutiveExamDao executiveExamDao(BasicDataSource dataSource) {
		ExecutiveExamDao bean = new ExecutiveExamDao();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name = "executiveExamBookingDao" )
	public ExecutiveExamBookingDao executiveExamBookingDao(BasicDataSource dataSource) {
		ExecutiveExamBookingDao bean = new ExecutiveExamBookingDao();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "analyticsApiDAO" )
	public AnalyticsApiDAO analyticsApiDAO(BasicDataSource dataSource) {
		AnalyticsApiDAO bean = new AnalyticsApiDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name = "upgradAssessmentDao" )
	public UpgradAssessmentDao upgradAssessmentDao(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
		UpgradAssessmentDao bean = new UpgradAssessmentDao();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}

	@Bean(name = "mbaxExamCenterDAO" )
	public MBAXCentersDAO mbaxExamCenterDAO(BasicDataSource dataSource) {
		MBAXCentersDAO bean = new MBAXCentersDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "mbaxSlotDAO" )
	public MBAXSlotDAO mbaxSlotDAO(BasicDataSource dataSource) {
		MBAXSlotDAO bean = new MBAXSlotDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "mbaxTimeTableDAO" )
	public MBAXTimeTableDAO mbaxTimeTableDAO(BasicDataSource dataSource) {
		MBAXTimeTableDAO bean = new MBAXTimeTableDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "mbaxPaymentsDao" )
	public MBAXPaymentsDao mbaxPaymentsDao(BasicDataSource dataSource) {
		MBAXPaymentsDao bean = new MBAXPaymentsDao();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "mbaxExamBookingDAO" )
	public MBAXExamBookingDAO mbaxExamBookingDAO(BasicDataSource dataSource) {
		MBAXExamBookingDAO bean = new MBAXExamBookingDAO();
		bean.setDataSource(dataSource);
		return bean;
	}


@Bean(name = "mbaxHallTicketDAO" )
public MBAXHallTicketDAO mbaxHallTicketDAO(BasicDataSource dataSource) {
	MBAXHallTicketDAO bean = new MBAXHallTicketDAO();
	bean.setDataSource(dataSource);
	return bean;
}


@Bean(name = "mbawxHallTicketDAO" )
public MBAWXHallTicketDAO mbawxHallTicketDAO(BasicDataSource dataSource) {
	MBAWXHallTicketDAO bean = new MBAWXHallTicketDAO();
	bean.setDataSource(dataSource);
	return bean;
}



@Bean(name = "mbawxReportsDAO" )
public MBAWXReportsDAO mbawxReportsDAO(BasicDataSource dataSource) {
	MBAWXReportsDAO bean = new MBAWXReportsDAO();
	bean.setDataSource(dataSource);
	return bean;
}


@Bean(name = "mbaxReportsDAO" )
public MBAXReportsDAO mbaxReportsDAO(BasicDataSource dataSource) {
	MBAXReportsDAO bean = new MBAXReportsDAO();
	bean.setDataSource(dataSource);
	return bean;
}


@Bean(name = "mbaStudentDetailsDAO" )
public MBAStudentDetailsDAO mbaStudentDetailsDAO(BasicDataSource dataSource) {
	MBAStudentDetailsDAO bean = new MBAStudentDetailsDAO();
	bean.setDataSource(dataSource);
	return bean;
}


@Bean(name = "mbawxLiveSettingsDAO" )
public MBAWXLiveSettingsDAO mbawxLiveSettingsDAO(BasicDataSource dataSource) {
	MBAWXLiveSettingsDAO bean = new MBAWXLiveSettingsDAO();
	bean.setDataSource(dataSource);
	return bean;
}


@Bean(name = "mbaxLiveSettingsDAO" )
public MBAXLiveSettingsDAO mbaxLiveSettingsDAO(BasicDataSource dataSource) {
	MBAXLiveSettingsDAO bean = new MBAXLiveSettingsDAO();
	bean.setDataSource(dataSource);
	return bean;
}

@Bean(name = "upgradResultProcessingDao" )
public UpgradResultProcessingDao upgradResultProcessingDao(BasicDataSource dataSource) {
	UpgradResultProcessingDao bean = new UpgradResultProcessingDao();
	bean.setDataSource(dataSource);
	return bean;
}

@Bean(name = "mettlPGResultProcessingDAO" )
public MettlPGResultProcessingDAO mettlPGResultProcessingDAO(BasicDataSource dataSource) {
	MettlPGResultProcessingDAO bean = new MettlPGResultProcessingDAO();
	bean.setDataSource(dataSource);
	return bean;
}

@Bean(name = "examBookingEligibilityDAO" )
public ExamBookingEligibilityDAO examBookingEligibilityDAO(BasicDataSource dataSource) {
	ExamBookingEligibilityDAO bean = new ExamBookingEligibilityDAO();
	bean.setDataSource(dataSource);
	return bean;
}

@Bean(name = "auditDao" )
public AuditTrailsDAO auditDao(BasicDataSource dataSource) {
	AuditTrailsDAO bean = new AuditTrailsDAO();
	bean.setDataSource(dataSource);
	return bean;
}

@Bean(name = "serviceRequestDao" )
public ServiceRequestDAO serviceRequestDao(BasicDataSource dataSource) {
	ServiceRequestDAO bean = new ServiceRequestDAO();
	bean.setDataSource(dataSource);
	return bean;
}


@Bean(name = "tcsApiDAO" )
public TCSApiDAO tcsApiDAO(BasicDataSource dataSource) {
	TCSApiDAO bean = new TCSApiDAO();
	bean.setDataSource(dataSource);
	return bean;
}


@Bean(name = "specialisationDao" )
public SpecialisationDAO specialisationDao(BasicDataSource dataSource) {
	SpecialisationDAO bean = new SpecialisationDAO();
	bean.setDataSource(dataSource);
	return bean;
}

@Bean(name = "freeCertificateCourseDAO" )
public FreeCertificateCourseDAO freeCertificateCourseDAO(BasicDataSource dataSource) {
	FreeCertificateCourseDAO bean = new FreeCertificateCourseDAO();
	bean.setDataSource(dataSource);
	return bean;
}





	
	@Bean(name="multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver bean = new CommonsMultipartResolver();
		bean.setMaxUploadSize(81485760);
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
	public ResponseBean responseBean() {
		ResponseBean bean = new ResponseBean();
		return bean;
	}
	
	

	@Bean(name="testDao")
	public TestDAO testDao(BasicDataSource dataSource) {
		TestDAO bean = new TestDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name="mbaxIADao")
	public MBAXIADAO mbaxIADao(BasicDataSource dataSource) {
		MBAXIADAO bean = new MBAXIADAO();
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

	@Bean(name = "redisResultsDAO" )
	public RedisResultsDAO redisResultsDAO(BasicDataSource dataSource) {
		RedisResultsDAO bean = new RedisResultsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean
	public RemarkResultsDisplayStrategyInterface remarkResultsDisplayStrategy() {
		return new RemarkResultsDisplayStrategy();
	}
	
//	@Bean(name="mettlHelper")
//	@Primary
//	public MettlHelper defaultMettlHelper(BasicDataSource dataSource) {
//		MettlHelper bean = new MettlHelper();
//		return bean;
//	}
	
	@Bean(name="mscMettlHelper")
	public MettlHelper mscMettlHelper(BasicDataSource dataSource) {
		MettlHelper bean = new MettlHelper();
		bean.setBaseUrl(MSC_METTL_BASE_URL);
		bean.setPrivateKey(MSC_METTL_PRIVATE_KEY);
		bean.setPublicKey(MSC_METTL_PUBLIC_KEY);
		return bean;
	}
	
	@Bean(name="mbaWxMettlHelper")
	public MettlHelper mbaWxMettlHelper(BasicDataSource dataSource) {
		MettlHelper bean = new MettlHelper();
		bean.setBaseUrl(MettlBaseUrl);
		bean.setPrivateKey(MettlPrivateKey);
		bean.setPublicKey(MettlPublicKey);
		return bean;
	}
	 
	@Bean(name="mbaxMettlHelper")
	public MettlHelper mbaxMettlHelper(BasicDataSource dataSource) {
		MettlHelper bean = new MettlHelper();
		bean.setBaseUrl(MBA_X_METTL_CHILD_BASE_URL);
		bean.setPrivateKey(MBA_X_METTL_CHILD_PRIVATE_KEY);
		bean.setPublicKey(MBA_X_METTL_CHILD_PUBLIC_KEY);
		return bean;
	}
	
	@Bean(name="pddmMettlHelper")
	public MettlHelper pddmMettlHelper(BasicDataSource dataSource) {
		MettlHelper bean = new MettlHelper();
		bean.setBaseUrl(MettlBaseUrl);
		bean.setPrivateKey(MODULAR_PDDM_METTL_PRIVATE_KEY);
		bean.setPublicKey(MODULAR_PDDM_METTL_PUBLIC_KEY);
		return bean;
	}
	
	@Bean(name = "levelBasedProjectHelper" )
	public LevelBasedProjectHelper levelBasedProjectHelper(BasicDataSource dataSource) {
		LevelBasedProjectHelper bean = new LevelBasedProjectHelper();
		return bean;
	}
	@Bean(name = "levelBasedProjectDAO" )
	public LevelBasedProjectDAO levelBasedProjectDAO(BasicDataSource dataSource) {
		LevelBasedProjectDAO bean = new LevelBasedProjectDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	@Bean(name = "projectTitleDAO" )
	public ProjectTitleDAO projectTitleDAO(BasicDataSource dataSource) {
		ProjectTitleDAO bean = new ProjectTitleDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

//	
//	@Bean(name="marshaller")
//	public Jaxb2Marshaller marshaller() {
//		Jaxb2Marshaller bean = new Jaxb2Marshaller();
//		bean.setContextPath("bookingservice.wsdl");
//		return bean;
//	}
//	
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
	
	@Bean
	public StudentRepository studentRepository() {
		return new StudentRepository();
	}
	
	@Bean(name = "mbaxAuditTrailsDAO" )
	public MBAXAuditTrailsDAO mbaxAuditTrailsDAO(BasicDataSource dataSource) {
		MBAXAuditTrailsDAO bean = new MBAXAuditTrailsDAO();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "currencyDAO" )
	public CurrencyDAO currencyDAO(BasicDataSource dataSource) {
		CurrencyDAO bean = new CurrencyDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name = "exitSrMdmDao" )
	public ExitSrMdmDao exitSrMdmDao(BasicDataSource dataSource) {
		ExitSrMdmDao bean = new ExitSrMdmDao();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(name = "blockCenterDAO" )
	public BlockStudentExamCenterDAO blockCenterDAO(BasicDataSource dataSource) {
		BlockStudentExamCenterDAO bean = new BlockStudentExamCenterDAO();
		bean.setDataSource(dataSource);
		return bean;
	}

	@Bean(name = "studentTestDAO" )
	public StudentTestDAO studentTestDAO(BasicDataSource dataSource) {
		StudentTestDAO dao = new StudentTestDAO();
		dao.setDataSource(dataSource);
		return dao;
	}
	
	@Bean(name = "examResultChecklistDao" )
	public ExamResultChecklistDao examResultChecklistDao(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
		ExamResultChecklistDao bean = new ExamResultChecklistDao();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}
	

	@Bean(name = "dissertationQ7DAO")
	public DissertationQ7DAO DissertationQ7DAO(BasicDataSource dataSource) {
		DissertationQ7DAO dao= new DissertationQ7DAO();
		dao.setDataSource(dataSource);
		return dao;
}

	@Bean(name = "passFailTransferDao" )
	public PassFailTransferDao passFailTransferDao(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
		PassFailTransferDao bean = new PassFailTransferDao();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}
	
	/**
	 * Executor bean to manage async tasks
	 */
	@Bean(name = "makeLiveAsyncExecutor")
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(EXECUTOR_BEAN_CORE_SIZE);
		executor.setMaxPoolSize(EXECUTOR_BEAN_MAX_POOL_SIZE);
		executor.setQueueCapacity(EXECUTOR_BEAN_QUEUE_CAPACITY);
		executor.setThreadNamePrefix(EXECUTOR_BEAN_THREAD_NAME_PREFIX);
		executor.initialize();
		return executor;
	}
	
	@Bean(name= "dissertationQ8ResultDaoImpl")
	public DissertationQ8ResultDaoImpl dissertationQ8ResultDaoImpl(BasicDataSource dataSource) {
		DissertationQ8ResultDaoImpl dao = new DissertationQ8ResultDaoImpl();
		dao.setDataSource(dataSource);
		return dao;
	}
	

	@Bean(name="divisionDetailsDao")
	public DivisionDetailsDao divisionDetailsDao(BasicDataSource dataSource) {
		DivisionDetailsDao dao=new DivisionDetailsDao();
		dao.setDataSource(dataSource);
		return dao;
	}

}