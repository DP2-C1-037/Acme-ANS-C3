
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDashboard extends AbstractForm {

	// Serialisation version -----------------------------------------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	List<String>				lastFiveDestinations;
	Money						lastYearMoneySpentInBookings;
	Map<String, Integer>		numberOfBookingsByTravelClass;
	Money						lastFiveYearsBookingCostsCount;
	Money						lastFiveYearsBookingCostsAverage;
	Money						lastFiveYearsBookingCostsMinimum;
	Money						lastFiveYearsBookingCostsMaximum;
	Money						lastFiveYearsBookingCostsStandardDeviation;
	Integer						bookingsNumberOfPassengersCount;
	Double						bookingsNumberOfPassengersAverage;
	Integer						bookingsNumberOfPassengersMinimum;
	Integer						bookingsNumberOfPassengersMaximum;
	Double						bookingsNumberOfPassengersStandardDeviation;

}
