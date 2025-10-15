
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.entities.claim.Claim;
import acme.entities.leg.Leg;
import acme.entities.trackingLog.TrackingLog;
import acme.realms.assistanceAgent.AssistanceAgent;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where (c.status=acme.entities.claim.ClaimStatus.ACCEPTED or c.status = acme.entities.claim.ClaimStatus.DENIED) and c.assistanceAgent.id = :assistanceAgentId")
	Collection<Claim> findCompletedClaimsByAssistanceAgentId(int assistanceAgentId);

	@Query("select c from Claim c where c.status = acme.entities.claim.ClaimStatus.PENDING and c.assistanceAgent.id = :assistanceAgentId")
	Collection<Claim> findUndergoingClaimsByAssistanceAgentId(int assistanceAgentId);

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("select c from Claim c where c.assistanceAgent.id = :assistanceAgentId")
	Collection<Claim> findAllClaimsByAssistanceAgentId(int assistanceAgentId);

	@Query("select a from AssistanceAgent a where a.id = :assistanceAgentId")
	AssistanceAgent findAssistanceAgentById(int assistanceAgentId);

	@Query("select l from Leg l where l.draftMode = false")
	Collection<Leg> findPublishedLegs();

	@Query("select l from Leg l where l.id = :legId")
	Leg findPublishedLegById(int legId);

	@Query("select t from TrackingLog t where t.claim.id = :claimId")
	Collection<TrackingLog> findAllTrackingLogsByClaimId(int claimId);

	@Query("select c from Claim c left join fetch c.leg where c.id = :id")
	Claim findClaimWithLegById(int id);

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("SELECT l FROM Leg l WHERE l.id = :id")
	Leg findLegById(int id);

	@Query("SELECT l from Leg l WHERE l.scheduledArrival < CURRENT_TIMESTAMP and l.draftMode = false and l.aircraft.airline = :agentAirline and l.id = :id")
	Leg findValidLegById(Airline agentAirline, int id);

}
