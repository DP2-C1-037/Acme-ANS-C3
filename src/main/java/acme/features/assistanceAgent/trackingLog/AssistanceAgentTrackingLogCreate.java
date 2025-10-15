
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

			// Todos los logs del claim en orden de creación
			List<TrackingLog> logs = this.repository.findAllByClaimIdOrderByCreationMomentAsc(claim.getId());

			if (!logs.isEmpty()) {
				// Buscar el último log publicado con 100%
				int lastPublished100Index = -1;
				for (int i = logs.size() - 1; i >= 0; i--) {
					TrackingLog t = logs.get(i);
					if (!t.isDraftMode() && t.getResolPercentage() == 100) {
						lastPublished100Index = i;
						break;
					}
				}

				// Si hay un 100% publicado, validar solo si ya existe al menos un log después de ese 100%
				if (lastPublished100Index >= 0) {
					List<TrackingLog> logsAfter100 = logs.subList(lastPublished100Index + 1, logs.size());
					if (!logsAfter100.isEmpty()) {
						TrackingLog lastLog = logsAfter100.get(logsAfter100.size() - 1);
						if (trackingLog.getResolPercentage() <= lastLog.getResolPercentage())
							super.getBuffer().getErrors().add("resolPercentage", "El porcentaje debe ser mayor que el último log registrado (" + lastLog.getResolPercentage() + "%)");
					}
					// Si no hay logs después del 100%, no hay restricción (puede empezar desde cualquier valor)
				} else {
					// Caso normal: antes de cualquier 100%, debe crecer siempre
					TrackingLog lastLog = logs.get(logs.size() - 1);
					if (trackingLog.getResolPercentage() <= lastLog.getResolPercentage())
						super.getBuffer().getErrors().add("resolPercentage", "El porcentaje debe ser mayor que el último log registrado (" + lastLog.getResolPercentage() + "%)");
				}
			}
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
