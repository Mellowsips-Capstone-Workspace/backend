package com.capstone.workspace.services.auth;

import com.capstone.workspace.dtos.auth.PasswordLoginDto;
import com.capstone.workspace.dtos.auth.ResendConfirmationCodeDto;
import com.capstone.workspace.dtos.auth.ResetPasswordDto;
import com.capstone.workspace.dtos.auth.VerifyUserDto;
import com.capstone.workspace.dtos.auth.RegisterUserDto;
import com.capstone.workspace.dtos.user.AddEmployeeDto;
import com.capstone.workspace.entities.user.User;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    @NonNull
    private final UserService userService;

    @NonNull
    private final CognitoService cognitoService;

    @NonNull
    private final IdentityService identityService;

    @Transactional
    public User register(RegisterUserDto dto) {
        try {
            User user = userService.getUserByUsername(dto.getUsername());
            if (user != null || dto.getUsername().equalsIgnoreCase("system")) {
                throwConflictUsernameException(dto.getUsername());
            }
        } catch (NotFoundException notFoundException) {
            try {
                User createdEntity = userService.create(dto);
                cognitoService.registerUserByPassword(dto);
                return createdEntity;
            } catch (UsernameExistsException usernameExistsException) {
                throwConflictUsernameException(dto.getUsername());
            }
        }

        return null;
    }

    public Map loginByPassword(PasswordLoginDto dto) {
        // TODO: prevent login from devices that do not support user type
        // userService.validateUsername(dto.getUsername());
        userService.loginByPassword(dto.getUsername());
        return cognitoService.loginUserByPassword(dto.getUsername(), dto.getPassword(), true);
    }

    @Transactional
    public void verifyUser(VerifyUserDto dto) {
        userService.verifyUser(dto.getUsername());
        cognitoService.verifyUser(dto.getUsername(), dto.getConfirmationCode());
    }

    public void logout() {
        UserIdentity userIdentity = identityService.getUserIdentity();
        String username = userIdentity.getUsername();
        userService.getUserByUsername(username);
        cognitoService.logout(username);
    }

    public void resendConfirmationCode(ResendConfirmationCodeDto dto) {
        User user = userService.getUserByUsername(dto.getUsername());
        if (user.getIsVerified() != null && user.getIsVerified()) {
            throw new BadRequestException("User is already verified");
        }

        cognitoService.resendConfirmationCode(dto.getUsername());
    }

    private void throwConflictUsernameException(String username) {
        String usernameType = AppHelper.isVietnamNumberPhone(username) ? "phone" : "username";
        throw new ConflictException("User with this " + usernameType + " already exists");
    }

    @Transactional
    public void addUserToGroup(String groupName, String username) {
        userService.addUserToGroup(groupName, username);
    }

    @Transactional
    public void resetPassword(ResetPasswordDto dto) {
        userService.getUserByUsername(dto.getUsername());

        if (dto.getNewPassword().equals(dto.getPassword())) {
            throw new BadRequestException("New password must be different from current one");
        }

        Map res = cognitoService.loginUserByPassword(dto.getUsername(), dto.getPassword(), false);
        if (res == null) {
            userService.verifyUser(dto.getUsername());
        }
        cognitoService.resetPassword(dto.getUsername(), dto.getNewPassword());
    }

    @Transactional
    public User addEmployee(AddEmployeeDto dto) {
        try {
            User user = userService.getUserByUsername(dto.getUsername());
            if (user != null || dto.getUsername().equalsIgnoreCase("system")) {
                throwConflictUsernameException(dto.getUsername());
            }
        } catch (NotFoundException notFoundException) {
            try {
                User createdEntity = userService.addEmployee(dto);
                cognitoService.adminCreateUser(dto);
                return createdEntity;
            } catch (UsernameExistsException usernameExistsException) {
                throwConflictUsernameException(dto.getUsername());
            }
        }

        return null;
    }
}
