package com.capstone.workspace.configurations.application;

import com.capstone.workspace.enums.application.ApplicationEvent;
import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.shared.JobService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.util.UUID;

@Configuration
@EnableStateMachine
@RequiredArgsConstructor
public class ApplicationStateMachineConfiguration extends EnumStateMachineConfigurerAdapter<ApplicationStatus, ApplicationEvent> {
    @NonNull
    private final JobService jobService;

    @NonNull
    private final IdentityService identityService;

    @Override
    public void configure(StateMachineStateConfigurer<ApplicationStatus, ApplicationEvent> states) throws Exception {
        states
            .withStates()
            .initial(ApplicationStatus.INITIAL)
            .state(ApplicationStatus.DRAFT)
            .state(ApplicationStatus.WAITING_FOR_APPROVAL)
            .state(ApplicationStatus.PROCESSING)
            .state(ApplicationStatus.REJECTED)
            .state(ApplicationStatus.APPROVED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ApplicationStatus, ApplicationEvent> transitions) throws Exception {
        transitions

            // Init real state
            .withExternal()
                .source(ApplicationStatus.INITIAL)
                .target(ApplicationStatus.DRAFT)
                .event(ApplicationEvent.TO_DRAFT)
                .guard(hasActions())
                .and()
            .withExternal()
                .source(ApplicationStatus.INITIAL)
                .target(ApplicationStatus.WAITING_FOR_APPROVAL)
                .event(ApplicationEvent.TO_WAITING_FOR_APPROVAL)
                .guard(hasActions())
                .and()
            .withExternal()
                .source(ApplicationStatus.INITIAL)
                .target(ApplicationStatus.PROCESSING)
                .event(ApplicationEvent.TO_PROCESSING)
                .guard(hasActions())
                .and()

            // Real state transitions
            .withExternal()
                .source(ApplicationStatus.DRAFT)
                .target(ApplicationStatus.WAITING_FOR_APPROVAL)
                .event(ApplicationEvent.SUBMIT)
                .guard(hasActions())
                .and()
            .withExternal()
                .source(ApplicationStatus.WAITING_FOR_APPROVAL)
                .target(ApplicationStatus.DRAFT)
                .event(ApplicationEvent.AMEND)
                .guard(hasActions())
                .and()
            .withExternal()
                .source(ApplicationStatus.WAITING_FOR_APPROVAL)
                .target(ApplicationStatus.PROCESSING)
                .event(ApplicationEvent.PROCESS)
                .guard(isAdmin())
                .and()
            .withExternal()
                .source(ApplicationStatus.PROCESSING)
                .target(ApplicationStatus.REJECTED)
                .event(ApplicationEvent.REJECT)
                .guard(isAdmin())
                .and()
            .withExternal()
                .source(ApplicationStatus.PROCESSING)
                .target(ApplicationStatus.APPROVED)
                .event(ApplicationEvent.APPROVE)
                .action(approve())
                .guard(isAdmin());
    }

    @Bean
    public Action<ApplicationStatus, ApplicationEvent> approve() {
        return context -> {
            UUID applicationId = (UUID) context.getExtendedState().getVariables().get("applicationId");
            jobService.publishApprovedApplicationJob(applicationId);
        };
    }

    @Bean
    public Guard<ApplicationStatus, ApplicationEvent> hasActions() {
        return context -> {
            return true;
        };
    }

    @Bean
    public Guard<ApplicationStatus, ApplicationEvent> isAdmin() {
        return context -> {
            UserIdentity userIdentity = identityService.getUserIdentity();
            return userIdentity != null && userIdentity.getUserType() == UserType.ADMIN;
        };
    }
}
