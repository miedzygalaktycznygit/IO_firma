package projekt.io.firma.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Set;
import projekt.io.firma.dto.CreateEmployeeRequest;
import projekt.io.firma.model.Employee;
import projekt.io.firma.model.Role;
import projekt.io.firma.model.Task;
import projekt.io.firma.repository.EmployeeRepository;
import projekt.io.firma.repository.TaskRepository;

@Service
public class TaskManagementService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public TaskManagementService(TaskRepository taskRepository, EmployeeRepository employeeRepository,
                                 PasswordEncoder passwordEncoder) {
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Employee createEmployeeAccount(CreateEmployeeRequest request) {
        Employee employee = new Employee();
        employee.setLogin(request.login());
        employee.setPassword(passwordEncoder.encode(request.password()));
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setRole(request.role());
        return employeeRepository.save(employee);
    }

    public Task assignTaskToTailor(Long taskId, Long tailorId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        Employee tailor = employeeRepository.findById(tailorId).orElseThrow();
        task.setTailor(tailor);
        return taskRepository.save(task);
    }

    public Task createTask(Task task, String login) {
        if (login != null) {
            employeeRepository.findByLogin(login).ifPresent(employee -> {
                if (employee.getRole() == Role.PROJEKTANT) {
                    task.setDesigner(employee);
                }
            });
        }
        task.updateState();
        return taskRepository.save(task);
    }

    public List<Task> getTasksForUser(String login, Set<String> roles) {
        if (roles.contains("ROLE_ADMINISTRATOR")) {
            return taskRepository.findAll();
        }
        if (roles.contains("ROLE_PROJEKTANT")) {
            return taskRepository.findByDesigner_Login(login);
        }
        if (roles.contains("ROLE_KRAWIEC")) {
            return taskRepository.findByTailor_Login(login);
        }
        return List.of();
    }

    public Task updateTask(Long taskId, Task updatedTaskDetails) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setTitle(updatedTaskDetails.getTitle());
        task.setDescription(updatedTaskDetails.getDescription());
        return taskRepository.save(task);
    }
    
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public Task acceptTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.accept();
        return taskRepository.save(task);
    }

    public Task completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.complete();
        return taskRepository.save(task);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

}