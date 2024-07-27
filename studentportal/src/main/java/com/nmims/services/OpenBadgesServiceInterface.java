package com.nmims.services;

import java.math.BigInteger;
import java.util.List;

import com.nmims.beans.OpenBadgeBean;
import com.nmims.beans.OpenBadgesEvidenceBean;
import com.nmims.beans.OpenBadgesIssuedBean;
import com.nmims.beans.OpenBadgesUsersBean;


public interface OpenBadgesServiceInterface {

	public abstract OpenBadgesUsersBean getMyBadgeList(String sapid, Integer masterKey);
	public abstract OpenBadgesIssuedBean getBadgesDetails(String uniquehash, Integer badgeId, String sapid, String awardedAt) throws Exception;
	public abstract void claimedMyBadge(String uniquehash) throws Exception;
	public abstract void revokedMyBadge(String uniquehash) throws Exception;
	public abstract void reclaimedRevokedMyBadge(String uniquehash) throws Exception;
	public abstract OpenBadgesUsersBean getPublicBadgesDetails(String uniquehash) throws Exception;
	public abstract OpenBadgesUsersBean getDashboardBadgeList(String sapid, Integer CPSId);
}
