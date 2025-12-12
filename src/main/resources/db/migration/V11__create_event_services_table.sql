CREATE TABLE event_services (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID NOT NULL,
    service_type_id UUID NOT NULL,
    vendor_id UUID NOT NULL,
    rate DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_services_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_event_services_service_type FOREIGN KEY (service_type_id) REFERENCES service_types(id) ON DELETE RESTRICT,
    CONSTRAINT fk_event_services_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE RESTRICT
);

CREATE INDEX idx_event_services_event ON event_services(event_id);
CREATE INDEX idx_event_services_service_type ON event_services(service_type_id);
CREATE INDEX idx_event_services_vendor ON event_services(vendor_id);






