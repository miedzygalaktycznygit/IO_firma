package projekt.io.firma.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import projekt.io.firma.dto.AuthResponse;
import projekt.io.firma.dto.EmployeeDto;
import projekt.io.firma.dto.LoginRequest;
import projekt.io.firma.model.Employee;
import projekt.io.firma.security.JwtService;
import projekt.io.firma.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        Employee employee = authService.authenticate(request.login(), request.password());
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        String token = jwtService.generateToken(employee);
        return new AuthResponse(EmployeeDto.from(employee), token);
    }
}
