package projekt.io.firma.model.state;

import projekt.io.firma.model.Task;

public class InProgressState implements TaskState {
    @Override
    public void accept(Task task) {
        throw new IllegalStateException("Zadanie jest juz w realizacji.");
    }

    @Override
    public void complete(Task task) {
        task.changeState(new CompletedState());
    }

    @Override
    public String getStatusName() {
        return "W_REALIZACJI";
    }
}
