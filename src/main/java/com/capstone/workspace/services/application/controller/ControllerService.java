package com.capstone.workspace.services.application.controller;

import com.capstone.workspace.entities.application.Controller;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.repositories.application.ControllerRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ControllerService {
    @NonNull
    private final ControllerRepository repository;

    @NonNull
    private final IdentityService identityService;

    public Controller create(Object dto) {
        UserIdentity userIdentity = identityService.getUserIdentity();

        String partnerId = userIdentity != null ? userIdentity.getPartnerId() : null;
        if (partnerId == null) {
            throw new NotFoundException("Missing partner id");
        }

        Controller exist = repository.findByPartnerId(partnerId);
        if (exist != null) {
            throw new ConflictException("Not allow to have multiple invoice controllers");
        }

        Controller entity = new Controller();
        BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);

        return repository.save(entity);
    }
}
