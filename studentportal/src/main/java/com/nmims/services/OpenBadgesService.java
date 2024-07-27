package com.nmims.services;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nmims.beans.AnnouncementMasterBean;
import com.nmims.beans.OpenBadgeBean;
import com.nmims.beans.OpenBadgeLectureAttendanceBean;
import com.nmims.beans.OpenBadgesAppearedForTEEBean;
import com.nmims.beans.OpenBadgesCriteriaBean;
import com.nmims.beans.OpenBadgesCriteriaParamBean;
import com.nmims.beans.OpenBadgesEvidenceBean;
import com.nmims.beans.OpenBadgesIssuedBean;
import com.nmims.beans.OpenBadgesTopInAssignmentDto;
import com.nmims.beans.OpenBadgesUsersBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.daos.OpenBadgesDAO;
import com.nmims.dto.BadgeKeywordsDto;
import com.nmims.dto.OpenBadgesForReRegSemDto;
import com.nmims.dto.OpenBadgesPortalVisitStreakDto;
import com.nmims.dto.OpenBadgesTopInProgramDto;
import com.nmims.dto.OpenBadgesTopInSemesterDto;
import com.nmims.dto.OpenBadgesTopInTEEDto;
import com.nmims.dto.OpenBadgesTopInsubjectDto;
import com.nmims.interfaces.EarnedBadgesNotificationInterface;



@Service("openBadgesService")
public class OpenBadgesService implements OpenBadgesServiceInterface {
	

	private static final Logger logger = LoggerFactory.getLogger("badgeScheduler");

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;
	
	@Autowired
	private OpenBadgesDAO openBadgesDAO;
	
	@Autowired
	private CertificateService certificateService;
	
	@Autowired
	private EarnedBadgesNotificationInterface earnedBadgesNotificationService;

	private static final Logger alumni_reReg_badge = LoggerFactory.getLogger("alumni_reReg_badge");
	
	private static final Integer FIFTEEN_MINUTES = 900000;
	
	@Override
	public OpenBadgesUsersBean getMyBadgeList(String sapid, Integer consumerProgramStructureId) {
		 OpenBadgesUsersBean usersBean =  new OpenBadgesUsersBean();
		try {
			Integer userId = getBadgeUserId(sapid, consumerProgramStructureId);
			List<OpenBadgeBean> openBadgeBeanList = openBadgesDAO.getAllBadgesList(consumerProgramStructureId);

			if(openBadgeBeanList.size() > 0) {
				if(userId == 0) {
					List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
					usersBean.setEarnedBadgeList(list);
					usersBean.setLockedBadgeList (getLockedBadgeList(openBadgeBeanList, userId, sapid) );
					
					return usersBean;
				}else {
				
					usersBean.setEarnedBadgeList( getIssuedBadgeList(userId) );
					usersBean.setClaimedBadgeList( getClaimedBadgeList( userId) );
					usersBean.setRevokedBadgeList( getRevokedBadgeList( userId) );
					usersBean.setLockedBadgeList ( getLockedBadgeList(openBadgeBeanList, userId, sapid) );
					
					return usersBean;	
				}
			}
			return usersBean;
			
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
			return usersBean;
		}
	}
	
	private Integer getBadgeUserId(String sapid, Integer consumerProgramStructureId) {
		Integer userId = 0;
		try {
			userId = openBadgesDAO.getBadgeUserId(sapid, consumerProgramStructureId);
			return userId;
		} catch (Exception e) {
			// TODO: handle exception
			return userId;
		}
	}
	
	private String getSubjectCode(String str) {
		 String acronym = "";
	        acronym += str.toUpperCase().charAt(0);

	        for (int i = 1; i <= str.length() - 1; i++) {
	            if (str.charAt(i - 1) == ' ') {
	                acronym += str.toUpperCase().charAt(i);
	            }
	        }
	        return acronym;
	    }


	
	private  List<OpenBadgesUsersBean> getIssuedBadgeList(Integer userId) {
		 List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		 try {
			 list = openBadgesDAO.getIssuedBadgeList(userId);
			 
			 for(OpenBadgesUsersBean bean : list ) {
				 try {
					 OpenBadgesCriteriaBean criteriaBean = openBadgesDAO.getCriteriaDetailsByBadgeId(bean.getBadgeId());	
					 bean.setCriteriatype(criteriaBean.getCriteriatype());
					 if( criteriaBean.getCriteriatype() == 1 ) {
							OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(criteriaBean.getCriteriaId());
						 if("topInSubject".equals(criteriaDetail.getCriteriaName())) {
								bean.setAwardedAtCode( bean.getBadgeName());
						}else if("topInTEE".equals(criteriaDetail.getCriteriaName())){
							bean.setAwardedAtCode(bean.getBadgeName());
						}else if(bean.getAwardedAt().length() > 35 ){
							bean.setAwardedAtCode( getSubjectCode(bean.getAwardedAt()));
						}else {
							 bean.setAwardedAtCode(bean.getAwardedAt());
						 }
					 }else if (criteriaBean.getCriteriatype() == 2) {
						 OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(criteriaBean.getCriteriaId());
							if(criteriaDetail.getCriteriaName().equals("ngascealumni")) {
									bean.setAwardedAtCode("NMIMS Global - Alumni");
							}else if(criteriaDetail.getCriteriaName().equals("programCompletion")) {
								bean.setAwardedAtCode("Program Completion");
							}else if(criteriaDetail.getCriteriaName().equals("topInProgram")) {
								bean.setAwardedAtCode(bean.getBadgeName());
							}
					 }else if(criteriaBean.getCriteriatype() == 3) {
							OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(criteriaBean.getCriteriaId());
							if("topInSemester".equals(criteriaDetail.getCriteriaName())) {
								bean.setAwardedAtCode( bean.getBadgeName());
							}
							if("Re-RegisteredForSem".equals(criteriaDetail.getCriteriaName())) {
								bean.setAwardedAtCode( bean.getBadgeName());
							}
							if("portalVisitStreak".equals(criteriaDetail.getCriteriaName())) {
								bean.setAwardedAtCode( "Visit Streak - " +criteriaDetail.getCriteriaValue()+ " Days");
							}
					} else {
						 bean.setAwardedAtCode(bean.getAwardedAt());

					 }
					 
				 }catch (Exception e) {
					// TODO: handle exception
				}
			 }
			 
			 return list;
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
			return list;
		}
	}

	private  List<OpenBadgesUsersBean> getClaimedBadgeList(Integer userId) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		try {
			list = openBadgesDAO.getClaimedBadgeList(userId);
			
			for(OpenBadgesUsersBean bean : list ) {
				 try {
			 OpenBadgesCriteriaBean criteriaBean = openBadgesDAO.getCriteriaDetailsByBadgeId(bean.getBadgeId());
					 bean.setCriteriatype(criteriaBean.getCriteriatype());
					 if( criteriaBean.getCriteriatype() == 1 ) {
							OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(criteriaBean.getCriteriaId());
						 if("topInSubject".equals(criteriaDetail.getCriteriaName())) {
								bean.setAwardedAtCode( bean.getBadgeName());
						}else if("topInTEE".equals(criteriaDetail.getCriteriaName())){
							bean.setAwardedAtCode(bean.getBadgeName());
						}else if(bean.getAwardedAt().length() > 35 ){
							bean.setAwardedAtCode( getSubjectCode(bean.getAwardedAt()));
						}else {
							 bean.setAwardedAtCode(bean.getAwardedAt());
						 }
					 }else if(criteriaBean.getCriteriatype() == 2) {
							OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(criteriaBean.getCriteriaId());
							if(criteriaDetail.getCriteriaName().equals("ngascealumni")) {
								bean.setAwardedAtCode("NMIMS Global - Alumni");
							}else if(criteriaDetail.getCriteriaName().equals("programCompletion")) {
								bean.setAwardedAtCode("Program Completion");
							}else if(criteriaDetail.getCriteriaName().equals("topInProgram")) {
								bean.setAwardedAtCode(bean.getBadgeName());
							}
					}else if(criteriaBean.getCriteriatype() == 3) {
							OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(criteriaBean.getCriteriaId());
							if("topInSemester".equals(criteriaDetail.getCriteriaName())) {
								bean.setAwardedAtCode( bean.getBadgeName());
							}
							if("Re-RegisteredForSem".equals(criteriaDetail.getCriteriaName())) {
								bean.setAwardedAtCode( bean.getBadgeName());
							}
							if("portalVisitStreak".equals(criteriaDetail.getCriteriaName())) {
								bean.setAwardedAtCode( "Visit Streak - " +criteriaDetail.getCriteriaValue()+ " Days");
							}
					} else {
						 bean.setAwardedAtCode(bean.getAwardedAt());

					 }
					
				 }catch (Exception e) {
					// TODO: handle exception
				}
			 }
			
			return list;
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
			return list;
		}
	}

	private  List<OpenBadgesUsersBean> getRevokedBadgeList(Integer userId) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		try {
			list = openBadgesDAO.getRevokedBadgeList(userId);
			
			 for(OpenBadgesUsersBean bean : list ) {
				 try {
					 OpenBadgesCriteriaBean criteriaBean = openBadgesDAO.getCriteriaDetailsByBadgeId(bean.getBadgeId());
					 bean.setCriteriatype(criteriaBean.getCriteriatype());
					 if( criteriaBean.getCriteriatype() == 1 ) {
							OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(criteriaBean.getCriteriaId());
						 if("topInSubject".equals(criteriaDetail.getCriteriaName())) {
								bean.setAwardedAtCode( bean.getBadgeName());
						}else if("topInTEE".equals(criteriaDetail.getCriteriaName())){
							bean.setAwardedAtCode(bean.getBadgeName());
						}else if(bean.getAwardedAt().length() > 35 ){
							bean.setAwardedAtCode( getSubjectCode(bean.getAwardedAt()));
						}else {
							 bean.setAwardedAtCode(bean.getAwardedAt());
						 }
					 }else if(criteriaBean.getCriteriatype() == 2) {
							OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(criteriaBean.getCriteriaId());
							if(criteriaDetail.getCriteriaName().equals("ngascealumni")) {
								bean.setAwardedAtCode("NMIMS Global - Alumni");
							}else if(criteriaDetail.getCriteriaName().equals("programCompletion")) {
								bean.setAwardedAtCode("Program Completion");
							}else if(criteriaDetail.getCriteriaName().equals("topInProgram")) {
								bean.setAwardedAtCode(bean.getBadgeName());
							}
					}else if(criteriaBean.getCriteriatype() == 3) {
							OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(criteriaBean.getCriteriaId());
							if("topInSemester".equals(criteriaDetail.getCriteriaName())) {
								bean.setAwardedAtCode( bean.getBadgeName());
							}
							if("Re-RegisteredForSem".equals(criteriaDetail.getCriteriaName())) {
								bean.setAwardedAtCode( bean.getBadgeName());
							}
							if("portalVisitStreak".equals(criteriaDetail.getCriteriaName())) {
								bean.setAwardedAtCode( "Visit Streak - " +criteriaDetail.getCriteriaValue()+ " Days");
							}
					} else {
						 bean.setAwardedAtCode(bean.getAwardedAt());

					 }
					
				 }catch (Exception e) {
					// TODO: handle exception
				}
			 }
			
			
			return list;
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
			return list;
		}
	}
	
	private List<OpenBadgesUsersBean> getLockedBadgeList(List<OpenBadgeBean> badgeIdList, Integer userId, String sapid) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		for(OpenBadgeBean openBadgeBean : badgeIdList ) {
			try {

				OpenBadgesCriteriaBean criteriaBean = openBadgesDAO.getCriteriaDetailsByBadgeId(openBadgeBean.getBadgeId());	
		
				if(criteriaBean.getCriteriatype() == 1 ) {
					list.addAll( getSubjectLevelLockedBadges(openBadgeBean.getBadgeId(), sapid, userId, criteriaBean.getCriteriaId()) );
				} else if(criteriaBean.getCriteriatype() == 2) {

					OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(criteriaBean.getCriteriaId());
					if("programCompletion".equals(criteriaDetail.getCriteriaName())) {
						OpenBadgesUsersBean userBean = openBadgesDAO.getLockedBadge(userId, openBadgeBean.getBadgeId(), "Program Completion");
						if(userBean.getIsBadgeIssued() == 0) {
							userBean.setAwardedAt("Program Completion");
							userBean.setAwardedAtCode( userBean.getAwardedAt());
							list.add(userBean);
						}
					}else if(criteriaDetail.getCriteriaName().equals("ngascealumni")) {
						String programName = openBadgesDAO.getProgramNameByProgramId(criteriaDetail.getCriteriaValue());
						OpenBadgesUsersBean userBean = openBadgesDAO.getLockedBadge(userId, openBadgeBean.getBadgeId(),programName);
						if(userBean.getIsBadgeIssued() == 0) {
							userBean.setAwardedAt(programName);
							userBean.setAwardedAtCode("NMIMS Global - Alumni");
							list.add(userBean);
						}
					} else if("topInProgram".equals(criteriaDetail.getCriteriaName())) {
						boolean programCleared =  openBadgesDAO.programCleared(sapid);
						if(!programCleared) {
							String program = openBadgesDAO.getProgramNameBySapId(sapid);
							OpenBadgesUsersBean userBean = openBadgesDAO.getLockedBadge(userId, openBadgeBean.getBadgeId(), program );
							userBean.setCriteriatype(criteriaBean.getCriteriatype());
							if(userBean.getIsBadgeIssued() == 0) {
								userBean.setAwardedAt(program);
								userBean.setAwardedAtCode( userBean.getBadgeName());
								list.add(userBean);
							}
						}
					  }
				} else if(criteriaBean.getCriteriatype() == 3) {
					OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(criteriaBean.getCriteriaId());

						//BADGE FOR RE-REGISTRATION SEMESTER 
						if(criteriaDetail.getCriteriaName().equals("Re-RegisteredForSem"))
						{	
							
								String awardedAt = openBadgesDAO.getProgramNameBySem(sapid,Integer.valueOf(criteriaDetail.getCriteriaValue()));
								
								OpenBadgesUsersBean userBean = openBadgesDAO.getLockedBadge(userId, openBadgeBean.getBadgeId(), awardedAt);
								if(userBean.getIsBadgeIssued() == 0) {
									String programname = openBadgesDAO.getProgramName(sapid);
									userBean.setAwardedAt(programname);
									userBean.setAwardedAtCode(openBadgeBean.getBadgeName());
									setKeywordForReRegForSem(userBean, sapid,criteriaDetail.getCriteriaValue());
									list.add(userBean);
								
							}
						}
				 if("topInSemester".equals(criteriaDetail.getCriteriaName())) {
					Integer sem = openBadgesDAO.getMaxRegistration(sapid);
					boolean passfailExists =  openBadgesDAO.passfailExists(sapid, sem);
					if(!passfailExists) {
						OpenBadgesUsersBean userBean = openBadgesDAO.getLockedBadge(userId, openBadgeBean.getBadgeId(), "Semester "+sem);
						userBean.setCriteriatype(criteriaBean.getCriteriatype());
						if(userBean.getIsBadgeIssued() == 0) {
							userBean.setAwardedAt("Semester "+sem);
							userBean.setAwardedAtCode( userBean.getBadgeName());
							list.add(userBean);
						}
					}
				  }
				 
				 if("portalVisitStreak".equals(criteriaDetail.getCriteriaName())) {
					 	Integer sem = openBadgesDAO.getMaxRegistration(sapid);
						OpenBadgesUsersBean userBean = openBadgesDAO.getLockedBadge(userId, openBadgeBean.getBadgeId(), "Semester "+sem);
						
						userBean.setCriteriatype(criteriaBean.getCriteriatype());
						if(userBean.getIsBadgeIssued() == 0) {
							userBean.setAwardedAt(String.valueOf("Semester "+sem));
							userBean.setAwardedAtCode( "Visit Streak - " +criteriaDetail.getCriteriaValue()+ " Days");
							list.add(userBean);
						}
				  }

				} 
			} catch (Exception e) {
				// TODO: handle exception
//				e.printStackTrace();
			}
		}
		return list;
	}
	

	private List<OpenBadgesUsersBean> getSubjectLevelLockedBadges( Integer badgeId, String sapid, Integer userId, Integer criteriaId) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		List<OpenBadgesIssuedBean> applicableSubjectList = openBadgesDAO.getApplicableSubjectList(sapid);
		for(OpenBadgesIssuedBean openBadgesIssuedBean :applicableSubjectList) {
			String subject = openBadgesIssuedBean.getAwardedAt();
			OpenBadgesUsersBean userBean = openBadgesDAO.getLockedBadge(userId, badgeId, subject);
			userBean.setCriteriatype(1);
			if(userBean.getIsBadgeIssued() == 0) {
				Integer checkSubjectPass = openBadgesDAO.checkSubjectPass(sapid, subject);
				if(checkSubjectPass == 0) {
					userBean.setAwardedAt(subject);
					OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(criteriaId);
					if("topInSubject".equals(criteriaDetail.getCriteriaName())) {
						userBean.setAwardedAtCode( userBean.getBadgeName());
					}else if("topInTEE".equals(criteriaDetail.getCriteriaName())){
						userBean.setAwardedAtCode(userBean.getBadgeName());
					}else if(userBean.getAwardedAt().length() > 35  ) {
						userBean.setAwardedAtCode( getSubjectCode(userBean.getAwardedAt()));
					 }else {
						userBean.setAwardedAtCode( userBean.getAwardedAt());
					 }
					list.add(userBean);
				}
			}
		}
		
		return list;
	}
	

	@Override
	public OpenBadgesIssuedBean getBadgesDetails(String uniquehash, Integer badgeId,String sapid, String awardedAt) throws Exception {
		OpenBadgesIssuedBean openBadgesIssuedBean = new OpenBadgesIssuedBean();
		
		if(!"0".equals(uniquehash)) {
			openBadgesIssuedBean = openBadgesDAO.getIssuedBadgeDetailsById(uniquehash, sapid);	
			openBadgesIssuedBean.setOpenBadgesCriteriaParamBeanList(openBadgesDAO.getCriteriaParamList(openBadgesIssuedBean.getCriteriaId()));
			openBadgesIssuedBean.setEvidenceBeanList(openBadgesDAO.getEvidenceByIssuedId(uniquehash));
			
			if("topInAssignment".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
				setKeywordForTopinAssignment(openBadgesIssuedBean, sapid);
			}else if("topInSemester".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
				setKeywordForTopinSemester(openBadgesIssuedBean, sapid);
			}else if("Re-RegisteredForSem".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
				setKeywordForReRegForSem(openBadgesIssuedBean, sapid,openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaValue());
			}else if("topInProgram".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
				setKeywordForTopinProgram(openBadgesIssuedBean, sapid);
			}
			else if("topInSubject".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
				setKeywordForTopInSubject(openBadgesIssuedBean,sapid);
			}
			else if("topInTEE".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
				setKeywordForTopInTEE(openBadgesIssuedBean,sapid);
			}
			else if("portalVisitStreak".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
				setKeywordForStreak(openBadgesIssuedBean,sapid);
			}
			
			return openBadgesIssuedBean;
		}
		openBadgesIssuedBean = openBadgesDAO.getNotIssuedBadgeDetailsById(badgeId);

		openBadgesIssuedBean.setOpenBadgesCriteriaParamBeanList(openBadgesDAO.getCriteriaParamList(openBadgesIssuedBean.getCriteriaId()));
		openBadgesIssuedBean.setAwardedAt(awardedAt);
		if("topInAssignment".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
			setKeywordForTopinAssignment(openBadgesIssuedBean, sapid);
		}else if("topInSemester".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
			setKeywordForTopinSemester(openBadgesIssuedBean, sapid);
		}
		else if("Re-RegisteredForSem".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
			setKeywordForReRegForSem(openBadgesIssuedBean, sapid,openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaValue());
		}else if("topInProgram".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
			setKeywordForTopinProgram(openBadgesIssuedBean, sapid);
		}
		else if("topInSubject".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
			setKeywordForTopInSubject(openBadgesIssuedBean,sapid);
			
		}
		else if("topInTEE".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
			setKeywordForTopInTEE(openBadgesIssuedBean,sapid);
			
		}
		else if("portalVisitStreak".equals(openBadgesIssuedBean.getOpenBadgesCriteriaParamBeanList().get(0).getCriteriaName())) {
			setKeywordForStreak(openBadgesIssuedBean,sapid);
		}
		
		return openBadgesIssuedBean;
		
	}


	@Override
	public void claimedMyBadge(String uniquehash) throws Exception {
		openBadgesDAO.claimedMyBadge(uniquehash);	
	}



	@Override
	public void revokedMyBadge(String uniquehash) throws Exception {
		openBadgesDAO.revokedMyBadge(uniquehash);
	}
	
	@Override
	public void reclaimedRevokedMyBadge(String uniquehash) throws Exception {
		openBadgesDAO.reclaimedRevokedMyBadge(uniquehash);
	}



	@Override
	public OpenBadgesUsersBean getPublicBadgesDetails(String uniquehash) throws Exception {
		OpenBadgesUsersBean openBadgesUsersBean = new OpenBadgesUsersBean();
		openBadgesUsersBean = openBadgesDAO.getPublicBadgeDetails(uniquehash);
		openBadgesUsersBean.setEvidenceBeanList(openBadgesDAO.getEvidenceByIssuedId(uniquehash));
		OpenBadgesCriteriaParamBean criteriaDetail = openBadgesDAO.getCriteriaParamDetails(openBadgesUsersBean.getCriteriaId());
		if("topInAssignment".equals(criteriaDetail.getCriteriaName())) {
			setKeywordForTopinAssignment(openBadgesUsersBean, openBadgesUsersBean.getSapid());
		}else if("topInSemester".equals(criteriaDetail.getCriteriaName())) {
			setKeywordForTopinSemester(openBadgesUsersBean, openBadgesUsersBean.getSapid());
		}
		else if("Re-RegisteredForSem".equals(criteriaDetail.getCriteriaName())) {
			setKeywordForReRegForSem(openBadgesUsersBean, openBadgesUsersBean.getSapid(),criteriaDetail.getCriteriaValue());
		}else if("topInSubject".equals(criteriaDetail.getCriteriaName())) {
			setKeywordForTopInSubject(openBadgesUsersBean, openBadgesUsersBean.getSapid());
			
		}else if("topInTEE".equals(criteriaDetail.getCriteriaName())) {
			setKeywordForTopInTEE(openBadgesUsersBean,openBadgesUsersBean.getSapid());
		}
		else if("portalVisitStreak".equals(criteriaDetail.getCriteriaName())) {
			setKeywordForStreak(openBadgesUsersBean,openBadgesUsersBean.getSapid());
		}else if("topInProgram".equals(criteriaDetail.getCriteriaName())) {
			setKeywordForTopinProgram(openBadgesUsersBean, openBadgesUsersBean.getSapid());
		}
		return openBadgesUsersBean;
	}
	
	
	public String callAskQueryProcForAllStudent() {
		List<OpenBadgesUsersBean> openBadgesUsersBean = openBadgesDAO.getAskQueryList(1);
		System.out.println("callAskQueryProcForAllStudent >> openBadgesUsersBean size : "+openBadgesUsersBean.size());
		logger.info("OpenBadgesService >> call callAskQueryProcForAllStudent >> openBadgesUsersBean size : "+openBadgesUsersBean.size());
		Integer count = 0 ;
		Integer successCount = 0 ;
		Integer errorCount = 0 ;
		for(OpenBadgesUsersBean bean : openBadgesUsersBean) {
			try {

				count++;
				bean.setBadgeId(1);
				String inputMd5 = bean.getBadgeId()+bean.getAwardedAt()+bean.getUserId();
	    		String uniquehash = getMd5(inputMd5) ;
	    		bean.setUniquehash(uniquehash);
	    		bean.setCreatedBy("askQueryScheduler");
				bean.setLastModifiedBy("askQueryScheduler");
	    		Long issuedId = openBadgesDAO.insertBadgeIssued(bean);
		    	
	    		OpenBadgesEvidenceBean evidenceBean = new OpenBadgesEvidenceBean();
		    	evidenceBean.setCreatedBy("askQueryScheduler");
		    	evidenceBean.setLastModifiedBy("askQueryScheduler");
		    	
		    	evidenceBean = openBadgesDAO.getAskQueryEvidence(bean.getSapid(), bean.getAwardedAt());
		    	evidenceBean.setIssuedId(BigInteger.valueOf(issuedId));
		    	  
		    	openBadgesDAO.insertEvidence(evidenceBean);
				
				//System.out.println(count+" callAskQueryProcForAllStudent ");
				logger.info("OpenBadgesService >> call callAskQueryProcForAllStudent "+count);
				
				successCount++; 
				earnedBadgesNotificationService.sendEmail(bean.getSapid(), new Integer(1), issuedId, bean.getAwardedAt());
			}catch(Exception e) {
				//e.printStackTrace();
				//System.out.println(count+" callAskQueryProcForAllStudent >> error");
				logger.error("OpenBadgesService >> call callAskQueryProcForAllStudent >> error count "+count);
				logger.error("OpenBadgesService >> call callAskQueryProcForAllStudent >> error message "+e.getMessage());
				errorCount++;
			}
		}

		logger.info("OpenBadgesService >> callAskQueryProcForAllStudent Scheduler successCount "+successCount+" and  errorCount "+errorCount );
		return "callAskQueryProcForAllStudent Scheduler successCount "+successCount+" and  errorCount "+errorCount ;
	}

	public String callSubmitAssignmentProcForAllStudent() {
		List<OpenBadgesUsersBean> openBadgesUsersBean = openBadgesDAO.getAssignmentsubmissionList(2);
		System.out.println("callSubmitAssignmentProcForAllStudent >> openBadgesUsersBean size : "+openBadgesUsersBean.size());
		logger.info("OpenBadgesService >> call callSubmitAssignmentProcForAllStudent >> openBadgesUsersBean size : "+openBadgesUsersBean.size());
		Integer count = 0 ;
		Integer successCount = 0 ;
		Integer errorCount = 0 ;
		for(OpenBadgesUsersBean bean : openBadgesUsersBean) {
			try {
				count++;
				bean.setBadgeId(2);
				String inputMd5 = bean.getBadgeId()+bean.getAwardedAt()+bean.getUserId();
	    		String uniquehash = getMd5(inputMd5) ;
	    		bean.setUniquehash(uniquehash);
	    		bean.setCreatedBy("submitAssignmentScheduler");
				bean.setLastModifiedBy("submitAssignmentScheduler");
	    		Long issuedId = openBadgesDAO.insertBadgeIssued(bean);
				
				//System.out.println(count+" callSubmitAssignmentProcForAllStudent ");
				logger.info("OpenBadgesService >> call callSubmitAssignmentProcForAllStudent "+count);
				
				successCount++; 
				earnedBadgesNotificationService.sendEmail(bean.getSapid(), bean.getBadgeId(), issuedId, bean.getAwardedAt());
			}catch(Exception e) {
				//e.printStackTrace();
				//System.out.println(count+" callSubmitAssignmentProcForAllStudent >> error");
				logger.error("OpenBadgesService >> call callSubmitAssignmentProcForAllStudent >> error count "+count);
				logger.error("OpenBadgesService >> call callSubmitAssignmentProcForAllStudent >> error message "+e.getMessage());
				errorCount++;
			}
		} 
		callSubmitAssignmentEvidenceProcForAllStudent();
		logger.info("OpenBadgesService >> callSubmitAssignmentProcForAllStudent Scheduler successCount "+successCount+" and  errorCount "+errorCount );
		return "callSubmitAssignmentProcForAllStudent Scheduler successCount "+successCount+" and  errorCount "+errorCount ;
	}

	public void callSubmitAssignmentEvidenceProcForAllStudent() {
		List<OpenBadgesEvidenceBean> openBadgesEvidenceBean = openBadgesDAO.getAssignmentsubmissionEvidenceList(2);
		System.out.println("callSubmitAssignmentEvidenceProcForAllStudent >> openBadgesEvidenceBean size : "+openBadgesEvidenceBean.size());
		logger.info("OpenBadgesService >> call callSubmitAssignmentEvidenceProcForAllStudent >> openBadgesEvidenceBean size : "+openBadgesEvidenceBean.size());
		Integer count = 0 ;
		Integer successCount = 0 ;
		Integer errorCount = 0 ;
		for(OpenBadgesEvidenceBean evidenceBean : openBadgesEvidenceBean) {
			try {
				count++;
				
	    		evidenceBean.setCreatedBy("submitAssignmentEvidenceScheduler");
		    	evidenceBean.setLastModifiedBy("submitAssignmentEvidenceScheduler");
		    	
		    	openBadgesDAO.insertEvidence(evidenceBean);
				
				//System.out.println(count+" callSubmitAssignmentEvidenceProcForAllStudent ");
				logger.info("OpenBadgesService >> call callSubmitAssignmentEvidenceProcForAllStudent "+count);
				
				successCount++; 
				
			}catch(Exception e) {
				//e.printStackTrace();
				//System.out.println(count+" callSubmitAssignmentEvidenceProcForAllStudent >> error");
				logger.error("OpenBadgesService >> call callSubmitAssignmentEvidenceProcForAllStudent >> error count "+count);
				logger.error("OpenBadgesService >> call callSubmitAssignmentEvidenceProcForAllStudent >> error message "+e.getMessage());
				errorCount++;
			}
		} 
		
		logger.info("OpenBadgesService >> callSubmitAssignmentEvidenceProcForAllStudent Scheduler successCount "+successCount+" and  errorCount "+errorCount );
		
	}
	

	
	 private String getMd5(String input) 
	    { 
	        try { 
	  
	            // Static getInstance method is called with hashing MD5 
	            MessageDigest md = MessageDigest.getInstance("MD5"); 
	  
	            // digest() method is called to calculate message digest 
	            //  of an input digest() return array of byte 
	            byte[] messageDigest = md.digest(input.getBytes()); 
	  
	            // Convert byte array into signum representation 
	            BigInteger no = new BigInteger(1, messageDigest); 
	  
	            // Convert message digest into hex value 
	            String hashtext = no.toString(16); 
	            while (hashtext.length() < 32) { 
	                hashtext = "0" + hashtext; 
	            } 
	            return hashtext; 
	        }  
	  
	        // For specifying wrong message digest algorithms 
	        catch (NoSuchAlgorithmException e) { 
	            throw new RuntimeException(e); 
	        } 
	  }
	 
	
	 public String createBadgeUserEntry() {
		 List<OpenBadgesUsersBean> openBadgesUsersBean = openBadgesDAO.getAllStudentForScheduler();
		System.out.println("createBadgeUserEntry >> openBadgesUsersBean size : "+openBadgesUsersBean.size());
		logger.info("OpenBadgesService >> call createBadgeUserEntry >> openBadgesUsersBean size : "+openBadgesUsersBean.size());
		
		Integer count = 0 ;
		Integer successCount = 0 ;
		Integer errorCount = 0 ;
		for(OpenBadgesUsersBean bean : openBadgesUsersBean) {
			try {
				count++;
				bean.setCreatedBy("userEntryScheduler");
				bean.setLastModifiedBy("userEntryScheduler");
				openBadgesDAO.insertBadgeUser(bean);
				//System.out.println(count+" createBadgeUserEntry ");
				logger.info("OpenBadgesService >> call createBadgeUserEntry "+count);
				
				successCount++; 
			}catch(Exception e) {
				//e.printStackTrace();
				//System.out.println(count+" createBadgeUserEntry >> error");
				logger.error("OpenBadgesService >> call createBadgeUserEntry >> error count "+count);
				logger.error("OpenBadgesService >> call createBadgeUserEntry >> error message "+e.getMessage());
				errorCount++;
			}
		}
		
		logger.info("OpenBadgesService >> createBadgeUserEntry Scheduler successCount "+successCount+" and  errorCount "+errorCount );
		return "createBadgeUserEntry Scheduler successCount "+successCount+" and  errorCount "+errorCount;
	 }
	 
	 public String callProgramCompletionBadgeForAllStudent() {
		 List<OpenBadgesCriteriaParamBean> criteriaParamBean = openBadgesDAO.getCriteriaDetails("programCompletion");
		 System.out.println("OpenBadgesService >> call callProgramCompletionBadgeForAllStudent >> criteriaParamBean size : "+criteriaParamBean.size());
			
		 for(OpenBadgesCriteriaParamBean bean : criteriaParamBean) {
			 try {
				 processProgramCompletionBadge(bean.getBadgeId() );
			 }catch(Exception e) {
				 //e.printStackTrace();
			 }
		}
		
		return "";
	 }
	 
	 
	 private void processProgramCompletionBadge(Integer badgeId ) {
		 
		
		 List<OpenBadgesUsersBean> masterKeyList = openBadgesDAO.getMasterKeyListByBadgeId(badgeId);
		 System.out.println("OpenBadgesService >> call processProgramCompletionBadge >> masterKeyList size : "+masterKeyList.size());
		 logger.info("OpenBadgesService >> call processProgramCompletionBadge >> masterKeyList size : "+masterKeyList.size());
		
		 	
		 for(OpenBadgesUsersBean openBadgeBean : masterKeyList) {
			 try {
				 System.out.println("OpenBadgesService >> call processProgramCompletionBadge >> BadgeId : "+badgeId+" masterKey "+openBadgeBean.getConsumerProgramStructureId());
				 logger.info("OpenBadgesService >> call processProgramCompletionBadge >> BadgeId : "+badgeId+" masterKey "+openBadgeBean.getConsumerProgramStructureId());
				 List<OpenBadgesUsersBean> criteriaMetUserList = getProgramCompletionCriteriaMetUserList(badgeId, openBadgeBean.getConsumerProgramStructureId());
				 System.out.println("OpenBadgesService >> call processProgramCompletionBadge >> criteriaMetUserList : "+criteriaMetUserList.size());
				 logger.info("OpenBadgesService >> call processProgramCompletionBadge >> criteriaMetUserList : "+criteriaMetUserList.size());
				 if(criteriaMetUserList != null && criteriaMetUserList.size() > 0) {
					 createBadgeIssuedEntryByList(criteriaMetUserList, badgeId);
					 createProgramCompletionEvidenceEntry(badgeId);
				 }
			 }catch(Exception e) {
				 StringWriter errors = new StringWriter();
				 e.printStackTrace(new PrintWriter(errors));
				 logger.error("Error "+errors.toString());
			 }
		 } 
	 }
	 
	 public List<OpenBadgesUsersBean> getProgramCompletionCriteriaMetUserList(Integer badgeId, Integer consumerProgramStructureId){
			 List<OpenBadgesUsersBean> list = openBadgesDAO.getProgramCompletionBadgeNotIssuedStudentList(badgeId, consumerProgramStructureId );
			 logger.info("OpenBadgesService >> call processProgramCompletionBadge >> getProgramCompletionBadgeNotIssuedStudentList size : "+list.size());
			 return getProgramCompletionBadgeCriteriaMetUserList(list);
	 }
	 
	 private List<OpenBadgesUsersBean> getProgramCompletionBadgeCriteriaMetUserList(List<OpenBadgesUsersBean> list ) {
		 List<OpenBadgesUsersBean> criteriaMetUserList =  new ArrayList<OpenBadgesUsersBean>();
		 for(OpenBadgesUsersBean bean : list) {
			 try {
				 ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
				 sr.setSapId(bean.getSapid());
				 sr.setServiceRequestType("Issuance of Final Certificate");
				 ServiceRequestStudentPortal response = certificateService.checkFinalCertificateEligibility(sr);
				 logger.info(" Sapid "+bean.getSapid()+" Error:  "+response.getError() );
				 if(StringUtils.isBlank(response.getError())) {
					 bean.setIsClaimed(1);
					 bean.setAwardedAt("Program Completion");
					 criteriaMetUserList.add(bean); 
				 }
			 }catch (Exception e) {
				// TODO: handle exception
				 StringWriter errors = new StringWriter();
				 e.printStackTrace(new PrintWriter(errors));
				 logger.error("Error "+errors.toString());
			}
		 }
		 return criteriaMetUserList;
	 }
	 
	 private void createProgramCompletionEvidenceEntry(Integer badgeId) {
			List<OpenBadgesEvidenceBean> openBadgesEvidenceBean = openBadgesDAO.getProgramCompletionEvidenList(badgeId);
			System.out.println("createProgramCompletionEvidenceEntry >> openBadgesEvidenceBean size : "+openBadgesEvidenceBean.size());
			logger.info("OpenBadgesService >> call createProgramCompletionEvidenceEntry >> openBadgesEvidenceBean size : "+openBadgesEvidenceBean.size());
			Integer count = 0 ;
			Integer successCount = 0 ;
			Integer errorCount = 0 ;
			for(OpenBadgesEvidenceBean evidenceBean : openBadgesEvidenceBean) {
				try {
					count++;
					
		    		evidenceBean.setCreatedBy("ProgramCompletionScheduler");
			    	evidenceBean.setLastModifiedBy("ProgramCompletionScheduler");
			    	
			    	openBadgesDAO.insertEvidence(evidenceBean);
					
					//System.out.println(count+" createProgramCompletionEvidenceEntry ");
					logger.info("OpenBadgesService >> call createProgramCompletionEvidenceEntry "+count);
					
					successCount++; 
					
				}catch(Exception e) {
					//e.printStackTrace();
					//System.out.println(count+" createProgramCompletionEvidenceEntry >> error");
					logger.error("OpenBadgesService >> call createProgramCompletionEvidenceEntry >> error count "+count);
					logger.error("OpenBadgesService >> call createProgramCompletionEvidenceEntry >> error message "+e.getMessage());
					errorCount++;
				}
			} 
			
			logger.info("OpenBadgesService >> createProgramCompletionEvidenceEntry Scheduler successCount "+successCount+" and  errorCount "+errorCount );
//			return "createProgramCompletionEvidenceEntry Scheduler successCount "+successCount+" and  errorCount "+errorCount ;
		}
	 
	 private String createBadgeIssuedEntryByList(List<OpenBadgesUsersBean> criteriaMetUserList, Integer badgeId) {
			logger.info("OpenBadgesService >> call createBadgeIssuedEntry >> criteriaMetUserList size : "+criteriaMetUserList.size());
			Integer count = 0 ;
			Integer successCount = 0 ;
			Integer errorCount = 0 ;
			for(OpenBadgesUsersBean bean : criteriaMetUserList) {
				try {
					count++;
					Long issuedId = createBadgeIssuedEntryByBean(bean, badgeId);
					//System.out.println(count+" createBadgeIssuedEntry ");
					logger.info("OpenBadgesService >> call createBadgeIssuedEntry "+count);
					successCount++; 
					earnedBadgesNotificationService.sendEmail(bean.getSapid(), badgeId, issuedId, bean.getAwardedAt());
				}catch(Exception e) {
					//e.printStackTrace();
					//System.out.println(count+" createBadgeIssuedEntry >> error");
					logger.error("OpenBadgesService >> call createBadgeIssuedEntry >> error count "+count);
					logger.error("OpenBadgesService >> call createBadgeIssuedEntry >> error message "+e.getMessage());
					errorCount++;
				}
			} 
			
			logger.info("OpenBadgesService >> createBadgeIssuedEntry Scheduler successCount "+successCount+" and  errorCount "+errorCount );
			return "createBadgeIssuedEntry Scheduler successCount "+successCount+" and  errorCount "+errorCount ;
		}
	 
	 private String createBadgeIssuedEntryByListIfNotPresent(List<OpenBadgesUsersBean> criteriaMetUserList, Integer badgeId) {
			logger.info("OpenBadgesService >> call createBadgeIssuedEntry >> criteriaMetUserList size : "+criteriaMetUserList.size());
			Integer count = 0 ;
			Integer successCount = 0 ;
			Integer errorCount = 0 ;
			for(OpenBadgesUsersBean bean : criteriaMetUserList) {
				try {

					if (!openBadgesDAO.checkIfBadgeAlreadyAwarded(bean.getUserId(), bean.getAwardedAt(), badgeId)) {
						count++;
						Long issuedId = createBadgeIssuedEntryByBean(bean, badgeId);
						// System.out.println(count+" createBadgeIssuedEntry ");
						logger.info("OpenBadgesService >> call createBadgeIssuedEntry " + count);
						successCount++;
						earnedBadgesNotificationService.sendEmail(bean.getSapid(), badgeId, issuedId, bean.getAwardedAt());
					}

				}catch(Exception e) {
					//e.printStackTrace();
					//System.out.println(count+" createBadgeIssuedEntry >> error");
					logger.error("OpenBadgesService >> call createBadgeIssuedEntry >> error count "+count);
					logger.error("OpenBadgesService >> call createBadgeIssuedEntry >> error message "+e.getMessage());
					errorCount++;
				}
			} 
			
			logger.info("OpenBadgesService >> createBadgeIssuedEntry Scheduler successCount "+successCount+" and  errorCount "+errorCount );
			return "createBadgeIssuedEntry Scheduler successCount "+successCount+" and  errorCount "+errorCount ;
		}
	 
	 private Long createBadgeIssuedEntryByBean(OpenBadgesUsersBean bean, Integer badgeId) {
		bean.setBadgeId(badgeId);
		String inputMd5 = bean.getBadgeId()+bean.getAwardedAt()+bean.getUserId();
 		String uniquehash = getMd5(inputMd5) ;
 		bean.setUniquehash(uniquehash);
 		bean.setCreatedBy("createBadgeIssuedEntry");
		bean.setLastModifiedBy("createBadgeIssuedEntry");
		Long issuedId = openBadgesDAO.insertBadgeIssued(bean);
		logger.info("OpenBadgesService >> call createBadgeIssuedEntryByBean  issuedId : "+issuedId);
		if(bean.getEvidenceBean() != null) {
			bean.getEvidenceBean().setIssuedId(BigInteger.valueOf(issuedId));
		}
		return issuedId;
	 }


//		Start Top In Assignment Logic	 
	 public String callTopInAssignmentBadgeForAllStudent(
 			 String examMonth, Integer examYear
 			 ) {
 		 List<OpenBadgesCriteriaParamBean> criteriaParamBean = openBadgesDAO.getCriteriaDetails("topInAssignment");
 		 System.out.println(" callTopInAssignmentBadgeForAllStudent >> criteriaParamBean size : "+criteriaParamBean.size());
 			
 		 for(OpenBadgesCriteriaParamBean bean : criteriaParamBean) {
 			 try {
 				 processTopInAssignmentBadge(bean.getBadgeId(), Integer.parseInt(bean.getCriteriaValue()),
 						 examMonth, examYear
 						 );
 			 }catch(Exception e) {
 				 //e.printStackTrace();
 			 }
 		}
 		
 		return "";
 	 }
	 
	 public String callTopInAssignmentBadgeForAllMonthYear() {
 		 List<OpenBadgesTopInAssignmentDto> list = openBadgesDAO.getAcadNExamYearMonthList();
 		 System.out.println(" getAcadNExamYearMonthList list size : "+list.size());
 		for(OpenBadgesTopInAssignmentDto bean : list) {
 			try {
 				callTopInAssignmentBadgeForAllStudent(bean.getExamMonth(), bean.getExamYear());
 			}catch (Exception e) {
				// TODO: handle exception
 				e.printStackTrace();
			}
 		}
 		return "";
 	 }

	 private void processTopInAssignmentBadge(Integer badgeId, Integer criteriaValue,
		 			 String examMonth, Integer examYear
	 			 ) {
	 		 
	 		 System.out.println(" processTopInAssignmentBadge >> criteriaValue : "+criteriaValue );
 		 logger.info(" processTopInAssignmentBadge  criteriaValue : "+criteriaValue);
 		 
 		 
 		 List<OpenBadgesUsersBean> masterKeyList = openBadgesDAO.getMasterKeyListByBadgeId(badgeId);
 		 System.out.println(" processTopInAssignmentBadge >> masterKeyList size : "+masterKeyList.size());
 		 logger.info(" processTopInAssignmentBadge >> masterKeyList size : "+masterKeyList.size());
 		 	
 		 	
 		 for(OpenBadgesUsersBean openBadgeBean : masterKeyList) {
 			 try {
 				 List<Integer> semList = openBadgesDAO.getSemesterListByMasterKey(openBadgeBean.getConsumerProgramStructureId());
 				 List<OpenBadgesUsersBean> criteriaMetUserList = getTopInAssignment(
 						 semList, badgeId, openBadgeBean.getConsumerProgramStructureId() , criteriaValue,
 						 examMonth, examYear
 						 );
 				 if(criteriaMetUserList != null && criteriaMetUserList.size() > 0) {
 					createBadgeIssuedEntryByListIfNotPresent(criteriaMetUserList, badgeId);
 					 createTopInAssignmentEvidenceEntry( criteriaMetUserList);
 				 }
 			 }catch(Exception e) {
 				 //e.printStackTrace();
 			 }
 		 } 
 	 }	 
	 
	 protected List<OpenBadgesUsersBean> getTopInAssignment(
		 List<Integer> semList, Integer badgeId, 
		 Integer consumerProgramStructureId, Integer criteriaValue,
		 String examMonth, Integer examYear
		 ){
 		 List<OpenBadgesTopInAssignmentDto> tempList = new ArrayList<OpenBadgesTopInAssignmentDto>();
 		 for(Integer sem : semList  ) {
 			  List<String> subjectlist = openBadgesDAO.getSubjectList(consumerProgramStructureId, sem);
 				 for(String subject : subjectlist) {
 					try {
 						
 						List<String> applicableSapids = openBadgesDAO.getApplicableSapids(examMonth, examYear,
								consumerProgramStructureId, subject, sem);

						if (applicableSapids != null && applicableSapids.size() > 0) {
							tempList.addAll(openBadgesDAO.getAllStudentDataForTopInAssignment(badgeId, examMonth, examYear,
									applicableSapids, subject, sem, criteriaValue));
						}
 						
 					 }catch (Exception e) {
 		 				 //e.printStackTrace();
 		 				// TODO: handle exception
 		 			}
 				 }
 			
 		 }
 		 
 		 return mapTopInAssignmentDtoToBean(tempList);
 	 }
	 
	 protected List<OpenBadgesUsersBean> mapTopInAssignmentDtoToBean(List<OpenBadgesTopInAssignmentDto> list){
 		 List<OpenBadgesUsersBean> tempList = new ArrayList<OpenBadgesUsersBean>();
 		 for(OpenBadgesTopInAssignmentDto dto : list) {
 			 OpenBadgesUsersBean bean = new OpenBadgesUsersBean();
 			 OpenBadgesEvidenceBean ebean = new OpenBadgesEvidenceBean();
 			 bean.setUserId(dto.getUserId());
 			 bean.setSapid(dto.getSapid());
 			 bean.setAwardedAt(dto.getSubjectname());
 			 
 			 ebean.setEvidenceType("htmlText");
 			 ebean.setEvidenceValue("<p> Exam Year / Month : <strong>"+dto.getAssignmentYear()+" / "+dto.getAssignmentMonth()
 			 						+" </strong></p> <p> Submission Date : <strong> "+dto.getSubmissionDate()+" </strong> </p> "
 			 						+" <p><strong>"+dto.getAssignmentscore()+" marks out of 30 </strong> </p>"
 					 				);
 			 bean.setEvidenceBean(ebean);
 			 tempList.add(bean);
 		 }
 		 return tempList;
 	 }
	 
	 
	 protected void createTopInAssignmentEvidenceEntry(List<OpenBadgesUsersBean> evidencList) {

		 for(OpenBadgesUsersBean bean : evidencList) {
			 try {
			 	OpenBadgesEvidenceBean evidenceBean = new OpenBadgesEvidenceBean();
			 	evidenceBean = bean.getEvidenceBean();
			 	evidenceBean.setCreatedBy("TopInAssignmentScheduler");
		    	evidenceBean.setLastModifiedBy("TopInAssignmentScheduler");
		    	openBadgesDAO.insertEvidence(evidenceBean);
			 }catch (Exception e) {
				// TODO: handle exception
			}
		 }
		
	}
	 public void   setKeywordForTopinAssignment(OpenBadgesIssuedBean openBadgesIssuedBean, String sapid) {
		 try {
		 BadgeKeywordsDto dto = new BadgeKeywordsDto();
		 dto = openBadgesDAO.getProgramNameAndSemForAssignmentBadge(sapid,openBadgesIssuedBean.getAwardedAt());
		 dto.setSubjectName(openBadgesIssuedBean.getAwardedAt());
		 
		 replaceAllConstant(openBadgesIssuedBean,dto);
		 }catch (Exception e) {
			// TODO: handle exception
			 //e.printStackTrace();
		}
	 }
	 
	 public void   replaceAllConstant(OpenBadgesIssuedBean openBadgesIssuedBean,BadgeKeywordsDto dto){
		 Class class1 = dto.getClass();
          Field[] allFields = class1.getDeclaredFields();
         
          for (Field field : allFields) {
            try {
            	
                  if (field.get(dto) != null ) {
                	
                	  openBadgesIssuedBean.setBadgeName(openBadgesIssuedBean.getBadgeName().replaceAll("#"+field.getName(),(String) field.get(dto)));
                	  openBadgesIssuedBean.setBadgeDescription(openBadgesIssuedBean.getBadgeDescription().replaceAll("#"+field.getName(),(String) field.get(dto)));
                	  openBadgesIssuedBean.setCriteriaDescription(openBadgesIssuedBean.getCriteriaDescription().replaceAll("#"+field.getName(),(String) field.get(dto)));
                	  openBadgesIssuedBean.setAwardedAt(openBadgesIssuedBean.getAwardedAt().replaceAll("#"+field.getName(),(String) field.get(dto)));
                      
                	  
                  }
            }catch(Exception e) {
                    //e.printStackTrace();
             }
          }
          
      }

	 //		END Top In Assignment Logic 
	 
	 
//		Start Top In Semester Logic 
		 
		 public String callTopInSemesterBadgeForAllStudent(
				 String examMonth, Integer examYear
				 ) {
			 List<OpenBadgesCriteriaParamBean> criteriaParamBean = openBadgesDAO.getCriteriaDetails("topInSemester");
			 System.out.println(" callTopInSemesterBadgeForAllStudent >> criteriaParamBean size : "+criteriaParamBean.size());
				
			 for(OpenBadgesCriteriaParamBean bean : criteriaParamBean) {
				 try {
					 processTopInSemesterBadge(bean.getBadgeId(), Integer.parseInt(bean.getCriteriaValue()),
							 examMonth, examYear
							 );
				 }catch(Exception e) {
					 //e.printStackTrace();
				 }
			}
			
			return "";
		 }
		 
		 private void processTopInSemesterBadge(Integer badgeId, Integer criteriaValue,
				 String examMonth, Integer examYear
				 ) {
			 
			 System.out.println(" processTopInSemesterBadge >> criteriaValue : "+criteriaValue );
			 logger.info(" processTopInSemesterBadge  criteriaValue : "+criteriaValue);
			 
			 
			 List<OpenBadgesUsersBean> masterKeyList = openBadgesDAO.getMasterKeyListByBadgeId(badgeId);
			 //System.out.println(" processTopInSemesterBadge >> masterKeyList size : "+masterKeyList.size());
			 logger.info(" processTopInSemesterBadge >> masterKeyList size : "+masterKeyList.size());
			 	
			 	
			 for(OpenBadgesUsersBean openBadgeBean : masterKeyList) {
				 try {
					 List<OpenBadgesTopInSemesterDto> semList = openBadgesDAO.getSemesterListForTopInSemesterBadge(openBadgeBean.getConsumerProgramStructureId());
					 List<OpenBadgesUsersBean> criteriaMetUserList = getTopInSemester(
							 semList, badgeId, openBadgeBean.getConsumerProgramStructureId() , criteriaValue,
							 examMonth, examYear
							 );
					 if(criteriaMetUserList != null && criteriaMetUserList.size() > 0) {
						 createBadgeIssuedEntryByList(criteriaMetUserList, badgeId);
						 createTopInSemesterEvidenceEntry(badgeId, semList, criteriaMetUserList);
					 }
				 }catch(Exception e) {
					 //e.printStackTrace();
				 }
			 } 
		 }
		 
		 protected List<OpenBadgesUsersBean> getTopInSemester(
				 List<OpenBadgesTopInSemesterDto> semList, Integer badgeId, 
				 Integer consumerProgramStructureId, Integer criteriaValue,
				 String examMonth, Integer examYear
				 ){
			 List<OpenBadgesTopInSemesterDto> tempList = new ArrayList<OpenBadgesTopInSemesterDto>();
			 for(OpenBadgesTopInSemesterDto dto : semList  ) {
				 try {
					 tempList.addAll(openBadgesDAO.getToppedInSemApplicableList(dto.getSem(), consumerProgramStructureId,
						 examMonth, examYear, criteriaValue, badgeId));
				 }catch (Exception e) {
					// TODO: handle exception
				}
			 }
			 
			 return mapTopInSemDtoToBean(tempList);
		 }
		 
		 protected List<OpenBadgesUsersBean> mapTopInSemDtoToBean(List<OpenBadgesTopInSemesterDto> list){
			 List<OpenBadgesUsersBean> tempList = new ArrayList<OpenBadgesUsersBean>();
			 for(OpenBadgesTopInSemesterDto dto : list) {
				 OpenBadgesUsersBean bean = new OpenBadgesUsersBean();
				 OpenBadgesEvidenceBean ebean = new OpenBadgesEvidenceBean();
				 bean.setUserId(dto.getUserId());
				 bean.setSapid(dto.getSapid());
				 bean.setAwardedAt("Semester "+dto.getSem());
				 
				 String evidenceValue = "<p> <strong>Total "+dto.getTotalMarks()+" marks out of "+dto.getOutOfMarks()+" in the semester "+dto.getSem()+" exam </strong></p>";
				 ebean.setEvidenceType("htmlText");
				 ebean.setEvidenceValue(evidenceValue);
				 bean.setEvidenceBean(ebean);
				 tempList.add(bean);
			 }
			 return tempList;
		 }
		 
		 protected void createTopInSemesterEvidenceEntry(Integer badgeId, List<OpenBadgesTopInSemesterDto> semList, List<OpenBadgesUsersBean> totalMarksEvidencList) {
			 for(OpenBadgesTopInSemesterDto dto : semList  ) {
				List<OpenBadgesEvidenceBean> openBadgesEvidenceBean = openBadgesDAO.getTopInSemesterEvidenList(badgeId, dto.getSem());
				System.out.println("createTopInSemesterEvidenceEntry >> openBadgesEvidenceBean size : "+openBadgesEvidenceBean.size()+" sem "+dto.getSem()+" badgeId "+badgeId);
				logger.info(" createTopInSemesterEvidenceEntry >> openBadgesEvidenceBean size : "+openBadgesEvidenceBean.size());
				Integer count = 0 ;
				Integer successCount = 0 ;
				Integer errorCount = 0 ;
				for(OpenBadgesEvidenceBean evidenceBean : openBadgesEvidenceBean) {
					try {
						count++;
						
			    		evidenceBean.setCreatedBy("TopInSemesterScheduler");
				    	evidenceBean.setLastModifiedBy("TopInSemesterScheduler");
				    	
				    	openBadgesDAO.insertEvidence(evidenceBean);
						
						//System.out.println(count+" createTopInSemesterEvidenceEntry ");
						logger.info(" TopInSemesterEvidenceEntry "+count);
						
						successCount++; 
						
					}catch(Exception e) {
						//e.printStackTrace();
						//System.out.println(count+" createTopInSemesterEvidenceEntry >> error");
						logger.error(" createTopInSemesterEvidenceEntry >> error count "+count);
						logger.error(" createTopInSemesterEvidenceEntry >> error message "+e.getMessage());
						errorCount++;
					}
				}

				logger.info(" createTopInSemesterEvidenceEntry Scheduler successCount "+successCount+" and  errorCount "+errorCount );
			 }
			 
			 for(OpenBadgesUsersBean bean : totalMarksEvidencList) {
				 try {
				 	OpenBadgesEvidenceBean evidenceBean = new OpenBadgesEvidenceBean();
				 	evidenceBean = bean.getEvidenceBean();
				 	evidenceBean.setCreatedBy("TopInSemesterScheduler");
			    	evidenceBean.setLastModifiedBy("TopInSemesterScheduler");
			    	openBadgesDAO.insertEvidence(evidenceBean);
				 }catch (Exception e) {
					// TODO: handle exception
				}
			 }
			 
				
			}

		 public void setKeywordForTopinSemester(OpenBadgesIssuedBean openBadgesIssuedBean, String sapid) {
			 try {
				 BadgeKeywordsDto dto = new BadgeKeywordsDto();
				 dto.setSemester(openBadgesIssuedBean.getAwardedAt());
				 replaceAllConstant(openBadgesIssuedBean,dto);
				 
				String sem = openBadgesIssuedBean.getAwardedAt().replaceAll("[^0-9]", "");
				openBadgesIssuedBean.setBadgeName(openBadgesIssuedBean.getBadgeName()+" - "+openBadgesIssuedBean.getAwardedAt());
				String programname = openBadgesDAO.getProgramNameBySem(sapid, Integer.parseInt(sem));
				openBadgesIssuedBean.setAwardedAt(programname);
			 }catch (Exception e) {
				// TODO: handle exception
				 //e.printStackTrace();
			}
		 }
		 
//			End Top In Semester Logic 
	 


	 //Start  Re-Registration For New Sem Badge Logic
	 
	 
	 
	 
	 public HashMap<String,String> callReRegistrationForSemBadgeForAllStudent() {
		 List<OpenBadgesCriteriaParamBean> criteriaParamBean = openBadgesDAO.getCriteriaDetails("Re-RegisteredForSem");
		 
		 System.out.println(" Batch Job For Re-Registration In Sem Started ");
		 logger.info(" Batch Job For Re-Registration In Sem Started " );
		 int totalStudentsEntryPerBadge = 0;
		 
		 HashMap<String,String> response; response = new HashMap<String,String>();
		 	
		 for(OpenBadgesCriteriaParamBean bean : criteriaParamBean) {
			 try {
				 
				int result_count =  processReRegistrationForSemBadgeForAllStudent(bean.getBadgeId(), Integer.parseInt(bean.getCriteriaValue()), totalStudentsEntryPerBadge);
				response.put("Issued For Student For badge  "+bean.getBadgeId(),String.valueOf(result_count));
				totalStudentsEntryPerBadge = 0;
			 }catch(Exception e) {
				 logger.info(" Error in Processing (processReRegistrationForSemBadgeForAllStudent) For BadgeId "+bean.getBadgeId() +"  And For Sem "+bean.getCriteriaValue(),e);
			 }
		}
		 
		
		 //System.out.println(" Batch Job For Re-Registration In Sem Ended ");
		 logger.info(" Batch Job For Re-Registration In Sem Ended ");
		 
		 logger.info("Result "+response);
		 
	
		 response.put("Issued Student ",String.valueOf(totalStudentsEntryPerBadge));
		
		return response;
	 }
	 
	 public int processReRegistrationForSemBadgeForAllStudent(Integer badgeId,Integer criteriaValue,int totalStudentsEntryPerBadge)
	 {
		 System.out.println(" processReRegistrationForSemBadgeForAllStudent FOR Badge Id started. "+badgeId);
		 logger.info(" processReRegistrationForSemBadgeForAllStudent FOR Badge Id "+badgeId );
		
			 //Get the masterKeys which are linked with BadgeId
			 List<OpenBadgesUsersBean> getMasterKeyListByBadgeId = openBadgesDAO.getMasterKeyListByBadgeId(badgeId);
		 
			 for(OpenBadgesUsersBean masterKey : getMasterKeyListByBadgeId)
			 {
				 	
				 	List<OpenBadgesForReRegSemDto> studentsData = openBadgesDAO.getAllStudentDataForReReg(criteriaValue,masterKey.getConsumerProgramStructureId(),badgeId);
				 	
				 	totalStudentsEntryPerBadge += studentsData.size();
				 	//System.out.println("Total Number Of Students For Sem "+criteriaValue+ " And Masterkey "+masterKey.getConsumerProgramStructureId()+" :-  "+studentsData.size());
				 	logger.info("Total Number Of Students For Sem "+criteriaValue+ " And Masterkey "+masterKey.getConsumerProgramStructureId()+" :-  "+studentsData.size());
				 	
				 	if(studentsData.size() > 0) {
				 		
				 		popuateReRegistrationIssuedAndEvidence(studentsData,badgeId,criteriaValue,masterKey.getConsumerProgramStructureId());
				
				 	}
				 	
			 }
			 System.out.println(" processReRegistrationForSemBadgeForAllStudent FOR Badge Id ended. "+badgeId);
		 return totalStudentsEntryPerBadge;
	 }
	 
	
	 public void popuateReRegistrationIssuedAndEvidence(List<OpenBadgesForReRegSemDto> studentsData,Integer badgeId,Integer criteriaValue,Integer masterKey)
	 {

		 int i = 0 ;
		 List<String> error_List = new ArrayList<String>();
		 int success_count = 0;
		 for(OpenBadgesForReRegSemDto studentData1:studentsData)
		 {	
			 	try {
			 		
			 	OpenBadgesUsersBean studentData = new OpenBadgesUsersBean();
			 	OpenBadgesEvidenceBean bean = new OpenBadgesEvidenceBean();
			 	//System.out.println(" Iteration:-  "+(++i)+"/"+studentsData.size());
			 	
			 	String programname = openBadgesDAO.getProgramNameBySem(studentData1.getSapid(),Integer.valueOf(criteriaValue));
			 	studentData.setAwardedAt(programname);
			 	studentData.setUserId(Integer.valueOf(studentData1.getUserId()));
				studentData.setBadgeId(badgeId);
			
				
				String inputMd5 = studentData.getBadgeId()+studentData.getAwardedAt()+studentData.getUserId();
		 		String uniquehash = getMd5(inputMd5) ;
		 		studentData.setUniquehash(uniquehash);
		 		studentData.setCreatedBy("createBadgeIssuedEntry");
		 		studentData.setLastModifiedBy("createBadgeIssuedEntry");
				Long issuedId = openBadgesDAO.insertBadgeIssued(studentData);
			 	
				//Issue Badge To the Student
				
				bean.setIssuedId(BigInteger.valueOf(issuedId));
				bean.setCreatedBy("ReRegisteredInSem");
				bean.setLastModifiedBy("ReRegisteredInSem");
				bean.setEvidenceValue("<p> Re-Registered For  Semester "+criteriaValue+".  Admission Date : <strong>"+studentData1.getCreatedDate()+" </strong> . For Academic Cycle :  <strong>"+studentData1.getMonth()+"/"+studentData1.getYear()+" </strong> </p>");
				bean.setEvidenceType("htmlbody");
				
				//Insert The Evidence For that issued Id
				openBadgesDAO.insertEvidence(bean);
				success_count++;
				earnedBadgesNotificationService.sendEmail(studentData1.getSapid(), badgeId, issuedId, studentData.getAwardedAt());
			 	}catch(Exception e)
			 	{
			 		//e.printStackTrace();
			 		error_List.add("Error in Badge Issued Or Evidence Insertion For Student "+studentData1.getUserId() + " For Sem "+criteriaValue);
			 		
			 	}
			 	
		 }
		 
		 logger.error("Total Entries Should be Done For MasterKey "+masterKey +"And For BadgeId "+badgeId+" are " + studentsData.size());
		 logger.error("Total Success/Failure Entries  Done For MasterKey "+masterKey +"And For BadgeId "+badgeId+" are "+success_count+"/"+error_List.size());
		 logger.error("Error Entries   For MasterKey are "+masterKey +"And For BadgeId "+badgeId+" are " ,error_List);
	 }
	 
	 public void setKeywordForReRegForSem(OpenBadgesIssuedBean openBadgesIssuedBean, String sapid,String sem) {
		 try {
			 BadgeKeywordsDto dto = new BadgeKeywordsDto();
			
			dto.setProgramNameFull(openBadgesIssuedBean.getAwardedAt());
		    dto.setType("Semester");
		  
		    
		    replaceAllConstant(openBadgesIssuedBean,dto);
		 }catch (Exception e) {
			// TODO: handle exception
			 //e.printStackTrace();
		}
	 }
	 
	 public void setKeywordForTopInSubject(OpenBadgesIssuedBean openBadgesIssuedBean, String sapid) {
		 try {
			 BadgeKeywordsDto dto = new BadgeKeywordsDto();
			 String semester= openBadgesDAO. getSemesterNameBySubject(sapid,openBadgesIssuedBean.getAwardedAt());
			 dto.setSubjectName(openBadgesIssuedBean.getAwardedAt());
			 dto.setSemester(semester);
		    replaceAllConstant(openBadgesIssuedBean,dto);
		 }catch (Exception e) {
		}
	 }
	 
	 //End Start  Re-Registration For New Sem Badge Logic
	 
	 ///NGASCE Alumni Badge Start
	 
	 
	 public HashMap<String,String> runNgasceAlumniBadgeScheduler() {
		 List<OpenBadgesCriteriaParamBean> criteriaParamBean = openBadgesDAO.getCriteriaDetails("ngascealumni");
		 
		 System.out.println(" Batch Job For NGASCE Alumni Started ");
		 alumni_reReg_badge.info(" Batch Job For NGASCE Alumni  Started " );
	
		
		 List<String> error = new ArrayList<String>();
		 HashMap<String,String> response = new HashMap<String,String>();
		
		 for(OpenBadgesCriteriaParamBean bean : criteriaParamBean) {
			 try {
				 int totalStudentsEntryToBeDone = 0; 	
				 totalStudentsEntryToBeDone = processNgasceAlumniBadgeForAllStudent(bean.getBadgeId(),totalStudentsEntryToBeDone,bean.getCriteriaValue());
				 response.put("Total Students For BageId "+bean.getBadgeId(),String.valueOf(totalStudentsEntryToBeDone));
				 
			 }catch(Exception e) {
				// e.printStackTrace();
				 error.add("Error in Processing (runNgasceAlumniBadgeScheduler) For BadgeId "+bean.getBadgeId() +"  And For Program "+bean.getCriteriaValue());
				 alumni_reReg_badge.info(" Error in Processing (runNgasceAlumniBadgeScheduler) For BadgeId "+bean.getBadgeId() +"  And For Program "+bean.getCriteriaValue(),e);
			 }
		}
		 
		
		 System.out.println(" Batch Job For NGASCE Alumni   Ended ");
		 alumni_reReg_badge.info(" Batch Job For NGASCE Alumni  Ended ");
		 
		 alumni_reReg_badge.info("Total Students Entry Done: "+response );
		 
		
		return response;
	 }
	 
	 public int processNgasceAlumniBadgeForAllStudent(Integer badgeId,int totalStudentsEntryToBeDone,String criteriaValue)
	 {
		 System.out.println(" processNgasceAlumniBadgeForAllStudent FOR Badge Id Started. "+badgeId);
		 alumni_reReg_badge.info(" processNgasceAlumniBadgeForAllStudent FOR Badge Id "+badgeId );
		
			 //Get the masterKeys which are linked with BadgeId
			 List<OpenBadgesUsersBean> getMasterKeyListByBadgeId = openBadgesDAO.getMasterKeyListByBadgeId(badgeId);
			 for(OpenBadgesUsersBean masterKey : getMasterKeyListByBadgeId)
			 {
				 	try {
				 	System.out.println("Collect Student's Data of MasterKey "+masterKey.getConsumerProgramStructureId()+" For BadgeId "+badgeId);
				 	
				 	 List<OpenBadgesUsersBean> criteriaMetUserList = openBadgesDAO.getAllStudentDataForAlumni(masterKey.getConsumerProgramStructureId(),badgeId);
				 	
				 	totalStudentsEntryToBeDone += criteriaMetUserList.size();

				 	criteriaMetUserList = checkProgramClearedStudentList(criteriaMetUserList,criteriaValue);

				 	if(criteriaMetUserList.size() > 0) {
				 	createBadgeIssuedEntryByList(criteriaMetUserList,badgeId);
				 	createProgramCompletionEvidenceEntry(badgeId);
				 	}
				 	
				 	alumni_reReg_badge.info("Total Number Of Students For  Masterkey  after "+masterKey.getConsumerProgramStructureId()+" :-  "+criteriaMetUserList.size());
				 	}catch(Exception e)
				 	{
				 		//e.printStackTrace();
				 		alumni_reReg_badge.info("Error  For  Masterkey "+masterKey.getConsumerProgramStructureId()+" and for badgeId  :-  "+badgeId +".Error ",e);
				 	}
			 }
			 System.out.println(" processNgasceAlumniBadgeForAllStudent FOR Badge Id Ended."+badgeId);
			 return totalStudentsEntryToBeDone;
	 }

	 
	 public String getCertificatePath(String uniquehash) {
			return openBadgesDAO.getCertificatePath(uniquehash);
		}
	 
	 private List<OpenBadgesUsersBean> checkProgramClearedStudentList(List<OpenBadgesUsersBean> list ,String criteriaValue) {
		 List<OpenBadgesUsersBean> criteriaMetUserList =  new ArrayList<OpenBadgesUsersBean>();
		 for(OpenBadgesUsersBean bean : list) {
			 try {
				 ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
				 sr.setSapId(bean.getSapid());
				 sr.setServiceRequestType("Issuance of Final Certificate");
				 ServiceRequestStudentPortal response = certificateService.checkFinalCertificateEligibility(sr);
				 if(StringUtils.isBlank(response.getError())) {
					 bean.setAwardedAt(openBadgesDAO.getProgramNameByProgramId(criteriaValue));
					 criteriaMetUserList.add(bean); 
				 }
			 }catch (Exception e) {
				// TODO: handle exception
				 StringWriter errors = new StringWriter();
				 e.printStackTrace(new PrintWriter(errors));
				 alumni_reReg_badge.error("Error for sapid ("+bean.getSapid()+") in getting final certificate result :- "+errors.toString());
			}
		 }
		 return criteriaMetUserList;
	 }
	 
	 //NGASCE Alumni Badge End
	 
	 
	 // start top in subject
	 
	 public HashMap<String,String> callTopnInSubjectBadgeForAllStudent() {
		 List<OpenBadgesCriteriaParamBean> criteriaParamBean = openBadgesDAO.getCriteriaDetails("topInSubject");
		 
		 logger.info(" Batch Job ForTop in subject  Started " );
		 int totalStudentsEntryPerBadge = 0;
		 
		 HashMap<String,String> response = new HashMap<String,String>();
		 	
		 for(OpenBadgesCriteriaParamBean bean : criteriaParamBean) {
			 try {
				int result_count =  processTopInSubjectBadgeForAllStudent(bean.getBadgeId(), Integer.parseInt(bean.getCriteriaValue()), totalStudentsEntryPerBadge);
				response.put("Issued For Student For badge  "+bean.getBadgeId(),String.valueOf(result_count));
				totalStudentsEntryPerBadge = 0;
			 }catch(Exception e) {
				 logger.error(" Error in Processing (processTopInSubjectBadgeForAllStudent) For BadgeId "+bean.getBadgeId() +"  And For Sub "+bean.getCriteriaValue(),e);
			 }
		}
		 logger.info(" Batch Job For top In Sub Ended ");
		 logger.info("Result "+response);
		 response.put("Issued Student ",String.valueOf(totalStudentsEntryPerBadge));
		return response;
	 }
	 
	 public int processTopInSubjectBadgeForAllStudent(Integer badgeId,Integer criteriaValue,int totalStudentsEntryPerBadge)
	 {
		 logger.info(" processTopInSubjectBadgeForAllStudent FOR Badge Id "+badgeId );
		
			 //Get the masterKeys which are linked with BadgeId
			 List<OpenBadgesUsersBean> getMasterKeyListByBadgeId = openBadgesDAO.getMasterKeyListByBadgeId(badgeId);
		 
			 for(OpenBadgesUsersBean masterKey : getMasterKeyListByBadgeId)
			 {  try {
				 	List<OpenBadgesTopInsubjectDto > studentsData = openBadgesDAO.getAllStudentDataForTopInSubject(criteriaValue,masterKey.getConsumerProgramStructureId(),badgeId);
				 	totalStudentsEntryPerBadge += studentsData.size();
				 	logger.info("Total Number Of Students  "+criteriaValue+ " And Masterkey "+masterKey.getConsumerProgramStructureId()+" :-  "+studentsData.size());
				 	
				 	if(studentsData.size() > 0) {
				 		popuateTopInSubjectIssuedAndEvidence(studentsData,badgeId, masterKey.getConsumerProgramStructureId());
				
				 	}
			 	}catch (Exception e) {
					// TODO: handle exception
				}
			 }
		 return totalStudentsEntryPerBadge;
	 }
	 
	
	 public void popuateTopInSubjectIssuedAndEvidence(List<OpenBadgesTopInsubjectDto> studentsData,Integer badgeId, Integer masterKey)
	 {

		 int i = 0 ;
		 List<String> error_List = new ArrayList<String>();
		 int success_count = 0;
		 for( OpenBadgesTopInsubjectDto  studentData1: studentsData)
		 {	
			 	try {
			 		
			 	OpenBadgesUsersBean studentData = new OpenBadgesUsersBean();
			 	OpenBadgesEvidenceBean bean = new OpenBadgesEvidenceBean();
			 	studentData.setAwardedAt(studentData1.getSubject());
			 	studentData.setUserId(Integer.valueOf(studentData1.getUserId()));
				studentData.setBadgeId(badgeId);
			
				
				String inputMd5 = studentData.getBadgeId()+studentData.getAwardedAt()+studentData.getUserId();
		 		String uniquehash = getMd5(inputMd5) ;
		 		studentData.setUniquehash(uniquehash);
		 		studentData.setCreatedBy("createBadgeIssuedEntry");
		 		studentData.setLastModifiedBy("createBadgeIssuedEntry");
				Long issuedId = openBadgesDAO.insertBadgeIssued(studentData);
			 	
				//Issue Badge To the Student
				
				bean.setIssuedId(BigInteger.valueOf(issuedId));
				bean.setCreatedBy("TopInSubject");
				bean.setLastModifiedBy("TopINSubject");
				bean.setEvidenceValue("<p> Subject Name: "+studentData1.getSubject() +" Total Marks: "+ studentData1.getTotal() +" out of "+ studentData1.getOutOfMarks()+ " Subject Topper - Rank : " +studentData1.getRank()+" </p> ");
				bean.setEvidenceType("htmlbody");
				
				//Insert The Evidence For that issued Id
				openBadgesDAO.insertEvidence(bean);
				success_count++;
				earnedBadgesNotificationService.sendEmail(studentData1.getSapid(), badgeId, issuedId, studentData.getAwardedAt());
			 	}catch(Exception e){
			 		error_List.add("Error in Badge Issued Or Evidence Insertion For Student "+studentData1.getUserId() + " For badgeId "+badgeId);
			 		 logger.error("Error in Badge Issued Or Evidence Insertion For Studen "+e.getMessage());
			 	}
			 }
		 
		 logger.info("Total Entries Should be Done For MasterKey "+masterKey +"And For BadgeId "+badgeId+" are " + studentsData.size());
		 logger.info("Total Success/Failure Entries  Done For MasterKey "+masterKey +"And For BadgeId "+badgeId+" are "+success_count+"/"+error_List.size());
		 logger.info("Error Entries   For MasterKey are "+masterKey +"And For BadgeId "+badgeId+" are " ,error_List);
	 }
	 
	 public HashMap<String,String> callTopnInTEEBadgeForAllStudent(String month, Integer year) {
		 List<OpenBadgesCriteriaParamBean> criteriaParamBean = openBadgesDAO.getCriteriaDetails("topInTEE");
		 
		 System.out.println(" Batch Job For Top in TEE  Started criteriaParamBean size "+criteriaParamBean.size() );
		 logger.info(" Batch Job ForTop in TEE  Started " );
		 int totalStudentsEntryPerBadge = 0;
		 HashMap<String,String> response = new HashMap<String,String>();
		 
		 for(OpenBadgesCriteriaParamBean bean : criteriaParamBean) {
			 try {
				 System.out.println("bean.getBadgeId() "+bean.getBadgeId());
				int result_count =  processTopInTEEBadgeForAllStudent(bean.getBadgeId(), Integer.parseInt(bean.getCriteriaValue()), totalStudentsEntryPerBadge, month, year);
				response.put("Issued For TEE For badge  "+bean.getBadgeId(),String.valueOf(result_count));
				totalStudentsEntryPerBadge = 0;
			 }catch(Exception e) {
				 e.printStackTrace();
				 logger.error(" Error in Processing (processTopInTEEBadgeForAllStudent) For BadgeId "+bean.getBadgeId() +"  And For Sub "+bean.getCriteriaValue(),e);
			 }
		}
		 System.out.println(" Batch Job For top In TEE Ended ");
		 logger.info(" Batch Job For top In TEE Ended ");
		 logger.info("Result "+response);
		 
		return response;
	 }
	 
	 public int processTopInTEEBadgeForAllStudent(Integer badgeId,Integer criteriaValue,int totalStudentsEntryPerBadge, String month, Integer year) {
		 logger.info(" processTopInTEEBadgeForAllStudent FOR Badge Id "+badgeId );
		 List<OpenBadgesUsersBean> getMasterKeyListByBadgeId = openBadgesDAO.getMasterKeyListByBadgeId(badgeId);
		 
		 for (OpenBadgesUsersBean openBadgeBean : getMasterKeyListByBadgeId) {
			 try {
				 List<Integer> semList = openBadgesDAO.getSemesterListByMasterKey(openBadgeBean.getConsumerProgramStructureId());
				 List<OpenBadgesUsersBean> criteriaMetUserList = getTopInTEE(
						 badgeId, openBadgeBean.getConsumerProgramStructureId(), semList, month, year, criteriaValue);
				 
				 totalStudentsEntryPerBadge += criteriaMetUserList.size();
				 logger.info("Total Number Of Students  "+criteriaValue+ " And Masterkey "+openBadgeBean.getConsumerProgramStructureId()+" :-  "+criteriaMetUserList.size());
					 
				 if(criteriaMetUserList != null && criteriaMetUserList.size() > 0) {
					 createBadgeIssuedEntryByListIfNotPresent(criteriaMetUserList, badgeId);
 					 createTopInTEEEvidenceEntry( criteriaMetUserList);
 					 logger.info("criteriaMetUserList {} for badgeId {}  criteriaValue {} ", criteriaMetUserList, badgeId, criteriaValue);
 				 }
			} catch (Exception e) {
				// TODO: handle exception
			}
		 }
		 return totalStudentsEntryPerBadge;
	 }

		private List<OpenBadgesUsersBean> getTopInTEE(Integer badgeId, Integer consumerProgramStructureId,
				List<Integer> semList, String month, Integer year, Integer criteriaValue) {
			List<OpenBadgesTopInTEEDto> tempList = new ArrayList<OpenBadgesTopInTEEDto>();

			if (semList != null && semList.size() > 0) {
				for (Integer sem : semList) {
					List<String> subjectlist = openBadgesDAO.getSubjectList(consumerProgramStructureId, sem);
					
					for (String subject : subjectlist) {
						try {
							List<String> applicableSapids = openBadgesDAO.getApplicableSapids(month, year,
									consumerProgramStructureId, subject, sem);

							if (applicableSapids != null && applicableSapids.size() > 0) {
								tempList.addAll(openBadgesDAO.getAllStudentDataForTopInTEE(month, year,
										applicableSapids, subject, sem, criteriaValue));
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
			return mapTopInTEEDtoToBean(tempList);
		}

	protected List<OpenBadgesUsersBean> mapTopInTEEDtoToBean(List<OpenBadgesTopInTEEDto> list) {
		 List<OpenBadgesUsersBean> tempList = new ArrayList<OpenBadgesUsersBean>();
		 
		 for(OpenBadgesTopInTEEDto dto : list) {
			 OpenBadgesUsersBean bean = new OpenBadgesUsersBean();
			 OpenBadgesEvidenceBean ebean = new OpenBadgesEvidenceBean();
			 bean.setUserId(dto.getUserId());
 			 bean.setSapid(dto.getSapid());
 			 bean.setAwardedAt(dto.getSubject());
 			 
 			 ebean.setEvidenceType("htmlText");
 			 ebean.setEvidenceValue("<p>Student had scored in TEE : <strong>"+dto.getWrittenscore()+"</strong> in Exam Year / Month : <strong>"+dto.getWrittenYear()+" / "+dto.getWrittenMonth()+" </strong> </p> ");
 			 bean.setEvidenceBean(ebean);
			 tempList.add(bean);
		 }
		 return tempList;
	}
	
	protected void createTopInTEEEvidenceEntry(List<OpenBadgesUsersBean> evidencList) {

		 for(OpenBadgesUsersBean bean : evidencList) {
			 try {
			 	OpenBadgesEvidenceBean evidenceBean = new OpenBadgesEvidenceBean();
			 	evidenceBean = bean.getEvidenceBean();
			 	evidenceBean.setCreatedBy("TopInTEEScheduler");
		    	evidenceBean.setLastModifiedBy("TopInTEEScheduler");
		    	openBadgesDAO.insertEvidence(evidenceBean);
			 }catch (Exception e) {
				// TODO: handle exception
			}
		 }
		
	}
	
	 public void setKeywordForTopInTEE(OpenBadgesIssuedBean openBadgesIssuedBean, String sapid) {
		 try {
			 BadgeKeywordsDto dto = new BadgeKeywordsDto();
			 String semester= openBadgesDAO. getSemesterNameBySubject(sapid,openBadgesIssuedBean.getAwardedAt());
			 dto.setSubjectName(openBadgesIssuedBean.getAwardedAt());
			 dto.setSemester(semester);
		    replaceAllConstant(openBadgesIssuedBean,dto);
		 }catch (Exception e) {
		}
	 }

		public String callAppearedInTEEBadgeForAllMonthYear() {
			List<OpenBadgesTopInAssignmentDto> list = openBadgesDAO.getAcadNExamYearMonthList();
			System.out.println(" getAcadNExamYearMonthList list size : " + list.size());
			for (OpenBadgesTopInAssignmentDto bean : list) {
				try {
					callAppearedTEEBadgeForAllStudent(bean.getAcadMonth(), bean.getAcadYear(), bean.getExamMonth(),
							bean.getExamYear());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			return "";
		}

		public Map<String, String> callAppearedTEEBadgeForAllStudent(String acadmonth, Integer acadyear,
				String examMonth, Integer examyear) {
			List<OpenBadgesCriteriaParamBean> criteriaParamBean = openBadgesDAO.getCriteriaDetails("appearForTEE");

			System.out.println(" Batch Job For Top in TEE  Started criteriaParamBean size " + criteriaParamBean.size());
			logger.info(" Batch Job ForTop in TEE  Started ");
			int totalStudentsEntryPerBadge = 0;
			Map<String, String> response = new HashMap<>();

			for (OpenBadgesCriteriaParamBean bean : criteriaParamBean) {
				try {
					int resultCount = processAppearedInTEEBadgeForAllStudent(bean.getBadgeId(), totalStudentsEntryPerBadge, acadmonth, acadyear,
							examMonth, examyear);
					response.put("Issued For TEE For badge  " + bean.getBadgeId(), String.valueOf(resultCount));
					totalStudentsEntryPerBadge = 0;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(" Error in Processing (processAppearedInTEEBadgeForAllStudent) For BadgeId "
							+ bean.getBadgeId() + "  And For Sub " + bean.getCriteriaValue(), e);
				}
			}
			System.out.println(" Batch Job For Appeared for TEE Ended ");
			logger.info(" Batch Job For Appeared In TEE Ended ");
			logger.info("Result {}", response);

			return response;
		}

		public int processAppearedInTEEBadgeForAllStudent(Integer badgeId,
				int totalStudentsEntryPerBadge, String acadmonth, Integer acadyear, String examMonth,
				Integer examyear) {
			logger.info(" processAppearedInTEEBadgeForAllStudent FOR Badge Id {}", badgeId);
			List<OpenBadgesUsersBean> getMasterKeyListByBadgeId = openBadgesDAO.getMasterKeyListByBadgeId(badgeId);

			for (OpenBadgesUsersBean openBadgeBean : getMasterKeyListByBadgeId) {
				try {
					List<Integer> semList = openBadgesDAO
							.getSemesterListByMasterKey(openBadgeBean.getConsumerProgramStructureId());
					List<OpenBadgesUsersBean> criteriaMetUserList = getAppearedInTEE(badgeId,
							openBadgeBean.getConsumerProgramStructureId(), semList, acadmonth, acadyear, examMonth,
							examyear);

					totalStudentsEntryPerBadge += criteriaMetUserList.size();
					logger.info("Total Number Of Students And Masterkey {} :-  {}",
							openBadgeBean.getConsumerProgramStructureId(), criteriaMetUserList.size());

					if (criteriaMetUserList != null && criteriaMetUserList.size() > 0) {
						createBadgeIssuedEntryByList(criteriaMetUserList, badgeId);
						createAppearedTEEEvidence(criteriaMetUserList );
						logger.info("criteriaMetUserList {} for badgeId {}  ", criteriaMetUserList,
								badgeId);
					}
				} catch (Exception e) {
				}
			}
			return totalStudentsEntryPerBadge;
		}

		private List<OpenBadgesUsersBean> getAppearedInTEE(Integer badgeId, Integer consumerProgramStructureId,
				List<Integer> semList, String acadmonth, Integer acadyear, String examMonth, Integer examyear) {
			List<OpenBadgesUsersBean> tempList = new ArrayList<OpenBadgesUsersBean>();

			if (semList != null && !semList.isEmpty()) {
				for (Integer sem : semList) {
					List<String> subjectlist = openBadgesDAO.getSubjectList(consumerProgramStructureId, sem);

					for (String subject : subjectlist) {
						try {
							List<OpenBadgesAppearedForTEEBean> applicableSapids = openBadgesDAO
									.getAppearedTEEApplicableSapids(acadmonth, acadyear, consumerProgramStructureId,
											subject, sem, badgeId);
							if (applicableSapids != null && !applicableSapids.isEmpty()) {
								tempList.addAll(
										checkAppearedTEERegularAttempt(applicableSapids, examMonth, examyear, subject, acadmonth, acadyear));
							}
						} catch (Exception e) {
							logger.info("Error while trying to get applicable sapids : {}", e.getMessage());
						}
					}
				}
			}
			return tempList;
		}

		public List<OpenBadgesUsersBean> checkAppearedTEERegularAttempt(List<OpenBadgesAppearedForTEEBean> list,
				String examMonth, Integer examyear, String subject, String acadmonth, Integer acadyear) {
			List<OpenBadgesUsersBean> tempList = new ArrayList<OpenBadgesUsersBean>();
			for (OpenBadgesAppearedForTEEBean bean : list) {
				try {
					OpenBadgesUsersBean criteriaMetbean = new OpenBadgesUsersBean();
					if (openBadgesDAO.checkAppearedTEERegularAttempt(bean.getSapId(), subject, examMonth, examyear)) {
						OpenBadgesEvidenceBean evidenceBean = new OpenBadgesEvidenceBean();
						criteriaMetbean.setSapid(bean.getSapId());
						criteriaMetbean.setUserId(bean.getUserId());
						criteriaMetbean.setAwardedAt(subject);
						OpenBadgesAppearedForTEEBean dateAndTimeBean = new OpenBadgesAppearedForTEEBean();

						if (CURRENT_ACAD_MONTH.equalsIgnoreCase(acadmonth)
								&& CURRENT_ACAD_YEAR.equalsIgnoreCase(String.valueOf(acadyear))) {
							dateAndTimeBean = openBadgesDAO.getExamDateAndTime(examMonth, examyear, bean.getSapId(),
									subject);
						} else {
							dateAndTimeBean = openBadgesDAO.getExamDateAndTimeFromHistory(examMonth, examyear,
									bean.getSapId(), subject);
						}

						evidenceBean.setCreatedBy("AppearedForTEE");
						evidenceBean.setLastModifiedBy("AppearedForTEE");

						String formattedEvidence = "<p> Exam Year / Month : <strong> " + examyear + " / " + examMonth
								+ " </strong></p> <p> Exam Date : <strong>" + dateAndTimeBean.getExamDate() + " "
								+ dateAndTimeBean.getExamTime() + " </strong> </p>";

						evidenceBean.setEvidenceValue(formattedEvidence);
						evidenceBean.setEvidenceType("htmlText");
						criteriaMetbean.setEvidenceBean(evidenceBean);
						tempList.add(criteriaMetbean);
					}
				} catch (Exception e) {
					logger.info("Error while trying to get evidence : " + e.getMessage());
				}
			}
			return tempList;
		}

		private void createAppearedTEEEvidence(List<OpenBadgesUsersBean> list) {

			for (OpenBadgesUsersBean bean : list) {
				try {
					logger.info(bean.getEvidenceBean().getIssuedId() + " issued id");

					// Insert The Evidence For that issued Id
					openBadgesDAO.insertEvidence(bean.getEvidenceBean());

				} catch (Exception e) {
					StringWriter errors = new StringWriter();
					 e.printStackTrace(new PrintWriter(errors));
					logger.info(
							"Error while issuing badge, inserting evidence and collecting evidence for sapid : {} and subject {} "
									+ errors.toString(),
							bean.getSapid(), bean.getAwardedAt());
				}

			}

		}

	/**
	 * issued the badges of portal visit streak for a given month and year
	 * @param month
	 * @param year
	 * @param tableNames
	 * @return
	 * @author anilkumar.prajapati
	 * @throws ParseException 
	 */
	public HashMap<String, String> callStreakForAllStudent(String monthName, Integer year, String tableNames) throws ParseException {
		List<OpenBadgesCriteriaParamBean> criteriaParamBean = openBadgesDAO.getCriteriaDetails("portalVisitStreak");
		System.out.println(" Batch Job For Portal Visit Streak  Started criteriaParamBean size "+criteriaParamBean.size());
		logger.info(" Batch Job For Portal Visit Streak Started " );
		HashMap<String,String> response = new HashMap<String,String>();
		
		//set the start date and end date for getting page visit details
		Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(monthName);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
		LocalDate startDate = LocalDate.parse(year+"-"+"0"+cal.get(Calendar.MONTH)+"-01");
		LocalDate endDate = startDate.plusMonths(7).minusDays(1);
		List<String> tableNameList = Arrays.asList(tableNames.split(","));
		
			for(OpenBadgesCriteriaParamBean bean : criteriaParamBean) {
				try {
					int result_count =  processStreakForAllStudent(bean.getBadgeId(), Integer.parseInt(bean.getCriteriaValue()), 
							monthName, startDate, endDate, tableNameList);
					response.put("Issued For Portal Visit Streak For badge  "+bean.getBadgeId(),String.valueOf(result_count));
					result_count = 0;
				}catch(Exception e) {
					logger.error(" Error in Processing (processStreakForAllStudent) For BadgeId "+bean.getBadgeId() +"  And For Sub "+
							bean.getCriteriaValue(),e);
				}
			}
		 System.out.println(" Batch Job For Streak Ended ");
		 logger.info(" Batch Job For Streak Ended ");
		 logger.info("Result "+response);
		return response;
	}

	private int processStreakForAllStudent(Integer badgeId, Integer criteriaValue,String monthName, LocalDate startDate, 
		LocalDate endDate, List<String> tableNameList) {
		logger.info(" processStreakForAllStudent FOR Badge Id "+badgeId );
		Integer totalStudentsEntryPerBadge = 0;
 		List<OpenBadgesUsersBean> getMasterKeyListByBadgeId = openBadgesDAO.getMasterKeyListByBadgeId(badgeId);
		 
		 for (OpenBadgesUsersBean openBadgeBean : getMasterKeyListByBadgeId) {
			 try {
				 List<Integer> semList = openBadgesDAO.getSemesterListByMasterKey(openBadgeBean.getConsumerProgramStructureId());
				 
				 //get applicable list of streak details for each masterKeys
				 List<OpenBadgesUsersBean> criteriaMetUserList = getStreakDetailsList(badgeId, openBadgeBean.getConsumerProgramStructureId(), 
				 criteriaValue, semList, monthName, startDate, endDate, tableNameList);
				 
				 totalStudentsEntryPerBadge += criteriaMetUserList.size();
				 logger.info("Total Number Of Students  "+criteriaValue+ " And Masterkey "+openBadgeBean.getConsumerProgramStructureId()+" :-  "+
				 criteriaMetUserList.size());
					 
				 if(criteriaMetUserList != null && criteriaMetUserList.size() > 0) {
					 createBadgeIssuedEntryByListIfNotPresent(criteriaMetUserList, badgeId);
					 createStreakEvidenceEntry(criteriaMetUserList);
					 logger.info("criteriaMetUserList {} for badgeId {}  criteriaValue {} ", criteriaMetUserList, badgeId, criteriaValue);
				 }
			} catch (Exception e) {
				logger.info("Error in getting streak details list for badgeId {} masterKey {} criteriavelue {} due to {}",
				badgeId, openBadgeBean.getConsumerProgramStructureId(), criteriaValue, e);
			}
		 }
		 return totalStudentsEntryPerBadge;
	}
	
	private List<OpenBadgesUsersBean> getStreakDetailsList(Integer badgeId, Integer consumerProgramStructureId,
			Integer criteriaValue, List<Integer> semList, String monthName, LocalDate startDate, LocalDate endDate, List<String> tableNameList) {
			
			List<OpenBadgesPortalVisitStreakDto> tempList = new ArrayList<OpenBadgesPortalVisitStreakDto>();
			try {
				for (Integer sem : semList) {
					List<String> sapIdList = openBadgesDAO.getSapidList(monthName, startDate.getYear(), consumerProgramStructureId, sem);
					
					for (String sapId : sapIdList) {
						for(String tableName : tableNameList) {
							Map<String, OpenBadgesPortalVisitStreakDto> mapOfPageVisitDetails = openBadgesDAO.getPageVisitDetails(sapId, 
									startDate.toString(), endDate.toString(), tableName);
							
							//applicable sapId will get portal visit streak for given criteria value
							List<String> streakDates = getApplicableSapIdForStreak(mapOfPageVisitDetails, criteriaValue, sapId);
							
							if(!streakDates.isEmpty())
								tempList.add(getStudentDetailsForStreak(sapId, sem, criteriaValue, streakDates));
						}
					}
				}
			} catch (Exception e) {
				logger.info("Error in getting streak details list for badgeId {} masterKey {} criteriavelue {} semList {}  due to {}",
				badgeId, consumerProgramStructureId, criteriaValue, semList, e);
			}
			return mapStreakDtoToBean(tempList);
		}
	
	//logic for checking weather sapId is applicable for the portal visit streak
	private List<String> getApplicableSapIdForStreak(Map<String, OpenBadgesPortalVisitStreakDto> mapOfPageVisitDetails,Integer criteriaValue, String sapId) {
		List<String> finalStreakDates = new ArrayList<String>();
		Integer count = 0;
		
		for (String createdDate : mapOfPageVisitDetails.keySet()) {
			LocalDate currentDate = LocalDate.parse(createdDate);
			List<String> streakDates = new ArrayList<String>();

			if (hasSpentFifteenMinutes(mapOfPageVisitDetails.get(createdDate), FIFTEEN_MINUTES)) {
				streakDates.add(currentDate.toString());
				count++;
				LocalDate nextDate = currentDate.plusDays(1);

				while (mapOfPageVisitDetails.containsKey(nextDate.toString()) && count != criteriaValue) {
					if (hasSpentFifteenMinutes(mapOfPageVisitDetails.get(nextDate.toString()), FIFTEEN_MINUTES)) {
						streakDates.add(nextDate.toString());
						nextDate = nextDate.plusDays(1);
						count++;
					}
					else{
						break;
					}
				}
			}
			if (count == criteriaValue) {
				finalStreakDates.addAll(streakDates);
				break;
			}
			count = 0;
		}
		return finalStreakDates;
	}

	//check weather user has spent 15 minutes or more
	private boolean hasSpentFifteenMinutes(OpenBadgesPortalVisitStreakDto bean, Integer fifteenMinutes) {
		
		if(bean.getTimeSpent() >= fifteenMinutes) 
			return true;
		else
			return false;
	}

	private OpenBadgesPortalVisitStreakDto getStudentDetailsForStreak(String sapId, Integer sem, Integer criteriaValue, List<String> streakDates) {
		
		OpenBadgesPortalVisitStreakDto streakDto = openBadgesDAO.getStudentDetailsForStreak(sapId);
		streakDto.setSapId(sapId);
		streakDto.setCriteriaValue(criteriaValue);
		streakDto.setSemester(sem);
		streakDto.setStreakDates(streakDates);
		
		return streakDto;
	}

	protected List<OpenBadgesUsersBean> mapStreakDtoToBean(List<OpenBadgesPortalVisitStreakDto> list) {
		 List<OpenBadgesUsersBean> tempList = new ArrayList<OpenBadgesUsersBean>();
		 for(OpenBadgesPortalVisitStreakDto dto : list) {
			 OpenBadgesUsersBean bean = new OpenBadgesUsersBean();
			 OpenBadgesEvidenceBean ebean = new OpenBadgesEvidenceBean();
			 bean.setUserId(dto.getUserId());
			 bean.setSapid(dto.getSapId());
			 bean.setAwardedAt(String.valueOf("Semester "+dto.getSemester()));
			 ebean.setEvidenceType("htmlText");
			 ebean.setEvidenceValue(getStreakEvidence(dto.getStreakDates()));
			 bean.setEvidenceBean(ebean);
			 tempList.add(bean);
		 }
		 return tempList;
	}
	
	private String getStreakEvidence(List<String> streakDates) {
		String evidenceValue = new String();
		try {
			for(String streakDate : streakDates) { 
				if(streakDates.indexOf(streakDate) == 0)
					evidenceValue += " "+streakDate;
				else
					evidenceValue += "<li> "+streakDate+"</li>";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in getting Streak Evidence due to {}", e);
		}
		
		return evidenceValue;
	}

	//creating evidence entry against portal visit streak for each criteria value
	protected void createStreakEvidenceEntry(List<OpenBadgesUsersBean> evidenceList) {

		 for(OpenBadgesUsersBean bean : evidenceList) {
			 try {
			 	OpenBadgesEvidenceBean evidenceBean = new OpenBadgesEvidenceBean();
			 	evidenceBean = bean.getEvidenceBean();
			 	evidenceBean.setCreatedBy("PortalVisitStreakScheduler");
		    	evidenceBean.setLastModifiedBy("PortalVisitStreakScheduler");
		    	openBadgesDAO.insertEvidence(evidenceBean);
			 }catch (Exception e) {
				logger.info("Error in creating Evidence entry for portal visit streak due to {}", e);
			}
		 }
		
	}
	
	//setting and replacing all the constant used for badge description and earning criteria
	 public void setKeywordForStreak(OpenBadgesIssuedBean openBadgesIssuedBean, String sapid) {
		 try {
			 BadgeKeywordsDto dto = new BadgeKeywordsDto();
			 OpenBadgesPortalVisitStreakDto badgeDetails = openBadgesDAO.getbadgeDetailsBySapId(sapid, openBadgesIssuedBean.getAwardedAt().replaceAll("Semester ", "").trim());
			 dto.setSubjectName(openBadgesIssuedBean.getBadgeName());
			 dto.setSemester(openBadgesIssuedBean.getAwardedAt().replaceAll("Semester ", "").trim());
			 dto.setProgramNameFull(badgeDetails.getProgramNameFull());
			 
		    replaceAllConstant(openBadgesIssuedBean,dto);
		    openBadgesIssuedBean.setAwardedAt("Portal Visits");
		 }catch (Exception e) {
			 logger.info("Error in setting keyWords for sapId {} and badge name {} while getting Badge Details", sapid, openBadgesIssuedBean.getBadgeName());
		 }
	 }

	/**
	 * issued the badges of portal visit streak for a given month and year for one sapId
	 * @param month
	 * @param year
	 * @param tableNames
	 * @return
	 * @author anilkumar.prajapati
	 * @throws ParseException 
	 */
	public HashMap<String, String> callStreakForStudent(String monthName, Integer year, String sapId) throws ParseException {
		List<OpenBadgesCriteriaParamBean> criteriaParamBean = openBadgesDAO.getCriteriaDetails("portalVisitStreak");
		System.out.println(" Batch Job For Portal Visit Streak  Started criteriaParamBean size "+criteriaParamBean.size() );
		logger.info(" Batch Job For Portal Visit Streak Started " );
		HashMap<String,String> response = new HashMap<String,String>();
		
		//set the start date and end date for getting page visit details
		Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(monthName);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
		LocalDate startDate = LocalDate.parse(year+"-"+"0"+cal.get(Calendar.MONTH)+"-01");
		LocalDate endDate = startDate.plusMonths(7).minusDays(1);
		for(OpenBadgesCriteriaParamBean bean : criteriaParamBean) {
			try {
				int result_count =  processStreakForStudent(bean.getBadgeId(), Integer.parseInt(bean.getCriteriaValue()), 
				monthName, startDate, endDate, sapId);
				response.put("Issued For Portal Visit Streak For badge  "+bean.getBadgeId(),String.valueOf(result_count));
				result_count = 0;
			 }catch(Exception e) {
				 logger.error(" Error in Processing (processStreakForStudent) For BadgeId "+bean.getBadgeId() +"  And For Sub "+
				 bean.getCriteriaValue(),e);
			 }
		}
		 System.out.println(" Batch Job For Streak Ended ");
		 logger.info(" Batch Job For Streak Ended ");
		 logger.info("Result "+response);
		return response;
	}

	private int processStreakForStudent(Integer badgeId, Integer criteriaValue,String monthName, LocalDate startDate, 
			LocalDate endDate, String sapId) {
			logger.info(" processStreakForStudent FOR Badge Id "+badgeId );
			Integer totalStudentsEntryPerBadge = 0;
			try {
				 Integer masterKey = openBadgesDAO.getMasterKeyBySapId(sapId);
				 List<Integer> semList = openBadgesDAO.getSemesterListByMasterKey(masterKey);
				 
				 //get applicable list of streak details
				 List<OpenBadgesUsersBean> criteriaMetUser = getStreakDetails(badgeId, masterKey, 
				 criteriaValue, semList, monthName, startDate, endDate, sapId);
				 
				 totalStudentsEntryPerBadge += criteriaMetUser.size();
				 logger.info("Total Number Of Students  "+criteriaValue+ " And Masterkey "+masterKey+" :-  "+
				 criteriaMetUser.size());
					 
				 if(criteriaMetUser != null && criteriaMetUser.size() > 0) {
					 createBadgeIssuedEntryByListIfNotPresent(criteriaMetUser, badgeId);
					 createStreakEvidenceEntry(criteriaMetUser);
					 logger.info("criteriaMetUser {} for badgeId {}  criteriaValue {} ", criteriaMetUser, badgeId, criteriaValue);
				 }
			} catch (Exception e) {
				logger.info("Error in getting streak details for badgeId {} criteriavelue {} due to {}",
				badgeId, criteriaValue, e);
			}
		 return totalStudentsEntryPerBadge;
		}

	private List<OpenBadgesUsersBean> getStreakDetails(Integer badgeId, Integer masterKey, Integer criteriaValue,
			List<Integer> semList, String monthName, LocalDate startDate, LocalDate endDate, String sapId) {
		List<OpenBadgesPortalVisitStreakDto> tempList = new ArrayList<OpenBadgesPortalVisitStreakDto>();
		try {
			for (Integer sem : semList) {
				List<String> sapIdList = openBadgesDAO.getSapidList(monthName, startDate.getYear(), masterKey, sem);
				if(sapIdList.contains(sapId)) {
					Map<String, OpenBadgesPortalVisitStreakDto> mapOfPageVisitDetails = openBadgesDAO.getPageVisitDetailsBySapId(sapId, 
							startDate.toString(), endDate.toString());
					
					//applicable sapId will get portal visit streak for given criteria value
					List<String> streakDates = getApplicableSapIdForStreak(mapOfPageVisitDetails, criteriaValue, sapId);
					
					if(!streakDates.isEmpty())
						tempList.add(getStudentDetailsForStreak(sapId, sem, criteriaValue, streakDates));
				}
			}
		} catch (Exception e) {
			logger.info("Error in getting streak details list for badgeId {} masterKey {} criteriavelue {} semList {}  due to {}",
			badgeId, masterKey, criteriaValue, semList, e);
		}
		return mapStreakDtoToBean(tempList);
	}
	
	 public String callLectureAttendanceBadgeForAllStudent(String year, String month) {
		 List<OpenBadgesCriteriaParamBean> criteriaParamBean = openBadgesDAO.getCriteriaDetails("lectureAttendance");
		 logger.info("\n OpenBadgesService >> call callLectureAttendanceBadgeForAllStudent >> criteriaParamBean size : "+criteriaParamBean.size());
			
		 for(OpenBadgesCriteriaParamBean bean : criteriaParamBean) {
			 try {
				  processLectureAttendanceBadge(bean.getBadgeId(), Integer.parseInt(bean.getCriteriaValue()),year, month );
			}catch(Exception e) {
				 e.printStackTrace();
			 }
		}
		
		return "";
	 }
	 
	 private void processLectureAttendanceBadge(Integer badgeId, Integer criteriaValue, String year, String month) {
		 logger.info("\n OpenBadgesService >> call processLectureAttendanceBadge >> criteriaValue : "+criteriaValue);
		 
		 
		 List<OpenBadgesUsersBean> masterKeyList = openBadgesDAO.getMasterKeyListByBadgeId(badgeId);
		 logger.info("\n OpenBadgesService >> call processLectureAttendanceBadge >> masterKeyList size : "+masterKeyList.size());
		 	
		 	
		 for(OpenBadgesUsersBean openBadgeBean : masterKeyList) {
			 try {
				 List<OpenBadgesUsersBean> criteriaMetUserList = getLectureAttendanceBadgeCriteriaMetUserList(badgeId, openBadgeBean.getConsumerProgramStructureId() , criteriaValue, year, month);
				 logger.info("\n OpenBadgesService >> call processLectureAttendanceBadge >> badgeId : "+badgeId+"  ConsumerProgramStructureId :  "+openBadgeBean.getConsumerProgramStructureId()+"  criteriaMetUserList size : "+criteriaMetUserList.size());
				 
				 for(OpenBadgesUsersBean userbean : criteriaMetUserList) {
					
					 try { 
					 Long issuedId = createBadgeIssuedEntryByBean(userbean, badgeId); 
					 createEvidenceEntryByList(userbean.getEvidenceBeanList(), issuedId);
					 earnedBadgesNotificationService.sendEmail(userbean.getSapid(), badgeId, issuedId, userbean.getAwardedAt());
					}catch (Exception e) {
						// TODO: handle exception
						 logger.error("Error while creating issued badge entry : " +e.getMessage());
					}
					
				 }
			 }catch(Exception e) {
				 e.printStackTrace();
			 }
		 } 
	 }
	 
	 private List<OpenBadgesUsersBean> getLectureAttendanceBadgeCriteriaMetUserList(Integer badgeId, Integer consumerProgramStructureId, Integer criteriaValue, String year, String month) {
		 List<OpenBadgesUsersBean> criteriaMetUserList = new ArrayList<OpenBadgesUsersBean>();
		 List<OpenBadgeLectureAttendanceBean> notIssuedBadgeUserList = openBadgesDAO.getNotIssuedBadgeUserIdList(badgeId, consumerProgramStructureId, year, month);
		 logger.info("\n OpenBadgesService >> call getLectureAttendanceBadgeCriteriaMetUserList >> notIssuedBadgeUserList size : "+notIssuedBadgeUserList.size());
		 int count = 0;	
		 for(OpenBadgeLectureAttendanceBean bean : notIssuedBadgeUserList ) {
			 OpenBadgesUsersBean usersBean = new OpenBadgesUsersBean();
			 count++;
			 try {
				 List<OpenBadgeLectureAttendanceBean> list = openBadgesDAO.getLectureAttendanceDataForProcess(bean.getProgram_sem_subject_id(), bean.getYear(), bean.getMonth(), bean.getSubject(), bean.getSapid());
				 logger.info("\n OpenBadgesService >> call getLectureAttendanceBadgeCriteriaMetUserList >> "+count+" : "+notIssuedBadgeUserList.size()+"  masterKey : "+consumerProgramStructureId+"  getLectureAttendanceDataForProcess size : "+list.size());
				if(list.size() >= criteriaValue) {	
				 usersBean =  runLectureAttendanceStreakLogic(list, criteriaValue);
				 usersBean.setUserId(bean.getUserId());
				 usersBean.setAwardedAt(bean.getSubject());
				 usersBean.setSapid(bean.getSapid());
					
				 if(usersBean.getIsBadgeIssued() == 1) {
					 criteriaMetUserList.add(usersBean);
				 }
				} 
			 }catch (Exception e) {
				// TODO: handle exception
				 logger.error("Error in OpenBadgesService >> call getLectureAttendanceBadgeCriteriaMetUserList >> : " +e.getMessage());
			}
		 }
		 return criteriaMetUserList; 
	 }
	 
	 private OpenBadgesUsersBean runLectureAttendanceStreakLogic(List<OpenBadgeLectureAttendanceBean> lectureAttendanceList, Integer criteriaValue) {
		Integer count = 0;
		OpenBadgesUsersBean usersBean = new OpenBadgesUsersBean();
		usersBean.setIsBadgeIssued(0);
		List<OpenBadgesEvidenceBean> evidenceList = new ArrayList<OpenBadgesEvidenceBean>();
		HashMap<String, Integer> criteriaMetHashMap = new HashMap<>();
		forLoopLabel : for(OpenBadgeLectureAttendanceBean list : lectureAttendanceList) {
			
			String sessionName = list.getSessionName();
			
			 if(sessionName != null) {
				sessionName = sessionName.replaceAll("\\s", ""); 
			 }
			
			 if( "Y".equals(list.getAttended()) && !criteriaMetHashMap.containsKey(sessionName) ) {
				 count++;  
						
			 }else if( !"Y".equals(list.getAttended()) && !criteriaMetHashMap.containsKey(sessionName) ){
				 count = 0;
				 criteriaMetHashMap.clear();
				 evidenceList.clear();
			 }	
				 
			 if(count > 0 && !criteriaMetHashMap.containsKey(sessionName)) {
				
				 criteriaMetHashMap.put(sessionName, list.getSessionId());	
				 
				 OpenBadgesEvidenceBean evidenceBean = new OpenBadgesEvidenceBean();
				 
				 String evidenceValue = "<p>Session Name : <strong>"+list.getSessionName()+"  "+ (list.getTrack() != null ? list.getTrack() : "") +"</strong></p>  "+
				 						"<p>Session Time : <strong>"+list.getSessionTime()+"</strong></p>"+
				 						"<p>Session Attend Time : <strong>"+list.getAttendTime()+"</strong></p>" ;
				 
				 evidenceBean.setEvidenceType("html");
				 evidenceBean.setEvidenceValue(evidenceValue);
				 evidenceBean.setCreatedBy("LectureAttendanceScheduler");
				 evidenceBean.setLastModifiedBy("LectureAttendanceScheduler");
				 evidenceList.add(evidenceBean);
				 
				 if(criteriaValue == count) {
					 usersBean.setIsBadgeIssued(1);
					 break forLoopLabel;
				 } 
			 }				 
		 }
			
		 usersBean.setEvidenceBeanList(evidenceList);
		 return usersBean; 
	 }
	 
	 private void createEvidenceEntryByList(List<OpenBadgesEvidenceBean> evidenceBean, Long issuedId) {
			System.out.println("OpenBadgesService >> call createEvidenceEntryByList >> evidenceBean size : "+evidenceBean.size());
			logger.info("\n OpenBadgesService >> call createEvidenceEntryByList >> evidenceBean size : "+evidenceBean.size());
			Integer count = 0 ;
			Integer successCount = 0 ;
			Integer errorCount = 0 ;
			for(OpenBadgesEvidenceBean bean : evidenceBean) {
				try {
					count++;
					bean.setIssuedId(BigInteger.valueOf(issuedId));
					createEvidenceEntryByBean(bean);
					System.out.println(count+" createEvidenceEntryByList ");
					logger.info("\n OpenBadgesService >> call createEvidenceEntryByList "+count);
					successCount++; 
				}catch(Exception e) {
					e.printStackTrace();
					System.out.println(count+" createEvidenceEntryByList >> error");
					logger.error("\n OpenBadgesService >> call createEvidenceEntryByList >> error count "+count);
					logger.error("\n OpenBadgesService >> call createEvidenceEntryByList >> error message "+e.getMessage());
					errorCount++;
				}
			} 
			
			logger.info("\n OpenBadgesService >> createEvidenceEntryByList Scheduler successCount "+successCount+" and  errorCount "+errorCount );
		}
	 
	 private void createEvidenceEntryByBean(OpenBadgesEvidenceBean evidenceBean) {
		 	evidenceBean.setCreatedBy("ProgramCompletionScheduler");
	    	evidenceBean.setLastModifiedBy("ProgramCompletionScheduler");
	    	openBadgesDAO.insertEvidence(evidenceBean);
	 }
	
	 public HashMap<String, String> callTopInProgramBadgeForAllStudent(
			 String examMonth, Integer examYear
			 ) {
		 HashMap<String, String> response = new HashMap<>();
		 List<Integer> masterKeyList = openBadgesDAO.getMasterKeyListbyCriteriaName("topInProgram");
		 logger.info(" callTopInSemesterBadgeForAllStudent >> masterKeyList  size : "+masterKeyList.size());
		 HashMap<Integer, List<OpenBadgesTopInProgramDto>> map = getMasterKeyTopInProgramList(masterKeyList, examMonth, examYear );
		 
		 List<OpenBadgesCriteriaParamBean> criteriaParamBean = openBadgesDAO.getCriteriaDetails("topInProgram");
		 System.out.println(" callTopInSemesterBadgeForAllStudent >> criteriaParamBean size : "+criteriaParamBean.size());
		 logger.info(" callTopInSemesterBadgeForAllStudent >> criteriaParamBean size : "+criteriaParamBean.size());
			
		 for(OpenBadgesCriteriaParamBean bean : criteriaParamBean) {
			 try {
				Integer result_count =  processTopInProgramBadge(bean.getBadgeId(), Integer.parseInt(bean.getCriteriaValue()),
						 examMonth, examYear, map
						 );
				response.put("Issued For Portal Visit Streak For badge  "+bean.getBadgeId(),result_count.toString());
			 }catch(Exception e) {
				 e.printStackTrace();
			 }
		}
		
		return response;
	 }
	 
	 private Integer processTopInProgramBadge(Integer badgeId, Integer criteriaValue,
			 String examMonth, Integer examYear, HashMap<Integer, List<OpenBadgesTopInProgramDto>> criteriaMetUserMap
			 ) {
		 Integer totalStudentsEntryPerBadge = 0;
		 System.out.println(" processTopInSemesterBadge >> criteriaValue : "+criteriaValue );
		 logger.info(" processTopInSemesterBadge  criteriaValue : "+criteriaValue);
		 
		 
		 List<OpenBadgesUsersBean> masterKeyList = openBadgesDAO.getMasterKeyListByBadgeId(badgeId);
		 System.out.println(" processTopInSemesterBadge >> masterKeyList size : "+masterKeyList.size());
		 logger.info(" processTopInSemesterBadge >> masterKeyList size : "+masterKeyList.size());
		 	
		 	
		 for(OpenBadgesUsersBean openBadgeBean : masterKeyList) {
			 try {
				 List<OpenBadgesTopInProgramDto> list = criteriaMetUserMap.get(openBadgeBean.getConsumerProgramStructureId());
				 logger.info("list "+list.toString()+" \n");
				 
				 List<OpenBadgesTopInProgramDto> criteriaMetList = list.stream()
				 .filter(myObject2 -> criteriaValue.equals(myObject2.getRank()))
			     .collect(Collectors.toList());
				 logger.info("criteriaValue : "+criteriaValue+" criteriaMetList "+criteriaMetList.toString()+" \n");
				 
				 List<OpenBadgesUsersBean> criteriaMetUserList = mapTopInProgramDtoToBean(criteriaMetList, badgeId);
				 totalStudentsEntryPerBadge += criteriaMetUserList.size();
				 
				 if(criteriaMetUserList != null && criteriaMetUserList.size() > 0) {
					 createBadgeIssuedEntryByList(criteriaMetUserList, badgeId);
					 createProgramCompletionEvidenceEntry(badgeId);
				 }
			 }catch(Exception e) {
//				 e.printStackTrace();
			 }
		 } 
		 return totalStudentsEntryPerBadge;
	 }
	 
	 protected HashMap<Integer, List<OpenBadgesTopInProgramDto>> getMasterKeyTopInProgramList(List<Integer> masterKeyList, String examMonth, Integer examYear){
		 HashMap<Integer, List<OpenBadgesTopInProgramDto>> map = new HashMap<Integer, List<OpenBadgesTopInProgramDto>>();
		 for(Integer masterKey : masterKeyList) {
			 try {
				 logger.info(" masterKey "+masterKey);
				 List<OpenBadgesTopInSemesterDto> semList = openBadgesDAO.getSemesterListForTopInSemesterBadge(masterKey);
				  semList = semList.stream()
						 .sorted(Comparator.comparing(OpenBadgesTopInSemesterDto::getSem).reversed())
						 .collect(Collectors.toList());
				  logger.info(" semList "+semList.toString()+" \n");
				  List<OpenBadgesTopInProgramDto> criteriaMetUserList = getTopInProgram(
						 semList, masterKey , 
						 examMonth, examYear
						 );
				  map.put(masterKey,criteriaMetUserList);
			 }catch (Exception e) {
				// TODO: handle exception
			}
		 }
		 return map;
	 }
	 
	 protected List<OpenBadgesTopInProgramDto> getTopInProgram(
			 List<OpenBadgesTopInSemesterDto> semList,  
			 Integer consumerProgramStructureId,
			 String examMonth, Integer examYear
			 ){
		 
		 String examOrder =  openBadgesDAO.getExamOrder( examMonth, examYear);
		 
		 
		 List<OpenBadgesTopInProgramDto> tempList = new ArrayList<OpenBadgesTopInProgramDto>();
		 for(OpenBadgesTopInSemesterDto dto : semList  ) {
			 try {
				 logger.info(" examOrder "+examOrder);
				 OpenBadgesTopInProgramDto topInProgramdto = openBadgesDAO.getYearMonthForTopInProgram(examOrder);
				 topInProgramdto.setSem(dto.getSem());
				 topInProgramdto.setSubjectCount(dto.getSubjectCount());
				 logger.info(" ExamMonth() : "+topInProgramdto.getExamMonth()+" AcadMonth()"+topInProgramdto.getAcadMonth()
				 + " Year() :"+topInProgramdto.getYear()+" getSubjectCount() : "+topInProgramdto.getSubjectCount()+" getSem() : "+topInProgramdto.getSem());
				 logger.info("consumerProgramStructureId : "+consumerProgramStructureId+" topInProgramdto "+topInProgramdto.toString());
				 List<OpenBadgesTopInProgramDto> list = openBadgesDAO.getTotalMarksBYSemForTopInProgram(topInProgramdto , consumerProgramStructureId);
				 logger.info(" TotalMarksBYSemForTopInProgram size() "+list.size());
				 if(tempList == null || tempList.size() == 0 ) {
					 tempList.addAll(list) ;
				 }else {
					 tempList = setTopInProgram(tempList, list);
				 }
			 }catch (Exception e) {
				// TODO: handle exception
			}
			 examOrder = new BigDecimal(examOrder).subtract(new BigDecimal("1.00")).toString(); 
		 }
		 
		 setRankTopInProgram(tempList);
		 logger.info("tempList "+tempList.toString()+" \n");
		 return tempList;
	 }
	 
	 public  List<OpenBadgesTopInProgramDto> setTopInProgram(List<OpenBadgesTopInProgramDto> tempList, List<OpenBadgesTopInProgramDto> list) {
		 logger.info(" tempList size() "+tempList.size());
		 List<OpenBadgesTopInProgramDto> dtolist = new ArrayList<OpenBadgesTopInProgramDto>();
		 for(OpenBadgesTopInProgramDto tmpdto : tempList) {
			 for(OpenBadgesTopInProgramDto dto : list) {
				 if(tmpdto.getSapid().equals(dto.getSapid())) {
					 Integer outOfMarks = tmpdto.getOutOfMarks() + dto.getOutOfMarks();
					 Integer totalMarks = tmpdto.getTotalMarks() + dto.getTotalMarks();
					 
					 tmpdto.setOutOfMarks(outOfMarks);
					 tmpdto.setTotalMarks(totalMarks);
					 dtolist.add(tmpdto);
				 }
			 }
		 }
		 logger.info(" dtolist size() "+dtolist.size());
		return dtolist;
	 }
	 
	 
	 public void setRankTopInProgram(List<OpenBadgesTopInProgramDto> list) {
		 Integer prev_val = null;
		 int rank = 0;
		 list =list.stream()
		 .sorted(Comparator.comparing(OpenBadgesTopInProgramDto::getTotalMarks).reversed())
		 .collect(Collectors.toList()); 	
		 
		 for(OpenBadgesTopInProgramDto bean : list) {
			 if(prev_val == bean.getTotalMarks()) {
				 bean.setRank(rank);
			 }else {
				 rank  = rank + 1; 
				 prev_val = bean.getTotalMarks();
				 bean.setRank(rank);
			 }
		 }
	 }
	 
	 protected List<OpenBadgesUsersBean> mapTopInProgramDtoToBean(List<OpenBadgesTopInProgramDto> list, Integer badgeId){
		 List<OpenBadgesUsersBean> tempList = new ArrayList<OpenBadgesUsersBean>();
		 for(OpenBadgesTopInProgramDto dto : list) {
			 OpenBadgesUsersBean bean = new OpenBadgesUsersBean();
			 Integer userId= openBadgesDAO.getUserIdIfBadgeNotIssued(dto.getSapid(), badgeId);
			 bean.setUserId(userId);
			 bean.setSapid(dto.getSapid());
			 bean.setAwardedAt(dto.getProgram());
			 
			 tempList.add(bean);
		 }
		 return tempList;
	 }
	 
	 public void setKeywordForTopinProgram(OpenBadgesIssuedBean openBadgesIssuedBean, String sapid) {
		 try {
			 BadgeKeywordsDto dto = new BadgeKeywordsDto();
			 dto.setProgramNameFull(openBadgesDAO.getProgramName(sapid));
			 replaceAllConstant(openBadgesIssuedBean,dto);
		 }catch (Exception e) {
			// TODO: handle exception
		}
	 }

	@Override
	public OpenBadgesUsersBean getDashboardBadgeList(String sapid, Integer CPSId) {

		OpenBadgesUsersBean usersBean = new OpenBadgesUsersBean();
		Integer userId = getBadgeUserId(sapid, CPSId);		

		List<OpenBadgesUsersBean> list = openBadgesDAO.getDashboardBadgeList(userId);

		if (list.size() > 0 && Objects.nonNull(userId)) {
			usersBean.setEarnedBadgeList(list);
			return usersBean;
		}
		return usersBean;
	}
}
