
package acme.features.assistanceAgent.dashboard;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.realms.assistanceAgent.AssistanceAgent;

@Repository
public interface AssistanceAgentDashboardRepository extends AbstractRepository {

	@Query("SELECT DISTINCT c FROM Claim c JOIN TrackingLog t ON t.claim.id = c.id WHERE t.resolPercentage = (SELECT MAX(t2.resolPercentage) FROM TrackingLog t2 WHERE t2.claim = c) AND (t.status = 'ACCEPTED' AND c.assistanceAgent.id = :agentId)")
	Collection<Claim> findAllAcceptedClaimsByAgentId(int agentId);

	@Query("SELECT c FROM Claim c WHERE c.assistanceAgent.id = :agentId")
	Collection<Claim> findAllClaims(int agentId);

	default Double resolvedRatio(final AssistanceAgent assistanceAgent) {
		int total = this.findAllClaims(assistanceAgent.getId()).size();
		int res = this.findAllAcceptedClaimsByAgentId(assistanceAgent.getId()).size();
		return total > 0 ? 100.0 * ((double) res / total) : 0.0;
	}

	@Query("SELECT DISTINCT c FROM Claim c JOIN TrackingLog t ON t.claim.id = c.id WHERE t.resolPercentage = (SELECT MAX(t2.resolPercentage) FROM TrackingLog t2 WHERE t2.claim = c) AND (t.status = 'REJECTED' AND c.assistanceAgent.id = :agentId)")
	List<Claim> findAllRejectedClaimsByAgentId(int agentId);

	default Double rejectedRatio(final AssistanceAgent assistanceAgent) {
		int total = this.findAllClaims(assistanceAgent.getId()).size();
		int res = this.findAllRejectedClaimsByAgentId(assistanceAgent.getId()).size();
		return total > 0 ? 100.0 * ((double) res / total) : 0.0;
	}

	@Query("SELECT FUNCTION('MONTHNAME', c.registrationMoment) as month, COUNT(c) as count " + "FROM Claim c WHERE c.assistanceAgent = :agent " + "GROUP BY FUNCTION('MONTH', c.registrationMoment), month " + "ORDER BY COUNT(c) DESC")
	List<Object[]> findMonths(AssistanceAgent agent);

	default List<String> topThreeMonths(final AssistanceAgent agent) {
		return this.findMonths(agent).stream().limit(3).map(arr -> (String) arr[0]).toList();
	}

	@Query("SELECT AVG((SELECT COUNT(t) FROM TrackingLog t WHERE t.claim = c)) FROM Claim c WHERE c.assistanceAgent = :agent")
	Double averageNumberOfLogs(AssistanceAgent agent);

	@Query("SELECT MIN((SELECT COUNT(t) FROM TrackingLog t WHERE t.claim = c)) FROM Claim c WHERE c.assistanceAgent = :agent")
	Double minimumNumberOfLogs(AssistanceAgent agent);

	@Query("SELECT MAX((SELECT COUNT(t) FROM TrackingLog t WHERE t.claim = c)) FROM Claim c WHERE c.assistanceAgent = :agent")
	Double maximumNumberOfLogs(AssistanceAgent agent);

	@Query("SELECT STDDEV((SELECT COUNT(t) FROM TrackingLog t WHERE t.claim = c)) FROM Claim c WHERE c.assistanceAgent = :agent")
	Double deviationNumberOfLogs(AssistanceAgent agent);

	@Query("SELECT COUNT(c) FROM Claim c WHERE c.assistanceAgent = :assistanceAgent AND YEAR(c.registrationMoment) = :year AND MONTH(c.registrationMoment) = :month")
	Integer countClaimsByMonth(AssistanceAgent assistanceAgent, int year, int month);

	@Query("SELECT YEAR(c.registrationMoment) as year, MONTH(c.registrationMoment) as month, COUNT(c) as count " + "FROM Claim c WHERE c.assistanceAgent = :assistanceAgent AND c.registrationMoment BETWEEN :startDate AND :endDate "
		+ "GROUP BY YEAR(c.registrationMoment), MONTH(c.registrationMoment)")
	List<Object[]> getMonthlyClaimsCounts(AssistanceAgent assistanceAgent, Date startDate, Date endDate);

}
