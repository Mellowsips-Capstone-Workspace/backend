package com.capstone.workspace.services.user;

import com.capstone.workspace.dtos.user.CreateRoleDto;
import com.capstone.workspace.entities.user.Permission;
import com.capstone.workspace.entities.user.Role;
import com.capstone.workspace.entities.user.User;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.repositories.user.RoleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {
    @NonNull
    private final RoleRepository repository;

    @NonNull
    private final PermissionService permissionService;

    @NonNull
    private final UserService userService;

    public Role create(CreateRoleDto dto) {
        Role entity = new Role();
        BeanUtils.copyProperties(dto, entity, "permissions");

        List<String> permissionIds = dto.getPermissions();
        List<Permission> permissions = permissionService.getBulk(permissionIds);
        entity.setPermissions(permissions);

        return repository.save(entity);
    }

    public void assignUserToRole(UUID roleId, String username) {
        Role role = getRoleById(roleId);
        User user = userService.getUserByUsername(username);

        List<User> roleUsers = role.getUsers();
        if (roleUsers == null) {
            roleUsers = new ArrayList<>();
        }

        roleUsers.add(user);
        role.setUsers(roleUsers);
        repository.save(role);
    }

    public Role getRoleById(UUID id) {
        Role entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Role not found");
        }

        return entity;
    }
}
