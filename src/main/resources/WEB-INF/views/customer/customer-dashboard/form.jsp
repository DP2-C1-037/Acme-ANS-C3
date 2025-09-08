<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2>
	<acme:print code="customer.dashboard.form.title.general-indicators"/>
</h2>

<table class="table table-sm">
	<tr>
		<th scope="row">
			<acme:print code="customer.dashboard.form.label.last-five-destinations"/>
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
			<acme:print code="customer.dashboard.form.label.last-year-money-spent-in-bookings"/>
		</th>
		<td>
			<jstl:choose>
				<jstl:when test="${lastYearMoneySpentInBookings != null}">
					<acme:print value="${lastYearMoneySpentInBookings}"/>
				</jstl:when>
				<jstl:otherwise>
					<acme:print value="---"/>
				</jstl:otherwise>
			</jstl:choose>
		</td>
	</tr>
	
	<tr>
		<th scope="row">
			<acme:print code="customer.dashboard.form.label.number-of-bookings-by-travel-class"/>
		</th>
		<td>
			<jstl:choose>
				<jstl:when test="${numberOfBookingsByTravelClass != null}">
	        			<jstl:forEach var="entry" items="${numberOfBookingsByTravelClass}">
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
			<acme:print code="customer.dashboard.form.label.last-five-years-booking-costs-count"/>
		</th>
		<td>
			<jstl:choose>
				<jstl:when test="${lastFiveYearsBookingCostsCount != null}">
					<acme:print value="${lastFiveYearsBookingCostsCount}"/>
				</jstl:when>
				<jstl:otherwise>
					<acme:print value="---"/>
				</jstl:otherwise>
			</jstl:choose>
		</td>
	</tr>
	
	<tr>
		<th scope="row">
			<acme:print code="customer.dashboard.form.label.last-five-years-booking-costs-average"/>
		</th>
		<td>
			<jstl:choose>
				<jstl:when test="${lastFiveYearsBookingCostsAverage != null}">
					<acme:print value="${lastFiveYearsBookingCostsAverage}"/>
				</jstl:when>
				<jstl:otherwise>
					<acme:print value="---"/>
				</jstl:otherwise>
			</jstl:choose>
		</td>
	</tr>
	
	<tr>
		<th scope="row">
			<acme:print code="customer.dashboard.form.label.last-five-years-booking-costs-minimum"/>
		</th>
		<td>
			<jstl:choose>
				<jstl:when test="${lastFiveYearsBookingCostsMinimum != null}">
					<acme:print value="${lastFiveYearsBookingCostsMinimum}"/>
				</jstl:when>
				<jstl:otherwise>
					<acme:print value="---"/>
				</jstl:otherwise>
			</jstl:choose>
		</td>
	</tr>
	
	<tr>
		<th scope="row">
			<acme:print code="customer.dashboard.form.label.last-five-years-booking-costs-maximum"/>
		</th>
		<td>
			<jstl:choose>
				<jstl:when test="${lastFiveYearsBookingCostsMaximum != null}">
					<acme:print value="${lastFiveYearsBookingCostsMaximum}"/>
				</jstl:when>
				<jstl:otherwise>
					<acme:print value="---"/>
				</jstl:otherwise>
			</jstl:choose>
		</td>
	</tr>
	
	<tr>
		<th scope="row">
			<acme:print code="customer.dashboard.form.label.last-five-years-booking-costs-standard-deviation"/>
		</th>
		<td>
			<jstl:choose>
				<jstl:when test="${lastFiveYearsBookingCostsStandardDeviation != null}">
					<acme:print value="${lastFiveYearsBookingCostsStandardDeviation}"/>
				</jstl:when>
				<jstl:otherwise>
					<acme:print value="---"/>
				</jstl:otherwise>
			</jstl:choose>
		</td>
	</tr>
	
	<tr>
		<th scope="row">
			<acme:print code="customer.dashboard.form.label.bookings-number-of-passengers-count"/>
		</th>
		<td>
			<jstl:choose>
				<jstl:when test="${bookingsNumberOfPassengersCount != null}">
					<acme:print value="${bookingsNumberOfPassengersCount}"/>
				</jstl:when>
				<jstl:otherwise>
					<acme:print value="---"/>
				</jstl:otherwise>
			</jstl:choose>
		</td>
	</tr>
	
	<tr>
		<th scope="row">
			<acme:print code="customer.dashboard.form.label.bookings-number-of-passengers-average"/>
		</th>
		<td>
			<jstl:choose>
				<jstl:when test="${bookingsNumberOfPassengersAverage != null}">
					<acme:print value="${bookingsNumberOfPassengersAverage}"/>
				</jstl:when>
				<jstl:otherwise>
					<acme:print value="---"/>
				</jstl:otherwise>
			</jstl:choose>
		</td>
	</tr>
	
	<tr>
		<th scope="row">
			<acme:print code="customer.dashboard.form.label.bookings-number-of-passengers-minimum"/>
		</th>
		<td>
			<jstl:choose>
				<jstl:when test="${bookingsNumberOfPassengersMinimum != null}">
					<acme:print value="${bookingsNumberOfPassengersMinimum}"/>
				</jstl:when>
				<jstl:otherwise>
					<acme:print value="---"/>
				</jstl:otherwise>
			</jstl:choose>
		</td>
	</tr>
	
	<tr>
		<th scope="row">
			<acme:print code="customer.dashboard.form.label.bookings-number-of-passengers-maximum"/>
		</th>
		<td>
			<jstl:choose>
				<jstl:when test="${bookingsNumberOfPassengersMaximum != null}">
					<acme:print value="${bookingsNumberOfPassengersMaximum}"/>
				</jstl:when>
				<jstl:otherwise>
					<acme:print value="---"/>
				</jstl:otherwise>
			</jstl:choose>
		</td>
	</tr>
	
	<tr>
		<th scope="row">
			<acme:print code="customer.dashboard.form.label.bookings-number-of-passengers-standard-deviation"/>
		</th>
		<td>
			<jstl:choose>
				<jstl:when test="${bookingsNumberOfPassengersStandardDeviation != null}">
					<acme:print value="${bookingsNumberOfPassengersStandardDeviation}"/>
				</jstl:when>
				<jstl:otherwise>
					<acme:print value="---"/>
				</jstl:otherwise>
			</jstl:choose>
		</td>
	</tr>
	
</table>