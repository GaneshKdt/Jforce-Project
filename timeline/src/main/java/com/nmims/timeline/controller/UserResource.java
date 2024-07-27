package com.nmims.timeline.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.timeline.model.User;
import com.nmims.timeline.repository.UserRepository;
import com.nmims.timeline.service.UserService;

@RestController
@RequestMapping("/rest/user")
public class UserResource {

	/*
	 * private UserRepository userService;
	 * 
	 * public UserResource(UserRepository userService) { this.userService =
	 * userService; }
	 */
    private UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/add/{id}/{name}")
    public User add(@PathVariable("id") final String id,
                    @PathVariable("name") final String name) {
        userService.save(new User(id, name, 20000L));
        return userService.findById(id);
    }

    @GetMapping("/update/{id}/{name}")
    public User update(@PathVariable("id") final String id,
                       @PathVariable("name") final String name) {
        userService.update(new User(id, name, 1000L));
        return userService.findById(id);
    }

    @GetMapping("/delete/{id}")
    public Map<String, User> delete(@PathVariable("id") final String id) {
        userService.delete(id);
        return all();
    }

    @GetMapping("/all")
    public Map<String, User> all() {
        return userService.findAll();
    }
}
