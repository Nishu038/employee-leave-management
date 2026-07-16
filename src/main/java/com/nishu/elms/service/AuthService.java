package com.nishu.elms.service;

import com.nishu.elms.dto.request.LoginRequest;
import com.nishu.elms.dto.request.RegisterRequest;
import com.nishu.elms.dto.response.LoginResponse;
import com.nishu.elms.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
