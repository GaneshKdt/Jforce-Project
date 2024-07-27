package com.nmims.services;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nmims.beans.ReRegistrationStudentPortalBean;
import com.nmims.beans.StudentOpportunity;
import com.nmims.enums.StudentPaymentStage;
import com.nmims.enums.StudentTermCleared;
import com.nmims.exceptions.StudentNotFoundException;
import com.nmims.helpers.DateTimeHelper;
import com.nmims.helpers.SalesforceHelper;

@Service("studentReRegistrationService")
public class StudentReRegistrationService implements IStudentReRegistrationService {

	private static final int[] STUDENT_TERM_CLEARED = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	private static final String PRE_LINK = "https://ngasce.secure.force.com/nmLogin_new?studentNo=";
	private static final String DOB_LINK = "&dob=";
	private static final String POST_LINK = "&type=reregistration";
	private static final String default_format = "yyyy-MM-dd";

	private static final Logger logger = LoggerFactory.getLogger("studentReRegistrationService");
	
	@Autowired
	private SalesforceHelper salesforceHelper;

	@Override
	public String getReRegistrationPaymentLink(String sapId, String dob) throws Exception {

		logger.info("Entered getReRegistrationPaymentLink() method of StudentReRegistrationService");
		String link = "";

		// Get student Re-Registration applicable details
		ReRegistrationStudentPortalBean reRegStdBean = this.getStudentDetailsForAutoRegister(sapId);

		dob = DateTimeHelper.getDateInFormat(default_format, dob);

		if (reRegStdBean.isTermCleared() && reRegStdBean.isPaymentApplicable()) {
			link = PRE_LINK + sapId + DOB_LINK + dob + POST_LINK;
		}

		logger.info("Exiting getReRegistrationPaymentLink() method of StudentReRegistrationService");
		return link;
	}

	/**
	 * Get student Re-Registration applicable details like Term-4 cleared status and
	 * Is payment done for Term-5.
	 * 
	 * @param sapId - Student Number[sapId] for which Re-Registration applicable
	 *              details need to get.
	 * @return ReRegistrationStudentPortalBean - Bean contains all Re-Registration
	 *         applicable details of a student.
	 * @throws StudentNotFoundException If opportunities not found on the salesforce
	 *                                  platform.
	 * @throws Exception                If establishing the connection fails with
	 *                                  Salesforce
	 */

	private ReRegistrationStudentPortalBean getStudentDetailsForAutoRegister(String sapId)
			throws StudentNotFoundException, Exception {
		logger.info("Entered getStudentDetailsForAutoRegister() method of StudentReRegistrationService");

		// Step 1: Get all student opportunities based on student number[sapId].
		List<StudentOpportunity> stdOpportunitiesList = salesforceHelper.getStudentOpportunities(sapId);

		// Create Re-Registration student bean object
		ReRegistrationStudentPortalBean reRegStdBean = new ReRegistrationStudentPortalBean();

		// Check for term clearance and payment status
		for (StudentOpportunity stdOpptDetls : stdOpportunitiesList) {

			if (!reRegStdBean.isTermCleared() && stdOpptDetls.getSemester().equals(STUDENT_TERM_CLEARED[3])
					&& StringUtils.equalsIgnoreCase(stdOpptDetls.getTermCleared(),
							StudentTermCleared.YES.getStudentTermCleared())
					&& StringUtils.equalsIgnoreCase(stdOpptDetls.getStageName(),
							StudentPaymentStage.PENDING_PAYMENT.getStudentPaymentStage())) {
				reRegStdBean.setTermCleared(true);
				reRegStdBean.setPaymentApplicable(true);
			} else if (!reRegStdBean.isTermCleared() && stdOpptDetls.getSemester().equals(STUDENT_TERM_CLEARED[4])
					&& StringUtils.equalsIgnoreCase(stdOpptDetls.getTermCleared(),
							StudentTermCleared.YES.getStudentTermCleared())
					&& StringUtils.equalsIgnoreCase(stdOpptDetls.getStageName(),
							StudentPaymentStage.PENDING_PAYMENT.getStudentPaymentStage())) {
				reRegStdBean.setTermCleared(true);
				reRegStdBean.setPaymentApplicable(true);
			}
		}

		// return Re-Registration bean having student Re-Registration applicable details
		logger.info("Exiting getStudentDetailsForAutoRegister() method of StudentReRegistrationService");
		return reRegStdBean;
	}
}
