<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-select code="technician.involved-in.form.label.task" path="task" choices="${tasks}" readonly="${_command != 'create'}"/>
	
	<jstl:choose>	
		<jstl:when test="${acme:anyOf(_command, 'show|delete')}">
			<acme:input-select code="technician.involved-in.form.label.technician" path="technician" choices= "${technicians}" readonly="true"/>	
			<acme:input-select code="technician.involved-in.form.label.type" path="task.type" choices="${types}" readonly="true"/>
			<acme:input-textarea code="technician.involved-in.form.label.description" path="task.description" readonly="true"/>
			<acme:input-integer code="technician.involved-in.form.label.priority" path="task.priority" readonly="true"/>
			<acme:input-double code="technician.involved-in.form.label.estimated-duration" path="task.estimatedDuration" readonly="true"/>
		</jstl:when>		
	</jstl:choose>	
	
	<jstl:choose>	
		<jstl:when test="${acme:anyOf(_command, 'show|delete') && draftMode == true}">
			<acme:submit code="technician.involved-in.form.button.delete" action="/technician/involved-in/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="technician.involved-in.form.button.add" action="/technician/involved-in/create?masterId=${masterId}"/>
		</jstl:when>		
	</jstl:choose>	
</acme:form>