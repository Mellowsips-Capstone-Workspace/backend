package com.capstone.workspace.services.application.application_approval;

import com.capstone.workspace.entities.application.Application;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class BaseApproveApplication {
    public abstract void execute(Application application);
}
