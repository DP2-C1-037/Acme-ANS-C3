
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.flight.Flight;
import acme.entities.mappings.AssignedTo;
import acme.entities.passenger.Passenger;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(final int bookingId);

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findBookingsByCustomerId(final int customerId);

	@Query("select f from Flight f where f.draftMode = false")
	Collection<Flight> findAllFlightsPublished();

	@Query("select count(at) from AssignedTo at where at.booking.id = :bookingId")
	Integer findNumberOfPassengersAssignedToBookingById(final int bookingId);

	@Query("select b from Booking b where b.locatorCode = :locatorCode")
	Booking findBookingByLocatorCode(final String locatorCode);

	@Query("select at from AssignedTo at where at.booking.id = :bookingId")
	Collection<AssignedTo> findAllAssignedToByBookingId(final int bookingId);

	@Query("select f from Flight f where f.id = :flightId")
	Flight findFlightById(final int flightId);

	@Query("select at.passenger from AssignedTo at where at.booking.id = :bookingId")
	Collection<Passenger> findAllPassengersFromBookingById(final int bookingId);

}
