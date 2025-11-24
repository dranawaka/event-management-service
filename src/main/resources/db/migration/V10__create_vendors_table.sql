CREATE TABLE vendors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(50) NOT NULL,
    service_type_id UUID NOT NULL,
    base_rate DECIMAL(10, 2),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vendors_service_type FOREIGN KEY (service_type_id) REFERENCES service_types(id) ON DELETE RESTRICT
);

CREATE INDEX idx_vendors_service_type ON vendors(service_type_id);
CREATE INDEX idx_vendors_active ON vendors(is_active);


