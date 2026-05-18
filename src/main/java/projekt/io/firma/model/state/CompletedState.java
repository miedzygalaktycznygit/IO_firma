package projekt.io.firma.model.state;

import projekt.io.firma.model.Task;

public class CompletedState implements TaskState {
    @Override
    public void accept(Task task) {
        throw new IllegalStateException("Zadanie zostalo juz zakonczone.");
    }

    @Override
    public void complete(Task task) {
        throw new IllegalStateException("Zadanie zostalo juz zakonczone.");
    }

    @Override
    public String getStatusName() {
        return "ZAKONCZONE";
    }
}
