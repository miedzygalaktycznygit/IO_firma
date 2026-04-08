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

    public Task assignTaskToTailor(Long taskId, Long tailorId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        Employee tailor = employeeRepository.findById(tailorId).orElseThrow();
        task.setTailor(tailor);
        return taskRepository.save(task);
    }

    public Task createTask(Task task, Long designerId, Long tailorId) {
        if (designerId != null) {
            Employee designer = employeeRepository.findById(designerId).orElseThrow();
            task.setDesigner(designer);
        }
        if (tailorId != null) {
            Employee tailor = employeeRepository.findById(tailorId).orElseThrow();
            task.setTailor(tailor);
        }
        task.setStatus("NOWE");
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

    public Task decideOnTask(Long taskId, boolean accept, String reason) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setStatus(accept ? "W_REALIZACJI" : "ODRZUCONO");
        task.setDecisionReason(reason);
        task.setDecisionDate(java.time.LocalDateTime.now());
        return taskRepository.save(task);
    }
}