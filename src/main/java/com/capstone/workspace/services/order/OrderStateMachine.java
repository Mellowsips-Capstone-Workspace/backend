package com.capstone.workspace.services.order;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.enums.order.OrderEvent;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.repositories.order.OrderRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderStateMachine {
    @NonNull
    private StateMachine<OrderStatus, OrderEvent> stateMachine;

    @NonNull
    private final OrderRepository orderRepository;

    public void init(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order != null) {
            stateMachine.start();

            OrderStatus initialState = order.getStatus();
            OrderEvent initialEvent = OrderEvent.valueOf("TO_" + initialState);
            stateMachine.sendEvent(initialEvent);

            stateMachine.getExtendedState().getVariables().put("orderId", orderId);
        }
    }

    private boolean can(OrderStatus currentState, OrderEvent eventType) {
        Collection<Transition<OrderStatus, OrderEvent>> transitions = stateMachine.getTransitions();
        return transitions.stream()
            .anyMatch(
                transition -> transition.getSource().getId() == currentState && transition.getTrigger().getEvent() == eventType
            );
    }

    public OrderStatus transition(OrderStatus currentState, OrderEvent eventType) {
        if (!can(currentState, eventType)) {
            throw new ForbiddenException("Event is not accepted");
        }

        stateMachine.sendEvent(eventType);
        OrderStatus newState = stateMachine.getState().getId();
        stateMachine.stop();

        if (newState == currentState) {
            throw new ForbiddenException("You are not allowed to execute this action");
        }

        return newState;
    }
}
