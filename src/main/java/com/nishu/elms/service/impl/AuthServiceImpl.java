package com.nishu.elms.service.impl;

import com.nishu.elms.dto.request.LoginRequest;
import com.nishu.elms.dto.request.RegisterRequest;
import com.nishu.elms.dto.response.LoginResponse;
import com.nishu.elms.dto.response.RegisterResponse;
import com.nishu.elms.entity.Role;
import com.nishu.elms.entity.User;
import com.nishu.elms.enums.RoleName;
import com.nishu.elms.exception.EmailAlreadyExistsException;
import com.nishu.elms.exception.ResourceNotFoundException;
import com.nishu.elms.repository.RoleRepository;
import com.nishu.elms.repository.UserRepository;
import com.nishu.elms.security.CustomUserDetails;
import com.nishu.elms.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        // Step 1 : Check email already exists
        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists.");
        }
        // Step 2 : Fetch Employee Role
        Role employeeRole = roleRepository.findByName(RoleName.ROLE_EMPLOYEE)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

        // Step 3 : Create User
        User user = User.builder().
                firstName(request.getFirstName()).
                lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber()).enabled(true)
                .build();
        Set<Role> roles = new HashSet<>();
        roles.add(employeeRole);
        user.setRoles(roles);

        //step 4:save
        User savedUser = userRepository.save(user);

        return RegisterResponse.builder().id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .message("User registered successfully.")
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        return LoginResponse.builder()
                .message("Login Successful")
                .email(userDetails.getUsername())
                .role(role)
                .token(null)
                .build();
    }
}
