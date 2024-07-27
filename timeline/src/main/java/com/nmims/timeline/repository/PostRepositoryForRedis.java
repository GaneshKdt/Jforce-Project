
package com.nmims.timeline.repository;

import java.util.List;
import java.util.Map;

import com.nmims.timeline.model.Post;

public interface PostRepositoryForRedis {

    String save(Post post);
    Map<String, Post> findAll();
    Post findById(String id);
    void update(Post post);
    String delete(Integer timeboundId,String postId);
    boolean deleteAll();
	List<Post> findByTimeboundId(Integer subject_config_id, String studentType, String timeBoundIdAndStudentType);
	String deleteAllByTimeboundId(Integer subject_config_id);
	
	String saveAll(Integer timeboundId, List<Post> posts);
	String deletePostsByTimeboundIdFromCache(String timeBoundIdAndStudentType);
}

