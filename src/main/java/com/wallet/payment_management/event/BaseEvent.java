package com.wallet.payment_management.event;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all domain events.
 * Provides common properties for event tracking and auditing.
 */
@Getter
public abstract class BaseEvent {

    private final String eventId;
    private final LocalDateTime timestamp;
    private final String eventType;

    protected BaseEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.eventType = this.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return String.format("%s[eventId=%s, timestamp=%s]",
                eventType, eventId, timestamp);
    }
}
