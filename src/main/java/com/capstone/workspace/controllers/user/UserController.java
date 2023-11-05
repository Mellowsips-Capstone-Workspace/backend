package com.capstone.workspace.controllers.user;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.user.AddEmployeeDto;
import com.capstone.workspace.entities.user.User;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.user.UserModel;
import com.capstone.workspace.services.auth.AuthService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/users")
@RequiredArgsConstructor
public class UserController {
    @NonNull
    private final AuthService authService;

    @NonNull
    private final ModelMapper mapper;

    @AllowedUsers(userTypes = {UserType.OWNER})
    @PostMapping
    public ResponseModel<UserModel> addEmployee(@Valid @RequestBody AddEmployeeDto dto) {
        User entity = authService.addEmployee(dto);
        UserModel model = mapper.map(entity, UserModel.class);
        return ResponseModel.<UserModel>builder().data(model).build();
    }
}
