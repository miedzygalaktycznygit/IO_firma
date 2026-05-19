package projekt.io.firma.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import projekt.io.firma.model.Task;
import projekt.io.firma.service.TaskManagementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import java.util.Set;


@RestController
@RequestMapping("/api")
public class AppController {

    private final TaskManagementService service;

    public AppController(TaskManagementService service) {
        this.service = service;
    }

    @GetMapping("/tasks")
    public List<Task> getTasks(Authentication authentication) {
        return service.getTasksForUser(authentication.getName(), extractRoles(authentication));
    }

    @PostMapping("/tasks")
    public Task createTask(@RequestBody Task task, Authentication authentication) {
        return service.createTask(task, authentication.getName());
    }

    @PutMapping("/tasks/{taskId}/accept")
    public Task acceptTask(@PathVariable Long taskId) {
        return service.acceptTask(taskId);
    }

    @PutMapping("/tasks/{taskId}/complete")
    public Task completeTask(@PathVariable Long taskId) {
        return service.completeTask(taskId);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
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

    private Set<String> extractRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(granted -> granted.getAuthority())
                .collect(java.util.stream.Collectors.toSet());
    }


}
