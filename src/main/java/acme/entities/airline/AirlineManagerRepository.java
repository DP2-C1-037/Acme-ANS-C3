
package acme.entities.airline;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirlineManagerRepository extends AbstractRepository {

	@Query("SELECT am FROM AirlineManager am WHERE am.identifierNumber = :identifierNumber")
	public AirlineManager findAirlineManagerByIdentifierNumber(String identifierNumber);

}
