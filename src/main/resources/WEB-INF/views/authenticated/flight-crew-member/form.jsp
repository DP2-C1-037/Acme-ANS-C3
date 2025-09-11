
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="authenticated.flight-crew-member.form.label.employee-code" path="employeeCode"/>
	<acme:input-textbox code="authenticated.flight-crew-member.form.label.phone-number" path="phoneNumber"/>
	<acme:input-textbox code="authenticated.flight-crew-member.form.label.language-skills" path="languageSkills"/>
	<acme:input-select code="authenticated.flight-crew-member.form.label.availability-status" path="availabilityStatus" choices="${availabilities}"/>
	<acme:input-money code="authenticated.flight-crew-member.form.label.salary" path="salary"/>
	<acme:input-integer code="authenticated.flight-crew-member.form.label.years-of-experience" path="yearsOfExperience"/>
	<acme:input-select code="authenticated.flight-crew-member.form.label.airline" path="airline" choices="${airlines}"/>
	
	<jstl:if test="${_command == 'create'}">
		<acme:submit code="authenticated.flight-crew-member.form.button.create" action="/authenticated/flight-crew-member/create"/>
	</jstl:if>
</acme:form>
