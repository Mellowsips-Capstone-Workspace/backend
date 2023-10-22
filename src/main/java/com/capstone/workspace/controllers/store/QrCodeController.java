package com.capstone.workspace.controllers.store;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.store.QrCodeDto;
import com.capstone.workspace.entities.store.QrCode;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.store.QrCodeModel;
import com.capstone.workspace.services.store.QrCodeService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( "/api/qrcodes")
@RequiredArgsConstructor
public class QrCodeController {
    @NonNull
    private final QrCodeService qrCodeService;

    @NonNull
    private final ModelMapper mapper;

    @GetMapping("/{id}")
    public ResponseModel<QrCodeModel> getQrCodeById(@PathVariable UUID id) {
        QrCode entity = qrCodeService.getOneById(id);
        QrCodeModel model = mapper.map(entity, QrCodeModel.class);
        return ResponseModel.<QrCodeModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.EMPLOYEE})
    @PutMapping("/{id}")
    public ResponseModel<QrCodeModel> updateQrCodeById(@PathVariable UUID id, @Valid @RequestBody QrCodeDto dto) {
        QrCode entity = qrCodeService.update(id, dto);
        QrCodeModel model = mapper.map(entity, QrCodeModel.class);
        return ResponseModel.<QrCodeModel>builder().data(model).build();
    }
}
