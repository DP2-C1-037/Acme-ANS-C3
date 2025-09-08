<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.involved-in.list.label.description" path="task.description" width="50%"/>
	<acme:list-column code="technician.involved-in.list.label.type" path="task.type" width="35%"/>
	<acme:list-column code="technician.involved-in.list.label.priority" path="task.priority" width="15%"/>

	<acme:list-payload path="payload"/>	
</acme:list>

<jstl:choose>	
	<jstl:when test="${draftMode == true}">
		<acme:button code="technician.involved-in.list.button.add" action="/technician/involved-in/create?masterId=${masterId}"/>
	</jstl:when> 		
</jstl:choose>	
