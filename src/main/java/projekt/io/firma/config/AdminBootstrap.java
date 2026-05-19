package projekt.io.firma.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import projekt.io.firma.model.Employee;
import projekt.io.firma.model.Role;
import projekt.io.firma.repository.EmployeeRepository;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.login:admin}")
    private String defaultLogin;

    @Value("${app.admin.password:admin}")
    private String defaultPassword;

    @Value("${app.admin.first-name:Admin}")
    private String defaultFirstName;

    @Value("${app.admin.last-name:Root}")
    private String defaultLastName;

    public AdminBootstrap(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (employeeRepository.count() > 0) {
            return;
        }

        Employee admin = new Employee();
        admin.setLogin(defaultLogin);
        admin.setPassword(passwordEncoder.encode(defaultPassword));
        admin.setFirstName(defaultFirstName);
        admin.setLastName(defaultLastName);
        admin.setRole(Role.ADMINISTRATOR);
        employeeRepository.save(admin);
    }
}
