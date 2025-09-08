
package acme.entities.courses;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUuid;
import lombok.Getter;
import lombok.Setter;

/*
 * API information can be accessed from: https:https:// github.com/Purdue-io/PurdueApi
 * https:// github.com/Purdue-io/PurdueApi/wiki/OData-Queries
 * 
 * The API has information about Purdue University's course catalog and scheduling system.
 * When obtaining a Course, the following information conforms the response data (as seen when using Postman)
 */

@Entity
@Getter
@Setter
public class Course extends AbstractEntity {

	// Serialisation version -----------------------------------------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes ----------------------------------------------------------------------------------------------------

	@Mandatory
	@ValidUuid
	@Column(unique = true)
	private String				courseIdentifier;

	@Mandatory
	@ValidString(pattern = "\\d{5}")
	@Automapped
	private String				number;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				title;

	@Mandatory
	@ValidUuid
	@Automapped
	private String				belongedToSubjectIdentifier;

	@Mandatory
	@ValidNumber(min = 0, integer = 3, fraction = 2)
	@Automapped
	private Double				creditHours;

	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				description;

}
