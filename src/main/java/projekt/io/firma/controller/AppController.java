package projekt.io.firma.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import projekt.io.firma.model.Task;
import projekt.io.firma.service.TaskManagementService;
import projekt.io.firma.model.Employee;


@RestController
@RequestMapping("/api")
public class AppController {

    private final TaskManagementService service;

    public AppController(TaskManagementService service) {
        this.service = service;
    }

    @GetMapping("/tasks")
    public List<Task> getTasks() {
        return service.getAllTasks();
    }

    @PostMapping("/tasks")
    public Task createTask(@RequestBody Task task) {
        return service.createTask(task);
    }

    @PutMapping("/tasks/{taskId}/accept")
    public Task acceptTask(@PathVariable Long taskId) {
        return service.acceptTask(taskId);
    }

    @PostMapping("/employees")
    public Employee addEmployee(@RequestBody Employee employee) {
        return service.addEmployee(employee);
    }

    @PutMapping("/tasks/{taskId}/assign/{tailorId}")
    public Task assignTaskToTailor(@PathVariable Long taskId, @PathVariable Long tailorId) {
        return service.assignTaskToTailor(taskId, tailorId);
    }

    @PutMapping("/tasks/{taskId}")
    public Task updateTask(@PathVariable Long taskId, @RequestBody Task task) {
        return service.updateTask(taskId, task);
    }

    @DeleteMapping("/tasks/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        service.deleteTask(taskId);
    }


}
