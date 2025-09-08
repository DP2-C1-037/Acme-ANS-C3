
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicianDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	private Map<String, Integer>	maintenanceRecordsByStatus;
	private String					nearestInspectionMaintenanceRecord;
	private List<String>			higherTaskNumberAircrafts;
	private Money					lastYearAverageMaintenanceCost;
	private Money					lastYearMaxMaintenanceCost;
	private Money					lastYearMinMaintenanceCost;
	private Money					lastYearStandardDeviationMaintenanceCost;
	private Double					taskAverageDuration;
	private Double					taskMaxDuration;
	private Double					tasgMinDuration;
	private Double					taskStandardDeviationDuration;

}
