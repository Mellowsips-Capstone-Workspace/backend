package com.capstone.workspace.models.shared;

import lombok.AllArgsConstructor;

import java.security.Principal;

@AllArgsConstructor
public class UserPrincipal implements Principal {
    private String name;

    @Override
    public String getName() {
        return name;
    }
}
