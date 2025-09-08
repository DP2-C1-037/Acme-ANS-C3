<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>	
	
		<acme:input-textbox code="administrator.booking.form.label.customer" path="customer"/>
		<acme:input-textbox code="administrator.booking.form.label.locator-code" path="locatorCode"/>
		<acme:input-textbox code="administrator.booking.form.label.travel-class" path="travelClass"/>
		<acme:input-textbox code="administrator.booking.form.label.last-nibble" path="lastNibble"/>
		<acme:input-textbox code="administrator.booking.form.label.flight" path="flight"/>
		<acme:input-textbox code="administrator.booking.form.label.purchase-moment" path="purchaseMoment"/>
		<acme:input-money code="administrator.booking.form.label.price" path="price"/>
		
		<acme:button code="administrator.booking.form.button.passengers" action="/administrator/assigned-to/list?masterId=${id}"/>			
	
</acme:form>
