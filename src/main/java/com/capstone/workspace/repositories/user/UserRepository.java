package com.capstone.workspace.repositories.user;

import com.capstone.workspace.entities.user.User;
import com.capstone.workspace.enums.user.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);

    List<User> findByPartnerIdAndType(String partnerId, UserType type);

    List<User> findByStoreId(String storeId);
}