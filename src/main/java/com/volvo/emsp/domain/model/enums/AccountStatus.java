package com.volvo.emsp.domain.model.enums;

public enum AccountStatus {
    CREATED,
    ACTIVATED,
    DEACTIVATED;

    public boolean canTransitionTo(AccountStatus target) {
        return switch (this) {
            case CREATED -> (target == AccountStatus.ACTIVATED || target == AccountStatus.DEACTIVATED);
            case ACTIVATED -> target == AccountStatus.DEACTIVATED;
            case DEACTIVATED -> target == AccountStatus.ACTIVATED;
        };
    }
}
