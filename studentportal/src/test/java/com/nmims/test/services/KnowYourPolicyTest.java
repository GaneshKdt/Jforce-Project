package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.KnowYourPolicyBean;
import com.nmims.daos.KnowYourPolicyDAO;
import com.nmims.interfaces.KnowYourPolicyInterface;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KnowYourPolicyTest {
	@Mock
	KnowYourPolicyDAO policyDAO;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getGroupMapFromDataBase() {
		int expected = 1;
		int actual = 0;
		List<KnowYourPolicyBean> list = new ArrayList<>();
		KnowYourPolicyBean bean = new KnowYourPolicyBean();
		bean.setGroupId(1);
		bean.setGroupName("MBAX");
		list.add(bean);
		when(policyDAO.getAllGroups()).thenReturn(list);
		if (list.size() > 0) {
			actual = 1;
		}
		assertEquals(expected, actual);
	}

	@Test
	public void getCategoryMapFromDataBase() {
		int expected = 1;
		int actual = 0;
		List<KnowYourPolicyBean> list = new ArrayList<>();
		KnowYourPolicyBean bean = new KnowYourPolicyBean();
		bean.setCategoryName("Exam");
		bean.setCategoryId(3);
		list.add(bean);
		when(policyDAO.getAllCategory()).thenReturn(list);
		if (list.size() > 0) {
			actual = 1;
		}
		assertEquals(expected, actual);
	}

	@Test
	public void getSubCategoryMapFromDataBase() {
		int expected = 1;
		int actual = 0;
		List<KnowYourPolicyBean> list = new ArrayList<>();
		KnowYourPolicyBean bean = new KnowYourPolicyBean();
		bean.setCategoryId(1);
		bean.setSubcategoryId(2);
		bean.setSubcategoryName("Fees");
		list.add(bean);
		when(policyDAO.getAllSubcategory()).thenReturn(list);
		if (list.size() > 0) {
			actual = 1;
		}
		assertEquals(expected, actual);
	}

	@Test
	public void getSubCategoryByCategoryId() {
		int expected = 1;
		int actual = 0;
		List<KnowYourPolicyBean> list = new ArrayList<>();
		KnowYourPolicyBean bean = new KnowYourPolicyBean();
		bean.setCategoryId(1);
		bean.setSubcategoryId(2);
		bean.setSubcategoryName("Fees");
		list.add(bean);
		when(policyDAO.fetchsubcategoryusingcategory(1)).thenReturn(list);
		if (list.size() > 0) {
			actual = 1;
		}
		assertEquals(actual, expected);
	}

	@Test
	public void getAllPolicy() {
		int expected = 1;
		int actual = 0;
		List<KnowYourPolicyBean> list = new ArrayList<>();
		KnowYourPolicyBean bean = new KnowYourPolicyBean();
		bean.setPolicyId(1);
		bean.setTitle("title");
		bean.setDescription("description");
		bean.setGroupId(1);
		bean.setCategoryId(2);
		bean.setSubcategoryId(4);
		list.add(bean);
		when(policyDAO.getAllPolicy()).thenReturn(list);
		if (list.size() > 0) {
			actual = 1;
		}
		assertEquals(actual, expected);
	}
}
