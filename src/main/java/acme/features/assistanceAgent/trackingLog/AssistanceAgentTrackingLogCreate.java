
package acme.features.assistanceAgent.trackingLog;

import java.util.Date;

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
	public void validate(final TrackingLog trackingLog) {
		if (trackingLog == null || trackingLog.getClaim() == null)
			return;

		Double currentResol = trackingLog.getResolPercentage();
		if (currentResol == null)
			return;

		int claimId = trackingLog.getClaim().getId();

		//  Recuperamos todos los logs ordenados por porcentaje descendente (primero el mayor)
		var logs = this.repository.findAllTrackingLogsByClaimIdOrderedByResolPercentageDesc(claimId);

		if (!logs.isEmpty()) {
			TrackingLog highest = logs.iterator().next(); // el primero es el mayor porcentaje
			boolean valid;

			if (Double.compare(highest.getResolPercentage(), 100.0) == 0 && trackingLog.getCreationMoment() != null && trackingLog.getCreationMoment().after(highest.getCreationMoment()))
				//  Si ya hubo un 100% y este tracking es posterior → se permite reiniciar desde 0
				valid = currentResol >= 0.0;
			else
				//  En cualquier otro caso, debe ser >= al mayor previo
				valid = currentResol >= highest.getResolPercentage();

			super.state(valid, "resolPercentage", "acme.validation.trackingLog.resolPercentageOrder.message");
		}
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
