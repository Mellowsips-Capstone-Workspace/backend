package com.capstone.workspace.jobs.handlers;

import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.jobs.requests.HandleFlakerJobRequest;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandleFlakerJobRequestHandler implements JobRequestHandler<HandleFlakerJobRequest> {
    private static Logger logger = LoggerFactory.getLogger(HandleFlakerJobRequestHandler.class);

    @NonNull
    private final UserService userService;

    @NonNull
    private final IdentityService identityService;

    @Override
    @Job(name = "Handle customer flake job")
    public void run(HandleFlakerJobRequest request) throws Exception {
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setUsername("system");
        userIdentity.setUserType(UserType.ADMIN);
        identityService.setUserIdentity(userIdentity);

        logger.info("Start handling customer flake");
        userService.handleCustomerFlake(request.getUsername());
        logger.info("Complete handling customer flake");
    }
}
