
package acme.entities.leg;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("SELECT MIN(l.scheduledDeparture) FROM Leg l where l.flight.id = :flightId")
	public Date findScheduledDeparture(int flightId);

	@Query("SELECT MAX(l.scheduledArrival) FROM Leg l where l.flight.id = :flightId")
	public Date findScheduledArrival(int flightId);

	@Query("SELECT l.departureAirport.iataCode FROM Leg l WHERE l.flight.id = :flightId ORDER BY l.scheduledDeparture ASC")
	Collection<String> findDepartureAirport(int flightId);

	@Query("SELECT l.arrivalAirport.iataCode FROM Leg l WHERE l.flight.id = :flightId ORDER BY l.scheduledArrival DESC")
	Collection<String> findDestinationAirport(int flightId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId")
	public Collection<Leg> findLegsFromFlightId(int flightId);

	@Query("SELECT l FROM Leg l WHERE l.flightNumber = :flightNumber")
	public Leg findLegByFlightNumber(String flightNumber);

	@Query("SELECT f.airlineManager.airline.iataCode FROM Flight f WHERE f.id = :flightId")
	public String getIataCodeFromFlightId(int flightId);

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> findLegsByFlightId(int flightId);

	@Query("select l from Leg l where l.flight.id = :flightId and l.draftMode = false")
	Collection<Leg> findPublishedLegsByFlightId(int flightId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId AND l.id <> :legId AND (:scheduledDeparture BETWEEN l.scheduledDeparture AND l.scheduledArrival OR l.scheduledDeparture BETWEEN :scheduledDeparture AND :scheduledArrival)")
	Collection<Leg> findOverlappingLegs(int flightId, int legId, Date scheduledDeparture, Date scheduledArrival);

	@Query("select l from Leg l where l.aircraft.id = :aircraftId and l.id <> :legId and (:scheduledDeparture BETWEEN l.scheduledDeparture AND l.scheduledArrival OR l.scheduledDeparture BETWEEN :scheduledDeparture AND :scheduledArrival)")
	Collection<Leg> findLegByAircraftIdSameTime(int aircraftId, int legId, Date scheduledDeparture, Date scheduledArrival);

	@Query("select l from Leg l where l.departureAirport.id = :departureAirportId and l.id <> :legId and l.scheduledDeparture = :scheduledDeparture")
	Collection<Leg> findLegByAirportIdSameDeparture(int departureAirportId, int legId, Date scheduledDeparture);

	@Query("select l from Leg l where l.arrivalAirport.id = :arrivalAirportId and l.id <> :legId and l.scheduledArrival = :scheduledArrival")
	Collection<Leg> findLegByAirportIdSameArrival(int arrivalAirportId, int legId, Date scheduledArrival);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId AND l.scheduledDeparture >= :scheduledArrival ORDER BY l.scheduledDeparture ASC")
	Collection<Leg> findNextLeg(int flightId, Date scheduledArrival);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId AND l.scheduledArrival <= :scheduledDeparture ORDER BY l.scheduledArrival DESC")
	Collection<Leg> findPreviousLeg(int flightId, Date scheduledDeparture);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId AND l.id <> :legId AND (:scheduledArrival BETWEEN l.scheduledDeparture AND l.scheduledArrival)")
	Collection<Leg> findOverlappingLegSa(int flightId, int legId, Date scheduledArrival);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId AND l.id <> :legId AND (:scheduledDeparture BETWEEN l.scheduledDeparture AND l.scheduledArrival)")
	Collection<Leg> findOverlappingLegSd(int flightId, int legId, Date scheduledDeparture);
}
