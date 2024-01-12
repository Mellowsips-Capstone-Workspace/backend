package com.capstone.workspace.controllers.order;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.entities.order.Transaction;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.order.TransactionModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.services.order.TransactionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( "/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    @NonNull
    private final TransactionService transactionService;

    @NonNull
    private final ModelMapper mapper;

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF})
    @PutMapping("/{id}/pay")
    public ResponseModel<TransactionModel> pay(@PathVariable UUID id) {
        Transaction entity = transactionService.pay(id);
        TransactionModel model = mapper.map(entity, TransactionModel.class);
        return ResponseModel.<TransactionModel>builder().data(model).build();
    }
}
