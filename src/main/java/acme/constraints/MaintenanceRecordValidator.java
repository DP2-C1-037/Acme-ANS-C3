
package acme.constraints;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.maintenanceRecords.MaintenanceRecord;

@Validator
public class MaintenanceRecordValidator extends AbstractValidator<ValidMaintenanceRecord, MaintenanceRecord> {

	@Override
	protected void initialise(final ValidMaintenanceRecord constraintAnnotation) {
		assert constraintAnnotation != null;
	}

	@Override
	public boolean isValid(final MaintenanceRecord maintenanceRecord, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		boolean isNull;

		isNull = maintenanceRecord == null || maintenanceRecord.getNextInspectionDueDate() == null;

		if (!isNull) {
			boolean nextInspectionIsFuture;

			Date nextInspection = maintenanceRecord.getNextInspectionDueDate();
			Date minDate = MomentHelper.deltaFromCurrentMoment(1, ChronoUnit.MINUTES);
			nextInspectionIsFuture = MomentHelper.isAfterOrEqual(nextInspection, minDate);

			super.state(context, nextInspectionIsFuture, "nextInspectionDueDate", "{acme.validation.maintenance-record.nextInspection.message}");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
