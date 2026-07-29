package com.nishu.elms.service.impl;

import com.nishu.elms.entity.User;
import com.nishu.elms.exception.ResourceNotFoundException;
import com.nishu.elms.repository.UserRepository;
import com.nishu.elms.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailWithRoles(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
