package com.capstone.workspace.controllers;

import com.capstone.workspace.dtos.*;
import com.capstone.workspace.entities.User;
import com.capstone.workspace.models.ResponseModel;
import com.capstone.workspace.models.UserModel;
import com.capstone.workspace.services.AuthService;
import com.capstone.workspace.services.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping( "/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @NonNull
    private final AuthService authService;

    @NonNull
    private final UserService userService;

    @NonNull
    private final ModelMapper mapper;

    @PostMapping("/register")
    public ResponseModel register(@Valid @RequestBody RegisterUserDto params) {
        authService.register(params);
        return ResponseModel.builder().message("Sign up successfully").build();
    }

    @PostMapping("/login")
    public ResponseModel loginByPassword(@Valid @RequestBody PasswordLoginDto params) {
        Map model = authService.loginByPassword(params);
        return ResponseModel.builder().data(model).build();
    }

    @PostMapping("/verify")
    public ResponseModel verify(@Valid @RequestBody VerifyUserDto params) {
        authService.verifyUser(params);
        return ResponseModel.builder().message("Verify user successfully").build();
    }

    @PostMapping("/resend-confirmation-code")
    public ResponseModel resendConfirmationCode(@Valid @RequestBody ResendConfirmationCodeDto params) {
        authService.resendConfirmationCode(params);
        return ResponseModel.builder().message("Resend confirmation code successfully").build();
    }

    @PostMapping("/logout")
    public ResponseModel logout() {
        authService.logout();
        return ResponseModel.builder().message("Logout successfully").build();
    }

    @GetMapping("/me/profile")
    public ResponseModel<UserModel> getMyOwnProfile() {
        User user = userService.getMyOwnProfile();
        UserModel userModel = mapper.map(user, UserModel.class);
        return ResponseModel.<UserModel>builder().data(userModel).build();
    }
}
