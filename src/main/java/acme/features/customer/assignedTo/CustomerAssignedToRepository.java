
package acme.features.customer.assignedTo;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.mappings.AssignedTo;
import acme.entities.passenger.Passenger;

@Repository
public interface CustomerAssignedToRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(final int bookingId);

	@Query("select p from Passenger p where p.id = :passengerId")
	Passenger findPassengerById(final int passengerId);

	@Query("select at from AssignedTo at where at.id = :assignedToId")
	AssignedTo findAssignedToById(final int assignedToId);

	@Query("select at from AssignedTo at where at.booking.id = :bookingId")
	Collection<AssignedTo> findAssignedTosByBookingId(final int bookingId);

	@Query("select p from Passenger p where p.customer.id = :customerId")
	Collection<Passenger> findAllPassengersFromCustomerId(final int customerId);

	@Query("select at from AssignedTo at where at.booking.id = :bookingId and at.passenger.id = :passengerId")
	Collection<AssignedTo> findAssignationFromBookingIdAndPassengerId(final int bookingId, final int passengerId);

}
