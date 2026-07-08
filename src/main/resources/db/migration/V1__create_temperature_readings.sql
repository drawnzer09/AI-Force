CREATE TABLE temperature_readings (
    reading_timestamp timestamptz NOT NULL,
    temperature numeric NOT NULL,
    CONSTRAINT temperature_readings_temperature_finite_chk
        CHECK (temperature::text NOT IN ('NaN', 'Infinity', '-Infinity'))
);

CREATE INDEX temperature_readings_timestamp_idx
    ON temperature_readings (reading_timestamp ASC)
    INCLUDE (temperature);
