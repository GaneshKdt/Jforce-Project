package com.nmims.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DatabaseConfig {
	
	@Value("${mysql.datasource.driver-class-name}")
	String MYSQL_DATASOURCE_CLASS;
	
	@Value("${mysql.datasource.url}")
	String MYSQL_DATASOURCE_URL;
	
	@Value("${mysql.datasource.username}")
	String MYSQL_DATASOURCE_USERNAME;
	
	@Value("${mysql.datasource.password}")
	String MYSQL_DATASOURCE_PASSWORD;

	@Primary @Bean(name="dataSource", destroyMethod="close")
	public BasicDataSource dataSource() {
		
		BasicDataSource bean = new BasicDataSource();
		bean.setDriverClassName(MYSQL_DATASOURCE_CLASS);
		bean.setUrl(MYSQL_DATASOURCE_URL + "/exam");
		bean.setUsername(MYSQL_DATASOURCE_USERNAME);
		bean.setPassword(MYSQL_DATASOURCE_PASSWORD);
		bean.setTestOnBorrow(true);
		bean.setTestOnReturn(true);
		bean.setTestWhileIdle(true);
		bean.setTimeBetweenEvictionRunsMillis(1800000);
		bean.setNumTestsPerEvictionRun(10);
		bean.setMinEvictableIdleTimeMillis(1800000);
		bean.setValidationQuery("SELECT 1");
		bean.setInitialSize(50);
		bean.setMaxIdle(50);
		bean.setMaxTotal(-1);
		return bean;		
	}
	
	@Bean(name="slave1", destroyMethod="close")
	public BasicDataSource slave1() {
		
		BasicDataSource bean = new BasicDataSource();
		bean.setDriverClassName(MYSQL_DATASOURCE_CLASS);
		bean.setUrl(MYSQL_DATASOURCE_URL + "/exam");
		bean.setUsername(MYSQL_DATASOURCE_USERNAME);
		bean.setPassword(MYSQL_DATASOURCE_PASSWORD);
		bean.setTestOnBorrow(true);
		bean.setTestOnReturn(true);
		bean.setTestWhileIdle(true);
		bean.setTimeBetweenEvictionRunsMillis(1800000);
		bean.setNumTestsPerEvictionRun(10);
		bean.setMinEvictableIdleTimeMillis(1800000);
		bean.setValidationQuery("SELECT 1");
		bean.setInitialSize(50);
		bean.setMaxIdle(50);
		bean.setMaxTotal(-1);
		return bean;		
	}
		
}
