package com.capstone.workspace.services.partner;

import com.capstone.workspace.entities.partner.Partner;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.partner.PartnerModel;
import com.capstone.workspace.repositories.partner.PartnerRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartnerService {
    @NonNull
    private final PartnerRepository repository;

    @NonNull
    private final ModelMapper mapper;

    public Partner create(PartnerModel model) {
        Partner entity = upsert(null, model);
        return repository.save(entity);
    }

    private Partner upsert(UUID id, Object dto) {
        if (id != null) {
            Partner entity = getOneById(id);
            BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
            return entity;
        }

        return mapper.map(dto, Partner.class);
    }

    public Partner getOneById(UUID id) {
        Partner partner = repository.findById(id).orElse(null);

        if (partner == null) {
            throw new NotFoundException("Partner not found");
        }

        return partner;
    }
}
