package com.capstone.workspace.controllers.voucher;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.voucher.CreateVoucherDto;
import com.capstone.workspace.entities.voucher.Voucher;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.voucher.VoucherModel;
import com.capstone.workspace.services.voucher.VoucherService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
