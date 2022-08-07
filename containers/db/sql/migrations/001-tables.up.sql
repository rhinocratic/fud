create schema IF NOT EXISTS fud;

grant all privileges on database fud to fud;
alter role fud set search_path = fud,public;

ALTER DATABASE fud OWNER TO fud;
ALTER SCHEMA fud OWNER TO fud;

DROP TABLE IF EXISTS fud.supplier CASCADE;
CREATE TABLE fud.supplier (
    supplier_id INT GENERATED ALWAYS AS IDENTITY,
    supplier_name TEXT NOT NULL,
    website TEXT,
    email TEXT,
    telephone TEXT,
    address TEXT,
    notes TEXT,
    PRIMARY KEY(supplier_id),
    UNIQUE(supplier_name)
);
ALTER TABLE fud.supplier OWNER TO fud;

DROP TABLE IF EXISTS fud.brand CASCADE;
CREATE TABLE fud.brand (
    brand_id INT GENERATED ALWAYS AS IDENTITY,
    brand_name TEXT NOT NULL,
    notes TEXT,
    PRIMARY KEY(brand_id),
    UNIQUE(brand_name)
);
ALTER TABLE fud.brand OWNER TO fud;

DROP TABLE IF EXISTS fud.fud_category CASCADE;
CREATE TABLE fud.fud_category (
    fud_category_id INT GENERATED ALWAYS AS IDENTITY,
    fud_category_name TEXT NOT NULL,
    notes TEXT,
    PRIMARY KEY(fud_category_id),
    UNIQUE(fud_category_name)
);
ALTER TABLE fud.fud_category OWNER TO fud;

DROP TABLE IF EXISTS fud.unit CASCADE;
CREATE TABLE fud.unit (
    unit_id INT GENERATED ALWAYS AS IDENTITY,
    unit_name TEXT NOT NULL,
    PRIMARY KEY(unit_id),
    UNIQUE(unit_name)
);
ALTER TABLE fud.unit OWNER TO fud;

DROP TABLE IF EXISTS fud.fud_type CASCADE;
CREATE TABLE fud.fud_type (
    fud_type_id INT GENERATED ALWAYS AS IDENTITY,
    fud_type_name TEXT NOT NULL,
    low_stock_qty INT NULL,
    low_stock_unit_id INT NULL,
    notes TEXT,
    PRIMARY KEY (fud_type_id),
    CONSTRAINT fk_low_stock_unit FOREIGN KEY(low_stock_unit_id) REFERENCES fud.unit(unit_id) ON DELETE CASCADE,
    CONSTRAINT if_low_stock_qty_then_low_stock_unit
        CHECK (((low_stock_qty IS NOT NULL) AND (low_stock_unit_id IS NOT NULL)
                OR (low_stock_qty IS NULL) AND (low_stock_unit_id IS NULL)))
);
ALTER TABLE fud.fud_type OWNER TO fud;

DROP TABLE IF EXISTS fud.fud_item CASCADE;
CREATE TABLE fud.fud_item (
    fud_item_id INT GENERATED ALWAYS AS IDENTITY,
    fud_type_id INT NOT NULL,
    brand_id INT NOT NULL,
    unit_qty INT NOT NULL,
    unit_id  INT NOT NULL, 
    notes TEXT,
    PRIMARY KEY(fud_item_id),
    UNIQUE(fud_type_id, brand_id),
    CONSTRAINT fk_fud_item_fud_type FOREIGN KEY(fud_type_id) REFERENCES fud.fud_type(fud_type_id) ON DELETE CASCADE,
    CONSTRAINT fk_fud_item_brand FOREIGN KEY(brand_id) REFERENCES fud.brand(brand_id) ON DELETE CASCADE,
    CONSTRAINT fk_fud_item_unit_id FOREIGN KEY(unit_id) REFERENCES fud.unit(unit_id) ON DELETE CASCADE
);
ALTER TABLE fud.fud_item OWNER TO fud;
CREATE INDEX idx_fud_item_type ON fud.fud_item(fud_type_id);
CREATE INDEX idx_fud_item_brand_id ON fud.fud_item(brand_id);

DROP TABLE IF EXISTS fud.fud_item_supplier CASCADE;
CREATE TABLE fud.fud_item_supplier (
    fud_item_id INT NOT NULL,
    supplier_id INT NOT NULL,
    UNIQUE (fud_item_id, supplier_id),
    CONSTRAINT fk_fud_item_supplier_item FOREIGN KEY(fud_item_id) REFERENCES fud.fud_item(fud_item_id) ON DELETE CASCADE,
    CONSTRAINT fk_fud_item_supplier_supplier FOREIGN KEY(supplier_id) REFERENCES fud.supplier(supplier_id) ON DELETE CASCADE
);
ALTER TABLE fud.fud_item_supplier OWNER TO fud;
CREATE INDEX idx_fud_item_supplier_item ON fud.fud_item_supplier(fud_item_id);
CREATE INDEX idx_fud_item_supplier_supplier ON fud.fud_item_supplier(supplier_id);

DROP TABLE IF EXISTS fud.fud_item_category CASCADE;
CREATE TABLE fud.fud_item_category (
    fud_item_id INT NOT NULL,
    fud_category_id INT NOT NULL,
    UNIQUE(fud_item_id, fud_category_id),
    CONSTRAINT fk_fud_item FOREIGN KEY(fud_item_id) REFERENCES fud.fud_item(fud_item_id) ON DELETE CASCADE,
    CONSTRAINT fk_category FOREIGN KEY(fud_category_id) REFERENCES fud.fud_category(fud_category_id) ON DELETE CASCADE
);
ALTER TABLE fud.fud_item_category OWNER TO fud;
CREATE INDEX idx_fud_item_category_fud_item_id ON fud.fud_item_category(fud_item_id);
CREATE INDEX idx_fud_item_category_fud_category_id ON fud.fud_item_category(fud_category_id);

DROP TABLE IF EXISTS fud.inventory_item CASCADE;
CREATE TABLE fud.inventory_item (
    inventory_item_id INT GENERATED ALWAYS AS IDENTITY,
    fud_item_id INT NOT NULL,
    expiry_day INT,
    expiry_month INT NOT NULL,
    expiry_year INT NOT NULL,
    date_expiry DATE NOT NULL,
    date_added TIMESTAMP NOT NULL,
    date_used TIMESTAMP NULL,
    PRIMARY KEY(inventory_item_id),
    CONSTRAINT fk_fud_item FOREIGN KEY(fud_item_id) REFERENCES fud.fud_item(fud_item_id) ON DELETE CASCADE
);
ALTER TABLE fud.inventory_item OWNER TO fud;
CREATE INDEX idx_fud_inventory_item_fud_item_id ON fud.inventory_item(fud_item_id);
CREATE INDEX idx_fud_inventory_item_date_expiry ON fud.inventory_item(date_expiry);