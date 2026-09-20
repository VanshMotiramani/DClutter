package com.declutter.dclutter.repository;

import com.declutter.dclutter.model.AppRole;
import com.declutter.dclutter.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(AppRole roleName);
}