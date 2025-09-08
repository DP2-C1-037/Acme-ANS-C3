
package acme.constraints;

import java.time.Duration;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.entities.leg.Leg;
import acme.entities.leg.LegRepository;

public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private LegRepository legRepository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidLeg annotation) {
		;
	}

	@Override
	public boolean isValid(final Leg legToValidate, final ConstraintValidatorContext context) {
		boolean result = true;

		boolean statusNotNull = legToValidate.getStatus() != null;
		super.state(context, statusNotNull, "status", "acme.validation.leg.status.notNull.message");

		boolean flightNumberNotNull = !legToValidate.getFlightNumber().isBlank();
		super.state(context, flightNumberNotNull, "flightNumber", "acme.validation.leg.flightNumber.notNull.message");
		if (flightNumberNotNull) {
			// flightNumber uniqueness
			Leg existingLeg = this.legRepository.findLegByFlightNumber(legToValidate.getFlightNumber());
			boolean flightNumberuniqueness = existingLeg == null || existingLeg.getId() == legToValidate.getId();
			super.state(context, flightNumberuniqueness, "flightNumber", "acme.validation.leg.flightNumber.unique.message");
			// flightNumber letters correspond to airline's iataCode
			String airlineIataCode = this.legRepository.getIataCodeFromFlightId(legToValidate.getFlight().getId());
			boolean flightNumberIataCode = StringHelper.startsWith(legToValidate.getFlightNumber(), airlineIataCode, true);
			super.state(context, flightNumberIataCode, "flightNumber", "acme.validation.leg.flightNumber.iataCode.message");
		}

		boolean scheduledDepartureNotNull = legToValidate.getScheduledDeparture() != null;
		super.state(context, scheduledDepartureNotNull, "scheduledDeparture", "acme.validation.leg.scheduledDeparture.notNull.message");

		boolean scheduledArrivalNotNull = legToValidate.getScheduledArrival() != null;
		super.state(context, scheduledArrivalNotNull, "scheduledArrival", "acme.validation.leg.scheduledArrival.notNull.message");

		boolean departureAirportNotNull = legToValidate.getDepartureAirport() != null;
		super.state(context, departureAirportNotNull, "departureAirport", "acme.validation.leg.departureAirport.notNull.message");

		boolean arrivalAirportNotNull = legToValidate.getArrivalAirport() != null;
		super.state(context, arrivalAirportNotNull, "arrivalAirport", "acme.validation.leg.arrivalAirport.notNull.message");

		boolean aircraftNotNull = legToValidate.getAircraft() != null;
		super.state(context, aircraftNotNull, "aircraft", "acme.validation.leg.aircraft.notNull.message");

		if (scheduledDepartureNotNull && scheduledArrivalNotNull) {
			// Diferencia de al menos 1 minuto entre scheduledDeparture y scheduledArrival
			boolean differentSchedules = legToValidate.getScheduledArrival().getTime() - legToValidate.getScheduledDeparture().getTime() >= 60 * 1000;
			super.state(context, differentSchedules, "scheduledArrival", "acme.validation.leg.scheduledArrival.equals.message");
			// No se solapan Legs para el mismo vuelo
			boolean noOverlappingLegs = this.legRepository.findOverlappingLegs(legToValidate.getFlight().getId(), legToValidate.getId(), legToValidate.getScheduledDeparture(), legToValidate.getScheduledArrival()).isEmpty();
			super.state(context, noOverlappingLegs, "scheduledDeparture", "acme.validation.leg.scheduledDeparture.noOverlappingLegs.message");
			if (aircraftNotNull) {
				// No se usa la misma aeronave en diferentes escalas que son a la vez
				boolean noSameAircraft = this.legRepository.findLegByAircraftIdSameTime(legToValidate.getAircraft().getId(), legToValidate.getId(), legToValidate.getScheduledDeparture(), legToValidate.getScheduledArrival()).isEmpty();
				super.state(context, noSameAircraft, "aircraft", "acme.validation.leg.aircraft.noSameAircraft.message");
			}
		}

		if (scheduledDepartureNotNull) {
			// Diferencia máxima de 24 horas y mínima de 1 minuto con la escala anterior
			List<Leg> previousLegs = (List<Leg>) this.legRepository.findPreviousLeg(legToValidate.getFlight().getId(), legToValidate.getScheduledDeparture());
			long diff = !previousLegs.isEmpty() ? legToValidate.getScheduledDeparture().getTime() - previousLegs.get(0).getScheduledArrival().getTime() : 0L;
			boolean noMoreThan1DayWithPreviousLeg = !previousLegs.isEmpty() ? diff >= Duration.ofMinutes(1).toMillis() && diff <= Duration.ofHours(24).toMillis() : true;
			super.state(context, noMoreThan1DayWithPreviousLeg, "scheduledDeparture", "acme.validation.leg.scheduledDeparture.noMoreThan1DayWithPreviousLeg.message");
			boolean noOverlappingLegs = this.legRepository.findOverlappingLegSd(legToValidate.getFlight().getId(), legToValidate.getId(), legToValidate.getScheduledDeparture()).isEmpty();
			super.state(context, noOverlappingLegs, "scheduledDeparture", "acme.validation.leg.scheduledDeparture.noOverlappingLegs.message");
			if (departureAirportNotNull) {
				// No hay 2 escalas saliendo del mismo aeropuerto de salida en el mismo momento (tienen una entrada y una salida)
				boolean noDepartureAirportOverlap = this.legRepository.findLegByAirportIdSameDeparture(legToValidate.getDepartureAirport().getId(), legToValidate.getId(), legToValidate.getScheduledDeparture()).isEmpty();
				super.state(context, noDepartureAirportOverlap, "departureAirport", "acme.validation.leg.departureAirport.noDepartureAirportOverlap.message");
				// El aeropuerto de salida nuevo es igual al de llegada anterior.
				boolean consecutiveLegsArrivalAirportEqualsDepartureAirport = !previousLegs.isEmpty() ? previousLegs.get(0).getArrivalAirport().getId() == legToValidate.getDepartureAirport().getId() : true;
				super.state(context, consecutiveLegsArrivalAirportEqualsDepartureAirport, "departureAirport", "acme.validation.leg.departureAirport.consecutiveLegsMyDepartureAirportEqualsPreviousArrivalAirport.message");
			}
		}

		if (scheduledArrivalNotNull) {
			// Diferencia máxima de 24 horas y mínima de 1 minuto con la escala siguiente
			List<Leg> nextLegs = (List<Leg>) this.legRepository.findNextLeg(legToValidate.getFlight().getId(), legToValidate.getScheduledArrival());
			long diff = !nextLegs.isEmpty() ? nextLegs.get(0).getScheduledDeparture().getTime() - legToValidate.getScheduledArrival().getTime() : 0L;
			boolean noMoreThan1DayWithPreviousLeg = !nextLegs.isEmpty() ? diff >= Duration.ofMinutes(1).toMillis() && diff <= Duration.ofHours(24).toMillis() : true;
			super.state(context, noMoreThan1DayWithPreviousLeg, "scheduledArrival", "acme.validation.leg.scheduledArrival.noMoreThan1DayWithNextLeg.message");
			boolean noOverlappingLegs = this.legRepository.findOverlappingLegSa(legToValidate.getFlight().getId(), legToValidate.getId(), legToValidate.getScheduledArrival()).isEmpty();
			super.state(context, noOverlappingLegs, "scheduledArrival", "acme.validation.leg.scheduledDeparture.noOverlappingLegs.message");
			if (arrivalAirportNotNull) {
				// No hay 2 escalas aterrizando en el mismo aeropuerto de llegada en el mismo momento (tienen una entrada y una salida)
				boolean noArrivalAirportOverlap = this.legRepository.findLegByAirportIdSameArrival(legToValidate.getArrivalAirport().getId(), legToValidate.getId(), legToValidate.getScheduledArrival()).isEmpty();
				super.state(context, noArrivalAirportOverlap, "arrivalAirport", "acme.validation.leg.arrivalAirport.noArrivalAirportOverlap.message");
				// Coherencia entre departureAirport y arrivalAirport con la leg i y la i+1 (con la anterior)
				boolean consecutiveLegsDepartureAirportEqualsArrivalAirport = !nextLegs.isEmpty() ? nextLegs.get(0).getDepartureAirport().getId() == legToValidate.getArrivalAirport().getId() : true;
				super.state(context, consecutiveLegsDepartureAirportEqualsArrivalAirport, "arrivalAirport", "acme.validation.leg.arrivalAirport.consecutiveLegsMyArrivalAirportEqualsNextDepartureAirport.message");
			}
		}

		if (departureAirportNotNull && arrivalAirportNotNull) {
			// departureAirport different than arrivalAirport
			boolean differentAirports = legToValidate.getDepartureAirport().getId() != legToValidate.getArrivalAirport().getId();
			super.state(context, differentAirports, "departureAirport", "acme.validation.leg.departureAirport.equals.message");
		}
		result = !super.hasErrors(context);

		return result;
	}
}
