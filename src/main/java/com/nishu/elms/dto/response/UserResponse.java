package com.nishu.elms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder

public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private boolean enabled;
    private String department;
    private Set<String> roles;
}
