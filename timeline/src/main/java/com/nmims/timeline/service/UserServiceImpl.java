package com.nmims.timeline.service;

import org.springframework.stereotype.Service;
import java.util.Map;

import com.nmims.timeline.model.User;
import com.nmims.timeline.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

	@Override
	public User save(User user) {
        userRepository.save(user);
        return userRepository.findById(user.getId());
    }

	@Override
	public Map<String, User> findAll() {
        return userRepository.findAll();
    }

	@Override
	public User findById(String id) {
        return userRepository.findById(id);
    }

	@Override
	public User update(User user) {
        userRepository.update(user);
        return userRepository.findById(user.getId());
    }

	@Override
	public Map<String, User> delete(String id) {
        userRepository.delete(id);
        return findAll();
    }

}
