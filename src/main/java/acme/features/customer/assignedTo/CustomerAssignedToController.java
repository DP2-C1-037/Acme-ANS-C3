
package acme.features.customer.assignedTo;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.mappings.AssignedTo;
import acme.realms.customer.Customer;

@GuiController
public class CustomerAssignedToController extends AbstractGuiController<Customer, AssignedTo> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerAssignedToListService	listService;

	@Autowired
	private CustomerAssignedToShowService	showService;

	@Autowired
	private CustomerAssignedToCreateService	createService;

	@Autowired
	private CustomerAssignedToDeleteService	deleteService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);

	}

}
