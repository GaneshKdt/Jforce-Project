package com.nmims.timeline.service;

import java.util.List;
import java.util.Map;

import com.nmims.timeline.model.GetPostsByTimeboundIdResponseBean;
import com.nmims.timeline.model.Post;
import com.nmims.timeline.model.RequestBean;

public interface PostService {

	List<Post> findAll();

	String setAllPostsInRedisCache();

	Map<String, Post> allRedisPosts();

	List<Post> findByTimeboundId(Integer subject_config_id, String studentType,String timeBoundIdAndStudentType);


	String save(Post post);

	String deletePostsByTimeboundIdFromCache(String timeBoundIdAndStudentType);

	String deletePostByTimeboundIdAndPostId(Post post);

	String refreshRedisDataByTimeboundId(Integer timeboundId);

	String setAllPostsInRedisCacheForAllInstances();

	String refreshRedisDataByTimeboundIdForAllIntances(Integer subject_config_id);

	List<Post> findByTimeboundIdIn(String timeboundIdsCommaSeparated, String studentType);


	GetPostsByTimeboundIdResponseBean findByTimeboundIdForResponseBean(RequestBean bean,String userIdAndTimeboundId);

	String cacheEvictPostsByTimeboundIdAndSapid();

}
