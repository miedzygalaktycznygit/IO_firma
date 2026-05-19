package projekt.io.firma.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import projekt.io.firma.dto.CreateEmployeeRequest;
import projekt.io.firma.dto.EmployeeDto;
import projekt.io.firma.dto.LoginRequest;
import projekt.io.firma.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class ApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    private String login;
    private String password;

    public ApiClient(String baseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl;
    }

    public void setCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public EmployeeDto login(String login, String password) throws IOException, InterruptedException {
        LoginRequest request = new LoginRequest(login, password);
        String body = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }

        EmployeeDto employee = objectMapper.readValue(response.body(), EmployeeDto.class);
        setCredentials(login, password);
        return employee;
    }

    public List<Task> getTasks() throws IOException, InterruptedException {
        HttpRequest request = authorizedRequest("/api/tasks")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
        }
        return objectMapper.readValue(response.body(), new TypeReference<List<Task>>() {
        });
    }

    public Task createTask(Task task) throws IOException, InterruptedException {
        String body = objectMapper.writeValueAsString(task);
        HttpRequest request = authorizedRequest("/api/tasks")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
        }
        return objectMapper.readValue(response.body(), Task.class);
    }

    public Task updateTask(Long taskId, Task task) throws IOException, InterruptedException {
        String body = objectMapper.writeValueAsString(task);
        HttpRequest request = authorizedRequest("/api/tasks/" + taskId)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
        }
        return objectMapper.readValue(response.body(), Task.class);
    }

    public void deleteTask(Long taskId) throws IOException, InterruptedException {
        HttpRequest request = authorizedRequest("/api/tasks/" + taskId)
                .DELETE()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public Task acceptTask(Long taskId) throws IOException, InterruptedException {
        HttpRequest request = authorizedRequest("/api/tasks/" + taskId + "/accept")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
        }
        return objectMapper.readValue(response.body(), Task.class);
    }

    public Task completeTask(Long taskId) throws IOException, InterruptedException {
        HttpRequest request = authorizedRequest("/api/tasks/" + taskId + "/complete")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
        }
        return objectMapper.readValue(response.body(), Task.class);
    }

    public Task assignTaskToTailor(Long taskId, Long tailorId) throws IOException, InterruptedException {
        HttpRequest request = authorizedRequest("/api/tasks/" + taskId + "/assign/" + tailorId)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), Task.class);
    }

    public List<EmployeeDto> getEmployees() throws IOException, InterruptedException {
        HttpRequest request = authorizedRequest("/api/admin/employees")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<EmployeeDto>>() {
        });
    }

    public EmployeeDto createEmployee(CreateEmployeeRequest requestBody) throws IOException, InterruptedException {
        String body = objectMapper.writeValueAsString(requestBody);
        HttpRequest request = authorizedRequest("/api/admin/employees")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), EmployeeDto.class);
    }

    private HttpRequest.Builder authorizedRequest(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path));

        if (login != null && password != null) {
            String token = Base64.getEncoder()
                    .encodeToString((login + ":" + password).getBytes(StandardCharsets.UTF_8));
            builder.header("Authorization", "Basic " + token);
        }

        return builder;
    }
}
