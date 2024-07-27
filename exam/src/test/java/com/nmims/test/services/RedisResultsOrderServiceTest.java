/**
 * 
 */
package com.nmims.test.services;

import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.RedisStudentMarksBean;
import com.nmims.services.RedisResultsOrderService;

/**
 * @author vil_m
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisResultsOrderServiceTest {
	
	@Autowired
	private RedisResultsOrderService redisResultsOrderService;

	@Test
	public void testNull_Marks() {
		// Input: null List, Output: null List.

		List<RedisStudentMarksBean> list2 = null;
		List<RedisStudentMarksBean> list = null;

		list2 = redisResultsOrderService.orderMarksList(list);

		assertNull("testNull_Marks > Output List must be Null", list2);
	}
}
