package com.capstone.workspace.services.store;

import com.capstone.workspace.dtos.store.QrCodeDto;
import com.capstone.workspace.entities.store.QrCode;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.store.QrCodeModel;
import com.capstone.workspace.repositories.store.QrCodeRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrCodeService {
    @NonNull
    private final QrCodeRepository repository;

    @NonNull
    private final StoreService storeService;

    @NonNull
    private final ModelMapper mapper;
    
    public QrCode create(String storeId, QrCodeDto dto) {
        storeService.getStoreById(UUID.fromString(storeId));

        QrCode entity = new QrCode();
        entity.setName(dto.getName());
        entity.setStoreId(storeId);
        entity.setCode(getCodeSequence());

        return repository.save(entity);
    }

    public List<QrCodeModel> getStoreQrCodes(String storeId) {
        List<QrCode> entities = repository.findAllByStoreId(storeId);
        return mapper.map(
            entities,
            new TypeToken<List<QrCodeModel>>() {}.getType()
        );
    }

    public QrCode getOneById(UUID id) {
        QrCode entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("QR Code not found");
        }

        return entity;
    }

    public QrCode update(UUID id, QrCodeDto dto) {
        QrCode entity = getOneById(id);
        BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
        return repository.save(entity);
    }

    private String getCodeSequence() {
        int number = repository.getCodeSequenceNextValue();
        return String.format("%06d", number);
    }

    public QrCode getOneByCode(String code) {
        QrCode entity = repository.findByCode(code);

        if (entity == null) {
            throw new NotFoundException("QR Code not found");
        }

        return entity;
    }
}
