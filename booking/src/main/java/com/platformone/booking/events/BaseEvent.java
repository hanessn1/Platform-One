package com.platformone.booking.events;

import java.io.Serializable;
import java.time.Instant;

public abstract class BaseEvent implements Serializable {
    protected Instant eventTimestamp = Instant.now();

    public Instant getEventTimestamp() {
        return eventTimestamp;
    }
}