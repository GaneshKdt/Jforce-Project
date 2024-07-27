package com.nmims.timeline.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nmims.timeline.model.Comments;
import com.nmims.timeline.model.Faculty;
import com.nmims.timeline.model.GetPostsByTimeboundIdResponseBean;
import com.nmims.timeline.model.Post;
import com.nmims.timeline.model.PostReactions;
import com.nmims.timeline.model.Registration;
import com.nmims.timeline.model.RequestBean;
import com.nmims.timeline.model.Student;
import com.nmims.timeline.repository.FacultyRepository;
import com.nmims.timeline.repository.PostRepository;
import com.nmims.timeline.repository.PostRepositoryForRedis;
import com.nmims.timeline.repository.RegistrationRepository;
import com.nmims.timeline.repository.StudentRepository;



@Service
public class PostServiceImpl implements PostService {
	
    private PostRepository postRepository;
    private PostRepositoryForRedis postRepositoryForRedis;
    private StudentRepository studentRepository;
    private RegistrationRepository registrationRepository;
    private FacultyRepository facultyRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private TestService testService;

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value("${SERVER_PATH_FEED_FILES}")
	private String SERVER_PATH_FEED_FILES;
	

	@Value("${SERVER_PORT}")
	private int[] port;
	
	
	static final Map<String, String> acadsMonthMap;
	private static final Logger logger = LogManager.getLogger(PostServiceImpl.class);

    static {
    	acadsMonthMap = new LinkedHashMap<>(); 
    	acadsMonthMap.put("Jan", "01");
    	acadsMonthMap.put("Apr", "04");
    	acadsMonthMap.put("Jul", "07");
    	acadsMonthMap.put("Oct", "10");
    }
    
    public PostServiceImpl(PostRepository postRepository,
    					   PostRepositoryForRedis postRepositoryForRedis,
    					   StudentRepository studentRepository,
    					   RegistrationRepository registrationRepository,
    					   FacultyRepository facultyRepository) {
        this.postRepository = postRepository;
        this.postRepositoryForRedis = postRepositoryForRedis;
        this.studentRepository = studentRepository;
        this.registrationRepository = registrationRepository;

        this.facultyRepository = facultyRepository;
    }
	
	@Override
	public List<Post> findAll() {
		
		return (List<Post>)postRepository.findAll();
	}

	@Override
	public String setAllPostsInRedisCache() {
		
		Map<String,Faculty> facultyMap = new HashMap<>();
		Map<String,Student> studentMap = new HashMap<>();
		
		 // 0. get all timeboundIds
		List<Post> timeboundIds = postRepository.getAllTimeboundIds();
		  //System.out.println("IN setAllPostsInRedisCache() timeboundIds : ");
		  //System.out.println(timeboundIds != null ? timeboundIds.size() : 0);

		  //1. delete all old data
		  for(Post p : timeboundIds) {
			  Integer timeboundId = p.getSubjectConfigId();
			  //System.out.println("IN setAllPostsInRedisCache() timeboundId : "+timeboundId);
			  String deletePostByTimeboundIdError = postRepositoryForRedis.deleteAllByTimeboundId(timeboundId);
			  if(!StringUtils.isBlank(deletePostByTimeboundIdError)) {
				  //System.out.println("IN setAllPostsInRedisCache() deletePostByTimeboundIdError : "+deletePostByTimeboundIdError);
				  return deletePostByTimeboundIdError;
			  }
		  }
		 
		  
		  //2. Add get all post 
		  int addMinuteTime = 2;
		  Date targetTime = new Date(); //now
		  targetTime = DateUtils.addMinutes(targetTime, addMinuteTime); 
		  String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(targetTime);
		  List<Post> allPosts = (List<Post>) postRepository.findByScheduleFlagAndScheduledDateBeforeOrderByScheduledDateDescPostIdDesc("N",currentDate);

		  List<String> postTypes = Arrays.asList("MCQ","Session");
		  List<Post> allPostsForResit = (List<Post>) postRepository.findByScheduleFlagAndScheduledDateBeforeAndTypeNotInOrderByScheduledDateDescPostIdDesc("N",currentDate,postTypes);
		  
		/*
		 * List allPostsFaculties = postRepository.
		 * getPostsFacultysByScheduleFlagAndScheduledDateBeforeOrderByScheduledDateDesc(
		 * "N",currentDate);
		 * 
		 * 
		 * //System.out.println("IN setAllPostsInRedisCache() allPostsFaculties : ");
		 * //System.out.println(allPostsFaculties != null ? allPostsFaculties.size() : 0);
		 * 
		 * for(Object pf : allPostsFaculties) {
		 * 
		 * try { //
		 * //System.out.println("IN saveAllPosts PostFacultyDTO: "+pf.getClass().getField(
		 * "userId")+"==="+pf.getClass().getField("firstName")+" "+pf);
		 * //System.out.println("IN saveAllPosts PostFacultyDTO: "+pf.getClass().getFields
		 * ()+"==="+pf); } catch (Exception e) { e.printStackTrace(); }
		 * 
		 * }
		 */
		  
		  //System.out.println("IN setAllPostsInRedisCache() allPosts : ");
		  //System.out.println(allPosts != null ? allPosts.size() : 0);
		  //System.out.println(allPostsForResit != null ? allPostsForResit.size() : 0);

//		  if(allPosts == null) { return "No new post found"; }
//		  
//		  if(allPosts.size() == 0) { return "No new post found"; }
		  
		  //3. save all new posts in redis
		  String saveAllPostError = ""; 
		  
		  saveAllPostError = savePostInRedisCache(allPosts,facultyMap,studentMap,"regular");
		  
		  saveAllPostError = savePostInRedisCache(allPostsForResit,facultyMap,studentMap,"resit");

		  
		  if(!StringUtils.isBlank(saveAllPostError)) {
					
			  return "Error in saving posts, Error : "+ saveAllPostError; 
		  }else { 
			  
			  //PostService postService = new PostServiceImpl(postRepository,postRepositoryForRedis);
			  //PostController postController = new PostController(postService);

			  //delete cache

			  for(Post p : timeboundIds) {
				  Integer timeboundId = p.getSubjectConfigId();
				  //System.out.println("IN setAllPostsInRedisCache() timeboundId : "+timeboundId);
				  //postController.deletePostsByTimeboundIdFromCache(p);
				  String callDeletePostsByTimeboundIdFromCache = callDeletePostsByTimeboundIdFromCache(p);
				  //System.out.println("callDeletePostsByTimeboundIdFromCache : \n"+callDeletePostsByTimeboundIdFromCache);
			  }
			  

			  //update cache commmented below but method is working kept for future use.

			/*
			 * List<Post> newTimeboundIds = postRepository.getAllTimeboundIds();
			 * 
			 * for(Post p : newTimeboundIds) { Integer newTimeboundId =
			 * p.getSubjectConfigId();
			 * //System.out.println("IN setAllPostsInRedisCache() newTimeboundId : "
			 * +newTimeboundId); String callGetAllPostsFromRedisByTimeboundId =
			 * callGetAllPostsFromRedisByTimeboundId(p);
			 * //System.out.println("callGetAllPostsFromRedisByTimeboundId : \n"
			 * +callGetAllPostsFromRedisByTimeboundId); }
			 */
			  
			  
			  return "Success, moved all data to redis cache"; 
		  }
		 

		
		
	}

	private List<Comments> sortCommentsByIdAsc(List<Comments> postComments) {
		LinkedList<Comments> tempList = new LinkedList<>();
		try {
			for(Comments c : postComments) {
				tempList.add(c);
			}
			
			Comparator<Comments> compareById = new Comparator<Comments>() {
				@Override
				public int compare(Comments o1, Comments o2) {
					return o1.getCommentId().compareTo(o2.getCommentId());
				}
			};

			// Sort all subjects in desc order of score
			Collections.sort(tempList, compareById);
			/*
			for(Comments c : tempList) {
				//System.out.println("IN sortCommentsByIdAsc() after sort "+c.getCommentId()+"="+c.getComment());
			}
			//System.out.println();
			*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tempList;
	}

	private String callGetAllPostsFromRedisByTimeboundId(Post post) {
		

	      try {
	  	    String url = SERVER_PATH+"timeline/api/post/getAllPostsFromRedisByTimeboundId";
	    	  //System.out.println("IN callGetAllPostsFromRedisByTimeboundId() got url : \n"+url);
			HttpHeaders headers = new HttpHeaders();
			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			  HttpEntity<Post> entity = new HttpEntity<Post>(post,headers);
			  
			  return restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, String.class).getBody();
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error IN rest call got "+e.getMessage();
		}
	   
	}
	

	private String callSetAllPostsInRedisCache(int port) {
		

	      try {
	  	    String url = SERVER_PATH+"/timeline/api/post/setAllPostsInRedisCache";
	    	  //System.out.println("IN callGetAllPostsFromRedisByTimeboundId() got url : \n"+url);
	    	  HttpHeaders headers = new HttpHeaders();
			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			  HttpEntity<Post> entity = new HttpEntity<>(null,headers);
			  
			  return restTemplate.exchange(
				 url,
			     HttpMethod.GET, entity, String.class).getBody();
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error IN rest call to "+port+" got "+e.getMessage();
		}
	   
	}

	private String callDeletePostsByTimeboundIdFromCache(Post post) {
		logger.info("PostServiceImpl.callDeletePostsByTimeboundIdFromCache() - START");

	      try {
	  	    String url = SERVER_PATH+"timeline/api/post/deletePostsByTimeboundIdFromCache";
	  	    logger.info("IN callDeletePostsByTimeboundIdFromCache() got url : \n"+url);
			HttpHeaders headers = new HttpHeaders();
			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			  HttpEntity<Post> entity = new HttpEntity<Post>(post,headers);
			  
			  logger.info("PostServiceImpl.callDeletePostsByTimeboundIdFromCache() - END");
			  return restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, String.class).getBody();
		} catch (RestClientException e) {
			logger.error("callDeletePostsByTimeboundIdFromCache Error Message :"+e.getMessage());
			e.printStackTrace();
			return "Error IN rest call got "+e.getMessage();
		}
	   
	}

	@Override
	public Map<String, Post> allRedisPosts() {
		return postRepositoryForRedis.findAll();
	}
	

//	@Cacheable(value = "postsByTimeboundIdAndUserId",key = "#userIdAndTimeboundId")
	@Override
	public GetPostsByTimeboundIdResponseBean findByTimeboundIdForResponseBean(RequestBean request,String userIdAndTimeboundId) {
		//System.out.println("IN PostServiceImpl findByTimeboundIdForResponseBean() called --->");
		//System.out.println("IN PostServiceImpl findByTimeboundIdForResponseBean() userIdAndTimeboundId :"+userIdAndTimeboundId);
		

		int timeBoundId = request.getTimeBoundId();
		String userId = request.getUserId();
		GetPostsByTimeboundIdResponseBean response  =  new GetPostsByTimeboundIdResponseBean();
		List<Post> listOfPosts = new ArrayList<>();
		String genericTimeBoundId = "-1";
		if (userId.startsWith("77")) {
			
			Student student = getSingleStudentsData(userId);
			Registration studentRegDetails = getSingleStudentsRegistrationData(userId);
			String studentType = getStudentType(userId, ""+timeBoundId);
			//System.out.println("studentType-------"+studentType);
			student.setYear(studentRegDetails.getYear());
			student.setMonth(studentRegDetails.getMonth());
			student.setSem(studentRegDetails.getSem());
			
			String month = "0";
			if (acadsMonthMap.containsKey(student.getMonth())) {
				month = acadsMonthMap.get(student.getMonth());
			}
			genericTimeBoundId = student.getConsumerProgramStructureId()+month+student.getYear()+student.getSem();
			//System.out.println("genericTimeBoundId :- "+genericTimeBoundId);
			listOfPosts = findByTimeboundIdIn(genericTimeBoundId+","+timeBoundId, studentType);
			

			response.setServerPath(SERVER_PATH_FEED_FILES);
			response.setListOfPosts(listOfPosts);
			//System.out.println("response:::::" + response);
			return response;
		}
		   
		return new GetPostsByTimeboundIdResponseBean();
	}

	private Registration getSingleStudentsRegistrationData(String userId) {
		return registrationRepository.findFirstBySapidOrderBySemDesc(userId);
	}

	private Student getSingleStudentsData(String userId) {
		return studentRepository.findFirstBySapidOrderBySemDesc(userId);
	}
	
	private String getStudentType(String userId, String timeboundId) {
		String studentType = "Student";
		if (!StringUtils.isBlank(studentRepository.getStudentType(userId, timeboundId))) {
			studentType = studentRepository.getStudentType(userId, timeboundId);
		}
		return studentType;
	}

	@Override
	public List<Post> findByTimeboundId(Integer subject_config_id, String studentType, String timeBoundIdAndStudentType) {
		//System.out.println("IN PostServiceImpl findByTimeboundId() called --->");
		return postRepositoryForRedis.findByTimeboundId(subject_config_id,studentType,timeBoundIdAndStudentType);
	}
	

	@Override
	public List<Post> findByTimeboundIdIn( String timeboundIdsCommaSeparated, String studentType) {
		//System.out.println("IN PostServiceImpl findByTimeboundId() called --->");
		//System.out.println("IN PostServiceImpl findByTimeboundId() got timeboundIdsCommaSeparated : "+timeboundIdsCommaSeparated);
		
		List<Post> listToReturn = new LinkedList<>();
		String[] timeboundIds = timeboundIdsCommaSeparated.split(",",-1); 
		for(int i = timeboundIds.length-1; i>=0 ; i--) {
			//System.out.println("IN PostServiceImpl findByTimeboundId() got timeboundId : "+timeboundIds[i]);
			String timeBoundIdAndStudentType = timeboundIds[i]+studentType;
			listToReturn.addAll(findByTimeboundId((Integer.parseInt(timeboundIds[i])), studentType, timeBoundIdAndStudentType));
			//System.out.println("IN PostServiceImpl findByTimeboundId() got listToReturn size : "+listToReturn.size());
			
		}
		
		
		return listToReturn;
	}

	@Override
	public String save(Post post) {
		
		//1. save post in redis key value table by timeboundId
		String saveInRedisKeyError = "";
		saveInRedisKeyError = postRepositoryForRedis.save(post);
		//System.out.println("IN PostServiceImpl save() got saveInRedisKeyError : "+saveInRedisKeyError);
		
		if(StringUtils.isBlank(saveInRedisKeyError)) {
				
			try {
				 
				  //delete cache
				  String callDeletePostsByTimeboundIdFromCache = callDeletePostsByTimeboundIdFromCache(post);
				  //System.out.println("callDeletePostsByTimeboundIdFromCache : \n"+callDeletePostsByTimeboundIdFromCache);

				  //update cache commmented below but method is working kept for future use. 	  
				  //String callGetAllPostsFromRedisByTimeboundId = callGetAllPostsFromRedisByTimeboundId(post); 
			 
				  
				  return "";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				postRepositoryForRedis.delete(post.getSubjectConfigId(),post.getPostId());
				return "Errror in saving new post in cache try again, "+e.getMessage();
			}
			
		}else {
			return "Errror in saving new post, "+saveInRedisKeyError;
		}
		
		
		
	}
	
	
	
	
	@Override
	public String deletePostsByTimeboundIdFromCache(String timeBoundIdAndStudentType) {
		logger.info("IN PostServiceImpl deletePostsByTimeboundIdFromCache() got timeBoundIdAndStudentType : "+timeBoundIdAndStudentType);
		return postRepositoryForRedis.deletePostsByTimeboundIdFromCache(timeBoundIdAndStudentType);
		
	}

	@Override
	public String deletePostByTimeboundIdAndPostId(Post post) {
		
		String deleteError = postRepositoryForRedis.delete(post.getSubjectConfigId(),post.getPostId());
		
		if(!StringUtils.isBlank(deleteError)) {
			return "Error in deleting the post : "+deleteError;
		}else {
			
			//delete cache
			  String callDeletePostsByTimeboundIdFromCache = callDeletePostsByTimeboundIdFromCache(post);
			  //System.out.println("callDeletePostsByTimeboundIdFromCache : \n"+callDeletePostsByTimeboundIdFromCache);
			  return "Post deleted successfully!";	
		}
		
	}
	
	

	@Override
	public String refreshRedisDataByTimeboundId(Integer timeboundId) {
		logger.info("PostServiceImpl.refreshRedisDataByTimeboundId() - START");
		
		String deletePostByTimeboundIdError = postRepositoryForRedis.deleteAllByTimeboundId(timeboundId);
		  if(!StringUtils.isBlank(deletePostByTimeboundIdError)) {
			  logger.info("IN refreshRedisDataByTimeboundId() deletePostByTimeboundIdError : "+deletePostByTimeboundIdError);
			  return deletePostByTimeboundIdError;
		  }
		  
		  
		  //2. Add get all post 
		  int addMinuteTime = 2;
		  Date targetTime = new Date(); //now
		  targetTime = DateUtils.addMinutes(targetTime, addMinuteTime); 
		  String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(targetTime);
		  List<Post> allPosts = (List<Post>) postRepository.findBySubjectConfigIdAndScheduleFlagAndScheduledDateBeforeOrderByScheduledDateDescPostIdDesc(timeboundId,"N",currentDate);
		  
		  //Added to get all post for Resit Students
		  List<String> postTypes = Arrays.asList("MCQ","Session");
		  List<Post> allPostsForResit = (List<Post>) postRepository.findBySubjectConfigIdAndScheduleFlagAndScheduledDateBeforeAndTypeNotInOrderByScheduledDateDescPostIdDesc(timeboundId, "N", currentDate, postTypes);
		  
		  logger.info("IN setAllPostsInRedisCache() allPosts : ");
		  logger.info(allPosts != null ? allPosts.size() : 0);
		  logger.info(allPostsForResit != null ? allPostsForResit.size() : 0);
		  
//		  if(allPosts == null) { return "No new post found"; }
//		  
//		  if(allPosts.size() == 0) { return "No new post found"; }
		  
		  
		  //3. save all new posts in redis
		  String saveAllPostError = "";
		  String saveAllResitPostError = "";
		  
		  Map<String,Faculty> facultyMap = new HashMap<>();
		  Map<String,Student> studentMap = new HashMap<>();
		  try { 
			  
			  saveAllPostError = savePostInRedisCacheForRefreshRedisData(allPosts,facultyMap,studentMap,"regular");
			  
			  saveAllResitPostError = savePostInRedisCacheForRefreshRedisData(allPostsForResit,facultyMap,studentMap,"resit");
			  
		  } catch (Exception e) { 
			  e.printStackTrace(); saveAllPostError =
			  saveAllPostError + e.getMessage(); 
		  }
		  
		  String returnMessage = "";
		  if(!StringUtils.isBlank(saveAllPostError)) {
			  returnMessage = returnMessage + "Error in saving posts, Error : "+ saveAllPostError; 
		  }else { 
			  Post p =new Post();
			  p.setSubjectConfigId(timeboundId);
			  String callDeletePostsByTimeboundIdFromCache = callDeletePostsByTimeboundIdFromCache(p);
			  logger.info("callDeletePostsByTimeboundIdFromCache : \n"+callDeletePostsByTimeboundIdFromCache);
			  returnMessage = returnMessage + "Success, in refreshing all for Regular Post"; 
		  }
		  
		  if(!StringUtils.isBlank(saveAllResitPostError)) {
			  returnMessage = returnMessage + "Error in saving posts, Error : "+ saveAllPostError; 
		  }else { 
			  Post p =new Post();
			  p.setSubjectConfigId(timeboundId);
			  String callDeletePostsByTimeboundIdFromCache = callDeletePostsByTimeboundIdFromCache(p);
			  logger.info("callDeletePostsByTimeboundIdFromCache : \n"+callDeletePostsByTimeboundIdFromCache);
			  returnMessage = returnMessage + "Success, in refreshing all for Resit Post"; 
		  }
		  logger.info("Result Message after refreshing redis by timeboundId :"+returnMessage);
		  logger.info("PostServiceImpl.refreshRedisDataByTimeboundId() - END");
		  return returnMessage;
	}
	

	@Override
	public String setAllPostsInRedisCacheForAllInstances() {
		String responseString ="";
		for(int i = 0; i<port.length; i++) {
			responseString += callSetAllPostsInRedisCache(port[i]);
		}
		
		return responseString;
	}

	@Override
	public String refreshRedisDataByTimeboundIdForAllIntances(Integer subject_config_id) {
		logger.info("PostServiceImpl.refreshRedisDataByTimeboundIdForAllIntances() - START");
		String responseString ="";
		Post p = new Post();
		p.setSubjectConfigId(subject_config_id);
		logger.info("subject_config_id :"+subject_config_id);
		
		responseString += callrefreshRedisDataByTimeboundIdForAllIntances(0,p);
		
//		for(int i = 0; i<port.length; i++) {
//			responseString += callrefreshRedisDataByTimeboundIdForAllIntances(port[i],p);
//		}
		  try {
				String setUpcomingTestsPerStudentInRedisByTimeboundIdMessage = testService.setUpcomingTestsPerStudentInRedisByTimeboundId(subject_config_id);
				logger.info("setUpcomingTestsPerStudentInRedisByTimeboundIdMessage : \n"+setUpcomingTestsPerStudentInRedisByTimeboundIdMessage);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("PostServiceImpl.refreshRedisDataByTimeboundIdForAllIntances() - Error Message :"+e.getMessage());
			}
		  logger.info("PostServiceImpl.refreshRedisDataByTimeboundIdForAllIntances() - END");
		return responseString;
	}

	private String callrefreshRedisDataByTimeboundIdForAllIntances(int port, Post post) {
		logger.info("PostServiceImpl.callrefreshRedisDataByTimeboundIdForAllIntances() - START");
		logger.info("PORT :"+port);
		 String returnMessage = "";
	      try {
		  	    String url = SERVER_PATH+"/timeline/api/post/refreshRedisDataByTimeboundId";
		  	  logger.info("IN callGetAllPostsFromRedisByTimeboundId() got url : \n"+url);
			HttpHeaders headers = new HttpHeaders();
			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			  HttpEntity<Post> entity = new HttpEntity<Post>(post,headers);
			  
			  returnMessage += restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, String.class).getBody();
		} catch (RestClientException e) {
			logger.error("Error while doing '/refreshRedisDataByTimeboundId' - Error Message :"+e.getMessage());
			e.printStackTrace();
			returnMessage += "Error IN rest call got "+e.getMessage();
		}
	   
	      

	      try {
		  	    String url = SERVER_PATH+"/timeline/api/post/cacheEvictPostsByTimeboundIdAndSapid";
		  	  logger.info("IN callGetAllPostsFromRedisByTimeboundId() got url : \n"+url);
			HttpHeaders headers = new HttpHeaders();
			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			  HttpEntity<Post> entity = new HttpEntity<Post>(post,headers);
			  
			  returnMessage += restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, String.class).getBody();
		} catch (RestClientException e) {
			logger.error("Error while doing '/cacheEvictPostsByTimeboundIdAndSapid' - Error Message :"+e.getMessage());
			e.printStackTrace();
			returnMessage += "Error IN rest call got "+e.getMessage();
		}
	      logger.info("PostServiceImpl.callrefreshRedisDataByTimeboundIdForAllIntances() - END");
	      
	      return returnMessage;
	}

//	@CacheEvict(value = "postsByTimeboundIdAndUserId", allEntries=true)
	@Override
	public String cacheEvictPostsByTimeboundIdAndSapid() {
		//System.out.println("IN PostServiceImpl cacheEvictPostsByTimeboundIdAndSapid() ");
		return " Success in cacheEvictPostsByTimeboundIdAndSapid() ";
	}
	
	public String savePostInRedisCache(List<Post> allPosts, Map<String, Faculty> facultyMap, Map<String, Student> studentMap, String studentType) {
		String saveAllPostError = ""; 
		  
		  if(allPosts == null) { return "No new post found for "+studentType+ "Students"; }
		  
		  if(allPosts.size() == 0) { return "No new post found for "+studentType+ "Students"; }
		  
		  for(Post post : allPosts) {
		  post.setStudentType(studentType);
		  try { 
			  if(!StringUtils.isBlank(post.getUserId())) {

				  if(post.getUserId().startsWith("N")) {
					  //System.out.println("IN setAllPostsInRedisCache() Post : facultyId : "+post.getUserId());
					  Faculty faculty = null;
					  if(facultyMap.containsKey(post.getUserId())) {
						  faculty = facultyMap.get(post.getUserId());
						  //System.out.println("IN setAllPostsInRedisCache() faculty from Map : "+faculty.toString());
					  }else {
						  faculty = facultyRepository.findByFacultyId(post.getUserId());

						  if(faculty != null) {
							  facultyMap.put(post.getUserId(), faculty);
							  //System.out.println("IN setAllPostsInRedisCache() faculty from DB : "+faculty.toString());
						  }
					  }
					  if(faculty != null) {
						  post.setFirstName(faculty.getFirstName());
						  post.setLastName(faculty.getLastName());  
						  post.setProfilePicFilePath(faculty.getProfilePicFilePath());  
					  }else {
						  post.setFirstName("");
						  post.setLastName("");  
						  post.setProfilePicFilePath("");  
					  
					  }
				  }else {
					  post.setFirstName("");
					  post.setLastName("");  
					  post.setProfilePicFilePath("");  
				  
				  }
				  

				  
			  }else {
				  post.setFirstName("");
				  post.setLastName("");  
				  post.setProfilePicFilePath("");  
			  
			  }
			  
			  //For reactionWithUserData start
			  Set<PostReactions> reactions = post.getPostReactions();
			  for (PostReactions r : reactions) { 
				  
//					Student s=null;
//					Faculty f=null;
//					try {
//						s = postDao.getSingleStudentsData( r.getUserId()); 
//						f = (Faculty)postDao.getFacultyDetailsByUserId(r.getUserId());
//					} catch (Exception e) {
//					}
//					if(s != null) {  
//						r.setFullName(s.getFirstName()+" "+s.getLastName());
//					}else if (f != null){
//						r.setFullName(f.getFirstName()+" "+f.getLastName());
//					}else {
//						r.setFullName("Admin");
//					}
//					reactionWithUserData.add(r);
				  
			  if(r.getUserId().startsWith("7")) {
				  Student student = null;
				  if(studentMap.containsKey(r.getUserId())) {
					  student = studentMap.get(r.getUserId());
					  //System.out.println("IN setAllPostsInRedisCache() reactionWithUserData student from Map : "+student.toString());
				  }else {

						student = getSingleStudentsData(r.getUserId());
					
					  if(student != null) {
						  studentMap.put(r.getUserId(), student);
						  //System.out.println("IN setAllPostsInRedisCache() reactionWithUserData student from DB : "+student.toString());
					  }
				  }
				  if(student != null) {
						r.setFullName(student.getFirstName()+" "+student.getLastName());
				  }else { 
					  r.setFullName("Admin");
				  }
			  
				  
			  }else if(r.getUserId().startsWith("N")) {
				  Faculty faculty = null;
				  if(facultyMap.containsKey(r.getUserId())) {
					  faculty = facultyMap.get(r.getUserId());
					  //System.out.println("IN setAllPostsInRedisCache() reactionWithUserData faculty from Map : "+faculty.toString());
				  }else {
					  faculty = facultyRepository.findByFacultyId(r.getUserId());

					  if(faculty != null) {
						  facultyMap.put(r.getUserId(), faculty);
						  //System.out.println("IN setAllPostsInRedisCache() reactionWithUserData faculty from DB : "+faculty.toString());
					  }
				  }
				  if(faculty != null) {
						r.setFullName(faculty.getFirstName()+" "+faculty.getLastName());
				  }else { 
					  r.setFullName("Admin");
				  }
			  }else {
				  r.setFullName("Admin");
			  }
			  
			  //System.out.println("IN For reactionWithUserData FullName : "+r.getFullName());
			  
			  }
			  //For reactionWithUserData end
			  post.setPostReactions(reactions);
			  
			  //For SettingSubCommentsToReplies[] start
			  List<Comments> postComments = post.getPostComments();
			  Map<Integer,Comments> postCommentsMap = new HashMap<>();
			  
			  postComments = sortCommentsByIdAsc(postComments);
			  
			  for(Comments c : postComments ) {
				  
				  //Set user details to comment start

				  if(c.getSapid().startsWith("7")) {
					  Student student = null;
					  if(studentMap.containsKey(c.getSapid())) {
						  student = studentMap.get(c.getSapid());
						  //System.out.println("IN setAllPostsInRedisCache() commentsWithUserData student from Map : "+student.toString());
					  }else {

							student = getSingleStudentsData(c.getSapid());
						
						  if(student != null) {
							  studentMap.put(c.getSapid(), student);
							  //System.out.println("IN setAllPostsInRedisCache() commentsWithUserData student from DB : "+student.toString());
						  }
					  }
					  if(student != null) {
							c.setFirstName(student.getFirstName());
							c.setLastName(student.getLastName());
							c.setImageUrl(student.getImageUrl());
					  }else { 
							c.setFirstName("Admin");
							c.setLastName("");
							c.setImageUrl("");
					  }
				  
					  
				  }else if(c.getSapid().startsWith("N")) {
					  Faculty faculty = null;
					  if(facultyMap.containsKey(c.getSapid())) {
						  faculty = facultyMap.get(c.getSapid());
						  //System.out.println("IN setAllPostsInRedisCache() commentsWithUserData faculty from Map : "+faculty.toString());
					  }else {
						  faculty = facultyRepository.findByFacultyId(c.getSapid());

						  if(faculty != null) {
							  facultyMap.put(c.getSapid(), faculty);
							  //System.out.println("IN setAllPostsInRedisCache() commentsWithUserData faculty from DB : "+faculty.toString());
						  }
					  }
					  if(faculty != null){
							c.setFirstName(faculty.getFirstName());
							c.setLastName(faculty.getLastName());
							c.setImageUrl(faculty.getImgUrl());
					  }else { 
							c.setFirstName("Admin");
							c.setLastName("");
							c.setImageUrl("");
					  } 
						  
						
				  }else {
						c.setFirstName("Admin");
						c.setLastName("");
						c.setImageUrl("");
				  }
				  
				  //System.out.println("IN For commentsWithUserData firstName : "+c.getFirstName()+" LastName : "+c.getLastName());


				  //Set user details to comment end
				  
				  
				  if(c.getMaster_comment_id() != 0) {
					  if(postCommentsMap.containsKey(c.getMaster_comment_id())) {
						  Comments commentForReplieToBeAdded = postCommentsMap.get(c.getMaster_comment_id()) ;
						  LinkedList<Comments> replies = commentForReplieToBeAdded.getReplies();
						  replies.add(c);
						  commentForReplieToBeAdded.setReplies(replies);

						  postCommentsMap.put(commentForReplieToBeAdded.getCommentId(), commentForReplieToBeAdded);
					  }
				  }else {
					  postCommentsMap.put(c.getCommentId(), c);
				  }
			  }

			  LinkedList<Comments> postCommentsAfterSettingReplies = new LinkedList<>();
			  	
			  	Collection<Comments> c =  postCommentsMap.values();
				

				  for(Comments cmnt : postComments ) {

						  for(Comments cmntWithReplies : c ) {
							if(cmnt.getCommentId().equals(cmntWithReplies.getCommentId()) && cmnt.getMaster_comment_id() == 0) {
								postCommentsAfterSettingReplies.add(cmntWithReplies);
							}
						}
				  }
			  	
			  post.setPostComments(postCommentsAfterSettingReplies);
			  //For SettingSubCommentsToReplies[] end
			  
			  //System.out.println("IN saveAllPosts post: "+post.toString());
			  //System.out.println("IN saveAllPosts reactions : "+post.getPostReactions());
			  //System.out.println("IN saveAllPosts comments : "+post.getPostComments());
			  postRepositoryForRedis.save(post); 
		   
		  } catch (Exception e) { 
			  e.printStackTrace(); saveAllPostError =
			  saveAllPostError + e.getMessage(); 
		  }
		  
		  }
		  return saveAllPostError;
	}
	
	public String savePostInRedisCacheForRefreshRedisData(List<Post> allPosts, Map<String, Faculty> facultyMap, Map<String, Student> studentMap, String studentType) {
		String saveAllPostError = "";
		logger.info("PostServiceImpl.savePostInRedisCacheForRefreshRedisData() - START");
		
		if(allPosts == null) { return "No new post found for "+studentType+ " Students"; }
		  
		if(allPosts.size() == 0) { return "No new post found for "+studentType+ " Students"; }
		  
		for(Post post : allPosts) {
			post.setStudentType(studentType);
			  try { 
				  if(!StringUtils.isBlank(post.getUserId())) {

					  if(post.getUserId().startsWith("N")) {
						  logger.info("IN setAllPostsInRedisCache() Post : facultyId : "+post.getUserId());
						  Faculty faculty = null;
						  if(facultyMap.containsKey(post.getUserId())) {
							  faculty = facultyMap.get(post.getUserId());
							  logger.info("IN setAllPostsInRedisCache() faculty from Map : "+faculty.toString());
						  }else {
							  faculty = facultyRepository.findByFacultyId(post.getUserId());

							  if(faculty != null) {
								  facultyMap.put(post.getUserId(), faculty);
								  logger.info("IN setAllPostsInRedisCache() faculty from DB : "+faculty.toString());
							  }
						  }
						  if(faculty != null) {
							  post.setFirstName(faculty.getFirstName());
							  post.setLastName(faculty.getLastName());  
							  post.setProfilePicFilePath(faculty.getImgUrl());  
						  }else {
							  post.setFirstName("");
							  post.setLastName("");  
							  post.setProfilePicFilePath("");  
						  
						  }
					  }else {
						  post.setFirstName("");
						  post.setLastName("");  
						  post.setProfilePicFilePath("");  
					  
					  }  
				  }else {
					  post.setFirstName("");
					  post.setLastName("");  
					  post.setProfilePicFilePath("");  
				  
				  }
				  
				  //For reactionWithUserData start
				  Set<PostReactions> reactions = post.getPostReactions();
				  for (PostReactions r : reactions) { 
					  
//						Student s=null;
//						Faculty f=null;
//						try {
//							s = postDao.getSingleStudentsData( r.getUserId()); 
//							f = (Faculty)postDao.getFacultyDetailsByUserId(r.getUserId());
//						} catch (Exception e) {
//						}
//						if(s != null) {  
//							r.setFullName(s.getFirstName()+" "+s.getLastName());
//						}else if (f != null){
//							r.setFullName(f.getFirstName()+" "+f.getLastName());
//						}else {
//							r.setFullName("Admin");
//						}
//						reactionWithUserData.add(r);
					  
				  if(r.getUserId().startsWith("7")) {
					  Student student = null;
					  if(studentMap.containsKey(r.getUserId())) {
						  student = studentMap.get(r.getUserId());
						  logger.info("IN setAllPostsInRedisCache() reactionWithUserData student from Map : "+student.toString());
					  }else {

							student = getSingleStudentsData(r.getUserId());
						
						  if(student != null) {
							  studentMap.put(r.getUserId(), student);
							  logger.info("IN setAllPostsInRedisCache() reactionWithUserData student from DB : "+student.toString());
						  }
					  }
					  if(student != null) {
							r.setFullName(student.getFirstName()+" "+student.getLastName());
					  }else { 
						  r.setFullName("Admin");
					  }
				  
					  
				  }else if(r.getUserId().startsWith("N")) {
					  Faculty faculty = null;
					  if(facultyMap.containsKey(r.getUserId())) {
						  faculty = facultyMap.get(r.getUserId());
						  logger.info("IN setAllPostsInRedisCache() reactionWithUserData faculty from Map : "+faculty.toString());
					  }else {
						  faculty = facultyRepository.findByFacultyId(r.getUserId());

						  if(faculty != null) {
							  facultyMap.put(r.getUserId(), faculty);
							  logger.info("IN setAllPostsInRedisCache() reactionWithUserData faculty from DB : "+faculty.toString());
						  }
					  }
					  if(faculty != null) {
							r.setFullName(faculty.getFirstName()+" "+faculty.getLastName());
					  }else { 
						  r.setFullName("Admin");
					  }
				  }else {
					  r.setFullName("Admin");
				  }
				  
				  logger.info("IN For reactionWithUserData FullName : "+r.getFullName());
				  
				  }
				  //For reactionWithUserData end
				  post.setPostReactions(reactions);
				  
				  //For SettingSubCommentsToReplies[] and user details start
				  List<Comments> postComments = post.getPostComments();
				  Map<Integer,Comments> postCommentsMap = new HashMap<>();

				  postComments = sortCommentsByIdAsc(postComments);
				  
				  for(Comments c : postComments ) {
					  
					  //Set user details to comment start

					  if(c.getSapid().startsWith("7")) {
						  Student student = null;
						  if(studentMap.containsKey(c.getSapid())) {
							  student = studentMap.get(c.getSapid());
							  logger.info("IN setAllPostsInRedisCache() commentsWithUserData student from Map : "+student.toString());
						  }else {

								student = getSingleStudentsData(c.getSapid());
							
							  if(student != null) {
								  studentMap.put(c.getSapid(), student);
								  logger.info("IN setAllPostsInRedisCache() commentsWithUserData student from DB : "+student.toString());
							  }
						  }
						  if(student != null) {
								c.setFirstName(student.getFirstName());
								c.setLastName(student.getLastName());
								c.setImageUrl(student.getImageUrl());
						  }else { 
								c.setFirstName("Admin");
								c.setLastName("");
								c.setImageUrl("");
						  }
					  
						  
					  }else if(c.getSapid().startsWith("N")) {
						  Faculty faculty = null;
						  if(facultyMap.containsKey(c.getSapid())) {
							  faculty = facultyMap.get(c.getSapid());
							  logger.info("IN setAllPostsInRedisCache() commentsWithUserData faculty from Map : "+faculty.toString());
						  }else {
							  faculty = facultyRepository.findByFacultyId(c.getSapid());

							  if(faculty != null) {
								  facultyMap.put(c.getSapid(), faculty);
								  logger.info("IN setAllPostsInRedisCache() commentsWithUserData faculty from DB : "+faculty.toString());
							  }
						  }
						  if(faculty != null){
								c.setFirstName(faculty.getFirstName());
								c.setLastName(faculty.getLastName());
								c.setImageUrl(SERVER_PATH+faculty.getImgUrl());
						  }else { 
								c.setFirstName("Admin");
								c.setLastName("");
								c.setImageUrl("");
						  } 
							  
							
					  }else {
							c.setFirstName("Admin");
							c.setLastName("");
					  }
					  
					  logger.info("IN For commentsWithUserData firstName : "+c.getFirstName()+" LastName : "+c.getLastName());


					  //Set user details to comment end
					  
					  if(c.getMaster_comment_id() != 0) {
						  if(postCommentsMap.containsKey(c.getMaster_comment_id())) {
							  Comments commentForReplieToBeAdded = postCommentsMap.get(c.getMaster_comment_id()) ;
							  LinkedList<Comments> replies = commentForReplieToBeAdded.getReplies();
							  replies.add(c);
							  commentForReplieToBeAdded.setReplies(replies);

							  postCommentsMap.put(commentForReplieToBeAdded.getCommentId(), commentForReplieToBeAdded);
						  }
					  }else {
						  postCommentsMap.put(c.getCommentId(), c);
					  }
				  }

				  LinkedList<Comments> postCommentsAfterSettingReplies = new LinkedList<>();
				  	
				  	Collection<Comments> c =  postCommentsMap.values();
					

					  for(Comments cmnt : postComments ) {

							  for(Comments cmntWithReplies : c ) {
								if(cmnt.getCommentId().equals(cmntWithReplies.getCommentId()) && cmnt.getMaster_comment_id() == 0) {
									postCommentsAfterSettingReplies.add(cmntWithReplies);
								}
							}
					  }
				  	
				  
				  post.setPostComments(postCommentsAfterSettingReplies);
				  //For SettingSubCommentsToReplies[] end
				  
				  //System.out.println("IN saveAllPosts post: "+post.toString());
				  //System.out.println("IN saveAllPosts reactions : "+post.getPostReactions());
				  //System.out.println("IN saveAllPosts comments : "+post.getPostComments());
				  postRepositoryForRedis.save(post); 
			  } catch (Exception e) {
				  logger.error("savePostInRedisCacheForRefreshRedisData - Error Message :"+e.getMessage());
				  e.printStackTrace(); saveAllPostError =
				  saveAllPostError + e.getMessage(); 
			  }
			  
			  }
		logger.info("saveAllPostError Message :"+saveAllPostError);
		logger.info("PostServiceImpl.savePostInRedisCacheForRefreshRedisData() - END");
		return saveAllPostError;
		
	}
	
    
	/*
	 *
	 
//@CacheEvict(value = "users", allEntries=true)
@DeleteMapping("/{id}")
public void deleteUserByID(@PathVariable Long id) {
  LOG.info("deleting person with id {}", id);
  userRepository.delete(id);
}

//@CachePut(value = "users", key = "#user.id")
@PutMapping("/update")
public User updatePersonByID(@RequestBody User user) {
  userRepository.save(user);
  return user;
}

//@CacheEvict(value = "first", key = "#cacheKey")
public void evictSingleCacheValue(String cacheKey) {}

	 * */
	
}
