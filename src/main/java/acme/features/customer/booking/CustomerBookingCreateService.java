
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		boolean status = true;
		Flight flight;
		String travelClass;
		int flightId;

		if (super.getRequest().getMethod().equals("GET"))
			status = true;
		else {

			travelClass = super.getRequest().getData("travelClass", String.class);
			status = !(!travelClass.equals("0") && Stream.of(TravelClass.values()).noneMatch(v -> v.name().equals(travelClass)));

			flightId = super.getRequest().getData("flight", int.class);
			flight = this.repository.findFlightById(flightId);
			status = status && (flightId == 0 || flight != null);
			if (status && flight != null)
				status = !flight.isDraftMode();
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Date moment;
		Customer customer;
		Booking booking;

		moment = MomentHelper.getCurrentMoment();
		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		booking = new Booking();
		booking.setLocatorCode("");
		booking.setPurchaseMoment(moment);
		booking.setTravelClass(TravelClass.ECONOMY);
		booking.setLastNibble(null);
		booking.setCustomer(customer);
		booking.setDraftMode(true);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "travelClass", "lastNibble", "flight");
	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		SelectChoices travelClassesChoices;
		Collection<Flight> flights;
		List<Flight> flightsInFuture;
		SelectChoices flightsChoices;
		Dataset dataset;

		flights = this.repository.findAllFlightsPublished();
		flightsInFuture = flights.stream().filter(f -> MomentHelper.isFuture(f.getScheduledDeparture())).collect(Collectors.toList());

		travelClassesChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		flightsChoices = SelectChoices.from(flightsInFuture, "originDestinationTag", null);

		dataset = super.unbindObject(booking, "locatorCode", "travelClass", "lastNibble", "flight", "draftMode");
		dataset.put("travelClasses", travelClassesChoices);
		dataset.put("travelClass", travelClassesChoices.getSelected().getKey());
		dataset.put("flights", flightsChoices);
		dataset.put("flight", flightsChoices.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

}
