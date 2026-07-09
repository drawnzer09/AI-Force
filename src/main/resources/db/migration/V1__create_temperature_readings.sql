CREATE TABLE temperature_readings (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    source_id text NOT NULL,
    reading_timestamp timestamptz NOT NULL,
    temperature numeric NOT NULL,
    unit char(1) NOT NULL,
    ingested_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT temperature_readings_source_id_length_chk
        CHECK (char_length(source_id) BETWEEN 1 AND 128),

    CONSTRAINT temperature_readings_source_id_not_blank_chk
        CHECK (btrim(source_id) <> ''),

    CONSTRAINT temperature_readings_unit_chk
        CHECK (unit IN ('C', 'F', 'K')),

    CONSTRAINT temperature_readings_temperature_finite_chk
        CHECK (temperature::text NOT IN ('NaN', 'Infinity', '-Infinity')),

    CONSTRAINT temperature_readings_absolute_zero_chk
        CHECK (
            (unit = 'C' AND temperature >= -273.15)
            OR
            (unit = 'F' AND temperature >= -459.67)
            OR
            (unit = 'K' AND temperature >= 0)
        )
);

CREATE INDEX idx_temperature_readings_timestamp_id
    ON temperature_readings (reading_timestamp, id);

CREATE INDEX idx_temperature_readings_source_timestamp_id
    ON temperature_readings (source_id, reading_timestamp, id);

CREATE INDEX idx_temperature_readings_unit_timestamp_id
    ON temperature_readings (unit, reading_timestamp, id);

CREATE INDEX idx_temperature_readings_source_unit_timestamp_id
    ON temperature_readings (source_id, unit, reading_timestamp, id);
