package com.capstone.workspace.services.application.application_approval;

import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.repositories.application.ApplicationRepository;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@RequiredArgsConstructor
public abstract class BaseApproveApplication {
    @NonNull
    protected ApplicationRepository applicationRepository;

    public abstract void execute(Application application);
}
