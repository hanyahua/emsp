package com.volvo.emsp.application.command;

public class ChangeAccountStatusCommand {

    private String targetStatus;

    public String getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(String targetStatus) {
        this.targetStatus = targetStatus;
    }
}

