package com.capstone.workspace.models;

import lombok.Data;

import java.util.UUID;

@Data
public class DocumentModel extends BaseModel {
    private String name;

    private byte[] content;

    private String fileType;

    private long size;

    private UUID reference;

    private String referenceType;
}
