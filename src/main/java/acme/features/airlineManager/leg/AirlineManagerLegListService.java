
package acme.features.airlineManager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.AirlineManager;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;

@GuiService
public class AirlineManagerLegListService extends AbstractGuiService<AirlineManager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AirlineManagerLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int masterId = super.getRequest().getData("masterId", int.class);

		Flight flight = this.repository.findFlightById(masterId);

		boolean status = flight != null && flight.getAirlineManager().getId() == managerId;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		int masterId = super.getRequest().getData("masterId", int.class);

		Collection<Leg> legs = this.repository.findAllLegsByFlightId(masterId);

		super.getResponse().addGlobal("masterId", masterId);
		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		int masterId = super.getRequest().getData("masterId", int.class);

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival");
		super.addPayload(dataset, leg, "status", "draftMode", "duration");

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addData(dataset);
	}

}
