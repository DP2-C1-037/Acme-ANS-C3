<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-moment code="any.flight-assignment.form.label.last-update-moment" path="lastUpdateMoment" readonly="true"/>
    <acme:input-textbox code="any.flight-assignment.form.label.flight-crew-member" path="employeeCode" readonly="true"/>
    <acme:input-textbox code="any.flight-assignment.form.label.flight-crew-duty" path="flightCrewDuty" readonly="true"/>
    <acme:input-textbox code="any.flight-assignment.form.label.leg" path="flightNumber" readonly="true"/>
    <acme:input-textbox code="any.flight-assignment.form.label.status" path="status" readonly="true"/>
    <acme:input-textarea code="any.flight-assignment.form.label.remarks" path="remarks" readonly="true"/>
</acme:form>
