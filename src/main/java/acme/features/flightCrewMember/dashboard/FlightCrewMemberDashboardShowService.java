
package acme.features.flightCrewMember.dashboard;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.datatypes.AssignmentStatus;
import acme.entities.flightAssignment.FlightAssignment;
import acme.forms.FlightCrewMemberDashboard;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightCrewMemberDashboardShowService extends AbstractGuiService<FlightCrewMember, FlightCrewMemberDashboard> {

	@Autowired
	private FlightCrewMemberDashboardRepository repository;


	@Override
	public void authorise() {
		FlightCrewMember member;
		boolean status;

		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		status = super.getRequest().getPrincipal().hasRealm(member);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightCrewMemberDashboard dashboard;
		FlightCrewMember member;
		int memberId;

		dashboard = new FlightCrewMemberDashboard();
		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		memberId = member.getId();

		dashboard.setLastFiveDestinations(this.fetchLastFiveDestinations(memberId));
		dashboard.setIncidentCountsBySeverity(this.fetchIncidentCounts(memberId));
		dashboard.setLastLegCrewMembers(this.fetchLastLegCrewMembers(memberId));
		dashboard.setFlightAssignmentsByStatus(this.fetchAssignmentsByStatus(memberId));
		this.setAssignmentStatistics(dashboard, memberId);

		super.getBuffer().addData(dashboard);
	}

	private List<String> fetchLastFiveDestinations(final int memberId) {
		return this.repository.findLastFiveDestinations(memberId, PageRequest.of(0, 5));
	}

	private Map<String, Integer> fetchIncidentCounts(final int memberId) {
		Map<String, Integer> incidents = new HashMap<>();
		incidents.put("0-3", this.repository.countLegsWithSeverity(memberId, 0, 3));
		incidents.put("4-7", this.repository.countLegsWithSeverity(memberId, 4, 7));
		incidents.put("8-10", this.repository.countLegsWithSeverity(memberId, 8, 10));

		return incidents;
	}

	private List<String> fetchLastLegCrewMembers(final int memberId) {
		List<FlightAssignment> lastAssignments = this.repository.findFlightAssignments(memberId, PageRequest.of(0, 1));
		if (!lastAssignments.isEmpty()) {
			FlightAssignment last = lastAssignments.get(0);
			return this.repository.findLastLegCrewMembers(last.getLeg().getId(), memberId);
		} else
			return List.of();
	}

	private Map<String, Integer> fetchAssignmentsByStatus(final int memberId) {
		Map<String, Integer> assignmentsByStatus = new HashMap<>();
		for (AssignmentStatus s : AssignmentStatus.values())
			assignmentsByStatus.put(s.name(), this.repository.countFlightAssignmentsByStatus(memberId, s));
		return assignmentsByStatus;
	}

	private void setAssignmentStatistics(final FlightCrewMemberDashboard dashboard, final int memberId) {
		LocalDate startLocalDate = LocalDate.now().minusMonths(10);
		Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

		List<Date> arrivals = this.repository.findFlightAssignmentArrivals(memberId, startDate);

		Map<YearMonth, Long> counts = arrivals.stream().map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).collect(Collectors.groupingBy(d -> YearMonth.of(d.getYear(), d.getMonth()), Collectors.counting()));

		List<Integer> flightsPerMonth = counts.values().stream().map(Long::intValue).toList();

		double avg = flightsPerMonth.stream().mapToInt(Integer::intValue).average().orElse(0.0);
		int min = flightsPerMonth.stream().mapToInt(Integer::intValue).min().orElse(0);
		int max = flightsPerMonth.stream().mapToInt(Integer::intValue).max().orElse(0);

		double stdDev = 0.0;
		if (flightsPerMonth.size() > 1) {
			double mean = avg;
			double variance = flightsPerMonth.stream().mapToDouble(n -> Math.pow(n - mean, 2)).sum() / (flightsPerMonth.size() - 1);
			stdDev = Math.sqrt(variance);
		}

		dashboard.setAverageFlightAssignments(avg);
		dashboard.setMinFlightAssignments(min);
		dashboard.setMaxFlightAssignments(max);
		dashboard.setStdDevFlightAssignments(stdDev);
	}

	@Override
	public void unbind(final FlightCrewMemberDashboard dashboard) {
		Dataset dataset = super.unbindObject(dashboard, "lastFiveDestinations", "incidentCountsBySeverity", "lastLegCrewMembers", "flightAssignmentsByStatus", "averageFlightAssignments", "minFlightAssignments", "maxFlightAssignments",
			"stdDevFlightAssignments");

		super.getResponse().addData(dataset);
	}
}
