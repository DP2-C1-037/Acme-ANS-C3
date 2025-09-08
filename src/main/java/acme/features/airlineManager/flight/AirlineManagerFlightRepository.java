
package acme.features.airlineManager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.airline.AirlineManager;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;

public interface AirlineManagerFlightRepository extends AbstractRepository {

	@Query("SELECT f FROM Flight f WHERE f.id = :flightId")
	Flight findFlightById(int flightId);

	@Query("select f from Flight f where f.airlineManager.id = :managerId")
	Collection<Flight> findAllFlightsByAirlineManagerId(int managerId);

	@Query("SELECT am FROM AirlineManager am")
	Collection<AirlineManager> findAllManagers();

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> findLegsByFlightId(int flightId);

	@Query("select l from Leg l where l.flight.id = :flightId and l.draftMode = false")
	Collection<Leg> findPublishedLegsByFlightId(int flightId);

	@Modifying
	@Query("delete from Leg l where l.flight.id = :flightId and l.draftMode = true")
	void deleteUnpublishedLegsByFlightId(int flightId);
}
