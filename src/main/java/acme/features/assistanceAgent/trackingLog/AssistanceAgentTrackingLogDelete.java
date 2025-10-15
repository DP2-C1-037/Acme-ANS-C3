
package acme.features.assistanceAgent.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Principal;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.trackingLog.TrackingLog;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogDelete extends AbstractGuiService<AssistanceAgent, TrackingLog>

{

	// Internal State --------------------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;

	// AbstractGuiService ----------------------------------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		int trackingLogId;
		int currentAssistanceAgentId;
		TrackingLog trackingLog;
		Principal principal;

		principal = super.getRequest().getPrincipal();

		if (super.getRequest().getMethod().equals("POST")) {
			currentAssistanceAgentId = principal.getActiveRealm().getId();
			trackingLogId = super.getRequest().getData("id", int.class);
			trackingLog = this.repository.findTrackingLogById(trackingLogId);

			status = principal.hasRealmOfType(AssistanceAgent.class) && trackingLog.getClaim().getAssistanceAgent().getId() == currentAssistanceAgentId && trackingLog.isDraftMode();
		} else
			status = false;

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
		super.bindObject(trackingLog, "lastUpdateMoment", "step", "resolPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		// No validation needed for delete
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.delete(trackingLog);
	}

}
