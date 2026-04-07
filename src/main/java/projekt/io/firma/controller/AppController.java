package projekt.io.firma.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import projekt.io.firma.model.Task;
import projekt.io.firma.service.TaskManagementService;

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

}
