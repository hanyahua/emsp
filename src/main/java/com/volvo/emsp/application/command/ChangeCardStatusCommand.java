package com.volvo.emsp.application.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class ChangeCardStatusCommand {

    @Schema(
            examples = "ASSIGNED",
            description = "The status to change the card to. Valid values are ASSIGNED, ACTIVATED, DEACTIVATED"
    )
    @NotBlank(message = "targetStatus is required")
    private String targetStatus;

    @Schema(
            examples = "1234567890", description = "Account ID to assign the card to, nonnull when targetStatus is ASSIGNED"
    )
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

