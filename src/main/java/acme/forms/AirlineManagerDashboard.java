
package acme.forms;

import java.util.Map;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirlineManagerDashboard extends AbstractForm {

	private static final long		serialVersionUID	= 1L;

	private Double					ranking;

	private Integer					yearsToRetire;

	private Double					onTimeRatio;

	private Map<String, Integer>	mostPopularAirports;

	private Map<String, Integer>	leastPopularAirports;

	private Map<String, Integer>	legsByStatus;

	private Double					averageCost;

	private Double					minCost;

	private Double					maxCost;

	private Double					standardDeviationCost;
}
