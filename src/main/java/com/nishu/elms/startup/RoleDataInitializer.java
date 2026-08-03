package com.nishu.elms.startup;

import com.nishu.elms.entity.Department;
import com.nishu.elms.entity.Role;
import com.nishu.elms.entity.User;
import com.nishu.elms.enums.RoleName;
import com.nishu.elms.repository.DepartmentRepository;
import com.nishu.elms.repository.RoleRepository;
import com.nishu.elms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleDataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        createRoleIfNotExists(RoleName.ROLE_ADMIN,
                "System Administrator");

        createRoleIfNotExists(RoleName.ROLE_MANAGER,
                "Department Manager");

        createRoleIfNotExists(RoleName.ROLE_EMPLOYEE,
                "Regular Employee");

        // Create Departments
        Department engineering = createDepartmentIfNotExists("Engineering");
        Department hr = createDepartmentIfNotExists("HR");

        // Create Users
        createUserIfNotExists( "Admin", "User", "admin@gmail.com", "Password@123", "9999999999", RoleName.ROLE_ADMIN, engineering );
        createUserIfNotExists( "Manager", "User", "manager@gmail.com", "Password@123", "8888888888", RoleName.ROLE_MANAGER, engineering );
        createUserIfNotExists( "Employee", "User", "employee@gmail.com", "Password@123", "7777777777", RoleName.ROLE_EMPLOYEE, hr );
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

    private Department createDepartmentIfNotExists(String name) {
        return departmentRepository.findByName(name)
                .orElseGet(() -> departmentRepository.save(
                        Department.builder()
                                .name(name)
                                .build()
                )
                );
    }

    private void createUserIfNotExists( String firstName, String lastName, String email, String password, String phoneNumber, RoleName roleName, Department department ) {
        if (userRepository.existsByEmail(email)) {
            return;
        }
        Role role = roleRepository.findByName(roleName)
                .orElseThrow();
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .enabled(true)
                .department(department)
                .build();
        user.setRoles(Set.of(role));
        userRepository.save(user);
    }
}
