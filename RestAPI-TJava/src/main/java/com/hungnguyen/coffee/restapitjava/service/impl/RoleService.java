package com.hungnguyen.coffee.restapitjava.service.impl;

import com.hungnguyen.coffee.restapitjava.model.Role;
import com.hungnguyen.coffee.restapitjava.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public record RoleService(RoleRepository roleRepository) {

    @PostConstruct
    public List<Role> findAll() {
        List<Role> roles = roleRepository.findAll();

        return roles;
    }
}
