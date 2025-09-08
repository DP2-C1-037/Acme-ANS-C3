
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
public class AirlineManagerFlightCreateService extends AbstractGuiService<AirlineManager, Flight> {

	@Autowired
	private AirlineManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status = true;
		if (super.getRequest().hasData("id", int.class)) {
			int flightId = super.getRequest().getData("id", int.class);
			status = flightId == 0;
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		AirlineManager airlineManager = (AirlineManager) super.getRequest().getPrincipal().getActiveRealm();

		Flight flight = new Flight();
		flight.setDraftMode(true);
		flight.setAirlineManager(airlineManager);

		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "requiresSelfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		;
	}

	@Override
	public void perform(final Flight flight) {
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		SelectChoices selfTransfer = SelectChoices.from(FlightSelfTransfer.class, flight.getRequiresSelfTransfer());

		dataset = super.unbindObject(flight, "tag", "requiresSelfTransfer", "cost", "description", "draftMode");
		dataset.put("selfTransfer", selfTransfer);
		super.getResponse().addData(dataset);
	}
}
