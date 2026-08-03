package com.nishu.elms.service.impl;

import com.nishu.elms.dto.request.CreateUserRequest;
import com.nishu.elms.dto.response.RegisterResponse;
import com.nishu.elms.dto.response.UserResponse;
import com.nishu.elms.entity.Department;
import com.nishu.elms.entity.Role;
import com.nishu.elms.entity.User;
import com.nishu.elms.exception.EmailAlreadyExistsException;
import com.nishu.elms.exception.ResourceNotFoundException;
import com.nishu.elms.repository.DepartmentRepository;
import com.nishu.elms.repository.RoleRepository;
import com.nishu.elms.repository.UserRepository;
import com.nishu.elms.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public RegisterResponse createUser(CreateUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceNotFoundException("Email already exists");
        }
        Role role = roleRepository.findByName(request.getRole()).orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(()-> new ResourceNotFoundException("Department not found"));
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .enabled(true)
                .department(department)
                .build();
        user.setRoles(Set.of(role));
        User savedUser = userRepository.save(user);
        return RegisterResponse.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .message("User created successfully")
                .build();

    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToResponse);
    }

    private UserResponse mapToResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.getEnabled())
                .department(user.getDepartment() != null
                ? user.getDepartment().getName()
                        :null)
                .roles(user.getRoles()
                        .stream().map(role -> role.getName().name())
                        .collect(java.util.stream.Collectors.toSet())).build();
    }
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        //check email conflict
        if(!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists");
        }
        Role  role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("role not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(()-> new ResourceNotFoundException("department not found"));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDepartment(department);
        user.setRoles(Set.of(role));
        //update password if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()){
            user.setPassword(request.getPassword());

        }
        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }
}
