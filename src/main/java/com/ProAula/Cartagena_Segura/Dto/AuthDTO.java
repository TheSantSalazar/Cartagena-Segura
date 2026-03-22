package com.ProAula.Cartagena_Segura.Dto;

public class AuthDTO {

    // ---- REQUEST: Login ----
    public record LoginRequest(String username, String password) {}

    // ---- REQUEST: Registro ----
    public record RegisterRequest(
            String username,
            String password,
            String email,
            String fullName,
            String phone
    ) {}

    // ---- RESPONSE: Token JWT ----
   public record AuthResponse(
        String token,
        String username,
        String fullName,
        String email,
        String phone,
        java.util.Set<String> roles
) {}
