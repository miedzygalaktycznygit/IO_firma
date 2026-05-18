package projekt.io.firma.model.state;

import projekt.io.firma.model.Task;

public class NewState implements TaskState {
    @Override
    public void accept(Task task) {
        task.changeState(new InProgressState());
    }

    @Override
    public void complete(Task task) {
        throw new IllegalStateException("Nie mozna zakonczyc nowego zadania przed jego rozpoczeciem.");
    }

    @Override
    public String getStatusName() {
        return "NOWE";
    }
}
