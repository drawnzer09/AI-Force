CREATE TABLE temperature_records (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    measurement_timestamp timestamptz NOT NULL,
    temperature numeric NOT NULL,
    ingested_at timestamptz NOT NULL DEFAULT statement_timestamp(),

    CONSTRAINT temperature_records_measurement_timestamp_finite_chk
        CHECK (isfinite(measurement_timestamp)),

    CONSTRAINT temperature_records_temperature_finite_chk
        CHECK (isfinite(temperature)),

    CONSTRAINT temperature_records_ingested_at_finite_chk
        CHECK (isfinite(ingested_at))
);

CREATE INDEX ix_temperature_records_measurement_timestamp_id
    ON temperature_records (measurement_timestamp, id)
    INCLUDE (temperature);
