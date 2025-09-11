
package acme.features.authenticated.flightCrewMember;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class AuthenticatedFlightCrewMemberUpdateService extends AbstractGuiService<Authenticated, FlightCrewMember> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedFlightCrewMemberRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightCrewMember object;
		int userAccountId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		object = this.repository.findFlightCrewMemberByUserAccountId(userAccountId);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final FlightCrewMember member) {
		super.bindObject(member, "employeeCode", "phoneNumber", "languageSkills", "availabilityStatus", "salary", "yearsOfExperience", "airline");
	}

	@Override
	public void validate(final FlightCrewMember member) {
		;
	}

	@Override
	public void perform(final FlightCrewMember member) {
		FlightCrewMember updated = this.repository.findFlightCrewMemberById(member.getId());

		updated.setEmployeeCode(member.getEmployeeCode());
		updated.setPhoneNumber(member.getPhoneNumber());
		updated.setLanguageSkills(member.getLanguageSkills());
		updated.setAvailabilityStatus(member.getAvailabilityStatus());
		updated.setSalary(member.getSalary());
		updated.setYearsOfExperience(member.getYearsOfExperience());
		updated.setAirline(member.getAirline());

		this.repository.save(updated);
	}

	@Override
	public void unbind(final FlightCrewMember member) {
		Dataset dataset;
		SelectChoices availabilities;
		Collection<Airline> airlines;
		SelectChoices airlinesCode;

		airlines = this.repository.findAllAirlines();
		availabilities = SelectChoices.from(acme.datatypes.AvailabilityStatus.class, member.getAvailabilityStatus());
		airlinesCode = SelectChoices.from(airlines, "iataCode", member.getAirline());

		dataset = super.unbindObject(member, "employeeCode", "phoneNumber", "languageSkills", "availabilityStatus", "salary", "yearsOfExperience", "airline");
		dataset.put("availabilities", availabilities);
		dataset.put("airlines", airlinesCode);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
