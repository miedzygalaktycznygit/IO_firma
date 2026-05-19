package projekt.io.firma.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import projekt.io.firma.dto.EmployeeDto;
import projekt.io.firma.dto.LoginRequest;
import projekt.io.firma.model.Employee;
import projekt.io.firma.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public EmployeeDto login(@RequestBody LoginRequest request) {
        Employee employee = authService.authenticate(request.login(), request.password());
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return EmployeeDto.from(employee);
    }
}
