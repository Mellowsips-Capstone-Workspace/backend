package com.capstone.workspace.dtos.document;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class UpdateDocumentDto {
    private UUID reference;
    private String referenceType;
}
