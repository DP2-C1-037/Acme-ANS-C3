
package acme.features.technician.involvedIn;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.involvedIn.InvolvedIn;
import acme.entities.tasks.Task;
import acme.entities.tasks.TaskType;
import acme.realms.technicians.Technician;

@GuiService
public class TechnicianInvolvedInShowService extends AbstractGuiService<Technician, InvolvedIn> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private TechnicianInvolvedInRepository repository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		Technician technician;
		InvolvedIn involvedIn;

		id = super.getRequest().getData("id", int.class);
		involvedIn = this.repository.findInvolvedInById(id);
		technician = involvedIn == null ? null : involvedIn.getMaintenanceRecord().getTechnician();
		status = involvedIn != null && (!involvedIn.getMaintenanceRecord().getDraftMode() || super.getRequest().getPrincipal().hasRealm(technician));

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		InvolvedIn involvedIn;
		int id;

		id = super.getRequest().getData("id", int.class);
		involvedIn = this.repository.findInvolvedInById(id);

		super.getBuffer().addData(involvedIn);
	}

	@Override
	public void unbind(final InvolvedIn involvedIn) {
		Collection<Task> tasks;
		SelectChoices choices;
		SelectChoices types;
		Collection<Technician> technicians;
		SelectChoices possibleTechnicians;
		Dataset dataset;
		int id;

		id = super.getRequest().getPrincipal().getActiveRealm().getId();

		tasks = this.repository.findAllAvailableTasksForInvolvedIn(id, involvedIn.getMaintenanceRecord().getId());
		tasks.add(involvedIn.getTask());
		choices = SelectChoices.from(tasks, "description", involvedIn.getTask());

		types = SelectChoices.from(TaskType.class, involvedIn.getTask().getType());

		technicians = this.repository.findAllTechnicians();
		possibleTechnicians = SelectChoices.from(technicians, "identity.name", involvedIn.getTask().getTechnician());

		dataset = super.unbindObject(involvedIn, "task.description", "task.priority", "task.estimatedDuration");
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("tasks", choices);
		dataset.put("types", types);
		dataset.put("technician", possibleTechnicians.getSelected().getKey());
		dataset.put("technicians", possibleTechnicians);
		dataset.put("draftMode", involvedIn.getMaintenanceRecord().getDraftMode());

		super.getResponse().addData(dataset);
	}
}
