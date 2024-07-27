package com.nmims.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.nmims.beans.OpenBadgesUsersBean;
import com.nmims.daos.OpenBadgesDAO;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class OpenBadgesServiceTest {

	OpenBadgesUsersBean badgeBean = new OpenBadgesUsersBean();
	String sapid = "77121843333";
	Integer CPSId = 85;
	Integer userId = 113195;

	List<OpenBadgesUsersBean> list = new ArrayList<>();	

	@InjectMocks
	private OpenBadgesService underTest;

	@Mock
	private OpenBadgesDAO dao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

//	@Ignore
	@Test
	public void testGetDashboardBadgeListForNotNull() {
		list.add(badgeBean);
		assertNotNull(underTest.getDashboardBadgeList(this.sapid, this.CPSId));
	}

//	@Ignore
	@Test
	public void testGetDashboardBadgeListCompareResultObjects() {
		Mockito.when(dao.getBadgeUserId(sapid, CPSId)).thenReturn(userId);
		Mockito.when(dao.getDashboardBadgeList(userId)).thenReturn(list);
		assertThat(underTest.getDashboardBadgeList(sapid, CPSId)).isEqualToComparingFieldByField(badgeBean);
	}
	
//	@Ignore
	@Test
	public void testGetDashboardBadgeListWhenListSizeIsGreaterThanZero() {
		List<OpenBadgesUsersBean> earnedBadgeList = new ArrayList<>();

		earnedBadgeList.add(new OpenBadgesUsersBean());
		list.add(badgeBean);
		OpenBadgesUsersBean badgeBeanResponse = new OpenBadgesUsersBean();
		badgeBeanResponse.setEarnedBadgeList(earnedBadgeList);
		Mockito.when(dao.getBadgeUserId(sapid, CPSId)).thenReturn(userId);
		Mockito.when(dao.getDashboardBadgeList(userId)).thenReturn(list);
		System.err.println("TEST CASE: " + badgeBeanResponse);

		assertThat(underTest.getDashboardBadgeList(sapid, CPSId)).isEqualToComparingFieldByFieldRecursively(badgeBeanResponse);
	}	
}
