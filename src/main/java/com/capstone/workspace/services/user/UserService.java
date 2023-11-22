package com.capstone.workspace.services.user;

import com.capstone.workspace.dtos.auth.VerifyUserDto;
import com.capstone.workspace.dtos.auth.RegisterUserDto;
import com.capstone.workspace.dtos.user.AddEmployeeDto;
import com.capstone.workspace.dtos.user.UpdateUserProfileDto;
import com.capstone.workspace.entities.user.User;
import com.capstone.workspace.enums.auth.AuthProviderType;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.*;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.repositories.user.UserRepository;
import com.capstone.workspace.services.auth.IdentityService;
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
    private final IdentityService identityService;

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
            BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
            return entity;
        }

        return mapper.map(dto, User.class);
    }

    @Transactional
    public User create(RegisterUserDto dto) {
        return save(dto);
    }

    private User save(RegisterUserDto dto) {
        User entity = upsert(null, dto);
        String username = entity.getUsername();

        // TODO: prevent login from devices that do not support user type
        // validateUsername(username);

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
            entity.setType(UserType.OWNER);
        }

        return repository.save(entity);
    }

    public User addEmployee(AddEmployeeDto dto) {
        User entity = upsert(null, dto);
        String username = entity.getUsername();

        // TODO: prevent login from devices that do not support user type
        // validateUsername(username);

        if (dto.getType() != UserType.STORE_MANAGER && dto.getType() != UserType.STAFF) {
            throw new BadRequestException("Not allow to create this type of user");
        }

        if (AppHelper.isEmail(username) || AppHelper.isVietnamNumberPhone(username)) {
            throw new BadRequestException("Username must not be an email or number phone");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new BadRequestException("Email must not be null or empty");
        }
        entity.setProvider(AuthProviderType.USERNAME);

        return repository.save(entity);
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
        UserIdentity userIdentity = identityService.getUserIdentity();
        return getUserByUsername(userIdentity.getUsername());
    }

    public void addUserToGroup(String groupName, String username) {
        User user = getUserByUsername(username);
        if (user.getPartnerId() != null) {
            throw new ConflictException("User already in a group");
        }

        user.setPartnerId(groupName);
        repository.save(user);
    }

    public User updateProfile(UpdateUserProfileDto dto) {
        User entity = getMyOwnProfile();

        if (dto.getEmail() != null && entity.getProvider() == AuthProviderType.USERNAME && !dto.getEmail().equalsIgnoreCase(entity.getEmail())) {
            throw new ConflictException("Not allow to update email");
        }

        if (dto.getPhone() != null && entity.getProvider() == AuthProviderType.PHONE && !dto.getPhone().equals(entity.getPhone())) {
            throw new ConflictException("Not allow to update phone");
        }

        BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
        return repository.save(entity);
    }
}
