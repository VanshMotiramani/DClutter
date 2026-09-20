package com.declutter.dclutter.config;

import com.declutter.dclutter.model.AppRole;
import com.declutter.dclutter.model.Role;
import com.declutter.dclutter.model.User;
import com.declutter.dclutter.repository.RoleRepository;
import com.declutter.dclutter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Initialize roles
        Role userRole = initializeRole(AppRole.ROLE_USER);
        Role adminRole = initializeRole(AppRole.ROLE_ADMIN);

        System.out.println("Roles initialized: ROLE_USER, ROLE_ADMIN");
        createOrUpdateAdmin(userRole, adminRole);
    }

    private Role initializeRole(AppRole roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    return roleRepository.save(role);
                });
    }

    private void createOrUpdateAdmin(Role userRole, Role adminRole) {
        User admin = userRepository.findByUsername("admin")
                .orElse(new User());

        // Set admin details
        admin.setUsername("admin");
        admin.setEmail("admin@declutter.com");
        admin.setPassword(passwordEncoder.encode("admin123"));

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        roles.add(userRole);
        admin.setRoles(roles);

        userRepository.save(admin);

        System.out.println("Admin user created:");
        System.out.println("   Username: admin");
        System.out.println("   Password: admin123");
        System.out.println("   Email: admin@declutter.com");
        System.out.println("   Password Hash: " + admin.getPassword());
    }
}