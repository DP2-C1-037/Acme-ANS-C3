
package acme.features.airlineManager.leg;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.airports.Airport;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;

public interface AirlineManagerLegRepository extends AbstractRepository {

	// Legs
	@Query("SELECT l FROM Leg l WHERE l.id = :legId")
	Leg findLegById(int legId);

	@Query("SELECT l FROM Leg l WHERE l.flight.airlineManager.id = :airlineManagerId")
	Collection<Leg> findAllLegsByAirlineManagerId(int airlineManagerId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId")
	Collection<Leg> findAllLegsByFlightId(int flightId);

	// Flights
	@Query("SELECT f FROM Flight f WHERE f.id = :flightId")
	Flight findFlightById(int flightId);

	@Query("SELECT f FROM Flight f")
	Collection<Flight> findAllFlights();

	@Query("SELECT f FROM Flight f WHERE f.airlineManager.id = :managerId")
	Collection<Flight> findAllFlightsByAirlineManagerId(int managerId);

	// Aircrafts and airports
	@Query("SELECT a FROM Aircraft a where a.id = :aircraftId")
	Aircraft findAircraftById(int aircraftId);

	@Query("SELECT a FROM Airport a where a.id = :airportId")
	Airport findAirportById(int airportId);

	@Query("SELECT a FROM Aircraft a")
	Collection<Aircraft> findAllAircrafts();

	@Query("SELECT a FROM Airport a")
	Collection<Airport> findAllAirports();
}
