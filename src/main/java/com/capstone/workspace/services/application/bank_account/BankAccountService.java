package com.capstone.workspace.services.application.bank_account;

import com.capstone.workspace.entities.application.BankAccount;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.repositories.application.BankAccountRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    @NonNull
    private final BankAccountRepository repository;

    @NonNull
    private final IdentityService identityService;

    public BankAccount create(Object dto) {
        UserIdentity userIdentity = identityService.getUserIdentity();

        String partnerId = userIdentity != null ? userIdentity.getPartnerId() : null;
        if (partnerId == null) {
            throw new NotFoundException("Missing partner id");
        }

        BankAccount exist = repository.findByPartnerId(partnerId);
        if (exist != null) {
            throw new ConflictException("Not allow to have multiple bank accounts");
        }

        BankAccount entity = new BankAccount();
        BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);

        return repository.save(entity);
    }
}
