
CREATE TABLE order_drafts (
    draft_id UUID PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    product_name VARCHAR(150) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    confirmed_order_id UUID UNIQUE,

    CONSTRAINT chk_order_draft_status
        CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED')),

    CONSTRAINT fk_order_draft_confirmed_order
        FOREIGN KEY (confirmed_order_id)
        REFERENCES customer_orders(id)
);
