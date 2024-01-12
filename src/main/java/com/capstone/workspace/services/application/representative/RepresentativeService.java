package com.capstone.workspace.services.application.representative;

import com.capstone.workspace.entities.application.Representative;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.application.RepresentativeModel;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.repositories.application.RepresentativeRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepresentativeService {
    @NonNull
    private final RepresentativeRepository repository;

    @NonNull
    private final IdentityService identityService;

    public Representative create(RepresentativeModel dto) {
        UserIdentity userIdentity = identityService.getUserIdentity();

        String partnerId = userIdentity != null ? userIdentity.getPartnerId() : null;
        if (partnerId == null) {
            throw new NotFoundException("Missing partner id");
        }

        Representative exist = repository.findByPartnerId(partnerId);
        if (exist != null) {
            throw new ConflictException("Not allow to have multiple representative");
        }

        Representative entity = new Representative();
        BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);

        return repository.save(entity);
    }
}
