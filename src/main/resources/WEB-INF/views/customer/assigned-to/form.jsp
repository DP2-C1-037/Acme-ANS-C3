<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>		
	<acme:input-select code="customer.assigned-to.form.label.passport-number" path="passenger" choices="${passengers}" readonly="${_command != 'create'}"/>
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|delete')}">	
			<acme:input-textbox code="customer.assigned-to.form.label.full-name" path="passenger.fullName" readonly="true"/>
			<acme:input-textbox code="customer.assigned-to.form.label.email" path="passenger.email" readonly="true"/>	
			<acme:input-moment code="customer.assigned-to.form.label.birth-date" path="passenger.birthDate" readonly="true"/>
			<acme:input-textbox code="customer.assigned-to.form.label.special-needs" path="passenger.specialNeeds" readonly="true"/>
			<jstl:if test="${draftMode == true }">
				<acme:submit code="customer.assigned-to.form.button.delete" action="/customer/assigned-to/delete"/>
			</jstl:if>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.assigned-to.list.button.add" action="/customer/assigned-to/create?masterId=${masterId}"/>
		</jstl:when>
	</jstl:choose>

</acme:form>
