package com.nmims.interfaces;

public interface EarnedBadgesNotificationInterface {
	
	void sendEmail(String sapId, Integer badgeId, Long issuedId, String awardedAt);
}
