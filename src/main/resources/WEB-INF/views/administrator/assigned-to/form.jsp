<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>		
	<acme:input-textbox code="administrator.assigned-to.form.label.passport-number" path="passenger.passportNumber"/>
	<acme:input-textbox code="administrator.assigned-to.form.label.full-name" path="passenger.fullName"/>
	<acme:input-textbox code="administrator.assigned-to.form.label.email" path="passenger.email"/>	
	<acme:input-moment code="administrator.assigned-to.form.label.birth-date" path="passenger.birthDate"/>
	<acme:input-textbox code="administrator.assigned-to.form.label.special-needs" path="passenger.specialNeeds"/>
</acme:form>
