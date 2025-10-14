
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Principal;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.datatypes.ClaimType;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimStatus;
import acme.entities.leg.Leg;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimShow extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal State --------------------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractGuiService ----------------------------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int currentAssistanceAgentId;
		int claimId;
		Claim selectedClaim;
		Principal principal;

		principal = super.getRequest().getPrincipal();

		currentAssistanceAgentId = principal.getActiveRealm().getId();
		claimId = super.getRequest().getData("id", int.class);
		selectedClaim = this.repository.findClaimById(claimId);

		status = selectedClaim != null && principal.hasRealmOfType(AssistanceAgent.class) && selectedClaim.getAssistanceAgent().getId() == currentAssistanceAgentId;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int claimId;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(claimId);

		super.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		SelectChoices typesChoices;
		SelectChoices statusChoices;
		SelectChoices legsChoices;
		AssistanceAgent assistanceAgent;
		Collection<Leg> allLegs;
		List<Leg> legs;

		assistanceAgent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();

		typesChoices = SelectChoices.from(ClaimType.class, claim.getType());
		statusChoices = SelectChoices.from(ClaimStatus.class, claim.getStatus());

		allLegs = this.repository.findPublishedLegs();
		legs = allLegs.stream().filter(l -> MomentHelper.isBefore(l.getScheduledArrival(), MomentHelper.getCurrentMoment()) && l.getAircraft().getAirline().equals(assistanceAgent.getAirline())).toList();

		legsChoices = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "status", "draftMode");

		dataset.put("types", typesChoices);
		dataset.put("status", statusChoices);
		dataset.put("legs", legsChoices);

		if (legsChoices.getSelected() != null)
			dataset.put("leg", legsChoices.getSelected().getKey());
		else
			dataset.put("leg", "");

		super.getResponse().addData(dataset);
	}
}
