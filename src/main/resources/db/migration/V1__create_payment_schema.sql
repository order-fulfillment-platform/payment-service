CREATE TABLE payments (
    id              UUID            NOT NULL,
    order_id        UUID            NOT NULL,
    customer_id     UUID            NOT NULL,
    amount          NUMERIC(10,2)   NOT NULL,
    status          VARCHAR(50)     NOT NULL,
    created_at      TIMESTAMP       NOT NULL,
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

CREATE TABLE outbox_events (
    id              UUID            NOT NULL,
    aggregate_id    UUID            NOT NULL,
    event_type      VARCHAR(100)    NOT NULL,
    payload         TEXT            NOT NULL,
    created_at      TIMESTAMP       NOT NULL,
    processed       BOOLEAN         NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_outbox_events PRIMARY KEY (id)
);

CREATE INDEX idx_outbox_events_processed
    ON outbox_events(processed)
    WHERE processed = FALSE;

CREATE INDEX idx_payments_order_id
    ON payments(order_id);