
package acme.features.assistanceAgent.trackingLog;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.trackingLog.TrackingLog;

@Repository
public interface AssistanceAgentTrackingLogRepository extends AbstractRepository {

	@Query("select t from TrackingLog t where t.claim.id = :claimId")
	Collection<TrackingLog> findAllTrackingLogsByClaimId(int claimId);

	@Query("select t from TrackingLog t where t.id = :trackingLogId")
	TrackingLog findTrackingLogById(int trackingLogId);

	@Query("select c from Claim c where c.id = :claimId")
	Claim findClaimById(int claimId);

	@Query("select t from TrackingLog t where t.claim.id = :claimId order by t.resolPercentage desc")
	Collection<TrackingLog> findAllTrackingLogsByClaimIdOrderedByResolPercentageDesc(int claimId);

	@Query("""
		SELECT t FROM TrackingLog t
		WHERE t.claim.id = :claimId
		  AND (t.creationMoment < :creationMoment
		       OR (t.creationMoment = :creationMoment AND t.id < :id))
		ORDER BY t.creationMoment DESC, t.id DESC
		""")
	List<TrackingLog> findPreviousTrackingLog(@Param("claimId") int claimId, @Param("creationMoment") Date creationMoment, @Param("id") int id);

	@Query("""
		SELECT t FROM TrackingLog t
		WHERE t.claim.id = :claimId
		  AND (t.creationMoment > :creationMoment
		       OR (t.creationMoment = :creationMoment AND t.id > :id))
		ORDER BY t.creationMoment ASC, t.id ASC
		""")
	List<TrackingLog> findNextTrackingLog(@Param("claimId") int claimId, @Param("creationMoment") Date creationMoment, @Param("id") int id);

	TrackingLog findFirstByClaimIdOrderByResolPercentageDesc(int claimId);
}
