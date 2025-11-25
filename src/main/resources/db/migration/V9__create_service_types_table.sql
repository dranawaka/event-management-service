CREATE TABLE service_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_service_types_name ON service_types(name);

-- Insert some default service types
INSERT INTO service_types (name, description) VALUES
    ('Photography', 'Professional event photography services'),
    ('Food Catering', 'Food and beverage catering services'),
    ('Sound System', 'Audio equipment and sound system services'),
    ('Lighting', 'Event lighting and stage lighting services'),
    ('Security', 'Event security and crowd management services'),
    ('Decoration', 'Event decoration and floral arrangements');



