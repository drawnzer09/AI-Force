CREATE TABLE temperature_data_points (
    temperature_data_point_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recorded_at timestamptz NOT NULL,
    temperature numeric NOT NULL,
    CONSTRAINT temperature_data_points_temperature_finite_chk
        CHECK (temperature::text NOT IN ('NaN', 'Infinity', '-Infinity'))
);

CREATE INDEX temperature_data_points_recorded_at_id_idx
    ON temperature_data_points (recorded_at ASC, temperature_data_point_id ASC);
