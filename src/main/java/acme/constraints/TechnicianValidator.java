
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.realms.technicians.Technician;
import acme.realms.technicians.TechnicianRepository;

@Validator
public class TechnicianValidator extends AbstractValidator<ValidTechnician, Technician> {

	@Autowired
	TechnicianRepository repository;


	@Override
	public void initialise(final ValidTechnician constraintAnnotation) {
		assert constraintAnnotation != null;
	}

	@Override
	public boolean isValid(final Technician technician, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		boolean isNull;

		isNull = technician == null || technician.getLicenseNumber() == null || technician.getUserAccount() == null;

		if (!isNull) {
			{
				boolean uniqueTechnician;
				Technician existingTechnician;

				existingTechnician = this.repository.findTechnicianByLicenseNumber(technician.getLicenseNumber());
				uniqueTechnician = existingTechnician == null || existingTechnician.equals(technician);

				super.state(context, uniqueTechnician, "licenseNumber", "acme.validation.technician.license-number-duplicated.message");
			}
			{
				boolean licenseNumberValid;

				String licenseNumber = technician.getLicenseNumber();
				DefaultUserIdentity identity = technician.getUserAccount().getIdentity();
				String name = identity.getName().trim();
				String surname = identity.getSurname().trim();

				String initials = "" + name.charAt(0) + surname.charAt(0);

				licenseNumberValid = StringHelper.startsWith(licenseNumber, initials, true);

				super.state(context, licenseNumberValid, "licenseNumber", "{acme.validation.technician.license-number.message}");
			}
		}

		result = !super.hasErrors(context);

		return result;
	}

}
