package com.espe.oauth_server.oauth.server.service;

import com.espe.oauth_server.oauth.server.model.Role;
import com.espe.oauth_server.oauth.server.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Transactional
    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("El rol ya existe");
        }
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        role.setDescription(roleDetails.getDescription());
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
