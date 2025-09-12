
package acme.features.any;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;

@Repository
public interface AnyFlightAssignmentRepository extends AbstractRepository {

	@Query("select f from FlightAssignment f where f.draftMode = false")
	Collection<FlightAssignment> findPublishedFlightAssignments();

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);
}
