package com.nmims.listeners;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.nmims.services.OpenBadgesService;


@Configuration
@EnableScheduling
public class OpenBadgeScheduler {
	
	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Autowired
	private OpenBadgesService openBadgesService;
	
	private static final Logger logger = LoggerFactory.getLogger("badgeScheduler");
	
	@Scheduled(cron = "0 0 3 * * ?")
	public void runOpenBadgeScheduler() {
		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running runOpenBadgeScheduler : scheduler since this is not tomcat4. This is "+SERVER);
			logger.info("Not running runOpenBadgeScheduler : scheduler since this is not tomcat4. This is "+SERVER);
			return;
		}
		
		System.out.println("start running runBadgeUserEntryScheduler  ");
		logger.info("start running runBadgeUserEntryScheduler  ");
		String result = openBadgesService.createBadgeUserEntry();
		System.out.println("end running runBadgeUserEntryScheduler result : "+result);
		logger.info("end running runBadgeUserEntryScheduler ");
		
		System.out.println("start running runAskQueryBadgeScheduler  ");
		logger.info("start running runAskQueryBadgeScheduler  ");
		result = openBadgesService.callAskQueryProcForAllStudent();
		System.out.println("end running runAskQueryBadgeScheduler result : "+result);
		logger.info("end running runAskQueryBadgeScheduler ");
		

		System.out.println("start running runSubmitAssignmentBadgeScheduler ");
		logger.info("start running runSubmitAssignmentBadgeScheduler ");
		result = openBadgesService.callSubmitAssignmentProcForAllStudent();
		System.out.println("end running runSubmitAssignmentBadgeScheduler result : "+result);
		logger.info("end running runSubmitAssignmentBadgeScheduler ");
		
		System.out.println("start running callReRegistrationForSemBadgeForAllStudent ");
        logger.info("start running callReRegistrationForSemBadgeForAllStudent ");
        HashMap<String,String> result_count_rereg = openBadgesService.callReRegistrationForSemBadgeForAllStudent();
        System.out.println("end running callReRegistrationForSemBadgeForAllStudent result : "+result_count_rereg);
        logger.info("end running callReRegistrationForSemBadgeForAllStudent ");

        System.out.println("start running runNgasceAlumniBadgeScheduler ");
        logger.info("start running runNgasceAlumniBadgeScheduler ");
        HashMap<String,String> result_count_alumni = openBadgesService.runNgasceAlumniBadgeScheduler();
        System.out.println("end running runNgasceAlumniBadgeScheduler result : "+result_count_alumni);
        logger.info("end running runNgasceAlumniBadgeScheduler ");
        
        System.out.println("start running runLectureAttendanceStreakBadge ");
        logger.info("start running runLectureAttendanceStreakBadge :");
        openBadgesService.callLectureAttendanceBadgeForAllStudent(CURRENT_ACAD_YEAR, CURRENT_ACAD_MONTH);
        System.out.println("end running runLectureAttendanceStreakBadge :");
        logger.info("end running runLectureAttendanceStreakBadge ");
		
	}
	
}
