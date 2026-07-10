CREATE TABLE temperature_records (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recorded_at timestamptz NOT NULL,
    temperature numeric NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT temperature_records_recorded_at_finite_ck
        CHECK (isfinite(recorded_at)),

    CONSTRAINT temperature_records_temperature_not_nan_ck
        CHECK (temperature::text <> 'NaN'),

    CONSTRAINT temperature_records_created_at_finite_ck
        CHECK (isfinite(created_at))
);

CREATE INDEX ix_temperature_records_recorded_at_id
    ON temperature_records (recorded_at ASC, id ASC)
    INCLUDE (temperature);
