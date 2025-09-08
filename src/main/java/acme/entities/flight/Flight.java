
package acme.entities.flight;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidFlight;
import acme.datatypes.FlightSelfTransfer;
import acme.entities.airline.AirlineManager;
import acme.entities.leg.LegRepository;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidFlight
// No tiene índices ya que los pone el framework
public class Flight extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Valid
	@Automapped
	private FlightSelfTransfer	requiresSelfTransfer;

	@Mandatory
	@ValidMoney(min = 0, max = 1000000)
	@Automapped
	private Money				cost;

	@Optional
	@ValidString
	@Automapped
	private String				description;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	// Derived attributes


	@Transient
	public Date getScheduledDeparture() {
		LegRepository legRepository = SpringHelper.getBean(LegRepository.class);
		return legRepository.findScheduledDeparture(this.getId());
	}

	@Transient
	public Date getScheduledArrival() {
		LegRepository legRepository = SpringHelper.getBean(LegRepository.class);
		return legRepository.findScheduledArrival(this.getId());
	}

	@Transient
	public String getOriginCity() {
		LegRepository legRepository = SpringHelper.getBean(LegRepository.class);
		return legRepository.findDepartureAirport(this.getId()).stream().findFirst().orElse(null);
	}

	@Transient
	public String getDestinationCity() {
		LegRepository legRepository = SpringHelper.getBean(LegRepository.class);
		return legRepository.findDestinationAirport(this.getId()).stream().findFirst().orElse(null);
	}

	@Transient
	public int getLayoversNumber() {
		LegRepository legRepository = SpringHelper.getBean(LegRepository.class);
		return legRepository.findLegsFromFlightId(this.getId()).size();
	}

	@Transient
	public String getOriginDestinationTag() {
		return this.getOriginCity().concat(" --> ").concat(this.getDestinationCity());
	}

	// Relationships


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private AirlineManager airlineManager;
}
