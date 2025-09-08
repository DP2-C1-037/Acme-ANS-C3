
package acme.constraints;

import java.util.Objects;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.entities.flight.Flight;
import acme.entities.leg.LegRepository;

public class FlightValidator extends AbstractValidator<ValidFlight, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private LegRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidFlight annotation) {
	}

	@Override
	public boolean isValid(final Flight flightToValidate, final ConstraintValidatorContext context) {
		boolean result = true;

		if (Boolean.valueOf(flightToValidate.isDraftMode()) != null && Integer.valueOf(flightToValidate.getId()) != null)
			if (!flightToValidate.isDraftMode()) {
				int publishedLegsSize = this.repository.findPublishedLegsByFlightId(flightToValidate.getId()).size();
				int legsSize = this.repository.findLegsByFlightId(flightToValidate.getId()).size();
				boolean allLegsPublished = publishedLegsSize > 0 && legsSize > 0 && publishedLegsSize == legsSize;
				super.state(context, allLegsPublished, "*", "acme.validation.flight.*.notAllLegsPublishedOrNoLegs.message");
			}
		if (flightToValidate.getOriginCity() != null && !flightToValidate.getOriginCity().isBlank() && flightToValidate.getDestinationCity() != null && !flightToValidate.getDestinationCity().isBlank()) {
			boolean flightDifferentAirports = !Objects.equals(flightToValidate.getOriginCity(), flightToValidate.getDestinationCity());
			super.state(context, flightDifferentAirports, "*", "acme.validation.flight.*.flightDifferentAirports.message");
		}
		result = !super.hasErrors(context);
		return result;
	}
}
