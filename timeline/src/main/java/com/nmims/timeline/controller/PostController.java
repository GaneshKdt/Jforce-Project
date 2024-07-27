package com.nmims.timeline.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.timeline.model.GetPostsByTimeboundIdResponseBean;
import com.nmims.timeline.model.Post;
import com.nmims.timeline.model.RequestBean;
import com.nmims.timeline.service.PostService;

@RestController
@RequestMapping("/api/post")
public class PostController {

    private PostService postService;
    
    private static final Logger logger = LogManager.getLogger(PostController.class);

    public PostController(PostService postService) {
        this.postService = postService;
    }
    



	@GetMapping("/all")
    public List<Post> all() {
    	//System.out.println("IN PostController all() called --->");
        return postService.findAll();
    }
    

    @GetMapping("/allRedisPosts")
    public Map<String, Post> allRedisPosts() {
    	//System.out.println("IN PostController allRedisPosts() called --->");
        return postService.allRedisPosts();
    }
    
    @PostMapping(path = "/getAllPostsFromRedisByTimeboundId", consumes = "application/json", produces = "application/json")
    public GetPostsByTimeboundIdResponseBean getAllPostsFromRedisByTimeboundId(@RequestBody RequestBean requestBean) {
        
    	//System.out.println("IN PostController getAllPostsFromRedisByTimeboundId() called --->");
    	//System.out.println("IN PostController getAllPostsFromRedisByTimeboundId() got requestBean : "+requestBean.toString());
        return postService.findByTimeboundIdForResponseBean(requestBean,requestBean.getUserId()+"~"+requestBean.getTimeBoundId());
    }
    

    @PostMapping(path = "/refreshRedisDataByTimeboundId", consumes = "application/json", produces = "application/json")
    public String refreshRedisDataByTimeboundId(@RequestBody Post post) {
        
    	logger.info("IN PostController refreshRedisDataByTimeboundId() called --->");
    	logger.info("IN PostController refreshRedisDataByTimeboundId() got id : "+post.getSubjectConfigId());
        return postService.refreshRedisDataByTimeboundId(post.getSubjectConfigId());
    }
    
    @PostMapping(path = "/cacheEvictPostsByTimeboundIdAndSapid", consumes = "application/json", produces = "application/json")
    public String cacheEvictPostsByTimeboundIdAndSapid(@RequestBody Post post) {
        
    	//System.out.println("IN PostController cacheEvictPostsByTimeboundIdAndSapid() called --->");
    	//System.out.println("IN PostController cacheEvictPostsByTimeboundIdAndSapid() got id : "+post.getSubjectConfigId());
        return postService.cacheEvictPostsByTimeboundIdAndSapid();
    }

    @PostMapping(path = "/refreshRedisDataByTimeboundIdForAllIntances", consumes = "application/json", produces = "application/json")
    public HashMap<String,String> refreshRedisDataByTimeboundIdForAllIntances(@RequestBody Post post) {
        
    	logger.info("IN PostController refreshRedisDataByTimeboundIdForAllIntances() called --->");
    	logger.info("IN PostController refreshRedisDataByTimeboundIdForAllIntances() got id : "+post.getTimeboundId());
        
    	String returnMessage =  postService.refreshRedisDataByTimeboundIdForAllIntances(post.getTimeboundId());
    	HashMap<String,String> returnMap = new HashMap<String,String>();
    	returnMap.put("message", returnMessage);
    	
    	logger.info("Result Message after refresh all instances :"+returnMessage);
    	logger.info("PostController.refreshRedisDataByTimeboundIdForAllIntances() - END");
        return returnMap;
    }
    
    @PostMapping(path = "/savePostInRedis", consumes = "application/json", produces = "application/json")
    public String savePostInRedis(@RequestBody Post post) {
        
    	//System.out.println("IN PostController savePostInRedis() called --->");
    	//System.out.println("IN PostController savePostInRedis() got : "+post.toString());
        return postService.save(post);
    }
    
    @GetMapping("/setAllPostsInRedisCache")
    public String setAllPostsInRedisCache() {
    	//System.out.println("IN PostController setAllPostsInRedisCache() called --->");
        return postService.setAllPostsInRedisCache();
    }
    
    @GetMapping("/setAllPostsInRedisCacheForAllInstances")
    public String setAllPostsInRedisCacheForAllInstances() {
    	//System.out.println("IN PostController setAllPostsInRedisCacheForAllInstances() called --->");
        return postService.setAllPostsInRedisCacheForAllInstances();
    }

    @PostMapping("/deletePostsByTimeboundIdFromCache")
    public HashMap<String,String> deletePostsByTimeboundIdFromCache(@RequestBody Post post) {
    	logger.info("IN PostController deletePostsByTimeboundIdFromCache() - START");
    	String returnMessage = "";
    	List<String> studentTypes = Arrays.asList("Student","Resit");
    	for (String studentType : studentTypes) {
    		String timeBoundIdAndStudentType = post.getSubjectConfigId()+studentType;
    		returnMessage =  postService.deletePostsByTimeboundIdFromCache(timeBoundIdAndStudentType);
		}
    	
    	HashMap<String,String> returnMap = new HashMap<String,String>();
    	returnMap.put("message", returnMessage);
    	logger.info("After deleting the post from cache by timebound Id :"+returnMessage);
    	logger.info("IN PostController deletePostsByTimeboundIdFromCache() - END");
        return returnMap;
    
    }
    

    @PostMapping("/deletePostByTimeboundIdAndPostId")
    public String deletePostByTimeboundIdAndPostId(@RequestBody Post post) {
    	//System.out.println("IN PostController deletePostByTimeboundIdAndPostId() called --->");
        return postService.deletePostByTimeboundIdAndPostId(post);
    }
}
