
package acme.features.airlineManager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.datatypes.FlightSelfTransfer;
import acme.entities.airline.AirlineManager;
import acme.entities.flight.Flight;

@GuiService
public class AirlineManagerFlightPublishService extends AbstractGuiService<AirlineManager, Flight> {

	@Autowired
	private AirlineManagerFlightRepository repository;


	@Override
	public void authorise() {
		int flightId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(flightId);
		AirlineManager manager = flight == null ? null : flight.getAirlineManager();

		boolean status = manager != null && super.getRequest().getPrincipal().getActiveRealm().getId() == manager.getId() && flight.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		int flightId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(flightId);

		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {

		super.bindObject(flight, "tag", "requiresSelfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {

		int flightId = super.getRequest().getData("id", int.class);
		int publishedLegsSize = this.repository.findPublishedLegsByFlightId(flightId).size();
		int legsSize = this.repository.findLegsByFlightId(flightId).size();
		boolean status = flight.isDraftMode() && publishedLegsSize > 0 && legsSize > 0 && publishedLegsSize == legsSize;

		super.state(status, "*", "acme.validation.flight.*.notAllLegsPublishedOrNoLegs.message");
	}

	@Override
	public void perform(final Flight flight) {
		flight.setDraftMode(false);
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		SelectChoices selfTransfer = SelectChoices.from(FlightSelfTransfer.class, flight.getRequiresSelfTransfer());

		dataset = super.unbindObject(flight, "tag", "requiresSelfTransfer", "cost", "description", "draftMode");
		dataset.put("selfTransfer", selfTransfer);
		dataset.put("originCity", flight.getOriginCity());
		dataset.put("destinationCity", flight.getDestinationCity());
		dataset.put("scheduledDeparture", flight.getScheduledDeparture());
		dataset.put("scheduledArrival", flight.getScheduledArrival());
		dataset.put("layovers", flight.getLayoversNumber());
		super.getResponse().addData(dataset);
	}
}
