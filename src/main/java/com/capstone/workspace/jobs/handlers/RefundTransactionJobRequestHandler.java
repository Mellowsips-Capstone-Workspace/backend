package com.capstone.workspace.jobs.handlers;

import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.jobs.requests.RefundTransactionJobRequest;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.order.TransactionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundTransactionJobRequestHandler implements JobRequestHandler<RefundTransactionJobRequest> {
    private static Logger logger = LoggerFactory.getLogger(RefundTransactionJobRequestHandler.class);

    @NonNull
    private final TransactionService transactionService;

    @NonNull
    private final IdentityService identityService;

    @Override
    @Job(name = "Refund transaction job")
    public void run(RefundTransactionJobRequest request) throws Exception {
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setUsername("system");
        userIdentity.setUserType(UserType.ADMIN);
        identityService.setUserIdentity(userIdentity);

        logger.info("Start refund transaction process");
        transactionService.handleCashback(request.getOrderId());
        logger.info("Complete refund transaction process");
    }
}
