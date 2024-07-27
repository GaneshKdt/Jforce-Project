package com.nmims.timeline.repository;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.Post;

@Repository
public class PostRepositoryForRedisImpl implements PostRepositoryForRedis {
	
	private static final String POST_TABLE_NAME = "POST-";
	
	private static final String RESIT_POST_TABLE_NAME = "RESIT_POST-";
	
    private RedisTemplate<Object, Object> redisTemplate;

    private HashOperations<Object, String, Post> hashOperations;

    private ListOperations listOperations;
    private SetOperations setOperations;

    private static final Logger logger = LogManager.getLogger(PostRepositoryForRedisImpl.class);

    public PostRepositoryForRedisImpl(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
        listOperations = redisTemplate.opsForList();
        setOperations = redisTemplate.opsForSet();
    }

    @Override
    public String save(Post post) {
    	
    	try {
			//hashOperations.put(post.getSubjectConfigId(), post.getPost_id(), post);
			
    		if (post.getStudentType().equalsIgnoreCase("regular")) {
    			listOperations.rightPush(POST_TABLE_NAME+post.getSubjectConfigId(), post);
			}else if (post.getStudentType().equalsIgnoreCase("resit")) {
    			listOperations.rightPush(RESIT_POST_TABLE_NAME+post.getSubjectConfigId(), post);
			}
    		
    		Long sizeOfList1 = listOperations.size(POST_TABLE_NAME+post.getSubjectConfigId());
    		Long sizeOfList2 = listOperations.size(RESIT_POST_TABLE_NAME+post.getSubjectConfigId());
    		//System.out.println("For TimeBound id "+post.getSubjectConfigId()+"sizeOfList1---"+sizeOfList1+"& sizeOfList2 ---"+sizeOfList2);
    		
    		//setOperations.add(post.getSubjectConfigId(), post.getSubjectConfigId());
    		
    		return "";
    	} catch (Exception e) {
    		//System.out.println("IN PostRepositoryForRedisImpl save() catch got error : ");
    		e.printStackTrace();
			return e.getMessage();
		}
    
    }
    @Override
    public String saveAll(Integer timeboundId,List<Post> posts) {
    	
    	try {
    		listOperations.rightPushAll(POST_TABLE_NAME+timeboundId, posts);
    		
    		//setOperations.add(timeboundId, posts);
    		
    		return "";
    	} catch (Exception e) {
    		//System.out.println("IN PostRepositoryForRedisImpl saveAll() catch got error : ");
    		e.printStackTrace();
			return e.getMessage();
		}
    
    }

    @Override
    public Map<String, Post> findAll() {
        return hashOperations.entries(POST_TABLE_NAME);
    }
    

    public Set<String> findAllKeys() {
        try {
			return (Set<String>)hashOperations.keys(POST_TABLE_NAME);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }

    public Set<String> findAllKeysByTimeboundId(Integer id) {
        try {
			return (Set<String>)hashOperations.keys(id+"");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }

    @Override
    public Post findById(String id) {
        return (Post)hashOperations.get(POST_TABLE_NAME, id);
    }

    @Override
    public void update(Post post) {
        save(post);
    }

	@Override
	public String delete(Integer timeboundId,String postId) {

        try {
			hashOperations.delete(""+timeboundId, postId);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error in deleting posst, "+e.getMessage();
		}
    }

	@Override
	public boolean deleteAll() {
		
		Set<String> allKeys = findAllKeys();
		
		//System.out.println("IN deleteAll() got allKeys : \n "+allKeys);
		
		String deleteError = "";
		
		Iterator<String> itr = allKeys.iterator();

		while(itr.hasNext()){
			
			try {
				//delete(itr.next());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				deleteError = deleteError + e.getMessage();
			}
		}
		//System.out.println("IN deleteAll() got deleteError : "+deleteError);
		
		if(!StringUtils.isBlank(deleteError)) {
				
			return false;
		}else {
			return true;
		}
		
	}

//	@Cacheable(value = "postsByTimeboundIdAndStudentType",key = "#timeBoundIdAndStudentType")
	@Override
	public List<Post> findByTimeboundId(Integer subject_config_id, String studentType, String timeBoundIdAndStudentType) {

		/*
		 * Set<String> keysByTimeboundId = findAllKeysByTimeboundId(subject_config_id);
		 * System.out.
		 * println("IN PostRepositoryForRedisImpl findByTimeboundId() keysByTimeboundId size : "
		 * ); System.out.println(keysByTimeboundId != null ? keysByTimeboundId.size() :
		 * 0); System.out.println(keysByTimeboundId);
		 * 
		 * return hashOperations.multiGet(subject_config_id, keysByTimeboundId);
		 */
		//return hashOperations.values(subject_config_id);
		//System.out.println("IN PostRepositoryForRedisImpl findByTimeboundId() called ===> "+studentType);
			
			if (studentType.equalsIgnoreCase("Resit")) {
				return (List<Post>)listOperations.range(RESIT_POST_TABLE_NAME+subject_config_id, 0, -1);
			}else {
				return (List<Post>)listOperations.range(POST_TABLE_NAME+subject_config_id, 0, -1);
			}
			
		
		/*
		 * Set<Post> postByTimeboundId =
		 * (Set<Post>)setOperations.members(subject_config_id);
		 * LinkedList<Post>(postByTimeboundId);
		 */
	}

	@Override
	public String deleteAllByTimeboundId(Integer subject_config_id) {
		String deletePostError ="";
		logger.info("PostRepositoryForRedisImpl.deleteAllByTimeboundId() - START");
		/*
		 * Set<String> keysByTimeboundId = findAllKeysByTimeboundId(subject_config_id);
		 * 
		 * for(String postId : keysByTimeboundId) { deletePostError +=
		 * delete(subject_config_id, postId); }
		 
		 */
		try {
			  Long sizeOfList = listOperations.size(POST_TABLE_NAME+subject_config_id);
			  logger.info("IN deleteAllByTimeboundId subject_config_id : "+subject_config_id+", before size : "+listOperations.size(POST_TABLE_NAME+subject_config_id));
			  
			  for(int i= 0;i<sizeOfList.intValue(); i++) {
				  logger.info("IN deleteAllByTimeboundId post : "+i+", ");
				  listOperations.rightPop(POST_TABLE_NAME+subject_config_id);
				  
			  }
			  
			  //For Resit students
			  Long sizeOfListForResit = listOperations.size(RESIT_POST_TABLE_NAME+subject_config_id);
			  logger.info("IN deleteAllByTimeboundId subject_config_id for Resit: "+subject_config_id+", before size : "+listOperations.size(RESIT_POST_TABLE_NAME+subject_config_id));
			  
			  for(int i= 0;i<sizeOfListForResit.intValue(); i++) {
				  logger.info("IN deleteAllByTimeboundId post : "+i+", ");
				  listOperations.rightPop(RESIT_POST_TABLE_NAME+subject_config_id);
			  }
			  
			  
			  //listOperations.remove(subject_config_id, 0, p);
			  
			  logger.info("IN deleteAllByTimeboundId subject_config_id : "+subject_config_id+", after size : "+listOperations.size(POST_TABLE_NAME+subject_config_id));
			  logger.info("IN deleteAllByTimeboundId subject_config_id For Resit: "+subject_config_id+", after size : "+listOperations.size(RESIT_POST_TABLE_NAME+subject_config_id));

			/*
			 * setOperations.pop(subject_config_id);
			 * System.out.println("IN deleteAllByTimeboundId subject_config_id : "
			 * +subject_config_id+", after trim left key : ");
			 * System.out.println(setOperations.size(subject_config_id));
			 */
		} catch (Exception e) {
			logger.error("Error in deleting post :"+e.getMessage());
			e.printStackTrace();
			deletePostError = "Error in deleting posst, "+e.getMessage();
		}
		
		logger.info("IN deleteAllByTimeboundId deletePostError : "+deletePostError);
		logger.info("PostRepositoryForRedisImpl.deleteAllByTimeboundId() - END");
		return deletePostError;
	}
	
//	@CacheEvict(value = "postsByTimeboundIdAndStudentType",key = "#timeBoundIdAndStudentType")
	@Override
	public String deletePostsByTimeboundIdFromCache(String timeBoundIdAndStudentType) {
		logger.info("IN deletePostsByTimeboundIdFromCache timeBoundIdAndStudentType : "+timeBoundIdAndStudentType);
		return "Deleted cache!";	
	}
}

