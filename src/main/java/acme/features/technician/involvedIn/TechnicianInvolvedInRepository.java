
package acme.features.technician.involvedIn;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.involvedIn.InvolvedIn;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.entities.tasks.Task;
import acme.realms.technicians.Technician;

@Repository
public interface TechnicianInvolvedInRepository extends AbstractRepository {

	@Query("select ii from InvolvedIn ii where ii.maintenanceRecord.id = :maintenanceRecordId")
	Collection<InvolvedIn> findInvolvedInsByMaintenanceRecordId(int maintenanceRecordId);

	@Query("select ii from InvolvedIn ii where ii.id = :id")
	InvolvedIn findInvolvedInById(int id);

	@Query("select mr from MaintenanceRecord mr where mr.id = :maintenanceRecordId")
	MaintenanceRecord findMaintenanceRecordById(int maintenanceRecordId);

	@Query("select t from Task t where t.id = :taskId")
	Task findTaskByTaskId(int taskId);

	@Query("select t from Task t where (t.draftMode = false or t.technician.id = :technicianId) and (t.id not in (select ii.task.id from InvolvedIn ii where ii.maintenanceRecord.id = :maintenanceRecordId))")
	Collection<Task> findAllAvailableTasksForInvolvedIn(int technicianId, int maintenanceRecordId);

	@Query("select t from Task t where (t.draftMode = false or t.technician.id = :technicianId) and (t.id not in (select ii.task.id from InvolvedIn ii where ii.maintenanceRecord.id = :maintenanceRecordId)) and t.id = :taskId")
	Task findNotDuplicatedTaskInMaintenanceRecord(int technicianId, int maintenanceRecordId, int taskId);

	@Query("select ii from InvolvedIn ii where ii.maintenanceRecord.id = :maintenanceRecordId and ii.task.id = :taskId")
	InvolvedIn findInvolvedInByMaintenanceRecordIdAndTaskId(int maintenanceRecordId, int taskId);

	@Query("select t from Technician t")
	Collection<Technician> findAllTechnicians();
}
