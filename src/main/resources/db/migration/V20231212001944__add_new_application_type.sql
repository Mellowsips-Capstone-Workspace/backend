ALTER TABLE application
    DROP CONSTRAINT type_chk,
    ADD CONSTRAINT type_chk CHECK (type IN ('CREATE_ORGANIZATION', 'ADD_STORE'));