package com.nmims.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nmims.beans.*;
import com.nmims.daos.DashboardDAO;
import com.nmims.helpers.ApplozicGroupHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletConfigAware;

import javax.servlet.ServletConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("configurationScheduler")
public class ConfigurationScheduler implements ApplicationContextAware, ServletConfigAware {

    @Value("${SERVER}")
    private String SERVER;

    @Value("${ENVIRONMENT}")
    private String ENVIRONMENT;

    @Value("${APPLOZIC_OF_USER_ID}")
    private String APPLOZIC_OF_USER_ID;

    @Value("${APPLOZIC_GROUP_IMG_URL}")
    private String APPLOZIC_GROUP_IMG_URL;

    @Autowired
    ApplozicGroupHelper applozicGroupHelper;
    

    private static ApplicationContext act = null;
    private static ServletConfig sc = null;
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationScheduler.class);

//    @Autowired
//    ApplozicGroupHelper applozicGroupHelper;

    @Override
    public void setServletConfig(ServletConfig sc) {
        this.sc = sc;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.act = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return act;
    }

    public String getInitials(String str) {
        String[] arr = str.split(" ");
        String finalResult = "";
        for (String s : arr) {
            if (s.equals("-") || s.equals(":") || Character.isLowerCase(s.charAt(0)) || s.equals("&")) {
                continue;
            }
            finalResult += Character.toUpperCase(s.charAt(0));
            if (s.contains(":")) {
                finalResult += s.split(":").length>1?Character.toUpperCase(s.split(":")[1].charAt(0)):"";
            }
            if (s.contains("-")) {
                finalResult += s.split("-").length>1?Character.toUpperCase(s.split("-")[1].charAt(0)):"";
            }
            if (s.contains("&")) {
                finalResult += s.split("&").length>1?Character.toUpperCase(s.split("&")[1].charAt(0)):"";
            }
        }
        return finalResult;
    }

  // @Scheduled(fixedDelay = 1440 * 60 * 1000)    // every 24 hours
    public void checkSubjectTimeboundDuration() {

//        if(!"tomcat4".equalsIgnoreCase(SERVER)){
          //  return;
      //  }
		logger.info("--------------INSIDE Chat group creation SCHEDULER START---------------------");
        DashboardDAO dao = (DashboardDAO) act.getBean("dashboardDAO");
        List<TimeBoundUserMapping> list = dao.getTimeboundUserMappingList();

        logger.info("Total Student Count : " + list.size());
        int current_user = 1 ;
        int failure_count = 0;
        int success_count = 0;
        int already_exists = 0;
        
        List<TimeBoundUserMapping> failure_list = new ArrayList<TimeBoundUserMapping>();
        JsonObject responseJsonObject = new JsonObject();
        try {
            for (TimeBoundUserMapping bean : list) {
                logger.info("Current User: " + current_user + "/" + list.size());
                logger.info("Success: " + success_count);
                logger.info("Already Exists: " + already_exists);
                logger.info("Failure: " + failure_count);
                logger.info("Student: " + bean.toString());
            	
            	 if("Course Coordinator".equalsIgnoreCase(bean.getRole())){
            	     logger.info("Group -->" + bean.getName());
            	     logger.info("Adding Course Coordinator : " + bean.getUserId());
            		 APPLOZIC_OF_USER_ID = bean.getUserId();
            		 
                 }
                List<SubjectGroupsBean> subjectGroupsBeans = dao.getSubjectsToCheckIfExistsOrNotInSubjectGroups(bean);


                for (SubjectGroupsBean subjectGroupsBean : subjectGroupsBeans) {
                    ApplozicGroupBean applozicGroupBean=new ApplozicGroupBean();
                    long appLozicGroupPrimaryKey, subjectGroupPrimaryKey;
                    boolean exist = dao.checkIfRecordExistsInSubjectGroup(subjectGroupsBean);

                    logger.info("Checking "+subjectGroupsBean.getSubject()+" subject group is synced or not? : "+exist);

                    if (!exist) {
                    	logger.info("Create Group API Start--> ");
                        logger.info(subjectGroupsBean.getBatchName());

                        subjectGroupsBean.setSubject_initials(getInitials(subjectGroupsBean.getSubject()));
                          

                        String chat_group_name = null;
                        
                  		Pattern batchOneCheck = Pattern.compile("(?=.*batch 1)", Pattern.CASE_INSENSITIVE );
                  		Pattern batchTwoCheck = Pattern.compile("(?=.*batch 2)", Pattern.CASE_INSENSITIVE );
                  		Pattern batchThreeCheck = Pattern.compile("(?=.*batch 3)", Pattern.CASE_INSENSITIVE );
                  		Pattern batchFourCheck = Pattern.compile("(?=.*batch 4)", Pattern.CASE_INSENSITIVE );
                  		Pattern batchFiveCheck = Pattern.compile("(?=.*batch 5)", Pattern.CASE_INSENSITIVE );
                  		Pattern newSpecializationCheck = Pattern.compile("(?=.*new)", Pattern.CASE_INSENSITIVE );
                      
                  		if( StringUtils.isBlank(subjectGroupsBean.getSpecialisation_initials()) ) {

                  			if( batchOneCheck.matcher( subjectGroupsBean.getBatchName() ).find() ) {

                  				chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getTerm() + "-B1-" + 
                  						subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}else if( batchTwoCheck.matcher( subjectGroupsBean.getBatchName() ).find() ) {

                  				chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getTerm() + "-B2-" + 
                  						subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}else if( batchThreeCheck.matcher( subjectGroupsBean.getBatchName() ).find() ) {

                  				chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getTerm() + "-B3-" + 
                  						subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}else if( batchFourCheck.matcher( subjectGroupsBean.getBatchName() ).find() ) {

                  				chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getTerm() + "-B4-" + 
                  						subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}else if( batchFiveCheck.matcher( subjectGroupsBean.getBatchName() ).find() ) {

                  				chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getTerm() + "-B5-" + 
                  						subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}else {
                        	  
                  				chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getTerm() + "-B1-" + 
                  						subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}
                          
                  		}else {

                  			if( batchOneCheck.matcher( subjectGroupsBean.getBatchName() ).find() ) {

                  				chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getSpecialisation_initials() + "-B1-" + 
                  						subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}else if( batchTwoCheck.matcher( subjectGroupsBean.getBatchName() ).find() ) {

                  				chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getSpecialisation_initials() + "-B2-" + 
                  						subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}else if( batchThreeCheck.matcher( subjectGroupsBean.getBatchName() ).find() ) {

                  				chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getSpecialisation_initials() + "-B3-" + 
                  						subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}else if( batchFourCheck.matcher( subjectGroupsBean.getBatchName() ).find() ) {

                  				chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getSpecialisation_initials() + "-B4-" + 
                  						subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}else if( batchFiveCheck.matcher( subjectGroupsBean.getBatchName() ).find() ) {

                  				chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getSpecialisation_initials() + "-B5-" + 
                  						subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}else if( newSpecializationCheck.matcher( subjectGroupsBean.getBatchName() ).find() ) {

                    		  	chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getSpecialisation_initials() + "-B2-" + 
                    				  subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}else {

                  				chat_group_name =  subjectGroupsBean.getSubject_initials() + "-" + subjectGroupsBean.getSpecialisation_initials() + "-B1-" + 
                  						subjectGroupsBean.getAcadMonth() + Integer.toString(subjectGroupsBean.getAcadYear()).substring(2);
                              
                  			}
                      }

                      boolean exist1 = dao.checkIfApplozicGroupExist(chat_group_name);

                        logger.info("Checking "+subjectGroupsBean.getSubject()+" subject groupo is synced with applogic or not? : "+exist1);

                        if(!exist1) {
                            
                        	logger.info(subjectGroupsBean.getSubject() +" subject group is left to be synced with applogic!");
                            
                        	ApplozicCreateGroupBean applozicCreateGroupBean = new ApplozicCreateGroupBean();
                            if("Course Coordinator".equalsIgnoreCase(bean.getRole())){
                                applozicCreateGroupBean.setAdmin(bean.getUserId());
                            
                            applozicCreateGroupBean.setGroupName(chat_group_name);
                            applozicCreateGroupBean.setImageUrl(APPLOZIC_GROUP_IMG_URL); // set group icon
                            applozicCreateGroupBean.setType(1); // 1 for private group & 2 for public group

                           // ApplozicGroupHelper applozicGroupHelper = new ApplozicGroupHelper();
                           // String json = gson.toJson();
                            responseJsonObject = applozicGroupHelper.createApplozicGroup(applozicCreateGroupBean);

                            logger.info("Response object got from createApplozicGroup api : "+responseJsonObject);

                            if (responseJsonObject.get("status").getAsString().equals("success")) {
                                
                            	logger.info("SUCCESSFULLY APPLOZIC GROUP CREATED VIA API.");
                                logger.info("Successfully applogic group created : "+subjectGroupsBean);

                                applozicGroupBean.setGroupId(responseJsonObject.get("response").getAsJsonObject().get("id").getAsString());
                                applozicGroupBean.setClientGroupId(responseJsonObject.get("response").getAsJsonObject().get("clientGroupId").getAsString());
                                applozicGroupBean.setChat_group_name(responseJsonObject.get("response").getAsJsonObject().get("name").getAsString());
                                applozicGroupBean.setCreatedBy("Chat Group Creator Scheduler");
                                applozicGroupBean.setLastModifiedBy("Chat Group Creator Scheduler");
                                appLozicGroupPrimaryKey = dao.insertRecordInApplozicGroups(applozicGroupBean);
                                subjectGroupsBean.setApplozic_group_id((int)appLozicGroupPrimaryKey);
                                if (appLozicGroupPrimaryKey != 0) {
                                    
                                	logger.info("Successfully applozic group inserted in DB: "  + applozicGroupBean.toString());                                    
                                    
                                } else {
                                    logger.info("Successfully applozic group inserted in DB: "  + applozicGroupBean.toString());
                                	
                                }
                            }else {
                            	logger.info("FAILED TO CREATE APPLOZIC GROUP VIA API.");
                                logger.info("Failed to create applogic group : "+subjectGroupsBean);
                            }
                            }
                        } else{ 

                            logger.info("Applozic group already exists : "  + subjectGroupsBean);

                            appLozicGroupPrimaryKey=dao.getApplozicGroupIfExist(chat_group_name);
                            subjectGroupsBean.setApplozic_group_id((int)appLozicGroupPrimaryKey);
                        }


                        subjectGroupPrimaryKey=dao.insertRecordInSubjectGroup(subjectGroupsBean);
                        if (subjectGroupPrimaryKey != 0) {
                            
                        } else {
                        }
                        
                    } else{
                    	
                        try {
							subjectGroupsBean = dao.getSubjectGroupById(subjectGroupsBean);
						} catch (Exception e) {
							logger.info(e.getMessage());
							failure_count ++;
                            failure_list.add(bean);
							continue;
						}
                    }

                    if (subjectGroupsBean.getApplozic_group_id()!=null && !"Course Coordinator".equalsIgnoreCase(subjectGroupsBean.getRole())) {

                        ApplozicGroupBean applozicGroupBean1=dao.getApplozicGroupById(subjectGroupsBean.getApplozic_group_id());

                        ApplozicAddMemberToGroupBean applozicRegisterMemberBean = new ApplozicAddMemberToGroupBean();
                        applozicRegisterMemberBean.setUserId(subjectGroupsBean.getUserId());
                        applozicRegisterMemberBean.setClientGroupId(applozicGroupBean1.getClientGroupId());
                    
                        	if ("Student".equalsIgnoreCase(subjectGroupsBean.getRole())) {
                        	applozicRegisterMemberBean.setRole(3);
                        }
                        
                        try {
							responseJsonObject = applozicGroupHelper.addMemberToApplozicGroup(applozicRegisterMemberBean, APPLOZIC_OF_USER_ID);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.info(e.getMessage());
							failure_count ++;
                            failure_list.add(bean);
                            continue;
						}

                        logger.info("Response object got from addMemberToApplozicGroup api : "+responseJsonObject);

                        if (responseJsonObject.get("status").equals("success")) {
                        	if(responseJsonObject.get("response").equals("true")){

                                logger.info("User Already Exist: " + responseJsonObject + " in the group : " + subjectGroupsBean);
                                already_exists ++;
                                continue;


                        	}else if(responseJsonObject.get("response").equals("success")) {
                        		
                                logger.info("Successfully registered and added member: " + responseJsonObject + " in the group : " + subjectGroupsBean);
                                
                                success_count++;
                                continue;

                        	}
                        		success_count++;

                                continue;

                        }
                        
                        else if(!"Course Coordinator".equalsIgnoreCase(subjectGroupsBean.getRole())){
                            failure_count ++;
                            failure_list.add(bean);
                            continue;

                        }    
                    }
                }
                
                logger.info("Get Extra Details for Forming Batch Name Failed");
                current_user ++;
                continue;

            }
            logger.info("--------------INSIDE Chat group creation SCHEDULER END NO ERRORS---------------------");
            logger.info("Total Users " + current_user);
            logger.info("Success " + success_count);
            logger.info("Already Exists: " + already_exists);
            logger.info("Failure " + failure_count);

            

        } catch (Exception ex) {
            logger.error("Chat Group Creation Logic Exception",ex);
            
        }
    }
}
