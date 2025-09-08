
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.entities.maintenanceRecords.MaintenanceStatus;
import acme.realms.technicians.Technician;

@GuiService
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int aircraftId;
		Aircraft aircraft;
		MaintenanceStatus maintenanceRecordStatus;

		if (super.getRequest().getMethod().equals("GET"))
			status = true;
		else {
			aircraftId = super.getRequest().getData("aircraft", int.class);
			aircraft = this.repository.findAircraftById(aircraftId);

			status = aircraftId == 0 || aircraft != null;

			if (status) {
				maintenanceRecordStatus = super.getRequest().getData("status", MaintenanceStatus.class);
				if (maintenanceRecordStatus != null)
					status = !maintenanceRecordStatus.equals(MaintenanceStatus.COMPLETED);
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		Technician technician;

		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();

		maintenanceRecord = new MaintenanceRecord();
		maintenanceRecord.setTechnician(technician);
		maintenanceRecord.setDraftMode(true);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		int aircraftId;
		Aircraft aircraft;

		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.repository.findAircraftById(aircraftId);

		super.bindObject(maintenanceRecord, "maintenanceDate", "status", "nextInspectionDueDate", "estimatedCost", "notes");
		maintenanceRecord.setAircraft(aircraft);
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		;
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		SelectChoices possibleStatus;
		Collection<Aircraft> aircrafts;
		SelectChoices choices;
		Collection<Technician> technicians;
		SelectChoices possibleTechnicians;
		Dataset dataset;

		possibleStatus = new SelectChoices();
		possibleStatus.add("0", "----", maintenanceRecord.getStatus() == null);
		possibleStatus.add("PENDING", "PENDING", maintenanceRecord.getStatus() == MaintenanceStatus.PENDING);
		possibleStatus.add("IN_PROGRESS", "IN_PROGRESS", maintenanceRecord.getStatus() == MaintenanceStatus.IN_PROGRESS);

		technicians = this.repository.findAllTechnicians();
		possibleTechnicians = SelectChoices.from(technicians, "identity.name", maintenanceRecord.getTechnician());

		aircrafts = this.repository.findAvailableAircrafts();
		choices = SelectChoices.from(aircrafts, "model", maintenanceRecord.getAircraft());

		dataset = super.unbindObject(maintenanceRecord, "maintenanceDate", "status", "nextInspectionDueDate", "estimatedCost", "notes");
		dataset.put("statuses", possibleStatus);
		dataset.put("aircraft", choices.getSelected().getKey());
		dataset.put("aircrafts", choices);
		dataset.put("technician", possibleTechnicians.getSelected().getKey());
		dataset.put("technicians", possibleTechnicians);

		super.getResponse().addData(dataset);
	}

}
