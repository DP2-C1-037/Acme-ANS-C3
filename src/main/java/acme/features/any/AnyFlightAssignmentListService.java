
package acme.features.any;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;

@GuiService
public class AnyFlightAssignmentListService extends AbstractGuiService<Any, FlightAssignment> {

	@Autowired
	private AnyFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<FlightAssignment> assignments;
		assignments = this.repository.findPublishedFlightAssignments();
		super.getBuffer().addData(assignments);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;
		dataset = super.unbindObject(assignment, "lastUpdateMoment", "status");
		dataset.put("flightNumber", assignment.getLeg().getFlightNumber());

		super.addPayload(dataset, assignment, "remarks");
		super.getResponse().addData(dataset);
	}
}
