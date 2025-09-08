
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.involvedIn.InvolvedIn;
import acme.entities.tasks.Task;
import acme.realms.technicians.Technician;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("select t from Task t where t.draftMode = false")
	Collection<Task> findPublishedTasks();

	@Query("select t from Task t where t.technician.id = :id")
	Collection<Task> findTasksByTechniciandId(int id);

	@Query("select t from Task t where t.id = :id")
	Task findTaskById(int id);

	@Query("select ii from InvolvedIn ii where ii.task.id = :taskId")
	Collection<InvolvedIn> findInvolvedInsFromTaskId(int taskId);

	@Query("select t from Technician t")
	Collection<Technician> findAllTechnicians();
}
