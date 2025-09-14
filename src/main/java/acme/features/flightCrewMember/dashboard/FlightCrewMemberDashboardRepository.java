
package acme.features.flightCrewMember.dashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.datatypes.AssignmentStatus;
import acme.entities.flightAssignment.FlightAssignment;

@Repository
public interface FlightCrewMemberDashboardRepository extends AbstractRepository {

	@Query("SELECT fa FROM FlightAssignment fa JOIN fa.leg l WHERE fa.flightCrewMember.id = :flightCrewMemberId ORDER BY l.scheduledArrival DESC")
	List<FlightAssignment> findFlightAssignments(int flightCrewMemberId, Pageable pageable);

	@Query("SELECT fa.leg.scheduledArrival FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :flightCrewMemberId AND fa.leg.scheduledArrival >= :startDate")
	List<Date> findFlightAssignmentArrivals(int flightCrewMemberId, Date startDate);

	@Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.flightAssignment.flightCrewMember.id = :memberId AND al.severityLevel >= :minSeverity AND al.severityLevel <= :maxSeverity")
	Integer countLegsWithSeverity(int memberId, int minSeverity, int maxSeverity);

	@Query("SELECT fa.flightCrewMember.userAccount.username FROM FlightAssignment fa WHERE fa.leg.id = :legId AND fa.flightCrewMember.id <> :memberId GROUP BY fa.flightCrewMember.userAccount.username")
	List<String> findLastLegCrewMembers(int legId, int memberId);

	@Query("SELECT l.arrivalAirport.city FROM FlightAssignment fa JOIN fa.leg l WHERE fa.flightCrewMember.id = :memberId ORDER BY fa.lastUpdateMoment DESC")
	List<String> findLastFiveDestinations(int memberId, Pageable pageable);

	@Query("SELECT COUNT(fa) FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :flightCrewMemberId AND fa.status = :status")
	int countFlightAssignmentsByStatus(int flightCrewMemberId, AssignmentStatus status);
}
