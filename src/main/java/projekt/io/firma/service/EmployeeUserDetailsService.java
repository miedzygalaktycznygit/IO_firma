package projekt.io.firma.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projekt.io.firma.model.Employee;
import projekt.io.firma.repository.EmployeeRepository;

@Service
public class EmployeeUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public EmployeeUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String roleName = employee.getRole() != null ? employee.getRole().name() : "";

        return User.builder()
                .username(employee.getLogin())
                .password(employee.getPassword())
                .roles(roleName)
                .build();
    }
}
