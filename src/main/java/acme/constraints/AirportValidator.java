
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.entities.airports.Airport;
import acme.entities.airports.AirportRepository;

@Validator
public class AirportValidator extends AbstractValidator<ValidAirport, Airport> {

	@Autowired
	AirportRepository repository;


	@Override
	protected void initialise(final ValidAirport constraintAnnotation) {
		;
	}

	@Override
	public boolean isValid(final Airport airport, final ConstraintValidatorContext context) {

		boolean result;
		boolean isNull;

		isNull = airport == null || airport.getIataCode() == null;

		if (!isNull) {
			boolean uniqueAirport;
			Airport existingAirport;

			existingAirport = StringHelper.matches(airport.getIataCode(), "^[A-Z]{3}$") ? this.repository.findAirportByCode(airport.getIataCode()) : null;
			uniqueAirport = existingAirport == null || existingAirport.equals(airport);

			super.state(context, uniqueAirport, "iataCode", "acme.validation.airport.code-duplicated.message");
		}

		result = !super.hasErrors(context);

		return result;
	}
}
