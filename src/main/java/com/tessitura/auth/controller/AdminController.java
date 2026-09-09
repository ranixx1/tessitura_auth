package com.tessitura.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tessitura.auth.dto.admin.UpdateRoleRequest;
import com.tessitura.auth.dto.admin.UserDetailResponse;
import com.tessitura.auth.dto.admin.UserSummaryResponse;
import com.tessitura.auth.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryResponse>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDetailResponse> detalharUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.detalharUsuario(id));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Void> alterarRole(
            @PathVariable Long id,
            @RequestBody UpdateRoleRequest request) {
        adminService.alterarRole(id, request.getRole());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/active")
    public ResponseEntity<Void> toggleAtivo(
            @PathVariable Long id,
            @RequestParam boolean active) {
        adminService.toggleAtivo(id, active);
        return ResponseEntity.noContent().build();
    }
}