
package acme.features.administrator.assignedTo;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.mappings.AssignedTo;
import acme.entities.passenger.Passenger;

@GuiService
public class AdministratorAssignedToListService extends AbstractGuiService<Administrator, AssignedTo> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private AdministratorAssignedToRepository repository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		int masterId;
		boolean status;
		Booking booking;

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);
		status = booking != null && !booking.isDraftMode() && super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<AssignedTo> assignedTos;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		assignedTos = this.repository.findAssignedTosByBookingId(masterId);

		super.getBuffer().addData(assignedTos);
	}

	@Override
	public void unbind(final Collection<AssignedTo> assignedTos) {
		int masterId;
		Booking booking;

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("draftMode", booking.isDraftMode());
	}

	@Override
	public void unbind(final AssignedTo assignedTo) {
		Dataset dataset;

		Passenger passenger = assignedTo.getPassenger();

		String fullname = passenger.getFullName().length() > 50 ? passenger.getFullName().substring(0, 50) + "..." : passenger.getFullName();
		String email = passenger.getEmail().length() > 50 ? passenger.getEmail().substring(0, 50) + "..." : passenger.getEmail();

		dataset = super.unbindObject(assignedTo, "passenger.passportNumber");

		dataset.put("passenger.fullName", fullname);
		dataset.put("passenger.email", email);
		super.addPayload(dataset, assignedTo);

		super.getResponse().addData(dataset);
	}
}
