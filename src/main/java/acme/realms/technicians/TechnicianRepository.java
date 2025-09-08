
package acme.realms.technicians;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TechnicianRepository extends AbstractRepository {

	@Query("select t from Technician t where t.licenseNumber = :licenseNumber")
	Technician findTechnicianByLicenseNumber(String licenseNumber);

}
