package com.nmims.coursera.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

@Configuration
@EnableAutoConfiguration
@ComponentScan({ "com.nmims.coursera.*" })
public class DatabaseConfig 
{
	 	@Autowired
	    private Environment environment;
	 	
	 	@Value("${mysql.datasource.driver-class-name}")
		String MYSQL_DATASOURCE_CLASS;
		
		@Value("${mysql.datasource.url}")
		String MYSQL_DATASOURCE_URL;
		
		@Value("${mysql.datasource.username}")
		String MYSQL_DATASOURCE_USERNAME;
		
		@Value("${mysql.datasource.password}")
		String MYSQL_DATASOURCE_PASSWORD;

	 	@Bean(name = "dataSource")
	 	public DataSource getDataSource() {
	 	    BasicDataSource dataSource = new BasicDataSource();
	 	    dataSource.setDriverClassName(MYSQL_DATASOURCE_CLASS);
	        dataSource.setUrl(MYSQL_DATASOURCE_URL);
	        dataSource.setUsername(MYSQL_DATASOURCE_USERNAME);
	        dataSource.setPassword(MYSQL_DATASOURCE_PASSWORD);
	 	 
	 	    return dataSource;
	 	}
	 	 
	 	 
	 	@Autowired
	 	@Bean(name = "sessionFactory")
	 	public SessionFactory getSessionFactory(DataSource dataSource) {
	 	   LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
	 	   sessionBuilder.scanPackages("com.nmims.coursera.*");
	 	   sessionBuilder.addProperties(getHibernateProperties());
	 	   
	 	   return sessionBuilder.buildSessionFactory();
	 	}
	 	
	 	
	 	private Properties getHibernateProperties() {
	 	    Properties properties = new Properties();
	 	    properties.put("hibernate.show_sql", "true");
	 	    properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
	 	   
	 	    return properties;
	 	}
	 	
	 	
	 	@Autowired
	 	@Bean(name = "transactionManager")
	 	public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
	 	    HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
	 	    
	 	    return transactionManager;
	 	}

	    
}
