
package acme.features.airlineManager.leg;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.datatypes.LegStatus;
import acme.entities.aircraft.Aircraft;
import acme.entities.airline.AirlineManager;
import acme.entities.airports.Airport;
import acme.entities.leg.Leg;

@GuiService
public class AirlineManagerLegPublishService extends AbstractGuiService<AirlineManager, Leg> {

	@Autowired
	private AirlineManagerLegRepository repository;


	@Override
	public void authorise() {

		boolean validDepartureAirport = true;
		boolean validArrivalAirport = true;
		boolean validAircraft = true;

		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(legId);
		boolean validLeg = leg != null && leg.isDraftMode();
		boolean managerOwnsLeg = leg != null && leg.getFlight().getAirlineManager().getId() == managerId;

		if (super.getRequest().hasData("departureAirport", int.class)) {
			int departureAirportId = super.getRequest().getData("departureAirport", int.class);
			if (departureAirportId != 0)
				validDepartureAirport = this.repository.findAirportById(departureAirportId) != null;
		}

		if (super.getRequest().hasData("arrivalAirport", int.class)) {
			int arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
			if (arrivalAirportId != 0)
				validArrivalAirport = this.repository.findAirportById(arrivalAirportId) != null;
		}

		if (super.getRequest().hasData("aircraft", int.class)) {
			int aircraftId = super.getRequest().getData("aircraft", int.class);
			if (aircraftId != 0)
				validAircraft = this.repository.findAircraftById(aircraftId) != null;
		}

		boolean status = validLeg && managerOwnsLeg && validDepartureAirport && validArrivalAirport && validAircraft;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(legId);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {

		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft");
	}

	@Override
	public void validate(final Leg leg) {

		Date now = MomentHelper.getCurrentMoment();
		if (leg.getScheduledDeparture() != null) {
			// Validar que scheduledDeparture sea futura
			boolean isFutureDeparture = !leg.getScheduledDeparture().before(now);
			super.state(isFutureDeparture, "scheduledDeparture", "acme.validation.leg.scheduledDeparture.future.message");
		}
		if (leg.getScheduledArrival() != null) {
			// Validar que scheduledArrival sea futura
			boolean isFutureArrival = !leg.getScheduledArrival().before(now);
			super.state(isFutureArrival, "scheduledArrival", "acme.validation.leg.scheduledArrival.future.message");
		}
		if (leg.getScheduledDeparture() != null && leg.getScheduledArrival() != null) {
			boolean lessThan24HoursLeg = Math.abs(leg.getScheduledArrival().getTime() - leg.getScheduledDeparture().getTime()) <= 24 * 60 * 60 * 1000;
			super.state(lessThan24HoursLeg, "scheduledArrival", "acme.validation.leg.scheduledArrival.lessThan24HoursLeg.message");
		}
	}

	@Override
	public void perform(final Leg leg) {
		leg.setDraftMode(false);
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;
		Collection<Aircraft> aircraftsList = this.repository.findAllAircrafts();
		Collection<Airport> airportsList = this.repository.findAllAirports();

		SelectChoices status = SelectChoices.from(LegStatus.class, leg.getStatus());
		SelectChoices aircrafts = SelectChoices.from(aircraftsList, "registrationNumber", leg.getAircraft());
		SelectChoices arrivalAirports = SelectChoices.from(airportsList, "iataCode", leg.getArrivalAirport());
		SelectChoices departureAirports = SelectChoices.from(airportsList, "iataCode", leg.getDepartureAirport());

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "draftMode", "duration", "departureAirport", "arrivalAirport", "aircraft");
		dataset.put("flight", leg.getFlight().getTag());
		dataset.put("arrivalAirport", arrivalAirports.getSelected().getKey());
		dataset.put("arrivalAirports", arrivalAirports);
		dataset.put("departureAirport", departureAirports.getSelected().getKey());
		dataset.put("departureAirports", departureAirports);
		dataset.put("aircraft", aircrafts.getSelected().getKey());
		dataset.put("aircrafts", aircrafts);
		dataset.put("status", status.getSelected().getKey());
		dataset.put("statuses", status);

		super.getResponse().addData(dataset);
	}
}
