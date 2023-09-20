package com.capstone.workspace.services.application.application_machine;

import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.enums.application.ApplicationEvent;
import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.repositories.application.ApplicationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApplicationStateMachine {
    @NonNull
    private StateMachine<ApplicationStatus, ApplicationEvent> stateMachine;

    @NonNull
    private final ApplicationRepository applicationRepository;

    public void init(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId).orElse(null);

        if (application != null) {
            stateMachine.start();

            ApplicationStatus initialState = application.getStatus();
            ApplicationEvent initialEvent = ApplicationEvent.valueOf("TO_" + initialState);
            stateMachine.sendEvent(initialEvent);

            stateMachine.getExtendedState().getVariables().put("applicationId", applicationId);
            // stateMachine.getExtendedState().getVariables().put("actions", userActions);
            stateMachine.getExtendedState().getVariables().put("application", application);
        }
    }

    private boolean can(ApplicationStatus currentState, ApplicationEvent eventType) {
        Collection<Transition<ApplicationStatus, ApplicationEvent>> transitions = stateMachine.getTransitions();
        return transitions.stream()
            .anyMatch(
                transition -> transition.getSource().getId() == currentState && transition.getTrigger().getEvent() == eventType
            );
    }

    public ApplicationStatus transition(ApplicationStatus currentState, ApplicationEvent eventType) {
        if (!can(currentState, eventType)) {
            throw new ForbiddenException("Event is not accepted");
        }

        stateMachine.sendEvent(eventType);
        ApplicationStatus newState = stateMachine.getState().getId();
        stateMachine.stop();

        if (newState == currentState) {
            throw new ForbiddenException("You are not allowed to execute this action");
        }

        return newState;
    }
}
