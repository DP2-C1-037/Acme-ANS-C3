
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.involvedIn.InvolvedIn;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.realms.technicians.Technician;

@Repository
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("select m from MaintenanceRecord m where m.draftMode = false")
	Collection<MaintenanceRecord> findPublishedMaintenanceRecords();

	@Query("select m from MaintenanceRecord m where m.technician.id = :technicianId")
	Collection<MaintenanceRecord> findMaintenanceRecordsByTechnicianId(int technicianId);

	@Query("select m from MaintenanceRecord m where m.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("select a from Aircraft a where a.id = :id")
	Aircraft findAircraftById(int id);

	@Query("select a from Aircraft a")
	Collection<Aircraft> findAvailableAircrafts();

	@Query("select ii from InvolvedIn ii where ii.maintenanceRecord.id = :maintenanceRecordId")
	Collection<InvolvedIn> findInvolvedInsFromMaintenanceRecordId(int maintenanceRecordId);

	@Query("select count(ii.task) from InvolvedIn ii where ii.maintenanceRecord.id = :maintenanceRecordId")
	int findTasksByMaintenanceRecordId(int maintenanceRecordId);

	@Query("select count(ii.task) from InvolvedIn ii where ii.maintenanceRecord.id = :maintenanceRecordId and ii.task.draftMode = true")
	int findNotPublishedTasksByMaintenanceRecordId(int maintenanceRecordId);

	@Query("select t from Technician t")
	Collection<Technician> findAllTechnicians();
}
