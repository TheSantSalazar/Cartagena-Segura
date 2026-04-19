package Com.Backend.CartagenaSegura.Service;
import Com.Backend.CartagenaSegura.Dto.AuthDto.*;
import Com.Backend.CartagenaSegura.Model.Role;
import Com.Backend.CartagenaSegura.Model.User;
import Com.Backend.CartagenaSegura.Repository.RoleRepository;
import Com.Backend.CartagenaSegura.Repository.UserRepository;
import Com.Backend.CartagenaSegura.Security.JwtUtil;
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
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       LogService logService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.logService = logService;
        this.emailService = emailService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El username ya esta en uso");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya esta en uso");
        }
        if (request.phone() != null && !request.phone().isBlank() &&
                userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("El telefono ya esta en uso");
        }
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER", "Usuario estÃƒÂ¡ndar")));
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
        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername(), user.getFullName());
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
        logService.logFull("LOGIN", request.username(), "Inicio de sesiÃƒÂ³n exitoso",
                ipAddress, userAgent, "User", null);
        String token = jwtUtil.generateToken(user);
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        return new AuthResponse(token, user.getUsername(), user.getFullName(), user.getEmail(), user.getPhone(), roles);
    }
}
