
package acme.entities.claim;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.trackingLog.TrackingLog;

public interface ClaimRepository extends AbstractRepository {

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = ?1 ORDER BY t.resolPercentage ASC")
	List<TrackingLog> getTrackingLogsByResolutionOrder(Integer claimId);
}
