
package acme.entities.visaRequirement;

import javax.persistence.Entity;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
// https://travel-buddy.ai/api/
public class VisaRequirement extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				passportOf;

	@Mandatory
	@ValidString(min = 2, max = 2)
	@Automapped
	private String				passportCode;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				destination;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				passValid;

	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				exceptText;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				visa;

	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				stayOf;

	@Mandatory
	@ValidUrl
	@Automapped
	private String				link;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				error;

	@Mandatory
	@ValidString(min = 1, max = 10)
	@Automapped
	private String				color;
}
