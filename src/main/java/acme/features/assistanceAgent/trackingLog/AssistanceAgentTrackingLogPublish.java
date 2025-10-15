
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
import acme.entities.claim.Claim;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogStatus;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogPublish extends AbstractGuiService<AssistanceAgent, TrackingLog> {

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
	public void validate(final TrackingLog trackingLog) {

		Claim claim = trackingLog.getClaim();

		// Validar que el claim no esté en draft
		if (claim.isDraftMode())
			super.state(false, "*", "assistance-agent.tracking-log.form.error.claimDraftMode");

		// Validación básica de porcentaje
		Double currentResol = trackingLog.getResolPercentage();
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

		int claimId = claim.getId();
		int trackingLogId = trackingLog.getId();

		// Todos los logs del claim ordenados
		List<TrackingLog> logs = this.repository.findAllTrackingLogsByClaimId(claimId).stream().sorted((a, b) -> {
			int cmp = a.getCreationMoment().compareTo(b.getCreationMoment());
			if (cmp == 0)
				return Integer.compare(a.getId(), b.getId());
			return cmp;
		}).toList();

		// --- MISMA LÓGICA DE BLOQUES QUE EN UPDATE ---
		TrackingLog previousInBlock = null;
		TrackingLog nextInBlock = null;

		for (int i = 0; i < logs.size(); i++) {
			TrackingLog t = logs.get(i);

			if (t.getId() == trackingLogId) {
				// Buscar previous dentro del mismo bloque (antes del siguiente 100%)
				for (int j = i - 1; j >= 0; j--) {
					TrackingLog candidate = logs.get(j);
					if (candidate.getResolPercentage() == 100.0)
						break;
					previousInBlock = candidate;
					break;
				}
				// Buscar next dentro del mismo bloque (hasta el siguiente 100%)
				for (int j = i + 1; j < logs.size(); j++) {
					TrackingLog candidate = logs.get(j);
					if (candidate.getResolPercentage() == 100.0)
						break; // ya se corta el bloque
					if (nextInBlock == null)
						nextInBlock = candidate; // el primero tras el actual
				}

				break;
			}
		}

		double min = previousInBlock != null ? previousInBlock.getResolPercentage() : 0.0;
		double max = nextInBlock != null ? nextInBlock.getResolPercentage() : 100.0;

		boolean valid;
		if (min == 0.0)
			valid = currentResol >= min && currentResol <= max;
		else
			valid = currentResol > min && currentResol <= max;

		if (!valid)
			if (previousInBlock != null && currentResol <= previousInBlock.getResolPercentage())
				super.state(false, "resolPercentage", "assistance-agent.tracking-log.form.error.resolPercentageLowerThanPrevious");
			else if (nextInBlock != null && currentResol > nextInBlock.getResolPercentage())
				super.state(false, "resolPercentage", "assistance-agent.tracking-log.form.error.resolPercentageHigherThanNext");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		Date currentMoment;

		currentMoment = MomentHelper.getCurrentMoment();

		trackingLog.setLastUpdateMoment(currentMoment);
		trackingLog.setDraftMode(false);
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
