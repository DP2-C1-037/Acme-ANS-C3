<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.maintenance-record.list.label.aircraft-model" path="aircraft.model" width="30%"/>
	<acme:list-column code="technician.maintenance-record.list.label.maintenanceDate" path="maintenanceDate" width="25%"/>
	<acme:list-column code="technician.maintenance-record.list.label.nextInspectionDueDate" path="nextInspectionDueDate" width="25%"/>
	<acme:list-column code="technician.maintenance-record.list.label.status" path="status" width="20%"/>
	
	<acme:list-payload path="payload"/>	
</acme:list>


<acme:button code="technician.maintenance-record.list.button.create" action="/technician/maintenance-record/create"/>
