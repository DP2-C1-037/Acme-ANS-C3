
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
public class AssistanceAgentTrackingLogCreate extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal State --------------------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;

	// AbstractGuiService ----------------------------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int claimId;
		int currentAssistanceAgentId;
		Claim claim;
		Principal principal;

		principal = super.getRequest().getPrincipal();

		currentAssistanceAgentId = principal.getActiveRealm().getId();
		claimId = super.getRequest().getData("claimId", int.class);
		claim = this.repository.findClaimById(claimId);

		status = principal.hasRealmOfType(AssistanceAgent.class) && claim.getAssistanceAgent().getId() == currentAssistanceAgentId;

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int claimId;
		TrackingLog trackingLog;
		Claim claim;
		Date currentMoment;

		claimId = super.getRequest().getData("claimId", int.class);
		claim = this.repository.findClaimById(claimId);

		currentMoment = MomentHelper.getCurrentMoment();

		trackingLog = new TrackingLog();
		trackingLog.setClaim(claim);
		trackingLog.setCreationMoment(currentMoment);
		trackingLog.setLastUpdateMoment(currentMoment);
		trackingLog.setDraftMode(true);

		super.getBuffer().addData(trackingLog);

	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolPercentage", "status", "resolution");
	}
	@Override
	public void validate(final TrackingLog trackingLog) {
		if (!super.getBuffer().getErrors().hasErrors("resolPercentage")) {
			Claim claim = trackingLog.getClaim();

			// Sacamos todos los logs del claim en orden de creación
			List<TrackingLog> logs = this.repository.findAllByClaimIdOrderByCreationMomentAsc(claim.getId());

			if (!logs.isEmpty()) {

				// Encontramos el índice del último log publicado al 100%
				int lastPublished100Index = -1;
				for (int i = logs.size() - 1; i >= 0; i--) {
					TrackingLog t = logs.get(i);
					if (!t.isDraftMode() && t.getResolPercentage() == 100) {
						lastPublished100Index = i;
						break;
					}
				}

				// Determinamos la lista de logs sobre los que validar
				List<TrackingLog> logsToCheck;
				if (lastPublished100Index >= 0)
					// Caso: hay un 100% publicado
					logsToCheck = logs.subList(lastPublished100Index + 1, logs.size());
				else
					// Caso normal: antes de cualquier 100%
					logsToCheck = logs;

				if (!logsToCheck.isEmpty()) {
					// Tomamos el último log de esta lista
					TrackingLog lastLog = logsToCheck.get(logsToCheck.size() - 1);
					// El nuevo porcentaje debe ser mayor que el último de la lista
					if (trackingLog.getResolPercentage() <= lastLog.getResolPercentage())
						super.getBuffer().getErrors().add("resolPercentage", "El porcentaje debe ser mayor que el último log registrado (" + lastLog.getResolPercentage() + "%)");
				}
				// Si no hay logs a validar (primer log post-100% o primer log de todos), no hay restricción
			}
			// Si no hay logs previos, tampoco hay restricción
		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		SelectChoices status;

		dataset = super.unbindObject(trackingLog, "creationMoment", "lastUpdateMoment", "step", "resolPercentage", "status", "resolution");
		status = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("status", status);
		dataset.put("claimId", trackingLog.getClaim().getId());

		super.getResponse().addData(dataset);
	}

}
