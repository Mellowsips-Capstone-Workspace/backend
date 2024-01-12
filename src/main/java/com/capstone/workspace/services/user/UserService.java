package com.capstone.workspace.services.user;

import com.capstone.workspace.dtos.auth.VerifyUserDto;
import com.capstone.workspace.dtos.auth.RegisterUserDto;
import com.capstone.workspace.dtos.notification.PushNotificationDto;
import com.capstone.workspace.dtos.user.*;
import com.capstone.workspace.entities.user.User;
import com.capstone.workspace.enums.auth.AuthProviderType;
import com.capstone.workspace.enums.notification.NotificationKey;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.*;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.user.UserModel;
import com.capstone.workspace.repositories.user.UserRepository;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.notification.NotificationService;
import com.capstone.workspace.services.shared.JobService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    @NonNull
    private final JobService jobService;

    @NonNull
    private final NotificationService notificationService;

    public User getUserByUsername(String username) {
        User user = repository.findByUsername(username);

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        return user;
    }

    public void loginByPassword(String username) {
        User user = getUserByUsername(username);

        if (user.getInactiveUntil() != null) {
            if (Instant.now().isBefore(user.getInactiveUntil())) {
                throw new ForbiddenException("Your account has been blocked");
            }

            user.setInactiveUntil(null);
            user.setIsActive(true);
        } else {
            if (!Boolean.TRUE.equals(user.getIsActive())) {
                throw new ForbiddenException("Your account has been blocked");
            }
        }

        repository.save(user);
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

    public void verifyUser(String username) {
        User user = getUserByUsername(username);
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

        if (entity.getProvider() == AuthProviderType.USERNAME) {
            if (dto.getEmail() == null) {
                throw new BadRequestException("Email must not be null");
            }
            if (!dto.getEmail().equalsIgnoreCase(entity.getEmail())) {
                throw new ConflictException("Not allow to update email");
            }
        }

        if (entity.getProvider() == AuthProviderType.PHONE) {
            if (dto.getPhone() == null) {
                throw new BadRequestException("Phone must not be null");
            }
            if (!dto.getPhone().equals(entity.getPhone())) {
                throw new ConflictException("Not allow to update phone");
            }
        }

        BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
        return repository.save(entity);
    }

    public PaginationResponseModel<UserModel> search(SearchUserDto dto) {
        String[] searchableFields = new String[]{"username", "displayName", "email"};
        Map<String, Object> filterParams = Collections.emptyMap();

        SearchUserCriteriaDto criteria = dto.getCriteria();
        String keyword = null;
        Map orderCriteria = null;

        if (criteria != null) {
            if (criteria.getFilter() != null) {
                filterParams = AppHelper.copyPropertiesToMap(criteria.getFilter());
            }
            keyword = criteria.getKeyword();
            orderCriteria = criteria.getOrder();
        }

        PaginationResponseModel result = repository.searchBy(
                keyword,
                searchableFields,
                filterParams,
                orderCriteria,
                dto.getPagination()
        );

        List<UserModel> userModels = mapper.map(
                result.getResults(),
                new TypeToken<List<UserModel>>() {}.getType()
        );
        result.setResults(userModels);

        return result;
    }

    public User activate(UUID id) {
        User entity = getUserById(id);
        validateUserPermission(entity);

        if (Boolean.TRUE.equals(entity.getIsActive())) {
            throw new ConflictException("User has been activated");
        }

        entity.setIsActive(true);
        entity.setInactiveUntil(null);
        return repository.save(entity);
    }

    public void handleCustomerFlake(String username) {
        User user = getUserByUsername(username);

        int flakeCount = user.getNumberOfFlakes();
        flakeCount++;
        if (flakeCount >= 3) {
            user.setInactiveUntil(Instant.now().plus(30, ChronoUnit.DAYS));
            user.setIsActive(false);
            user.setNumberOfFlakes(0);

            PushNotificationDto dto = PushNotificationDto.builder()
                    .key(String.valueOf(NotificationKey.ACCOUNT_BLOCKED))
                    .receivers(List.of(username))
                    .subject("Tài khoản của bạn đã bị khóa")
                    .shortDescription("Hệ thống tạm thời khóa tài khoản của bạn trong vòng 30 ngày")
                    .content("Hệ thống tạm thời khóa tài khoản của bạn trong vòng 30 ngày vì bạn đã bom hàng 3 lần.")
                    .metadata(new HashMap<>(){{
                        put("username", username);
                    }})
                    .build();
            notificationService.createPrivateNotification(dto);
        } else {
            user.setNumberOfFlakes(flakeCount);
        }

        repository.save(user);
    }

    public User deactivate(UUID id) {
        User entity = getUserById(id);
        validateUserPermission(entity);

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity.getUsername().equals(entity.getUsername())) {
            throw new BadRequestException("Not allow to deactivate yourself");
        }

        if (Boolean.FALSE.equals(entity.getIsActive())) {
            throw new ConflictException("User has been deactivated");
        }

        entity.setIsActive(false);
        return repository.save(entity);
    }

    public User updateUser(UUID userId, UpdateUserDto dto) {
        User entity = getUserById(userId);
        validateUserPermission(entity);

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity.getUserType() != UserType.OWNER) {
            dto.setType(entity.getType());
        }

        if (entity.getProvider() == AuthProviderType.USERNAME) {
            if (dto.getEmail() == null) {
                throw new BadRequestException("Email must not be null");
            }
            if (!dto.getEmail().equalsIgnoreCase(entity.getEmail())) {
                throw new ConflictException("Not allow to update email");
            }
        }

        if (entity.getProvider() == AuthProviderType.PHONE) {
            if (dto.getPhone() == null) {
                throw new BadRequestException("Phone must not be null");
            }
            if (!dto.getPhone().equals(entity.getPhone())) {
                throw new ConflictException("Not allow to update phone");
            }
        }

        BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
        return repository.save(entity);
    }

    private void validateUserPermission(User entity) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        UserType userType = userIdentity.getUserType();

        if (
                (userType == UserType.OWNER && !List.of(UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF).contains(entity.getType()))
                || (userType == UserType.STORE_MANAGER && entity.getType() != UserType.STAFF)
        ) {
            throw new ForbiddenException("You are not allowed to update this account");
        }
    }
}
