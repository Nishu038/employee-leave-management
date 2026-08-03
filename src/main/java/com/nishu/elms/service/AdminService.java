package com.nishu.elms.service;

import com.nishu.elms.dto.request.CreateUserRequest;
import com.nishu.elms.dto.response.RegisterResponse;
import com.nishu.elms.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    RegisterResponse createUser(CreateUserRequest request);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id,CreateUserRequest request);
    void deleteUser(Long id);
}
