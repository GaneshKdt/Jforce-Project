package com.nmims.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.nmims.services.MaxmindService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MaxmindServiceTest {
	
	@Autowired
	MaxmindService maxmindservice;
	
	@Test
	public void checkIp1()
	{
		String expected="false";
		String actual="false";
		try 
		{
			String ip="103.175.175.93";
			maxmindservice.getIpDetails(ip);
		}
		catch(Exception e)
		{
			actual="true";
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void checkIp2()
	{
		String expected="false";
		String actual="false";
		try 
		{
			String ip="128.101.101.101";
			maxmindservice.getIpDetails(ip);
		}
		catch(Exception e)
		{
			actual="true";
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void checkIp3()
	{
		String expected="false";
		String actual="false";
		try 
		{
			String ip="128";
			maxmindservice.getIpDetails(ip);
		}
		catch(Exception e)
		{
			actual="true";
		}
		assertEquals(expected, actual);
	}
	
}
