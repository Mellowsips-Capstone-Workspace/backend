package com.capstone.workspace.configurations.application;

import com.capstone.workspace.enums.application.ApplicationEvent;
import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.services.shared.JobService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableStateMachine
@RequiredArgsConstructor
public class ApplicationStateMachineConfiguration extends EnumStateMachineConfigurerAdapter<ApplicationStatus, ApplicationEvent> {
    @NonNull
    private final JobService jobService;

    @Override
    public void configure(StateMachineStateConfigurer<ApplicationStatus, ApplicationEvent> states) throws Exception {
        states
            .withStates()
            .initial(ApplicationStatus.DRAFT)
            .initial(ApplicationStatus.WAITING_FOR_APPROVAL)
            .state(ApplicationStatus.DRAFT)
            .state(ApplicationStatus.WAITING_FOR_APPROVAL)
            .state(ApplicationStatus.PROCESSING)
            .end(ApplicationStatus.REJECTED)
            .end(ApplicationStatus.APPROVED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ApplicationStatus, ApplicationEvent> transitions) throws Exception {
        transitions
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
                .guard(hasActions())
                .and()
            .withExternal()
                .source(ApplicationStatus.PROCESSING)
                .target(ApplicationStatus.REJECTED)
                .event(ApplicationEvent.REJECT)
                .guard(hasActions())
                .and()
            .withExternal()
                .source(ApplicationStatus.PROCESSING)
                .target(ApplicationStatus.APPROVED)
                .event(ApplicationEvent.APPROVE)
                .action(approve())
                .guard(hasActions());
    }

    @Bean
    public Action<ApplicationStatus, ApplicationEvent> approve() {
        return context -> {
//            StateMachine stateMachine = context.getStateMachine();

            UUID applicationId = (UUID) context.getExtendedState().getVariables().get("applicationId");
            ApplicationStatus lastState = (ApplicationStatus) context.getExtendedState().getVariables().get("lastState");

            Map<String, Object> payload = new HashMap<>();
            payload.put("applicationId", applicationId);
            payload.put("lastState", lastState);

            jobService.publishApprovedApplicationJob(payload);
//
//            stateMachine.getExtendedState().getVariables().put("lastState", ApplicationStatus.APPROVED);
//            stateMachine.stop();
        };
    }

    @Bean
    public Guard<ApplicationStatus, ApplicationEvent> hasActions() {
        return context -> {
            return true;
        };
    }
}
