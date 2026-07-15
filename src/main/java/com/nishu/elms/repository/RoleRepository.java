package com.nishu.elms.repository;

import com.nishu.elms.entity.Role;
import com.nishu.elms.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(RoleName name);
}
