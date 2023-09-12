package com.capstone.workspace.controllers;

import com.capstone.workspace.entities.document.Document;
import com.capstone.workspace.models.document.DocumentModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.services.document.DocumentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping( "/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    @NonNull
    private final DocumentService documentService;

    @NonNull
    private final ModelMapper mapper;

    @PostMapping
    public ResponseModel<DocumentModel> create(MultipartFile file) throws IOException {
        Document entity = documentService.uploadDocument(file);
        DocumentModel model = mapper.map(entity, DocumentModel.class);
        return ResponseModel.<DocumentModel>builder().data(model).build();
    }

    @GetMapping("/{id}")
    public ResponseModel<DocumentModel> getDocument(@PathVariable UUID id) {
        Document entity = documentService.getDocumentById(id);
        DocumentModel model = mapper.map(entity, DocumentModel.class);
        return ResponseModel.<DocumentModel>builder().data(model).build();
    }
}
