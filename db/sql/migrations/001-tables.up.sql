create schema IF NOT EXISTS fud;

grant all privileges on database fud to fud;
alter role fud set search_path = fud,public;

ALTER DATABASE fud OWNER TO fud;
ALTER SCHEMA fud OWNER TO fud;

DROP TABLE IF EXISTS fud.supplier;
CREATE TABLE fud.supplier (
    supplier_id INT GENERATED ALWAYS AS IDENTITY,
    supplier_name TEXT NOT NULL,
    website TEXT,
    email TEXT,
    telephone TEXT,
    address TEXT,
    notes TEXT,
    PRIMARY KEY(supplier_id)
);
ALTER TABLE fud.supplier OWNER TO fud;

DROP TABLE IF EXISTS fud.brand;
CREATE TABLE fud.brand (
    brand_id INT GENERATED ALWAYS AS IDENTITY,
    brand_name TEXT NOT NULL,
    notes TEXT,
    PRIMARY KEY(brand_id)
);
ALTER TABLE fud.brand OWNER TO fud;

DROP TABLE IF EXISTS fud.fud_category;
CREATE TABLE fud.fud_category (
    fud_category_id INT GENERATED ALWAYS AS IDENTITY,
    fud_category_name TEXT NOT NULL,
    notes TEXT,
    PRIMARY KEY(fud_category_id)
);
ALTER TABLE fud.fud_category OWNER TO fud;

DROP TABLE IF EXISTS fud.fud_item;
CREATE TABLE fud.fud_item (
    fud_item_id INT GENERATED ALWAYS AS IDENTITY,
    fud_item_name TEXT NOT NULL,
    notes TEXT,
    brand_id INT NOT NULL,
    supplier_id INT NOT NULL,
    PRIMARY KEY(fud_item_id),
    CONSTRAINT fk_brand FOREIGN KEY(brand_id) REFERENCES fud.brand(brand_id) ON DELETE CASCADE,
    CONSTRAINT fk_supplier FOREIGN KEY(supplier_id) REFERENCES fud.supplier(supplier_id) ON DELETE CASCADE
);
ALTER TABLE fud.fud_item OWNER TO fud;
CREATE INDEX idx_fud_item_name ON fud.fud_item(fud_item_name);
CREATE INDEX idx_fud_item_brand_id ON fud.fud_item(brand_id);
CREATE INDEX idx_fud_item_supplier_id ON fud.fud_item(supplier_id);

DROP TABLE IF EXISTS fud.fud_item_category;
CREATE TABLE fud.fud_item_category (
    fud_item_id INT NOT NULL,
    fud_category_id INT NOT NULL,
    CONSTRAINT fk_fud_item FOREIGN KEY(fud_item_id) REFERENCES fud.fud_item(fud_item_id) ON DELETE CASCADE,
    CONSTRAINT fk_category FOREIGN KEY(fud_category_id) REFERENCES fud.fud_category(fud_category_id) ON DELETE CASCADE
);
ALTER TABLE fud.fud_item_category OWNER TO fud;
CREATE INDEX idx_fud_item_category_fud_item_id ON fud.fud_item_category(fud_item_id);
CREATE INDEX idx_fud_item_category_fud_category_id ON fud.fud_item_category(fud_category_id);

DROP TABLE IF EXISTS fud.inventory_item;
CREATE TABLE fud.inventory_item (
    inventory_item_id INT GENERATED ALWAYS AS IDENTITY,
    fud_item_id INT NOT NULL,
    expiry_day INT,
    expiry_month INT NOT NULL,
    expiry_year INT NOT NULL,
    date_expiry DATE NOT NULL,
    date_added TIMESTAMP NOT NULL,
    PRIMARY KEY(inventory_item_id),
    CONSTRAINT fk_fud_item FOREIGN KEY(fud_item_id) REFERENCES fud.fud_item(fud_item_id) ON DELETE CASCADE
);
ALTER TABLE fud.inventory_item OWNER TO fud;
CREATE INDEX idx_fud_inventory_item_fud_item_id ON fud.inventory_item(fud_item_id);
CREATE INDEX idx_fud_inventory_item_date_expiry ON fud.inventory_item(date_expiry);
