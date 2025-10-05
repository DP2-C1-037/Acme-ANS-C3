
package acme.features.assistanceAgent.trackingLog;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Principal;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogStatus;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogUpdate extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal State --------------------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;

	// AbstractGuiService ----------------------------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int trackingLogId;
		int currentAssistanceAgentId;
		Principal principal;
		TrackingLog trackingLog;

		principal = super.getRequest().getPrincipal();

		currentAssistanceAgentId = principal.getActiveRealm().getId();
		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(trackingLogId);

		status = principal.hasRealmOfType(AssistanceAgent.class) && trackingLog.getClaim().getAssistanceAgent().getId() == currentAssistanceAgentId && trackingLog.isDraftMode();
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		if (trackingLog == null || trackingLog.getClaim() == null)
			return;

		Double currentResol = trackingLog.getResolPercentage();

		// Validación básica de porcentaje
		if (currentResol == null) {
			super.state(false, "resolPercentage", "assistance-agent.tracking-log.form.error.resolPercentageNull");
			return;
		}
		if (currentResol < 0.0) {
			super.state(false, "resolPercentage", "assistance-agent.tracking-log.form.error.resolPercentageNegative");
			return;
		}
		if (currentResol > 100.0) {
			super.state(false, "resolPercentage", "assistance-agent.tracking-log.form.error.resolPercentageOver");
			return;
		}

		int claimId = trackingLog.getClaim().getId();
		int trackingLogId = trackingLog.getId();
		Date creationMoment = trackingLog.getCreationMoment();

		// Obtener todos los logs ordenados por creationMoment e id
		List<TrackingLog> logs = this.repository.findAllTrackingLogsByClaimId(claimId).stream().sorted((a, b) -> {
			int cmp = a.getCreationMoment().compareTo(b.getCreationMoment());
			if (cmp == 0)
				return Integer.compare(a.getId(), b.getId());
			return cmp;
		}).toList();

		TrackingLog previous = null;
		TrackingLog next = null;

		// Buscar previous y next
		for (int i = 0; i < logs.size(); i++) {
			TrackingLog t = logs.get(i);
			if (t.getId() == trackingLogId) {
				if (i > 0)
					previous = logs.get(i - 1);
				if (i < logs.size() - 1)
					next = logs.get(i + 1);
				break;
			}
		}

		// Detectar el último log publicado al 100%
		TrackingLog last100 = logs.stream().filter(l -> Double.compare(l.getResolPercentage(), 100.0) == 0).max((a, b) -> {
			int cmp = a.getCreationMoment().compareTo(b.getCreationMoment());
			if (cmp == 0)
				return Integer.compare(a.getId(), b.getId());
			return cmp;
		}).orElse(null);

		boolean isPost100 = last100 != null && (creationMoment.after(last100.getCreationMoment()) || creationMoment.equals(last100.getCreationMoment()) && trackingLogId > last100.getId());

		// Si el anterior NO es el 100%, debe ser estrictamente mayor que el anterior
		double min;
		if (isPost100 && previous != null && previous.getResolPercentage() == 100.0)
			min = 0.0; // Permitir reiniciar después del 100%
		else if (previous != null)
			min = previous.getResolPercentage(); // Debe ser mayor que el anterior
		else
			min = 0.0; // Primer log

		double max = next != null ? next.getResolPercentage() : 100.0;

		boolean valid;
		if (min == 0.0)
			valid = currentResol >= min && currentResol <= max;
		else
			valid = currentResol > min && currentResol <= max;

		// Aplicar errores
		if (!valid)
			if (previous != null && currentResol <= previous.getResolPercentage() && !(isPost100 && previous.getResolPercentage() == 100.0))
				super.state(false, "resolPercentage", "assistance-agent.tracking-log.form.error.resolPercentageLowerThanPrevious");
			else if (next != null && currentResol > next.getResolPercentage())
				super.state(false, "resolPercentage", "assistance-agent.tracking-log.form.error.resolPercentageHigherThanNext");
			else
				super.state(false, "resolPercentage", "assistance-agent.tracking-log.form.error.resolPercentageOrder");
	}

	@Override
	public void load() {
		int trackingLogId;
		TrackingLog trackingLog;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(trackingLogId);

		super.getBuffer().addData(trackingLog);

	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolPercentage", "status", "resolution");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		Date currentMoment;

		currentMoment = MomentHelper.getCurrentMoment();

		trackingLog.setLastUpdateMoment(currentMoment);
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		SelectChoices status;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolPercentage", "status", "resolution");
		status = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("status", status);

		super.getResponse().addData(dataset);
	}
}
