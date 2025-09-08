
package acme.features.assistanceAgent.trackingLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.trackingLog.TrackingLog;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiController
public class AssistanceAgentTrackingLogController extends AbstractGuiController<AssistanceAgent, TrackingLog> {

	// Internal State --------------------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogList		listService;

	@Autowired
	private AssistanceAgentTrackingLogShow		showService;

	@Autowired
	private AssistanceAgentTrackingLogCreate	createService;

	@Autowired
	private AssistanceAgentTrackingLogDelete	deleteService;

	@Autowired
	private AssistanceAgentTrackingLogUpdate	updateService;

	@Autowired
	private AssistanceAgentTrackingLogPublish	publishService;

	// Constructors  ----------------------------------------------------------------------


	@PostConstruct
	protected void initialise() {

		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);

		super.addCustomCommand("publish", "update", this.publishService);

	}
}
