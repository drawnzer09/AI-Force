CREATE TABLE temperature_data_points (
    temperature_data_point_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    "timestamp" timestamptz NOT NULL,
    temperature numeric NOT NULL
);

CREATE INDEX idx_temperature_data_points_timestamp
    ON temperature_data_points ("timestamp" ASC);
