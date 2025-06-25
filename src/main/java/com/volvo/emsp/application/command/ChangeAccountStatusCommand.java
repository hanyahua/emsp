package com.volvo.emsp.application.command;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Update Account Status Command")
public class ChangeAccountStatusCommand {

    @Schema(description = "New status for the account",
            example = "ACTIVATED",
            allowableValues = {"ACTIVATED", "DEACTIVATED"}
    )
    private String targetStatus;

    public String getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(String targetStatus) {
        this.targetStatus = targetStatus;
    }
}

