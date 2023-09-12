CREATE TYPE application_status AS ENUM ('DRAFT', 'WAITING_FOR_APPROVAL', 'PROCESSING', 'APPROVED', 'REJECTED');
CREATE CAST (CHARACTER VARYING AS application_status) WITH INOUT AS ASSIGNMENT;

CREATE TYPE application_type AS ENUM ('CREATE_ORGANIZATION');
CREATE CAST (CHARACTER VARYING AS application_type) WITH INOUT AS ASSIGNMENT;

CREATE TABLE IF NOT EXISTS application
(
    id          uuid         NOT NULL,
    type        application_type,
    status      application_status,
    json_data   jsonb,
    approved_by VARCHAR(255),
    approved_at TIMESTAMP(6) NOT NULL,
    created_by  VARCHAR(255),
    created_at  TIMESTAMP(6) NOT NULL,
    updated_by  VARCHAR(255),
    updated_at  TIMESTAMP(6) NOT NULL,
    is_deleted  BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);