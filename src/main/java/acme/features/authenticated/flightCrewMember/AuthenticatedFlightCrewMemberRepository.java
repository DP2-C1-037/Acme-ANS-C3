
package acme.features.authenticated.flightCrewMember;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface AuthenticatedFlightCrewMemberRepository extends AbstractRepository {

	@Query("select ua from UserAccount ua where ua.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select a from Airline a")
	Collection<Airline> findAllAirlines();

	@Query("select f from FlightCrewMember f where f.id = :id")
	FlightCrewMember findFlightCrewMemberById(int id);

	@Query("select f from FlightCrewMember f where f.userAccount.id = :id")
	FlightCrewMember findFlightCrewMemberByUserAccountId(int id);

}
