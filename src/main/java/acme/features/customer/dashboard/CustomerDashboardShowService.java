
package acme.features.customer.dashboard;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.forms.CustomerDashboard;
import acme.realms.customer.Customer;

@GuiService
public class CustomerDashboardShowService extends AbstractGuiService<Customer, CustomerDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int id = super.getRequest().getPrincipal().getActiveRealm().getId();
		CustomerDashboard dashboard;
		String currencyUsed;
		List<String> lastFiveDestinations;
		Money lastYearMoneySpentInBookings = new Money();
		Map<String, Integer> numberOfBookingsByTravelClass;
		Money lastFiveYearsBookingCostsCount = new Money();
		Money lastFiveYearsBookingCostsAverage = new Money();
		Money lastFiveYearsBookingCostsMinimum = new Money();
		Money lastFiveYearsBookingCostsMaximum = new Money();
		Money lastFiveYearsBookingCostsStandardDeviation = new Money();
		Integer bookingsNumberOfPassengersCount;
		Double bookingsNumberOfPassengersAverage;
		Integer bookingsNumberOfPassengersMinimum;
		Integer bookingsNumberOfPassengersMaximum;
		Double bookingsNumberOfPassengersStandardDeviation;

		// Last five destinations

		Pageable pageable = PageRequest.of(0, 5);
		List<Flight> lastYearFiveFlights = this.repository.findLastFiveFlights(id, pageable);
		lastFiveDestinations = lastYearFiveFlights.isEmpty() ? null : lastYearFiveFlights.stream().map(Flight::getDestinationCity).toList();

		// Money spent in bookings during last year

		Date lastYear = MomentHelper.deltaFromCurrentMoment(-1L, ChronoUnit.YEARS);
		List<Booking> lastYearBookings = this.repository.findDeltaYearBookings(id, lastYear);
		currencyUsed = lastYearBookings.isEmpty() ? "EUR" : lastYearBookings.stream().findFirst().get().getPrice().getCurrency();
		Double lastYearMoneySpentInBookingsValue = lastYearBookings.stream().mapToDouble(b -> b.getPrice().getAmount()).sum();
		lastYearMoneySpentInBookings.setAmount(lastYearMoneySpentInBookingsValue);
		lastYearMoneySpentInBookings.setCurrency(currencyUsed);

		// Number of bookings grouped by travel class

		List<Object[]> numberOfBookingsAndTravelClasses = this.repository.findNumberOfBookingsByTravelClass(id);
		if (!numberOfBookingsAndTravelClasses.isEmpty()) {
			numberOfBookingsByTravelClass = new HashMap<>();
			for (Object[] o : numberOfBookingsAndTravelClasses) {
				TravelClass travelClass = (TravelClass) o[0];
				Long count = (Long) o[1];
				numberOfBookingsByTravelClass.put(travelClass.toString(), count.intValue());
			}
		} else
			numberOfBookingsByTravelClass = null;

		// Last five year statistics

		Date lastFiveYears = MomentHelper.deltaFromCurrentMoment(-5L, ChronoUnit.YEARS);
		List<Booking> lastFiveYearBookings = this.repository.findDeltaYearBookings(id, lastFiveYears);
		lastFiveYearsBookingCostsCount.setCurrency(currencyUsed);
		lastFiveYearsBookingCostsAverage.setCurrency(currencyUsed);
		lastFiveYearsBookingCostsMinimum.setCurrency(currencyUsed);
		lastFiveYearsBookingCostsMaximum.setCurrency(currencyUsed);
		lastFiveYearsBookingCostsStandardDeviation.setCurrency(currencyUsed);
		if (!lastFiveYearBookings.isEmpty()) {
			List<Double> lastFiveYearBookingsPrices = lastFiveYearBookings.stream().map(b -> b.getPrice().getAmount()).collect(Collectors.toList());
			lastFiveYearsBookingCostsCount.setAmount(lastFiveYearBookingsPrices.stream().mapToDouble(p -> p).sum());
			lastFiveYearsBookingCostsAverage.setAmount(lastFiveYearBookingsPrices.stream().mapToDouble(p -> p).average().getAsDouble());
			lastFiveYearsBookingCostsMinimum.setAmount(lastFiveYearBookingsPrices.stream().mapToDouble(p -> p).min().getAsDouble());
			lastFiveYearsBookingCostsMaximum.setAmount(lastFiveYearBookingsPrices.stream().mapToDouble(p -> p).max().getAsDouble());
			Double lastFiveYearBookingsSquaredSum = lastFiveYearBookingsPrices.stream().mapToDouble(p -> Math.pow(p - lastFiveYearsBookingCostsAverage.getAmount(), 2)).sum();
			lastFiveYearsBookingCostsStandardDeviation.setAmount(Math.sqrt(lastFiveYearBookingsSquaredSum / (lastFiveYearBookings.size() - 1)));

			if (lastFiveYearsBookingCostsStandardDeviation.getAmount().isNaN())
				lastFiveYearsBookingCostsStandardDeviation = null;
		} else {
			lastFiveYearsBookingCostsCount.setAmount(0.);
			lastFiveYearsBookingCostsAverage.setAmount(0.);
			lastFiveYearsBookingCostsMinimum.setAmount(0.);
			lastFiveYearsBookingCostsMaximum.setAmount(0.);
			lastFiveYearsBookingCostsStandardDeviation = null;
		}

		// Number of passengers statistics

		List<Booking> bookingsFromCustomer = this.repository.findBookingsFromCustomer(id);
		if (!bookingsFromCustomer.isEmpty()) {
			List<Integer> bookingsNumberOfPassengers = bookingsFromCustomer.stream().map(b -> this.repository.findNumberOfPassengersFromBooking(b.getId())).toList();
			bookingsNumberOfPassengersCount = (int) bookingsNumberOfPassengers.stream().mapToInt(n -> n).sum();
			bookingsNumberOfPassengersAverage = bookingsNumberOfPassengers.isEmpty() ? null : bookingsNumberOfPassengers.stream().mapToInt(n -> n).average().getAsDouble();
			bookingsNumberOfPassengersMinimum = bookingsNumberOfPassengers.stream().mapToInt(n -> n).min().getAsInt();
			bookingsNumberOfPassengersMaximum = bookingsNumberOfPassengers.stream().mapToInt(n -> n).max().getAsInt();
			Double numberOfPassengersSquaredSum = bookingsNumberOfPassengers.stream().mapToInt(n -> n).mapToDouble(p -> Math.pow(p - bookingsNumberOfPassengersAverage, 2)).sum();
			bookingsNumberOfPassengersStandardDeviation = Math.sqrt(numberOfPassengersSquaredSum / (bookingsFromCustomer.size() - 1));
			if (bookingsNumberOfPassengersStandardDeviation.isNaN())
				bookingsNumberOfPassengersStandardDeviation = null;
		} else {
			bookingsNumberOfPassengersCount = 0;
			bookingsNumberOfPassengersAverage = 0.;
			bookingsNumberOfPassengersMinimum = 0;
			bookingsNumberOfPassengersMaximum = 0;
			bookingsNumberOfPassengersStandardDeviation = null;
		}

		dashboard = new CustomerDashboard();
		dashboard.setLastFiveDestinations(lastFiveDestinations);
		dashboard.setLastYearMoneySpentInBookings(lastYearMoneySpentInBookings);
		dashboard.setNumberOfBookingsByTravelClass(numberOfBookingsByTravelClass);
		dashboard.setLastFiveYearsBookingCostsCount(lastFiveYearsBookingCostsCount);
		dashboard.setLastFiveYearsBookingCostsAverage(lastFiveYearsBookingCostsAverage);
		dashboard.setLastFiveYearsBookingCostsMinimum(lastFiveYearsBookingCostsMinimum);
		dashboard.setLastFiveYearsBookingCostsMaximum(lastFiveYearsBookingCostsMaximum);
		dashboard.setLastFiveYearsBookingCostsStandardDeviation(lastFiveYearsBookingCostsStandardDeviation);
		dashboard.setBookingsNumberOfPassengersCount(bookingsNumberOfPassengersCount);
		dashboard.setBookingsNumberOfPassengersAverage(bookingsNumberOfPassengersAverage);
		dashboard.setBookingsNumberOfPassengersMinimum(bookingsNumberOfPassengersMinimum);
		dashboard.setBookingsNumberOfPassengersMaximum(bookingsNumberOfPassengersMaximum);
		dashboard.setBookingsNumberOfPassengersStandardDeviation(bookingsNumberOfPassengersStandardDeviation);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final CustomerDashboard dashboard) {
		Dataset dataset;

		dataset = super.unbindObject(dashboard, //
			"lastFiveDestinations", "lastYearMoneySpentInBookings", // 
			"numberOfBookingsByTravelClass", "lastFiveYearsBookingCostsCount", //
			"lastFiveYearsBookingCostsAverage", "lastFiveYearsBookingCostsMinimum", //
			"lastFiveYearsBookingCostsMaximum", "lastFiveYearsBookingCostsStandardDeviation", //
			"bookingsNumberOfPassengersCount", "bookingsNumberOfPassengersAverage", //
			"bookingsNumberOfPassengersMinimum", "bookingsNumberOfPassengersMaximum", //
			"bookingsNumberOfPassengersStandardDeviation");

		super.getResponse().addData(dataset);
	}

}
