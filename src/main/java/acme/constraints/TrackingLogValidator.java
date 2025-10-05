
package acme.constraints;

import java.util.Date;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.claim.Claim;
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
		assert context != null;

		if (trackingLog == null)
			return true;

		Claim claim = trackingLog.getClaim();
		List<TrackingLog> trackingLogs = this.claimRepository.getTrackingLogsByResolutionOrder(claim.getId());

		boolean isNull = trackingLog.getResolPercentage() == null || trackingLog.getStatus() == null;

		if (!isNull) {
			{
				Double resolPercentage = trackingLog.getResolPercentage();
				boolean validPercentage = resolPercentage >= 0.0 && resolPercentage <= 100.0;
				super.state(context, validPercentage, "resolPercentage", "acme.validation.trackingLog.resolPercentage.message");
			}

			{
				TrackingLogStatus status = trackingLog.getStatus();
				boolean validStatus;
				if (trackingLog.getResolPercentage() == 100.0)
					validStatus = status == TrackingLogStatus.ACCEPTED || status == TrackingLogStatus.DENIED;
				else
					validStatus = status == TrackingLogStatus.PENDING;
				super.state(context, validStatus, "status", "acme.validation.trackingLog.status.message");
			}
			{
				String resolution = trackingLog.getResolution();
				boolean validResolution;
				if (trackingLog.getResolPercentage() == 100.0)
					validResolution = resolution != null && !resolution.isEmpty();
				else
					validResolution = true;
				super.state(context, validResolution, "resolution", "acme.validation.trackingLog.resolution.message");
			}
			{
				Date creationMoment = trackingLog.getCreationMoment();
				Date lastUpdateMoment = trackingLog.getLastUpdateMoment();
				boolean validMoments = creationMoment != null && lastUpdateMoment != null && !creationMoment.after(lastUpdateMoment);
				super.state(context, validMoments, "lastUpdateMoment", "acme.validation.trackingLog.moments.message");
			}
		}

		return !super.hasErrors(context);
	}
}
