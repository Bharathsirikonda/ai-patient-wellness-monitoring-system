package com.patientwellness.service;

import com.patientwellness.dto.request.AuthLoginRequest;
import com.patientwellness.dto.request.AuthRegisterRequest;
import com.patientwellness.dto.response.AuthResponse;
import com.patientwellness.dto.response.UserResponse;
import com.patientwellness.security.UserPrincipal;

public interface AuthService {
    AuthResponse register(AuthRegisterRequest request);
    AuthResponse login(AuthLoginRequest request);
    UserResponse getCurrentUser(UserPrincipal principal);
}
