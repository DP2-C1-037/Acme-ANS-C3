
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.mappings.AssignedTo;
import acme.entities.passenger.Passenger;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("select p from Passenger p where p.id = :passengerId")
	Passenger findPassengerById(final int passengerId);

	@Query("select p from Passenger p where p.customer.id = :customerId")
	Collection<Passenger> findPassengersByCustomerId(final int customerId);

	@Query("select at from AssignedTo at where at.passenger.id = :passengerId")
	Collection<AssignedTo> findAllAssignedToByPassengerId(final int passengerId);

	@Query("select p from Passenger p where p.customer.id = :customerId and p.passportNumber = :passportNumber")
	Passenger findPassengerByCustomerIdAndPassportNumber(final int customerId, final String passportNumber);

}
