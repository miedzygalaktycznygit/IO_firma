package projekt.io.firma.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projekt.io.firma.model.Employee;
import projekt.io.firma.repository.EmployeeRepository;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Employee authenticate(String login, String password) {
        Employee employee = employeeRepository.findByLogin(login).orElse(null);
        if (employee == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, employee.getPassword())) {
            return null;
        }
        return employee;
    }
}
