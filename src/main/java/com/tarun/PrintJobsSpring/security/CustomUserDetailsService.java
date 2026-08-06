package com.tarun.PrintJobsSpring.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tarun.PrintJobsSpring.entity.Admin;
import com.tarun.PrintJobsSpring.repository.AdminRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository repository;

    public CustomUserDetailsService(AdminRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = repository.findByUsername(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
        return new CustomUserDetails(admin);
    }
}
