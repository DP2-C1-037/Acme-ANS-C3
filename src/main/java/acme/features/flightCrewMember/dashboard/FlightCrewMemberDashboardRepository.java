
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

	@Query("SELECT fa FROM FlightAssignment fa JOIN fa.leg l WHERE fa.flightCrewMember.id = :memberId ORDER BY l.scheduledArrival DESC")
	List<FlightAssignment> findAssignmentsByCrewMember(int memberId, Pageable pageable);

	@Query("SELECT fa.leg.scheduledArrival FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :memberId AND fa.leg.scheduledArrival >= :fromDate")
	List<Date> findUpcomingAssignmentArrivals(int memberId, Date fromDate);

	@Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.flightAssignment.flightCrewMember.id = :memberId AND al.severityLevel >= :minLevel AND al.severityLevel <= :maxLevel")
	Integer countAssignmentsBySeverityRange(int memberId, int minLevel, int maxLevel);

	@Query("SELECT fa.flightCrewMember.userAccount.username FROM FlightAssignment fa WHERE fa.leg.id = :legId AND fa.flightCrewMember.id <> :memberId GROUP BY fa.flightCrewMember.userAccount.username")
	List<String> findOtherCrewMembersInLeg(int legId, int memberId);

	@Query("SELECT l.arrivalAirport.city FROM FlightAssignment fa JOIN fa.leg l WHERE fa.flightCrewMember.id = :memberId ORDER BY fa.lastUpdateMoment DESC")
	List<String> findRecentDestinations(int memberId, Pageable pageable);

	@Query("SELECT COUNT(fa) FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :memberId AND fa.status = :status")
	int countAssignmentsByStatus(int memberId, AssignmentStatus status);
}
