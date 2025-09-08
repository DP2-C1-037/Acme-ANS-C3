<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="airline-manager.leg.list.label.flightNumber" path="flightNumber" width="33%" sortable="false"/>	
	<acme:list-column code="airline-manager.leg.list.label.scheduledDeparture" path="scheduledDeparture" width="34%"/>
	<acme:list-column code="airline-manager.leg.list.label.scheduledArrival" path="scheduledArrival" width="33%"/>
	
	<acme:list-payload path="payload"/>	
</acme:list>

<jstl:if test="${_command == 'list-all-mine'}">
    <acme:button code="airline-manager.leg.list.button.create-from-all-mine" action="/airline-manager/leg/create-from-all-mine"/>
</jstl:if>
<jstl:if test="${_command == 'list' && masterId != null}">
    <acme:button code="airline-manager.leg.list.button.create" action="/airline-manager/leg/create?masterId=${masterId}"/>
</jstl:if>