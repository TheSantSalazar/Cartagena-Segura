package com.ProAula.Cartagena_Segura.Service;
import com.ProAula.Cartagena_Segura.Dto.AuthDTO.*;
import com.ProAula.Cartagena_Segura.Model.Role;
import com.ProAula.Cartagena_Segura.Model.User;
import com.ProAula.Cartagena_Segura.Repository.RoleRepository;
import com.ProAula.Cartagena_Segura.Repository.UserRepository;
import com.ProAula.Cartagena_Segura.Security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final LogService logService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       LogService logService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.logService = logService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El username ya está en uso");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER", "Usuario estándar")));
        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.email(),
                request.fullName(),
                request.phone()
        );
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        logService.log("REGISTER", request.username(), "Nuevo usuario registrado", "User", null);
        String token = jwtUtil.generateToken(user);
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        return new AuthResponse(token, user.getUsername(), user.getFullName(), user.getEmail(), user.getPhone(), roles);
    }

    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        User user = userRepository.findByUsername(request.username())
                .or(() -> userRepository.findByEmail(request.username()))
                .or(() -> userRepository.findByPhone(request.username()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        logService.logFull("LOGIN", request.username(), "Inicio de sesión exitoso",
                ipAddress, userAgent, "User", null);
        String token = jwtUtil.generateToken(user);
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        return new AuthResponse(token, user.getUsername(), user.getFullName(), user.getEmail(), user.getPhone(), roles);
    }
}
