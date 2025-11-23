CREATE TABLE payouts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizer_id UUID NOT NULL,
    event_id UUID,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    transaction_reference VARCHAR(255),
    notes TEXT,
    processed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payouts_organizer FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_payouts_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE SET NULL
);

CREATE INDEX idx_payouts_organizer ON payouts(organizer_id);
CREATE INDEX idx_payouts_event ON payouts(event_id);
CREATE INDEX idx_payouts_status ON payouts(status);
CREATE INDEX idx_payouts_created_at ON payouts(created_at);

