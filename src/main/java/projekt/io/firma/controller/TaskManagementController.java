package projekt.io.firma.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import projekt.io.firma.model.Task;
import projekt.io.firma.service.TaskManagementService;

@RestController
@RequestMapping("/api/tasks")
public class TaskManagementController {

    private final TaskManagementService taskService;

    public TaskManagementController(TaskManagementService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping
    public Task createTask(@RequestBody Task task, @RequestParam(required = false) Long designerId, @RequestParam(required = false) Long tailorId) {
        return taskService.createTask(task, designerId, tailorId);
    }

    @PutMapping("/{taskId}/decide")
    public Task decideOnTask(@PathVariable Long taskId, @RequestParam boolean accept, @RequestParam String reason) {
        return taskService.decideOnTask(taskId, accept, reason);
    }
}
