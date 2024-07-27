package com.nmims.timeline.repository;

import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.User;

@Repository
public class UserRepositoryImpl implements UserRepository {
	
	private static final String USER_TABLE_NAME = "USER";
	
    private RedisTemplate<Object, Object> redisTemplate;

    private HashOperations hashOperations;


    public UserRepositoryImpl(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(User user) {
        hashOperations.put(USER_TABLE_NAME, user.getId(), user);
    }

    @Override
    public Map<String, User> findAll() {
        return hashOperations.entries(USER_TABLE_NAME);
    }

    @Override
    public User findById(String id) {
        return (User)hashOperations.get(USER_TABLE_NAME, id);
    }

    @Override
    public void update(User user) {
        save(user);
    }

    @Override
    public void delete(String id) {

        hashOperations.delete(USER_TABLE_NAME, id);
    }
}
