package com.capstone.workspace.configurations.order;

import com.capstone.workspace.enums.order.OrderEvent;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

@Configuration
@EnableStateMachine(name = {"orderStateMachineConfig"})
@RequiredArgsConstructor
public class OrderStateMachineConfiguration extends EnumStateMachineConfigurerAdapter<OrderStatus, OrderEvent> {
    @NonNull
    private final IdentityService identityService;

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvent> states) throws Exception {
        states
            .withStates()
            .initial(OrderStatus.INITIAL)
            .state(OrderStatus.PENDING)
            .state(OrderStatus.ORDERED)
            .state(OrderStatus.CANCELED)
            .state(OrderStatus.REJECTED)
            .state(OrderStatus.PROCESSING)
            .state(OrderStatus.COMPLETED)
            .state(OrderStatus.RECEIVED)
            .state(OrderStatus.DECLINED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvent> transitions) throws Exception {
        transitions

            // Init real state
            .withExternal()
                .source(OrderStatus.INITIAL)
                .target(OrderStatus.PENDING)
                .event(OrderEvent.TO_PENDING)
                .and()
            .withExternal()
                .source(OrderStatus.INITIAL)
                .target(OrderStatus.ORDERED)
                .event(OrderEvent.TO_ORDERED)
                .and()
            .withExternal()
                .source(OrderStatus.INITIAL)
                .target(OrderStatus.PROCESSING)
                .event(OrderEvent.TO_PROCESSING)
                .and()
            .withExternal()
                .source(OrderStatus.INITIAL)
                .target(OrderStatus.COMPLETED)
                .event(OrderEvent.TO_COMPLETED)
                .and()

            // Real state transitions
            .withExternal()
                .source(OrderStatus.PENDING)
                .target(OrderStatus.CANCELED)
                .event(OrderEvent.CANCEL)
                .guard(isCustomer())
                .and()
            .withExternal()
                .source(OrderStatus.ORDERED)
                .target(OrderStatus.CANCELED)
                .event(OrderEvent.CANCEL)
                .guard(isCustomer())
                .and()
            .withExternal()
                .source(OrderStatus.ORDERED)
                .target(OrderStatus.REJECTED)
                .event(OrderEvent.REJECT)
                .guard(isEmployee())
                .and()
            .withExternal()
                .source(OrderStatus.ORDERED)
                .target(OrderStatus.PROCESSING)
                .event(OrderEvent.PROCESS)
                .guard(isEmployee())
                .and()
            .withExternal()
                .source(OrderStatus.PROCESSING)
                .target(OrderStatus.COMPLETED)
                .event(OrderEvent.COMPLETE)
                .guard(isEmployee())
                .and()
            .withExternal()
                .source(OrderStatus.COMPLETED)
                .target(OrderStatus.RECEIVED)
                .event(OrderEvent.RECEIVE)
                .guard(isEmployee())
                .and()
            .withExternal()
                .source(OrderStatus.COMPLETED)
                .target(OrderStatus.DECLINED)
                .event(OrderEvent.DECLINE)
                .guard(isEmployee());
    }

    @Bean
    public Guard<OrderStatus, OrderEvent> isEmployee() {
        return context -> {
            UserIdentity userIdentity = identityService.getUserIdentity();
            return userIdentity != null && userIdentity.getUserType() == UserType.EMPLOYEE;
        };
    }

    @Bean
    public Guard<OrderStatus, OrderEvent> isCustomer() {
        return context -> {
            UserIdentity userIdentity = identityService.getUserIdentity();
            return userIdentity != null && userIdentity.getUserType() == UserType.CUSTOMER;
        };
    }
}
