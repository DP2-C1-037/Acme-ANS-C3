
package acme.entities.passenger;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidPassenger;
import acme.realms.customer.Customer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidPassenger
@Table(indexes = {
	@Index(columnList = "customer_id"), @Index(columnList = "customer_id, passportNumber")
})
public class Passenger extends AbstractEntity {

	// Serialisation version -----------------------------------------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes ----------------------------------------------------------------------------------------------------

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	String						fullName;

	@Mandatory
	@ValidEmail
	@Automapped
	String						email;

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,9}$")
	@Automapped
	String						passportNumber;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date						birthDate;

	@Optional
	@ValidString(min = 0, max = 50)
	@Automapped
	String						specialNeeds;

	@Mandatory
	@Automapped
	private boolean				draftMode			= true;

	// Relationships -------------------------------------------------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne
	private Customer			customer;

}
