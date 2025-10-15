
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.claim.ClaimRepository;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogStatus;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	private final ClaimRepository claimRepository;


	@Autowired
	public TrackingLogValidator(final ClaimRepository claimRepository) {
		this.claimRepository = claimRepository;
	}

	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		// HINT: trackingLog can be null
		assert context != null;

		if (trackingLog == null)
			return true;

		if (trackingLog.getResolPercentage() == null)
			return true;

		boolean result;

		// --- Validación de rango del porcentaje ---
		{
			Double resolPercentage;
			boolean validPercentage;

			resolPercentage = trackingLog.getResolPercentage();
			validPercentage = resolPercentage >= 0.0 && resolPercentage <= 100.0;
			super.state(context, validPercentage, "resolPercentage", "acme.validation.trackingLog.resolPercentage.message");
		}

		// --- Validación del estado en función del porcentaje ---
		{
			TrackingLogStatus status;
			boolean validStatus;

			status = trackingLog.getStatus();
			if (trackingLog.getResolPercentage() == 100.0)
				validStatus = status == TrackingLogStatus.ACCEPTED || status == TrackingLogStatus.DENIED;
			else
				validStatus = status == TrackingLogStatus.PENDING;
			super.state(context, validStatus, "status", "acme.validation.trackingLog.status.message");
		}

		// --- Validación de resolución obligatoria al 100% ---
		{
			String resolution;
			boolean validResolution;

			resolution = trackingLog.getResolution();
			if (trackingLog.getResolPercentage() == 100.0)
				validResolution = resolution != null && !resolution.isEmpty();
			else
				validResolution = true;
			super.state(context, validResolution, "resolution", "acme.validation.trackingLog.resolution.message");
		}

		result = !super.hasErrors(context);
		return result;
	}
}
