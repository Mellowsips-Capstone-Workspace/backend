ALTER TABLE representative
    ADD CONSTRAINT representative_identity_type_chk CHECK (identity_type IN ('CITIZEN_ID_CARD', 'IDENTITY_CARD', 'PASSPORT'));