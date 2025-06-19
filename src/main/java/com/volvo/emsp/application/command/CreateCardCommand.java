package com.volvo.emsp.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCardCommand {

    @NotBlank(message = "rfidUid is required")
    @Size(max = 100, message = "rfidUid must be between 1 and 100 characters")
    private String rfidUid;

    @NotBlank(message = "visibleNumber is required")
    @Size(max = 100, message = "visibleNumber must be between 1 and 100 characters")
    private String visibleNumber;

    public String getRfidUid() {
        return rfidUid;
    }

    public void setRfidUid(String rfidUid) {
        this.rfidUid = rfidUid;
    }

    public String getVisibleNumber() {
        return visibleNumber;
    }

    public void setVisibleNumber(String visibleNumber) {
        this.visibleNumber = visibleNumber;
    }

    @Override
    public String toString() {
        return "CreateCardCommand{" +
                "rfidUid='" + rfidUid + '\'' +
                ", visibleNumber='" + visibleNumber + '\'' +
                '}';
    }
}
