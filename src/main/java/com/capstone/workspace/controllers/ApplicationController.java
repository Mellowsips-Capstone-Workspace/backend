package com.capstone.workspace.controllers;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.CreateApplicationDto;
import com.capstone.workspace.entities.Application;
import com.capstone.workspace.enums.UserType;
import com.capstone.workspace.models.ApplicationModel;
import com.capstone.workspace.models.ResponseModel;
import com.capstone.workspace.services.ApplicationService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    @NonNull
    private final ApplicationService applicationService;

    @NonNull
    private final ModelMapper mapper;

    @AllowedUsers(userTypes = {UserType.EMPLOYEE})
    @PostMapping
    public ResponseModel<ApplicationModel> create(@Valid @RequestBody CreateApplicationDto params) {
        Application entity = applicationService.create(params);
        ApplicationModel model = mapper.map(entity, ApplicationModel.class);
        return ResponseModel.<ApplicationModel>builder().data(model).build();
    }
}
