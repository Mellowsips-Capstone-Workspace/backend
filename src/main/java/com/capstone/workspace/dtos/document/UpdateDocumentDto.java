package com.capstone.workspace.dtos.document;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateDocumentDto {
    private String reference;
    private String referenceType;
}
