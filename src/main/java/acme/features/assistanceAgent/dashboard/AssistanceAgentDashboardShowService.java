
package acme.features.assistanceAgent.dashboard;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.datatypes.StatisticsAssistanceAgent;
import acme.forms.AssistanceAgentDashboard;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentDashboardShowService extends AbstractGuiService<AssistanceAgent, AssistanceAgentDashboard> {

	@Autowired
	private AssistanceAgentDashboardRepository repository;


	@Override
	public void authorise() {
		boolean status;
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
		status = super.getRequest().getPrincipal().hasRealm(agent);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		AssistanceAgentDashboard dashboard;
		Double resolvedClaims;
		Double rejectedClaims;
		List<String> topThreeMonths;
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();

		//The ratio of claims that have been resolved successfully.  
		resolvedClaims = this.repository.resolvedRatio(agent);

		//The ratio of claims that have been rejected.
		rejectedClaims = this.repository.rejectedRatio(agent);

		//The top three months with the highest number of claims.  
		topThreeMonths = this.repository.topThreeMonths(agent);

		//The average, minimum, maximum, and standard deviation of the number of logs their claims have.
		StatisticsAssistanceAgent logsStatistics = new StatisticsAssistanceAgent();
		logsStatistics.setAverage(this.repository.averageNumberOfLogs(agent));
		logsStatistics.setMinimum(this.repository.minimumNumberOfLogs(agent));
		logsStatistics.setMaximum(this.repository.maximumNumberOfLogs(agent));
		logsStatistics.setDeviation(this.repository.deviationNumberOfLogs(agent));

		dashboard = new AssistanceAgentDashboard();
		dashboard.setResolvedClaims(resolvedClaims);
		dashboard.setRejectedClaims(rejectedClaims);
		dashboard.setTopThreeMonths(topThreeMonths);
		dashboard.setLogsStatistics(logsStatistics);

		// ////////////////////////////////////////////////////////////////////// 

		Date moment = MomentHelper.getCurrentMoment();
		LocalDate localDateMoment = moment.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		int currentMonth = localDateMoment.getMonthValue();
		int currentYear = localDateMoment.getYear();

		int previousMonth = currentMonth == 1 ? 12 : currentMonth - 1;
		int previousYear = currentMonth == 1 ? currentYear - 1 : currentYear;

		LocalDate startOfPreviousMonth = LocalDate.of(previousYear, previousMonth, 1);
		LocalDate endOfPreviousMonth = startOfPreviousMonth.withDayOfMonth(startOfPreviousMonth.lengthOfMonth());

		Date startDate = Date.from(startOfPreviousMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfPreviousMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

		List<Object[]> monthlyCounts = this.repository.getMonthlyClaimsCounts(agent, startDate, endDate);

		List<Double> counts = monthlyCounts.stream().map(obj -> ((Number) obj[2]).doubleValue()).collect(Collectors.toList());

		StatisticsAssistanceAgent statsClaimsLastMonth = new StatisticsAssistanceAgent();

		if (!counts.isEmpty()) {
			DoubleSummaryStatistics stats = counts.stream().mapToDouble(Double::doubleValue).summaryStatistics();

			statsClaimsLastMonth.setAverage(stats.getAverage());
			statsClaimsLastMonth.setMinimum(stats.getMin());
			statsClaimsLastMonth.setMaximum(stats.getMax());
			double variance = counts.stream().mapToDouble(count -> Math.pow(count - stats.getAverage(), 2)).average().orElse(0.0);
			statsClaimsLastMonth.setDeviation(Math.sqrt(variance));
		} else {
			statsClaimsLastMonth.setAverage(0.0);
			statsClaimsLastMonth.setMinimum(0.0);
			statsClaimsLastMonth.setMaximum(0.0);
			statsClaimsLastMonth.setDeviation(0.0);
		}

		dashboard.setAssistedStatistics(statsClaimsLastMonth);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final AssistanceAgentDashboard dashboard) {
		Dataset dataset;

		dataset = super.unbindObject(dashboard, "resolvedClaims", "rejectedClaims", "topThreeMonths", "logsStatistics", "assistedStatistics");

		super.getResponse().addData(dataset);
	}

}
