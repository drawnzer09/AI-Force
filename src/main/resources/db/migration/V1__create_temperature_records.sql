CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE temperature_records (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    "timestamp" timestamp with time zone NOT NULL,
    temperature numeric NOT NULL,
    CONSTRAINT temperature_records_temperature_finite_chk
        CHECK (temperature <> 'NaN'::numeric)
);

CREATE INDEX idx_temperature_records_timestamp
    ON temperature_records ("timestamp")
    INCLUDE (id, temperature);
