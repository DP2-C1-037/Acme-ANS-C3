
package acme.entities.booking;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidBooking;
import acme.entities.flight.Flight;
import acme.features.customer.booking.CustomerBookingRepository;
import acme.realms.customer.Customer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidBooking
@Table(indexes = {
	@Index(columnList = "customer_id"), @Index(columnList = "locatorCode", unique = true), @Index(columnList = "draftMode"), @Index(columnList = "customer_id, draftMode"), @Index(columnList = "customer_id, purchaseMoment, draftMode")
})
public class Booking extends AbstractEntity {

	// Serialisation version -----------------------------------------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes ----------------------------------------------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				purchaseMoment;

	@Mandatory
	@Valid
	@Automapped
	private TravelClass			travelClass;

	@Optional
	@ValidString(pattern = "^[0-9]{4}$")
	@Automapped
	private String				lastNibble;

	@Mandatory
	@Automapped
	private boolean				draftMode			= true;

	// Relationships -------------------------------------------------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne
	private Customer			customer;

	@Mandatory
	@Valid
	@ManyToOne
	private Flight				flight;

	// Derived attributes --------------------------------------------------------------------------------------------


	@Transient
	public Money getPrice() {
		Money result;
		CustomerBookingRepository repository;
		Integer numberOfPassengers;
		Money flightCost;

		repository = SpringHelper.getBean(CustomerBookingRepository.class);
		result = new Money();

		if (this.getFlight() != null) {
			flightCost = this.getFlight().getCost();
			numberOfPassengers = repository.findNumberOfPassengersAssignedToBookingById(this.getId());

			result.setCurrency(flightCost.getCurrency());
			result.setAmount(flightCost.getAmount() * numberOfPassengers);
		} else {
			result.setCurrency("EUR");
			result.setAmount(0.);
		}

		return result;

	}

}
