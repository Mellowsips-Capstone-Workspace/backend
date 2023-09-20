package com.capstone.workspace.enums.application;

public enum ApplicationEvent {
    // Initial events
    TO_DRAFT,
    TO_WAITING_FOR_APPROVAL,
    TO_PROCESSING,

    // Real events
    SUBMIT,
    AMEND,
    PROCESS,
    REJECT,
    APPROVE
}
