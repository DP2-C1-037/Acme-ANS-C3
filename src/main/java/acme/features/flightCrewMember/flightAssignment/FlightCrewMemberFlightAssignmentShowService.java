
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.datatypes.AssignmentStatus;
import acme.datatypes.FlightCrewDuty;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.leg.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		FlightAssignment assignment;
		FlightCrewMember member;

		masterId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(masterId);
		member = assignment == null ? null : assignment.getFlightCrewMember();
		status = assignment != null;

		status = status && (!assignment.isDraftMode() || super.getRequest().getPrincipal().hasRealm(member));

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment assignment;
		int assignmentId;

		assignmentId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(assignmentId);

		super.getBuffer().addData(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;
		SelectChoices statuses;
		SelectChoices duties;
		Collection<Leg> legs;
		SelectChoices selectedLegs;
		String employeeCode;
		FlightCrewMember member;

		member = assignment.getFlightCrewMember();

		if (assignment.isDraftMode()) {
			legs = this.repository.findPublishedFutureOwnedLegs(MomentHelper.getCurrentMoment(), member.getAirline().getId());

			Leg currentLeg = assignment.getLeg();
			if (!legs.contains(currentLeg))
				legs.add(currentLeg);
		} else
			legs = this.repository.findPublishedLegs();

		employeeCode = assignment.getFlightCrewMember().getEmployeeCode();
		statuses = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());
		duties = SelectChoices.from(FlightCrewDuty.class, assignment.getFlightCrewDuty());
		selectedLegs = SelectChoices.from(legs, "flightNumber", assignment.getLeg());

		dataset = super.unbindObject(assignment, "flightCrewDuty", "lastUpdateMoment", "status", "remarks", "draftMode");
		dataset.put("statuses", statuses);
		dataset.put("employeeCode", employeeCode);
		dataset.put("duties", duties);
		dataset.put("leg", selectedLegs.getSelected().getKey());
		dataset.put("legs", selectedLegs);

		super.getResponse().addData(dataset);
	}

}
