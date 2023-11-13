package com.capstone.workspace.jobs.handlers;

import com.capstone.workspace.jobs.requests.ExpiringOrderJobRequest;
import com.capstone.workspace.services.order.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiringOrderJobRequestHandler implements JobRequestHandler<ExpiringOrderJobRequest> {
    private final static Logger logger = LoggerFactory.getLogger(ExpiringOrderJobRequestHandler.class);

    @NonNull
    private final OrderService orderService;

    @Override
    @Job(name = "Expiring order after 15 minutes")
    public void run(ExpiringOrderJobRequest request) {
        logger.info("Start handle expiring order");
        orderService.expiringOrder(request.getOrderId());
        logger.info("Complete handle expiring order");
    }
}
