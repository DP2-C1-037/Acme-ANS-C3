
package acme.features.any;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightAssignment.FlightAssignment;

@GuiController
public class AnyFlightAssignmentController extends AbstractGuiController<Any, FlightAssignment> {

	@Autowired
	private AnyFlightAssignmentListService	listService;

	@Autowired
	private AnyFlightAssignmentShowService	showService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}
}
