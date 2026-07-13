CREATE TABLE temperature_records (
    temperature_record_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recorded_at timestamptz NOT NULL,
    temperature numeric NOT NULL,
    CONSTRAINT chk_temperature_records_temperature_finite
        CHECK (temperature <> 'NaN'::numeric)
);

CREATE INDEX idx_temperature_records_recorded_at_id
    ON temperature_records (recorded_at, temperature_record_id);
