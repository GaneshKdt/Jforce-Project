package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.Comments;


@Repository
public interface CommentsRepository extends CrudRepository<Comments, Integer>   {

	List<Comments> findByPostPostId(String postId);
	
}
