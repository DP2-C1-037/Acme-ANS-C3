
package acme.features.customer.assignedTo;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.mappings.AssignedTo;
import acme.entities.passenger.Passenger;
import acme.realms.customer.Customer;

@GuiService
public class CustomerAssignedToShowService extends AbstractGuiService<Customer, AssignedTo> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerAssignedToRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int assignedToId;
		AssignedTo assignedTo;

		assignedToId = super.getRequest().getData("id", int.class);
		assignedTo = this.repository.findAssignedToById(assignedToId);
		status = assignedTo != null && super.getRequest().getPrincipal().hasRealm(assignedTo.getBooking().getCustomer());

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

		Collection<Passenger> passengers;

		SelectChoices passengerChoices;
		Customer customer;

		Dataset dataset;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		passengers = this.repository.findAllPassengersFromCustomerId(customer.getId());
		passengerChoices = SelectChoices.from(passengers, "passportNumber", assignedTo.getPassenger());

		dataset = super.unbindObject(assignedTo, "booking", "passenger", "passenger.fullName", "passenger.email", "passenger.passportNumber", "passenger.birthDate", "passenger.specialNeeds");
		dataset.put("passenger", passengerChoices.getSelected().getKey());
		dataset.put("passengers", passengerChoices);
		dataset.put("draftMode", assignedTo.getBooking().isDraftMode());

		super.getResponse().addData(dataset);

	}
}
