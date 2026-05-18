package projekt.io.firma.service;

import org.springframework.stereotype.Service;
import java.util.List;
import projekt.io.firma.model.Employee;
import projekt.io.firma.model.Task;
import projekt.io.firma.repository.EmployeeRepository;
import projekt.io.firma.repository.TaskRepository;

@Service
public class TaskManagementService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    public TaskManagementService(TaskRepository taskRepository, EmployeeRepository employeeRepository) {
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
    }

    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Task assignTaskToTailor(Long taskId, Long tailorId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        Employee tailor = employeeRepository.findById(tailorId).orElseThrow();
        task.setTailor(tailor);
        return taskRepository.save(task);
    }

    public Task createTask(Task task) {
        task.updateState();
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
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