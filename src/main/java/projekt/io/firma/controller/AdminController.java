package projekt.io.firma.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projekt.io.firma.dto.CreateEmployeeRequest;
import projekt.io.firma.dto.EmployeeDto;
import projekt.io.firma.model.Employee;
import projekt.io.firma.service.TaskManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final TaskManagementService service;

    public AdminController(TaskManagementService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping("/employees")
    public EmployeeDto createEmployee(@RequestBody CreateEmployeeRequest request) {
        Employee employee = service.createEmployeeAccount(request);
        return EmployeeDto.from(employee);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/employees")
    public List<EmployeeDto> getEmployees() {
        return service.getAllEmployees().stream().map(EmployeeDto::from).toList();
    }
}
