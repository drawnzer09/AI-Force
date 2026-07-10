CREATE TABLE temperature_records (
    temperature_record_id bigint GENERATED ALWAYS AS IDENTITY,
    recorded_at timestamptz NOT NULL,
    temperature_value numeric NOT NULL,

    CONSTRAINT temperature_records_pk
        PRIMARY KEY (temperature_record_id),

    CONSTRAINT temperature_records_recorded_at_finite_chk
        CHECK (isfinite(recorded_at)),

    CONSTRAINT temperature_records_temperature_value_finite_chk
        CHECK (temperature_value::text NOT IN ('NaN', 'Infinity', '-Infinity'))
);

CREATE INDEX idx_temperature_records_recorded_at_id
    ON temperature_records (recorded_at ASC, temperature_record_id ASC)
    INCLUDE (temperature_value);
