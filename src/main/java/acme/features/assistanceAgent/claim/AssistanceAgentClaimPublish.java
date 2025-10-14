
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
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
public class AssistanceAgentClaimPublish extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal State --------------------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractGuiService ----------------------------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		Claim claim;
		int id;
		AssistanceAgent assistanceAgent;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);
		assistanceAgent = claim == null ? null : claim.getAssistanceAgent();

		String method = super.getRequest().getMethod();

		if (method.equals("GET"))
			status = super.getRequest().getPrincipal().hasRealm(assistanceAgent) && claim != null && claim.isDraftMode();
		else {
			int legId = super.getRequest().getData("leg", int.class);

			List<Leg> legs = this.repository.findAllLegs().stream().filter(l -> (MomentHelper.isBefore(l.getScheduledArrival(), MomentHelper.getCurrentMoment()) && !l.isDraftMode() && l.getAircraft().getAirline().equals(assistanceAgent.getAirline())))
				.toList();

			Leg leg = this.repository.findLegById(legId);

			status = (legId == 0 || leg != null && !leg.isDraftMode() && legs.contains(leg)) && super.getRequest().getPrincipal().hasRealm(assistanceAgent) && claim != null && claim.isDraftMode();
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

		claim.setDraftMode(false);

		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		SelectChoices types;
		SelectChoices status;
		SelectChoices legsChoices;

		Collection<Leg> legs;
		legs = this.repository.findPublishedLegs(); // Assuming 'findAll' retrieves all published Legs

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
