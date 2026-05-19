package projekt.io.firma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projekt.io.firma.model.Task;

import java.util.List;
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByDesigner_Login(String login);

	List<Task> findByTailor_Login(String login);

}