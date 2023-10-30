package com.capstone.workspace.services.voucher;

import com.capstone.workspace.dtos.voucher.CreateVoucherDto;
import com.capstone.workspace.entities.voucher.Voucher;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.enums.voucher.VoucherDiscountType;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.repositories.voucher.VoucherRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoucherService {
    @NonNull
    private final VoucherRepository repository;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final IdentityService identityService;

    public Voucher create(CreateVoucherDto dto) {
        validate(dto);

        Voucher entity = upsert(null, dto);
        if (dto.getStartDate() == null) {
            entity.setStartDate(Instant.now());
        }
        if (dto.getDiscountType() == VoucherDiscountType.CASH) {
            entity.setMaxDiscountAmount(null);
        }

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity.getUserType() != UserType.OWNER) {
            entity.setStoreId(null);
        }

        return repository.save(entity);
    }

    private void validate(CreateVoucherDto dto) {
        Voucher duplicate = repository.findByCode(dto.getCode());
        if (duplicate != null) {
            throw new ConflictException("Voucher code already exists");
        }

        if (dto.getEndDate().isBefore(Instant.now()) || (dto.getStartDate() != null && dto.getEndDate() != null && dto.getStartDate().isAfter(dto.getEndDate()))) {
            throw new BadRequestException("End date must be after start date and in the future");
        }
    }

    private Voucher upsert(UUID id, Object dto) {
        if (id != null) {
            Voucher entity = getOneById(id);
            BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
            return entity;
        }

        return mapper.map(dto, Voucher.class);
    }

    public Voucher getOneById(UUID id) {
        Voucher entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Voucher not found");
        }

        return entity;
    }
}
