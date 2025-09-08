<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.booking.list.label.locator-code" path="locatorCode" width="40%"/>
	<acme:list-column code="administrator.booking.list.label.purchase-moment" path="purchaseMoment" width="40%"/>
	<acme:list-column code="administrator.booking.list.label.customer" path="customer" width="20%"/>	
	<acme:list-payload path="payload"/>
</acme:list>

