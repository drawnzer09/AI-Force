CREATE TABLE temperature_readings (
    reading_timestamp TIMESTAMPTZ NOT NULL,
    temperature_value NUMERIC NOT NULL
);

CREATE INDEX idx_temperature_readings_timestamp
    ON temperature_readings (reading_timestamp)
    INCLUDE (temperature_value);
