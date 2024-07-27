package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentRankBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.CreateRankPDF;
import com.nmims.helpers.LinkedInManager;
import com.nmims.services.LeaderBoardService;
import java.lang.RuntimeException;

@Controller
@CrossOrigin(origins="*", allowedHeaders="*")
public class LeaderBoardController extends BaseController{
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	HomeController homeController;
	
	@Autowired
	LeaderBoardService leaderBoardService;
	
	@Autowired
	CreateRankPDF createRankPDF;
	
	@Autowired
	LinkedInManager linkedInManager;

	@Autowired
	ServiceRequestDao serviceRequestDao;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value("${LINKED_IN_CLIENT_ID}")
	private String LINKED_IN_CLIENT_ID;
	
	@Value("${LINKED_IN_SCOPE}")
	private String LINKED_IN_SCOPE;
	
	@Value("${LINKED_IN_CODE}")
	private String LINKED_IN_CODE;

	@Value("${LINKED_IN_RANK_REDIRECT_URI}")
	private String LINKED_IN_RANK_REDIRECT_URI; 

	@Value("${TIMEBOUND_PORTAL_LIST}")
	private List<String> TIMEBOUND_PORTAL_LIST; 
	
	private static final Logger rankLogger = LoggerFactory.getLogger("rankDenormalization");
	
	@RequestMapping(value = "/student/ranks", method = {RequestMethod.GET})
    public ModelAndView getRanksBySapId(HttpServletRequest request, HttpServletResponse response){
		
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}

	    ModelAndView modelAndView = new ModelAndView("jsp/studentHome/rank");
		List<StudentRankBean> cycleWiseRankConfigList = new ArrayList<>();
		List<StudentRankBean> subjectWiseRankConfigList = new ArrayList<>();

	    String userId = (String)request.getSession().getAttribute("userId");

	    try {
	    	cycleWiseRankConfigList = leaderBoardService.getCycleWiseRankConfigList(userId);
	    }catch ( Throwable throwable ) {
			// TODO: handle exception
        	rankLogger.info("in getRankConfiguration got exception: "+ExceptionUtils.getFullStackTrace(throwable) );     
	    	cycleWiseRankConfigList = new ArrayList<StudentRankBean>();
		}

	    try {
	    	subjectWiseRankConfigList = leaderBoardService.getSubjectWiseRankConfigList(userId);
	    }catch ( Throwable throwable ) {
			// TODO: handle exception
        	rankLogger.info("in getRankConfiguration got exception: "+ExceptionUtils.getFullStackTrace(throwable) );       
	    	subjectWiseRankConfigList = new ArrayList<StudentRankBean>();
		}
	    
        modelAndView.addObject("cycleWiseRankConfigList", cycleWiseRankConfigList);
        modelAndView.addObject("subjectWiseRankConfigList", subjectWiseRankConfigList);
    	modelAndView.addObject("server_path", SERVER_PATH);
    	
        return modelAndView;
    }
	
	@RequestMapping(value = "/rankDenormalization", method = {RequestMethod.POST}, consumes ="application/json", produces = "application/json")
    public ResponseEntity<StudentRankBean> rankDenormalization(){

		Date startedAt = new Date();
		
		System.out.println("in rankDenormalization starting migration at: "+startedAt);
		rankLogger.info("in rankDenormalization starting migration at: "+startedAt);

        HttpHeaders header = new HttpHeaders();
        List<StudentRankBean> cycleWiseRankConfig = new ArrayList<>();   
        List<StudentRankBean> subjectWiseRankConfig = new ArrayList<>();  
        StudentRankBean response = new StudentRankBean();
        int cycleWiseCount = 0, subjectWiseCount = 0;
        
		
		rankLogger.info("in rankDenormalization fetching cycle wise rank configuration");
		
        cycleWiseRankConfig = leaderBoardService.getCycleWiseConfigurationForRankDenormalization();
        
		
		rankLogger.info("in rankDenormalization got cycle wise rank configuration: "+cycleWiseRankConfig.size());

        for( StudentRankBean bean : cycleWiseRankConfig ) {
        	 
            try {
            	 
            	leaderBoardService.fetchAndInsertCycleWiseRankDetails(bean);
            	cycleWiseCount++;
            	
            }catch (Exception e) {
            	rankLogger.info("in rankDenormalization cycle wise rank got error: "+e.getMessage());
			}
            
            if( cycleWiseCount!= 0 && cycleWiseCount % 500 == 0 ) {
            	
            	rankLogger.info("in rankDenormalization cycle wise rank completed migration of : "+cycleWiseCount);
            }
        }
        
		
		rankLogger.info("in rankDenormalization fetching subject wise rank configuration");
		
		subjectWiseRankConfig = leaderBoardService.getSubjectWiseConfigurationForRankDenormalization();
		
		
		rankLogger.info("in rankDenormalization got subject wise rank configuration: "+subjectWiseRankConfig.size());
		
        for( StudentRankBean bean : subjectWiseRankConfig ) {
       	 
            try {

            	leaderBoardService.fetchAndInsertSubjectWiseRankDetails(bean);
	            subjectWiseCount++;
	            
            }catch (Exception e) {
				rankLogger.info("in rankDenormalization subject wise rank got error: "+e.getMessage());
			}
            if( subjectWiseCount != 0 && subjectWiseCount % 500 == 0 ) {
            	
				rankLogger.info("in rankDenormalization subject wise rank completed migration of : "+subjectWiseCount);
            }
        }


        Date completedAt = new Date();
        
        long totalTimeTaken = completedAt.getTime() - startedAt.getTime();
        
    	System.out.println("in rankDenormalization completed migration at: "+completedAt);
    	rankLogger.info("in rankDenormalization completed migration at: "+completedAt);

    	System.out.println("in rankDenormalization total time take from migration : "+TimeUnit.MILLISECONDS.toMinutes(totalTimeTaken));
    	rankLogger.info("in rankDenormalization total time take from migration : "+TimeUnit.MILLISECONDS.toMinutes(totalTimeTaken));
    	
        return new ResponseEntity<StudentRankBean>(response, header, HttpStatus.OK);
	}
	
	@PostMapping(value = "/m/migrateRank", consumes ="application/json", produces = "application/json")
    public ResponseEntity<String> migrateRank( @RequestBody StudentRankBean details ){

		Date startedAt = new Date();
		
		rankLogger.info("in migrateRank starting migration at: "+startedAt);
		
        HttpHeaders header = new HttpHeaders();
        List<StudentRankBean> cycleWiseRankConfig = new ArrayList<>();   
        List<StudentRankBean> subjectWiseRankConfig = new ArrayList<>();  
        int cycleWiseCount = 0, subjectWiseCount = 0;
        
		rankLogger.info("in migrateRank fetching cycle wise rank configuration");
		
        cycleWiseRankConfig = leaderBoardService.getCycleWiseConfigurationToMigrateRank( details.getMonth(), details.getYear() );
        
		rankLogger.info("in migrateRank got cycle wise rank configuration: "+cycleWiseRankConfig.size());

        for( StudentRankBean bean : cycleWiseRankConfig ) {
        	 
            try {
            	 
            	leaderBoardService.fetchAndInsertCycleWiseRankDetails(bean);
            	cycleWiseCount++;
            	
            }catch (Exception e) {
            	rankLogger.info("in migrateRank cycle wise rank got error: "+e.getMessage());
			}
            
            if( cycleWiseCount!= 0 && cycleWiseCount % 500 == 0 ) {
            	
            	rankLogger.info("in migrateRank cycle wise rank completed migration of : "+cycleWiseCount);
            }
        }
        
		rankLogger.info("in migrateRank fetching subject wise rank configuration");
		
		subjectWiseRankConfig = leaderBoardService.getSubjectWiseConfigurationToMigrateRank( details.getMonth(), details.getYear() );

		rankLogger.info("in migrateRank got subject wise rank configuration: "+subjectWiseRankConfig.size());
		
        for( StudentRankBean bean : subjectWiseRankConfig ) {
       	 
            try {

            	leaderBoardService.fetchAndInsertSubjectWiseRankDetails(bean);
	            subjectWiseCount++;
	            
            }catch (Exception e) {
				rankLogger.info("in migrateRank subject wise rank got error: "+e.getMessage());
			}
            if( subjectWiseCount != 0 && subjectWiseCount % 500 == 0 ) {
            	
				rankLogger.info("in migrateRank subject wise rank completed migration of : "+subjectWiseCount);
            }
        }

        Date completedAt = new Date();
        
        long totalTimeTaken = completedAt.getTime() - startedAt.getTime();
        
    	rankLogger.info("in migrateRank completed migration at: "+completedAt);
    	rankLogger.info("in migrateRank total time take from migration : "+TimeUnit.MILLISECONDS.toMinutes(totalTimeTaken));

    	return new ResponseEntity<String>("migrateRank for "+details.getMonth()+details.getYear()+
    			" cycle completed, total time taken : "+TimeUnit.MILLISECONDS.toMinutes(totalTimeTaken)+" min ", header, HttpStatus.OK);
    	
    }
	
	/**@implNote
	 * @author Gaurav Yadav
	 * @payLoad month,year
	 * @return map of string and object as a value
	 * @reason to migrate subject wise rank for timebound students
	 * 
	 * */
	@PostMapping(value = "/m/migrateSubjectWiseRankForTimeBound", consumes ="application/json", produces = "application/json")
    public ResponseEntity<Map<String,Object>> migrateRankSubjectWiseForTimeBound( @RequestBody StudentRankBean details ){
		Map<String,Object> response =new HashMap<>();
 		try {
 			TIMEBOUND_PORTAL_LIST.clear();
 			TIMEBOUND_PORTAL_LIST.add("111");
 			TIMEBOUND_PORTAL_LIST.add("151");
 			TIMEBOUND_PORTAL_LIST.add("160");
 			response = leaderBoardService.migrateSubjectWiseRank(details.getMonth(), details.getYear(),TIMEBOUND_PORTAL_LIST);
			return new ResponseEntity<>(response,HttpStatus.OK);
		}catch(Exception e) {
			if(e instanceof RuntimeException) {
				rankLogger.error(e.getMessage()+" : ",e);
				response.put("message", e.getMessage());
			}else {
				rankLogger.error(" Error While Migrating Subject Wise Rank : ",e);
				response.put("message","Error While Migrating Subject Wise Rank");
			}
		//e.printStackTrace();
			response.put("status", "error");
			response.put("cause",e);
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/**@implNote
	 * @author Gaurav Yadav
	 * @payLoad sapid,masterkey
	 * @return map of string and object as a value
	 * @reason to fetch subject wise rank for timebound students
	 * 
	 * */
    @PostMapping(value = "/m/getSubjectWiseRankForTimebound", consumes ="application/json", produces = "application/json")
    public ResponseEntity<Map<String,Object>> getSubjectWiseRank(@RequestBody StudentStudentPortalBean bean){
		Map<String,Object> response =new HashMap<>();
 		try {
 			response = leaderBoardService.getSubjectWiseRankForStudent(bean.getConsumerProgramStructureId(), bean.getSapid());
			return new ResponseEntity<>(response,HttpStatus.OK);
		}catch(Exception e) {
			rankLogger.error("Got Error While Fetching Subject Wise Rank : ",e);
			response.put("status", "error");
			response.put("message","Error While Fetching Subject Wise Rank");
			response.put("cause",e);
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
        
    }
    
    /**@apiNote
     * The Below API will be used to migrate cycle wise rank for timebound[MBA-WX] students
     * @RequestBody sapid,masterkey
      */
    @PostMapping(value = "/m/migrateCycleWiseRankForTimeBound", consumes ="application/json", produces = "application/json")
    public ResponseEntity<Map<String,Object>> migrateCyclewWiseRankForTimeBound( @RequestBody StudentRankBean details ){
		Map<String,Object> response =new HashMap<>();
 		try {
 			TIMEBOUND_PORTAL_LIST.clear();
 			TIMEBOUND_PORTAL_LIST.add("111");
 			TIMEBOUND_PORTAL_LIST.add("151");
 			TIMEBOUND_PORTAL_LIST.add("160");
			response = leaderBoardService.migrateCycleWiseRank(details.getMonth(), details.getYear(),TIMEBOUND_PORTAL_LIST);
			return new ResponseEntity<>(response,HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			if(e instanceof RuntimeException) {
				rankLogger.error(e.getMessage()+" : ",e);
				response.put("message", e.getMessage());
			}else {
				rankLogger.error(" Error While Migrating Cycle Wise Rank : ",e);
				response.put("message","Error While Migrating Cycle Wise Rank");
			}
			e.printStackTrace();
			response.put("status", "error");
			response.put("cause",e);
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		
    	
    }
    
    /**@apiNote
     * The Below API will be used to fetch cycle wise rank for timebound[MBA-WX] students
     * @RequestBody month,year
      */
    @PostMapping(value = "/m/getCycleWiseRankForTimebound", consumes ="application/json", produces = "application/json")
    public ResponseEntity<Map<String,Object>> getCycleWiseRank(@RequestBody StudentStudentPortalBean bean){
		Map<String,Object> response =new HashMap<>();
 		try {
			response = leaderBoardService.getCycleWiseRankForStudent(bean.getConsumerProgramStructureId(), bean.getSapid());
			return new ResponseEntity<>(response,HttpStatus.OK);
		}catch(Exception e) {
			rankLogger.error("Got Error While Fetching Cycle Wise Rank : ",e);
			response.put("status", "error");
			response.put("message","Error While Fetching Cycle Wise Rank");
			response.put("cause",e);
			e.printStackTrace();
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
        
    }
	
	public String encryptWithOutSpecialCharacters(String stringToBeEncrypted) throws Exception{

		return AESencrp.encrypt(stringToBeEncrypted).replaceAll("\\+", "_plus_");
	}
	
	public String decryptWithOutSpecialCharacters(String stringToBeDecrypted) throws Exception{
		
		return AESencrp.decrypt(stringToBeDecrypted.replaceAll("_plus_", "\\+"));
	}
	
	/*
	@RequestMapping(value = "/getRankConfig", method = {RequestMethod.POST}, consumes ="application/json", produces = "application/json")
    public ResponseEntity<List<StudentRankBean>> getRankConfig(@RequestBody StudentBean bean){
        HttpHeaders header = new HttpHeaders();
        List<StudentRankBean> rankConfig = new ArrayList<>();               
        PortalDao portalDao = (PortalDao)act.getBean("portalDAO");		      
        try {

        	if(!homeController.checkIfMovingResultsToCache()) {
            	
	        	rankConfig = portalDao.getRankConfigList(bean.getSapid());
	        	Collections.reverse(rankConfig);
	        
        	} 
            
        	return new ResponseEntity<List<StudentRankBean>>(rankConfig, header, HttpStatus.OK);            
            
        } catch(Exception ex){
            ex.printStackTrace();
            return new ResponseEntity<List<StudentRankBean>>(rankConfig, header, HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/getRankSubjectConfig", method = {RequestMethod.POST}, consumes ="application/json", produces = "application/json")
    public ResponseEntity<List<StudentRankBean>> getRankSubjectConfig(@RequestBody StudentBean bean){
        HttpHeaders header = new HttpHeaders();
        List<StudentRankBean> rankConfig = new ArrayList<>();               
        PortalDao portalDao = (PortalDao)act.getBean("portalDAO");		    
        
        try {
        	if(!homeController.checkIfMovingResultsToCache()) {
                
	        	rankConfig = portalDao.getRankSubjectConfigList(bean.getSapid());
	        	Collections.reverse(rankConfig);
	            
        	}
        	
	return new ResponseEntity<List<StudentRankBean>>(rankConfig, header, HttpStatus.OK);            
        } catch(Exception ex){
            ex.printStackTrace();
            return new ResponseEntity<List<StudentRankBean>>(rankConfig, header, HttpStatus.BAD_REQUEST);
        }
    }
    
	@RequestMapping(value = "m/getRankConfiguration", method = {RequestMethod.POST}, consumes ="application/json", produces = "application/json")
    public ResponseEntity<List<List<StudentRankBean>>> getRankConfiguration(@RequestBody StudentBean bean){
		
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "application/json");
		
		List<List<StudentRankBean>> details = new ArrayList<>();
		List<StudentRankBean> rankConfigList = new ArrayList<>();
		List<StudentRankBean> rankSubjectConfigList = new ArrayList<>();
		PortalDao portalDao = (PortalDao)act.getBean("portalDAO");
	  
        try {
        	if(!homeController.checkIfMovingResultsToCache()) {
                
        	rankConfigList = portalDao.getRankConfigList(bean.getUserId());
        	Collections.reverse(rankConfigList);
        	rankSubjectConfigList = portalDao.getRankSubjectConfigList(bean.getUserId());
        	Collections.reverse(rankSubjectConfigList);
        	}
        	
        } catch(Exception ex){
            ex.printStackTrace();            
        }       
        
        details.add(rankConfigList);
        details.add(rankSubjectConfigList);
        
        return new ResponseEntity<List<List<StudentRankBean>>>(details, header, HttpStatus.OK);
        
    }
	 
	@RequestMapping(value = "m/getCycleWiseRankConfiguration", method = {RequestMethod.POST}, consumes ="application/json", produces = "application/json")
    public ResponseEntity< List<StudentRankBean> > getCycleWiseRankConfiguration(@RequestBody StudentBean bean){
		
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "application/json");
		
		List<StudentRankBean> rankConfigList = new ArrayList<>();
		PortalDao portalDao = (PortalDao)act.getBean("portalDAO");
	   
        try {
        	if(!homeController.checkIfMovingResultsToCache()) {
        
        		rankConfigList = portalDao.getRankConfigList(bean.getUserId());
        		Collections.reverse(rankConfigList);
        	
        	}
        } catch(Exception ex){
            ex.printStackTrace();            
        }       
        
        
        return new ResponseEntity< List<StudentRankBean> >(rankConfigList, header, HttpStatus.OK);
        
    }
	
	@RequestMapping(value = "m/getSubejctWiseRankConfiguration", method = {RequestMethod.POST}, consumes ="application/json", produces = "application/json")
	public ResponseEntity< List<StudentRankBean> > getSubejctWiseRankConfiguration(@RequestBody StudentBean bean){
		
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "application/json");
		
		List<StudentRankBean> rankSubjectConfigList = new ArrayList<>();
		PortalDao portalDao = (PortalDao)act.getBean("portalDAO");
	 
        try {
        	
        	if(!homeController.checkIfMovingResultsToCache()) {
                
        	rankSubjectConfigList = portalDao.getRankSubjectConfigList(bean.getUserId());
        	Collections.reverse(rankSubjectConfigList);
        	}
        } catch(Exception ex){
            ex.printStackTrace();            
        }       
        
        return new ResponseEntity< List<StudentRankBean> >(rankSubjectConfigList, header, HttpStatus.OK);
        
    }

	@RequestMapping(value = "/shareRank", method = {RequestMethod.GET})
    public ModelAndView shareRank( @RequestParam String rankType, @RequestParam String sapid, @RequestParam String sem, @RequestParam String subjectId) throws Exception{
      
    	ModelAndView modelAndView = new ModelAndView("jsp/studentHome/shareRank");
    	PortalDao portalDao = (PortalDao)act.getBean("portalDAO");
    	List<StudentRankBean> cycleWiseRank = new ArrayList<StudentRankBean>();
		List<StudentRankBean> semesterWiseRank = new ArrayList<>();
    	List<StudentRankBean> rank = new ArrayList<StudentRankBean>();
    	StudentRankBean studentRankDetails = new StudentRankBean();
        StudentBean bean = new StudentBean();
        
        String dencryptedUserId = decryptWithOutSpecialCharacters( sapid );
		String dencryptedSem = decryptWithOutSpecialCharacters( sem );
		String dencryptedSubjectId = decryptWithOutSpecialCharacters( subjectId );
		
    	bean.setSapid(dencryptedUserId); 
    	bean.setSem(dencryptedSem);
    	
    	int count = 0;
    	
    	try {
			bean = leaderBoardService.getStudentDetailsForRank(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	if("cycleWise".equals(rankType)) {
    		
    		cycleWiseRank = portalDao.getRankBySapId(bean.getSapid(), bean.getSem(), bean.getConsumerProgramStructureId(),
					bean.getProgram(), bean.getSubjectsCount(), bean.getMonth(), bean.getYear());
    		
    		if( cycleWiseRank.size() < 6 ) {
    			
    			rank.addAll(cycleWiseRank);
    			studentRankDetails.setName( portalDao.getStudentsNameForSapid( dencryptedUserId ) );
    			
    		}else {
	        	for(StudentRankBean detail : cycleWiseRank) {

	        		if(count < cycleWiseRank.size()-1)
	        			rank.add(detail);
	        		else
	        			studentRankDetails = detail;
	        		count++;
	        		
	        	}
    		}
    		
    	}else {

    		bean.setSubject( portalDao.getSubjectnameForId( dencryptedSubjectId ) );
    		semesterWiseRank = portalDao.getRankSubjectWiseBySapId(bean.getSapid(), bean.getSem(), bean.getMonth(), bean.getYear(),
					bean.getProgram(), bean.getSubject());	

    		studentRankDetails = (StudentRankBean) semesterWiseRank.get(0);
			studentRankDetails.setName( portalDao.getStudentsNameForSapid( dencryptedUserId ) );
    		rank = (List<StudentRankBean>) semesterWiseRank.get(1);
    		
    	}

    	modelAndView.addObject("rankType", rankType);
    	modelAndView.addObject("studentRankDetails", studentRankDetails);
    	modelAndView.addObject("rank", rank);
    	
        return modelAndView;

    }
	
	
	*/
	
}