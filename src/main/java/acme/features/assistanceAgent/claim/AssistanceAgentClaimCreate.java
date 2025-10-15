
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.Date;
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
public class AssistanceAgentClaimCreate extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal State --------------------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractGuiService ----------------------------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		String method;
		Leg leg;
		int legId;
		AssistanceAgent assistanceAgent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();

		method = super.getRequest().getMethod();

		if (method.equals("GET"))
			status = true;
		else {
			legId = super.getRequest().getData("leg", int.class);
			leg = this.repository.findValidLegById(assistanceAgent.getAirline(), legId);

			status = legId == 0 || leg != null && !leg.isDraftMode();

		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		AssistanceAgent assistanceAgent;
		int assistanceAgentId;
		Date registrationMoment;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assistanceAgent = this.repository.findAssistanceAgentById(assistanceAgentId);
		registrationMoment = MomentHelper.getCurrentMoment();

		claim = new Claim();
		claim.setAssistanceAgent(assistanceAgent);
		claim.setDraftMode(true);
		claim.setRegistrationMoment(registrationMoment);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		int legId;

		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findPublishedLegById(legId);
		claim.setLeg(leg);
		super.bindObject(claim, "passengerEmail", "description", "type", "status");
	}

	@Override
	public void validate(final Claim claim) {

		if (claim.getLeg() == null)
			super.state(claim.getLeg() != null, "leg", "assistanceAgent.claim.form.error.emptyLeg");

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
		AssistanceAgent assistanceAgent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();

		Collection<Leg> allLegs = this.repository.findPublishedLegs();

		List<Leg> legs = allLegs.stream().filter(l -> MomentHelper.isBefore(l.getScheduledArrival(), MomentHelper.getCurrentMoment()) && l.getAircraft().getAirline().equals(assistanceAgent.getAirline())).toList();

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
