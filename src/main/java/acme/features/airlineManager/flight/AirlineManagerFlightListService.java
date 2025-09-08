
package acme.features.airlineManager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.AirlineManager;
import acme.entities.flight.Flight;

@GuiService
public class AirlineManagerFlightListService extends AbstractGuiService<AirlineManager, Flight> {

	@Autowired
	private AirlineManagerFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		Collection<Flight> flights = this.repository.findAllFlightsByAirlineManagerId(managerId);

		super.getBuffer().addData(flights);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "requiresSelfTransfer", "cost");
		dataset.put("departure", flight.getScheduledDeparture());
		dataset.put("arrival", flight.getScheduledArrival());
		dataset.put("departureCity", flight.getOriginCity());
		dataset.put("arrivalCity", flight.getDestinationCity());
		dataset.put("numberOfLayovers", flight.getLayoversNumber());
		dataset.put("published", !flight.isDraftMode());
		super.addPayload(dataset, flight, "description");
		super.getResponse().addData(dataset);
	}
}
