
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightCrewMemberDashboard extends AbstractForm {

	private static final long		serialVersionUID	= 1L;

	private List<String>			lastFiveDestinations;
	private Map<String, Integer>	incidentCountsBySeverity; // Example: {"0-3": 5, "4-7": 2, "8-10": 1}
	private List<String>			lastLegCrewMembers;
	private Map<String, Integer>	flightAssignmentsByStatus;
	private Double					averageFlightAssignments;
	private Integer					minFlightAssignments;
	private Integer					maxFlightAssignments;
	private Double					stdDevFlightAssignments;
}
