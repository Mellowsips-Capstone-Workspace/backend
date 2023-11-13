package com.capstone.workspace.controllers.voucher;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.voucher.CreateVoucherDto;
import com.capstone.workspace.dtos.voucher.UpdateVoucherDto;
import com.capstone.workspace.entities.voucher.Voucher;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.voucher.VoucherModel;
import com.capstone.workspace.services.voucher.VoucherService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( "/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {
    @NonNull
    private final VoucherService voucherService;

    @NonNull
    private final ModelMapper mapper;

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.ADMIN})
    @PostMapping
    public ResponseModel<VoucherModel> create(@Valid @RequestBody CreateVoucherDto dto) {
        Voucher entity = voucherService.create(dto);
        VoucherModel model = mapper.map(entity, VoucherModel.class);
        return ResponseModel.<VoucherModel>builder().data(model).build();
    }

    @GetMapping("/details/{id}")
    public ResponseModel<VoucherModel> getVoucherById(@PathVariable UUID id) {
        Voucher entity = voucherService.getOneById(id);
        VoucherModel model = mapper.map(entity, VoucherModel.class);
        return ResponseModel.<VoucherModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.ADMIN})
    @PutMapping("/{id}")
    public ResponseModel<VoucherModel> update(@PathVariable UUID id, @Valid @RequestBody UpdateVoucherDto dto) {
        Voucher entity = voucherService.update(id, dto);
        VoucherModel model = mapper.map(entity, VoucherModel.class);
        return ResponseModel.<VoucherModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.ADMIN})
    @PutMapping("/{id}/close")
    public ResponseModel<VoucherModel> close(@PathVariable UUID id) {
        Voucher entity = voucherService.close(id);
        VoucherModel model = mapper.map(entity, VoucherModel.class);
        return ResponseModel.<VoucherModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.ADMIN})
    @DeleteMapping("/{id}")
    public ResponseModel delete(@PathVariable UUID id) {
        voucherService.delete(id);
        return ResponseModel.builder().message("Delete voucher successfully").build();
    }
}
