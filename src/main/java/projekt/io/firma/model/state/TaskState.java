package projekt.io.firma.model.state;

import projekt.io.firma.model.Task;

public interface TaskState {
    void accept(Task task);
    void complete(Task task);
    String getStatusName();
}
