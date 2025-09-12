
package acme.features.any;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;

@GuiService
public class AnyFlightAssignmentShowService extends AbstractGuiService<Any, FlightAssignment> {

	@Autowired
	private AnyFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		FlightAssignment assignment;

		masterId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(masterId);

		status = assignment != null && !assignment.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id;
		FlightAssignment assignment;

		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;

		dataset = super.unbindObject(assignment, "flightCrewDuty", "lastUpdateMoment", "status", "remarks");
		dataset.put("employeeCode", assignment.getFlightCrewMember().getEmployeeCode());
		dataset.put("flightNumber", assignment.getLeg().getFlightNumber());

		super.getResponse().addData(dataset);
	}
}
