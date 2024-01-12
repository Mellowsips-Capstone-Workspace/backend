package com.capstone.workspace.controllers.partner;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.partner.SearchPartnerDto;
import com.capstone.workspace.entities.partner.Partner;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.partner.PartnerModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.services.partner.PartnerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( "/api/partners")
@RequiredArgsConstructor
public class PartnerController {
    @NotNull
    private final PartnerService partnerService;

    @NotNull
    private final ModelMapper mapper;

    @AllowedUsers(userTypes = {UserType.ADMIN})
    @PostMapping("/search")
    public ResponseModel<PaginationResponseModel<PartnerModel>> search(@Valid @RequestBody SearchPartnerDto dto) {
        PaginationResponseModel<PartnerModel> data = partnerService.search(dto);
        return ResponseModel.<PaginationResponseModel<PartnerModel>>builder().data(data).build();
    }

    @AllowedUsers(userTypes = {UserType.ADMIN, UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF})
    @GetMapping("/{id}")
    public ResponseModel<PartnerModel> getOne(@PathVariable UUID id) {
        Partner entity = partnerService.getOneById(id);
        PartnerModel model = mapper.map(entity, PartnerModel.class);
        return ResponseModel.<PartnerModel>builder().data(model).build();
    }
}
