package com.capstone.workspace.services.user;

import com.capstone.workspace.dtos.user.CreateReceiverProfileDto;
import com.capstone.workspace.entities.user.ReceiverProfile;
import com.capstone.workspace.entities.user.User;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.NotAcceptableException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.repositories.user.ReceiverProfileRepository;
import com.capstone.workspace.repositories.user.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiverProfileService {
    @NonNull
    private final ReceiverProfileRepository repository;

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final ModelMapper mapper;

    @Transactional
    public ReceiverProfile create(CreateReceiverProfileDto dto, String username) {
        if (username == null) {
            throw new BadRequestException("Missing username");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        if (user.getType() != UserType.CUSTOMER) {
            throw new NotAcceptableException("User type does not allowed to have receiver profile");
        }

        validateNumberOfReceiverProfiles(user);

        ReceiverProfile entity = upsert(null, dto);
        entity.setUser(user);

        if (entity.getIsDefault() != null && entity.getIsDefault()) {
            ReceiverProfile defaultReceiverProfile = getDefaultReceiverProfileOfUser(user);
            if (defaultReceiverProfile != null) {
                defaultReceiverProfile.setIsDefault(false);
                repository.save(defaultReceiverProfile);
            }
        }

        return repository.save(entity);
    }

    private ReceiverProfile upsert(UUID id, Object dto) {
        if (id != null) {
            ReceiverProfile entity = getOneById(id);
            BeanUtils.copyProperties(dto, entity);
            return entity;
        }

        return mapper.map(dto, ReceiverProfile.class);
    }

    public ReceiverProfile getOneById(UUID id) {
        Optional<ReceiverProfile> entity = repository.findById(id);

        if (entity.isEmpty()) {
            throw new NotFoundException("Receiver profile not found");
        }

        return entity.get();
    }

    private void validateNumberOfReceiverProfiles(User user) {
        if (getReceiverProfilesOfUser(user).size() > 5) {
            throw new ConflictException("Number of receivers profile must not be greater than 5");
        }
    }

    private List<ReceiverProfile> getReceiverProfilesOfUser(User user) {
        return repository.findByUser_Id(user.getId());
    }

    private ReceiverProfile getDefaultReceiverProfileOfUser(User user) {
        return repository.findByUser_IdAndIsDefault(user.getId(), true);
    }
}
