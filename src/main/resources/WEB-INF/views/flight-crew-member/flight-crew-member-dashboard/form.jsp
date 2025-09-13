<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2>
    <acme:print code="flight-crew-member.dashboard.form.title.general-indicators"/>
</h2>

<table class="table table-sm">
    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.last-five-destinations"/>
        </th>
        <td>
            <jstl:choose>
                <jstl:when test="${lastFiveDestinations != null}">
                    <acme:print value="${lastFiveDestinations}"/>
                </jstl:when>
                <jstl:otherwise>
                    <acme:print value="---"/>
                </jstl:otherwise>
            </jstl:choose>
        </td>
    </tr>

    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.incident-counts-by-severity"/>
        </th>
        <td>
            <jstl:choose>
                <jstl:when test="${incidentCountsBySeverity != null}">
                    <jstl:forEach var="entry" items="${incidentCountsBySeverity}">
                        <div>${entry.key}: ${entry.value}</div>
                    </jstl:forEach>
                </jstl:when>
                <jstl:otherwise>
                    <acme:print value="---"/>
                </jstl:otherwise>
            </jstl:choose>
        </td>
    </tr>

    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.last-leg-crew-members"/>
        </th>
        <td>
            <jstl:choose>
                <jstl:when test="${lastLegCrewMembers != null}">
                    <acme:print value="${lastLegCrewMembers}"/>
                </jstl:when>
                <jstl:otherwise>
                    <acme:print value="---"/>
                </jstl:otherwise>
            </jstl:choose>
        </td>
    </tr>

    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.flight-assignments-by-status"/>
        </th>
        <td>
            <jstl:choose>
                <jstl:when test="${flightAssignmentsByStatus != null}">
                    <jstl:forEach var="entry" items="${flightAssignmentsByStatus}">
                        <div>${entry.key}: ${entry.value}</div>
                    </jstl:forEach>
                </jstl:when>
                <jstl:otherwise>
                    <acme:print value="---"/>
                </jstl:otherwise>
            </jstl:choose>
        </td>
    </tr>

    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.average-flight-assignments"/>
        </th>
        <td><acme:print value="${averageFlightAssignments != null ? averageFlightAssignments : '---'}"/></td>
    </tr>

    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.min-flight-assignments"/>
        </th>
        <td><acme:print value="${minFlightAssignments != null ? minFlightAssignments : '---'}"/></td>
    </tr>

    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.max-flight-assignments"/>
        </th>
        <td><acme:print value="${maxFlightAssignments != null ? maxFlightAssignments : '---'}"/></td>
    </tr>

    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.stddev-flight-assignments"/>
        </th>
        <td><acme:print value="${stdDevFlightAssignments != null ? stdDevFlightAssignments : '---'}"/></td>
    </tr>
</table>
