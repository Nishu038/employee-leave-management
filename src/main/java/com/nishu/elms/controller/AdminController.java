package com.nishu.elms.controller;

import com.nishu.elms.dto.request.CreateUserRequest;
import com.nishu.elms.dto.response.RegisterResponse;
import com.nishu.elms.dto.response.UserResponse;
import com.nishu.elms.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users")
    public ResponseEntity<RegisterResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminService.createUser(request));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 10)Pageable pageable
            ){
        return ResponseEntity.ok(
                adminService.getAllUsers(pageable)
        );
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(adminService.getUserById(id));
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody CreateUserRequest request
    ){
        return ResponseEntity.ok(adminService.updateUser(id,request));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){

        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
