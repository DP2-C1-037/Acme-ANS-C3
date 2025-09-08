
package acme.features.assistanceAgent.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.claim.Claim;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiController
public class AssistanceAgentClaimController extends AbstractGuiController<AssistanceAgent, Claim> {

	// Internal State --------------------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimListCompleted	listCompletedService;

	@Autowired
	private AssistanceAgentClaimListUndergoing	listUndergoingService;

	@Autowired
	private AssistanceAgentClaimShow			showService;
	@Autowired
	private AssistanceAgentClaimCreate			createService;

	@Autowired
	private AssistanceAgentClaimDelete			deleteService;

	@Autowired
	private AssistanceAgentClaimUpdate			updateService;

	@Autowired
	private AssistanceAgentClaimPublish			publishService;

	// Constructors  ----------------------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);

		super.addCustomCommand("list-completed", "list", this.listCompletedService);
		super.addCustomCommand("list-undergoing", "list", this.listUndergoingService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
