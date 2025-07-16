package com.friedo.authentication.service.serviceImpl;

import com.friedo.authentication.model.User;
import com.friedo.authentication.repository.UserRepository;
import com.friedo.authentication.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository, EmailServiceImpl emailService){
        this.userRepository = userRepository;
    }

    @Override
    public List<User> allUsers() {
        return List.of();
    }
}
