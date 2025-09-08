
package acme.features.technician.involvedIn;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.involvedIn.InvolvedIn;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.realms.technicians.Technician;

@GuiService
public class TechnicianInvolvedInListService extends AbstractGuiService<Technician, InvolvedIn> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private TechnicianInvolvedInRepository repository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		MaintenanceRecord maintenanceRecord;
		Technician technician;

		masterId = super.getRequest().getData("masterId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
		technician = maintenanceRecord == null ? null : maintenanceRecord.getTechnician();
		status = maintenanceRecord != null && (!maintenanceRecord.getDraftMode() || super.getRequest().getPrincipal().hasRealm(technician));

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<InvolvedIn> involvedIns;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		involvedIns = this.repository.findInvolvedInsByMaintenanceRecordId(masterId);

		super.getBuffer().addData(involvedIns);
	}

	@Override
	public void unbind(final Collection<InvolvedIn> involvedIns) {
		int masterId;
		MaintenanceRecord maintenanceRecord;

		masterId = super.getRequest().getData("masterId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("draftMode", maintenanceRecord.getDraftMode());
	}

	@Override
	public void unbind(final InvolvedIn involvedIn) {
		Dataset dataset;

		dataset = super.unbindObject(involvedIn, "task.description", "task.type", "task.priority");

		super.getResponse().addData(dataset);
	}
}
