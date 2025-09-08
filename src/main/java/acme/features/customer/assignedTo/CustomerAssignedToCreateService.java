
package acme.features.customer.assignedTo;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.mappings.AssignedTo;
import acme.entities.passenger.Passenger;
import acme.realms.customer.Customer;

@GuiService
public class CustomerAssignedToCreateService extends AbstractGuiService<Customer, AssignedTo> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerAssignedToRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Booking booking;
		Customer customer;
		Passenger passenger;
		int passengerId;

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);
		customer = booking == null ? null : booking.getCustomer();
		status = booking != null && booking.isDraftMode() && super.getRequest().getPrincipal().hasRealm(customer);

		if (status && super.getRequest().hasData("passenger", int.class)) {
			passengerId = super.getRequest().getData("passenger", int.class);
			passenger = this.repository.findPassengerById(passengerId);
			status = passengerId == 0 || passenger != null && super.getRequest().getPrincipal().hasRealm(passenger.getCustomer());
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		AssignedTo assignedTo;
		Booking booking;
		int masterId;

		assignedTo = new AssignedTo();

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);

		assignedTo.setBooking(booking);

		super.getBuffer().addData(assignedTo);
	}

	@Override
	public void bind(final AssignedTo assignedTo) {
		int passengerId;
		Passenger passenger;

		passengerId = super.getRequest().getData("passenger", int.class);
		passenger = this.repository.findPassengerById(passengerId);

		super.bindObject(assignedTo, "passenger");

		assignedTo.setPassenger(passenger);
	}

	@Override
	public void validate(final AssignedTo assignedTo) {
		{
			boolean alreadyAssigned;

			if (assignedTo.getPassenger() != null) {
				List<AssignedTo> assignedTos = this.repository.findAssignationFromBookingIdAndPassengerId(assignedTo.getBooking().getId(), assignedTo.getPassenger().getId()).stream().toList();

				alreadyAssigned = assignedTos.isEmpty();

				super.state(alreadyAssigned, "passenger", "acme.validation.assignedTo.alreadyAssigned.message");
			}
		}
	}

	@Override
	public void perform(final AssignedTo assignedTo) {
		this.repository.save(assignedTo);
	}

	@Override
	public void unbind(final AssignedTo assignedTo) {
		Collection<Passenger> passengers;

		SelectChoices passengerChoices;
		Customer customer;

		Dataset dataset;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		passengers = this.repository.findAllPassengersFromCustomerId(customer.getId());

		passengerChoices = SelectChoices.from(passengers, "passportNumber", assignedTo.getPassenger());

		dataset = super.unbindObject(assignedTo, "booking", "passenger");
		dataset.put("passenger", passengerChoices.getSelected().getKey());
		dataset.put("passengers", passengerChoices);

		dataset.put("masterId", super.getRequest().getData("masterId", int.class));

		super.getResponse().addData(dataset);
	}
}
