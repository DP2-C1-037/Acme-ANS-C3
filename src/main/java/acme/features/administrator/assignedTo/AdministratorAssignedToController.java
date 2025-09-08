
package acme.features.administrator.assignedTo;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.mappings.AssignedTo;

@GuiController
public class AdministratorAssignedToController extends AbstractGuiController<Administrator, AssignedTo> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAssignedToListService	listService;

	@Autowired
	private AdministratorAssignedToShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);

	}

}
