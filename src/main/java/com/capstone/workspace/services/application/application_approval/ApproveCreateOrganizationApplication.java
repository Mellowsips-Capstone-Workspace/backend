package com.capstone.workspace.services.application.application_approval;

import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.repositories.application.ApplicationRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApproveCreateOrganizationApplication extends BaseApproveApplication {
    private static final Logger logger = LoggerFactory.getLogger(ApproveCreateOrganizationApplication.class);

    @NonNull
    private final ApplicationRepository applicationRepository;

    @NonNull
    private final IdentityService identityService;

    @Override
    @Transactional
    public void execute(Application application) {
        application.setStatus(ApplicationStatus.APPROVED);

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity != null) {
            application.setApprovedBy(userIdentity.getUsername());
            application.setApprovedAt(LocalDateTime.now());
        }

        applicationRepository.save(application);
    }
}
