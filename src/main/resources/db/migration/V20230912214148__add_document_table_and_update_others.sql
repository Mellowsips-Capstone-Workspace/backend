CREATE TABLE IF NOT EXISTS document
(
    id             uuid         NOT NULL,
    name           VARCHAR(255) NOT NULL,
    content        bytea        NOT NULL,
    file_type      VARCHAR(255) NOT NULL,
    size           bigint       NOT NULL,
    reference      uuid,
    reference_type VARCHAR(255),
    created_by     VARCHAR(255),
    created_at     TIMESTAMP(6) NOT NULL,
    updated_by     VARCHAR(255),
    updated_at     TIMESTAMP(6) NOT NULL,
    is_deleted     BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

ALTER TABLE "user" ALTER COLUMN type SET NOT NULL;
ALTER TABLE "user" ALTER COLUMN provider SET NOT NULL;

ALTER TABLE application ALTER COLUMN type SET NOT NULL;
ALTER TABLE application ALTER COLUMN status SET NOT NULL;
ALTER TABLE application ALTER COLUMN json_data SET NOT NULL;