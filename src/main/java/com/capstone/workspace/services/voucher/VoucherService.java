package com.capstone.workspace.services.voucher;

import com.capstone.workspace.dtos.voucher.CreateVoucherDto;
import com.capstone.workspace.dtos.voucher.UpdateVoucherDto;
import com.capstone.workspace.entities.voucher.Voucher;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.enums.voucher.VoucherDiscountType;
import com.capstone.workspace.exceptions.*;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.repositories.voucher.VoucherRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoucherService {
    private static Logger logger = LoggerFactory.getLogger(VoucherService.class);

    @NonNull
    private final VoucherRepository repository;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final IdentityService identityService;

    public Voucher create(CreateVoucherDto dto) {
        validate(dto);
        Voucher duplicate = repository.findByCode(dto.getCode());
        if (duplicate != null) {
            throw new ConflictException("Voucher code already exists");
        }

        Instant now = Instant.now();
        Voucher entity = upsert(null, dto);
        if (dto.getStartDate() == null || dto.getStartDate().isBefore(now)) {
            entity.setStartDate(now);
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
        if (dto.getStartDate() != null && dto.getEndDate() != null && (dto.getEndDate().isBefore(Instant.now()) || dto.getStartDate().isAfter(dto.getEndDate()))) {
            throw new BadRequestException("End date must be after start date and in the future");
        }

        if (dto.getDiscountType() == VoucherDiscountType.CASH && dto.getValue() < 1000L) {
            throw new BadRequestException("Voucher discount amount must be equals or greater than 1000 VND");
        }

        if (dto.getDiscountType() == VoucherDiscountType.PERCENT && (dto.getValue() < 1L || dto.getValue() > 100L)) {
            throw new BadRequestException("Voucher discount percent value must be between 1 and 100");
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

    public Voucher update(UUID id, UpdateVoucherDto dto) {
        Voucher entity = getOneById(id);

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity.getUserType() == UserType.ADMIN && entity.getPartnerId() != null) {
            throw new ForbiddenException("You are not allowed to update partner's voucher");
        }

        Instant now = Instant.now();
        if (entity.getStartDate().isBefore(now)) {
            if (entity.getEndDate().isBefore(now)) {
                throw new GoneException("Voucher has expired");
            }

            if (dto.getEndDate() != null && dto.getEndDate().isBefore(now)) {
                throw new BadRequestException("End date must be in the future");
            }
            BeanUtils.copyProperties(dto, entity, "discountType", "startDate", "minOrderAmount", "maxDiscountAmount", "isHidden", "value");
        } else {
            validate(mapper.map(dto, CreateVoucherDto.class));
            BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
        }

        return repository.save(entity);
    }

    public void delete(UUID id) {
        Voucher entity = getOneById(id);

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity.getUserType() == UserType.ADMIN && entity.getPartnerId() != null) {
            throw new ForbiddenException("You are not allowed to delete partner's voucher");
        }

        if (entity.getStartDate().isBefore(Instant.now())) {
            throw new BadRequestException("Promotion is ongoing or has ended");
        }
        repository.delete(entity);
    }

    public Voucher close(UUID id) {
        Voucher entity = getOneById(id);

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity.getUserType() == UserType.ADMIN && entity.getPartnerId() != null) {
            throw new ForbiddenException("You are not allowed to close partner's voucher");
        }

        Instant now = Instant.now();
        if (entity.getStartDate().isBefore(now) && (entity.getEndDate() == null || entity.getEndDate().isAfter(now))) {
            entity.setEndDate(now);
        } else {
            throw new BadRequestException("You cannot close the promotion");
        }

        return repository.save(entity);
    }
}
