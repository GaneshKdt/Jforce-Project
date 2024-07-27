package com.nmims.test.services;

 import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
 
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.daos.LDAPDao;
import com.nmims.daos.PortalDao;
import com.nmims.services.AuthorizationServiceImpl;
import com.nmims.services.LDAPServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class updateAuthorizationTest {

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
		public void testUpdateRolesInLdap() throws Exception {
			// Prepare test data
			String userId = "77777777771";
			String roles = "Faculty";
			
			// Call the method under test
			authorizationService.updateRolesInLdap(userId, roles);
			
			// Verify the method calls
			verify(lDAPDao,times(1)).updateRolesLdapAttribute(userId, roles);
 		}

	 @Test
		public void testupdateRolesInAuthorizationTable() throws Exception {
			// Prepare test data
			String userId = "77777777771";
			String roles = "Faculty";
			
			// Call the method under test
			authorizationService.updateRolesInAuthorizationTable(userId, roles);
			
			// Verify the method calls
		    verify(pDao,times(1)).updateAuthorizationTable(userId,roles);
		}
}
