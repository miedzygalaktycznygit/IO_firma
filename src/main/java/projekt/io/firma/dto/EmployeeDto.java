package projekt.io.firma.dto;

import projekt.io.firma.model.Employee;
import projekt.io.firma.model.Role;

public record EmployeeDto(
        Long id,
        String login,
        String firstName,
        String lastName,
        Role role
) {
    public static EmployeeDto from(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getLogin(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getRole()
        );
    }
}
