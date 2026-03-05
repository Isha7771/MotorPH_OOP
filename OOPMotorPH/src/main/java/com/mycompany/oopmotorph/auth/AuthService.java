/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.auth;

import java.io.IOException;
import java.util.Optional;

public class AuthService {

    private final UserRepository repo;

    public AuthService(UserRepository repo) {
        this.repo = repo;
    }

    public Optional<User> login(String username, String password, Role expectedRole) throws IOException {
        if (username == null || username.trim().isEmpty()) return Optional.empty();
        if (password == null) password = "";

        Optional<User> u = repo.findByUsername(username.trim());
        if (u.isEmpty()) return Optional.empty();

        User user = u.get();
        if (!user.getPassword().equals(password.trim())) return Optional.empty();

        // Role gate: the user must be allowed to open the selected portal.
        // Option B: ADMIN users open the Supervisor portal (SupervisorFrame).
        if (expectedRole != null && !isRoleAllowedForPortal(user.getRole(), expectedRole)) return Optional.empty();

        return Optional.of(user);
    }

    private boolean isRoleAllowedForPortal(Role actual, Role portal) {
        if (portal == null) return true;
        if (actual == null) return false;

        // ADMIN is allowed to open Supervisor portal
        if (portal == Role.SUPERVISOR && actual == Role.ADMIN) return true;

        return actual == portal;
    }
}
