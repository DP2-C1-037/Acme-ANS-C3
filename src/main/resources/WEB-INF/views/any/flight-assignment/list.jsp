<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
    <acme:list-column code="any.flight-assignment.list.label.flight-number" path="flightNumber"/>
    <acme:list-column code="any.flight-assignment.list.label.status" path="status"/>
    <acme:list-column code="any.flight-assignment.list.label.last-update-moment" path="lastUpdateMoment"/>
   	<acme:list-payload path="payload"/>	
</acme:list>
