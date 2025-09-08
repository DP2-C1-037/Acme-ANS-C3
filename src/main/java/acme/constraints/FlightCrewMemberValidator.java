
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.realms.flightCrewMember.FlightCrewMember;
import acme.realms.flightCrewMember.FlightCrewMemberRepository;

@Validator
public class FlightCrewMemberValidator extends AbstractValidator<ValidFlightCrewMember, FlightCrewMember> {

	@Autowired
	private FlightCrewMemberRepository repository;


	@Override
	protected void initialise(final ValidFlightCrewMember constraintAnnotation) {
		assert constraintAnnotation != null;
	}

	@Override
	public boolean isValid(final FlightCrewMember flightCrewMember, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		boolean isNull;

		isNull = flightCrewMember == null || flightCrewMember.getEmployeeCode() == null || flightCrewMember.getUserAccount() == null;

		if (!isNull) {
			{
				boolean uniqueFlightCrewMember;
				FlightCrewMember existingFlightCrewMember;

				existingFlightCrewMember = this.repository.findFlightCrewMemberByEmployeeCode(flightCrewMember.getEmployeeCode());
				uniqueFlightCrewMember = existingFlightCrewMember == null || existingFlightCrewMember.equals(flightCrewMember);

				super.state(context, uniqueFlightCrewMember, "employeeCode", "acme.validation.flight-crew-member.employee-code-duplicated.message");
			}
			{
				String employeeCode = flightCrewMember.getEmployeeCode();
				boolean matchesPattern = employeeCode.matches("^[A-Z]{2,3}\\d{6}$");

				super.state(context, matchesPattern, "employeeCode", "{acme.validation.flight-crew-member.employee-code-invalid-format.message}");

			}
			{
				boolean employeeCodeValid;

				DefaultUserIdentity identity = flightCrewMember.getUserAccount().getIdentity();
				String employeeCode = flightCrewMember.getEmployeeCode();

				String name = identity.getName().trim();
				String surname = identity.getSurname().trim();
				String initials = "" + name.charAt(0) + surname.charAt(0);

				employeeCodeValid = StringHelper.startsWith(employeeCode, initials, true);

				super.state(context, employeeCodeValid, "employeeCode", "{acme.validation.flight-crew-member.employee-code-not-matching-initials.message}");
			}
		}

		result = !super.hasErrors(context);
		return result;
	}

}
