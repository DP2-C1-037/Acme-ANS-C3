<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="airline-manager.flight.list.label.tag" path="tag" width="33%"/>	
	<acme:list-column code="airline-manager.flight.list.label.requiresSelfTransfer" path="requiresSelfTransfer" width="34%"/>
	<acme:list-column code="airline-manager.flight.list.label.cost" path="cost" width="33%"/>
	
	<acme:list-payload path="payload"/>	
</acme:list>

<jstl:if test="${_command == 'list'}">
	<acme:button code="airline-manager.flight.list.button.create" action="/airline-manager/flight/create"/>
</jstl:if>	
