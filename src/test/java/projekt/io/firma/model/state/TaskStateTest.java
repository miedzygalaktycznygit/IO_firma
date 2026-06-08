package projekt.io.firma.model.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import projekt.io.firma.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskStateTest {

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setTitle("Testowe zadanie");
        task.setDescription("Opis testowy");
        task.setStatus("NOWE");
        task.init();
    }

    @Test
    void testInitialStateIsNew() {
        assertNotNull(task.getState());
        assertTrue(task.getState() instanceof NewState);
        assertEquals("NOWE", task.getStatus());
    }

    @Test
    void testAcceptFromNewToInProgress() {
        task.accept();
        assertTrue(task.getState() instanceof InProgressState);
        assertEquals("W_REALIZACJI", task.getStatus());
    }

    @Test
    void testCompleteFromNewThrowsException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> task.complete());
        assertEquals("Nie mozna zakonczyc nowego zadania przed jego rozpoczeciem.", exception.getMessage());
        assertTrue(task.getState() instanceof NewState);
        assertEquals("NOWE", task.getStatus());
    }

    @Test
    void testAcceptFromInProgressThrowsException() {
        task.accept();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> task.accept());
        assertEquals("Zadanie jest juz w realizacji.", exception.getMessage());
        assertTrue(task.getState() instanceof InProgressState);
    }

    @Test
    void testCompleteFromInProgressToCompleted() {
        task.accept();
        task.complete();

        assertTrue(task.getState() instanceof CompletedState);
        assertEquals("ZAKONCZONE", task.getStatus());
    }

    @Test
    void testAcceptFromCompletedThrowsException() {
        task.accept();
        task.complete();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> task.accept());
        assertEquals("Zadanie zostalo juz zakonczone.", exception.getMessage());
    }

    @Test
    void testCompleteFromCompletedThrowsException() {
        task.accept();
        task.complete();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> task.complete());
        assertEquals("Zadanie zostalo juz zakonczone.", exception.getMessage());
    }

    @Test
    void testUnknownStatusFallsBackToNewState() {
        task.setStatus("NIEZNANY_STATUS");
        task.updateState();
        
        assertTrue(task.getState() instanceof NewState);
        assertEquals("NOWE", task.getStatus());
    }
}
