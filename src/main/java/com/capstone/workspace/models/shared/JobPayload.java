package com.capstone.workspace.models.shared;

import com.capstone.workspace.models.auth.UserIdentity;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JobPayload {
    private UserIdentity userIdentity;
    private Object data;
}
