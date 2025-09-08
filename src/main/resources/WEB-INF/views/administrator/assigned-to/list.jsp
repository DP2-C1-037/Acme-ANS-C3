<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.assigned-to.list.label.full-name" path="passenger.fullName" width="40%"/>
	<acme:list-column code="administrator.assigned-to.list.label.email" path="passenger.email" width="40%"/>
	<acme:list-column code="administrator.assigned-to.list.label.passport-number" path="passenger.passportNumber" width="20%"/>
	<acme:list-payload path="payload"/>
</acme:list>
