package com.capstone.workspace.interceptors;

import com.capstone.workspace.helpers.auth.AuthHelper;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.shared.UserPrincipal;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.ArrayList;
import java.util.Map;

public class UserPrincipalInterceptor implements ChannelInterceptor {
    @Override
    public Message preSend(Message message, MessageChannel channel) {
        AuthHelper authHelper = BeanHelper.getBean(AuthHelper.class);
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);

            if (raw instanceof Map) {
                Object authorization = ((Map) raw).get("Authorization");

                if (authorization instanceof ArrayList) {
                    UserIdentity userIdentity = authHelper.parseUserIdentity(((ArrayList<String>) authorization).get(0));
                    accessor.setUser(new UserPrincipal(userIdentity.getUsername()));
                }
            }
        }

        return message;
    }
}
