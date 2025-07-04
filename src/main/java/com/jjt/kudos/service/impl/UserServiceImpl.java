package com.jjt.kudos.service.impl;

import com.jjt.kudos.entity.User;
import com.jjt.kudos.repository.UserRepository;
import com.jjt.kudos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
} 