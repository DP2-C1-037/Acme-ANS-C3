
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.entities.airline.AirlineManager;
import acme.entities.airline.AirlineManagerRepository;

public class AirlineManagerValidator extends AbstractValidator<ValidAirlineManager, AirlineManager> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AirlineManagerRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidAirlineManager annotation) {
		assert annotation != null;
	}

	// Con el if de las propiedades no nulas vale
	@Override
	public boolean isValid(final AirlineManager airlineManager, final ConstraintValidatorContext context) {
		boolean result = false;
		if (airlineManager != null && airlineManager.getUserAccount() != null && airlineManager.getIdentifierNumber() != null) {

			// IdentifierNumber first two letters correspond to manager's initials.
			DefaultUserIdentity managerIdentity = airlineManager.getUserAccount().getIdentity();
			String initials = AirlineManagerValidator.getInitials(managerIdentity.getName(), managerIdentity.getSurname());
			result = StringHelper.startsWith(airlineManager.getIdentifierNumber(), initials, true);
			super.state(context, result, "identifierNumber", "acme.validation.airlineManager.identifierNumber.initials.message");

			// IdentifierNumber uniqueness
			AirlineManager existingManager = this.repository.findAirlineManagerByIdentifierNumber(airlineManager.getIdentifierNumber());
			result = existingManager == null || existingManager.equals(airlineManager);
			super.state(context, result, "identifierNumber", "acme.validation.airlineManager.identifierNumber.unique.message");
		}
		return result;
	}

	private static String getInitials(final String name, final String surname) {
		StringBuilder result = new StringBuilder();
		result.append(name.charAt(0));
		result.append(surname.charAt(0));
		return result.toString();
	}
}
