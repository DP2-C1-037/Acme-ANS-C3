
package acme.features.customer.assignedTo;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.mappings.AssignedTo;
import acme.entities.passenger.Passenger;
import acme.realms.customer.Customer;

@GuiService
public class CustomerAssignedToListService extends AbstractGuiService<Customer, AssignedTo> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private CustomerAssignedToRepository repository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		int masterId;
		boolean status;
		Booking booking;
		Customer customer;

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);
		customer = booking == null ? null : booking.getCustomer();
		status = booking != null && super.getRequest().getPrincipal().hasRealm(customer);

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
