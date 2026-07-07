CREATE TABLE temperature_records (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reading_timestamp timestamptz NOT NULL,
    temperature double precision NOT NULL,
    CONSTRAINT temperature_records_temperature_finite_chk
        CHECK (
            temperature > '-Infinity'::double precision
            AND temperature < 'Infinity'::double precision
        )
);

CREATE INDEX temperature_records_reading_timestamp_id_idx
    ON temperature_records (reading_timestamp, id);
