package projekt.io.firma.dto;

import projekt.io.firma.model.Role;

public record CreateEmployeeRequest(
        String login,
        String password,
        String firstName,
        String lastName,
        Role role
) {
}
