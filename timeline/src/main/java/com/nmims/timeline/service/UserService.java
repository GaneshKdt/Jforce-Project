package com.nmims.timeline.service;

import java.util.Map;

import com.nmims.timeline.model.User;

public interface UserService {

	User save(User user);
    Map<String, User> findAll();
    User findById(String id);
    User update(User user);
    Map<String, User> delete(String id);
}
