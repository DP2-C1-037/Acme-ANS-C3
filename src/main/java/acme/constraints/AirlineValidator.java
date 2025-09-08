
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.SpringHelper;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineRepository;

public class AirlineValidator extends AbstractValidator<ValidAirline, Airline> {

	// Con el if de las propiedades no nulas vale
	@Override
	public boolean isValid(final Airline airlineToValidate, final ConstraintValidatorContext context) {
		AirlineRepository airlineRepository = SpringHelper.getBean(AirlineRepository.class);
		boolean result = false;
		if (airlineToValidate != null && airlineToValidate.getIataCode() != null) {
			// IataCode uniqueness
			Airline existingAirline = airlineRepository.findAirlineByIataCode(airlineToValidate.getIataCode());
			result = existingAirline == null || existingAirline.equals(airlineToValidate);
			super.state(context, result, "iataCode", "acme.validation.airline.iataCode.message");
		}
		return result;
	}
}
