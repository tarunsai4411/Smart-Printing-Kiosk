package com.tarun.PrintJobsSpring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tarun.PrintJobsSpring.entity.Admin;
import com.tarun.PrintJobsSpring.repository.AdminRepository;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminDataInitializer.class);

    private final AdminRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String username;

    @Value("${app.admin.password:admin123}")
    private String password;

    public AdminDataInitializer(AdminRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (repository.findByUsername(username).isEmpty()) {
            repository.save(new Admin(username, passwordEncoder.encode(password)));
            logger.info("Created default admin user '{}'. Change APP_ADMIN_PASSWORD before deployment.", username);
        }
    }
}
