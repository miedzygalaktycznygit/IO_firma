package projekt.io.firma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projekt.io.firma.model.Task;
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}