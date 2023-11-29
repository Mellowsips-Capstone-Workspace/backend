package com.capstone.workspace.controllers.user;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.user.AddEmployeeDto;
import com.capstone.workspace.dtos.user.SearchUserDto;
import com.capstone.workspace.entities.user.User;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.user.UserModel;
import com.capstone.workspace.services.auth.AuthService;
import com.capstone.workspace.services.user.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( "/api/users")
@RequiredArgsConstructor
public class UserController {
    @NonNull
    private final AuthService authService;

    @NonNull
    private final UserService userService;

    @NonNull
    private final ModelMapper mapper;

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER})
    @PostMapping
    public ResponseModel<UserModel> addEmployee(@Valid @RequestBody AddEmployeeDto dto) {
        User entity = authService.addEmployee(dto);
        UserModel model = mapper.map(entity, UserModel.class);
        return ResponseModel.<UserModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.ADMIN, UserType.OWNER, UserType.STORE_MANAGER})
    @PostMapping("/search")
    public ResponseModel<PaginationResponseModel<UserModel>> search(@Valid @RequestBody SearchUserDto dto) {
        PaginationResponseModel<UserModel> data = userService.search(dto);
        return ResponseModel.<PaginationResponseModel<UserModel>>builder().data(data).build();
    }

    @AllowedUsers(userTypes = {UserType.ADMIN, UserType.OWNER, UserType.STORE_MANAGER})
    @PutMapping("/{id}/activate")
    public ResponseModel<UserModel> activateUser(@PathVariable UUID id) {
        User entity = userService.activate(id);
        UserModel model = mapper.map(entity, UserModel.class);
        return ResponseModel.<UserModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.ADMIN, UserType.OWNER, UserType.STORE_MANAGER})
    @PutMapping("/{id}/deactivate")
    public ResponseModel<UserModel> deactivateUser(@PathVariable UUID id) {
        User entity = userService.deactivate(id);
        UserModel model = mapper.map(entity, UserModel.class);
        return ResponseModel.<UserModel>builder().data(model).build();
    }
}
