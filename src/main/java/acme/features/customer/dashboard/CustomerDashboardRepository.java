
package acme.features.customer.dashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.flight.Flight;

@Repository
public interface CustomerDashboardRepository extends AbstractRepository {

	@Query("select b.flight from Booking b where b.customer.id=:customerId and b.draftMode = false order by b.purchaseMoment desc")
	List<Flight> findLastFiveFlights(int customerId, Pageable pageable);

	@Query("select b from Booking b where b.customer.id=:customerId and b.purchaseMoment >= :yearsDelta and b.draftMode = false ")
	List<Booking> findDeltaYearBookings(int customerId, Date yearsDelta);

	@Query("select b.travelClass, count(b) from Booking b where b.customer.id=:customerId and b.draftMode = false group by b.travelClass")
	List<Object[]> findNumberOfBookingsByTravelClass(int customerId);

	@Query("select b from Booking b where b.customer.id=:customerId and b.draftMode = false")
	List<Booking> findBookingsFromCustomer(int customerId);

	@Query("select count(a) from AssignedTo a where a.booking.id=:bookingId")
	Integer findNumberOfPassengersFromBooking(int bookingId);

}
