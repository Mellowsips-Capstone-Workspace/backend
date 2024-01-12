package com.capstone.workspace.jobs.handlers;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.jobs.requests.RevokeVoucherJobRequest;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.order.OrderService;
import com.capstone.workspace.services.voucher.VoucherOrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RevokeVoucherJobRequestHandler implements JobRequestHandler<RevokeVoucherJobRequest> {
    private static Logger logger = LoggerFactory.getLogger(RevokeVoucherJobRequestHandler.class);

    @NonNull
    private final VoucherOrderService voucherOrderService;

    @NonNull
    private final OrderService orderService;

    @NonNull
    private final IdentityService identityService;

    @Override
    @Job(name = "Revoke voucher job")
    public void run(RevokeVoucherJobRequest request) throws Exception {
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setUsername("system");
        userIdentity.setUserType(UserType.ADMIN);
        identityService.setUserIdentity(userIdentity);

        logger.info("Start revoking voucher");
        Order order = orderService.getOneById(request.getOrderId());
        voucherOrderService.revoke(order);
        logger.info("Complete revoking voucher");
    }
}
