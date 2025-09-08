
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.entities.passenger.Passenger;
import acme.features.customer.passenger.CustomerPassengerRepository;

public class PassengerValidator extends AbstractValidator<ValidPassenger, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public boolean isValid(final Passenger passenger, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (passenger == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (StringHelper.matches(passenger.getPassportNumber(), "^[A-Z0-9]{6,9}$")) {
			boolean alreadyAssignedPassportNumber;
			Passenger existingPassenger;

			existingPassenger = this.repository.findPassengerByCustomerIdAndPassportNumber(passenger.getCustomer().getId(), passenger.getPassportNumber());
			alreadyAssignedPassportNumber = existingPassenger == null || existingPassenger.equals(passenger);

			super.state(context, alreadyAssignedPassportNumber, "passportNumber", "acme.validation.passenger.duplicated-passport-number.message");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
