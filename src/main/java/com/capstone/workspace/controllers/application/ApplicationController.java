package com.capstone.workspace.controllers.application;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.application.CreateApplicationDto;
import com.capstone.workspace.dtos.application.SearchApplicationDto;
import com.capstone.workspace.dtos.application.UpdateApplicationDto;
import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.application.ApplicationModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.services.application.ApplicationService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( "/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    @NonNull
    private final ApplicationService applicationService;

    @NonNull
    private final ModelMapper mapper;

    @AllowedUsers(userTypes = {UserType.OWNER})
    @PostMapping
    public ResponseModel<ApplicationModel> create(@Valid @RequestBody CreateApplicationDto params) {
        Application entity = applicationService.create(params);
        ApplicationModel model = mapper.map(entity, ApplicationModel.class);
        return ResponseModel.<ApplicationModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.ADMIN})
    @PutMapping("/{id}/events/{event}")
    public ResponseModel<ApplicationModel> transition(@PathVariable UUID id, @PathVariable String event) {
        Application entity = applicationService.transition(id, event);
        ApplicationModel model = mapper.map(entity, ApplicationModel.class);
        return ResponseModel.<ApplicationModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.ADMIN})
    @GetMapping("/{id}")
    public ResponseModel<ApplicationModel> getApplicationById(@PathVariable UUID id) {
        Application entity = applicationService.getApplicationById(id);
        ApplicationModel model = mapper.map(entity, ApplicationModel.class);
        return ResponseModel.<ApplicationModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.ADMIN})
    @PostMapping("/search")
    public ResponseModel<PaginationResponseModel<ApplicationModel>> search(@Valid @RequestBody SearchApplicationDto dto) {
        PaginationResponseModel<ApplicationModel> data = applicationService.search(dto);
        return ResponseModel.<PaginationResponseModel<ApplicationModel>>builder().data(data).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER})
    @PutMapping("/{id}")
    public ResponseModel<ApplicationModel> update(@PathVariable UUID id, @Valid @RequestBody UpdateApplicationDto params) {
        Application entity = applicationService.update(id, params);
        ApplicationModel model = mapper.map(entity, ApplicationModel.class);
        return ResponseModel.<ApplicationModel>builder().data(model).build();
    }
}
