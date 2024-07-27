package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.UserAuthorizationStudentPortalBean;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.PortalDao;
import com.nmims.helpers.PersonStudentPortalBean;
 import com.nmims.services.AuthorizationServiceImpl;
 import com.nmims.services.LDAPServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ViewAuthorizationTest {

	@InjectMocks
	LDAPServiceImpl lDAPService;

	@InjectMocks
	AuthorizationServiceImpl authorizationService;

	@Mock
	LDAPDao lDAPDao;
	
	@Mock
	PortalDao pDao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this); // without this you will get NPE
	}

	
	 @Test 
	 public void findPersonTest(){ 
		 Integer expected=1; 
		 Integer actual=0;

	  PersonStudentPortalBean person = new PersonStudentPortalBean();
	  person.setUserId("77122212410");
	  when(lDAPDao.findPerson("77122212410")).thenReturn(person);
	  PersonStudentPortalBean person1 = lDAPService.findPerson("77122212410");
	  
 
	  if(!person1.getUserId().isEmpty()) {
		  
		  actual = 1;
    	  }
	  assertEquals(expected, actual);
	 }

	@Test
	public void getUserAuthorizationTest() {

		UserAuthorizationStudentPortalBean expectedUserAuthorization = new UserAuthorizationStudentPortalBean();
		expectedUserAuthorization.setUserId("77777777771");
		
		when(pDao.getUserAuthorization("77777777771")).thenReturn(expectedUserAuthorization);
		UserAuthorizationStudentPortalBean actualUserAuthorization = authorizationService.getUserAuthorization("77777777771");
		  assertEquals(expectedUserAuthorization, actualUserAuthorization);

	}

}
