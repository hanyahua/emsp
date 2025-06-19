package com.volvo.emsp.domain.model.enums;

public enum CardStatus {

    CREATED,
    ASSIGNED,
    ACTIVATED,
    DEACTIVATED;

    /**
     * state machine
     */
    public boolean canTransitionTo(CardStatus targetStatus) {
        return switch (this) {
            case CREATED -> targetStatus == ASSIGNED;
            case ASSIGNED -> targetStatus == ACTIVATED;
            case ACTIVATED -> targetStatus == DEACTIVATED;
            case DEACTIVATED -> false; // TODO this is a question
        };
    }
}
