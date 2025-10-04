
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Principal;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.datatypes.ClaimType;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimStatus;
import acme.entities.leg.Leg;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimUpdate extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal State --------------------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractGuiService ----------------------------------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		int currentAssistanceAgentId;
		int claimId;
		Claim selectedClaim;
		Principal principal;

		principal = super.getRequest().getPrincipal();

		if (principal.hasRealmOfType(AssistanceAgent.class)) {
			currentAssistanceAgentId = principal.getActiveRealm().getId();
			claimId = super.getRequest().getData("id", int.class);
			selectedClaim = this.repository.findClaimById(claimId);

			status = selectedClaim != null && selectedClaim.getAssistanceAgent().getId() == currentAssistanceAgentId && selectedClaim.isDraftMode();

			if (status && super.getRequest().getMethod().equals("POST"))
				try {
					int legId = super.getRequest().getData("leg", int.class);
					Leg leg = this.repository.findPublishedLegById(legId);
					if (!(legId == 0 || leg != null))
						status = false;
				} catch (Exception e) {
					status = false;
				}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int claimId;
		Claim claim;

		claimId = super.getRequest().getData("id", int.class);

		claim = this.repository.findClaimById(claimId);
		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {

		super.bindObject(claim, "passengerEmail", "description", "type", "status", "leg");
	}

	@Override
	public void validate(final Claim claim) {

		if (claim.getLeg() == null)
			super.state(claim.getLeg() != null, "leg", "assistanceAgent.claim.form.error.emptyLeg");

		if (!super.getBuffer().getErrors().hasErrors("draftMode"))
			super.state(claim.isDraftMode(), "draftMode", "assistanceAgent.claim.form.error.draftMode");

	}

	@Override
	public void perform(final Claim claim) {
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		SelectChoices types;
		SelectChoices status;
		SelectChoices legsChoices;

		Collection<Leg> legs;
		legs = this.repository.findPublishedLegs();

		types = SelectChoices.from(ClaimType.class, claim.getType());
		status = SelectChoices.from(ClaimStatus.class, claim.getStatus());
		legsChoices = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "status");
		dataset.put("types", types);
		dataset.put("status", status);
		dataset.put("legs", legsChoices);
		dataset.put("leg", legsChoices.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

}
