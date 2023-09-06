package com.capstone.workspace.services;

import com.capstone.workspace.dtos.CreateReceiverProfileDto;
import com.capstone.workspace.dtos.RegisterUserDto;
import com.capstone.workspace.dtos.VerifyUserDto;
import com.capstone.workspace.entities.User;
import com.capstone.workspace.enums.AuthErrorCode;
import com.capstone.workspace.enums.AuthProviderType;
import com.capstone.workspace.enums.UserType;
import com.capstone.workspace.exceptions.*;
import com.capstone.workspace.helpers.AppHelper;
import com.capstone.workspace.models.UserIdentity;
import com.capstone.workspace.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    @NonNull
    private final UserRepository repository;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final ReceiverProfileService receiverProfileService;

    @NonNull
    private final AuthContextService authContextService;

    @NonNull
    private final HttpServletRequest httpServletRequest;

    public User getUserByUsername(String username) {
        User user = repository.findByUsername(username);

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        return user;
    }

    public User getUserById(UUID id) {
        Optional<User> user = repository.findById(id);

        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        return user.get();
    }

    private User upsert(UUID id, Object dto) {
        if (id != null) {
            User entity = getUserById(id);
            BeanUtils.copyProperties(dto, entity);
            return entity;
        }

        return mapper.map(dto, User.class);
    }

    @Transactional
    public User create(RegisterUserDto dto) {
        User user = save(dto);
        createDefaultReceiverProfile(user);
        return user;
    }

    private User save(RegisterUserDto dto) {
        User entity = upsert(null, dto);
        String username = entity.getUsername();

        validateUsername(username);

        if (AppHelper.isEmail(username)) {
            throw new BadRequestException("Username must not be an email");
        }

        if (AppHelper.isVietnamNumberPhone(username)) {
            entity.setPhone(username);
            entity.setProvider(AuthProviderType.PHONE);
            entity.setType(UserType.CUSTOMER);
        } else {
            if (dto.getEmail() == null || dto.getEmail().isBlank()) {
                throw new BadRequestException("Email must not be null or empty");
            }
            entity.setProvider(AuthProviderType.USERNAME);
            entity.setType(UserType.EMPLOYEE);
        }

        return repository.save(entity);
    }

    private void createDefaultReceiverProfile(User user) {
        if (!AppHelper.isVietnamNumberPhone(user.getUsername()) || user.getPhone() == null || user.getPhone().isBlank()) {
            return;
        }

        CreateReceiverProfileDto receiverProfileDto = CreateReceiverProfileDto.builder()
                .name(user.getDisplayName())
                .phone(user.getPhone())
                .isDefault(true)
                .build();

        receiverProfileService.create(receiverProfileDto, user.getUsername());
    }

    public void validateUsername(String username) {
        String userAgent = httpServletRequest.getHeader("User-Agent");
        if (userAgent == null) {
            throw new BadRequestException("User-Agent header not found");
        }

        boolean isMobileDevice = userAgent.contains("Mobile");
        boolean isValidUsername = (!AppHelper.isVietnamNumberPhone(username) && !isMobileDevice)
                || (AppHelper.isVietnamNumberPhone(username) && isMobileDevice);

        if (!isValidUsername && !userAgent.contains("Postman")) {
            throw new NotAcceptableException("This user is not supported by this device");
        }
    }

    public void verifyUser(VerifyUserDto dto) {
        User user = getUserByUsername(dto.getUsername());
        user.setIsVerified(true);
        repository.save(user);
    }

    public User getMyOwnProfile() {
        UserIdentity userIdentity = authContextService.getUserIdentity();
        if (userIdentity == null || userIdentity.getUsername() == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        return getUserByUsername(userIdentity.getUsername());
    }
}