<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.task.list.label.description" path="description" width="50%"/>
	<acme:list-column code="technician.task.list.label.type" path="type" width="35%"/>
	<acme:list-column code="technician.task.list.label.priority" path="priority" width="15%"/>
	
	<acme:list-payload path="payload"/>	
</acme:list>


<acme:button code="technician.task.list.button.create" action="/technician/task/create"/>
