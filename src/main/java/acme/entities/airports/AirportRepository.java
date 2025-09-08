
package acme.entities.airports;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirportRepository extends AbstractRepository {

	@Query("select a from Airport a where BINARY(a.iataCode) = :code")
	Airport findAirportByCode(String code);
}
