package com.capstone.workspace.services.voucher;

import com.capstone.workspace.dtos.voucher.CreateVoucherDto;
import com.capstone.workspace.dtos.voucher.SearchVoucherCriteriaDto;
import com.capstone.workspace.dtos.voucher.SearchVoucherDto;
import com.capstone.workspace.dtos.voucher.UpdateVoucherDto;
import com.capstone.workspace.entities.voucher.Voucher;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.enums.voucher.VoucherDiscountType;
import com.capstone.workspace.exceptions.*;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.cart.CartDetailsModel;
import com.capstone.workspace.models.voucher.VoucherCartModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.voucher.VoucherModel;
import com.capstone.workspace.repositories.voucher.VoucherRepository;
import com.capstone.workspace.services.auth.IdentityService;
import jakarta.persistence.OptimisticLockException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.*;

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
        } else {
            if (dto.getMaxDiscountAmount() == null || dto.getMaxDiscountAmount() < 1000L) {
                throw new BadRequestException("Max discount amount must be equals or greater than 1000 VND");
            }
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
            if (dto.getDiscountType() == VoucherDiscountType.CASH) {
                dto.setMaxDiscountAmount(null);
            } else {
                if (dto.getMaxDiscountAmount() == null || dto.getMaxDiscountAmount() < 1000L) {
                    throw new BadRequestException("Max discount amount must be equals or greater than 1000 VND");
                }
            }
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

    public Map customerGetCartVoucher(CartDetailsModel cartDetailsModel, Boolean isHidden) {
        Map<String, List<VoucherCartModel>> result = new HashMap<>();

        List<Voucher> entities = repository.customerCartGetVouchers(
            cartDetailsModel.getCreatedBy(),
            cartDetailsModel.getStore().getPartnerId(),
            String.valueOf(cartDetailsModel.getStore().getId())
        );

        if (Boolean.TRUE.equals(isHidden)) {
            entities = entities.stream().filter(item -> Boolean.TRUE.equals(item.getIsHidden())).toList();
        }

        List<VoucherCartModel> models = mapper.map(
                entities,
                new TypeToken<List<VoucherCartModel>>() {}.getType()
        );

        List<VoucherCartModel> mappedModels = models.stream()
            .map(item -> {
                item.setCanUse(item.getMinOrderAmount() <= cartDetailsModel.getTempPrice());
                if (item.getDiscountType() == VoucherDiscountType.PERCENT) {
                    long discountAmount = item.getValue() * cartDetailsModel.getTempPrice() / 100;
                    item.setDiscountAmount(Math.min(discountAmount, item.getMaxDiscountAmount()));
                } else {
                    item.setDiscountAmount(item.getValue());
                }
                return item;
            }).toList();

        List<VoucherCartModel> systemVouchers = mappedModels.stream().filter(item -> item.getPartnerId() == null).toList();
        result.put("SYSTEM", systemVouchers);

        List<VoucherCartModel> businessVouchers = mappedModels.stream().filter(item -> item.getPartnerId() != null).toList();
        result.put("BUSINESS", businessVouchers);

        return result;
    }

    public PaginationResponseModel<VoucherModel> search(SearchVoucherDto dto) {

        String[] searchableFields = new String[]{"name"};
        Map<String, Object> filterParams = new HashMap<>();

        SearchVoucherCriteriaDto criteria = dto.getCriteria();
        String keyword = null;
        Map orderCriteria = null;

        if (criteria != null) {
            if (criteria.getFilter() != null) {
                filterParams = AppHelper.copyPropertiesToMap(criteria.getFilter());
            }
            keyword = criteria.getKeyword();
            orderCriteria = criteria.getOrder();
        }

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity.getUserType() == UserType.ADMIN) {
            filterParams.put("partnerId", null);
            filterParams.put("storeId", null);
        }

        PaginationResponseModel result = repository.searchBy(
                keyword,
                searchableFields,
                filterParams,
                orderCriteria,
                dto.getPagination()
        );

        List<VoucherModel> voucherModels = mapper.map(
                result.getResults(),
                new TypeToken<List<VoucherModel>>() {}.getType()
        );
        result.setResults(voucherModels);

        return result;
    }

    public Voucher useVoucher(UUID id) {
        int count = 0;
        while (true) {
            try {
                count++;
                Voucher entity = getOneById(id);

                int newQuantity = entity.getQuantity() - 1;
                if (newQuantity < 0) {
                    throw new BadRequestException("Out of voucher " + entity.getCode());
                }

                return repository.useVoucher(id);
            } catch (OptimisticLockException e) {
                if (count == 3) {
                    throw new ServiceUnavailableException("Service is unavailable now. Please try again");
                }
            }
        }
    }

    public Voucher revokeVoucher(UUID id) {
        int count = 0;
        while (true) {
            try {
                count++;
                getOneById(id);
                return repository.revokeVoucher(id);
            } catch (OptimisticLockException e) {
                if (count == 3) {
                    throw new ServiceUnavailableException("Service is unavailable now. Please try again");
                }
            }
        }
    }
}
