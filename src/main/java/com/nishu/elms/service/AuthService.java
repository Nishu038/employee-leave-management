package com.nishu.elms.service;

import com.nishu.elms.dto.request.RegisterRequest;
import com.nishu.elms.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
}
