
package acme.features.authenticated.flightCrewMember;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class AuthenticatedFlightCrewMemberCreateService extends AbstractGuiService<Authenticated, FlightCrewMember> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedFlightCrewMemberRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightCrewMember crewMember;
		int userAccountId;
		UserAccount userAccount;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);

		crewMember = new FlightCrewMember();
		crewMember.setUserAccount(userAccount);

		super.getBuffer().addData(crewMember);
	}

	@Override
	public void bind(final FlightCrewMember crewMember) {
		super.bindObject(crewMember, "employeeCode", "phoneNumber", "languageSkills", "availabilityStatus", "salary", "yearsOfExperience", "airline");
	}

	@Override
	public void validate(final FlightCrewMember crewMember) {
		;
	}

	@Override
	public void perform(final FlightCrewMember crewMember) {
		this.repository.save(crewMember);
	}

	@Override
	public void unbind(final FlightCrewMember crewMember) {
		Dataset dataset;
		SelectChoices availabilities;
		SelectChoices airlines;

		availabilities = SelectChoices.from(acme.datatypes.AvailabilityStatus.class, crewMember.getAvailabilityStatus());
		airlines = SelectChoices.from(this.repository.findAllAirlines(), "name", crewMember.getAirline());

		dataset = super.unbindObject(crewMember, "employeeCode", "phoneNumber", "languageSkills", "availabilityStatus", "salary", "yearsOfExperience", "airline");
		dataset.put("availabilities", availabilities);
		dataset.put("airlines", airlines);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
