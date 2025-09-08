
package acme.features.customer.booking;

import java.util.Collection;
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
import acme.entities.passenger.Passenger;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		Booking booking;
		int flightId;
		Flight flight;
		String travelClass;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);
		status = booking != null && booking.isDraftMode() && super.getRequest().getPrincipal().hasRealm(booking.getCustomer()) && !super.getRequest().getMethod().equals("GET");

		if (status) {
			travelClass = super.getRequest().getData("travelClass", String.class);
			status = !(!travelClass.equals("0") && Stream.of(TravelClass.values()).noneMatch(v -> v.name().equals(travelClass)));

			flightId = super.getRequest().getData("flight", int.class);
			flight = this.repository.findFlightById(flightId);
			status = status && (flightId == 0 || flight != null && !flight.isDraftMode());
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking booking;
		int id;

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "travelClass", "lastNibble", "flight");
	}

	@Override
	public void validate(final Booking booking) {
		{
			boolean lastNibbleStored;

			lastNibbleStored = !booking.getLastNibble().isBlank();
			super.state(lastNibbleStored, "lastNibble", "acme.validation.booking.lastNibble.message");
		}
		{
			boolean atLeastAPassengerAssigned;
			Integer numberOfPassengersAssigned;

			numberOfPassengersAssigned = this.repository.findNumberOfPassengersAssignedToBookingById(booking.getId());

			atLeastAPassengerAssigned = numberOfPassengersAssigned != 0;

			super.state(atLeastAPassengerAssigned, "*", "acme.validation.booking.passengers.message");
		}
		{
			boolean allPassengersPublished;
			Collection<Passenger> bookingPassengers;

			bookingPassengers = this.repository.findAllPassengersFromBookingById(booking.getId());

			allPassengersPublished = bookingPassengers.stream().allMatch(p -> !p.isDraftMode());

			super.state(allPassengersPublished, "*", "acme.validation.booking.passengersPublished.message");

		}

	}

	@Override
	public void perform(final Booking booking) {
		booking.setDraftMode(false);
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
		flightsChoices = SelectChoices.from(flightsInFuture, "originDestinationTag", booking.getFlight());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "lastNibble", "draftMode");
		dataset.put("price", booking.getPrice());
		dataset.put("travelClasses", travelClassesChoices);
		dataset.put("travelClass", travelClassesChoices.getSelected().getKey());
		dataset.put("flights", flightsChoices);
		dataset.put("flight", flightsChoices.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

}
