package com.capstone.workspace.services.user;

import com.capstone.workspace.entities.user.Permission;
import com.capstone.workspace.repositories.user.PermissionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    @NonNull
    private final PermissionRepository repository;

    public List<Permission> getBulk(List<String> permissionIds) {
        return repository.findAllById(permissionIds);
    }

    public List<Permission> getAll() {
        return repository.findAll();
    }
}
