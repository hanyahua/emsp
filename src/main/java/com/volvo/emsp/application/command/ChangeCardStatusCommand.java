package com.volvo.emsp.application.command;

import jakarta.validation.constraints.NotBlank;

public class ChangeCardStatusCommand {

    @NotBlank(message = "targetStatus is required")
    private String targetStatus;

    private Long assignToAccount;

    public String getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(String targetStatus) {
        this.targetStatus = targetStatus;
    }

    public Long getAssignToAccount() {
        return assignToAccount;
    }

    public void setAssignToAccount(Long assignToAccount) {
        this.assignToAccount = assignToAccount;
    }
}

