package com.nishu.elms.startup;

import com.nishu.elms.entity.Role;
import com.nishu.elms.enums.RoleName;
import com.nishu.elms.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleDataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    @Override
    public void run(String... args) throws Exception {

        createRoleIfNotExists(RoleName.ROLE_ADMIN,
                "System Administrator");

        createRoleIfNotExists(RoleName.ROLE_MANAGER,
                "Department Manager");

        createRoleIfNotExists(RoleName.ROLE_EMPLOYEE,
                "Regular Employee");
    }

    private void createRoleIfNotExists(RoleName roleName,String description){
        if(roleRepository.findByName(roleName).isEmpty()){
            Role role = Role.builder().
                    name(roleName)
                    .description(description)
                    .build();
            roleRepository.save(role);
        }
    }
}
