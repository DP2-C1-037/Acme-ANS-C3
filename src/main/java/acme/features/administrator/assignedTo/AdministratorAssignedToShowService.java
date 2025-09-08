
package acme.features.administrator.assignedTo;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.mappings.AssignedTo;

@GuiService
public class AdministratorAssignedToShowService extends AbstractGuiService<Administrator, AssignedTo> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAssignedToRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int assignedToId;
		AssignedTo assignedTo;
		Booking booking;

		assignedToId = super.getRequest().getData("id", int.class);
		assignedTo = this.repository.findAssignedToById(assignedToId);
		booking = assignedTo == null ? null : assignedTo.getBooking();
		status = assignedTo != null && !booking.isDraftMode() && super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int assignedToId;
		AssignedTo assignedTo;

		assignedToId = super.getRequest().getData("id", int.class);
		assignedTo = this.repository.findAssignedToById(assignedToId);

		super.getBuffer().addData(assignedTo);
	}

	@Override
	public void unbind(final AssignedTo assignedTo) {

		Dataset dataset;

		dataset = super.unbindObject(assignedTo, "booking", "passenger.fullName", "passenger.email", "passenger.passportNumber", "passenger.birthDate", "passenger.specialNeeds");

		super.getResponse().addData(dataset);

	}
}
