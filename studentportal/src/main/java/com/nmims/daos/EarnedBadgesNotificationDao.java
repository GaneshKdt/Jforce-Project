package com.nmims.daos;

import org.springframework.stereotype.Component;

import com.nmims.beans.OpenBadgeBean;
import com.nmims.beans.StudentStudentPortalBean;

@Component
public interface EarnedBadgesNotificationDao {
	
	void updateEarnedBadgesNotificationStatus(String status, Long issuedId);

	OpenBadgeBean getBadgeDetailsByBadgeId(Integer badgeId);

	Integer getCriteriaTypeByAwardedAt(String awardedAt);

	Integer getSemesterBySapIdAndAwardedAt(String sapId, String subject);

	StudentStudentPortalBean getRegistrationBySapIdAndSemester(String sapId, Integer semester);

	Integer getSemesterByBadgeIdAndSapId(Integer badgeId);
}
