CREATE TABLE temperature_measurements (
    temperature_measurement_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    measurement_timestamp TIMESTAMPTZ NOT NULL,
    temperature NUMERIC NOT NULL,
    CONSTRAINT temperature_measurements_temperature_finite_chk
        CHECK (temperature <> 'NaN'::NUMERIC)
);

CREATE INDEX idx_temperature_measurements_timestamp_id
    ON temperature_measurements (measurement_timestamp ASC, temperature_measurement_id ASC);
