package com.capstone.workspace.services.application.application_approval;

import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.enums.application.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class ApproveCreateOrganizationApplication extends BaseApproveApplication {
    private static final Logger logger = LoggerFactory.getLogger(ApproveCreateOrganizationApplication.class);

    @Override
    @Transactional
    public void execute(Application application) {
        logger.info(String.valueOf(application.getId()));
        application.setStatus(ApplicationStatus.APPROVED);
        applicationRepository.save(application);
    }
}
