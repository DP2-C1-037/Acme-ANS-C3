
package acme.features.assistanceAgent.trackingLog;

import java.util.Date;

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
		if (currentResol == null)
			return;

		int claimId = trackingLog.getClaim().getId();
		int trackingLogId = trackingLog.getId();
		Date creationMoment = trackingLog.getCreationMoment();

		// Obtenemos anterior y siguiente
		TrackingLog previous = this.repository.findPreviousTrackingLog(claimId, creationMoment, trackingLogId).stream().findFirst().orElse(null);

		TrackingLog next = this.repository.findNextTrackingLog(claimId, creationMoment, trackingLogId).stream().findFirst().orElse(null);

		boolean valid = true;

		if (previous != null && next != null)
			valid = currentResol >= previous.getResolPercentage() && currentResol <= next.getResolPercentage();
		else if (previous != null)
			valid = currentResol >= previous.getResolPercentage();
		else if (next != null)
			valid = currentResol <= next.getResolPercentage();
		else
			valid = currentResol >= 0.0;

		// Caso especial: si ya hubo uno al 100% y este es posterior → puede reiniciar desde 0
		var highest = this.repository.findFirstByClaimIdOrderByResolPercentageDesc(claimId);
		if (highest != null && Double.compare(highest.getResolPercentage(), 100.0) == 0) {
			boolean isAfterHighest = trackingLog.getCreationMoment().after(highest.getCreationMoment());
			if (isAfterHighest)
				valid = true; // se permite reiniciar
		}

		super.state(valid, "resolPercentage", "acme.validation.trackingLog.resolPercentageOrder.message");
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
