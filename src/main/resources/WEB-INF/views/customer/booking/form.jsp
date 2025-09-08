<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>	
	
		<acme:input-textbox code="customer.booking.form.label.locator-code" path="locatorCode"/>
		<acme:input-select code="customer.booking.form.label.travel-class" path="travelClass" choices="${travelClasses}"/>
		<acme:input-textbox code="customer.booking.form.label.last-nibble" path="lastNibble"/>
		<acme:input-select code="customer.booking.form.label.flight" path="flight" choices="${flights}"/>
	
		<jstl:choose>
			<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && draftMode == true}">
				<acme:input-textbox code="customer.booking.form.label.purchase-moment" path="purchaseMoment" readonly="true"/>
				<acme:input-money code="customer.booking.form.label.price" path="price" readonly="true"/>
				<acme:button code="customer.booking.form.button.passengers" action="/customer/assigned-to/list?masterId=${id}"/>			
				<acme:submit code="customer.booking.form.button.update" action="/customer/booking/update"/>
				<acme:submit code="customer.booking.form.button.delete" action="/customer/booking/delete"/>
				<acme:submit code="customer.booking.form.button.publish" action="/customer/booking/publish"/>
			</jstl:when>
			<jstl:when test="${acme:anyOf(_command, 'show|update|publish|delete')}">
				<acme:input-textbox code="customer.booking.form.label.purchase-moment" path="purchaseMoment" readonly="true"/>
				<acme:input-money code="customer.booking.form.label.price" path="price" readonly="true"/>
				<acme:button code="customer.booking.form.button.passengers" action="/customer/assigned-to/list?masterId=${id}"/>			
			</jstl:when>
			<jstl:when test="${_command == 'create'}">
				<acme:submit code="customer.booking.form.button.create" action="/customer/booking/create"/>
			</jstl:when>		
		</jstl:choose>		
	

</acme:form>
