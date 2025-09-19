
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
public class FlightCrewMemberFlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		boolean validLeg;
		boolean isMember;
		FlightCrewMember member;

		isMember = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

		status = isMember;

		if (super.getRequest().getMethod().equals("POST")) {
			int legId = super.getRequest().getData("leg", int.class);
			Leg leg = this.repository.findLegById(legId);

			if (leg != null) {
				member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
				boolean isFuture = MomentHelper.isBefore(MomentHelper.getCurrentMoment(), leg.getScheduledArrival());
				boolean isMyAirline = leg.getAircraft().getAirline().getId() == member.getAirline().getId();
				validLeg = !leg.isDraftMode() && isFuture && isMyAirline;
				status = validLeg;
			} else
				validLeg = legId == 0;
			status = status && validLeg;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment assignment;

		assignment = new FlightAssignment();
		assignment.setDraftMode(true);
		assignment.setLastUpdateMoment(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		int legId;
		Leg leg;
		FlightCrewMember member;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(assignment, "flightCrewDuty", "status", "remarks");
		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		assignment.setLeg(leg);
		assignment.setFlightCrewMember(member);
		assignment.setLastUpdateMoment(MomentHelper.getCurrentMoment());
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		;
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;
		SelectChoices statuses;
		SelectChoices duties;
		Collection<Leg> legs;
		SelectChoices selectedLegs;
		FlightCrewMember member;

		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		legs = this.repository.findPublishedFutureOwnedLegs(MomentHelper.getCurrentMoment(), member.getAirline().getId());

		statuses = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());
		duties = SelectChoices.from(FlightCrewDuty.class, assignment.getFlightCrewDuty());
		selectedLegs = SelectChoices.from(legs, "flightNumber", assignment.getLeg());

		dataset = super.unbindObject(assignment, "flightCrewDuty", "status", "remarks", "draftMode");
		dataset.put("employeeCode", member.getEmployeeCode());
		dataset.put("statuses", statuses);
		dataset.put("duties", duties);
		dataset.put("leg", selectedLegs.getSelected().getKey());
		dataset.put("legs", selectedLegs);

		super.getResponse().addData(dataset);
	}
}
