package com.capstone.workspace.services.document;

import com.capstone.workspace.dtos.document.UpdateDocumentDto;
import com.capstone.workspace.entities.document.Document;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.repositories.document.DocumentRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {
    public static final long MAX_FILE_SIZE = 1024 * 1024 * 10L;

    @NonNull
    private final DocumentRepository repository;

    public Document uploadDocument(MultipartFile file) throws IOException {
        if (file == null) {
            throw new BadRequestException("Missing file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Max upload file size is 10 MB");
        }

        Document entity = Document.builder()
                .name(StringUtils.cleanPath(file.getOriginalFilename()))
                .content(file.getBytes())
                .size(file.getSize())
                .fileType(file.getContentType())
                .build();

        return repository.save(entity);
    }

    public Document getDocumentById(UUID id) {
        Document entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Document not found");
        }

        return entity;
    }

    public Document updateDocument(UUID id, UpdateDocumentDto dto) {
        Document entity = getDocumentById(id);
        BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
        return repository.save(entity);
    }
}
