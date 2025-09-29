
package acme.features.assistanceAgent.trackingLog;

import java.util.Collection;

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

	@Query("select t from TrackingLog t " + "where t.claim.id = :claimId and t.creationMoment <= :currentMoment and t.id <> :trackingLogId " + "order by t.creationMoment desc")
	TrackingLog findPreviousTrackingLog(@Param("claimId") int claimId, @Param("currentMoment") java.util.Date currentMoment, @Param("trackingLogId") int trackingLogId);

}
